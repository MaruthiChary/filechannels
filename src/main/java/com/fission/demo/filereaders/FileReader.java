package com.fission.demo.filereaders;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class FileReader {
	public static void copyFileNIO(InputStream is) {
		
		
	}
	
	public static void java8StramsFileReaderByteChannel(String path) {
		 long startTime = System.nanoTime();
	        Path file = Paths.get(path);
	        try
	        {
	            //Java 8: Stream class
	            Stream<String> lines = Files.lines( file, StandardCharsets.UTF_8 );
	            
	            for( String line : (Iterable<String>) lines::iterator )
	            {
	               //System.out.println(line);
	            }
	        
	        } catch (IOException ioe){
	            ioe.printStackTrace();
	        }
	 
	        long endTime = System.nanoTime();
	        long elapsedTimeInMillis = TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS);
	        System.out.println("Total elapsed time: " + elapsedTimeInMillis + " ms");   
	}
	public static void java7ioFileReader(String path) {
		 long startTime = System.nanoTime();
	      
	        try
	        {
	            Path file = Paths.get(path);
	       
	            List<String> readAllLines = Files.readAllLines(file, StandardCharsets.UTF_8);
	            
	            for (String string : readAllLines) {
	            System.out.println(string);
	        }
	           
	        } catch (IOException ioe){
	            ioe.printStackTrace();
	        }
	 
	        long endTime = System.nanoTime();
	        long elapsedTimeInMillis = TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS);
	        System.out.println("Total elapsed time: " + elapsedTimeInMillis + " ms");
	    
	}
	public static void javaNIOFileReaderMappedByteBuffer(String path,StringBuffer br) {
		long startTime = System.nanoTime();
		 
        try {
            RandomAccessFile aFile = new RandomAccessFile(path, "r");
            FileChannel inChannel = aFile.getChannel();
            MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
            
            for (int i = 0; i < buffer.limit(); i++)
            {
                System.out.print((char) buffer.get()); //Print the content of file
            }
            aFile.close();
            
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
 
        long endTime = System.nanoTime();
        long elapsedTimeInMillis = TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS);
        br.append("\n").append("javaNIOFileReaderMappedByteBuffer Total elapsed time: " + elapsedTimeInMillis + " ms");
    
	}
}
