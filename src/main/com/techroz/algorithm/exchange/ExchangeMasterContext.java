package main.com.techroz.algorithm.exchange;

import java.util.HashMap;
import java.util.Map;

import main.com.techroz.adf.admm.ContextBase;
import main.com.techroz.adf.admm.XUpdate;
import main.com.techroz.adf.utils.Utilities;

public class ExchangeMasterContext extends ContextBase {
	XUpdate xUpdateFunction;
	
	public ExchangeMasterContext(int size, XUpdate masterFunction, Map<String, String> configurationProperties) {
		super(size, configurationProperties);
		
		this.xUpdateFunction = masterFunction;
	}

	public double[] calculateU() {
		u = Utilities.vectorAdd(u, xMean);
		return u;
	}
	
	public double[] getXUpdate(String input) {
		//xOptimal = xUpdateFunction.getXUpdate(input, this, inputIndex);
		xOptimal = xUpdateFunction.getXUpdate(input, this);
		return xOptimal;
	}
	
	public double[] calculateXMean(double[] averageSlaveOptimal,int total) {
		double[] average = Utilities.vectorAdd(xOptimal, averageSlaveOptimal);
		xMean = Utilities.calculateMean(average, total);
		return xMean;
	}
	
	public boolean converged() {
		
		return false;
	}
	
	public Map<String, double[]> getMasterData() {
		Map<String, double[]> data = new HashMap<String,double[]>();
		
		data.put("u", u);
		data.put("xMean", xMean);
		
		return data;
	}
	
		
}
