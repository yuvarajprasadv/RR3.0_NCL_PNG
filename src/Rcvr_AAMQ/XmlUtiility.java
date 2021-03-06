package Rcvr_AAMQ;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;
import org.apache.log4j.Logger;

import org.w3c.dom.*;

public class XmlUtiility
{
	static Logger log = LogMQ.monitor("Rcvr_AAMQ.XmlUtiility");
		
	 public static boolean validateXMLSchema(String xsdPath, String xmlPath) throws Exception
	 {
	      try {
	         SchemaFactory factory = 
	            SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	         Schema schema = factory.newSchema(new File(xsdPath));
	            Validator validator = schema.newValidator();
	            validator.validate(new StreamSource(new File(xmlPath)));
	      } catch (IOException ioEx){    
	    	  log.error(ioEx.getMessage());
	    	  throw ioEx;
	      }catch(SAXException saxEx){
	    	  log.error(saxEx.getMessage());
	    	  throw saxEx;
	      }
	      return true;
	   }
	 
	   public static Document parseXmlFile(String filePath) throws Exception{
				//get the factory
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

				try {

					//Using factory get an instance of document builder
					DocumentBuilder db = dbf.newDocumentBuilder();

					//parse using builder to get DOM representation of the XML file
					Document dom = db.parse(filePath);
					return dom;

				}catch(Exception Ex) {
					log.error(Ex.getMessage());
					throw Ex;
				}
			}
	   
