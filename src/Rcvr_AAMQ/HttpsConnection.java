package Rcvr_AAMQ;

import javax.net.ssl.HostnameVerifier; 
import javax.net.ssl.HttpsURLConnection; 
import javax.net.ssl.SSLContext; 
import javax.net.ssl.SSLSession; 
import javax.net.ssl.SSLSocketFactory; 
import javax.net.ssl.TrustManager; 
import javax.net.ssl.X509TrustManager;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom; 
import java.security.cert.CertificateException; 
import java.security.cert.X509Certificate; 

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
 
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

public class HttpsConnection 
{
   private HostnameVerifier mDefaultHostnameVerifier = null; 
   private SSLSocketFactory mDefaultSSLSocketFactory = null;
   static Logger log = LogMQ.monitor("Rcvr_AAMQ.HttpsConnection");

   private final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() 
   { 
	    public boolean verify(String hostname, SSLSession session) 
	    { 
	        return true; 
	    } 
   }; 
	
   public String excutePost(String targetURL, String urlParameters) 
   {
	   System.setProperty ("jsse.enableSNIExtension", "false");
	   HttpsURLConnection connection = null;

	   try 
	   {
		   trustAllHosts();
		   URL url = new URL(targetURL);
		   connection = (HttpsURLConnection)url.openConnection();
		     
		   connection.setRequestMethod("POST");
		    
		   connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded"); 
		   connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;Windows98;DigExt)"); 
			
		   connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
		   connection.setRequestProperty("Content-Language", "en-US");  
		    
		   connection.setDoOutput(true); 
		   connection.setDoInput(true);

		   try
		   {
			    DataOutputStream wr = new DataOutputStream (
			    connection.getOutputStream());
			    wr.writeBytes(urlParameters);
			    wr.close();
		   }
		   catch(Exception ex)
		   {
			   log.error("Report failed to send " + ex.getMessage());
			   System.out.println("Report failed to send");
		   }
		   
		   InputStream is = connection.getInputStream();
		   BufferedReader rd = new BufferedReader(new InputStreamReader(is));		
		    
		   StringBuilder response = new StringBuilder(); 
		   String line;
		   while((line = rd.readLine()) != null) 
		   {
		      response.append(line);
		      response.append('\r');
		   }
		   rd.close();
		   return response.toString();
		}
		catch (Exception e) 
		{
		    e.printStackTrace();
			log.error("Error at execute post: " + e.getMessage());
		    return null;
		}
		finally
		{
		    if(connection != null)
		    {
		      connection.disconnect(); 
		    }
		}  
	}
   
   	private void trustAllHosts() 
   	{ 
   		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() { 
	        @Override 
	        public X509Certificate[] getAcceptedIssuers() { 
	            return new X509Certificate[] {}; 
	        } 

	        @Override 
	        public void checkClientTrusted(X509Certificate[] chain, 
	                String authType) throws CertificateException { 
	        } 

	        @Override 
	        public void checkServerTrusted(X509Certificate[] chain, 
	                String authType) throws CertificateException { 
	        } 
	    } }; 

	    // all-trusting TrustManager 
	    try 
	    { 
	        mDefaultSSLSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory(); 
	        //TrustManager 
	        SSLContext sc = SSLContext.getInstance("TLS"); 
	        sc.init(null, trustAllCerts, new SecureRandom()); 
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory()); 
	    }
	    catch (Exception e) 
	    { 
	    	log.error("Error at Connection trust: " + e.getMessage());
	    } 
	}

   public HttpURLConnection getURLConnection(URL url, boolean trustEveryone) throws IOException {
	   try
	   {
		System.setProperty ("jsse.enableSNIExtension", "false");
	    HttpURLConnection conn = null;
	    if (url.getProtocol().toLowerCase().equals("https")) {
	        // for HTTPS
	        if (!trustEveryone) {
	            conn = (HttpsURLConnection) url.openConnection();
	        }
	        else {
	            trustAllHosts();
	            HttpsURLConnection https = (HttpsURLConnection) url.openConnection();

	            mDefaultHostnameVerifier = https.getHostnameVerifier();
	            https.setHostnameVerifier(DO_NOT_VERIFY);
	            conn = https;
	        }
	    }
	    // for HTTP
	    else {
	        conn = (HttpURLConnection) url.openConnection();
	    }
	    return conn;
	   }
	   catch(Exception ex)
	   {
		   log.error("Error at HttpsUrConnection: " + ex.getMessage());
		   return null;
	   }
	   
	}

   
   public String excuteHttpJsonPost(String targetURL, String autoartwork_mq_id, String autoartwork_overall_status) 
   {
	  
	   JSONObject user=new JSONObject();
	   try
	   {
	   user.put("autoartwork_mq_id", autoartwork_mq_id);
	   user.put("autoartwork_overall_status", autoartwork_overall_status);
	   }
	   catch (Exception ex)
	   {
		   log.error("Exception on sendign report to tornado");
		   return "exception on report sending to tornado\n";
	   }
	   String jsonData=user.toString();
	   HttpPostReq httpPostReq=new HttpPostReq();
	   HttpPost httpPost=httpPostReq.createConnectivity(targetURL , "myusername", "mypassword");
	   return httpPostReq.executeReq( jsonData, httpPost);
   }
   
   public String excuteErrorStatusHttpJsonPost(String targetURL, String autoartwork_mq_id, String autoartwork_overall_status) 
   {
	  
	   JSONObject user=new JSONObject();
	   try
	   {
	   user.put("mqid", autoartwork_mq_id);
	   user.put("status", autoartwork_overall_status);
	   }
	   catch (Exception ex)
	   {
		   log.error("Exception on report sending to tornado");
		   return "exception on report sending to tornado\n";
	   }
	   String jsonData=user.toString();
	   HttpPostReq httpPostReq=new HttpPostReq();
	   HttpPost httpPost=httpPostReq.createConnectivity(targetURL , "myusername", "mypassword");
	   return httpPostReq.executeReq( jsonData, httpPost);
   }
   
   public String excuteClientMachineStatusHttpJsonPost(String targetURL, String machine_ip, String location_key, String category) 
   {
	  
	   JSONObject user=new JSONObject();
	   try
	   {
		   user.put("ip", machine_ip);
		   user.put("key", location_key);
		   user.put("category", category);
	   }
	   catch (Exception ex)
	   {
		   log.error("Exception on report sending to tornado: " + ex.getMessage());
		   return "exception on report sending to tornado\n";
	   }
	   String jsonData=user.toString();
	   HttpPostReq httpPostReq=new HttpPostReq();
	   HttpPost httpPost=httpPostReq.createConnectivity(targetURL , "myusername", "mypassword");
	   return httpPostReq.executeReq( jsonData, httpPost);
   }
   
   public String excuteErrorStatusHttpJsonPostWithRemark(String targetURL, String autoartwork_mq_id, String autoartwork_overall_status, String autoartwork_remarks) 
   {
	  
	   JSONObject user=new JSONObject();
	   try
	   {
	   user.put("mqid", autoartwork_mq_id);
	   user.put("status", autoartwork_overall_status);
	   user.put("remark", autoartwork_remarks);
	   }
	   catch (Exception ex)
	   {
		   log.error("Exception on report sending: " + ex.getMessage());
		   return "exception on report sending to tornado\n";
	   }
	   String jsonData=user.toString();
	   HttpPostReq httpPostReq=new HttpPostReq();
	   HttpPost httpPost=httpPostReq.createConnectivity(targetURL , "myusername", "mypassword");
	   return httpPostReq.executeReq( jsonData, httpPost);
   }
   
   public static void main(String args[]) throws JSONException
   {
	   String restUrl = MessageQueue.TORNADO_HOST + "/rest/pub/aaw/finalreport";
    	   String username="myusername";
       String password="mypassword";
       JSONObject user=new JSONObject();
       user.put("autoartwork_mq_id", "2c4805cb-042f-4944-b995-7e612fddab7f");
       user.put("autoartwork_overall_status", "Test from RR");
       String jsonData=user.toString();
       HttpPostReq httpPostReq=new HttpPostReq();
       HttpPost httpPost=httpPostReq.createConnectivity(restUrl , username, password);
       httpPostReq.executeReq( jsonData, httpPost);
   }
    
}



 
class HttpPostReq
{

