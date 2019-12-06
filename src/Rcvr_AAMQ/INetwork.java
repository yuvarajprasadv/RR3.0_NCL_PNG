package Rcvr_AAMQ;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

public class INetwork 
{
	static Logger log = LogMQ.monitor("Rcvr_AAMQ.INetwork");
	 public String GetClientIPAddr()
	 {
	        try {
	            InetAddress ipAddr = InetAddress.getLocalHost();
	            return (ipAddr.getHostAddress());
	        } catch (UnknownHostException ex) 
	        {
	        		log.error("Error on fetching client IP address");
	            ex.printStackTrace();
	        }
			return null;
	  }
	 public static void main(String[] args)
	 {
		 INetwork nt = new INetwork();
		// System.out.println(nt.GetClientIPAddr());
	 }

}