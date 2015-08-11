package main.com.techroz.adf.admm;

import java.util.Map;

import org.mortbay.log.Log;

public abstract class ContextBase { //implements Context {// implements UUpdate, XUpdate {
	
	protected double[] xMean;
	protected double[] xOptimal;
	protected double[] u;
	protected Map<String,String> configurationProperties;
	
	public ContextBase(int size, Map<String, String> configurationProperties) {
		xMean = new double[size];
		u = new double[size];
		xOptimal = new double[size];
		this.configurationProperties = configurationProperties;
	}
	
	public String getConfiguration(String key) {
		return configurationProperties.get(key);
	}
	
	public void setxMean(double[] value) {
		this.xMean = value;
	}
	
	public void setU(double[] value) {
		this.u = value;
	}
	
	public double[] getxMean() {
		return this.xMean;
	}
	
	public double[] getU() {
		return this.u;
	}
	
	public double[] getXOptimal() {
		return this.xOptimal;
	}
	
	public  String getDataSchema() {
		return "";
	}
	public  String getModelPath() {
		return "";
	}
}
