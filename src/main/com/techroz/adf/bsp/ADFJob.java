package main.com.techroz.adf.bsp;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.Path;
import org.apache.hama.bsp.BSP;
import org.apache.hama.bsp.BSPJob;
import org.apache.hama.bsp.FileInputFormat;
import org.apache.hama.bsp.TextInputFormat;
import org.apache.hama.HamaConfiguration;

import main.com.techroz.adf.admm.XUpdate;
import main.com.techroz.adf.utils.Constants;

import com.google.common.base.Preconditions;


public class ADFJob {
	public static final Log LOG = LogFactory.getLog(ADFJob.class);
	
	HamaConfiguration conf;
	public BSPJob job;
	
	public ADFJob() throws IOException {
		conf = new HamaConfiguration();
		job = new BSPJob(conf);
	}
	
	public void setMaxIteration(int maxIterations) {
		job.set(Constants.ADF_MAX_ITERATIONS, String.valueOf(maxIterations));
	}
	
	public void setInputPath(String inputPath) {
		job.set(Constants.ADF_INPUT_PATH, inputPath); //Save input path in configurations
		
		//set the job input path
		FileInputFormat.addInputPaths(job, inputPath);
		job.setInputFormat(TextInputFormat.class);
	}
	
	public void setOutputPath(String outputPath) {
		//Set the output path in configurations and also set the output path of the job
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
	
	public void setSolutionVectorSize(int size) {
		job.set(Constants.ADF_XOPTIMAL_SIZE, String.valueOf(size));
	}
	
	@SuppressWarnings("rawtypes")
	public void setADMMClass(Class<? extends BSP> cls) {
		job.setBspClass(cls);
		conf.setClass(Constants.ADF_ADMM_BSP_CLASS, cls, BSP.class);
	}
	
	public void setFunction1(Class<? extends XUpdate> cls) {
		conf.setClass(Constants.ADF_FUNCTION1, cls, XUpdate.class);
	}
	
	public void setFunction1(Class<? extends XUpdate> cls, String modelFilePath, String dataHeaders) {
		conf.setClass(Constants.ADF_FUNCTION1, cls, XUpdate.class);
		job.set(Constants.ADF_FUNCTION1_MODEL_PATH, modelFilePath);
		job.set(Constants.ADF_FUNCTION1_DATA_HEADER, dataHeaders);		
	}
	
	public void setFunction2(Class<? extends XUpdate> cls) {
		conf.setClass(Constants.ADF_FUNCTION2, cls, XUpdate.class);
	}
	
	public void setFunction2(Class<? extends XUpdate> cls, String modelFileName, String dataHeaders) {
		conf.setClass(Constants.ADF_FUNCTION2, cls, XUpdate.class);
		job.set(Constants.ADF_FUNCTION2_MODEL_PATH, modelFileName);
		job.set(Constants.ADF_FUNCTION2_DATA_HEADER, dataHeaders);
	}
	
	public boolean run() throws ClassNotFoundException, IOException, InterruptedException {
		//Create a Hama job here and call the waitForCompletion method
		
		Preconditions.checkArgument(
		        conf.get(Constants.ADF_MAX_ITERATIONS) != null,
		        "Please provide max iterations!");
		
		Preconditions.checkArgument(
		        conf.get(Constants.ADF_JOB_NAME) != null,
		        "Please provide a job name!");
		
		Preconditions.checkArgument(
		        conf.get(Constants.ADF_INPUT_PATH) != null,
		        "Please provide input path!");
		
		Preconditions.checkArgument(
		        conf.get(Constants.ADF_OUTPUT_PATH) != null,
		        "Please provide output path!");
		
		Preconditions.checkArgument(
		        conf.get(Constants.ADF_XOPTIMAL_SIZE) != null,
		        "Please provide solution vector size!");
		
		Preconditions.checkArgument(
		        conf.get(Constants.ADF_FUNCTION1) != null,
		        "Please provide function F!");
		
		Preconditions.checkArgument(
		        conf.get(Constants.ADF_FUNCTION2) != null,
		        "Please provide function G!");
		
		Preconditions.checkArgument(
				conf.get(Constants.ADF_ADMM_BSP_CLASS) != null,
		        "Please provide ADMM class!");
		
		//https://svn.apache.org/repos/asf/hama/trunk/graph/src/main/java/org/apache/hama/graph/GraphJob.java
		return job.waitForCompletion(true);
	}
}
