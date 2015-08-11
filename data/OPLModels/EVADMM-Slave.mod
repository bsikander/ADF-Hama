/*********************************************
 * OPL 12.6.1.0 Model
 * Author: bsikander
 * Creation Date: Jul 6, 2015 at 7:19:25 PM
 *********************************************/
//NOTE: USE execute section to get the size of arrays. Instead of hardcoding

int timeSlot = ...;
range R = 1..timeSlot;

range Bsize = 1..61;

float u[R] = ...;
float xMean[R] = ...;
float xOld[R] = ...;

float rho = ...;
int gamma = ...;
float alpha = ...;
float A[R] = ...;
//float d[R] = ...;
float R_value = ...;

float smin[Bsize] = ...;
float smax[Bsize] = ...;
float xi_max[R] = ...;
float xi_min[R] = ...;
float B[Bsize][R] = ...;

//Variable that we have to find with min and max values specified
dvar float xOptimal[i in R] in xi_min[i]..xi_max[i];  

minimize
  sum(i in R)
    (
    	(gamma * alpha * xOptimal[i] * xOptimal[i]) + 
    		( 
    			(rho/2) * 
    				(xOptimal[i] - xOld[i] + xMean[i] + u[i]) * 
    				(xOptimal[i] - xOld[i] + xMean[i] + u[i])
    		)
    );

subject to {
//	( sum(i in R) x_i[i] * A[i] ) == R_value;
//	   
//	forall(i in Bsize) {
//	  ( sum(j in R) x_i[j] * B[i][j] ) <= smax[i];
//	  ( sum(j in R) x_i[j] * B[i][j] ) >= smin[i];
//	}		  
	 
	sum(i in R) 
		xOptimal[i] * A[i]  == R_value;
	   
	forall(i in Bsize)
	  	sum(j in R) 
	  		xOptimal[j] * B[i][j] <= smax[i];
	  	
	forall(i in Bsize)
	  sum(j in R)
	    	xOptimal[j] * B[i][j]  >= smin[i];         
}