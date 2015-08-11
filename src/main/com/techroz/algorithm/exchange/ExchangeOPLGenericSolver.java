package main.com.techroz.algorithm.exchange;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import main.com.techroz.adf.admm.ContextBase;
import main.com.techroz.adf.admm.XUpdate;
import main.com.techroz.adf.utils.Constants;
import main.com.techroz.adf.utils.Utilities;
import main.com.techroz.example.evadmm.PriceBasedOptimizationFunction;
import ilog.concert.IloException;
import ilog.cplex.IloCplex;
import ilog.opl.IloCustomOplDataSource;
import ilog.opl.IloOplDataHandler;
import ilog.opl.IloOplErrorHandler;
import ilog.opl.IloOplFactory;
import ilog.opl.IloOplModel;
import ilog.opl.IloOplModelDefinition;
import ilog.opl.IloOplModelSource;
import ilog.opl.IloOplSettings;
import ilog.opl_core.cppimpl.IloNumMap;

public class ExchangeOPLGenericSolver implements XUpdate {
	public static final Log LOG = LogFactory.getLog(ExchangeOPLGenericSolver.class);
	
	Map<String, ParsedData> data;
	double[] x;
	double[] u;
	double[] xMean;
	
	@Override
	public double[] getXUpdate(String input, ContextBase context) {
		LOG.info("CPLEXEVMaster Function here");
		
		//Parse the data in input and add to the Map object.
		data = parse(input, context.getDataSchema());
		
		//Add x,u and xMean to the Map object also.
		x = context.getXOptimal();
		u = context.getU();
		xMean = context.getxMean();
		
		//Add the x, u and xMean in the main data object. So, at runtime whenever the OPL will call the fuction customread
		//x,u and xMean will be passed to the model including the other data objects
		data.put("xOld", new ParsedData("array", x));
		data.put("u", new ParsedData("array", u));
		data.put("xMean", new ParsedData("array", xMean));
		
		try {
			return optimize(context);
		} catch (FileNotFoundException e) {
			LOG.info(e.getMessage());
			e.printStackTrace();
		} catch (IloException e) {
			LOG.info(e.getMessage());
			e.printStackTrace();
		}
		
		LOG.error("Something went wrong ! Returning null value");
		return null;
	}
	
	public double[] optimize(ContextBase context) throws IloException, FileNotFoundException
	{	
		IloOplFactory.setDebugModeWarning(false); 
		IloOplFactory oplF = new IloOplFactory();
		IloOplErrorHandler errHandler = oplF.createOplErrorHandler();
		IloOplSettings settings = oplF.createOplSettings(errHandler);
		IloOplModelSource modelSource = oplF.createOplModelSource(context.getModelPath());
		IloOplModelDefinition def = oplF.createOplModelDefinition(modelSource, settings);
		IloCplex cplex = oplF.createCplex();
		IloOplModel opl = oplF.createOplModel(def, cplex);
		
		ExchangeDataSource datasource = new ExchangeDataSource(oplF, data);
		opl.addDataSource(datasource);
		
		opl.generate();
		
		double[] xOptimal = new double[u.length];
		
		if (cplex.solve()) {
			opl.postProcess();
			
			ilog.concert.IloNumMap x_n = opl.getElement("xOptimal").asNumMap();
		
			int count=0;
			for(int i = 0; i < x_n.getSize(); i++) {
				xOptimal[count] = x_n.get(i);
				count ++;
			}
			
			opl.printSolution(System.out);
			LOG.info("OBJECTIVE: " + opl.getCplex().getObjValue());
		}
		else {
			LOG.error("Optimal solution not found");
		}
		return xOptimal;
	}
	
