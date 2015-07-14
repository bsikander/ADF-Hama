package main.com.techroz.admm.ExchangeSolver.EVADMM;

public class ExchangeSlaveContext extends ExchangeBase {
	
	private double[] xOptimal;
	private double[] xMean; //Master xMean
	private double[] u; //Master u
	
	XUpdate slaveOptimizationFunction;
	
	public ExchangeSlaveContext(int size, XUpdate slaveFunction) {
		this.xOptimal = new double[size];
		this.slaveOptimizationFunction = slaveFunction;
	}
	
	public void setMasterData(ShareMasterData data) {
		this.xMean = data.getxMean();
		this.u = data.getU();
	}
	
	public void setxMean(double[] value) {
		this.xMean = value;
	}
	
	public void setU(double[] value) {
		this.u = value;
	}
	
	public double[] optimizeSlaveFunction(String input) {
		xOptimal = slaveOptimizationFunction.getXUpdate(input);
		return xOptimal;
	}
	
	public ShareSlaveData getSlaveData() {
		ShareSlaveData data = new ShareSlaveData();
		data.setXOptimal(xOptimal);
		
		return data;
	}
}
