package edu.cmu.socialgen;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class DataComSender implements Runnable{

	/* definitions */
	public static final int DATA_PORT = 15379;
	public static final int DATSKT_TIMEOUT = 1000; /* millisecond */
	public static final int BUF_SIZE = 1024;
	public static final byte USERID_MAXLEN = 20;
	public static final byte REALID_MAXLEN = 6;
	
	public static final byte DATA_PKT_TYPE_TEXT = 0x71;
	public static final byte DATA_PKT_TYPE_VOICE = 0x72;
	public static final byte DATA_PKT_TYPE_VIDEO = 0x73;
	public static final byte DATA_PKT_TYPE_IMAGE = 0x74;
	
	public static final int TEXT_DATA_MAX_LEN = 250;
	
	public InetAddress WCARD_ADDR;
	public InetAddress BCAST_ADDR;
	public InetAddress localInetIpAddr;
	public static byte[] localMacAddr = new byte[6];
	private static DatagramSocket dataSocket;
	public static Handler userMsgHandler;
	
	
	public DataComSender(DataComReceiver DCM) {
		/* reuse socket of DataComReceiver */
		this.dataSocket = DCM.dataSocket;
		this.WCARD_ADDR = DCM.WCARD_ADDR;
		this.BCAST_ADDR = DCM.BCAST_ADDR;
		this.localInetIpAddr = DCM.localInetIpAddr;
		this.localMacAddr = DCM.localMacAddr;
	}
	
	public void run() {

    	/* enable the message looper */
    	Looper.prepare();
    	userMsgHandler = new Handler() {
    		public void handleMessage(Message msg) {
    			UserMsg uMsg = (UserMsg)msg.obj;
    			/* text message structure */
    			/* | type | user_id_len | user_id | real_id_len | real_id | msg_len | msg | */
    			/* |1 byte|   1 byte    |  var.   |   1 byte    | var.    | 1 byte  | var.| */
    			/* add type */
    			StringBuffer msgStrBuf = new StringBuffer();
    			msgStrBuf.append(String.format("%c", DATA_PKT_TYPE_TEXT));
    			/* add user id */
    			String userId = "ElecPig";
    			byte userid_len = (byte)Math.min(userId.length(), USERID_MAXLEN);
    			msgStrBuf.append(String.format("%c", userid_len));
    			msgStrBuf.append(userId.substring(0, userid_len));
    			/* add real id len */
    		    int real_id_pos = msgStrBuf.toString().getBytes().length + 1;
    			msgStrBuf.append(String.format("%c000000", REALID_MAXLEN));
    			/* add msg */
    			byte[] msgByteArray = null;
    			byte[] pktByteArray = null;
    			try{
    				msgByteArray = uMsg.msgContent.getBytes("UTF-8");
        			int msgByteLen = Math.min(msgByteArray.length, TEXT_DATA_MAX_LEN);
        			msgStrBuf.append(String.format("%c", (byte)msgByteLen));
        			msgStrBuf.append(new String(msgByteArray, 0, msgByteLen));
        			pktByteArray = msgStrBuf.toString().getBytes("UTF-8");
    			} catch (Exception e) {
    				Log.i("DataComSender", "Exception - "+e);
    			}
    			/* add real id */
    			for (int i = 0; i < 6; i++) {
    				pktByteArray[real_id_pos+i] = DataComSender.localMacAddr[i];
    			}
    			int pktLen = pktByteArray.length;
    			DatagramPacket pkt = new DatagramPacket(pktByteArray, pktLen, BCAST_ADDR, DATA_PORT);
    			try {
    				DataComSender.dataSocket.send(pkt);
    			} catch (Exception e) {
    				Log.i("DataComSender", "Exception - "+e);
    			}
    			
    			Log.i("DataComSender", "text msg ("+pktLen+" bytes) sent from "
    				  +uMsg.userId+" ("+uMsg.msgContent+")");
    		}
    	};
    	Looper.loop();
    		
    	return;
	}
	
	
}