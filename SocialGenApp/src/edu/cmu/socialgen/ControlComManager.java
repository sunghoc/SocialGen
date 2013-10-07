package edu.cmu.socialgen;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Timer;
import java.util.TimerTask;

import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
//import android.os.SystemClock;
import android.util.Log;



public class ControlComManager implements Runnable{
	
	/* definitions */
	public static final int CONTROL_PORT = 15369;
	public static final int CTLSKT_TIMEOUT = 1000; /* millisecond */
	public static final int BUF_SIZE = 1024;
	public static final String BEACON_TMR_TASK_NAME = "BEACON";
	public static final int BEACON_PERIOD = 1000; /* millisecond */
	
	public static final byte CONTROL_PKT_TYPE_BEACON = 0x01;
	public static final byte BEACON_IE_TYPE_USERID = 0x11;
	public static final byte BEACON_IE_USERID_MAXLEN = 20;
	public static final byte BEACON_IE_TYPE_REALID = 0x12;
	public static final byte BEACON_IE_REALID_MAXLEN = 6;

	public InetAddress WCARD_ADDR;
	public InetAddress BCAST_ADDR;
	public InetAddress localInetIpAddr;
	private DatagramSocket controlSocket;
	private WifiManager wifiMgr;
	
	public ControlComManager(WifiManager wifiMgr) {
		this.wifiMgr = wifiMgr;
		int intIpAddr = wifiMgr.getConnectionInfo().getIpAddress();
		byte[] byteIpAddr = 
				ByteBuffer.allocate(4).order(ByteOrder.nativeOrder()).putInt(intIpAddr).array();
		String localMacAddr = wifiMgr.getConnectionInfo().getMacAddress();
		
		/* socket setting */
		try {
			localInetIpAddr = InetAddress.getByAddress(byteIpAddr);
			Log.i("ControlComSetting", "My IP:"+localInetIpAddr.toString());
			
			WCARD_ADDR = InetAddress.getByName("0.0.0.0");
			BCAST_ADDR = InetAddress.getByName("255.255.255.255");
			this.controlSocket = new DatagramSocket(CONTROL_PORT);
			this.controlSocket.setBroadcast(true);
			this.controlSocket.setSoTimeout(CTLSKT_TIMEOUT);
		} catch (Exception e) {
			Log.i("ControlComSetting", "Exception"+e);
		}
		
		/* beacon task setting */
		Timer beacon_timer = new Timer(BEACON_TMR_TASK_NAME);
		class BeaconTimerTask extends TimerTask{
			public DatagramSocket inheritedCS;
			public String localMacAddr;
			
			public BeaconTimerTask(DatagramSocket cs, String mac_addr){
				inheritedCS = cs;
				localMacAddr = mac_addr;
			}
			
			public void run(){
				DatagramPacket sndPkt = createBeacon("ElecPig", this.localMacAddr);
				try {
					this.inheritedCS.send(sndPkt);
				} catch (Exception e) {
					Log.i("BeaconModule", "Exception"+e);
				}
	    		Log.i("BeaconModule", "Msg sent!");
			}
		}
		beacon_timer.schedule(new BeaconTimerTask(this.controlSocket, localMacAddr),
							  BEACON_PERIOD, BEACON_PERIOD);
	}
	
	public DatagramPacket createBeacon(String userId, String realId) {
		/* Currently MAC address will be used for the realID.
		 * Later, we can think of other unique device id ad a real ID.
		 */
		StringBuffer msgStrBuf = new StringBuffer();
		msgStrBuf.append(String.format("%c", CONTROL_PKT_TYPE_BEACON));
		/* add user id TLV */
		byte userid_len = (byte)Math.min(userId.length(), BEACON_IE_USERID_MAXLEN);
		msgStrBuf.append(String.format("%c%c", BEACON_IE_TYPE_USERID, userid_len));
		msgStrBuf.append(userId.substring(0, userid_len));
		/* add real id TLV */
		byte realid_len = (byte)Math.min(realId.length(), BEACON_IE_REALID_MAXLEN);
		msgStrBuf.append(String.format("%c%c", BEACON_IE_TYPE_REALID, realid_len));
		msgStrBuf.append(realId.substring(0, realid_len));
		
		/* packet generation */
		DatagramPacket beacon =
				new DatagramPacket(msgStrBuf.toString().getBytes(),
								   msgStrBuf.length(),
								   BCAST_ADDR, CONTROL_PORT);
		return beacon;
	}
	
	public void parseBeacon(byte[] rcvBuf, DatagramPacket rcvPkt,
							InetAddress senderAddr, int senderPort) {
		String rcvStr = new String(rcvPkt.getData(), 0, rcvPkt.getLength());
		Log.i("ControlComReceiver", "Beacon rcvd from <"+
			  senderAddr+":"+senderPort+">, contents("+rcvStr+")");
		
		return;
	}
	
	public void run() {
		try {
	    	
	    	byte[] rcvBuf = new byte[BUF_SIZE];
	    	DatagramPacket rcvPkt = new DatagramPacket(rcvBuf, rcvBuf.length);
	    	
	    	/* WiFi multicast enable */
	    	MulticastLock wifi_mc_lock =
	    			this.wifiMgr.createMulticastLock("ccm_wifi_mc_lock");
	    	wifi_mc_lock.acquire();
	    	
	    	while(true) {
	    		
	    		try {
	    			this.controlSocket.receive(rcvPkt);
	    		} catch (Exception e) {
	    			Log.i("ControlComReceiver", "Exception"+e);
	    			continue;
	    		}
	    		
    			InetAddress senderAddr = rcvPkt.getAddress();
	    		int senderPort = rcvPkt.getPort();
	    		if (senderAddr.equals(this.localInetIpAddr)) {
	    			/* skip the packets sent by itself */
	    			continue;
	    		}
	    		
	    		/* read control packet type */
	    		byte ctl_type = rcvBuf[0];
	    		switch (ctl_type) {
	    			case CONTROL_PKT_TYPE_BEACON:
	    				parseBeacon(rcvBuf, rcvPkt, senderAddr, senderPort);
	    				break;
	    				
	    			default:
	    				Log.i("ControlComReceiver",
	    					  String.format("unexpected packet type(%c)", ctl_type));
	    		}

				//SystemClock.sleep(1000);
	    	}
	    	
	    	/* WiFi multicast disable */
	    	//wifi_mc_lock.release();

		} catch (Exception e) {
			Log.i("ControlComReceiver", "Exception"+e);
		}
		
		return;
	}

}