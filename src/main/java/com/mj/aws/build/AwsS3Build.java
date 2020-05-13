package com.mj.aws.build;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.TransferProgress;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;

import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.Transfer.TransferState;
public class AwsS3Build {
 public static void main(String args[]) {
		/*
		 * AWSCredentials credentials = new BasicAWSCredentials( args[0],args[1] );
		 * 
		 * System.out.println(args[0]); System.out.println(args[1]);
		 */
	 
	 
	 System.out.println("Connecting AWS S3 Bucket....");
	 BasicAWSCredentials creds = new BasicAWSCredentials("AKIA3YEWP2HAEQ3SMRX4", "rpYBU+TxVMTH/L05wo9D19hwEikxDNPXeAB3vGDA");
	 AmazonS3 s3client = AmazonS3ClientBuilder.standard()
		        .withCredentials(new AWSStaticCredentialsProvider(creds))
		        .withRegion(Regions.US_EAST_2)
		        .build();

	// s3client.putObject("www.checkmateinn.com","images/chatboticon.png",new File("C:\\Users\\moham\\Documents\\Innovation\\images\\chatboticon.png"));
	

	
	// TransferManager xfer_mgr = TransferManagerBuilder.defaultTransferManager();
	 TransferManager xfer_mgr = TransferManagerBuilder.standard()
             .withS3Client(s3client)
             .build();
	

	 try {
		 final long startTime = System.nanoTime();
		
	     MultipleFileUpload xfer = xfer_mgr.uploadDirectory("www.checkmateinn.com",
	             "", new File("C:\\Users\\moham\\Documents\\Innovation"), true);
	     // loop with Transfer.isDone()
	     showTransferProgress(xfer);
	     // or block with Transfer.waitForCompletion()
	   //  waitForCompletion(xfer);
	     final long duration = System.nanoTime() - startTime;
	    
	  
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
 
 public static void waitForCompletion(Transfer xfer) {
     // snippet-start:[s3.java1.s3_xfer_mgr_progress.wait_for_transfer]
     try {
         xfer.waitForCompletion();
     } catch (AmazonServiceException e) {
         System.err.println("Amazon service error: " + e.getMessage());
         System.exit(1);
     } catch (AmazonClientException e) {
         System.err.println("Amazon client error: " + e.getMessage());
         System.exit(1);
     } catch (InterruptedException e) {
         System.err.println("Transfer interrupted: " + e.getMessage());
         System.exit(1);
     }
     // snippet-end:[s3.java1.s3_xfer_mgr_progress.wait_for_transfer]
 }

 // Prints progress while waiting for the transfer to finish.
 public static void showTransferProgress(Transfer xfer) {
     // snippet-start:[s3.java1.s3_xfer_mgr_progress.poll]
     // print the transfer's human-readable description
   //  System.out.println(xfer.getDescription());
     // print an empty progress bar...
	 System.out.println("calculating progress percentage");
     printProgressBar(0.0);
     // update the progress bar while the xfer is ongoing.
     do {
         
         // Note: so_far and total aren't used, they're just for
         // documentation purposes.
         TransferProgress progress = xfer.getProgress();
         long so_far = progress.getBytesTransferred();
         long total = progress.getTotalBytesToTransfer();
         double pct = progress.getPercentTransferred();
         eraseProgressBar();
         printProgressBar(pct);
     } while (xfer.isDone() == false);
     // print the final state of the transfer.
     TransferState xfer_state = xfer.getState();
    
     // snippet-end:[s3.java1.s3_xfer_mgr_progress.poll]
 }

 // Prints progress of a multiple file upload while waiting for it to finish.
 public static void showMultiUploadProgress(MultipleFileUpload multi_upload) {
     // print the upload's human-readable description
    

     // snippet-start:[s3.java1.s3_xfer_mgr_progress.substranferes]
     Collection<? extends Upload> sub_xfers = new ArrayList<Upload>();
     sub_xfers = multi_upload.getSubTransfers();

     do {
        
         for (Upload u : sub_xfers) {
          
             if (u.isDone()) {
                 TransferState xfer_state = u.getState();
         //        System.out.println("  " + xfer_state);
             } else {
                 TransferProgress progress = u.getProgress();
                 double pct = progress.getPercentTransferred();
                 printProgressBar(pct);
                
             }
         }

         // wait a bit before the next update.
        
     } while (multi_upload.isDone() == false);
     // print the final state of the transfer.
     TransferState xfer_state = multi_upload.getState();
  
     // snippet-end:[s3.java1.s3_xfer_mgr_progress.substranferes]
 }

 // prints a simple text progressbar: [#####     ]
 public static void printProgressBar(double pct) {
     // if bar_size changes, then change erase_bar (in eraseProgressBar) to
     // match.
 	long total = 100;
     long startTime = System.currentTimeMillis();
     final int bar_size = 100;

     int amt_full = (int) pct;
     System.out.println("Upload started...");
     printProgress(startTime, total, amt_full);
    
		/*
		 * System.out.format("  [%s%s]", filled_bar.substring(0, amt_full),
		 * empty_bar.substring(0, bar_size - amt_full));
		 */
 }

 
 private static void printProgress(long startTime, long total, long current) {
 	
    
     StringBuilder string = new StringBuilder(140);   
     int percent = (int) (current * 100 / total);
     string
         .append('\r')
         .append(String.join("", Collections.nCopies(percent == 0 ? 2 : 2 - (int) (Math.log10(percent)), " ")))
         .append(String.format(" %d%% [", percent))
         .append(String.join("", Collections.nCopies(percent, "=")))
         .append('>')
         .append(String.join("", Collections.nCopies(100 - percent, " ")))
         .append(']')
         .append(String.join("", Collections.nCopies(current == 0 ? (int) (Math.log10(total)) : (int) (Math.log10(total)) - (int) (Math.log10(current)), " ")))
         .append(String.format(" %d/%d ", current, total));

     System.out.print(string);
     
     try {
         Thread.sleep(10000);
     } catch (InterruptedException e) {
         return;
     }
    
     
 }

 // erases the progress bar.
 public static void eraseProgressBar() {
     // erase_bar is bar_size (from printProgressBar) + 4 chars.
     final String erase_bar = "\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b";
   //  System.out.format(erase_bar);
 }

 public static void uploadFileWithListener(String file_path,
                                           String bucket_name, String key_prefix, boolean pause) {
  //   System.out.println("file: " + file_path +
         //    (pause ? " (pause)" : ""));

     String key_name = null;
     if (key_prefix != null) {
         key_name = key_prefix + '/' + file_path;
     } else {
         key_name = file_path;
     }

     // snippet-start:[s3.java1.s3_xfer_mgr_progress.progress_listener]
     File f = new File(file_path);
     TransferManager xfer_mgr = TransferManagerBuilder.standard().build();
     try {
         Upload u = xfer_mgr.upload(bucket_name, key_name, f);
         // print an empty progress bar...
         printProgressBar(0.0);
         u.addProgressListener(new ProgressListener() {
             public void progressChanged(ProgressEvent e) {
                 double pct = e.getBytesTransferred() * 100.0 / e.getBytes();
                 eraseProgressBar();
                 printProgressBar(pct);
             }
         });
         // block with Transfer.waitForCompletion()
         waitForCompletion(u);
         // print the final state of the transfer.
         TransferState xfer_state = u.getState();
        // System.out.print(": " + xfer_state);
     } catch (AmazonServiceException e) {
         System.err.println(e.getErrorMessage());
         System.exit(1);
     }
     xfer_mgr.shutdownNow();
     // snippet-end:[s3.java1.s3_xfer_mgr_progress.progress_listener]
 }

 public static void uploadDirWithSubprogress(String dir_path,
                                             String bucket_name, String key_prefix, boolean recursive,
                                             boolean pause) {
   //  System.out.println("directory: " + dir_path + (recursive ?
        //     " (recursive)" : "") + (pause ? " (pause)" : ""));

     TransferManager xfer_mgr = new TransferManager();
     try {
         MultipleFileUpload multi_upload = xfer_mgr.uploadDirectory(
                 bucket_name, key_prefix, new File(dir_path), recursive);
         // loop with Transfer.isDone()
         showMultiUploadProgress(multi_upload);
         // or block with Transfer.waitForCompletion()
         waitForCompletion(multi_upload);
     } catch (AmazonServiceException e) {
         System.err.println(e.getErrorMessage());
         System.exit(1);
     }
     xfer_mgr.shutdownNow();
 }


}
