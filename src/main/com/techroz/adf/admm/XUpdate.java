package main.com.techroz.adf.admm;

public interface XUpdate {//extends Update {
	//TODO: Instead of taking the input as string it can be improved
	public double[] getXUpdate(String input, ContextBase context, int inptutIndex);
}
