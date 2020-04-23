package Rcvr_AAMQ;

import java.io.IOException;
import java.lang.management.ManagementFactory;

import org.apache.log4j.Logger;

import Rcvr_AAMQ.Action;
import Rcvr_AAMQ.LogMQ;

public class HBTimer extends Thread 
{
	static Logger log = LogMQ.monitor("Rcvr_AAMQ.HBTimer");
	String ipAddress, locationKey, category;
	String pid = ManagementFactory.getRuntimeMXBean().getName();
	int delay;
	
	public HBTimer(String ip_Address, String location_Key, String category_Type, int delay_time) 
	{
		this.ipAddress = ip_Address;
		this.locationKey = location_Key;
		this.category = category_Type;
		this.delay = delay_time;
	}

	public void run() 
	{
		String ar = ("KILL -CONT "+ pid.split("@")[0]);
		while(true)
		{  
		    try
		    {
	    			Runtime.getRuntime().exec(ar);
		    		Action.UpdateClientMachineRunningStatus(this.ipAddress, this.locationKey, this.category);
		    		log.info(MessageQueue.WORK_ORDER + ": " + "Heart beat sent for IP : " + this.ipAddress + " location key : " + this.locationKey );
		    		Thread.sleep(this.delay);
		    }
			catch (IOException | InterruptedException ex) 
			{
				log.error(MessageQueue.WORK_ORDER + ": " + "HeartBeat Client machine update error : " + ex.getMessage());
			}
		}
	}

	public static void main(String[] args) 
	{
	    INetwork iNet = new INetwork();
	    HBTimer hb = new HBTimer(iNet.GetClientIPAddr(), "", "Wave Road Runner", 5000);
	    hb.start();
	    

	}

}
