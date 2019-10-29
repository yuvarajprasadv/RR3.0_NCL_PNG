package Rcvr_AAMQ;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import Rcvr_AAMQ.Utils;

public class Action {
	static Logger log = LogMQ.monitor("Rcvr_AAMQ.Action");

	public static void actionSeq(JSONObject jsonObj) throws Exception {
		JsonParser jspr = new JsonParser();
		XmlUtiility xmlUtil = new XmlUtiility();
		FileSystem fls = new FileSystem();
		Utils utils = new Utils();

		//RR in Progress status
		Action.UpdateErrorStatus("4"); //RR In Progress (4) status to Tornado API
		
		SEng.CallAdobeIllustrator();
		log.info("Illustrator activated to load file..");

		String[] appFonts = SEng.GetApplicationFonts().split(",");
		Thread.sleep(2000);

		SEng.OpenDocument(jspr.geFilePathFromJson(jsonObj, "Master"));
		log.info("AI file and other dependend file opening..");

		String dcFont = SEng.GetDocumentFonts();
		if (!dcFont.equalsIgnoreCase("error")) {
			if (dcFont.length() != 0) {
				String[] docFonts = (dcFont).split(",");
				Action.FindMissingFonts(appFonts, docFonts);
			}
		} else {
			MessageQueue.ERROR += "Document parsing error while finding missing fonts \n";
		}

		String dcFile = SEng.GetDocumentFiles();
		if (!dcFile.equalsIgnoreCase("error")) {
			if (dcFile.length() != 0) {
				String[] docFiles = dcFile.split(",");
				Action.FindMissingFiles(docFiles);
			}
		} else {
			MessageQueue.ERROR += "Document parsing error while finding missing image linked files \n";
		}
		Thread.sleep(1000);

		String[] arrString = new String[1];
		arrString[0] = MessageQueue.MESSAGE;
		arrString[0] = utils.RemoveForwardSlash(arrString[0]);
		arrString[0] = arrString[0].replace("\"", "'");

		
		SEng.CallTyphoonShadow(arrString);

		Thread.sleep(1000);
		////Swatch from xml // for  PNG
		SwatchMergeFromXML(jspr.geFilePathFromJson(jsonObj, "XMLFile"), "SL_ColorName","PANTONE");
	//	SwatchMergeFromXML(jspr.geFilePathFromJson(jsonObj, "XMLFile"), "SL_ColorName","P&G");
		////Swatch from xml // for  PNG
		
		log.info("TyphoonShadow called");

		String errorMsg = fls.ReadFileReport("error.txt");
		if (errorMsg.contains("\n") && errorMsg.length() != 1)
			MessageQueue.STATUS = false;

		{

			INIReader ini = new INIReader();
			ini.readIniForSingle();

			String[] docPath = jspr.getPath(jsonObj);
			String fileNameToSave = xmlUtil.getFileNameFromElement((docPath[0].split("~"))[0]);
			if(fileNameToSave != null)
			{
				
				String fileName = utils.GetNewNameIfFileExists(GetLastIndex(docPath[1])  + "/" + fileNameToSave + ".ai" );
				  
				int index = fileName.lastIndexOf("/");
				fileName =  fileName.substring(index+1, fileName.length());
				fileNameToSave = fileName.split("\\.")[0];
			}
				
			
			String[] fileName = new String[4];
			if (fileNameToSave != null) {
				fileName[0] = "none"; // dummy
				fileName[1] = "none"; // dummy
				fileName[2] = GetLastIndex(docPath[2]) + "/" + fileNameToSave;  
				fileName[3] = GetLastIndex(docPath[1]) + "/" + fileNameToSave; 
			}
			else //// After renmaing to <workorder no>.ai
			{
				fileNameToSave = jspr.getJsonValueForKey(jsonObj, "WO");
				fileName[2] = GetLastIndex(docPath[2]) + "/" + fileNameToSave;
				fileName[3] = GetLastIndex(docPath[1]) + "/" + fileNameToSave;
			} // // After renmaing to <workorder no>.ai

			if (MessageQueue.sPdfNormal) {
				if (fileNameToSave != null)
				{
					SEng.PostDocumentProcessForSingleJobFilename(fileName);
				}
				else
					SEng.PostDocumentProcess(jspr.getPath(jsonObj));
			} else if (MessageQueue.sPdfPreset) {

				String pdfPreset[] = utils.getPresetFileFromDirectory(
						utils.GetParentPath(jspr.geFilePathFromJson(jsonObj, "Master")), "joboptions");
				String[] pdfPresetArr = new String[2];
				if (pdfPreset.length != 0) {
					pdfPresetArr[0] = utils.GetParentPath(jspr.geFilePathFromJson(jsonObj, "Master")) + "/"
							+ pdfPreset[0];
					pdfPresetArr[1] = pdfPreset[0].split("\\.")[0];
					String resultPdfExport = "";
					if (fileNameToSave != null)
						resultPdfExport = SEng.PostDocMultiPDFPreset(fileName, pdfPresetArr);
					else {
						String[] dcPath = new String[4];
						dcPath[0] = "";
						dcPath[1] = "";
						dcPath[2] = docPath[2];
						dcPath[3] = docPath[1];
						resultPdfExport = SEng.PostDocMultiPDFPreset(dcPath, pdfPresetArr);
					}
					if (!resultPdfExport.equalsIgnoreCase("null")) {
						fls.AppendFileString("\n PDF with preset export failed: " + resultPdfExport + " \n");
						MessageQueue.ERROR += resultPdfExport + "\n";
					}
				} else {
					fls.AppendFileString(
							"\n PDF with preset export failed, preset file not found in master ai file path \n");
					MessageQueue.ERROR += "\n PDF with preset export failed, preset file not found in master ai file path \n";
				}
			} else if (MessageQueue.sPdfNormalised) {
				if (PostMultipleJobPreProcess()) {
					docPath[2] = docPath[2].split("\\.")[0];
					String resultPdfExport = "";
					if (fileNameToSave != null)
						resultPdfExport = SEng.PostDocumentMultipleProcess(fileName);
					else {
						String[] dcPath = new String[4];
						dcPath[0] = "";
						dcPath[1] = "";
						dcPath[2] = docPath[2];
						dcPath[3] = docPath[1];
						resultPdfExport = SEng.PostDocumentMultipleProcess(dcPath);
					}
					if (!resultPdfExport.equalsIgnoreCase("null")) {
						fls.AppendFileString("\n Normalized PDF export failed: " + resultPdfExport + " \n");
						MessageQueue.ERROR += resultPdfExport + "\n";
					}
				}
			}

			log.info("Pdf and xml generated..");
			
			
					////----PNG---// Only 3D xml
					JsonParser jsonPars = new JsonParser();
					boolean is3DXMLAI = jsonPars.getJsonBooleanValueForKey(jsonObj, "region", "RR3DXML");
					if(is3DXMLAI)
					{
						Thread.sleep(7000);
						String jsonString = jspr.updateJsonForMultipleJob(jsonObj, "999_Delete_at_Archive/", "3DXML.xml");
						String[] newArryStr= new String[1];
						newArryStr[0] = jsonString;
						newArryStr[0] = utils.RemoveForwardSlash(newArryStr[0]);
						newArryStr[0] = newArryStr[0].replace("\"", "'");
						
						SEng.CallTyphoonShadow(newArryStr);
						Thread.sleep(4000);
						
					//	SEng.SetLayerVisibleOff(); //// only for NCL  P  - N  -  G
						
						Thread.sleep(1000);
						String fileRenameString = jspr.getJsonValueForKey(jsonObj, "WO") + "_3dxml";
						
						String road_runnerDirPath = utils.RemoveForwardSlash((String) jspr.getJsonValueForKey(jsonObj, "Path") + "999_Delete_at_Archive/road_runner");
						if(!Utils.IsFolderExists(road_runnerDirPath))
						{
							utils.CreateNewDirectory(road_runnerDirPath, false);
						}
						
						fileName[0] = utils.RemoveForwardSlash((String) jspr.getJsonValueForKey(jsonObj, "Path") + "999_Delete_at_Archive/");
						fileName[1] = road_runnerDirPath + "/" + fileRenameString;
						fileName[2] = fileName[0] + fileRenameString;
						fileName[3] = fileName[0] + fileRenameString;
						SEng.PostDocumentProcessFor3DXML(fileName);

					}
					
					////----PNG---
					
			
			// ***PnG***// This is to Move from different part to 051_PA_Previous.
			
			Utils utls = new Utils();
			String sourceFile = jsonPars.getMasterAIWithoutPathValidate(jsonObj, "Master");
			String destinationFilePath = jsonPars.getJsonValueFromGroupKey(jsonObj, "aaw", "Path") + "050_Production_Art/051_PA_Previous/";
			
			String moveFileStatus = "";
			moveFileStatus = utls.MoveFileFromSourceToDestination(sourceFile, destinationFilePath); 
			

			// ***PnG**//
			
			
			

		}
		Thread.sleep(8000);
		SEng.PostDocumentClose();

		sendRespStatusMsg("delivered");
		log.info("Completed process for job id  '" + MessageQueue.MSGID + "' ");
		Thread.sleep(1000);

		
		Action.UpdateToServer(jsonObj, "xmlcompare");
		log.info("Xml comparision completed..");
		

		Action.UpdateReport(jsonObj, fls.ReadFileReport("Report.txt"));

		MessageQueue.ERROR += errorMsg;
		Action.sendStatusMsg((String) MessageQueue.ERROR);
		MessageQueue.ERROR = "";
		
		Thread.sleep(1000);
		MessageQueue.GATE = true;

	}

