package main.com.techroz.example.evadmm;

import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import main.com.techroz.adf.admm.ContextBase;
import main.com.techroz.adf.admm.XUpdate;
import main.com.techroz.adf.utils.Utilities;
import main.com.techroz.algorithm.exchange.ExchangeMasterContext;

public final class PriceBasedOptimizationFunction implements XUpdate {
	public static final Log LOG = LogFactory.getLog(PriceBasedOptimizationFunction.class);
	
	private double[] price;
	private Double rho;
	
	
	@Override
	public double[] getXUpdate(String input, ContextBase context) {
		LOG.info("CPLEXEVMaster Function here");
		context = (ExchangeMasterContext) context;
		
		parse(input);
		
		try {
			return optimize(context);
		} catch (FileNotFoundException e) {
			LOG.info(e.getMessage());
			e.printStackTrace();
		} catch (IloException e) {
			LOG.info(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public double[] optimize(ContextBase context) throws IloException, FileNotFoundException
	{	
		IloCplex cplex = new IloCplex();
		OutputStream out = new FileOutputStream("logfile_master");
		cplex.setOut(out);

		double[] x = context.getXOptimal();
		double[] u = context.getU();
		double[] xMean = context.getxMean();
		double[] xOptimal = new double[x.length];
		
		IloNumVar[] x_n = cplex.numVarArray(price.length, -60, 100000);
		
		double[] priceRealMatrix = price;
		priceRealMatrix = Utilities.scalerMultiply(priceRealMatrix, -1);
		
		double[] data = subtractOldMeanU(x, xMean, u);
		
		IloNumExpr[] exps = new IloNumExpr[data.length];
		
		for(int i =0; i< data.length; i++)
		{	
			//Original equation
			exps[i] = cplex.sum(cplex.prod(priceRealMatrix[i], x_n[i]) ,cplex.prod(rho/2, cplex.square(cplex.sum(x_n[i], cplex.constant(data[i])))));
		}
		
		IloNumExpr rightSide = cplex.sum(exps);
		cplex.addMinimize(rightSide);
				
		//cplex.exportModel("TestModel_beh" + iteration +".lp");

		cplex.solve();
		LOG.info("MASTER:: Optimal Value: " + cplex.getObjValue());
		
		xOptimal = new double[x_n.length];
		
		for(int u1=0; u1 < x_n.length; u1++)
		{
			xOptimal[u1] = cplex.getValues(x_n)[u1];
		}
		
		LOG.info("MASTER: Optimal Value");
		
		
		Utilities.PrintArray(xOptimal);
		return xOptimal;
	}
	
	private void parse(String input) {
		String[] splitData = input.split("\\|");
		
		price = Utilities.getArray(splitData[1]);
		rho = Double.parseDouble(splitData[2]);
	}
	
	private double[] subtractOldMeanU(double[] xold, double[] xMean, double[] u)
	{	
		xold = Utilities.scalerMultiply(xold, -1);
		double[] temp = Utilities.vectorAdd(xold, xMean);
		double[] output = Utilities.vectorAdd(temp, u);
		
		return output;
	}
	
	public int getT()
	{	
		return (24*3600)/(15*60);
	}

}
