package main.com.techroz.admm.ExchangeSolver.EVADMM;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;

public class ShareMasterData implements Writable {
	public static final Log LOG = LogFactory.getLog(ShareMasterData.class);
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	@JsonProperty("u")
	private double[] u;
	
	@JsonProperty("xMean")
	private double[] xMean;
	
	public ShareMasterData()
	{	
	}
	
	public ShareMasterData(double[] u, double[] xMean)
	{
		this.u = u;
		this.xMean = xMean;
	}
	
	public void setShareMasterData(ShareMasterData n)
	{
		this.u = n.u;
		this.xMean = n.xMean;
	}
	
	@Override
	public void readFields(DataInput in) throws IOException {
		Text contextJson = new Text();
		contextJson.readFields(in);
		setShareMasterData(OBJECT_MAPPER.readValue(contextJson.toString(), ShareMasterData.class));
	}

	@Override
	public void write(DataOutput out) throws IOException {
		Text contextJson = new Text(OBJECT_MAPPER.writeValueAsString(this));
		contextJson.write(out);
	}
	
	@JsonProperty("u")
	public double[] getU()
	{
		return this.u;
	}
	
	@JsonProperty("xMean")
	public double[] getxMean()
	{
		return this.xMean;
	}
	
	public void setU(double[] value)
	{
		this.u = value;
	}
	
	public void setxMean(double[] value) 
	{
		this.xMean = value;
	}
}
