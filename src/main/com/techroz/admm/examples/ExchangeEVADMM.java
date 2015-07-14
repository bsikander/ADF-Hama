package main.com.techroz.admm.examples;

import java.io.IOException;

import main.com.techroz.ADFJob;
import main.com.techroz.admm.Functions.CPLEXEVMasterFunction;
import main.com.techroz.admm.Functions.CPLEXEVSlaveFunction;
import main.com.techroz.bsp.BSPExchange.BSPExchange;

public class ExchangeEVADMM {

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		ADFJob job = new ADFJob();
		job.setInputPath("/Users/raja/Documents/workspace/ADFHama/data/aggregator.txt,/Users/raja/Documents/workspace/ADFHama/data/EVs.txt");
		job.setJobName("ADF Exchage EVADMM job");
		job.setMaxIteration(3);
		job.setOutputPath("/Users/raja/Documents/workspace/ADFHama/output/");
		
		job.setMasterXUpdate(CPLEXEVMasterFunction.class);
		job.setSlaveXUpdate(CPLEXEVSlaveFunction.class);
		
		job.setADMMSolverClass(BSPExchange.class);
		
		job.setDataHeader("d,A,R,Smax,Smin,B"); //Slave data
		job.setDataHeader("price,re,D,xa_min,xa_max"); //Master data
		job.setXOptimalSize(96); //TODO: Find some alternative. Try to make it part of BSPExchange class
		//TODO: Set IFunction f and g
		job.run();
	}

}