	public static void multiActionSeq(JSONObject jsonObj) throws Exception {
		JsonParser jspr = new JsonParser();
		XmlUtiility xmlUtil = new XmlUtiility();
		FileSystem fls = new FileSystem();
		Utils utils = new Utils();

		utils.CreateNewDirectory(jspr.getXmlFolderPathFromJson(jsonObj, "XMLFile") + "DummyFolder", true);

		String pdfPreset[] = utils.getPresetFileFromDirectory(
				utils.GetParentPath(jspr.geFilePathFromJson(jsonObj, "Master")), "joboptions");

		String xmlFiles[] = utils.getFileFromDirectory(jspr.getXmlFolderPathFromJson(jsonObj, "XMLFile"), "xml");
		ArrayList arrErrReport = new ArrayList();
		ArrayList arrDetailedReport = new ArrayList();
		ArrayList arrConsolidateErrorReport = new ArrayList();
		ArrayList arrConsolidateDetailedReport = new ArrayList();

		SEng.CallAdobeIllustrator();
		log.info("Illustrator activated to load file..");

		String[] appFonts = SEng.GetApplicationFonts().split(",");
		Thread.sleep(5000);

		SEng.OpenDocument(jspr.geFilePathFromJson(jsonObj, "Master"));
		log.info("AI file and other dependend file opening..");

		String dcFont = SEng.GetDocumentFonts();
		if (dcFont.length() != 0) {
			String[] docFonts = (dcFont).split(",");
			Action.FindMissingFonts(appFonts, docFonts);
		}
		String dcFile = SEng.GetDocumentFiles();
		if (dcFile.length() != 0) {
			String[] docFiles = dcFile.split(",");
			Action.FindMissingFiles(docFiles);
		}

		utils.DeleteDirectory(jspr.getXmlFolderPathFromJson(jsonObj, "XMLFile") + "DummyFolder");
		Thread.sleep(1000);

		INIReader ini = new INIReader();
		ini.readIniForMultiple();

		String[] pdfPresetArr = new String[2];
		if (MessageQueue.mPdfPreset) {
			if (pdfPreset.length != 0) {
				pdfPresetArr[0] = utils.GetParentPath(jspr.geFilePathFromJson(jsonObj, "Master")) + "/" + pdfPreset[0];
				pdfPresetArr[1] = pdfPreset[0].split("\\.")[0];
			}
		} else if (MessageQueue.mPdfNormalised) {
			PostMultipleJobPreProcess();
		}

		String xmlDirPath = jspr.getXmlDirPath(jsonObj);
		for (int eachXmlCount = 0; eachXmlCount < xmlFiles.length; eachXmlCount++) {
			MessageQueue.MESSAGE = jspr.updateJsonForMultipleJob(jsonObj, xmlDirPath, xmlFiles[eachXmlCount]);
			String[] docPath = jspr.getMultiPath(jsonObj, xmlFiles[eachXmlCount]);
			if (!XmlUtiility.multipleJobIsValidXml(docPath[0])) {
				ConsolidateErrorReport(fls, arrErrReport, arrConsolidateErrorReport, arrDetailedReport,
						arrConsolidateDetailedReport, xmlFiles[eachXmlCount]);
				continue;
			}
			String fileNameToSave = xmlUtil.getFileNameFromElement(docPath[0]);
			// docPath[0] += "~0";

			String[] arrString = new String[1];
			arrString[0] = MessageQueue.MESSAGE;
			arrString[0] = utils.RemoveForwardSlash(arrString[0]);
			arrString[0] = arrString[0].replace("\"", "'");



			SEng.CallTyphoonShadow(arrString);

			log.info("TyphoonShadow called");

			String[] fileName = new String[4];
			if (fileNameToSave != null) {

				fileName[0] = "none"; // dummy
				fileName[1] = "none"; // dummy
				fileName[2] = GetLastIndex(docPath[2]) + "/" + fileNameToSave;
				fileName[3] = GetLastIndex(docPath[3]) + "/" + fileNameToSave;

			}

			if (MessageQueue.mPdfNormal) {
				if (fileNameToSave != null) {
					SEng.PostDocumentProcessForSingleJobFilename(fileName);
				} else {
					SEng.PostDocumentProcessForSingleJobFilename(docPath);
				}
			} else if (MessageQueue.mPdfPreset) {
				if (pdfPreset.length != 0) {
					String resultPdfExport = "";
					if (fileNameToSave != null)
						resultPdfExport = SEng.PostDocMultiPDFPreset(fileName, pdfPresetArr);
					else
						resultPdfExport = SEng.PostDocMultiPDFPreset(docPath, pdfPresetArr);
					if (!resultPdfExport.equalsIgnoreCase("null")) {
						fls.AppendFileString("\n PDF with preset export failed: " + resultPdfExport + " \n");
						MessageQueue.ERROR += resultPdfExport + "\n";
					}
				} else {
					fls.AppendFileString(
							"\n PDF with preset export failed, preset file not found in master ai file path \n");
					MessageQueue.ERROR += "\n PDF with preset export failed, preset file not found in master ai file path \n";
				}
			} else if (MessageQueue.mPdfNormalised) {
				docPath[2] = docPath[2].split("\\.")[0];
				String resultPdfExport = "";
				if (fileNameToSave != null)
					resultPdfExport = SEng.PostDocumentMultipleProcess(fileName);
				else
					resultPdfExport = SEng.PostDocumentMultipleProcess(docPath);
				if (!resultPdfExport.equalsIgnoreCase("null")) {
					fls.AppendFileString("\n Normalized PDF export failed: " + resultPdfExport + " \n");
					MessageQueue.ERROR += resultPdfExport + "\n";
				}
			}
			Thread.sleep(4000);
			log.info("Pdf and xml generated..");
			ConsolidateErrorReport(fls, arrErrReport, arrConsolidateErrorReport, arrDetailedReport,
					arrConsolidateDetailedReport, xmlFiles[eachXmlCount]);
		}

		Thread.sleep(7000);
		SEng.PostDocumentClose();
		sendRespStatusMsg("delivered");
		log.info("Completed process for job id  '" + MessageQueue.MSGID + "' ");

		Action.UpdateToServer(jsonObj, "xmlcompare");
		log.info("Xml comparision completed..");

		Action.sendStatusMsg(arrConsolidateErrorReport.toString());
		log.info("Completed sending error report..");
		Action.UpdateReport(jsonObj, arrConsolidateDetailedReport.toString());
		log.info("Completed sending of detailed report..");

		MessageQueue.ERROR = "";
		Thread.sleep(1000);
		MessageQueue.GATE = true;
		log.info("Completed job..");
	}

	
	public static void SwatchMergeFromXML(String xmlPathString, String privateElmtTypeCode, String swatchColorName) throws Exception
	{
		 List<String> SwatchListFromXML = new ArrayList<String>();
		 XmlUtiility xmlUtils = new XmlUtiility();
		 SwatchListFromXML = xmlUtils.ParsePrivateElementSwatchColor(xmlPathString, privateElmtTypeCode, swatchColorName);
		 
 
		 String[] arryStr= new String[2];
		 arryStr[0] = "";
		 arryStr[1] = "";
		 
		 String swatchString = SEng.SwatchTest(arryStr);
		 
		 String[] arrSwatch = swatchString.split("~");
		 List<String> pngSwatchList = new ArrayList<String>();
		 for (int i=0; i<arrSwatch.length; i++)
		 {
			 if(arrSwatch[i].contains(swatchColorName))
				 pngSwatchList.add(arrSwatch[i]);
				 
				 
		 }
		 
		 int swatchListCount = 0;
		 if(SwatchListFromXML.size() <= pngSwatchList.size())
			 swatchListCount = SwatchListFromXML.size();
		 else
			 swatchListCount = pngSwatchList.size();
		 
		 for(int i=0; i<swatchListCount; i++)
		 {
			 
			 arryStr[1] = SwatchListFromXML.get(i);
			 arryStr[0] = pngSwatchList.get(i);
			 if(!arryStr[0].equals(arryStr[1]))
				 SEng.ExecuteIllustratorActions(arryStr);
		 }
		 
	}
	
