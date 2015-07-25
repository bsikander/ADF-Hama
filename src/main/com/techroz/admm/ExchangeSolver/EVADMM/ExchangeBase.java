package main.com.techroz.admm.ExchangeSolver.EVADMM;

import main.com.techroz.admm.Functions.UUpdate;
import main.com.techroz.admm.Functions.XUpdate;

public abstract class ExchangeBase implements UUpdate, XUpdate {
	@Override
	public double[] getUUpdate() {
		return null;
		//No U Update is required in case of 
	}
	
//	@Override
//	public double[] getXUpdate(String input) {
//		return null;
//		
//	}
}
