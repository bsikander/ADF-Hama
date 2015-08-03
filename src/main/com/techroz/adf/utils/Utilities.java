package main.com.techroz.adf.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

public class Utilities {
	public static final Log LOG = LogFactory.getLog(Utilities.class);
	/**
	 * This method takes size as input and returns an array of zeros
	 * @param size Size of array
	 * @return
	 */
	public static double[] getZeroArray(int size)
	{
		double[] zeroArray = new double[size];
		for(int i=0; i< size; i++)
		{
			zeroArray[i] = (double) 0;
		}
		
		return zeroArray;
	}
	
	/*
	 * This static method adds up 2 vectors
	 */
	public static double[] vectorAdd(double[] arr1, double[] arr2)
	{
		double[] sumArr = new double[arr1.length];
		
		for(int i=0; i < arr1.length; i++)
		{
			sumArr[i] = round(arr1[i] + arr2[i]);
		}
		
		return sumArr;
	}
	
	/*
	 * This static method rounds the double values to a specified decimal places
	 */
	public static double round(double value) {
//		int places = Constants.ROUND_PLACES;
//	    if (places < 0) throw new IllegalArgumentException();
//
//	    BigDecimal bd = new BigDecimal(value);
//	    bd = bd.setScale(places, RoundingMode.HALF_UP);
//	    return bd.doubleValue();
		return value;
	}
	
	/*
	 * This method calculates mean of a vector
	 */
	public static double[] calculateMean(double[] vector, int total)
	{	
		double[] mean = new double[vector.length];
		
		for(int i =0; i< vector.length; i++)
			mean[i] = round(vector[i]/(double)total);
		
		return mean;
	}
	
	public static double[] scalerMultiply(double[] arr, double multiplier)
	{	
		double[] newArr = new double[arr.length];
		for(int i=0; i < arr.length; i++)
		{
			newArr[i] = arr[i]*multiplier;
		}
		
		return newArr;
	}
	
	public static void PrintArray(double[] input)
	{		
		LOG.info("=====Priting Array=====");
		for(int i =0; i < input.length; i++)
		{		
			System.out.print(input[i] + " ");
		}
		LOG.info("=====END=====");
	}
	
	public static double[] getXOptimalFromJson(String dataString) throws JsonGenerationException, JsonMappingException, IOException {
		Map<String, double[]> data;
		data = BroadcastHelper.convertJsonToDictionary(dataString.toString());
		
		return data.get("xOptimal");
	}
	
	public static String getFinishedMessageObject() throws JsonGenerationException, JsonMappingException, IOException {
		Map<String, double[]> data = new HashMap<String, double[]>();
		return BroadcastHelper.convertDictionaryToJson(data);
	}
}