	public static String GetLastIndex(String filePath) {
		int index = filePath.lastIndexOf("/");
		return filePath.substring(0, index);
	}

	public static boolean PostMultipleJobPreProcess() {
		Utils utls = new Utils();
		boolean eskoPluginbool = false;
		String eskoPdfPlugin = "";
		if (MessageQueue.VERSION.equalsIgnoreCase("CS6")) {
			eskoPdfPlugin = "/Applications/Adobe Illustrator " + MessageQueue.VERSION
					+ "/Plug-ins.localized/Esko/Data Exchange/PDF Export/PDFExport_MAI16r.aip";
			eskoPluginbool = utls.FileExists(eskoPdfPlugin);
		} else if (MessageQueue.VERSION.equalsIgnoreCase("CC")) {
			eskoPdfPlugin = "/Applications/Adobe Illustrator " + MessageQueue.VERSION
					+ "/Plug-ins.localized/Esko/Data Exchange/PDF Export/PDFExport_MAI17r.aip";
			eskoPluginbool = utls.FileExists(eskoPdfPlugin);
		} else if (MessageQueue.VERSION.equalsIgnoreCase("CC 2014")) {
			eskoPdfPlugin = "/Applications/Adobe Illustrator " + MessageQueue.VERSION
					+ "/Plug-ins.localized/Esko/Data Exchange/PDF Export/PDFExport_MAI18r.aip";
			eskoPluginbool = utls.FileExists(eskoPdfPlugin);
		} else if (MessageQueue.VERSION.equalsIgnoreCase("CC 2015")) {
			eskoPdfPlugin = "/Applications/Adobe Illustrator " + MessageQueue.VERSION
					+ "/Plug-ins.localized/Esko/Data Exchange/PDF Export/PDFExport_MAI20r.aip";
			eskoPluginbool = utls.FileExists(eskoPdfPlugin);
		} else if (MessageQueue.VERSION.equalsIgnoreCase("CC 2015.3")) {
			eskoPdfPlugin = "/Applications/Adobe Illustrator " + MessageQueue.VERSION
					+ "/Plug-ins.localized/Esko/Data Exchange/PDF Export/PDFExport_MAI20r.aip";
			eskoPluginbool = utls.FileExists(eskoPdfPlugin);
		} else if (MessageQueue.VERSION.equalsIgnoreCase("CC 2017")) {
			eskoPdfPlugin = "/Applications/Adobe Illustrator " + MessageQueue.VERSION
					+ "/Plug-ins.localized/Esko/Data Exchange/PDF Export/PDFExport_MAI21r.aip";
			eskoPluginbool = utls.FileExists(eskoPdfPlugin);
		} else if (MessageQueue.VERSION.equalsIgnoreCase("CC 2018")) {
			eskoPdfPlugin = "/Applications/Adobe Illustrator " + MessageQueue.VERSION
					+ "/Plug-ins.localized/Esko/Data Exchange/PDF Export/PDFExport_MAI22r.aip";
			eskoPluginbool = utls.FileExists(eskoPdfPlugin);
		}
		

		if (!eskoPluginbool) {
			MessageQueue.ERROR += "PDF cannot be generated following plugin missing : " + eskoPdfPlugin + "\n";
			return false;
		}
		return true;
	}

