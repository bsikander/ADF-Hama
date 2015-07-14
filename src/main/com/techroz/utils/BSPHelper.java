package main.com.techroz.utils;

import java.io.IOException;

import main.com.techroz.admm.ExchangeSolver.EVADMM.ShareMasterData;
import main.com.techroz.admm.ExchangeSolver.EVADMM.ShareSlaveData;
import main.com.techroz.bsp.BSPExchange.BSPExchange;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hama.bsp.BSPPeer;


public class BSPHelper extends BSPExchange {
	
	/** This method sends the ShareMasterData object to all the slaves
	 * @param object Object containing u and xMean that needs to be sent
	 * @throws IOException
	 */
	public static void sendShareMasterObjectToSlaves(ShareMasterData object) throws IOException
	{	
		for(String p : bspPeer.getAllPeerNames()) {
			if(!p.equals(masterTask)) {
				bspPeer.send(p, new Text(NetworkHelper.shareMasterObjectToJson(object)));
			}
		}
	}
	
	/*
	 * 
	 */
	public static double[] getAverageOfReceivedOptimalSlaveValues() throws IOException
	{	
		ShareSlaveData slave;
		Text receivedJson;
		double[] averageXReceived = Utilities.getZeroArray(XOPTIMAL_SIZE); 
				
		while ((receivedJson = bspPeer.getCurrentMessage()) != null) //Receive initial array 
		{
			slave = NetworkHelper.jsonToShareSlaveObject(receivedJson.toString());
			averageXReceived =  Utilities.vectorAdd(averageXReceived, slave.getXOptimal()); 
		}
	
		return averageXReceived;
	}
	
	public static ShareMasterData receiveShareMasterDataObject() throws IOException
	{
		ShareMasterData masterData = new ShareMasterData();
		Text receivedJson;
		
		while ((receivedJson = bspPeer.getCurrentMessage()) != null) //Receive initial array 
		{	
			masterData = NetworkHelper.jsonToShareMasterObject(receivedJson.toString());
			break;
		}
		
		return masterData;
	}
	
	public static void sendShareSlaveObjectToMaster(ShareSlaveData object) throws IOException
	{	
		bspPeer.send(masterTask, new Text(NetworkHelper.shareSlaveObjectToJson(object)));
	}
}
