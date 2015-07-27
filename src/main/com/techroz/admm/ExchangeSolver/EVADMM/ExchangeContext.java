package main.com.techroz.admm.ExchangeSolver.EVADMM;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import main.com.techroz.admm.Functions.XUpdate;
import main.com.techroz.utils.Utilities;

public class ExchangeContext {// extends ExchangeBase {
//	public static final Log LOG = LogFactory.getLog(ExchangeContext.class);
//	
////	private double[] xMean;
////	private double[] xOptimal;
////	private double[] u;
//	XUpdate xUpdateFunction;
//	private double[][] wholeX;
//	//XUpdate slaveOptimizationFunction;
//	
//	public ExchangeContext(int size, XUpdate masterFunction) {
//		super(size);
//		
//		xMean = new double[size];
//		u = new double[size];
//		xOptimal = new double[size];
//		wholeX = new double[size][11]; //TODO: a matrix with 96 by number of EV processed on this machine 
//		
//		this.xUpdateFunction = masterFunction;
//	}
//	
//	public double[] getXUpdate(String input, int inputIndex) {
//		wholeX[inputIndex] = xOptimal;
//		return this.getXUpdate(input, this, inputIndex);
//	}
//	
//	public double[] getXUpdate(String input,ExchangeContext context, int inputIndex) {
//		xOptimal = xUpdateFunction.getXUpdate(input, context, inputIndex);
//		return xOptimal;
//	}
//	
//	public double[] calculateXMean(double[] averageSlaveOptimal,int total) {
//		double[] average = Utilities.vectorAdd(xOptimal, averageSlaveOptimal);
//		xMean = Utilities.calculateMean(average, total);
//		return xMean;
//	}
//	
//	public double[] calculateU() {
//		u = Utilities.vectorAdd(u, xMean);
//		return u;
//	}
//	
//	public boolean converged() {
//		//TODO: Add logic here
//		return false;
//	}
//	
//	public void setMasterData(ShareMasterData data) {
//		this.xMean = data.getxMean();
//		this.u = data.getU();
//	}
	
//	public ShareMasterData getMasterData()
//	{
//		ShareMasterData data = new ShareMasterData();
//		Utilities.PrintArray(u);
//		Utilities.PrintArray(xMean);
//		data.setU(u);
//		data.setxMean(xMean);
//		return data;
//	}
//	
//	public ShareSlaveData getSlaveData() {
//		ShareSlaveData data = new ShareSlaveData();
//		data.setXOptimal(xOptimal);
//		
//		return data;
//	}
	
//	public void setxMean(double[] value) {
//		this.xMean = value;
//	}
//	
//	public void setU(double[] value) {
//		this.u = value;
//	}
//	
//	public double[] getxMean() {
//		return this.xMean;
//	}
//	
//	public double[] getU() {
//		return this.u;
//	}
//	
//	public double[] getXOptimal() {
//		return this.xOptimal;
//	}
	
//	public double[] getXOld(int index) {
//		return this.wholeX[index];
//	}
}