	public static void ConsolidateErrorReport(FileSystem fls, ArrayList arrErrReport,
			ArrayList arrConsolidateErrorReport, ArrayList arrDetailedReport, ArrayList arrConsolidateDetailedReport,
			String xmlFile) {
		String errorMsg = fls.ReadFileReport("error.txt");
		MessageQueue.ERROR += errorMsg;
		arrErrReport.clear();
		arrErrReport.add(MessageQueue.ERROR);
		arrConsolidateErrorReport.add(xmlFile + ":" + arrErrReport);
		fls.CreateFile("error.txt");
		MessageQueue.ERROR = "";

		String errDetailedReport = fls.ReadFileReport("Report.txt");
		arrDetailedReport.clear();
		arrDetailedReport.add(errDetailedReport);
		arrConsolidateDetailedReport.add(xmlFile + ":" + arrDetailedReport);
		fls.CreateFile("Report.txt");
	}

	public static void ValidateFiles(JSONObject jsonObj) throws Exception {

		JsonParser jspr = new JsonParser();
		String[] pathString = jspr.getPath(jsonObj);
		try {
			String[] xmlFilesPath = pathString[0].split(",");
			for (int eachXmlCount = 0; eachXmlCount < xmlFilesPath.length; eachXmlCount++) {
				XmlUtiility.IsValidXML(xmlFilesPath[eachXmlCount].split("~")[0]);
			}
		} catch (Exception ex) {
			log.error("xml err: " + ex);
		//	ThrowException.CatchException(ex);
			ThrowException.CatchExceptionWithErrorMsgId(ex,"Error in xml" ,"14");
		}
	}

