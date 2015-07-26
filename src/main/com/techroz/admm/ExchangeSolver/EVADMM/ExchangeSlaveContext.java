package main.com.techroz.admm.ExchangeSolver.EVADMM;

import main.com.techroz.admm.Functions.XUpdate;

public class ExchangeSlaveContext extends ExchangeBase {// implements XUpdate {
	private double[][] wholeX;
	XUpdate xUpdateFunction;
	
	public ExchangeSlaveContext(int size, XUpdate slaveFunction) {
		super(size);
		
		wholeX = new double[size][11]; //TODO: a matrix with 96 by number of EV processed on this machine
		this.xUpdateFunction = slaveFunction;
	}
	
	//@Override
	public double[] getXUpdate(String input, int inputIndex) {
		wholeX[inputIndex] = xOptimal;
		xOptimal = xUpdateFunction.getXUpdate(input, this, inputIndex);
		return xOptimal;
	}
	
	public double[] getXOld(int index) {
		return this.wholeX[index];
	}
	
}
