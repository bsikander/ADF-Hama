package main.com.techroz.adf.utils;
import java.io.IOException;

import main.com.techroz.adf.bsp.BSPBase;
import main.com.techroz.algorithm.exchange.BSPExchange;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hama.bsp.BSPPeer;

public class BSPHelper extends BSPExchange {
	
	public static final Log LOG = LogFactory.getLog(BSPHelper.class);
	
	/** This method sends the ShareMasterData object to all the slaves
	 * @param object Object containing u and xMean that needs to be sent
	 * @throws IOException
	 */
	public static <K1,V1,K2,V2,M extends Writable> void sendShareMasterObjectToSlaves(String data,BSPPeer<K1,V1,K2,V2, M> peer) throws IOException
	{	
		for(String p : peer.getAllPeerNames()) {
			if(!p.equals(masterTask)) {
				peer.send(p, (M) new Text(data));
			}
		}
	}
	
	/*
	 * 
	 */
	public static <K1,V1,K2,V2,M extends Writable> double[] getAverageOfReceivedOptimalSlaveValues(BSPPeer<K1,V1,K2,V2,M> peer) throws IOException
	{	
		M receivedJson;
		double[] averageXReceived = Utilities.getZeroArray(XOPTIMAL_SIZE); 
				
		while ((receivedJson = peer.getCurrentMessage()) != null) //Receive initial array 
		{	
			averageXReceived =  Utilities.vectorAdd(averageXReceived, Utilities.getXOptimalFromJson(receivedJson.toString()));
		}
	
		return averageXReceived;
	}
	
	public static <K1,V1,K2,V2,M extends Writable> String receiveShareMasterDataObject(BSPPeer<K1,V1,K2,V2,M> peer) throws IOException
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
	
	public static <K1,V1,K2,V2,M extends Writable> void sendFinishMessage(BSPPeer<K1,V1,K2,V2,M> peer) throws IOException
	{	
		for(String peerName: peer.getAllPeerNames()) {
			if(!peerName.equals(BSPBase.masterTask)) {
					peer.send(peerName, (M) new Text(Utilities.getFinishedMessageObject()));
			}	
		}
	}
	
	public static <K1,V1,K2,V2,M extends Writable> void sendShareSlaveObjectToMaster(String data,BSPPeer<K1,V1,K2,V2,M> peer) throws IOException
	{	
		peer.send(masterTask, (M) new Text(data));
	}
}