	public static void acknowledge(String jsonString) throws Exception {
		JSONObject jsonObj = JsonParser.ParseJson(jsonString);
		
		String version = (String) jsonObj.get("version");
		MessageQueue.VERSION = version;
		FileSystem fls = new FileSystem();
		fls.CreateFile("Report.txt");
		fls.CreateFile("error.txt");

		INetwork iNet = new INetwork();

		MessageQueue.MSGID = (String) jsonObj.get("Id");
		//Action.AddVolumes(); // mount volume after creating message id.

		// ***PnG***// This is to copy from different part to 050_Production.
		JsonParser jsonPars = new JsonParser();
		Utils utls = new Utils();
		String destinationFilePath = jsonPars.getMasterAIWithoutPathValidate(jsonObj, "Master");
		String sourceFile = jsonPars.getJsonValueFromGroupKey(jsonObj, "aaw", "MasterTemplate");
		
		String copyFileStatus = "";
		copyFileStatus = utls.CopyFileFromSourceToDestination(sourceFile, destinationFilePath); 
		

		// ***PnG**//
	
		if(copyFileStatus != "") 
		{
			try {
				if (!((String) jsonObj.get("type")).equals("multi"))
					ValidateFiles(jsonObj);
			} catch (java.lang.NullPointerException Ex) {
				log.error(Ex.getMessage());
				MessageQueue.ERROR += "\nInvalid Json request";
				fls.AppendFileString("\nInvalid Json request:" + " \n");
			//	ThrowException.CustomExit(Ex, "Invalid JSON request from Tornado");
				ThrowException.CustomExitWithErrorMsgID(Ex, "Invalid JSON request from Tornado", "14");
			}
			try {
				sendRespStatusMsg("received" + "::" + iNet.GetClientIPAddr());
				log.info("Message received acknowledgement for job id  '" + MessageQueue.MSGID + "' ");
	
				if (!((String) jsonObj.get("type")).equals("multi"))
					actionSeq(jsonObj);
				else
					multiActionSeq(jsonObj);
			} catch (Exception ex) {
				log.error("Msg Ack err: " + ex);
			}
		}

	}

