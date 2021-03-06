package main.com.techroz.adf.bsp;

import java.io.IOException;
import java.util.Map;

import main.com.techroz.adf.admm.ContextBase;
import main.com.techroz.adf.admm.XUpdate;
import main.com.techroz.adf.utils.Constants;
import main.com.techroz.adf.utils.Utilities;
import main.com.techroz.algorithm.exchange.BSPExchange;
import main.com.techroz.algorithm.exchange.ExchangeMasterContext;
import main.com.techroz.algorithm.exchange.ExchangeSlaveContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hama.bsp.BSP;
import org.apache.hama.bsp.BSPPeer;
import org.apache.hama.bsp.sync.SyncException;

public abstract class BSPBase<K1, V1, K2, V2, M extends Writable> extends BSP<K1, V1, K2, V2, M> {
	public static final Log LOG = LogFactory.getLog(BSPBase.class);
	
	protected static String masterTask;
	protected static int ADF_ADMM_ITERATIONS_MAX;
	protected static int XOPTIMAL_SIZE; //size of solution vector
	
	@Override
	public abstract void bsp(BSPPeer<K1, V1, K2, V2, M> peer) throws IOException,
	      SyncException, InterruptedException;
	  
	@Override
	public void setup(BSPPeer<K1, V1, K2, V2, M> peer) throws IOException,
	      SyncException, InterruptedException {
		LOG.info(peer.getPeerName() + " is starting up");
		
		BSPBase.masterTask = peer.getPeerName(0); //0 is our master
		
		ADF_ADMM_ITERATIONS_MAX = Integer.parseInt(peer.getConfiguration().get(Constants.ADF_MAX_ITERATIONS));
		XOPTIMAL_SIZE = Integer.parseInt(peer.getConfiguration().get(Constants.ADF_XOPTIMAL_SIZE));
	}

	@Override
	public void cleanup(BSPPeer<K1, V1, K2, V2, M> peer) throws IOException {
		LOG.info(peer.getPeerName() + " is shutting down");
	}
	
	/** This method sends the ShareMasterData object to all the slaves
	 * @param object Object containing u and xMean that needs to be sent
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	protected void sendDataToSlaves(BSPPeer<K1, V1, K2, V2, M> peer, String data) throws IOException {
		for(String p : peer.getAllPeerNames()) {
			if(!p.equals(masterTask)) {
				peer.send(p, (M) new Text(data));
			}
		}
	}
	
	//receiveShareMasterDataObject
	protected String receiveDataAtSlave(BSPPeer<K1, V1, K2, V2, M> peer) throws IOException
	{
		M receivedJson;
		
		while ((receivedJson = peer.getCurrentMessage()) != null) //Receive initial array
		{
			LOG.info("Slave: Data found -> receiveShareMasterDataObject");
			break;
		}
		LOG.info("Received the data ----------");
		return receivedJson.toString();
	}
	
	//sendFinishMessage
	
	@SuppressWarnings({ "unchecked" })
	protected void sendFinishSignal(BSPPeer<K1, V1, K2, V2, M> peer) throws IOException
	{	
		LOG.info("Sending finished message");
		for(String peerName: peer.getAllPeerNames()) {
			if(!peerName.equals(BSPBase.masterTask)) {
					peer.send(peerName, (M) new Text(Utilities.getFinishedMessageObject()));
			}	
		}
	}
	
	//sendShareSlaveObjectToMaster
	@SuppressWarnings("unchecked")
	protected void sendDataToMaster(BSPPeer<K1,V1,K2,V2,M> peer, String data) throws IOException
	//protected static <K1,V1,K2,V2,M extends Writable> void sendDataToMaster(BSPPeer<K1,V1,K2,V2,M> peer, String data) throws IOException
	{	
		peer.send(masterTask, (M) new Text(data));
	}
	
	//getAverageOfReceivedOptimalSlaveValues
	protected double[] calculateAverageOfReceivedSlaveValues(BSPPeer<K1,V1,K2,V2,M> peer) throws IOException
	{	
		M receivedJson;
		double[] averageXReceived = Utilities.getZeroArray(XOPTIMAL_SIZE); 
				
		while ((receivedJson = peer.getCurrentMessage()) != null) //Receive initial array 
		{	
			averageXReceived =  Utilities.vectorAdd(averageXReceived, Utilities.getXOptimalFromJson(receivedJson.toString()));
		}
	
		return averageXReceived;
	}
	
	/*
	 * This function returns the object of class that is passed as generic parameter. It uses the configuration key
	 * to look for the class inside the configuration object of Hama. Using the generic parameter is casts the object
	 * to a class of type T and then create its objects. The object is returned back.
	 */
	@SuppressWarnings("unchecked")
	protected <T> T getClassFromConfiguration(BSPPeer<K1,V1,K2,V2,M> peer, String configurationKey, Class<? extends T> clazz)  {
		try {
			return ((T)peer.getConfiguration().getClass(configurationKey, clazz).newInstance());
		} catch (InstantiationException e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		return null;
	}
	
	protected int countInput(BSPPeer<K1,V1,K2,V2,M> peer) throws IOException {
		int count = 0;
		
		if(!peer.getPeerName().equals(BSPBase.masterTask))
			while(peer.readNext() != null) {
				count++;
			}
			peer.reopenInput();
		
		return count;
	}
	
	protected Map<String,String> getAllConfiguration(BSPPeer<K1,V1,K2,V2,M> peer) {
		return peer.getConfiguration().getValByRegex(Constants.ADF_CONFIGURATION_REGEX);
	}
	
	protected enum ADFCounters {
		TotalInput
	}
}