	   public static Boolean IsValidXML(String filePath) throws Exception
	   {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			FileSystem fls = new FileSystem();
			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				db.parse(filePath);
				return true;

			}
			catch(java.io.FileNotFoundException Ex)
			{
				log.error(Ex.getMessage());
				MessageQueue.ERROR += "\n XML File or file path not found: "+ filePath;
				fls.AppendFileString("\nXML File or file path not found :"+ filePath.toString()+"\n");
		//		ThrowException.CustomExit(Ex, "File or File path Invalid: "+ filePath.toString());
				ThrowException.CustomExitWithErrorMsgID(Ex, filePath.toString(), "23");
				throw Ex;
			}
			catch(org.xml.sax.SAXParseException Ex)
			{
				log.error(Ex.getMessage());
				MessageQueue.ERROR += "\n Invalid XML: "+ filePath;
				fls.AppendFileString("\nInvalid XML file path: " + filePath.toString()+"\n");
			//	ThrowException.CustomExit(Ex, "Invalid xml: "+ filePath.toString());
				ThrowException.CustomExitWithErrorMsgID(Ex, filePath.toString(), "23");
				throw Ex;
			}
			catch(Exception Ex) 
			{
				log.error(Ex.getMessage());
				MessageQueue.ERROR += "\n Invalid XML: "+ filePath;
				fls.AppendFileString("\nInvalid XML file: "+ filePath.toString()+"\n");
			//	ThrowException.CustomExit(Ex, "Invalid file: "+filePath.toString());
				ThrowException.CustomExitWithErrorMsgID(Ex, filePath.toString(), "23");
				
				throw Ex;
			} 
	   }
	   
	   public static Boolean multipleJobIsValidXml(String filePath) throws Exception
	   {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			FileSystem fls = new FileSystem();
			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				db.parse(filePath);
				return true;

			}
			catch(java.io.FileNotFoundException Ex)
			{
				log.error(Ex.getMessage());
				MessageQueue.ERROR += "\n XML File or file path not found: "+ filePath;
				fls.AppendFileString("\nXML File or file path not found :"+ filePath.toString()+"\n");
			}
			catch(org.xml.sax.SAXParseException Ex)
			{
				log.error(Ex.getMessage());
				MessageQueue.ERROR += "\n Invalid XML: " + Ex.getMessage() + " ---" + filePath;
				fls.AppendFileString("\nInvalid XML file path: " + filePath.toString()+"\n");
			}
			catch(Exception Ex) 
			{
				log.error(Ex.getMessage());
				MessageQueue.ERROR += "\n Invalid XML: "+ filePath;
				fls.AppendFileString("\nInvalid XML file: "+ filePath.toString()+"\n");
			} 
			return false;
	   }
	   
	   public String parsePrivateElement(Document dom, String privateElmTypeCode)
	   {
		   try{
			   String[] styleArr = new String[92];
			   String privateElements = "";
			   int inc = 0;
			   NodeList ld = dom.getElementsByTagName("privateElements").item(0).getChildNodes();
			   for (int tmp=1; tmp < ld.getLength(); tmp++)
			   {
				   Node nd = ld.item(tmp);
				   if(nd.getNodeType() == 1)
				   {
					  NodeList cld = nd.getChildNodes();
					  String cpyElement = "";
					  for(int eachChild=1; eachChild< cld.getLength(); eachChild++)
					  {
						  Node cnd = cld.item(eachChild);
						  if (cnd.getNodeType()==1)
						  {
							  if (cnd.getNodeName() == "privateElementTypeCode")
								  cpyElement = cnd.getFirstChild().getNodeValue();
							  else if(cnd.getNodeName() == "instanceSequence")
								  cpyElement += "-" + cnd.getFirstChild().getNodeValue();// + "...1";
							  else if(cnd.getNodeName() == "localeSequence")
							  {
								  String[] arrStr = cpyElement.split("-");
							  	  cpyElement =  arrStr[0] +  cnd.getFirstChild().getNodeValue() + "-" + arrStr[1];
							  }
							//  System.out.println("copy elem : " + cpyElement + "\n");
						  if(cpyElement.equals(privateElmTypeCode))
						  {
							  if(cnd.getNodeType()==1 && cnd.getChildNodes().getLength() > 1)
							  {
								  NodeList bdCldList = cnd.getChildNodes().item(1).getChildNodes().item(1).getChildNodes();
								  for (int bdClCnt=0; bdClCnt < bdCldList.getLength(); bdClCnt++)
								 //  for (int bdClCnt=0; bdClCnt <4; bdClCnt++)
								   {
									   Node bdChild = bdCldList.item(bdClCnt);
									   if(bdChild.getNodeType() == 1)
									   {
										   styleArr[inc] = bdChild.getNodeName();
										   privateElements = privateElements + styleArr[inc];
										   
										   if (bdChild.hasChildNodes())
										   {
											   inc += 1;
											   styleArr[inc] = bdChild.getFirstChild().getNodeValue();
											   privateElements += ":" + styleArr[inc] + ",";
											   inc += 1;
										   }
										   else
										   {
											   inc += 1;
											   styleArr[inc] = "None";
											   privateElements += ":" + styleArr[inc] + ",";
											   inc += 1;
										   }
										   
										   
									   }
									   
								   }
								  return (privateElements);
							  }
						  }
	
					  } 
	
				   }
				 }
			   }
			    
	      } catch (Exception ex) {
	         
	    	  log.error("No private elements " + ex.getMessage());
	    	  throw ex;
	    	
	      }
		//   System.out.println("gs1 parser");
		return null;
	   }
	   
	   public void WriteFile(String prvString)
	   {
		   Utils utl = new Utils();
		   
		   List<String> lines = Arrays.asList(prvString);
		   Path file = Paths.get(utl.ConvertToAbsolutePath("/Applications/Adobe Illustrator "+MessageQueue.VERSION+"/Plug-ins.localized/Sgk/Configuration/PrivateElements.txt"));
		   try {
			Files.write(file, lines, Charset.forName("UTF-8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   }
	   
	   
	   public void WriteFont(String prvString)
	   {
		   Utils utl = new Utils();
		   
		   List<String> lines = Arrays.asList(prvString);
		   Path file = Paths.get(utl.ConvertToAbsolutePath("/Applications/Adobe Illustrator "+MessageQueue.VERSION+"/Plug-ins.localized/Sgk/Configuration/PrivateElementFont.txt"));
		   try {
			Files.write(file, lines, Charset.forName("UTF-8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   }
	   
	   
	   public void SplitBy(String elementString)
	   {
		   String[] pvlElement;
		   String[] stringNewLine;
		   String otherThanFont = "";
		   String fontString = "";
		   stringNewLine = elementString.split("\n");
		   int j = 0;
		   while(j < stringNewLine.length)
		   {
			   pvlElement = stringNewLine[j].split(":");
			   for(int i=0; i<pvlElement.length; i++)
			   {
				   if(i!=1)
				   {
					   otherThanFont +=pvlElement[i].trim() + " ";
				   }
				   else
				   {
					   fontString += pvlElement[i].trim() + "\n";
				   }
			   }
			   otherThanFont +="\n";
		   j++;
		   }
		   WriteFile(otherThanFont.trim());
		   WriteFont(fontString.trim());
		  
	   }
	   
	   public String parsePrivateAllElement(Document dom, String privateElmTypeCode)
	   {
		   try{
			   String privateElements = "";
			   int inc = 0;
			   NodeList ld = dom.getElementsByTagName("privateElements").item(0).getChildNodes();
			   for (int tmp=1; tmp < ld.getLength(); tmp++)
			   {
				   Node nd = ld.item(tmp);
				   if(nd.getNodeType() == 1)
				   {
					  NodeList cld = nd.getChildNodes();
					  String cpyElement = "";
					  for(int eachChild=1; eachChild< cld.getLength(); eachChild++)
					  {
						  Node cnd = cld.item(eachChild);
						  if (cnd.getNodeType()==1)
						  {
							  if (cnd.getNodeName() == "privateElementTypeCode")
								  cpyElement = cnd.getFirstChild().getNodeValue();
							  else if(cnd.getNodeName() == "instanceSequence")
								  cpyElement += "-" + cnd.getFirstChild().getNodeValue() + "...1";
							  else if(cnd.getNodeName() == "localeSequence")
							  {
								  String[] arrStr = cpyElement.split("-");
							  	  cpyElement =  arrStr[0] +  cnd.getFirstChild().getNodeValue() + "-" + arrStr[1];
							  }
							 
						  if(cpyElement.equalsIgnoreCase(privateElmTypeCode))
						  {
							  if(cnd.getNodeType()==1 && cnd.getChildNodes().getLength() > 1)
							  {
								  NodeList bdCldList = cnd.getChildNodes().item(1).getChildNodes().item(1).getChildNodes();
								  
								  for (int bdClCnt=0; bdClCnt < bdCldList.getLength(); bdClCnt++)
								   {
									   Node bdChild = bdCldList.item(bdClCnt);
									   if((bdChild.getNodeName()).matches("Font|Sizemin|SizeIteration|Sizemax|Leadingmin|LeadingIteration|Leadingmax|HorizontalScale|verticalScale|TextFit"))
									   if(bdChild.getNodeType() == 1)
									   {
										   if (bdChild.hasChildNodes())
										   {
											   inc += 1;
											   privateElements += ":" + bdChild.getFirstChild().getNodeValue().trim();
											   inc += 1;
										   }
										   else
										   {
											   inc += 1;
											   privateElements += ":" + "None";
											   inc += 1;
										   } 
									   }
								   }
								  return (privateElements);
							  }
						  }
	
					  } 
	
				   }
				 }
			   }
			    
	      } catch (Exception ex) {
	         
	    	  log.error("No private elements " + ex.getMessage());
	    	  throw ex;
	    	
	      }
		return (null);
	   }

	   
	   public String GS1XmlParseAllElement(String xmlFilePath) throws Exception
	   {
		   XmlUtiility xmlUtl = new XmlUtiility();
			   try{
				   String prvElementsString= "";
				   Document dom = parseXmlFile(xmlFilePath);
				   String privateElements=""; 
				   
				   NodeList ldd= dom.getElementsByTagName("artworkContentPieceOfArt").item(0).getChildNodes();
				   for (int tmp=1; tmp < ldd.getLength(); tmp++)
				   {
					   
					   Node nds = ldd.item(tmp);
					   if(nds.getNodeType() == 1 && nds.getNodeName() == "artworkContentCopyElement")
					   { 
						   for(int cpyElInc=1; cpyElInc < nds.getChildNodes().getLength(); cpyElInc++)
						   {
							   String linkID = "";
							   String orgLinkID = "";
							   if(nds.getChildNodes().item(cpyElInc).getNodeName() == "copyElementTypeCode")
							   {
								   orgLinkID =  nds.getChildNodes().item(cpyElInc).getFirstChild().getTextContent();
								   linkID = (nds.getChildNodes().item(cpyElInc).getFirstChild().getTextContent()) + "_RULES";
								   int tmp1 = cpyElInc + 4;

								   if(nds.getChildNodes().item(tmp1).getNodeName() == "localeSequence") 
								   {
									   orgLinkID+=(nds.getChildNodes().item(tmp1).getFirstChild().getTextContent());
									   linkID+=(nds.getChildNodes().item(tmp1).getFirstChild().getTextContent());
								   }
								   if(nds.getChildNodes().item(tmp1-2).getNodeName() == "instanceSequence")
								   {
									   orgLinkID+=("-"+(nds.getChildNodes().item(tmp1-2).getFirstChild().getTextContent())+"...1");
								   	   linkID+=("-"+(nds.getChildNodes().item(tmp1-2).getFirstChild().getTextContent())+"...1");
								   }
								 // if(pvEl.equals(linkID))
								   {
									   privateElements = xmlUtl.parsePrivateAllElement(dom, linkID);
									   if(privateElements != null)
									   {
										   
										   prvElementsString += orgLinkID+privateElements+"\n";
									   }
									  // return privateElements;	
								   }
							   }
							 
							   
						   }
						  
					
					   }
					 
					   
				   }   

				   SplitBy(prvElementsString);
				   return privateElements;
			   }  
			   catch (Exception ex)
			   {
				   System.out.println("err " + ex.getMessage());
				   log.error(ex.getMessage());  
				   throw ex;
			   }
		  // return null;
	   }
	   
	   
	 
	   
	   public String GS1XmlParseElement(String xmlFilePath, String privateElementFromAI) throws Exception
	   {
		   XmlUtiility xmlUtl = new XmlUtiility();
			   try{
				   Document dom = parseXmlFile(xmlFilePath);
				   String privateElements="";
				   NodeList ldd= dom.getElementsByTagName("artworkContentPieceOfArt").item(0).getChildNodes();
				   for (int tmp=1; tmp < ldd.getLength(); tmp++)
				   {
					 String linkID = "";
					   Node nds = ldd.item(tmp);
					   if(nds.getNodeType() == 1 && nds.getNodeName() == "artworkContentCopyElement")
					   { 
						   for(int cpyElInc=1; cpyElInc < nds.getChildNodes().getLength(); cpyElInc++)
						   {
							  
							   if(nds.getChildNodes().item(cpyElInc).getNodeName() == "copyElementTypeCode")
							   {
								   linkID = nds.getChildNodes().item(cpyElInc).getFirstChild().getTextContent();
								   int tmp1 = cpyElInc + 4;

								   if(nds.getChildNodes().item(tmp1).getNodeName() == "localeSequence") 
									   linkID+=(nds.getChildNodes().item(tmp1).getFirstChild().getTextContent());
								   if(nds.getChildNodes().item(tmp1-2).getNodeName() == "instanceSequence")
									   linkID+=("-"+(nds.getChildNodes().item(tmp1-2).getFirstChild().getTextContent()));

								   String pvEl = (privateElementFromAI.split("::"))[1].toString();
								 // if(pvEl.equals(linkID))
								   {
									   privateElements += xmlUtl.parsePrivateElement(dom, linkID);
									  // return privateElements;	
								   }
							   } 
						   }
					   }
					   return privateElements;
					   
				   }   
				
			   }  
			   catch (Exception ex)
			   {
				   System.out.println("err " + ex.getMessage());
				   log.error(ex.getMessage());  
				   throw ex;
			   }

		   return null;
	   }
	   
	   
	   public String  getFileNameFromElement(String xmlFilePath)
	   {
		   try
		   {
			   Document dom = parseXmlFile(xmlFilePath);
			   NodeList nodePrivateList = dom.getElementsByTagName("privateElementTypeCode");
			   for(int eachNode = nodePrivateList.getLength() - 1; eachNode >= 0; eachNode--)
			   {
				   Node ldd = nodePrivateList.item(eachNode);
				   if(ldd.getTextContent().equalsIgnoreCase("FILE_NAME"))
				   {
					   Node nxtSibling = ldd.getNextSibling();
					   while (nxtSibling != null)
					   {
						   if(nxtSibling.getNodeName().equalsIgnoreCase("textContent"))
							   return nxtSibling.getTextContent().trim();
						   nxtSibling = nxtSibling.getNextSibling();
					   } 
				   }
			   }
		   }
		   catch (Exception ex)
		   {
			   MessageQueue.ERROR += "Issue on getting file name to save from xml \n";
		   }
		return null;
	   }
	   
	   public List<String> ParsePrivateElementSwatchColor(String xmlFilePath, String privateElmTypeCode, String filterBy) throws Exception
	   {
			List<String>swatchColorList = new ArrayList<String>();  
		   try{
			   String privateElementTypeCode = "";
			   
			   Document dom = parseXmlFile(xmlFilePath);
			   NodeList ld = dom.getElementsByTagName("privateElements").item(0).getChildNodes();
			   for (int tmp=1; tmp < ld.getLength(); tmp++)
			   {
				   Node nd = ld.item(tmp);
				   if(nd.getNodeType() == 1)
				   {
					  NodeList cld = nd.getChildNodes();
		
					  for(int eachChild=1; eachChild< cld.getLength(); eachChild++)
					  {
						  Node cnd = cld.item(eachChild);

						  	if(cnd.getNodeName() == "privateElementTypeCode")
							  privateElementTypeCode = cnd.getFirstChild().getNodeValue();
						  	if(cnd.getNodeType()==1 && cnd.getChildNodes().getLength() > 1)
							 {
								  NodeList bdCldList = cnd.getChildNodes().item(1).getChildNodes().item(1).getChildNodes();
								  for (int bdClCnt=0; bdClCnt < bdCldList.getLength(); bdClCnt++)
								   {
									   Node bdChild = bdCldList.item(bdClCnt);
									   if(bdChild.getNodeType() == 1)
									   {
										   if (bdChild.hasChildNodes() && privateElementTypeCode.equals(privateElmTypeCode))
										   {
											   if((bdChild.getFirstChild().getNodeValue()).contains(filterBy))
												   swatchColorList.add(bdChild.getFirstChild().getNodeValue());
										   }
									   }
									   
								   }
								 
							  }
						  
					  	}
				   }
			   } 
			 
	      } 
		   catch (Exception ex)
{
	         
	    	  log.error("No private elements " + ex.getMessage());
	    	  throw ex;
	    	
	      }
		//   System.out.println("gs1 parser");
		return swatchColorList;
	   }
	   
	   
		 public static void main(String[] args) throws Exception
		 {

		 }
}
