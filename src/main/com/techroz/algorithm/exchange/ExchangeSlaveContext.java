package main.com.techroz.algorithm.exchange;

import java.util.HashMap;
import java.util.Map;

import main.com.techroz.adf.admm.ContextBase;
import main.com.techroz.adf.admm.XUpdate;

public class ExchangeSlaveContext extends ContextBase {
	private double[][] wholeX;
	XUpdate xUpdateFunction;
	private int currentInputIndex;
	
	public ExchangeSlaveContext(int size, XUpdate slaveFunction, Map<String, String> configurationProperties) {
		super(size, configurationProperties);
		
		//wholeX = new double[size][11]; //a matrix with 96 by number of EV processed on this machine
		wholeX = new double[size][];
		this.xUpdateFunction = slaveFunction;
	}
	
	public double[] getXUpdate(String input, int inputIndex) {
		wholeX[inputIndex] = xOptimal;
		
		currentInputIndex = inputIndex;
		//xOptimal = xUpdateFunction.getXUpdate(input, this, inputIndex);
		xOptimal = xUpdateFunction.getXUpdate(input, this);
		return xOptimal;
	}
	
	//public double[] getXOld(int index) {
	public double[] getCurrentXOld() {
		//return this.wholeX[index];
		return this.wholeX[currentInputIndex];
	}
	
	public Map<String, double[]> getSlaveData() {
		Map<String, double[]> data = new HashMap<String,double[]>();
		data.put("xOptimal", xOptimal);
		
		return data;
	}
	
	public void setMasterData(Map<String, double[]> data) {
		this.xMean = data.get("xMean");
		this.u = data.get("u");
	}
	
}
