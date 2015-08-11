/*********************************************
 * OPL 12.6.1.0 Model
 * Author: bsikander
 * Creation Date: Jul 6, 2015 at 5:29:27 PM
 *********************************************/
int timeSlot = ...;
range R = 0..timeSlot-1;

float price[R] = ...;
float u[R] = ...;
float xMean[R] = ...;
float xOld[R] = ...;
float rho = ...;

//Variable that we have to find with min and max values specified
dvar float xOptimal[R] in -60..100000;  

minimize
  sum(i in R)
    (
    	(-1 * price[i] * xOptimal[i]) + 
    		( 
    			(rho/2) * 
    				(xOptimal[i] - xOld[i] + xMean[i] + u[i]) * 
    				(xOptimal[i] - xOld[i] + xMean[i] + u[i])
    		)
    );
