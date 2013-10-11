package edu.cmu.socialgen;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;



public class DataComManager implements Runnable{

	/* definitions */
	public static final int DATA_PORT = 15379;
	public static final int DATSKT_TIMEOUT = 1000; /* millisecond */
	public static final int BUF_SIZE = 1024;
	
	public static final byte DATA_PKT_TYPE_SELF = 0x60;	/* data from UI */
	
	public static final byte DATA_PKT_TYPE_TEXT = 0x71;
	public static final byte DATA_PKT_TYPE_VOICE = 0x72;
	public static final byte DATA_PKT_TYPE_VIDEO = 0x73;
	public static final byte DATA_PKT_TYPE_IMAGE = 0x74;
	
	public InetAddress WCARD_ADDR;
	public InetAddress BCAST_ADDR;
	
	public InetAddress localInetIpAddr;
	private DatagramSocket dataSocket;
	private WifiManager wifiMgr;
	public Handler userMsgHandler;
	
	
	public DataComManager(WifiManager wifiMgr) {
		this.wifiMgr = wifiMgr;
		int intIpAddr = wifiMgr.getConnectionInfo().getIpAddress();
		byte[] byteIpAddr = 
				ByteBuffer.allocate(4).order(ByteOrder.nativeOrder()).putInt(intIpAddr).array();
		
		/* socket setting */
		try {
			localInetIpAddr = InetAddress.getByAddress(byteIpAddr);
			Log.i("DataComSetting", "My IP:"+localInetIpAddr.toString());
			
			WCARD_ADDR = InetAddress.getByName("0.0.0.0");
			BCAST_ADDR = InetAddress.getByName("255.255.255.255");
			this.dataSocket = new DatagramSocket(DATA_PORT);
			this.dataSocket.setBroadcast(true);
			this.dataSocket.setSoTimeout(DATSKT_TIMEOUT);
		} catch (Exception e) {
			Log.i("DataComSetting", "Exception"+e);
		}

	}

	public void enterText(UserMsg uMsg) {
		StringBuffer msgStrBuf = new StringBuffer();
		msgStrBuf.append(String.format("%c", DATA_PKT_TYPE_SELF));
		msgStrBuf.append(String.format("%s",  uMsg.msgContent));
		byte[] msgByteArray = msgStrBuf.toString().getBytes();
		int msgLen = msgStrBuf.length();
		DatagramPacket pkt = new DatagramPacket(msgByteArray, msgLen, this.localInetIpAddr, DATA_PORT);
		try {
			this.dataSocket.send(pkt);
		} catch (Exception e) {
			Log.i("enterText", "Exception - "+e);
		}
		Log.i("enterText", "text msg sent to "+this.localInetIpAddr.toString()+" ("+uMsg.msgContent+")");
	}
	
	public void run() {
		byte[] rcvBuf = new byte[BUF_SIZE];
    	DatagramPacket rcvPkt = new DatagramPacket(rcvBuf, rcvBuf.length);
    	
    	/* enable the message looper */
    	/*
    	Looper.prepare();
    	userMsgHandler = new Handler() {
    		public void handleMessage(Message msg) {
    			UserMsg uMsg = (UserMsg)msg.obj;
    			Log.i("DataComThread", "text msg received from "+uMsg.userId+" ("+uMsg.msgContent+")");
    		}
    	};
    	Looper.loop();
    	*/
    	
    	/* WiFi multicast enable */
    	MulticastLock wifi_mc_lock =
    			this.wifiMgr.createMulticastLock("dcm_wifi_mc_lock");
    	wifi_mc_lock.acquire();
    	
    	while(true) {
    		
    		try {
    			this.dataSocket.receive(rcvPkt);
    		} catch(SocketTimeoutException toe) {
    			continue;
    		} catch (Exception e) {
    			Log.i("DataComReceiver", "Recv error - "+e);
    			continue;
    		}

			InetAddress senderAddr = rcvPkt.getAddress();
    		int senderPort = rcvPkt.getPort();
    		if (senderPort != DATA_PORT) {
    			/* skip if the packet is sent from other port */
    			continue;
    		}
    		
    		/* read data packet type */
    		byte dat_type = rcvBuf[0];
    		if (senderAddr.equals(this.localInetIpAddr)) {
    			if (dat_type == DATA_PKT_TYPE_SELF) {
    				Log.i("DataComReceiver", "CAPTURE!!");
    			}
    			else {
    				/* skip if the packet comes from itself and not from UI */
    				continue;
    			}
    		}
    		else {
	    		switch (dat_type) {
	    			case DATA_PKT_TYPE_TEXT:
	    				//Log.i("DataComReceiver", "Text message rcvd from <"+
	    				//		  senderAddr+">, UserID("+bp.userId+"), MacAddr("+bp.MacAddress+")");
	    				break;
	    				
	    			case DATA_PKT_TYPE_VOICE:
	    			case DATA_PKT_TYPE_VIDEO:
	    			case DATA_PKT_TYPE_IMAGE:
	    				/* FALL THROUGH */
	    			default:
	    				Log.i("DataComReceiver",
	    					  String.format("unexpected packet type(%c)", dat_type));
	    				break;
	    		}
    		}
    		
			//SystemClock.sleep(1000);
    	}
    	
    	/* WiFi multicast disable */
    	//wifi_mc_lock.release();
		
    	// return;
	}
	
	
}