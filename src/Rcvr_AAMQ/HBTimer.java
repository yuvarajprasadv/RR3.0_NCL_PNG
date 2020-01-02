package Rcvr_AAMQ;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import Rcvr_AAMQ.Action;
import Rcvr_AAMQ.LogMQ;

public class HBTimer extends TimerTask 
{
	static Logger log = LogMQ.monitor("Rcvr_AAMQ.HBTimer");
	String ipAddress, locationKey, category;

	public HBTimer(String ip_Address, String location_Key, String category_Type) 
	{
		this.ipAddress = ip_Address;
		this.locationKey = location_Key;
		this.category = category_Type;
	}

	public void run() 
	{
		try 
		{
			Action.UpdateClientMachineRunningStatus(this.ipAddress, this.locationKey, this.category);
			log.info("Heart beat sent for IP : " + this.ipAddress + " location key : " + this.locationKey );
		}
		catch (IOException ex) 
		{
			log.error("HeartBeat Client machine update error : " + ex.getMessage());
		}
	}

	public static void main(String[] args) 
	{
	    INetwork iNet = new INetwork();
	    Timer timer = new Timer();
	    timer.schedule(new HBTimer(iNet.GetClientIPAddr(), "", "Wave Road Runner"), 3000, 300000);

	}

}
