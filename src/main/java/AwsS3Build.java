import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.AmazonServiceException;

import com.amazonaws.services.s3.transfer.Upload;
public class AwsS3Build {
 public static void main(String args[]) {
	 AWSCredentials credentials = new BasicAWSCredentials(
			  args[0],args[1]
			);
	 
	 AmazonS3 s3client = AmazonS3ClientBuilder
			  .standard()
			  .withCredentials(new AWSStaticCredentialsProvider(credentials))
			  .withRegion(Regions.US_EAST_2)
			  .build();
	 
	// s3client.putObject("www.checkmateinn.com","images/chatboticon.png",new File("C:\\Users\\moham\\Documents\\Innovation\\images\\chatboticon.png"));
	 
	 TransferManager xfer_mgr = TransferManagerBuilder.standard().build();
	 try {
		 final long startTime = System.nanoTime();
		
	     MultipleFileUpload xfer = xfer_mgr.uploadDirectory("www.checkmateinn.com",
	             "", new File("C:\\Users\\moham\\Documents\\Innovation"), true);
	     // loop with Transfer.isDone()
	     XferMgrProgress.showTransferProgress(xfer);
	     // or block with Transfer.waitForCompletion()
	     XferMgrProgress.waitForCompletion(xfer);
	     final long duration = System.nanoTime() - startTime;
	     ;
	  
	     Period period = new Period(TimeUnit.NANOSECONDS.toSeconds(duration) * 1000L);
	     String time = PeriodFormat.getDefault().print(period);
	     System.out.println("Completed");
		 System.out.println("Estimated uploaded time:"+time);
	 } catch (AmazonServiceException e) {
	     System.err.println(e.getErrorMessage());
	     System.exit(1);
	 }
	 xfer_mgr.shutdownNow();
	 
		/*
		 * List<Bucket> buckets = s3client.listBuckets(); for(Bucket bucket : buckets) {
		 * System.out.println(bucket.getName()); }
		 */
 }
}
