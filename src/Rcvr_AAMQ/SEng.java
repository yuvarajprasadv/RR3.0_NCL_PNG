package Rcvr_AAMQ;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.script.ScriptEngine; 
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngineFactory;
import org.apache.log4j.Logger;

public class SEng{
	
	static Logger log = LogMQ.monitor("Rcvr_AAMQ.SEng");
	static String timeOutSec = "600";
	 
	public static  String ExecuteAppleScript(String appleString) throws Exception
	{
		
		try
		{
			ScriptEngineManager mgr = new ScriptEngineManager();
			List<ScriptEngineFactory> factories =
		            mgr.getEngineFactories();
			 for (ScriptEngineFactory factory : factories) 
			 {
			        List<String> extensions = factory.getExtensions(); 
		            for (String ext : extensions) 
		            { 
		                mgr.registerEngineExtension(ext, factory); 
		            }
			        
		            List<String> mimes = factory.getMimeTypes(); 
		            for (String mime : mimes) 
		            { 
		            		mgr.registerEngineExtension(mime, factory); 
		            }
			 }
	        ScriptEngine engine = mgr.getEngineByName("AppleScriptEngine");
	        String result = null;
	        result = (engine.eval(appleString)).toString();
	        return result;
		}
		catch(Exception ex)
		{
		//	log.error("Error on running script enginge: " + ex.getMessage());
			return ex.getMessage();
		}
		
        
	}
	
	 public static  void CallAdobeIllustrator() throws Exception 
	 {
		 try
		 {
		 	String scriptString = "tell application "+ '"' + "Applications:Adobe Illustrator "+ MessageQueue.VERSION +":Adobe Illustrator.app" +'"' + " \n with timeout of "+ timeOutSec +" seconds \n"
		 			+ "activate \n"
		 			+ "end timeout \n"
		 			+ "end tell \n "
		 			+ "return application";
		 	ExecuteAppleScript(scriptString);
		 }
		 catch(Exception ex)
		 {
			 log.error("Issue on launching illustrator " + ex.getMessage());
			 log.info("Error Status updated as 14 - Roadrunner exits on error");
			 ThrowException.CatchExceptionWithErrorMsgId(new Exception("Illustrator"), "Roadrunner exits on error", "14");
		 }
	 }
	 public static String GetApplicationFonts() throws Exception
	 {
		 Utils utils = new Utils();
		 String pathString = utils.GetPathFromResource("AppFonts.js");
		 String scriptString = "tell application "+ '"' +"Applications:Adobe Illustrator "+ MessageQueue.VERSION +":Adobe Illustrator.app"+'"' +"\n with timeout of "+ timeOutSec +" seconds \n"
			+ "do javascript (file "+'"'+pathString+'"'+") \n"
			+ "end timeout \n"
			+ "end tell";
		 return ExecuteAppleScript(scriptString);
	 }
	 	 
	 public static void OpenDocument(String arryStr) throws Exception
	 {

		 Utils utils = new Utils();
		 String pathString = utils.GetPathFromResource("PreDocument.js");
		 String scriptString = "tell application "+ '"' +"Applications:Adobe Illustrator "+ MessageQueue.VERSION +":Adobe Illustrator.app"+'"' +"\n with timeout of "+ timeOutSec +" seconds \n"
			+ "do javascript (file "+'"'+pathString+'"'+")  with arguments {"+ '"'+arryStr+'"' +"} \n"
			+ "end timeout \n"
			+ "end tell";
		 ExecuteAppleScript(scriptString);
	 }
	 
	 
	 public static String GetDocumentFonts() throws Exception
	 {
		 Utils utils = new Utils();
		 String pathString = utils.GetPathFromResource("DocumentFonts.js");
		 String scriptString = "tell application "+ '"' +"Applications:Adobe Illustrator "+ MessageQueue.VERSION +":Adobe Illustrator.app"+'"' +"\n with timeout of "+ timeOutSec +" seconds \n"
			+ "do javascript (file "+'"'+pathString+'"'+") \n"
			+ "end timeout \n"
			+ "end tell";
		 return ExecuteAppleScript(scriptString);
	 }
	 
