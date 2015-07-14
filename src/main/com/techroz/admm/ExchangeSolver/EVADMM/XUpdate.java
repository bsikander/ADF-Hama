package main.com.techroz.admm.ExchangeSolver.EVADMM;

public interface XUpdate {
	//TODO: Instead of taking the input as string it can be improved
	public double[] getXUpdate(String input, ExchangeContext context, int inputIndex);
}
