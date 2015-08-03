package main.com.techroz.adf.admm;

public abstract class ContextBase { //implements Context {// implements UUpdate, XUpdate {
	
	protected double[] xMean;
	protected double[] xOptimal;
	protected double[] u;
	
	public ContextBase(int size) {
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
}