	public static void sendRespStatusMsg(String status) throws Exception {
		try {

			HttpConnection.excutePost("http://" + MessageQueue.HOST_IP + ":8080/AAW/message/resp",
					MessageQueue.MSGID + "::" + status);
		} catch (Exception ex) {
			log.error(ex);
		}

	}

	public static void sendStatusMsg(String status) throws Exception {
		try {
			HttpConnection.excutePost("http://" + MessageQueue.HOST_IP + ":8080/AAW/message/error",
					MessageQueue.MSGID + "::" + status);
		} catch (Exception ex) {
			log.error(ex);
		}

	}

	public static void UpdateToServer(JSONObject jsonObj, String actionStr) throws IOException {
		try {
			HttpsConnection httpsCon = new HttpsConnection();
			HttpURLConnection connection;
			URL urlStr = new URL(
					MessageQueue.TORNADO_HOST + "/rest/pub/aaw/" + actionStr + "?mqid=" + (String) jsonObj.get("Id"));
			connection = (httpsCon.getURLConnection(urlStr, true));
			System.out.println("XML Compare : " + connection.getResponseCode());
		} catch (Exception ex) {
			log.error((String) ex.getMessage());
		}
	}

	public static void UpdateReport(JSONObject jsonObj, String reportStr) throws IOException {
		try {
			HttpsConnection httpsCon = new HttpsConnection();
			httpsCon.excuteHttpJsonPost(MessageQueue.TORNADO_HOST + "/rest/pub/aaw/finalreport",
					(String) jsonObj.get("Id"), reportStr);
		} catch (Exception ex) {
			log.error((String) ex.getMessage());
		}
	}
	