	 public static String GetDocumentFiles() throws Exception
	 {   
		 Utils utils = new Utils();
		 String pathString = utils.GetPathFromResource("DocumentFiles.js");
		 String scriptString = "tell application "+ '"' +"Applications:Adobe Illustrator "+ MessageQueue.VERSION +":Adobe Illustrator.app"+'"' +"\n with timeout of "+ timeOutSec +" seconds \n"
			+ "do javascript (file "+'"'+pathString+'"'+") \n"
			+ "end timeout \n"
			+ "end tell";
		 return ExecuteAppleScript(scriptString);
	 }
	 
	 public static void DocumentPreProcess() throws Exception
	 {
		 Utils utils = new Utils();
		 String pathString = utils.GetPathFromResource("FontMissing.js");
		 String scriptString = "tell application "+ '"' +"Applications:Adobe Illustrator "+ MessageQueue.VERSION +":Adobe Illustrator.app"+ '"' +"\n with timeout of "+ timeOutSec +" seconds \n"
			+ "do javascript (file "+'"'+pathString+'"'+") \n"
			+ "end timeout \n"
			+ "end tell";
		 String fontMissing = ExecuteAppleScript(scriptString);
		 if (fontMissing != "")
		 {
			 System.out.println(fontMissing);
			 log.error("Font Missing :" + fontMissing);
			 ThrowException.CatchException(new Exception("Font Missing"));
		 }
	 }

	 public static String CallTyphoonShadow(String arryStr[]) throws Exception
	 {
		 Utils utils = new Utils();
		 String pathString = utils.GetPathFromResource("MainDocument.js");
		 String scriptString = "tell application "+ '"' +"Applications:Adobe Illustrator "+ MessageQueue.VERSION +":Adobe Illustrator.app"+ '"' +"\n with timeout of "+ timeOutSec +" seconds \n"
			+ "do javascript (file "+'"'+pathString+'"'+")  with arguments {"+ '\"'+arryStr[0]+'\"' +"} \n"
			+ "end timeout \n"
			+ "end tell";
		 return ExecuteAppleScript(scriptString);
	 }
	 
	 public static void SetSwathColorFromTo(String swatchFrom, String swatchTo) throws Exception  {

		 String scriptString = "tell application "+ '"' +"Applications:Adobe Illustrator "+ MessageQueue.VERSION +":Adobe Illustrator.app"+'"' +"\n with timeout of "+ timeOutSec +" seconds \n"
			+ "tell application \"Adobe Illustrator\"'s document 1 \n"
			+ "try \n"
			+ "set spot "+ '"' + swatchFrom + '"' + "'s color to spot " +'"' + swatchTo + '"' +"'s color" + "\n"
			+ "delay \n"
			+ "end try \n"
			+ "end tell \n"
			+ "end timeout \n"
			+ "end tell";
		// System.out.println(scriptString);
		 ExecuteAppleScript(scriptString);
	 	}
	 
	 
	 public static void MergeSwatch(String arryStr[]) throws Exception  {

		 Utils utils = new Utils();
		 String pathString = utils.GetPathFromResource("ColorSpace.js");
		 String scriptString = "tell application "+ '"' +"Applications:Adobe Illustrator "+ MessageQueue.VERSION +":Adobe Illustrator.app"+'"' +"\n with timeout of "+ timeOutSec +" seconds \n"
			+ "do javascript (file "+'"'+pathString+'"'+")  with arguments {"+ '"'+arryStr[0]+'"' +", "+ '"'+arryStr[1]+'"' +"} \n"
			+ "end timeout \n"
			+ "end tell";
		 ExecuteAppleScript(scriptString);
	 	}
	 
	 public static String SwatchTest(String arryStr[]) throws Exception  
	 {

		 try
		 {
		 Utils utils = new Utils();
		 String pathString = utils.GetPathFromResource("SwatchTest.js");
		 String scriptString = "tell application "+ '"' +"Applications:Adobe Illustrator "+ MessageQueue.VERSION +":Adobe Illustrator.app"+'"' +"\n with timeout of "+ timeOutSec +" seconds \n"
			+ "do javascript (file "+'"'+pathString+'"'+")  with arguments {"+ '"'+arryStr[0]+'"' +", "+ '"'+arryStr[1]+'"' +"} \n"
			+ "end timeout \n"
			+ "end tell";
		 return ExecuteAppleScript(scriptString);
		 }
		 catch (Exception ex)
		 {
			 log.error("Failed to apply Swatch color merge");
			 return null;
		 }
		
	 }
	 
