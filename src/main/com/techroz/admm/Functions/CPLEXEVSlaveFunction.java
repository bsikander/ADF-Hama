package main.com.techroz.admm.Functions;

import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import main.com.techroz.admm.ExchangeSolver.EVADMM.XUpdate;

public class CPLEXEVSlaveFunction implements XUpdate{

	@Override
	public double[] getXUpdate(String input) {
		// TODO Auto-generated method stub
		System.out.println("CPLEXEVSlaveFunction here");
		return null;
	}
	
	public double optimize() throws IloException, FileNotFoundException
	{
		IloCplex cplex = new IloCplex();
		OutputStream out = new FileOutputStream("logfile_slave");
		cplex.setOut(out);
		
		//IloNumVar[] x_i = cplex.numVarArray(x.length, Double.MIN_VALUE, Double.MAX_VALUE);
		IloNumVar[] x_i = new IloNumVar[x.length];
		
		for(int i = 0; i < x.length ; i++) {
			x_i[i] = cplex.numVar(xi_min[i], xi_max[i]);
		}
		
	    double gammaAlpha = this.gamma * this.alpha;
		double[] data = subtractOldMeanU(x);
		
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
			//This constraint is already defined in the variable boundaries.
			//x_min <= x_i <= x_max
//			cplex.addLe(x_i[j], xi_max[j]);
//			cplex.addGe(x_i[j], xi_min[j]);
 
			
			//A_i*x_i = R
			//cplex.addEq(cplex.prod(x_i[j], this.slaveData.getA()[j]), this.slaveData.getR());
			AXExpEq[j] = cplex.prod(x_i[j], this.slaveData.getA()[j]);
		}
		cplex.addEq(cplex.sum(AXExpEq), this.slaveData.getR());
		
		//S_min <= B_i*x_i <= S_max
		for(int h=0; h < this.slaveData.getB().length; h++)
		{
			IloNumExpr[] BXExpLe = new IloNumExpr[this.slaveData.getB()[0].length];
			IloNumExpr[] BXExpGe = new IloNumExpr[this.slaveData.getB()[0].length];
			
			System.out.print("[");
			for(int f=0; f < this.slaveData.getB()[0].length; f++)
			{
				//NOTE: REmove this start
				if(f == this.slaveData.getB()[0].length - 1)
				{
					System.out.print(this.slaveData.getB()[h][f]);
				}
				else
				System.out.print(this.slaveData.getB()[h][f] + ",");
				//Note: remove this end
				
				BXExpLe[f] = cplex.prod(x_i[f],this.slaveData.getB()[h][f]);
				BXExpGe[f] = cplex.prod(x_i[f],this.slaveData.getB()[h][f]);
			}
			System.out.print("]");
			System.out.println();
			
			cplex.addLe(cplex.sum(BXExpLe), this.slaveData.getSmax()[h]);
			cplex.addGe(cplex.sum(BXExpGe), this.slaveData.getSmin()[h]);
		}
		
		//if(firstIteration)
			//cplex.exportModel("EV.lp");
		
		cplex.solve();
		
		System.out.println("Slave Output value: " + cplex.getObjValue() + "  CurrentEV: " + this.currentEVNo);
		System.out.println(cplex.getStatus());
		
		x_optimal = new double[x_i.length];
		
		for(int u=0; u< x_i.length; u++)
		{
			x_optimal[u] = cplex.getValues(x_i)[u];
		}
		
		System.out.println("PRINTING X_OPTIMAL VALUE");
		Utils.PrintArray(x_optimal);
		
		//Write the x_optimal to mat file
		Utils.SlaveXToMatFile(evFileName, x_optimal, conf);
		
		return cplex.getObjValue();
	}


}
