package main.com.techroz.admm.ExchangeSolver.EVADMM;

import main.com.techroz.admm.Functions.UUpdate;
import main.com.techroz.admm.Functions.XUpdate;

public abstract class ExchangeBase implements Context {// implements UUpdate, XUpdate {
	
	protected double[] xMean;
	protected double[] xOptimal;
	protected double[] u;
	
	public ExchangeBase(int size) {
		xMean = new double[size];
		u = new double[size];
		xOptimal = new double[size];
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

	public ShareSlaveData getSlaveData() {
		ShareSlaveData data = new ShareSlaveData();
		data.setXOptimal(xOptimal);
		
		return data;
	}
	
	public ShareMasterData getMasterData()
	{
		ShareMasterData data = new ShareMasterData();
		data.setU(u);
		data.setxMean(xMean);
		return data;
	}
	
	public void setMasterData(ShareMasterData data) {
		this.xMean = data.getxMean();
		this.u = data.getU();
	}
	
//	@Override
//	public double[] getUUpdate() {
//		return null;
//		//No U Update is required in case of 
//	}
	
//	@Override
//	public double[] getXUpdate(String input) {
//		return null;
//		
//	}
}
