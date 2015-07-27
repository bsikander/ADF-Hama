package main.com.techroz.admm.ExchangeSolver.EVADMM;

import java.util.HashMap;
import java.util.Map;

import main.com.techroz.admm.Functions.XUpdate;
import main.com.techroz.utils.Utilities;

public class ExchangeMasterContext extends ExchangeBase {//implements XUpdate, UUpdate {
	
	XUpdate xUpdateFunction;
	
	public ExchangeMasterContext(int size, XUpdate masterFunction) {
		super(size);
		
		this.xUpdateFunction = masterFunction;
	}
	
	//@Override
//	public double[] getXUpdate(String input, Context context, int inputIndex) {
//		xOptimal = xUpdateFunction.getXUpdate(input, context, inputIndex);
//		return xOptimal;
//	}
	
	//@Override
	public double[] calculateU() {
		u = Utilities.vectorAdd(u, xMean);
		return u;
	}
	
	public double[] getXUpdate(String input, int inputIndex) {
		xOptimal = xUpdateFunction.getXUpdate(input, this, inputIndex);
		return xOptimal;
	}
	
	public double[] calculateXMean(double[] averageSlaveOptimal,int total) {
		double[] average = Utilities.vectorAdd(xOptimal, averageSlaveOptimal);
		xMean = Utilities.calculateMean(average, total);
		return xMean;
	}
	
	public boolean converged() {
		//TODO: Add logic here
		return false;
	}
	
	public Map<String, double[]> getMasterData() {
		Map<String, double[]> data = new HashMap<String,double[]>();
		
		data.put("u", u);
		data.put("xMean", xMean);
		
		return data;
	}
	
		
}
