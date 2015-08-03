package main.com.techroz.adf.bsp;

import java.io.IOException;

import main.com.techroz.algorithm.exchange.BSPExchange;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hama.bsp.BSP;
import org.apache.hama.bsp.BSPPeer;
import org.apache.hama.bsp.sync.SyncException;

public abstract class BSPBase<K1, V1, K2, V2, M extends Writable> extends BSP<K1, V1, K2, V2, M> {
	public static final Log LOG = LogFactory.getLog(BSPBase.class);
	protected static String masterTask;
	
	
	@Override
	public abstract void bsp(BSPPeer<K1, V1, K2, V2, M> peer) throws IOException,
	      SyncException, InterruptedException;
	  
	@Override
	public void setup(BSPPeer<K1, V1, K2, V2, M> peer) throws IOException,
	      SyncException, InterruptedException {
		LOG.info(peer.getPeerName() + " is starting up");
		
		BSPBase.masterTask = peer.getPeerName(0); //0 is out master
	}

	@Override
	public void cleanup(BSPPeer<K1, V1, K2, V2, M> peer) throws IOException {
		LOG.info(peer.getPeerName() + " is shutting down");
	}
}
