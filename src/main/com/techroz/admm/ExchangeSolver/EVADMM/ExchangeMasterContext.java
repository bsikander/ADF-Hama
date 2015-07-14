package main.com.techroz.admm.ExchangeSolver.EVADMM;

import main.com.techroz.utils.Utilities;

public class ExchangeMasterContext extends ExchangeBase {
	private double[] xMean;
	private double[] xOptimal;
	private double[] u;
	XUpdate masterOptimizationFunction;

	public ExchangeMasterContext(int size, XUpdate masterFunction) {
		xMean = new double[size];
		u = new double[size];
		xOptimal = new double[size];
		
		this.masterOptimizationFunction = masterFunction;
	}
	
	public double[] optimizeMasterFunction(String input) {
		xOptimal = masterOptimizationFunction.getXUpdate(input);
		return xOptimal;
	}
	
	public double[] calculateXMean(double[] averageSlaveOptimal,int total) {
		double[] average = Utilities.vectorAdd(xOptimal, averageSlaveOptimal);
		xMean = Utilities.calculateMean(average, total);
		return xMean;
	}
	
	public double[] calculateU() {
		u = Utilities.vectorAdd(u, xMean);
		return u;
	}
	
	public ShareMasterData getMasterData()
	{
		ShareMasterData data = new ShareMasterData();
		data.setU(u);
		data.setxMean(xMean);
		return data;
	}
	
	public boolean converged() {
		//TODO: Add logic here
		return false;
	}
}
