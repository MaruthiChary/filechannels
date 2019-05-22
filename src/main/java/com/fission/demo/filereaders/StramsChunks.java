package com.fission.demo.filereaders;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileChannel;
import java.security.InvalidKeyException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.microsoft.azure.storage.blob.BlockBlobURL;
import com.microsoft.azure.storage.blob.ContainerURL;
import com.microsoft.azure.storage.blob.PipelineOptions;
import com.microsoft.azure.storage.blob.ServiceURL;
import com.microsoft.azure.storage.blob.SharedKeyCredentials;
import com.microsoft.azure.storage.blob.StorageURL;
import com.microsoft.azure.storage.blob.TransferManager;
import com.microsoft.azure.storage.blob.models.ContainerCreateResponse;
import com.microsoft.localforwarder.library.inputs.contracts.Request;
import com.microsoft.rest.v2.RestException;

public class StramsChunks {
// public static void main(String args[]) {
//		 StramsChunks.partsStram("",10);
//	 }
	public static void partsStram(String path, int noOfChuncks, StringBuffer br) {
		try {
			long startTime = System.nanoTime();
//		 String path = "C:\\Users\\FL_LPT-213\\Desktop\\test\\test1GB.db";
//		 path= "C:\\Users\\FL_LPT-213\\Desktop\\test\\test3gb";
//		 path= "C:\\Users\\FL_LPT-213\\Desktop\\test\\test5gb";
//		 path =s;
			
			RandomAccessFile aFile = new RandomAccessFile(path, "r");
			FileChannel inChannel = aFile.getChannel();

			long size = inChannel.size();
			System.out.println(size);
			long labytes = size % noOfChuncks;
			long chunkSize = (size / noOfChuncks) + labytes;
			br.append("Chunk Size  --- ").append(chunkSize);
//	long checkSize = chunkSize;
//	 RandomAccessFile oFile = new RandomAccessFile("C:\\Users\\FL_LPT-213\\Desktop\\test\\test\\test3gb", "rw");
//	 FileChannel outChannel =  oFile.getChannel();

			for (int i = 0; i < noOfChuncks; i++) {

				RandomAccessFile oFile = new RandomAccessFile(
						"C:\\Users\\FL_LPT-213\\Desktop\\test\\test\\test" + i + ".part", "rw");
				FileChannel outChannel = oFile.getChannel();
				inChannel.transferTo(i * chunkSize, chunkSize, outChannel);
				outChannel.close();
			}
//	outChannel.close();

//	 inChannel.transferTo(chunkSize, outChannel);
//	 inChannel.transferTo(10485760+10485760+10485760, 10485760, outChannel);
			// inChannel.transferTo(, 10485760, outChannel);
			inChannel.close();

			long endTime = System.nanoTime();
			long elapsedTimeInMillis = TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS);
			br.append("Channels Total elapsed time: " + elapsedTimeInMillis + " ms \n");

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	static public void uploadFileToBlob(File sourceFile) throws IOException, InvalidKeyException {

		AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(sourceFile.toPath());
		final String uri = "http://localhost:8761/broadcastmsg";
	     
	    final RestTemplate restTemplate = new RestTemplate();
	     
	    HttpHeaders headers = new HttpHeaders();
//	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	    final HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
	     
	  
	    
		SharedKeyCredentials creds = new SharedKeyCredentials("tempstoragevalidation",
				"UBshnVqMkSI45GuNwD2P+m1tGlK3b7byinMnlJqr/8uY1alhLgxYEOwouKGEijjVXPss19e6ytiipPJNbHK+lA==" + "");
		// We are using a default pipeline here, you can learn more about it at
		// https://github.com/Azure/azure-storage-java/wiki/Azure-Storage-Java-V10-Overview
		final ServiceURL serviceURL = new ServiceURL(
				new URL("https://" + "tempstoragevalidation" + ".blob.core.windows.net"),
				StorageURL.createPipeline(creds, new PipelineOptions()));

		// Let's create a container using a blocking call to Azure Storage
		// If container exists, we'll catch and continue
		ContainerURL containerURL = serviceURL.createContainerURL("test");

		try {
			ContainerCreateResponse response = containerURL.create(null, null, null).blockingGet();
			System.out.println("Container Create Response was " + response.statusCode());
		} catch (RestException e) {
			if (e instanceof RestException && ((RestException) e).response().statusCode() != 409) {
				throw e;
			} else {
				System.out.println("test container already exists, resuming...");
			}
		}

		// Create a BlockBlobURL to run operations on Blobs
		final BlockBlobURL blobURL = containerURL.createBlockBlobURL(sourceFile.getName());
		restTemplate.postForObject(uri,"upload started to blob", String.class);

		// Uploading a file to the blobURL using the high-level methods available in
		// TransferManager class
		// Alternatively call the Upload/StageBlock low-level methods from BlockBlobURL
		// type
		final long startTime = System.nanoTime();
//    System.out.println("upload started ----   "+);
		TransferManager.uploadFileToBlockBlob(fileChannel, blobURL, 8 * 1024 * 1024, null).subscribe(response -> {
			System.out.println("Completed upload request.");
			  String result = restTemplate.postForObject(uri,"upload Successfull", String.class);
			     
			    System.out.println(result);
			long endTime = System.nanoTime();
			long elapsedTimeInMillis = TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS);
			System.out.println("Channels Total elapsed time: " + elapsedTimeInMillis + " ms \n");
			System.out.println(response.response().statusCode());
		},res->{
			restTemplate.postForObject(uri,res, String.class);
		});
	}

