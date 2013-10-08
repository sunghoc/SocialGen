package edu.cmu.socialgen;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.util.Log;
//import android.os.SystemClock;



public class ControlComManager implements Runnable{
	
	/* definitions */
	public static final int CONTROL_PORT = 15369;
	public static final int CTLSKT_TIMEOUT = 1000; /* millisecond */
	public static final int BUF_SIZE = 1024;
	public static final String BEACON_TMR_TASK_NAME = "BEACON";
	public static final int BEACON_PERIOD = 3000; /* millisecond */
	
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
		String MacStr = wifiMgr.getConnectionInfo().getMacAddress();
		
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
			public byte[] localMacAddr = new byte[6];
			
			public BeaconTimerTask(DatagramSocket cs, String macStr){
				inheritedCS = cs;

				StringTokenizer macStrTok = new StringTokenizer(macStr, ":");
				for (int i = 0; i < 6; i++) {
					this.localMacAddr[i] = Integer.valueOf(macStrTok.nextToken(), 16).byteValue();
				}
			}
			
			public void run(){
				DatagramPacket sndPkt = createBeacon("ElecPig", this.localMacAddr);
				try {
					this.inheritedCS.send(sndPkt);
				} catch (Exception e) {
					Log.i("BeaconModule", "Exception"+e);
				}
	    		Log.d("BeaconModule", "Beacon sent!");
			}
		}
		beacon_timer.schedule(new BeaconTimerTask(this.controlSocket, MacStr),
							  BEACON_PERIOD, BEACON_PERIOD);
	}
	
	public DatagramPacket createBeacon(String userId, byte[] realId) {
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
		msgStrBuf.append(String.format("%c%c000000", BEACON_IE_TYPE_REALID, BEACON_IE_REALID_MAXLEN));
		byte[] msgByteArray = msgStrBuf.toString().getBytes();
		int msgLen = msgStrBuf.length();
		for (int i = 0; i < 6; i++) {
			msgByteArray[msgLen - 6 + i] = realId[i];
		}

		/* packet generation */
		DatagramPacket beacon =	new DatagramPacket(msgByteArray, msgLen, BCAST_ADDR, CONTROL_PORT);
		return beacon;
	}
	
	class BeaconParser {
		public String userId;
		public String MacAddress;

		public BeaconParser(byte[] rcvBuf, int length, InetAddress senderAddr) {
			int pointer = 1;
			int ie_len;
			boolean malformed = false;
			
			while (pointer < length) {
				switch (rcvBuf[pointer]) {
					case BEACON_IE_TYPE_USERID:
						ie_len = rcvBuf[++pointer];
						if (ie_len + pointer > length) malformed = true;
						this.userId = new String(rcvBuf, ++pointer, ie_len);
						pointer = pointer + ie_len;
						break;

					case BEACON_IE_TYPE_REALID:
						ie_len = rcvBuf[++pointer];
						if (ie_len + pointer > length) malformed = true;
						this.MacAddress =
								String.format("%x:%x:%x:%x:%x:%x", 
											  rcvBuf[++pointer], rcvBuf[++pointer], rcvBuf[++pointer],
											  rcvBuf[++pointer], rcvBuf[++pointer], rcvBuf[++pointer]);
						pointer = pointer + ie_len;
						break;
						
					default:
						malformed = true;
						break;
				}
				if (malformed == true) {
					/* received packet is malformed */
					Log.i("BeaconParser", "malfored packet");
					break;
				}
			}
			
			return;
		}
	}
	
	public void run() {
	    	
    	byte[] rcvBuf = new byte[BUF_SIZE];
    	DatagramPacket rcvPkt = new DatagramPacket(rcvBuf, rcvBuf.length);
    	
    	/* WiFi multicast enable */
    	MulticastLock wifi_mc_lock =
    			this.wifiMgr.createMulticastLock("ccm_wifi_mc_lock");
    	wifi_mc_lock.acquire();
    	
    	while(true) {
    		
    		try {
    			this.controlSocket.receive(rcvPkt);
    		} catch(SocketTimeoutException toe) {
    			continue;
    		} catch (Exception e) {
    			Log.i("ControlComReceiver", "Recv error - "+e);
    			continue;
    		}
    		
			InetAddress senderAddr = rcvPkt.getAddress();
    		int senderPort = rcvPkt.getPort();
    		if (senderAddr.equals(this.localInetIpAddr) || (senderPort != CONTROL_PORT)) {
    			/* skip if the packet is sent by itself or comes from other port */
    			continue;
    		}
    		
    		/* read control packet type */
    		byte ctl_type = rcvBuf[0];
    		switch (ctl_type) {
    			case CONTROL_PKT_TYPE_BEACON:
    				BeaconParser bp = new BeaconParser(rcvBuf, rcvPkt.getLength(), senderAddr);
    				Log.i("ControlComReceiver", "Beacon rcvd from <"+
    						  senderAddr+">, UserID("+bp.userId+"), MacAddr("+bp.MacAddress+")");
    				break;
    				
    			default:
    				Log.i("ControlComReceiver",
    					  String.format("unexpected packet type(%c)", ctl_type));
    		}

			//SystemClock.sleep(1000);
    	}
    	
    	/* WiFi multicast disable */
    	//wifi_mc_lock.release();
		
    	// return;
	}

}