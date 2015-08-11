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
import main.com.techroz.algorithm.exchange.ExchangeSlaveContext;

public class EVOptimizationFunction implements XUpdate{
	public static final Log LOG = LogFactory.getLog(EVOptimizationFunction.class);

	double[] xi_min;
	double[] xi_max;
	double[] S_max;
	double[] S_min;
	double[] A;
	double R;
	double gamma;
	double alpha;
	double rho;
	double[][] B;
	
	@Override
	//public double[] getXUpdate(String input,ContextBase context, int inputIndex) {
	public double[] getXUpdate(String input,ContextBase context) {
		LOG.info("CPLEXEVSlaveFunction here");
		parse(input);
		
		try {
			
			//return	optimize(context, inputIndex);
			return	optimize(context);
			
		} catch (FileNotFoundException e) {
			LOG.info(e.getMessage());
			e.printStackTrace();
		} catch (IloException e) {
			LOG.info(e.getMessage());
			e.printStackTrace();
		}
		
		return null;
	}
	
	//public double[] optimize(ContextBase context, int inputIndex) throws IloException, FileNotFoundException
	public double[] optimize(ContextBase context) throws IloException, FileNotFoundException
	{
		IloCplex cplex = new IloCplex();
		OutputStream out = new FileOutputStream("logfile_slave");
		cplex.setOut(out);

//		double[] x_old = ((ExchangeSlaveContext) context).getCurrentXOld();
//		double[] u = ((ExchangeSlaveContext) context).getU();
//		double[] xMean = ((ExchangeSlaveContext) context).getxMean();
		double[] x_old = context.getXOptimal();
		double[] u =  context.getU();
		double[] xMean = context.getxMean();
		double[] xOptimal = new double[x_old.length];
		
		//IloNumVar[] x_i = cplex.numVarArray(x.length, Double.MIN_VALUE, Double.MAX_VALUE);
		IloNumVar[] x_i = new IloNumVar[x_old.length];
		
		for(int i = 0; i < x_old.length ; i++) {
			x_i[i] = cplex.numVar(xi_min[i], xi_max[i]);
		}
		
	    double gammaAlpha = this.gamma * this.alpha;
		double[] data = subtractOldMeanU(x_old, xMean, u);
		
		IloNumExpr[] exps = new IloNumExpr[data.length];
		
		for(int i =0; i< data.length; i++)
		{	
			//exps[i] = cplex.sum(cplex.prod(gammaAlpha, cplex.square(x_i[i])) ,cplex.prod(rho/2, cplex.square(cplex.sum(x_i[i], cplex.constant(-data[i])))));
			exps[i] = cplex.sum(cplex.prod(gammaAlpha, cplex.square(x_i[i])) ,cplex.prod(rho/2, cplex.square(cplex.sum(x_i[i], cplex.constant(data[i])))));
		}
		
		IloNumExpr rightSide = cplex.sum(exps);
		cplex.addMinimize(rightSide);
		
		IloNumExpr[] AXExpEq = new IloNumExpr[data.length];
		
		for(int j = 0; j < data.length ; j++ )
		{
			AXExpEq[j] = cplex.prod(x_i[j], A[j]);
		}
		cplex.addEq(cplex.sum(AXExpEq), R);
		
		//S_min <= B_i*x_i <= S_max
		for(int h=0; h < B.length; h++)
		{
			IloNumExpr[] BXExpLe = new IloNumExpr[B[0].length];
			IloNumExpr[] BXExpGe = new IloNumExpr[B[0].length];
			
			for(int f=0; f < B[0].length; f++)
			{
				BXExpLe[f] = cplex.prod(x_i[f],B[h][f]);
				BXExpGe[f] = cplex.prod(x_i[f],B[h][f]);
			}

			cplex.addLe(cplex.sum(BXExpLe), this.S_max[h]);
			cplex.addGe(cplex.sum(BXExpGe), this.S_min[h]);
		}
		
		//cplex.exportModel("EV_" +inputIndex + ".lp");
		
		cplex.solve();
		
		LOG.info(cplex.getStatus());
		
		xOptimal = new double[x_i.length];
		
		for(int u1 = 0; u1 < x_i.length; u1++)
		{
			xOptimal[u1] = cplex.getValues(x_i)[u1];
		}
		
		LOG.info("SLAVE : PRINTING X_OPTIMAL VALUE");
		Utilities.PrintArray(xOptimal);
		
		return xOptimal;
	}
	
	private double[] subtractOldMeanU(double[] xold, double[] xMean, double[] u)
	{
		xold = Utilities.scalerMultiply(xold, -1);
		return Utilities.vectorAdd(Utilities.vectorAdd(xold, xMean), u);
	}
	
	private void parse(String input) {
		String[] splitData = input.split("\\|");
		
		xi_max = Utilities.getArray(splitData[0]);
		xi_min = Utilities.getArray(splitData[1]);
		A = Utilities.getArray(splitData[2]);
		R = Double.parseDouble(splitData[3]);
		gamma = Double.parseDouble(splitData[4]);
		alpha = Double.parseDouble(splitData[5]);
		rho = Double.parseDouble(splitData[6]);
		S_max = Utilities.getArray(splitData[7]);
		S_min = Utilities.getArray(splitData[8]);
		B = Utilities.getDoubleArray(splitData[9]);
	}
	
//	private double[] getArray(String input) {
//		double[] arr;
//		//System.out.println("INPUT> :" + input);
//		input = input.substring(1,input.length() - 1); //remove [ ] symbols
//		String[] values = input.split(",");
//		arr = new double[values.length];
//		
//		int index = 0;
//		for(String s: values) {
//			arr[index] = Double.parseDouble(s);
//			index ++;
//		}
//		return arr;
//	}
	
//	private double[][] getDoubleArray(String input) {
//		double[][] arr;
//		
//		String[] values = input.split("]");
//		
//		int index =0;
//		arr = new double[values.length][];
//		
//		for(String s: values) {
//			s += "]";
//			arr[index] = getArray(s);
//			index++;
//		}
//		return arr;
//	}


}
