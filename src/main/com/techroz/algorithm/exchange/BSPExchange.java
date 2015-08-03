package main.com.techroz.algorithm.exchange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hama.bsp.BSPPeer;
import org.apache.hama.bsp.sync.SyncException;

import main.com.techroz.adf.admm.XUpdate;
import main.com.techroz.adf.bsp.BSPBase;
import main.com.techroz.adf.utils.BSPHelper;
import main.com.techroz.adf.utils.BroadcastHelper;
import main.com.techroz.adf.utils.Constants;
import main.com.techroz.adf.utils.Utilities;
import main.com.techroz.deleteme.Result;
import main.com.techroz.deleteme.ResultMaster;

public class BSPExchange extends BSPBase<LongWritable, Text, IntWritable, Text, Text> {
	public static final Log LOG = LogFactory.getLog(BSPExchange.class);

	protected static BSPPeer<LongWritable, Text, IntWritable, Text, Text> bspPeer;
	ExchangeMasterContext masterContext;
	ExchangeSlaveContext slaveContext;
	
	protected static int ADF_ADMM_ITERATIONS_MAX;
	protected static double RHO;
	protected static int XOPTIMAL_SIZE;
	
	List<Result> resultList = new ArrayList<Result>();
	List<ResultMaster> resultMasterList = new ArrayList<ResultMaster>();
	
	@Override
	public void bsp(BSPPeer<LongWritable, Text, IntWritable, Text, Text> peer)
			throws IOException, SyncException, InterruptedException {
		
		int k = 0; //iteration counter
		
		if(peer.getPeerName().equals(BSPBase.masterTask)) { //The master task 
			LOG.info("MASTER: Starting Iteration Loop");
			
			while(k != ADF_ADMM_ITERATIONS_MAX && masterContext.converged() != true)
			{	
				LOG.info("Master: Sending U and X Mean to slaves");
				
				//Send U and XMean to all slaves
				//Get the Map and convert it to String and send to slaves
				BSPHelper.sendShareMasterObjectToSlaves(BroadcastHelper.convertDictionaryToJson(masterContext.getMasterData()),peer);
				peer.sync();  
				
				String input = peer.readNext().getValue().toString();
				LOG.info("Master: Sending aggregator data to optimize >> " + input);
				masterContext.getXUpdate(input,11); //TODO:Optimize Master Equation
				
				peer.sync();
				
				double[] average = BSPHelper.getAverageOfReceivedOptimalSlaveValues(peer); //Get average of all the data received
				LOG.info("--------- AVERAGE AT MASTER ---------" );
				Utilities.PrintArray(average);
				LOG.info("--------- AVERAGE AT MASTER ---------" );
				
				masterContext.calculateXMean(average, 11); //TODO: Replace this dummy 10 from here
				masterContext.calculateU();
				
				resultMasterList.add(new ResultMaster(peer.getPeerName(),k,0,masterContext.getU(),masterContext.getxMean(),masterContext.getXOptimal(),0,average));
				
				peer.reopenInput(); //Read the input again for next iteration
				
				k++;
			}
			
			LOG.info(peer.getPeerName() + "Master 1.8:: Sending finishing message");
			
			//TODO: Send Finished message
			LOG.info("Master: Finished");
			BSPHelper.sendFinishMessage(peer);
			peer.sync();
			peer.sync();
			
			LOG.info("\\\\\\\\MASTER OUTPUT\\\\\\\\");
			int count=0;
			String printResult = "";
			for(ResultMaster r : resultMasterList){
				 printResult = r.printResult(count);
				
				count++;
			}
			LOG.info("\\\\\\\\MASTER OUTPUT - END\\\\\\\\");
		}
		else {
			boolean finish = false;
			while(true)
			{
				LOG.info("Slave: Waiting for incoming data");
				peer.sync();
				
				LOG.info("Slave: Receving the data");

				Map<String, double[]> masterData = BroadcastHelper.convertJsonToDictionary( BSPHelper.receiveShareMasterDataObject(peer)); //Receive xMean and u from master
				

				if(masterData.get("u") == null) {
					finish = true;
					break;
				}
				
				slaveContext.setMasterData(masterData); //Set these in slave context
				
				LongWritable key = new LongWritable();
				Text value = new Text();
				
				LOG.info("Slave: Read the input data");
				//Read each input
				int i = 0;
			
				while(peer.readNext(key, value) != false) {
					//System.out.println("Slave: Optimize the slave data" + i +" >>>" + value.toString());
					slaveContext.getXUpdate(value.toString(),i);
					
					resultList.add(new Result(peer.getPeerName(),i,0, slaveContext.getXOld(i), masterData.get("xMean"),masterData.get("u"),slaveContext.getXOptimal(),0));
					
					BSPHelper.sendShareSlaveObjectToMaster(BroadcastHelper.convertDictionaryToJson(slaveContext.getSlaveData()),peer); //Send x* to master
					
					i++;
				}
				
				peer.sync(); //Send all the data
				//TODO: Check for finished message
				peer.reopenInput(); //Since we are going to the next iteration ... start the data again
			}
			peer.sync();
			if(finish == true) {
				LOG.info("Slave: Finshed");
				for(Result r : resultList){
					r.printResult();
				}
			}
		}	
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setup(BSPPeer<LongWritable, Text, IntWritable, Text, Text> peer) throws IOException,
	      SyncException, InterruptedException {
		LOG.info(peer.getPeerName() + " is starting up");
		
		BSPBase.masterTask = peer.getPeerName(0); //0 is out master
		BSPExchange.bspPeer = peer;
		
		ADF_ADMM_ITERATIONS_MAX = Integer.parseInt(peer.getConfiguration().get(Constants.ADF_MAX_ITERATIONS));
		XOPTIMAL_SIZE = Integer.parseInt(peer.getConfiguration().get(Constants.ADF_XOPTIMAL_SIZE));
		
		Class<? extends XUpdate> masterFunction = (Class<? extends XUpdate>) peer.getConfiguration().getClass(Constants.ADF_FUNCTION1, XUpdate.class);
		Class<? extends XUpdate> slaveFunction = (Class<? extends XUpdate>) peer.getConfiguration().getClass(Constants.ADF_FUNCTION2, XUpdate.class);
		
		try {
			if(peer.getPeerName().equals(BSPBase.masterTask)) { //If master then initialize master context otherwise slave
				//context = new ExchangeContext(XOPTIMAL_SIZE, masterFunction.newInstance());
				masterContext = new ExchangeMasterContext(XOPTIMAL_SIZE, masterFunction.newInstance());
			}
			else {
				slaveContext = new ExchangeSlaveContext(XOPTIMAL_SIZE, slaveFunction.newInstance());
			}
			
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	enum ExchangeCounters {
		TotalAgents
	}
	
}