	public static void UpdateErrorStatus(String reportStr) throws IOException {
		try {
			HttpsConnection httpsCon = new HttpsConnection();
		    httpsCon.excuteErrorStatusHttpJsonPost(MessageQueue.TORNADO_HOST + "/rest/pub/rr/updatestatus",
					MessageQueue.MSGID, reportStr);
		} catch (Exception ex) {
			log.error((String) ex.getMessage());
		}
	}
	public static void UpdateErrorStatusWithRemark(String reportStr, String remarks) throws IOException {
		try {
			HttpsConnection httpsCon = new HttpsConnection();
		    httpsCon.excuteErrorStatusHttpJsonPostWithRemark(MessageQueue.TORNADO_HOST + "/rest/pub/rr/updatestatus",
					MessageQueue.MSGID, reportStr, remarks);
		} catch (Exception ex) {
			log.error((String) ex.getMessage());
		}
	}

	public static void FindMissingFonts(String[] appFonts, String[] docFonts) throws Exception {
		Utils utls = new Utils();
		FileSystem fls = new FileSystem();
		ArrayList<String> missingFonts = utls.GetMissingFonts(appFonts, docFonts);
		if (missingFonts.size() > 0) {
			fls.AppendFileString("Document font not found:" + missingFonts.toString() + "\n");
			MessageQueue.ERROR += "\nDocument linked font missing \n";
		}
	}

	public static void FindMissingFiles(String[] arrFiles) throws Exception {

		Utils utls = new Utils();
		FileSystem fls = new FileSystem();
		ArrayList<String> missingFiles = new ArrayList<String>();
		{
			int arrLen = arrFiles.length;
			if (arrLen > 0) {
				for (int i = 0; i < arrLen; i++) {
					if (!utls.FileExists(arrFiles[i])) {
						missingFiles.add(arrFiles[i]);
					}
				}
				List<String> duplicateList = missingFiles;
				HashSet<String> listToSet = new HashSet<String>(duplicateList);
				List<String> listWithoutDuplicates = new ArrayList<String>(listToSet);
				missingFiles = (ArrayList<String>) listWithoutDuplicates;
			}

		}

		if (missingFiles.size() > 0) {
			fls.AppendFileString("Document file not found :" + missingFiles.toString() + "\n");
			MessageQueue.ERROR += "\nDocument linked file missing \n";
		}
	}

	public static void AddVolumes() throws Exception {
		List<String[]> rowList = new ArrayList<String[]>();
		Utils utls = new Utils();
		rowList = utls.ReadXLSXFile(utls.GetPathFromResource("SMB.xlsx"), "sheet1");
		for (String[] row : rowList) {
			int noOfShareFolder = row.length - 1;
			for (int inc = 0; inc < noOfShareFolder; inc++) {
				String status = SEng.MountVolume(row[0], "", "", row[row.length - inc - 1]);
				if(status.contains("Error"))
				{
					Action.UpdateErrorStatusWithRemark("20", "Volume not able to mount, " + "server: " + row[0] + " share directory: " + row[row.length - inc - 1]);
				}
			}
		}
	}

	public static void Mount() throws Exception {
		String[] arg = null;
		Utils utl = new Utils();
		arg = utl.ReadFromExcel("SMB.xls", true, 0, false, 0, false);
	//	int noOfShareFolder = arg.length - 3;
		int noOfShareFolder = arg.length - 1;
		for (int inc = 0; inc < noOfShareFolder; inc++) {
		//	SEng.MountVolume(arg[0], arg[1], arg[2], arg[arg.length - inc - 1]);
			SEng.MountVolume(arg[0], "", "", arg[arg.length - inc - 1]);
		}

	}

	public static void main(String args[]) throws Exception {


	}

}
