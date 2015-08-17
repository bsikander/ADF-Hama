package main.com.techroz.example.evadmm;

import java.io.IOException;

import main.com.techroz.adf.bsp.ADFJob;
import main.com.techroz.algorithm.exchange.BSPExchange;
import main.com.techroz.algorithm.exchange.ExchangeOPLGenericSolver;

public class Main {

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		ADFJob job = new ADFJob();
		job.setMaxIteration(4);
		job.setJobName("ADF Exchage EVADMM job");
		
		job.setInputPath("/Users/raja/Documents/workspace/ADFHama/data/aggregator.txt,/Users/raja/Documents/workspace/ADFHama/data/EVs.txt");
		job.setOutputPath("/Users/raja/Documents/workspace/ADFHama/output/");
		//job.setInputPath("/home/bsikander/ADF-Hama/data/aggregator.txt,/home/bsikander/ADF-Hama/data/EVs.txt");
//		job.setOutputPath("/home/bsikander/ADF-Hama/output/");
     	
     	job.setSolutionVectorSize(96); //TODO: Find some alternative. Try to make it part of BSPExchange class
		job.setFunction1(PriceBasedOptimizationFunction.class);
     	//job.setFunction1(ExchangeOPLGenericSolver.class, "/home/bsikander/Documents/OPLProject/EVADMM/EVADMM.mod", "timeSlot,price,rho");
     	//job.setFunction1(ExchangeOPLGenericSolver.class);	
     	job.setFunction2(EVOptimizationFunction.class);
     	//job.setFunction2(ExchangeOPLGenericSolver.class, "/home/bsikander/Documents/OPLProject/EVADMM-Slave/EVADMM-Slave.mod", "timeSlot,xi_max,xi_min,A,R_value,gamma,alpha,rho,smax,smin,B");
		job.setADMMClass(BSPExchange.class);
		
		job.run();
		
		//Only required if OPLGeneric method is used
//		job.setDataHeader("d,A,R,Smax,Smin,B"); //Slave data
//		job.setDataHeader("price,re,D,xa_min,xa_max"); //Master data		
		//"price,re,d,xa_min,xa_max,rho"
	}
}
