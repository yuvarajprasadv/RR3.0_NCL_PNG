package Rcvr_AAMQ;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.StandardCopyOption;
import java.nio.channels.FileChannel;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.json.simple.parser.*;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;


import org.apache.log4j.Logger;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Utils {
	
	static Logger log = LogMQ.monitor("Rcvr_AAMQ.Utils");
	private String[] fileLists;
	public static String jsonParser(String jsonString) {

		try {
				JSONParser parser = new JSONParser();
				parser.parse(jsonString);
				return jsonString;
			} 
		catch (ParseException e)
			{
			
				log.error(e.getMessage());
				return null;
			}

		}
	
	public static String IsValidJson(String jsonString) {

		try {
				JSONParser parser = new JSONParser();
				parser.parse(jsonString);
				return jsonString;
			} 
		catch (ParseException e)
			{
				log.error(e.getMessage());
				return null;
			}

		}
	
	public static String ConvertJsonToXml(String jsonString) throws JSONException{
		    JSONObject jsonObj = new JSONObject(jsonString);
		    return (XML.toString(jsonObj));
		  }


	public static String ConvertXmlToJson(java.lang.String xmlString)
	{
		org.json.JSONObject jsonObj = null;
		try {
		    jsonObj = XML.toJSONObject(xmlString);
		    return jsonObj.toString();
		} catch (JSONException e) {
		    log.error("JSON exception :" + e.getMessage());
		    return null;
		}
		
	}
	
	public static String ConvertPathToFileString(String file)
	{
		File filePath = new File(file);
		try {
			String filePathString = FileUtils.readFileToString(filePath, "UTF-8");
			return filePathString;
		} catch (IOException e) {
			log.error(e.getMessage());
			return null;
		}
		
	}
	
	public static String ConvertFileToString(File filePath)
	{
		String filePathString;
		try {
			filePathString = FileUtils.readFileToString(filePath, "UTF-8");
		} catch (IOException e) {
			log.error(e.getMessage());
			return null;
		}
		return filePathString;
	}

	public String RemoveForwardSlash(String pathString)
	{
		return pathString.replace("\\", "");
	}
	
	public String ReplaceStringWith(String pathString, String fromString, String toString)
	{
		return fromString.replace(fromString, toString);
	}
	
	
	public boolean FileExists(String pathString)
	{
		try
		{
			File file = new File(pathString);
			return file.exists();
		}
		catch (Exception Ex)
		{
			System.out.println(Ex.getMessage());
			return false;
		}
	}
	
	
	
	public String[] ArrayOfFileExists(String[] pathArray)
	{
		String rtString[] = new String[2];
		int iCount = 1; //ignore 0th index due to conjunction xmlpath 
		while(pathArray.length > iCount) 
		{
		    File file = new File(pathArray[iCount]);
		    if (!file.exists())
		    {
		    	rtString[0] = Integer.toString(iCount); 
		    	rtString[1] = Boolean.toString(file.exists());
		    	return rtString;
		    }
		    iCount++;
		}
		return new String[] { "0", "TRUE" };
	}
	
	
	public String ReadFileFromClassPath(String fileName)
	{
		StringBuffer sb = new StringBuffer();
	    String line;
		try
		{
	        InputStream stream = XmlUtiility.class.getResourceAsStream(fileName);
	       
	        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
	        while ((line = br.readLine()) != null) 
	        {
	          sb.append(line);
	        }
	        br.close();
	        stream.close();
		}
		catch (Exception ex)
		{
			log.error("Failed to read javaScript  " + ex.getMessage());
		}
        return sb.toString();
	}
	
	public String GetURLFromClassPath(String fileName){
		URL url;
		try
		{
		url = getClass().getResource(fileName);
		}
		catch (NullPointerException nEx)
		{
			throw nEx;
		}
        return (url.getFile());
	}
	 
	
	public String GetPathFromClassPath(String fileName)
	{
        URL url = Utils.class.getResource(fileName);
        return (url.getPath());
	}  
	
	public String GetPathFromResource(String fileName)
	{
		File file = new File("resources/"+fileName);
		String absolutePath = file.getAbsolutePath();
		return absolutePath;
	}
	
	public String ConvertToAbsolutePath(String fileName)
	{
		File file = new File(fileName);
		String absolutePath = file.getAbsolutePath();
		return absolutePath;
	}
	
	public ArrayList<String> GetMissingFonts(String[] appFonts, String[] docFonts)
	{	    
	    List<String> appFontList = Arrays.asList(appFonts);

	    ArrayList<String> missingFontList = new ArrayList<String>();
	    missingFontList.trimToSize();
	    
	    if (appFonts.length > 0 && docFonts.length > 0)
	    {

	    	for (int eachFontCnt=0; docFonts.length > eachFontCnt; eachFontCnt++)
	    	{
	    		if (!appFontList.contains(docFonts[eachFontCnt]))
	    		{    			
	    			missingFontList.add( docFonts[eachFontCnt]);
	    		}	
	    	}
	    }
		return missingFontList;
	}
	public List<String[]> ReadXLSXFile(String fileName, String sheetName) 
	{
		InputStream XlsxFileToRead = null;
		XSSFWorkbook workbook = null;
		try 
		{
			XlsxFileToRead = new FileInputStream(fileName);
			workbook = new XSSFWorkbook(XlsxFileToRead);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		XSSFSheet sheet = workbook.getSheet(sheetName);
		XSSFRow row;
		XSSFCell cell;

		Iterator rows = sheet.rowIterator();
		 List<String[]> arrList = new ArrayList<String[]>();
		while (rows.hasNext())
		{	
			row = (XSSFRow) rows.next();
			Iterator cells = row.cellIterator();
			ArrayList<String> arrStr = new ArrayList<String>();
			while (cells.hasNext()) 
			{
				cell = (XSSFCell) cells.next();
				if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING)
					arrStr.add(cell.getStringCellValue());
				else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC)
					arrStr.add(Integer.toString((int) cell.getNumericCellValue()));
				else if (cell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN)
					arrStr.add(Boolean.toString(cell.getBooleanCellValue()));
			}
		    arrList.add(arrStr.toArray(new String[0]));
			try 
			{
				XlsxFileToRead.close();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return arrList;
	}
	 
	
	 public String[] ReadFromExcel(String excelFile, boolean byRow, int fromRow, boolean byCol, int fromCol, boolean byBoth) 
	    {
	        boolean search = true;
	        try {
	        	Utils utls = new Utils();
	        	Workbook wrBook =  Workbook.getWorkbook(new File(utls.GetPathFromResource(excelFile)));
	            Sheet sheet = wrBook.getSheet(0);	            
	            ArrayList<String> arrStr = new ArrayList<String>();
	            while(search)
	            {
	            	try
	            	{
		            	Cell colRowVal = sheet.getCell(fromRow, fromCol);
		        		arrStr.add(colRowVal.getContents());
		        		if (byCol && !byRow)
		        			fromCol++;
		        		else if (byRow && !byCol)
		        			fromRow++;
	            	}
	            	catch (Exception ex)
	            	{
	            		search = false;
	            	} 
	            }
	            return arrStr.toArray(new String[0]);
	            
	             
	        } catch (BiffException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
			return null;
	         
	 
	    }

	   public long getFileSize(String filename) {
		      File file = new File(filename);
		      if (!file.exists() || !file.isFile()) 
		      {
		         System.out.println("File doesn\'t exist");
		         return -1;
		      }
		      return file.length();
		   }
	   
	   public static boolean IsFolderExists(String directoryPath) throws Exception
	   {
		   File folder = new File(directoryPath);
		   if(!folder.isDirectory())
			   return false;
		   return true;
	   }
	   
	   public String[] getFileFromDirectory(String directoryPath, String filterString) throws Exception
	   {
		   try
		   {
			   File folder = new File(directoryPath);
			   if(folder.isDirectory() && !folder.exists())
			   {
				   log.error("Folder path doesn't exists: " + directoryPath);
				//   ThrowException.CustomExit(new Exception("Folder path does not exists"), "Folder path does not exists : " + directoryPath);
				   ThrowException.CustomExitWithErrorMsgID(new Exception("Folder path does not exists "), directoryPath, "23");
			   }
			   File[] listOfFiles = folder.listFiles();
			   ArrayList<String> fileLists = new ArrayList<String>();
		       for (int iCount = 0; iCount < listOfFiles.length; iCount++) 
		       {
			         if (listOfFiles[iCount].isFile() && filterString.equals("None")) 
			         {
			        	 	System.out.println("File " + listOfFiles[iCount].getName());
			         }
			         else if (listOfFiles[iCount].isFile() && listOfFiles[iCount].toString().endsWith("." + filterString))
			         {
			        	 	fileLists.add(listOfFiles[iCount].getName().toString());
			         }
		       }
		       if(fileLists.size() == 0)
		       {
		    	   		log.error("Xml file doesn't exists in following path: " + directoryPath);
		    	   	//	ThrowException.CustomExit(new Exception("Xml file doesn't exists "), "Xml file doesn't exists in following path : " + directoryPath);
		    	   		ThrowException.CustomExitWithErrorMsgID(new Exception("Xml file doesn't exists "), directoryPath, "23");
		       }
		       String[] arrFiles = new String[fileLists.size()];
		       arrFiles = fileLists.toArray(arrFiles);
		       Arrays.sort(arrFiles);
			   
		       return arrFiles;
		   }
		   catch(Exception ex)
		   {
			   System.out.println(ex.getMessage());
			   return null;
		   }
	   }
	   
	   public String[] getPresetFileFromDirectory(String directoryPath, String filterString) throws Exception
	   {
		   try
		   {
			   File folder = new File(directoryPath);
			   if(folder.isDirectory() && !folder.exists())
			   {
				   log.error("Folder path doesn't exists: " + directoryPath);
				//   ThrowException.CustomExit(new Exception("Folder path does not exists"), "Folder path does not exists: "+directoryPath);
	    	   		   ThrowException.CustomExitWithErrorMsgID(new Exception("Folder path does not exists "), directoryPath, "23");
			   }
			   File[] listOfFiles = folder.listFiles();
			   ArrayList<String> fileLists = new ArrayList<String>();
		       for (int iCount = 0; iCount < listOfFiles.length; iCount++) 
		       {
			         if (listOfFiles[iCount].isFile() && filterString.equals("None")) 
			         {
			        	 	System.out.println("File " + listOfFiles[iCount].getName());
			         }
			         else if (listOfFiles[iCount].isFile() && listOfFiles[iCount].toString().endsWith("." + filterString))
			         {
			        	 	fileLists.add(listOfFiles[iCount].getName().toString());
			         }
		       }
		       if(fileLists.size() == 0)
		       {
		    	   		log.error("Pdf preset file doesn't exists in following path: " + directoryPath);
		    	   		MessageQueue.ERROR += "Pdf Preset file not exist" + "\n";
		       }
		       String[] arrFiles = new String[fileLists.size()];
		       arrFiles = fileLists.toArray(arrFiles);
		       Arrays.sort(arrFiles);
			   
		       return arrFiles;
		   }
		   catch(Exception ex)
		   {
			   System.out.println(ex.getMessage());
			   return null;
		   }
	   }
	   
	   
	   
	   public boolean CreateNewDirectory(String newFolderPath, boolean deleteExistDirectory) throws IOException
	   {
		   boolean success;
		   File dirPath = new File(newFolderPath);
		   if(dirPath.exists() && deleteExistDirectory)
			   DeleteDirectory(newFolderPath);
		   success = (new File(newFolderPath)).mkdirs();
		   return success;
	   }
	   
	   public void DeleteDirectory(String directoryPath) throws IOException
	   {
		   File dirPath = new File(directoryPath);
		   if(dirPath.exists())
		   {
			   FileUtils.deleteDirectory(dirPath);
		   }
				   
	   }
	   
	   
	    public static void XmlMultiFileExists(String xmlPathArray) throws Exception
		{
	    		Utils utls = new Utils();
	    		String xmlPath = "";

				String[] xmlFilesPath = xmlPathArray.split(",");
				for (int eachXmlCount = 0; eachXmlCount < xmlFilesPath.length; eachXmlCount++)
				{
					xmlPath = xmlFilesPath[eachXmlCount].split("~")[0];
					if(!utls.FileExists(xmlPath))
					{
						log.error("File path doesn't exists: " + xmlPath);
					//	ThrowException.CustomExit(new Exception("File Path or File does not exists "), "File path or File not exist: " + xmlPath);
		    	   		    ThrowException.CustomExitWithErrorMsgID(new Exception("File Path or File does not exists "), xmlPath, "23");
					}
				}
		}
	    
	    public String GetFileNameFromPath(String filePath)
	    {
		    	try
		    	{
			    	File fileObject = new File(filePath);
			    	return fileObject.getName();
		    	}
		    	catch(Exception ex)
		    	{
		    		return null;
		    	}

	    }
	    
	    public String GetParentPath(String filePath)
	    {
		    	try
		    	{
			    	File fileObject = new File(filePath);
			    	return fileObject.getParent();
		    	}
		    	catch(Exception ex)
		    	{
		    		return null;
		    	}

	    }
	   
	    public String CopyFileFromSourceToDestination(String fromSourceFile) throws Exception
	    {
	    		Utils utls = new Utils();
	    		int index = fromSourceFile.lastIndexOf("/");
	    		String fileName = fromSourceFile.substring(index, fromSourceFile.length());
	    		index = fromSourceFile.lastIndexOf("050_Production_Art");
	    		String SourceFile = fromSourceFile.substring(0, index) + "050_Production_Art/052_PA_Working_Files/Master Templates" + fileName;
	    		if(!utls.FileExists(SourceFile))
				{
					log.error("File path doesn't exists: " + SourceFile);
				//	ThrowException.CustomExit(new Exception("File Path or File does not exists "), "File path or File not exist: " + SourceFile);
					ThrowException.CustomExitWithErrorMsgID(new Exception("File Path or File does not exists "), SourceFile, "23");
					
				}
		    	try
		    	{
	    	        Path source = Paths.get(SourceFile);
	    	        Path destination = Paths.get(fromSourceFile);
	    	        Path retString = Files.copy(source, destination);
	    	        return retString.toString();
		    	}
		    	catch (java.nio.file.FileAlreadyExistsException fileAlreadyExists)
		    	{
		    		boolean fileRenamed = RenameFileIfExists(fileAlreadyExists.getMessage().toString());
		    		if(fileRenamed)
		    			CopyFileFromSourceToDestination(fromSourceFile);
		    		return "exists";
		    	}

	    }
	    public String CopyFileFromSourceToDestination(String fromSourceFile, String ToDestinationFolder) throws Exception
	    {
	    		Utils utls = new Utils();
	    		int index = ToDestinationFolder.lastIndexOf("/");
	    		String fileName = ToDestinationFolder.substring(index, ToDestinationFolder.length());
	    		String destinationFolderpath = ToDestinationFolder.substring(0, index) + fileName;
	    		String SourceFile = fromSourceFile +"/"+ fileName;
	    		if(!utls.FileExists(SourceFile))
			{
	    				//RR file not exists
		    			Action.UpdateErrorStatusWithRemark("23", "File Path or File does not exists : " + SourceFile); //file not exists
		    			
					log.error("File path doesn't exists: " + SourceFile);
				//	ThrowException.CustomExit(new Exception("File Path or File does not exists "), "File path or File not exist: " + SourceFile);
					ThrowException.CustomExitWithErrorMsgID(new Exception("File Path or File does not exists "), SourceFile, "23");
					
			}
	    		
		    	try
		    	{
	    	        Path source = Paths.get(SourceFile);
	    	        Path destination = Paths.get(destinationFolderpath);
	    	        Path retString = Files.copy(source, destination);
	    	        return retString.toString();
		    	}
		    	catch (java.nio.file.FileAlreadyExistsException fileAlreadyExists)
		    	{
		    		Path deletExistFile = Paths.get(ToDestinationFolder);
		    		Files.deleteIfExists(deletExistFile);
		    		CopyFileFromSourceToDestination(fromSourceFile, ToDestinationFolder);
		    		return "exists";
		    	}
		    	catch(java.nio.file.AccessDeniedException fileAccessDenied)
		    	{
		    		//RR file access permission denied status
		    		Action.UpdateErrorStatusWithRemark("21", "File or Folder access denied: " +  ToDestinationFolder);
		    		return "access denied";
		    	}
		    	catch(java.nio.file.NoSuchFileException noSuchFile)
		    	{
		    		//RR no such file exception
		    		Action.UpdateErrorStatusWithRemark("23", "File or Folder not found: " +  fromSourceFile);
		    		return "no such file";
		    	}

	    }
	    
	    
	    public String CopyFileFromSourceToDestRaw(String fromSourceFile, String ToDestinationFolder) throws Exception
	    {
	  
	    		Utils utls = new Utils();
	    		int index = fromSourceFile.lastIndexOf("/");
	    		String fileName = fromSourceFile.substring(index, fromSourceFile.length());
	    		String destinationFolderpath = ToDestinationFolder + fileName;
	    		if(!utls.FileExists(fromSourceFile))
			{
	    				//RR file not exists
		    			Action.UpdateErrorStatusWithRemark("23", "File path doesn't exists: " + fromSourceFile);
					log.error("File path doesn't exists: " + fromSourceFile);
				//	ThrowException.CustomExit(new Exception("File Path or File does not exists "), "File path or File not exist: " + fromSourceFile);
					ThrowException.CustomExitWithErrorMsgID(new Exception("File Path or File does not exists "), fromSourceFile, "23");
					
			}
	    		
		    	try
		    	{
	    	        Path source = Paths.get(fromSourceFile);
	    	        Path destination = Paths.get(destinationFolderpath);
	    	        Path retString = Files.copy(source, destination);
	    	        return retString.toString();
		    	}
		    	catch (java.nio.file.FileAlreadyExistsException fileAlreadyExists)
		    	{
		    		Path deletExistFile = Paths.get(destinationFolderpath);
		    		Files.deleteIfExists(deletExistFile);
		    		CopyFileFromSourceToDestRaw(fromSourceFile, ToDestinationFolder);
		    		return "exists";
		    	}
		    	catch(java.nio.file.AccessDeniedException fileAccessDenied)
		    	{
		    		//RR file access permission denied status
		    		Action.UpdateErrorStatusWithRemark("21", "File or Folder access denied: " +  ToDestinationFolder);
		    		return "access denied";
		    	}
		    	catch(java.nio.file.NoSuchFileException noSuchFile)
		    	{
		    		//RR no such file exception
		    		Action.UpdateErrorStatusWithRemark("23", "File or Folder not found: " +  fromSourceFile);
		    		return "no such file";
		    	}

	    }
	    
	    public String MoveFileFromSourceToDestination(String fromSourceFile, String ToDestinationFolder) throws Exception
	    {
	    		Utils utls = new Utils();
	    		int index = fromSourceFile.lastIndexOf("/");
	    		String fileName = fromSourceFile.substring(index, fromSourceFile.length());
	    		String destinationFolderpath = ToDestinationFolder + fileName;

	    		if(!utls.FileExists(fromSourceFile))
			{
					log.error("File path doesn't exists: " + fromSourceFile);
				//	ThrowException.CustomExit(new Exception("File Path or File does not exists "), "File path or File not exist: " + fromSourceFile);
					ThrowException.CustomExitWithErrorMsgID(new Exception("File Path or File does not exists "), fromSourceFile, "23");
					
			}
	    		
		    	try
		    	{
	    	        Path source = Paths.get(fromSourceFile);
	    	        Path destination = Paths.get(destinationFolderpath);
	    	        Path retString = Files.move(source, destination);
	    	        return retString.toString();
		    	}
		    	catch (java.nio.file.FileAlreadyExistsException fileAlreadyExists)
		    	{
		    		Path deletExistFile = Paths.get(destinationFolderpath);
		    		Files.deleteIfExists(deletExistFile);
		    		MoveFileFromSourceToDestination(fromSourceFile, ToDestinationFolder);
		    		return "exists";
		    	}

	    }
	    
	    public boolean RenameFile(String filePathString, String fileRenameString) throws Exception
	    {
		    	try
		    	{
			    	while(FileExists(filePathString)) 
			    	{
			    		File file = new File(filePathString);
			    		String newFileNamePath = file.getParent() + "/" + fileRenameString;
			    		File newName = new File(newFileNamePath);
			    		if(FileExists(newName.getAbsolutePath()))
			    		{
			    			continue;
			    		}
			    		else
			    		{
			    			file.renameTo(newName);
			    			return true;
			    		}
			    	}
		    	}
		    	catch(Exception ex)
		    	{
		    		log.error("Issue with file renaming: " + filePathString);
			//	ThrowException.CustomExit(new Exception("Not able to rename file 'File may be accessed by others' "), "File not accessable : " + filePathString);
				ThrowException.CustomExitWithErrorMsgID(new Exception("File access permission denied "), filePathString, "21");
		    	}
			return false;
	    }
	   
	    public boolean RenameFileIfExists(String filePathString) throws Exception
	    {
		    	try
		    	{
			    	int num = 1;
			    	while(FileExists(filePathString)) 
			    	{
			    		File file = new File(filePathString);
			    		String newFileNamePath = file.getParent() + "/" + FilenameUtils.removeExtension(file.getName()) + "_" + (num++) + ".ai";
			    		File newName = new File(newFileNamePath);
			    		if(FileExists(newName.getAbsolutePath()))
			    		{
			    			continue;
			    		}
			    		else
			    		{
			    			file.renameTo(newName);
			    			return true;
			    		}
			    	}
		    	}
		    	catch(Exception ex)
		    	{
		    		log.error("Issue with file renaming: " + filePathString);
			//	ThrowException.CustomExit(new Exception("Not able to rename file 'File may be accessed by others' "), "File not accessable : " + filePathString);
				ThrowException.CustomExitWithErrorMsgID(new Exception("File access permission denied "), filePathString, "21");
		    	}
				return false;
	    }
	    
	    public String GetNewNameIfFileExists(String filePathString) throws Exception
	    {
		    	try
		    	{
			    	int num = 1;
			    	while(FileExists(filePathString)) 
			    	{
			    		File file = new File(filePathString);
			    		String newFileNamePath = file.getParent() + "/" + FilenameUtils.removeExtension(file.getName()) + "_" + (num++) + ".ai";
			    		File newName = new File(newFileNamePath);
			    		if(FileExists(newName.getAbsolutePath()))
			    		{
			    			continue;
			    		}
			    		else
			    		{
			    			return newName.toString();
			    		}
			    	}
		    	}
		    	catch(Exception ex)
		    	{
		    		log.error("Issue with file renaming: " + filePathString);
			//	ThrowException.CustomExit(new Exception("Not able to rename file 'File may be accessed by others' "), "File not accessable : " + filePathString);
				ThrowException.CustomExitWithErrorMsgID(new Exception("File access permission denied "), filePathString, "21");
		    	}
				return "";
	    }
	    
	    
	    
  
	   public static void main(String args[]) throws Exception
	   {
		   Utils utls = new Utils();
		   
		//  System.out.println( utls.RenameFileIfExists("//Users//yuvaraj//Downloads//PnG//102196907//401400544//050_Production_Art//91377726_002_Master.ai"));
		  System.out.println(utls.MoveFileFromSourceToDestination("//Users//yuvaraj//Downloads//PnG//102196907//401400544//050_Production_Art//91377726_002_Master-A.ai", "//Users//yuvaraj//Downloads//PnG//102196907//401400544//050_Production_Art//051_PA_Previous//"));
	   }
	 
}
