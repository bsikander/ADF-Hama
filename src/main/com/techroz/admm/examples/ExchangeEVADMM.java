package main.com.techroz.admm.examples;

import java.io.IOException;

import main.com.techroz.ADFJob;
import main.com.techroz.admm.Functions.EVADMM.CPLEXEVMasterFunction;
import main.com.techroz.admm.Functions.EVADMM.CPLEXEVSlaveFunction;
import main.com.techroz.bsp.BSPExchange.BSPExchange;

public class ExchangeEVADMM {

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		ADFJob job = new ADFJob();
		job.setMaxIteration(2);
		job.setJobName("ADF Exchage EVADMM job");
		job.setInputPath("/Users/raja/Documents/workspace/ADFHama/data/aggregator.txt,/Users/raja/Documents/workspace/ADFHama/data/EVs.txt");
		job.setOutputPath("/Users/raja/Documents/workspace/ADFHama/output/");
     	job.setSolutionVectorSize(96); //TODO: Find some alternative. Try to make it part of BSPExchange class
		job.setFunction1(CPLEXEVMasterFunction.class);
		job.setFunction2(CPLEXEVSlaveFunction.class);
		job.setADMMClass(BSPExchange.class);
		
		job.run();
		
		//Only required if OPLGeneric method is used
//		job.setDataHeader("d,A,R,Smax,Smin,B"); //Slave data
//		job.setDataHeader("price,re,D,xa_min,xa_max"); //Master data		
	}
}
