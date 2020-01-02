package Rcvr_AAMQ;

import org.apache.log4j.Logger;

public class ThrowException {
	static Logger log = LogMQ.monitor("Rcvr_AAMQ.ThrowException");
	
	public static void CatchException(Exception exp) throws Exception
	{
		try
		{
			throw exp; 
		}
		finally
		{
		System.out.println(exp.getMessage());
		Action.sendStatusMsg((String) exp.getMessage());
		Action.sendRespStatusMsg("exit on error");
		SEng.OnError();
		//RR Error exit status
		Thread.sleep(5000);
		Action.UpdateErrorStatusWithRemark("14", "Roadrunner exits on error ");//RR error exit (14) status to Tornado API
		log.info("Sent error status id 14:" + "Roadrunner exits on error");
		MessageQueue.GATE = true;
		}
	}
	
	public static void CatchExceptionWithErrorMsgId(Exception exp, String errorMsg, String id) throws Exception
	{
		try
		{
			throw exp; 
		}
		finally
		{
		System.out.println(exp.getMessage());
		Action.sendStatusMsg((String) exp.getMessage());
		Action.sendRespStatusMsg("exit on error");
		SEng.OnError();
		//RR Error exit status
		Thread.sleep(5000);
		Action.UpdateErrorStatusWithRemark(id, "Roadrunner exits on error " + errorMsg);//RR error exit (14) status to Tornado API
		log.info("Sent error status id " + id + ": Roadrunner exits on error " + errorMsg.toString());
		MessageQueue.GATE = true;
		}
	}
	
	public static void CustomExit(Exception exp, String errorMsg) throws Exception
	{
		FileSystem fls = new FileSystem();
		try
		{
			throw exp; 
		}
		finally
		{
		System.out.println(errorMsg);
		fls.AppendFileString("Error :"+ errorMsg.toString()+"\n\n");
		Action.sendStatusMsg(errorMsg);
		Action.sendRespStatusMsg("exit on error");
		SEng.OnError();
		//RR Error exit status
		Thread.sleep(5000);
		Action.UpdateErrorStatusWithRemark("14", "Roadrunner exits on error: " +  errorMsg.toString());//RR error exit (14) status to Tornado API
		log.info("Sent error status id 14:" + "Roadrunner exits on error " + errorMsg.toString());
		MessageQueue.GATE = true;
		}
	}
	
	public static void CustomExitWithErrorMsgID(Exception exp, String errorMsg, String id) throws Exception
	{
		FileSystem fls = new FileSystem();
		try
		{
			throw exp; 
		}
		finally
		{
		System.out.println(errorMsg);
		fls.AppendFileString("Error :"+ errorMsg.toString()+"\n\n");
		Action.sendStatusMsg(errorMsg);
		Action.sendRespStatusMsg("exit on error");
		SEng.OnError();
		//RR Error exit status
		Thread.sleep(5000);
		Action.UpdateErrorStatusWithRemark(id, errorMsg.toString());
		log.info("Sent error status id "+ id +":" + errorMsg.toString());
		MessageQueue.GATE = true;
		}
	}
	

}