	 public static String SetLayerVisibleOff() throws Exception  
	 {
		 String[] arryStr1 = new String[1];
		 arryStr1[0] = "none";
		 Utils utils = new Utils();
		 String pathString = utils.GetPathFromResource("LayerOff.js");
		 String scriptString = "tell application "+ '"' +"Applications:Adobe Illustrator "+ MessageQueue.VERSION +":Adobe Illustrator.app"+'"' +"\n with timeout of "+ timeOutSec +" seconds \n"
			+ "do javascript (file "+'"'+pathString+'"'+")  with arguments {"+ '"'+arryStr1[0]+'"' +"} \n"
			+ "end timeout \n"
			+ "end tell";
		 return ExecuteAppleScript(scriptString);
	 }
	 
	 public static void ApplyStyleOverFlow(String arryStr[]) throws Exception
	 {
		 Utils utils = new Utils();
		 String pathString = utils.GetPathFromResource("ApplyStyleOverFlow.js");
		 String scriptString = "tell application "+ '"' +"Applications:Adobe Illustrator "+ MessageQueue.VERSION +":Adobe Illustrator.app"+'"' +"\n with timeout of "+ timeOutSec +" seconds \n"
			+ "do javascript (file "+'"'+pathString+'"'+")  with arguments {"+ '"'+arryStr[0]+'"' +", "+ '"'+arryStr[1]+'"' +", "+ '"'+arryStr[2]+'"'  +"} \n"
			+ "end timeout \n"
			+"end tell";
		 ExecuteAppleScript(scriptString);
	 }
	 
	 public static void ApplyElementStyle(String arg[]) throws Exception
     {
		 XmlUtiility xmlUtls = new XmlUtiility();
		 Utils utils = new Utils();
		 String[] arryStr1 = new String[1];
	     arryStr1[0] = "0";
         
		 String pathStrings = utils.GetPathFromResource("ApplyStyle.js");
		 String scriptStrings = "tell application "+ '"' +"Applications:Adobe Illustrator "+ MessageQueue.VERSION +":Adobe Illustrator.app"+'"' +"\n with timeout of "+ timeOutSec +" seconds \n"
			+ "do javascript (file "+'"'+pathStrings+'"'+")  with arguments {"+ '"'+arryStr1[0]+'"' +"} \n"
			+ "end timeout \n"
			+ "end tell";

		   String arrCopyElements = "";
		   arrCopyElements = ExecuteAppleScript(scriptStrings);
		   System.out.println("COPY EL  : "+ arrCopyElements + "\n");
		   for(String eachElement:(arrCopyElements.split(",")))
		   {  
			   String copyElement = eachElement.toString();
			   System.out.println("COPY EL  : "+ copyElement + "    "+arg[0]+  "\n");
			   String[] elements = copyElement.split("~");
			    for(String linkID:elements)
			    {
				    	if(linkID.length() > 3)
				    	{
				    		String stn = "";
				    		stn = xmlUtls.GS1XmlParseElement(arg[0], linkID.toString());
				    		System.out.println(linkID +"::"+stn);
				    		if (stn != null)
				    		{
				    			String pathString = utils.GetPathFromResource("ApplyStyle.js");
					   		 	String scriptString = "tell application "+ '"' +"Applications:Adobe Illustrator "+ MessageQueue.VERSION +":Adobe Illustrator.app"+'"' +"\n with timeout of "+ timeOutSec +" seconds \n"
					   			+ "do javascript (file "+'"'+pathString+'"'+")  with arguments {"+"1"+", "+ '"'+linkID+'"' +", "+ '"'+stn+'"' +", "+  '"'+copyElement+'"' +"} \n"
					   			+ "end timeout \n"
					   			+"end tell";
					   		 	System.out.println(ExecuteAppleScript(scriptString));
				    		}
				   		
				    	}
			    	
			    }
		  }
 
     }
	 
