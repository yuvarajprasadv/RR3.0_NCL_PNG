package Rcvr_AAMQ;

public class ThrowException {
	
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
		Action.UpdateErrorStatusWithRemark("14", "Roadrunner exits on error ");//RR error exit (14) status to Tornado API
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
		Action.UpdateErrorStatusWithRemark(id, "Roadrunner exits on error " + errorMsg);//RR error exit (14) status to Tornado API
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
		Action.UpdateErrorStatusWithRemark("14", "Roadrunner exits on error: " +  errorMsg.toString());//RR error exit (14) status to Tornado API
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
		Action.UpdateErrorStatusWithRemark(id, errorMsg.toString());
		MessageQueue.GATE = true;
		}
	}
	

}
