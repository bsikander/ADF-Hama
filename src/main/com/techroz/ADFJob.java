package main.com.techroz;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hama.bsp.BSP;
import org.apache.hama.bsp.BSPJob;
import org.apache.hama.bsp.FileInputFormat;
import org.apache.hama.bsp.TextInputFormat;
import org.apache.hama.HamaConfiguration;

import main.com.techroz.admm.ExchangeSolver.EVADMM.XUpdate;
import main.com.techroz.admm.Functions.CPLEXEVMasterFunction;
import main.com.techroz.admm.Functions.CPLEXEVSlaveFunction;
import main.com.techroz.utils.Constants;


public class ADFJob {
	
	HamaConfiguration conf;
	BSPJob job;
	
	public ADFJob() throws IOException {
		conf = new HamaConfiguration();
		job = new BSPJob(conf);
	}
	
	public void setMaxIteration(int maxIterations) {
		job.set(Constants.ADF_MAX_ITERATIONS, String.valueOf(maxIterations));
	}
	
	public void setInputPath(String inputPath) {
		job.set(Constants.ADF_INPUT_PATH, inputPath);
		
		FileInputFormat.addInputPaths(job, inputPath);
		job.setInputFormat(TextInputFormat.class);
	}
	
	public void setOutputPath(String outputPath) {
		job.set(Constants.ADF_OUTPUT_PATH, outputPath);
		job.setOutputPath(new Path(outputPath));
	}
	
	public void setNumBSPTask(int taskCount) {
		job.set(Constants.ADF_BSP_TASK, String.valueOf(taskCount));
		job.setNumBspTask(taskCount);
	}
	
	public void setJobName(String jobName) {
		job.set(Constants.ADF_JOB_NAME, jobName);
		job.setJobName(jobName);
	}
	
	public void setXOptimalSize(int size) {
		job.set(Constants.ADF_XOPTIMAL_SIZE, String.valueOf(size));
	}
	
	public void setDataHeader(String header) {
		job.set(Constants.ADF_DATA_HEADER, header);
	}
	
	@SuppressWarnings("rawtypes")
	public void setADMMSolverClass(Class<? extends BSP> cls) {
		job.setBspClass(cls);
	}
	
	public void setMasterXUpdate(Class<? extends XUpdate> cls) {
		conf.setClass(Constants.ADF_MASTER_FUNCTION, cls, XUpdate.class);
	}
	
	public void setMasterModelFile(String path) {
		job.set(Constants.ADF_MASTER_MODEL_PATH, path);
	}
	
	public void setSlaveModelFile(String path) {
		job.set(Constants.ADF_SLAVE_MODEL_PATH, path);
	}
	
	//TODO: BAD CONVENTION -- USER SHOULD NOT KNOW ABOUT XUPDATE- CHANGE IT TO IFUNTION OR SOMETHING
	public void setSlaveXUpdate(Class<? extends XUpdate> cls) {
		conf.setClass(Constants.ADF_SLAVE_FUNCTION, cls, XUpdate.class);
	}
	
	public boolean run() throws ClassNotFoundException, IOException, InterruptedException {
		//Create a Hama job here and call the waitForCompletion method
		
		//TODO: Use this link to develop the job class
		//https://svn.apache.org/repos/asf/hama/trunk/graph/src/main/java/org/apache/hama/graph/GraphJob.java
		return job.waitForCompletion(true);
	}
	
	
	
	//1- Take mod files as input (Assume data is already in HDFS Later on we can change this)
	//2- Take data file as input (txt format)
	//3- Do master optimization
	//4- Maybe submit header
	//5- Consensus ADMM solver class
	//6- 

}