	 public static void PostDocumentProcess(String arryStr[]) throws Exception  {

		 Utils utils = new Utils();
		 String pathString = utils.GetPathFromResource("PostDocument.js");
		 String scriptString = "tell application "+ '"' +"Applications:Adobe Illustrator "+ MessageQueue.VERSION +":Adobe Illustrator.app"+'"' +"\n with timeout of "+ timeOutSec +" seconds \n"
			+ "do javascript (file "+'"'+pathString+'"'+")  with arguments {"+ '"'+arryStr[2]+'"' +"} \n"
			+ "end timeout \n"
			+ "end tell";
		 ExecuteAppleScript(scriptString);
	 	}
	 
	 public static void PostDocumentProcessForSingleJobFilename(String arryStr[]) throws Exception  {

		 Utils utils = new Utils();
		 String pathString = utils.GetPathFromResource("PostDocumentWithFileName.js");
		 String scriptString = "tell application "+ '"' +"Applications:Adobe Illustrator "+ MessageQueue.VERSION +":Adobe Illustrator.app"+'"' +"\n with timeout of "+ timeOutSec +" seconds \n"
			+ "do javascript (file "+'"'+pathString+'"'+")  with arguments {"+ '"'+arryStr[2]+'"' +", "+ '"'+arryStr[3]+'"' +"} \n"
			+ "end timeout \n"
			+ "end tell";
		 ExecuteAppleScript(scriptString);
	 	}
	 public static void PostDocumentProcessFor3DXML(String arryStr[]) throws Exception  {

		 Utils utils = new Utils();
		 String pathString = utils.GetPathFromResource("PostDocumentFor3D.js");
		 String scriptString = "tell application "+ '"' +"Applications:Adobe Illustrator "+ MessageQueue.VERSION +":Adobe Illustrator.app"+'"' +"\n with timeout of "+ timeOutSec +" seconds \n"
			+ "do javascript (file "+'"'+pathString+'"'+")  with arguments {"+ '"'+arryStr[2]+'"' +", "+ '"'+arryStr[3]+'"' +", "+ '"'+arryStr[1]+'"' +"} \n"
			+ "end timeout \n"
			+ "end tell";
		 ExecuteAppleScript(scriptString);
	 	}
	 
	 public static String PostDocumentMultipleProcess(String arryStr[]) throws Exception
	 {
		 Utils utils = new Utils();
		 String pathString = utils.GetPathFromResource("PostDocumentMultiple.js");
		 String scriptString = "tell application "+ '"' +"Applications:Adobe Illustrator "+ MessageQueue.VERSION +":Adobe Illustrator.app"+'"' +"\n with timeout of "+ timeOutSec +" seconds \n"
			+ "do javascript (file "+'"'+pathString+'"'+")  with arguments {"+ '"'+arryStr[2]+'"' +", "+ '"'+arryStr[3]+'"' +", "+ '"'+MessageQueue.VERSION+'"'  +"} \n"
			+ "end timeout \n"
			+"end tell";
		return ExecuteAppleScript(scriptString);
	 }
	 
	 public static String PostDocMultiPDFPreset(String arryStr[], String pdfPresetArr[]) throws Exception
	 {
		 Utils utils = new Utils();
		 String pathString = utils.GetPathFromResource("PostDocMultiPDFPreset.js");
		 String scriptString = "tell application "+ '"' +"Applications:Adobe Illustrator "+ MessageQueue.VERSION +":Adobe Illustrator.app"+'"' +"\n with timeout of "+ timeOutSec +" seconds \n"
			+ "do javascript (file "+'"'+pathString+'"'+")  with arguments {"+ '"'+arryStr[2]+'"' +", "+ '"'+arryStr[3]+'"' +", "+ '"'+pdfPresetArr[0]+'"' +", "+ '"'+pdfPresetArr[1]+'"' +"} \n"
			+ "end timeout \n"
			+"end tell";
		return ExecuteAppleScript(scriptString);
	 }
	 
