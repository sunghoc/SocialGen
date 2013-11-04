package edu.cmu.socialgen;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.StringTokenizer;

import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Handler;
import android.util.Log;



public class DataComReceiver implements Runnable{

	/* definitions */
	public static final int DATA_PORT = 15379;
	public static final int DATSKT_TIMEOUT = 1000; /* millisecond */
	public static final int BUF_SIZE = 1024;
	
	public static final byte DATA_PKT_TYPE_TEXT = 0x71;
	public static final byte DATA_PKT_TYPE_VOICE = 0x72;
	public static final byte DATA_PKT_TYPE_VIDEO = 0x73;
	public static final byte DATA_PKT_TYPE_IMAGE = 0x74;
	
	public InetAddress WCARD_ADDR;
	public InetAddress BCAST_ADDR;
	
	public InetAddress localInetIpAddr;
	public byte[] localMacAddr = new byte[6];
	public DatagramSocket dataSocket;
	public WifiManager wifiMgr;
	public Handler userMsgHandler;
	
	
	public DataComReceiver(WifiManager wifiMgr) {
		this.wifiMgr = wifiMgr;
		int intIpAddr = wifiMgr.getConnectionInfo().getIpAddress();
		byte[] byteIpAddr = 
				ByteBuffer.allocate(4).order(ByteOrder.nativeOrder()).putInt(intIpAddr).array();
		String macStr = wifiMgr.getConnectionInfo().getMacAddress();
		StringTokenizer macStrTok = new StringTokenizer(macStr, ":");
		for (int i = 0; i < 6; i++) {
			this.localMacAddr[i] = Integer.valueOf(macStrTok.nextToken(), 16).byteValue();
		}
		
		/* socket setting */
		try {
			localInetIpAddr = InetAddress.getByAddress(byteIpAddr);
			Log.i("DataComReceiver setting", "My IP:"+localInetIpAddr.toString());
			
			WCARD_ADDR = InetAddress.getByName("0.0.0.0");
			BCAST_ADDR = InetAddress.getByName("255.255.255.255");
			this.dataSocket = new DatagramSocket(DATA_PORT);
			this.dataSocket.setBroadcast(true);
			this.dataSocket.setSoTimeout(DATSKT_TIMEOUT);
		} catch (Exception e) {
			Log.i("DataComReceiver setting", "Exception"+e);
		}

	}
	
	public void run() {
		byte[] rcvBuf = new byte[BUF_SIZE];
    	DatagramPacket rcvPkt = new DatagramPacket(rcvBuf, rcvBuf.length);
    	
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
				/* skip if the packet comes from itself */
				continue;
    		}
    		else {
	    		switch (dat_type) {
	    			case DATA_PKT_TYPE_TEXT:
	    				/* text message structure */
	        			/* | type | user_id_len | user_id | real_id_len | real_id | msg_len | msg | */
	        			/* |1 byte|   1 byte    |  var.   |   1 byte    | var.    | 1 byte  | var.| */
	    				int pos = 1;
	    				byte user_id_len = rcvBuf[pos++];
	    				String user_id = new String(rcvBuf, pos, user_id_len);
	    				pos = pos + user_id_len;
	    				byte real_id_len = rcvBuf[pos++];
	    				byte[] real_id = new byte[real_id_len];
	    				for(int i=0; i<real_id_len; i++) {
	    					real_id[i] = rcvBuf[pos++];
	    				}
	    				byte msg_len = rcvBuf[pos++];
	    				String msg = new String(rcvBuf, pos, msg_len);
	    				Log.i("DataComReceiver", "Text message rcvd from <"+
	    					  senderAddr+">, UserID("+user_id+"), RealId("+
	    					  String.format("%x:%x:%x:%x:%x:%x", real_id[0], real_id[1], real_id[2],
	    							  real_id[3], real_id[4], real_id[5])+"), msg["+msg+"]");
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