package main.com.techroz.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import main.com.techroz.admm.ExchangeSolver.EVADMM.ShareMasterData;
import main.com.techroz.admm.ExchangeSolver.EVADMM.ShareSlaveData;
import main.com.techroz.bsp.BSPBase;
import main.com.techroz.bsp.BSPExchange.BSPExchange;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hama.bsp.BSPPeer;

public class BSPHelper extends BSPExchange {
	
	public static final Log LOG = LogFactory.getLog(BSPHelper.class);
	
	/** This method sends the ShareMasterData object to all the slaves
	 * @param object Object containing u and xMean that needs to be sent
	 * @throws IOException
	 */
	//public static <K1,V1,K2,V2,M extends Writable> void sendShareMasterObjectToSlaves(Map<String, double[]> data,BSPPeer<K1,V1,K2,V2, M> peer) throws IOException
	//public static void sendShareMasterObjectToSlaves(ShareMasterData object,BSPPeer<LongWritable, Text, IntWritable, Text, Text> peer) throws IOException
	public static <K1,V1,K2,V2,M extends Writable> void sendShareMasterObjectToSlaves(String data,BSPPeer<K1,V1,K2,V2, M> peer) throws IOException
	{	
		for(String p : peer.getAllPeerNames()) {
			if(!p.equals(masterTask)) {
				//peer.send(p, (M) new Text(NetworkHelper.shareMasterObjectToJson(object)));
				//peer.send(p, (M) new Text(NetworkHelper.convertDictionaryToJson(data)));
				peer.send(p, (M) new Text(data));
			}
		}
	}
	
	/*
	 * 
	 */
	public static <K1,V1,K2,V2,M extends Writable> double[] getAverageOfReceivedOptimalSlaveValues(BSPPeer<K1,V1,K2,V2,M> peer) throws IOException
	{	
		//ShareSlaveData slave;
		//Map<String, double[]> slave;
		M receivedJson;
		double[] averageXReceived = Utilities.getZeroArray(XOPTIMAL_SIZE); 
				
		while ((receivedJson = peer.getCurrentMessage()) != null) //Receive initial array 
		{	 
			//slave = NetworkHelper.jsonToShareSlaveObject(receivedJson.toString());
			//slave = NetworkHelper.convertJsonToDictionary(receivedJson.toString());
			
			//averageXReceived =  Utilities.vectorAdd(averageXReceived, slave.getXOptimal());
			averageXReceived =  Utilities.vectorAdd(averageXReceived, Utilities.getXOptimalFromJson(receivedJson.toString()));
		}
	
		return averageXReceived;
	}
	
	//public static <K1,V1,K2,V2,M extends Writable> Map<String, double[]> receiveShareMasterDataObject(BSPPeer<K1,V1,K2,V2,M> peer) throws IOException
	public static <K1,V1,K2,V2,M extends Writable> String receiveShareMasterDataObject(BSPPeer<K1,V1,K2,V2,M> peer) throws IOException
	{
		//ShareMasterData masterData = new ShareMasterData();
		//Map<String, double[]> masterData = new HashMap<String, double[]>();
		M receivedJson;
		
		//while ((receivedJson = bspPeer.getCurrentMessage()) != null) //Receive initial array
		while ((receivedJson = peer.getCurrentMessage()) != null) //Receive initial array
		{	
			//masterData = NetworkHelper.jsonToShareMasterObject(receivedJson.toString());
			//masterData = NetworkHelper.convertJsonToDictionary(receivedJson.toString());
			LOG.info("Slave: Data found -> receiveShareMasterDataObject");
			break;
		}
		LOG.info("Received the data ----------");
		return receivedJson.toString();
		//return masterData;
	}
	
	public static <K1,V1,K2,V2,M extends Writable> void sendFinishMessage(BSPPeer<K1,V1,K2,V2,M> peer) throws IOException
	{
		//Map<String, double[]> data = new HashMap<String, double[]>();
		for(String peerName: peer.getAllPeerNames()) {
			if(!peerName.equals(BSPBase.masterTask)) {
				//peer.send(peerName,(M) new Text(NetworkHelper.shareMasterObjectToJson(new ShareMasterData(null, null))));
				
				//peer.send(peerName, (M) new Text(NetworkHelper.convertDictionaryToJson(data)));
				peer.send(peerName, (M) new Text(Utilities.getFinishedMessageObject()));
			}	
		}
	}
	
	//public static <K1,V1,K2,V2,M extends Writable> void sendShareSlaveObjectToMaster(Map<String, double[]> data,BSPPeer<K1,V1,K2,V2,M> peer) throws IOException
	public static <K1,V1,K2,V2,M extends Writable> void sendShareSlaveObjectToMaster(String data,BSPPeer<K1,V1,K2,V2,M> peer) throws IOException
	{	
		peer.send(masterTask, (M) new Text(data));
	}
}