	 public static void PostDocumentClose() throws Exception
	 {
		 Utils utils = new Utils();
		 String pathString = utils.GetPathFromResource("PostDocumentClose.js");
		 String scriptString = "tell application "+ '"' +"Applications:Adobe Illustrator "+ MessageQueue.VERSION +":Adobe Illustrator.app"+'"' +"\n with timeout of "+ timeOutSec +" seconds \n"
			+ "do javascript (file "+'"'+pathString+'"'+") \n"
			+ "end timeout \n"
			+ "end tell";
		 ExecuteAppleScript(scriptString);
	 }
	 
	 public static void PostDocumentProcessOnError(String arryStr[]) throws Exception  {

		 Utils utils = new Utils();
		 String pathString = utils.GetPathFromResource("PostDocumentOnError.js");
		 String scriptString = "tell application "+ '"' +"Applications:Adobe Illustrator "+ MessageQueue.VERSION +":Adobe Illustrator.app"+'"' +"\n with timeout of "+ timeOutSec +" seconds \n"
			+ "do javascript (file "+'"'+pathString+'"'+")  with arguments {"+ '"'+arryStr[2]+'"' +"} \n"
			+ "end timeout \n"
			+ "end tell";
		 ExecuteAppleScript(scriptString);
		 
	 	}
	 
	 public static String ExecuteIllustratorActions(String arryStr[]) throws Exception  {

		 String scriptString = "tell application "+ '"' +"Applications:Adobe Illustrator "+ MessageQueue.VERSION +":Adobe Illustrator.app"+'"' +"\n with timeout of "+ timeOutSec +" seconds \n"
			+ "tell application "+'"'+"Adobe Illustrator"+'"'+"'s document 1 \n"
			+ "set spot "+ '"'+ arryStr[0] +'"'+"'s color to spot "+ '"'+ arryStr[1] +'"'+"'s color \n"
			+ "delay 1 \n"
			+ "do script "+'"'+ "Select All Unused"+'"'+" from "+'"'+ "Merge Swatches"+'"'+" without dialogs \n"
			+ "delete swatch "+'"'+arryStr[1]+'"'+" \n"
			+ "delay 1 \n"
			+ "set name of swatch " +'"' + arryStr[0] + '"' +  " to " + '"' + arryStr[1] + '"' + " \n"
			+ "end tell \n"
			+ "end timeout \n"
			+ "end tell";
		return ExecuteAppleScript(scriptString);
		 
	 	}
	 
	 /*
	  	 public static String ExecuteIllustratorActions(String arryStr[]) throws Exception  {

		 String scriptString = "tell application "+ '"' +"Applications:Adobe Illustrator "+ MessageQueue.VERSION +":Adobe Illustrator.app"+'"' +"\n with timeout of "+ timeOutSec +" seconds \n"
			+ "tell application "+'"'+"Adobe Illustrator"+'"'+"'s document 1 \n"
			+ "set spot "+ '"'+ arryStr[0] +'"'+"'s color to spot "+ '"'+ arryStr[1] +'"'+"'s color \n"
			+ "do script "+'"'+ "Select All Unused"+'"'+" from "+'"'+ "Merge Swatches"+'"'+" without dialogs \n"
			+ "delete swatch "+'"'+arryStr[1]+'"'+"\n"
			+ "end tell \n"
			+ "end timeout \n"
			+ "end tell";
		return ExecuteAppleScript(scriptString);
		 
	 	} 
	  
	  */
	 
	 public static void OnError() throws Exception  {

		 Utils utils = new Utils();
		 String pathString = utils.GetPathFromResource("OnError.js");
		 String scriptString = "tell application "+ '"' +"Applications:Adobe Illustrator "+ MessageQueue.VERSION +":Adobe Illustrator.app"+'"' +"\n with timeout of "+ timeOutSec +" seconds \n"
			+ "do javascript (file "+'"'+pathString+'"'+") \n"
			+ "end timeout \n"
			+ "end tell";
		 ExecuteAppleScript(scriptString);
	 }
		 