	/*
	 * This function uses the input string and schema to create a Map<String,Object> object. This
	 * object can be used to automatically create a model of OPL language.
	 * @param input The input data passed by user as a text file
	 * @param schema The header passed by user
	 */
	private Map<String,ParsedData> parse(String input, String schema) {
		String[] splitData = input.split("\\|");
		String[] splitSchema = schema.split(",");
		//Map<String,ParsedData> data = new HashMap<String, ParsedData>();
		Map<String,ParsedData> data = new LinkedHashMap<String, ParsedData>();
		//price,re,D,xa_min,xa_max
		
		int index = 0; 
		for(String s : splitData) {
			if(s.contains("[")) { //s is an array
				data.put(splitSchema[index], new ParsedData("array", Utilities.getArray(s)));
			}
			else if(Utilities.checkDoubleArrayOccurrenceInInput(s)) { //s is a double array
				data.put(splitSchema[index], new ParsedData("doubleArray", Utilities.getDoubleArray(s)));
			}
			else if(s.contains(".")) { //data is double
				data.put(splitSchema[index], new ParsedData("double", Double.parseDouble(s)));
			}
			else { //data is int
				data.put(splitSchema[index], new ParsedData("int", Integer.parseInt(s)));
			}
			index++;
		}
		
		return data;
	}
}

class ExchangeDataSource extends IloCustomOplDataSource {
	Map<String,ParsedData> data;
	
	public ExchangeDataSource(IloOplFactory oplEnv, Map<String, ParsedData> data) {
		super(oplEnv);
		
		this.data = data;
	}
	
	@Override
	public void customRead() {
		IloOplDataHandler handler = getDataHandler();
		
//		handler.startElement("timeSlot");
//		handler.addIntItem(96);
//		handler.endElement();
		
		for (Entry<String, ParsedData> entry : data.entrySet()) {
			//if(entry.getKey().equals("timeSlot")) continue;
			handler.startElement(entry.getKey());
			
			if(entry.getValue().type.equals("int")) {
				handler.addIntItem((int) entry.getValue().data);
			}
			else if(entry.getValue().type.equals("double")) {
				handler.addNumItem((double) entry.getValue().data);
			}
			else if(entry.getValue().type.equals("array")) {
				setArray((double[]) entry.getValue().data, handler);
			}
			else if(entry.getValue().type.equals("doubleArray")) {
				//TODO: Add function here
			}
			
			handler.endElement();
		}	
	}
	
	private void setArray(double[] arr, IloOplDataHandler handler) {
    	handler.startIndexedArray();
    	for(int i = 0; i < arr.length; i++) {
    		handler.setItemIntIndex(i);
    		handler.addNumItem(arr[i]);
    	}
    	handler.endIndexedArray();
    }
	
}

class ParsedData {
	
	String type;
	Object data;
	
	ParsedData(String type, Object data) {
	
		this.type = type;
		this.data = data;
	}
}


//TODO: Remove this


