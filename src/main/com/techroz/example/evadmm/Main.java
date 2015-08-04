package main.com.techroz.example.evadmm;

import java.io.IOException;

import main.com.techroz.adf.bsp.ADFJob;
import main.com.techroz.algorithm.exchange.BSPExchange;

public class Main {

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		ADFJob job = new ADFJob();
		job.setMaxIteration(2);
		job.setJobName("ADF Exchage EVADMM job");
		job.setInputPath("/Users/raja/Documents/workspace/ADFHama/data/aggregator.txt,/Users/raja/Documents/workspace/ADFHama/data/EVs.txt");
		job.setOutputPath("/Users/raja/Documents/workspace/ADFHama/output/");
     	job.setSolutionVectorSize(96); //TODO: Find some alternative. Try to make it part of BSPExchange class
		job.setFunction1(PriceBasedOptimizationFunction.class);
		job.setFunction2(EVOptimizationFunction.class);
		job.setADMMClass(BSPExchange.class);
		
		job.run();
		
		//Only required if OPLGeneric method is used
//		job.setDataHeader("d,A,R,Smax,Smin,B"); //Slave data
//		job.setDataHeader("price,re,D,xa_min,xa_max"); //Master data		
	}
}
