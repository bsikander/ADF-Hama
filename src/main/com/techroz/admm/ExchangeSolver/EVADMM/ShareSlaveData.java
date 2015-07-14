package main.com.techroz.admm.ExchangeSolver.EVADMM;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;


public class ShareSlaveData implements Writable {
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	@JsonProperty("xOptimal")
	double[] xOptimal;
	
	public ShareSlaveData(double[] xOptimal)
	{
		this.xOptimal = xOptimal;
	}

	public void setNetworkObjectSlave(ShareSlaveData n)
	{
		this.xOptimal = n.xOptimal;
	}
	
	public ShareSlaveData()
	{}
	
	@Override
	public void readFields(DataInput in) throws IOException {
		Text contextJson = new Text();
		contextJson.readFields(in);
		setNetworkObjectSlave(OBJECT_MAPPER.readValue(contextJson.toString(), ShareSlaveData.class));
		
	}

	@Override
	public void write(DataOutput out) throws IOException {
		Text contextJson = new Text(OBJECT_MAPPER.writeValueAsString(this));
		contextJson.write(out);
		
	}
	
	@JsonProperty("xOptimal")
	public double[] getXOptimal()
	{
		return this.xOptimal;
	}
	
	public void setXOptimal(double[] value) {
		this.xOptimal = value;
	}
}