//
//
//public class OPLSolverMaster implements XUpdate {
//	public static final Log LOG = LogFactory.getLog(OPLSolverMaster.class);
//	
//	private double[] price;
//	private double[] xa_min;
//	private double[] xa_max;
//	private Double rho;
//	@Override
//	public double[] getXUpdate(String input, ContextBase context, int inptutIndex) {
//		LOG.info("CPLEXEVMaster Function here");
//		context = (ExchangeMasterContext) context;
//		
//		parse(input);
//		
//		try {
//			return optimize(context);
//		} catch (FileNotFoundException e) {
//			LOG.info(e.getMessage());
//			e.printStackTrace();
//		} catch (IloException e) {
//			LOG.info(e.getMessage());
//			e.printStackTrace();
//		}
//		return null;
//		
//	}
//	
//	public double[] optimize(ContextBase context) throws IloException, FileNotFoundException
//	{
//		double[] x = ((ExchangeMasterContext) context).getXOptimal();
//		double[] u = ((ExchangeMasterContext) context).getU();
//		double[] xMean = ((ExchangeMasterContext) context).getxMean();
//		
//		IloOplFactory.setDebugModeWarning(false); //TODO: set to false. When running in prod 
//		IloOplFactory oplF = new IloOplFactory();
//		IloOplErrorHandler errHandler = oplF.createOplErrorHandler();
//		IloOplSettings settings = oplF.createOplSettings(errHandler);
//		IloOplModelSource modelSource = oplF.createOplModelSource("/home/bsikander/Documents/OPLProject/EVADMM/EVADMM.mod");
//		//IloOplModelDefinition def = oplF.createOplModelDefinition(modelSource,settings);
//		//IloOplModelDefinition def =oplF.createOplModelDefinition(modelSource, errHandler);
//		IloOplModelDefinition def = oplF.createOplModelDefinition(modelSource, settings);
//		IloCplex cplex = oplF.createCplex();
//		IloOplModel opl = oplF.createOplModel(def, cplex);
//		
//		MyParams datasource = new MyParams(oplF, 96, price, u, xMean, x, rho);
//		opl.addDataSource(datasource);
//		
//		opl.generate();
//		
//		double[] xOptimal = new double[u.length];
//		
//		if (cplex.solve()) {
//			opl.postProcess();
//			
//			IloNumMap x_n = opl.getElement("x_n").asNumMap();//.asNumSet();//.asSymbolSet();
//		
//			int count=0;
//			for(int i = 0; i < x_n.getSize(); i++) {
//				xOptimal[count] = x_n.get(i);
//				count ++;
//			}
//			
//			opl.printSolution(System.out);
//			System.out.println("OBJECTIVE: " + opl.getCplex().getObjValue
//					());
//			
//			System.out.println("xoptimal:");
//			for(double d : xOptimal) {
//				System.out.print(d + "  ");
//			}
//		}
//		else {
//			System.out.println("Out");
//		}
//		return xOptimal;
//	}
//	
//
//	private void parse(String input) {
//		String[] splitData = input.split("\\|");
//		
//		price = getArray(splitData[0]);
//		xa_min = getArray(splitData[3]);
//		xa_max = getArray(splitData[4]);
//		rho = Double.parseDouble(splitData[5]);
//	}
//	
//	private double[] getArray(String input) {
//		double[] arr;
//		//System.out.println("GetArray -> " + input);
//		input = input.substring(1,input.length() - 1); //remove [ ] symbols
//		String[] values = input.split(",");
//		arr = new double[values.length];
//		
//		int index = 0;
//		for(String s: values) {
//			arr[index] = Utilities.round(Double.parseDouble(s));
//			index ++;
//		}
//		
//		return arr;
//	}
//	
//	
//}
//
//class MyParams extends IloCustomOplDataSource
//{
//    int _timeSlot;
//    double[] _price;
//    double[] _u_k;
//    double[] _xMean;
//    double[] _xOld;
//    double _rho;
//    
//    MyParams(IloOplFactory oplF,int ntimeSlot,double[] nprice,double[] uk,double[] xMean, double[] xOld, double rho)
//    {
//        super(oplF);
//        _timeSlot = ntimeSlot;
//        _price = nprice;
//        _u_k = uk;
//        _xMean = xMean;
//        _xOld = xOld;
//        _rho = rho;
//}
//    
//	public void customRead()
//    {	
//    	IloOplDataHandler handler = getDataHandler();
//    	handler.startElement("timeSlot");
//    	handler.addIntItem(_timeSlot);
//    	handler.endElement();
//    	
//    	setArray(_price, "price", handler);
//    	setArray(_u_k, "u_k", handler);
//    	setArray(_xMean, "x_mean", handler);
//    	setArray(_xOld, "x_old", handler);
//    	
//    	handler.startElement("rho");
//    	handler.addNumItem(_rho);
//    	handler.endElement();
//    	    } 
//    
//    private void setArray(double[] arr, String name, IloOplDataHandler handler) {
//    	handler.startElement(name);
//    	handler.startIndexedArray();
//    	for(int i =0; i < _timeSlot; i++) {
//    		handler.setItemIntIndex(i);
//    		handler.addNumItem(arr[i]);
//    	}
//    	handler.endIndexedArray();
//    	handler.endElement();
//    }
//};


