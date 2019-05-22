package com.fission.demo.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.security.InvalidKeyException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fission.demo.filereaders.FileReader;
import com.fission.demo.filereaders.StramsChunks;
import com.microsoft.azure.storage.blob.BlockBlobURL;

@RequestMapping("uploader")
@RestController
public class DemoController {
	@PostMapping(value = "/uploadNioBuffer", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)

	public String uploadFileUsingNioBuffer(MultipartFile[] files) {
		StringBuffer br = new StringBuffer();
		long timeStamp = new Date().getTime();
		Arrays.asList(files).parallelStream().forEach((file) -> {
			try {
				InputStream i = file.getInputStream();

				long startTime = System.nanoTime();
				file.transferTo(new File("C:\\Users\\FL_LPT-213\\Desktop\\upload\\" +timeStamp+ file.getOriginalFilename()));
				long endTime = System.nanoTime();
				long elapsedTimeInMillis = TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS);
				br.append("Writing to Destination Total elapsed time: " + elapsedTimeInMillis + " ms \n");
				FileReader.javaNIOFileReaderMappedByteBuffer(
						"C:\\Users\\FL_LPT-213\\Desktop\\upload\\"+timeStamp + file.getOriginalFilename(), br);
			} catch (IOException e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
			}
		});
		return br.toString();
	}

	@PostMapping(value = "/uploadChannel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)

	public String uploadFile(MultipartFile[] files) {
		StringBuffer br = new StringBuffer();
		long timeStamp = new Date().getTime();
		Arrays.asList(files).parallelStream().forEach((file) -> {
			try {
				// InputStream i = file.getInputStream();

				long startTime = System.nanoTime();
				file.transferTo(new File("C:\\Users\\FL_LPT-213\\Desktop\\upload\\"+timeStamp + file.getOriginalFilename()));
				long endTime = System.nanoTime();
				long elapsedTimeInMillis = TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS);
				br.append("Writing to Destination Total elapsed time: " + elapsedTimeInMillis + " ms\n");
				StramsChunks.partsStram("C:\\Users\\FL_LPT-213\\Desktop\\upload\\" +timeStamp+ file.getOriginalFilename(), 10, br);

			} catch (IOException e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
			}
		});
		return br.toString();
	}

	public String uploadFileToLocation(MultipartFile file) throws IOException {
//		InputStream i = file.getInputStream();
		System.out.println("test");
		long startTime = System.nanoTime();

		file.transferTo(new File("C:\\Users\\FL_LPT-213\\Desktop\\upload\\" + file.getOriginalFilename()));
		long endTime = System.nanoTime();
		long elapsedTimeInMillis = TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS);
		System.out.println("Writing to Destination Total elapsed time: " + elapsedTimeInMillis + " ms");

		System.out.println(file.getOriginalFilename() + file.getSize());
//				FileReader.java7ioFileReader("C:\\Users\\FL_LPT-213\\Desktop\\upload\\"+file.getOriginalFilename());
//				FileReader.java8StramsFileReaderByteChannel("C:\\Users\\FL_LPT-213\\Desktop\\upload\\"+file.getOriginalFilename());

		// StramsChunks.partsStram("C:\\Users\\FL_LPT-213\\Desktop\\upload\\"+file.getOriginalFilename(),
		// 10);
		return file.getName();
	}

	@PostMapping(value = "/uploadToBlob", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)

	public String uploadFileToBlob(MultipartFile[] files) {
		StringBuffer br = new StringBuffer();
		long timeStamp = new Date().getTime();
		Arrays.asList(files).parallelStream().forEach((file) -> {
			try {
				InputStream i = file.getInputStream();

				long startTime = System.nanoTime();
				file.transferTo(new File("C:\\Users\\FL_LPT-213\\Desktop\\upload\\"+timeStamp + file.getOriginalFilename()));
				long endTime = System.nanoTime();
				long elapsedTimeInMillis = TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS);
				br.append("Writing to Destination Total elapsed time: " + elapsedTimeInMillis + " ms \n");
//		       BlockBlobURL blob = new BlockBlobURL();
				StramsChunks.uploadFileToBlob(
						new File("C:\\Users\\FL_LPT-213\\Desktop\\upload\\" +timeStamp+ file.getOriginalFilename()));
			} catch (IOException e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		return br.toString();
	}

	@PostMapping(value = "/uploadChannelChunksToBlob", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)

	public String uploadFileWithChucks(MultipartFile[] files) {
		StringBuffer br = new StringBuffer();
		long timeStamp = new Date().getTime();
		Arrays.asList(files).parallelStream().forEach((file) -> {
			try {
				// InputStream i = file.getInputStream();

				long startTime = System.nanoTime();
				file.transferTo(new File("C:\\Users\\FL_LPT-213\\Desktop\\upload\\"+timeStamp + file.getOriginalFilename()));
				long endTime = System.nanoTime();
				long elapsedTimeInMillis = TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS);
				br.append("Writing to Destination Total elapsed time: " + elapsedTimeInMillis + " ms\n");
				StramsChunks.uploadFileToBlobWithChucks(
						"C:\\Users\\FL_LPT-213\\Desktop\\upload\\"+timeStamp + file.getOriginalFilename(), 10, br);

			} catch (IOException e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
			}
		});
		return br.toString();
	}
}