	static Logger log = LogMQ.monitor("Rcvr_AAMQ.HttpPostReq");
    HttpPost createConnectivity(String restUrl, String username, String password)
    {
        HttpPost post = new HttpPost(restUrl);
        String auth=new StringBuffer(username).append(":").append(password).toString();
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);
        post.setHeader("AUTHORIZATION", authHeader);
        post.setHeader("Content-Type", "application/json");
        post.setHeader("Accept", "application/json");
        post.setHeader("X-Stream" , "true");
        return post;
    }
     
    String executeReq(String jsonData, HttpPost httpPost)
    {
        try{
            return executeHttpRequest(jsonData, httpPost);
        }
        catch (UnsupportedEncodingException e)
        {
        	log.error("Error encoding api url: " + e.getMessage());
        	return ("error while encoding api url : " + e.getMessage());
        }
        catch (IOException e){
        	log.error("IO exception while sending http request: " + e.getMessage());
        	return ("ioException occured while sending http request : " + e.getMessage());
        }
        catch(Exception e){
        	log.error("Exception while sending http request: " + e.getMessage());
        	return ("exception occured while sending http request : " + e.getMessage());
        }
        finally{
            httpPost.releaseConnection();
        }
    }
     
    String executeHttpRequest(String jsonData,  HttpPost httpPost)  throws UnsupportedEncodingException, IOException
    {
        HttpResponse response=null;
        String line = "";
        StringBuffer result = new StringBuffer();
        httpPost.setEntity(new StringEntity(jsonData));
        HttpClient client = HttpClientBuilder.create().build();
        response = client.execute(httpPost);

        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        while ((line = reader.readLine()) != null){ result.append(line); }
        
        return result.toString();
    }
}
