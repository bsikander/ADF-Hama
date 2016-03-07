package main.com.techroz.admm.Functions;

import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import main.com.techroz.admm.ExchangeSolver.EVADMM.ExchangeContext;
import main.com.techroz.admm.ExchangeSolver.EVADMM.XUpdate;
import main.com.techroz.utils.Utilities;

public class CPLEXEVMasterFunction implements XUpdate {
	private double[] price;
	private double[] xa_min;
	private double[] xa_max;
	private double rho;
	
	
	@Override
	public double[] getXUpdate(String input, ExchangeContext context, int inputIndex) {
		// TODO Auto-generated method stub
		System.out.println("CPLEXEVMaster Function here");
		
		parse(input);
		
		try {
			return optimize(context);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public double[] optimize(ExchangeContext context) throws IloException, FileNotFoundException
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
		System.out.println("MASTER:: Optimal Value: " + cplex.getObjValue());
		
		xOptimal = new double[x_n.length];
		
		//System.out.println("======= MASTER: OPTIMZATION ARRAY =====");
		for(int u1=0; u1 < x_n.length; u1++)
		{
			xOptimal[u1] = cplex.getValues(x_n)[u1];
		}
		
		System.out.println("MASTER: Optimal Value");
		
		
		Utilities.PrintArray(xOptimal);
		return xOptimal;
	}
	
	private void parse(String input) {
		String[] splitData = input.split("\\|");
		
		price = getArray(splitData[0]);
		xa_min = getArray(splitData[3]);
		xa_max = getArray(splitData[4]);
		rho = Double.parseDouble(splitData[5]);
	}
	
	private double[] getArray(String input) {
		double[] arr;
		//System.out.println("GetArray -> " + input);
		input = input.substring(1,input.length() - 1); //remove [ ] symbols
		String[] values = input.split(",");
		arr = new double[values.length];
		
		int index = 0;
		for(String s: values) {
			arr[index] = Double.parseDouble(s);
			index ++;
		}
		return arr;
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