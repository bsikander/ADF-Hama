package main.com.techroz.admm.ExchangeSolver.EVADMM;

import java.util.HashMap;
import java.util.Map;

import main.com.techroz.admm.Functions.XUpdate;

public class ExchangeSlaveContext extends ExchangeBase {// implements XUpdate {
	private double[][] wholeX;
	XUpdate xUpdateFunction;
	
	public ExchangeSlaveContext(int size, XUpdate slaveFunction) {
		super(size);
		
		wholeX = new double[size][11]; //TODO: a matrix with 96 by number of EV processed on this machine
		this.xUpdateFunction = slaveFunction;
	}
	
	public double[] getXUpdate(String input, int inputIndex) {
		wholeX[inputIndex] = xOptimal;
		xOptimal = xUpdateFunction.getXUpdate(input, this, inputIndex);
		return xOptimal;
	}
	
	public double[] getXOld(int index) {
		return this.wholeX[index];
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