	 public static String MountVolume(String serverName, String userName, String userPass, String shareDirectory) throws Exception  
	 {	 
		 userName = "asia\\\\weblogic";
		 userPass = "417@sia123";
		String scriptString = "tell application \"Finder\" \n"
		+ "with timeout of "+ timeOutSec +" seconds \n"
		+ "set serverName to "+ '"' +serverName + '"' +" \n"
		+ "set userName to  "+ '"' + userName + '"' +"  \n"
		+ "set userPass to "+ '"' + userPass + '"' +" \n"
		+ "set shareDirectory to "+ '"' + shareDirectory + '"' +" \n"
		+ "set networkSmbString to \"smb://\" & serverName & \"/\" & shareDirectory \n"
			+ "if not (disk shareDirectory exists) then \n"
					+ "try \n"
				 		+ "mount volume networkSmbString as user name userName with password userPass \n"
				 		+ "on error errtext number errnum \n"
				 		+ "if errnum = -55 then \n"
				 			+ "return \"Error mounting on volume \" & shareDirectory \n"
				 		+ "else \n"
				 			+ "return \"Error mounting on volume \" & shareDirectory \n"
				 		+ "end if \n"
				 	+ "end try \n"
			 	+ "end if \n" 
			 	+ "end timeout \n"
				+ "end tell \n" 
				+ "return \"Volume mounted: \" &shareDirectory ";
		return ExecuteAppleScript(scriptString); 
		 
	 	}
	 
	 public static void FindIllustratorVersion() throws Exception
	 {
		   FileSystem fls = new FileSystem();	
		   String scriptString = fls.ReadFile("/Users/yuvaraj/Desktop/Desktop 2/FindTheVersion.txt");
		   System.out.println(ExecuteAppleScript(scriptString)); 
	 }
	 
	 public static void main(String[] args) throws Exception
	 {
			/* List<String>fruitList = new ArrayList<String>();  
		  
	        fruitList.add("Mango");  
	        fruitList.add("Banana");  
	        fruitList.add("Apple");  
	        fruitList.add("Strawberry");  
	        fruitList.add("Pineapple");  
	          
	    List<String>unmodifiableList= Collections.unmodifiableList(fruitList);  
	    unmodifiableList.remove(0);
	    System.out.println(unmodifiableList.get(0));  
	    */
		 
	//	 List<String> SwatchListFromXML = new ArrayList<String>();
	//	 XmlUtiility xmlUtils = new XmlUtiility();
	//	 SwatchListFromXML = xmlUtils.ParsePrivateElementSwatchColor("/Users/yuvaraj/Desktop/GS1_40160649301_10.xml","SL_ColorName", "PANTONE");
	//	 System.out.println(SwatchListFromXML);
		 
		 
		 String[] arryStr= new String[2];
		 arryStr[0] = "";
		 arryStr[1] = "";
		 MessageQueue.VERSION  = "CC 2018";
		 
		 SetSwathColorFromTo("White 2", "White");
	//	 System.out.println(SetLayerVisibleOff());
		 /*
		 String swatchString = SwatchTest(arryStr);
		 
		 String[] arrSwatch = swatchString.split("~");
		 List<String> pngSwatchList = new ArrayList<String>();
		 for (int i=0; i<arrSwatch.length; i++)
		 {
			 if(arrSwatch[i].contains("PANTONE"))
				 pngSwatchList.add(arrSwatch[i]);
				 
				 
		 }
		 System.out.println(pngSwatchList);
		 
		 int swatchListCount = 0;
		 if(SwatchListFromXML.size() <= pngSwatchList.size())
			 swatchListCount = SwatchListFromXML.size();
		 else
			 swatchListCount = pngSwatchList.size();
		 
		 for(int i=0; i<swatchListCount; i++)
		 {
			 
			 arryStr[1] = SwatchListFromXML.get(i);
			 arryStr[0] = pngSwatchList.get(i);
			 System.out.println(arryStr[0]+" Y  " +  arryStr[1]);
			 if(!arryStr[0].equals(arryStr[1]))
			 {
				 System.out.println(ExecuteIllustratorActions(arryStr));
				// MergeSwatch(arryStr);
			 }
		 }

		  */
	 }


}