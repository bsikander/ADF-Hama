package main.com.techroz.bsp.BSPExchange;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hama.bsp.BSPPeer;
import org.apache.hama.bsp.sync.SyncException;

import main.com.techroz.utils.BSPHelper;
import main.com.techroz.utils.Constants;
import main.com.techroz.admm.ExchangeSolver.EVADMM.ExchangeContext;
import main.com.techroz.admm.ExchangeSolver.EVADMM.ShareMasterData;
import main.com.techroz.admm.ExchangeSolver.EVADMM.XUpdate;
import main.com.techroz.bsp.IBSP;

public class BSPExchange extends IBSP<LongWritable, Text, IntWritable, Text, Text> {
	public static final Log LOG = LogFactory.getLog(BSPExchange.class);

	protected static BSPPeer<LongWritable, Text, IntWritable, Text, Text> bspPeer;
//	ExchangeMasterContext masterContext;
//	ExchangeSlaveContext slaveContext;
	ExchangeContext context;
	
	protected static int ADF_ADMM_ITERATIONS_MAX;
	protected static double RHO;
	protected static int XOPTIMAL_SIZE;
	
	@Override
	public void bsp(BSPPeer<LongWritable, Text, IntWritable, Text, Text> peer)
			throws IOException, SyncException, InterruptedException {
		
		int k = 0; //iteration counter
		
		if(peer.getPeerName().equals(IBSP.masterTask)) { //The master task 
			LOG.info("MASTER: Starting Iteration Loop");
			
			while(k != ADF_ADMM_ITERATIONS_MAX && context.converged() != true)
			{	
				System.out.println("Master: Sending U and X Mean to slaves");
				
				//Send U and XMean to all slaves
				BSPHelper.sendShareMasterObjectToSlaves(context.getMasterData(),peer);
				peer.sync();  
				
				String input = peer.readNext().getValue().toString();
				System.out.println("Master: Sending aggregator data to optimize >> " + input);
				context.getXUpdate(input,0); //Optimize Master Equation
				
				peer.sync();
				
				double[] average = BSPHelper.getAverageOfReceivedOptimalSlaveValues(); //Get average of all the data received
				
				context.calculateXMean(average, 10); //TODO: Replace this dummy 10 from here
				context.calculateU();
				
				peer.reopenInput(); //Read the input again for next iteration
				
				k++;
			}
			
			System.out.println(peer.getPeerName() + "Master 1.8:: Sending finishing message");
			
			//TODO: Send Finished message
			System.out.println("Master: Finished");
			BSPHelper.sendFinishMessage(peer);
			peer.sync();
			peer.sync();
		}
		else {
			boolean finish = false;
			while(true)
			{
				System.out.println("Slave: Waiting for incoming data");
				peer.sync();
				
				System.out.println("Slave: Receving the data");
				ShareMasterData masterData = BSPHelper.receiveShareMasterDataObject(peer); //Receive xMean and u from master
				
				if(masterData.getU() == null) {
					finish = true;
					break;
				}
				
				context.setMasterData(masterData); //Set these in slave context
				
				LongWritable key = new LongWritable();
				Text value = new Text();
				
				System.out.println("Slave: Read the input data");
				//Read each input
				int i = 0;
			
				while(peer.readNext(key, value) != false) {
					System.out.println("Slave: Optimize the slave data" + i +" >>>" + value.toString());
					context.getXUpdate(value.toString(),i);
					
					BSPHelper.sendShareSlaveObjectToMaster(context.getSlaveData()); //Send x* to master
					
					i++;
				}
				
				peer.sync(); //Send all the data
				//TODO: Check for finished message
				peer.reopenInput(); //Since we are going to the next iteration ... start the data again
			}
			peer.sync();
			if(finish == true) {
				System.out.println("Slave: Finshed");
			}
			
		}
			
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setup(BSPPeer<LongWritable, Text, IntWritable, Text, Text> peer) throws IOException,
	      SyncException, InterruptedException {
		LOG.info(peer.getPeerName() + " is starting up");
		
		IBSP.masterTask = peer.getPeerName(0); //0 is out master
		BSPExchange.bspPeer = peer;
		
		ADF_ADMM_ITERATIONS_MAX = Integer.parseInt(peer.getConfiguration().get(Constants.ADF_MAX_ITERATIONS));
		XOPTIMAL_SIZE = Integer.parseInt(peer.getConfiguration().get(Constants.ADF_XOPTIMAL_SIZE));
		
		Class<? extends XUpdate> masterFunction = (Class<? extends XUpdate>) peer.getConfiguration().getClass(Constants.ADF_MASTER_FUNCTION, XUpdate.class);
		Class<? extends XUpdate> slaveFunction = (Class<? extends XUpdate>) peer.getConfiguration().getClass(Constants.ADF_SLAVE_FUNCTION, XUpdate.class);
		
		try {
			if(peer.getPeerName().equals(IBSP.masterTask)) { //If master then initialize master context otherwise slave
				context = new ExchangeContext(XOPTIMAL_SIZE, masterFunction.newInstance());
			}
			else {
				context = new ExchangeContext(XOPTIMAL_SIZE, slaveFunction.newInstance());
			}
			
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//AGGREGATOR_PATH = peer.getConfiguration().get(Constants.EVADMM_AGGREGATOR_PATH);
		//EV_PATH = peer.getConfiguration().get(Constants.EVADMM_EV_PATH);
		
		//RHO = Double.parseDouble(peer.getConfiguration().get(Constants.ADF_RHO));
		//EV_COUNT = Integer.parseInt(peer.getConfiguration().get(Constants.EVADMM_EV_COUNT));
		
		//peer.getCounter(ExchangeCounters); //Read all the input and count them and put them in TotalAgents and then reopen the input
	}
	
	enum ExchangeCounters {
		TotalAgents
	}
	
}
