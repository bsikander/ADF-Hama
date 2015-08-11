package main.com.techroz.algorithm.exchange;

import java.util.HashMap;
import java.util.Map;

import org.mortbay.log.Log;

import main.com.techroz.adf.admm.ContextBase;
import main.com.techroz.adf.admm.XUpdate;
import main.com.techroz.adf.utils.Constants;

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
	
	/*
	 * (non-Javadoc)
	 * @see main.com.techroz.adf.admm.ContextBase#getXOptimal()
	 * This property overrides the base property. Since this class manages the current index of input and context
	 * It returns whatever the xOptimal value is of current EV.
	 */
	public double[] getXOptimal() {
		return this.wholeX[currentInputIndex];
	}
	
	
//	public double[] getCurrentXOld() {
//		return this.wholeX[currentInputIndex];
//	}
	
	public Map<String, double[]> getSlaveData() {
		Map<String, double[]> data = new HashMap<String,double[]>();
		data.put("xOptimal", xOptimal);
		
		return data;
	}
	
	public void setMasterData(Map<String, double[]> data) {
		this.xMean = data.get("xMean");
		this.u = data.get("u");
	}
	
	/*
	 * This function will return the schema of data provided by the user 
	 */
	public String getDataSchema() {
		return getConfiguration(Constants.ADF_FUNCTION2_DATA_SCHEMA);
	}
	
	/*
	 * This function will provide the path of model submitted by user
	 */
	public String getModelPath() {
		return getConfiguration(Constants.ADF_FUNCTION2_MODEL_PATH);
	}
	
}
