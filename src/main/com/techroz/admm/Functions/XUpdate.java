package main.com.techroz.admm.Functions;

import java.util.Dictionary;

import main.com.techroz.admm.ExchangeSolver.EVADMM.Context;

public interface XUpdate {//extends Update {
	//TODO: Instead of taking the input as string it can be improved
	//public double[] getXUpdate(String input, ExchangeContext context, int inputIndex, Dictionary properties);
	//public double[] getXUpdate(String input, ExchangeContext context, int inputIndex);
	public double[] getXUpdate(String input, Context context, int inputIndex);
}
