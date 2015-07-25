package main.com.techroz.admm.Functions;

import main.com.techroz.admm.ExchangeSolver.EVADMM.ExchangeContext;

public interface XUpdate extends Update {
	//TODO: Instead of taking the input as string it can be improved
	public double[] getXUpdate(String input, ExchangeContext context, int inputIndex);
}
