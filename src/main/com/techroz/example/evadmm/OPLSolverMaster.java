package main.com.techroz.example.evadmm;

import java.io.FileNotFoundException;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ilog.concert.IloException;
import ilog.concert.IloNumMap;
import ilog.concert.IloNumVar;
import ilog.concert.IloSymbolSet;
import ilog.concert.IloTupleSet;
import ilog.concert.cppimpl.IloNumSet;
import ilog.cplex.IloCplex;
import ilog.opl.IloCustomOplDataSource;
import ilog.opl.IloOplDataHandler;
import ilog.opl.IloOplElement;
import ilog.opl.IloOplErrorHandler;
import ilog.opl.IloOplFactory;
import ilog.opl.IloOplModel;
import ilog.opl.IloOplModelDefinition;
import ilog.opl.IloOplModelSource;
import ilog.opl.IloOplSettings;
import main.com.techroz.adf.admm.ContextBase;
import main.com.techroz.adf.admm.XUpdate;
import main.com.techroz.adf.utils.Utilities;
import main.com.techroz.algorithm.exchange.ExchangeMasterContext;

public class OPLSolverMaster implements XUpdate {
	public static final Log LOG = LogFactory.getLog(OPLSolverMaster.class);
	
	private double[] price;
	private double[] xa_min;
	private double[] xa_max;
	private Double rho;
	@Override
	public double[] getXUpdate(String input, ContextBase context, int inptutIndex) {
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
		double[] x = ((ExchangeMasterContext) context).getXOptimal();
		double[] u = ((ExchangeMasterContext) context).getU();
		double[] xMean = ((ExchangeMasterContext) context).getxMean();
		
		IloOplFactory.setDebugModeWarning(false); //TODO: set to false. When running in prod 
		IloOplFactory oplF = new IloOplFactory();
		IloOplErrorHandler errHandler = oplF.createOplErrorHandler();
		IloOplSettings settings = oplF.createOplSettings(errHandler);
		IloOplModelSource modelSource = oplF.createOplModelSource("/home/bsikander/Documents/OPLProject/EVADMM/EVADMM.mod");
		//IloOplModelDefinition def = oplF.createOplModelDefinition(modelSource,settings);
		//IloOplModelDefinition def =oplF.createOplModelDefinition(modelSource, errHandler);
		IloOplModelDefinition def = oplF.createOplModelDefinition(modelSource, settings);
		IloCplex cplex = oplF.createCplex();
		IloOplModel opl = oplF.createOplModel(def, cplex);
		
		MyParams datasource = new MyParams(oplF, 96, price, u, xMean, x, rho);
		opl.addDataSource(datasource);
		
		opl.generate();
		
		double[] xOptimal = new double[u.length];
		
		if (cplex.solve()) {
			opl.postProcess();
			
			IloNumMap x_n = opl.getElement("x_n").asNumMap();//.asNumSet();//.asSymbolSet();
		
			int count=0;
			for(int i = 0; i < x_n.getSize(); i++) {
				xOptimal[count] = x_n.get(i);
				count ++;
			}
			
			opl.printSolution(System.out);
			System.out.println("OBJECTIVE: " + opl.getCplex().getObjValue
					());
			
			System.out.println("xoptimal:");
			for(double d : xOptimal) {
				System.out.print(d + "  ");
			}
		}
		else {
			System.out.println("Out");
		}
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
			arr[index] = Utilities.round(Double.parseDouble(s));
			index ++;
		}
		
		return arr;
	}
	
	
}

class MyParams extends IloCustomOplDataSource
{
    int _timeSlot;
    double[] _price;
    double[] _u_k;
    double[] _xMean;
    double[] _xOld;
    double _rho;
    
    MyParams(IloOplFactory oplF,int ntimeSlot,double[] nprice,double[] uk,double[] xMean, double[] xOld, double rho)
    {
        super(oplF);
        _timeSlot = ntimeSlot;
        _price = nprice;
        _u_k = uk;
        _xMean = xMean;
        _xOld = xOld;
        _rho = rho;
}
    
	public void customRead()
    {	
    	IloOplDataHandler handler = getDataHandler();
    	handler.startElement("timeSlot");
    	handler.addIntItem(_timeSlot);
    	handler.endElement();
    	
    	setArray(_price, "price", handler);
    	setArray(_u_k, "u_k", handler);
    	setArray(_xMean, "x_mean", handler);
    	setArray(_xOld, "x_old", handler);
    	
    	handler.startElement("rho");
    	handler.addNumItem(_rho);
    	handler.endElement();
    	    } 
    
    private void setArray(double[] arr, String name, IloOplDataHandler handler) {
    	handler.startElement(name);
    	handler.startIndexedArray();
    	for(int i =0; i < _timeSlot; i++) {
    		handler.setItemIntIndex(i);
    		handler.addNumItem(arr[i]);
    	}
    	handler.endIndexedArray();
    	handler.endElement();
    }
};