	static public void uploadFileToBlobWithChucks(File sourceFile) throws IOException, InvalidKeyException {

		AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(sourceFile.toPath());
	
		SharedKeyCredentials creds = new SharedKeyCredentials("tempstoragevalidation",
				"UBshnVqMkSI45GuNwD2P+m1tGlK3b7byinMnlJqr/8uY1alhLgxYEOwouKGEijjVXPss19e6ytiipPJNbHK+lA==" + "");
		// We are using a default pipeline here, you can learn more about it at
		// https://github.com/Azure/azure-storage-java/wiki/Azure-Storage-Java-V10-Overview
		final ServiceURL serviceURL = new ServiceURL(
				new URL("https://" + "tempstoragevalidation" + ".blob.core.windows.net"),
				StorageURL.createPipeline(creds, new PipelineOptions()));

		// Let's create a container using a blocking call to Azure Storage
		// If container exists, we'll catch and continue
		ContainerURL containerURL = serviceURL.createContainerURL("test");

		try {
			ContainerCreateResponse response = containerURL.create(null, null, null).blockingGet();
			System.out.println("Container Create Response was " + response.statusCode());
		} catch (RestException e) {
			if (e instanceof RestException && ((RestException) e).response().statusCode() != 409) {
				throw e;
			} else {
				System.out.println("test container already exists, resuming...");
			}
		}

		// Create a BlockBlobURL to run operations on Blobs
		final BlockBlobURL blobURL = containerURL.createBlockBlobURL(sourceFile.getName());

		// Uploading a file to the blobURL using the high-level methods available in
		// TransferManager class
		// Alternatively call the Upload/StageBlock low-level methods from BlockBlobURL
		// type
		final long startTime = System.nanoTime();
	
//    System.out.println("upload started ----   "+);
		TransferManager.uploadFileToBlockBlob(fileChannel, blobURL, 8 * 1024 * 1024, null).subscribe(response -> {
			System.out.println("Completed upload request.");

			long endTime = System.nanoTime();
			long elapsedTimeInMillis = TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS);
			System.out.println("Channels Total elapsed time: " + elapsedTimeInMillis + " ms \n");
			System.out.println(response.response().statusCode());
		//	RestTemplate template = new RestTemplate();
			//RequestEntity<String> r = new RequestEntity<String>(RequestMethod.POST,"");
			 
		//	template.exchange("http://", ,, String.class, "");
		},res->{
			fileChannel.close();
			System.out.println("Error ");
			System.out.println(res);
		});
	}

	public static void uploadFileToBlobWithChucks(String path, int noOfChuncks, StringBuffer br) {
		try {
			long startTime = System.nanoTime();
//		 String path = "C:\\Users\\FL_LPT-213\\Desktop\\test\\test1GB.db";
//		 path= "C:\\Users\\FL_LPT-213\\Desktop\\test\\test3gb";
//		 path= "C:\\Users\\FL_LPT-213\\Desktop\\test\\test5gb";
//		 path =s;
			RandomAccessFile aFile = new RandomAccessFile(path, "r");
			FileChannel inChannel = aFile.getChannel();

			long size = inChannel.size();
			System.out.println(size);
			long labytes = size % noOfChuncks;
			long chunkSize = (size / noOfChuncks) + labytes;
			br.append("Chunk Size  --- ").append(chunkSize);
//	long checkSize = chunkSize;
//	 RandomAccessFile oFile = new RandomAccessFile("C:\\Users\\FL_LPT-213\\Desktop\\test\\test\\test3gb", "rw");
//	 FileChannel outChannel =  oFile.getChannel();

			SharedKeyCredentials creds = new SharedKeyCredentials("tempstoragevalidation",
					"UBshnVqMkSI45GuNwD2P+m1tGlK3b7byinMnlJqr/8uY1alhLgxYEOwouKGEijjVXPss19e6ytiipPJNbHK+lA==" + "");
			// We are using a default pipeline here, you can learn more about it at
			// https://github.com/Azure/azure-storage-java/wiki/Azure-Storage-Java-V10-Overview
			final ServiceURL serviceURL = new ServiceURL(
					new URL("https://" + "tempstoragevalidation" + ".blob.core.windows.net"),
					StorageURL.createPipeline(creds, new PipelineOptions()));

			// Let's create a container using a blocking call to Azure Storage
			// If container exists, we'll catch and continue
			ContainerURL containerURL = serviceURL.createContainerURL("test");

			try {
				ContainerCreateResponse response = containerURL.create(null, null, null).blockingGet();
				System.out.println("Container Create Response was " + response.statusCode());
			} catch (RestException e) {
				if (e instanceof RestException && ((RestException) e).response().statusCode() != 409) {
					throw e;
				} else {
					System.out.println("test container already exists, resuming...");
				}
			}
System.out.println("start time" + new Date());
			for (int i = 0; i < noOfChuncks; i++) {
long timeStamp = new Date().getTime();
				RandomAccessFile oFile = new RandomAccessFile(
						"C:\\Users\\FL_LPT-213\\Desktop\\test\\test\\test" + timeStamp + ".part", "rw");
				FileChannel outChannel = oFile.getChannel();
				inChannel.transferTo(i * chunkSize, chunkSize, outChannel);
				outChannel.close();
				File f = new File("C:\\Users\\FL_LPT-213\\Desktop\\test\\test\\test" + timeStamp + ".part");
				AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(f.toPath());
				final long startTe = System.nanoTime();
				final BlockBlobURL blobURL = containerURL.createBlockBlobURL(f.getName());

//	    System.out.println("upload started ----   "+);
				new Thread(()->{
				try {
					TransferManager.uploadFileToBlockBlob(fileChannel, blobURL, 8 * 1024 * 1024, null)
							.subscribe(response -> {
								fileChannel.close();
								System.out.println("Completed upload request.");

								long endTime = System.nanoTime();
								long elapsedTimeInMillis = TimeUnit.MILLISECONDS.convert((endTime - startTe),
										TimeUnit.NANOSECONDS);
								System.out.println("Channels Total elapsed time: " + elapsedTimeInMillis + " ms \n");
								System.out.println(response.response().statusCode());
							},res->{
								fileChannel.close();
								System.out.println("Error ");
								System.out.println(res);
							});
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}).start();
			}
//	outChannel.close();

//	 inChannel.transferTo(chunkSize, outChannel);
//	 inChannel.transferTo(10485760+10485760+10485760, 10485760, outChannel);
			// inChannel.transferTo(, 10485760, outChannel);
			inChannel.close();

			long endTime = System.nanoTime();
			long elapsedTimeInMillis = TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS);
			br.append("Channels Total elapsed time: " + elapsedTimeInMillis + " ms \n");

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}
}
