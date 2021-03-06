package Rcvr_AAMQ;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.rabbitmq.client.*;

public class MessageQueue extends Action {
	  protected final static String EXCHANGE_NAME = "Region";
	  protected final static String EXCHANGE_TYPE = "topic";
	  protected final static String USER_NAME = "aaw";
	  protected final static String PASSWORD = "aaw";
	  protected final static String VHOST = "AAW";
	  
//157//	  protected final static String TORNADO_HOST = "https://172.28.42.157:8443/tornado_rr"; // JAVA NEW LIVE IP for PITAA[PNG] Only
//PRV-168//	  protected final static String TORNADO_HOST = "http://172.28.42.168:8080/tornado_rr"; // JAVA LIVE IP for PITAA[PNG] Only.
	  protected final static String TORNADO_HOST = "http://172.28.42.151:8082/tornado"; // JAVA DEV IP for PITAA [PNG] Only.
		  
//	  protected final static String TORNADO_HOST = "http://172.28.42.157:8080/tornado"; // JAVA LIVE IP
//	  protected final static String TORNADO_HOST = "https://172.28.42.157:8443/tornado"; // JAVA LIVE IP with Secure
//	  protected final static String TORNADO_HOST = "https://devtornado.schawk.com";	//Dev tornado
//	  protected final static String TORNADO_HOST = "http://172.28.42.151:8082/tornado"; //JAVA DEV Tornado 
	  
	  
	  public static boolean GATE = true;
	  public static String MSGID = "";
	  public static boolean STATUS = true;
	  public static String ERROR = "";
	  public static String VERSION = "";
	  public static String MESSAGE = "";
	  
	  //PDF-Config-Single
	  public static boolean sPdfNormal = false;
	  public static boolean sPdfPreset = false;
	  public static boolean sPdfNormalised = false;
	  
	  //PDF-Config-Multiple
	  public static boolean mPdfNormal = false;
	  public static boolean mPdfPreset = false;
	  public static boolean mPdfNormalised = false;
	  

//	 protected final static String HOST_IP = "10.52.8.31";			// local system
//	 protected final static String HOST_IP = "192.168.43.10";			// local system
//	 protected final static String HOST_IP = "172.28.42.158";			// LIVE
	 protected final static String HOST_IP =  "S2PTTRNMSGQ01P.asia.schawk.com"; //LIVE Dns
	 
	  static Logger log = LogMQ.monitor("Rcvr_AAMQ.MessageQueue");
	  
	  public static void RecvMessage(Channel channel, String queueName) throws Exception {
		  
		 if (GATE)
		 {
			MessageQueue.ERROR = "";
			STATUS = true;
			GATE = false;
			
			
		    Consumer consumer = new DefaultConsumer(channel) {
		        @Override
		        public void handleDelivery(String consumerTag, Envelope envelope,
		        AMQP.BasicProperties properties, byte[] body) throws IOException {
		          try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					log.error(e.getMessage());
				}
		          String message = new String(body, "UTF-8");
		          System.out.println(" [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
				  try {
					  MESSAGE = message;
					Action.acknowledge(message);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}
		        }
		      };
		      channel.basicConsume(queueName, true, consumer);
		 	}
		  }
	  
	  public static String getRouting(String strings){
		    return strings;
		  }

}
