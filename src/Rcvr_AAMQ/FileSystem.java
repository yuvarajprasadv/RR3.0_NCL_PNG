package Rcvr_AAMQ;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;


public class FileSystem 
{
	static Logger log = LogMQ.monitor("Rcvr_AAMQ.FileSystem");
	Utils utl = new Utils();

	public long GetFileSize(String filename) 
	{
	  File file = new File(filename);
	  if (!file.exists() || !file.isFile()) 
	  {
		 log.error("File does not exist");
	     System.out.println("File doesn\'t exist");
		 return -1;
	  }
	return file.length();
	}
	   
	public boolean CreateFile(String prvString)
	{
	   List<String> lines = Arrays.asList("");
	   Path file = Paths.get(utl.ConvertToAbsolutePath("/Applications/Adobe Illustrator "+MessageQueue.VERSION+"/Plug-ins.localized/Sgk/Configuration/"+prvString));
	   try 
	   {
		Files.write(file, lines, Charset.forName("UTF-8"));
	   } 
	   catch (IOException e) 
	   {
		   log.error("Exception on creating file");
			e.printStackTrace();
	   }
	   return false;
	}
	   
	   
	public void AppendFileString(String fileMessage)
	{
	    try 
	    { 
	    		Files.write(Paths.get(utl.ConvertToAbsolutePath("/Applications/Adobe Illustrator "+MessageQueue.VERSION+"/Plug-ins.localized/Sgk/Configuration/Report.txt")), fileMessage.getBytes(), StandardOpenOption.APPEND);
	    		MessageQueue.STATUS = false;
	    }
	    catch (IOException e) 
	    {
	    	//exception handling left as an exercise for the reader
		}
	}
	   
	   
	public String ReadFileReport(String fileName)
	{
		String errorRepMsg = "";
		try
		{
			Path file = Paths.get(utl.ConvertToAbsolutePath("/Applications/Adobe Illustrator "+MessageQueue.VERSION+"/Plug-ins.localized/Sgk/Configuration/"+fileName));
			List<String> reportMsg = Files.readAllLines(file, StandardCharsets.UTF_8);
			for( String name : reportMsg )
				errorRepMsg += name + "\n";
			return errorRepMsg;
		}
		catch(Exception ex)
		{
			log.error("Error on file reading :" + ex.getMessage() );
			System.out.println(ex.getMessage());
		}
		return null;
   }
	   
   public String ReadFile(String filePath)
   {
	   String errorRepMsg = "";
	   try
	   {
		   Path file = Paths.get(utl.ConvertToAbsolutePath(filePath));
		   List<String> reportMsg = Files.readAllLines(file, StandardCharsets.UTF_8);
		   for( String name : reportMsg )
			   errorRepMsg += name + "\n";
		   return errorRepMsg;
	   }
	   catch(Exception ex)
	   {
		   log.error("Error on file reading:"+ ex.getMessage());
		   System.out.println("err"+ ex.getMessage());
	   }  
	   return null;
   }
	   
   public static void main(String[] args)
   {
	   FileSystem fls = new FileSystem();	
	   String errorMsg = fls.ReadFileReport("error.txt");
	   if(errorMsg.contains("\n") && errorMsg.length() != 1)
			System.out.println(errorMsg);
   }  
}
