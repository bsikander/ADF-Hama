package main.com.techroz.bsp.BSPExchange;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hama.bsp.BSPPeer;
import org.apache.hama.bsp.sync.SyncException;

import main.com.techroz.utils.BSPHelper;
import main.com.techroz.utils.Constants;
import main.com.techroz.admm.ExchangeSolver.EVADMM.ExchangeMasterContext;
import main.com.techroz.admm.ExchangeSolver.EVADMM.ExchangeSlaveContext;
import main.com.techroz.admm.ExchangeSolver.EVADMM.ShareMasterData;
import main.com.techroz.admm.ExchangeSolver.EVADMM.XUpdate;
import main.com.techroz.bsp.IBSP;

public class BSPExchange extends IBSP<LongWritable, Text, IntWritable, Text, Text> {
	public static final Log LOG = LogFactory.getLog(BSPExchange.class);

	protected static BSPPeer<LongWritable, Text, IntWritable, Text, Text> bspPeer;
	ExchangeMasterContext masterContext;
	ExchangeSlaveContext slaveContext;
	
	protected static int ADF_ADMM_ITERATIONS_MAX;
	protected static double RHO;
	protected static int XOPTIMAL_SIZE;
	
	@Override
	public void bsp(BSPPeer<LongWritable, Text, IntWritable, Text, Text> peer)
			throws IOException, SyncException, InterruptedException {
		
		int k = 0; //iteration counter
		
		if(peer.getPeerName().equals(this.masterTask)) { //The master task 
			LOG.info("MASTER: Starting Iteration Loop");
			
			while(k != ADF_ADMM_ITERATIONS_MAX && masterContext.converged() != true)
			{	
				//TODO: MasterContext object initialization missing
				//Send U and XMean to all slaves
				BSPHelper.sendShareMasterObjectToSlaves(masterContext.getMasterData());
				peer.sync();  
				
				//TODO: Fix this
				masterContext.optimizeMasterFunction(""); //Optimize Master Equation
				
				peer.sync();
				
				double[] average = BSPHelper.getAverageOfReceivedOptimalSlaveValues(); //Get average of all the data received
				
				masterContext.calculateXMean(average, 10); //TODO: Replace this dummy 10 from here
				masterContext.calculateU();
				
				k++;
			}
			//TODO: Send Finished message
		}
		else {
			while(true)
			{
				peer.sync();
				
				ShareMasterData masterData = BSPHelper.receiveShareMasterDataObject(); //Receive xMean and u from master
				slaveContext.setMasterData(masterData); //Set these in slave context
				
				LongWritable key = new LongWritable();
				Text value = new Text();
				
				//Read each input
				while(peer.readNext(key, value)) {
					//TODO: Parse data according to the header
					slaveContext.optimizeSlaveFunction(value.toString()); //Perform optimization
					
					BSPHelper.sendShareSlaveObjectToMaster(slaveContext.getSlaveData()); //Send x* to master
				}
				
				peer.sync(); //Send all the data
				//TODO: Check for finished message
			}
		}
			
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setup(BSPPeer<LongWritable, Text, IntWritable, Text, Text> peer) throws IOException,
	      SyncException, InterruptedException {
		LOG.info(peer.getPeerName() + " is starting up");
		
		this.masterTask = peer.getPeerName(0); //0 is out master
		this.bspPeer = peer;
		
		Class<? extends XUpdate> masterFunction = (Class<? extends XUpdate>) peer.getConfiguration().getClass(Constants.ADF_MASTER_FUNCTION, XUpdate.class);
		Class<? extends XUpdate> slaveFunction = (Class<? extends XUpdate>) peer.getConfiguration().getClass(Constants.ADF_SLAVE_FUNCTION, XUpdate.class);
		
		try {
			if(peer.getPeerName().equals(this.masterTask)) { //If master then initialize master context otherwise slave
				masterContext = new ExchangeMasterContext(XOPTIMAL_SIZE, masterFunction.newInstance());
				masterContext.optimizeMasterFunction("");
			}
			else {
				slaveContext = new ExchangeSlaveContext(XOPTIMAL_SIZE, slaveFunction.newInstance());
				slaveContext.optimizeSlaveFunction("");
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
		ADF_ADMM_ITERATIONS_MAX = Integer.parseInt(peer.getConfiguration().get(Constants.ADF_MAX_ITERATIONS));
		XOPTIMAL_SIZE = Integer.parseInt(peer.getConfiguration().get(Constants.ADF_XOPTIMAL_SIZE));
		//RHO = Double.parseDouble(peer.getConfiguration().get(Constants.ADF_RHO));
		//EV_COUNT = Integer.parseInt(peer.getConfiguration().get(Constants.EVADMM_EV_COUNT));
		
		//peer.getCounter(ExchangeCounters); //Read all the input and count them and put them in TotalAgents and then reopen the input
	}
	
	enum ExchangeCounters {
		TotalAgents
	}
	
}
