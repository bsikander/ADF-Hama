package main.com.techroz.deleteme;

public class ResultMaster {
	String peerName;
	int iteration;
	int ev;
	double[] u;
	double[] xMean;
	double[] x_master_optimal;
	double costValue;
	double[] evAverage;
	
	public ResultMaster(String peerName,int iteration, int ev_number, double[] u, double[] xMean, double[] x_optimal, double costvalue, double[] evAverageValue)
	{
		this.peerName = peerName;
		this.iteration = iteration;
		this.ev = ev_number;
		this.u = u;
		this.xMean = xMean;
		this.x_master_optimal = x_optimal;
		this.costValue = costvalue;
		this.evAverage = evAverageValue;
	}
	
	public String printResult(int count)
	{
		double[] temp = evAverage;
		
		double x_evSum = 0;
		for(int k=0; k < temp.length;k++)
			x_evSum += temp[k];
		
		double sum = 0;
		for(double d: this.x_master_optimal)
			sum+=d;

		double sumxMean=0;
		for(double d: this.xMean)
			sumxMean+=d;
		
		double sumu=0;
		for(double d: this.u)
			sumu+=d;
		
		if(count>0) {
			String print = "M:=> " + this.peerName + " \t " + this.iteration + " \t\t " + this.ev + " \t " + this.costValue + " \t " + x_evSum + " \t " + sum  + " \t " + sumxMean + " \t " + sumu + "\t" + this.x_master_optimal[0] + "\t" + this.xMean[0] + "\t" + this.u[0];
			System.out.println(print);
			return print;
		}
		else {
			String print = "M:=> " + this.peerName + " \t " + this.iteration + " \t\t " + this.ev + " \t " + this.costValue + " \t " + x_evSum + " \t " + sum  + " \t " + sumxMean + " \t " + sumu + "\t" + this.x_master_optimal[0] + "\t" + this.xMean[0] + "\t" + this.u[0];
			System.out.println(print);
			return print;
		}
		
	}
}
