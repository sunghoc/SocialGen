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
import android.os.SystemClock;
import android.util.Log;



public class ControlComManager implements Runnable{
	
	/* definitions */
	public static final int CONTROL_PORT = 15369;
	public static final int CTLSKT_TIMEOUT = 1000; /* millisecond */
	public static final int BUF_SIZE = 1024;
	public static final String BEACON_TMR_TASK_NAME = "BEACON";
	public static final int BEACON_PERIOD = 1000; /* millisecond */
	
	public InetAddress WCARD_ADDR;
	public InetAddress BCAST_ADDR;
	public InetAddress localInetIpAddr;
	private DatagramSocket controlSocket;
	private WifiManager wifiMgr;
	
	public ControlComManager(WifiManager wifiMgr) {
		this.wifiMgr = wifiMgr;
		int intIpAddr = wifiMgr.getConnectionInfo().getIpAddress();
		byte[] byteIpAddr = ByteBuffer.allocate(4).order(ByteOrder.nativeOrder()).putInt(intIpAddr).array();
		
		/* socket setting */
		try {
			localInetIpAddr = InetAddress.getByAddress(byteIpAddr);
			Log.i("ControlComSetting", "My IP:"+localInetIpAddr.toString());
			
			WCARD_ADDR = InetAddress.getByName("0.0.0.0");
			BCAST_ADDR = InetAddress.getByName("255.255.255.255");
			//this.controlSocket = new DatagramSocket(CONTROL_PORT, localInetIpAddr);
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
			
			public BeaconTimerTask(DatagramSocket cs){
				inheritedCS = cs;
			}
			
			public void run(){
		    	/* beaconing test now */
				String msgStr = "I'm sending";
				int msgLen = msgStr.length();
				byte[] msg = msgStr.getBytes();
				DatagramPacket sndPkt = new DatagramPacket(msg, msgLen, BCAST_ADDR, CONTROL_PORT);
				try {
					this.inheritedCS.send(sndPkt);
				} catch (Exception e) {
					Log.i("BeaconModule", "Exception"+e);
				}
	    		Log.i("BeaconModule", "Msg sent!");
			}
		}
		beacon_timer.schedule(new BeaconTimerTask(this.controlSocket), BEACON_PERIOD, BEACON_PERIOD);
	}
	
	//public DatagramPacket 
	public void run() {
		try {
	    	
	    	byte[] rcvBuf = new byte[BUF_SIZE];
	    	DatagramPacket rcvPkt = new DatagramPacket(rcvBuf, rcvBuf.length);
	    	
	    	/* WiFi multicast enable */
	    	MulticastLock wifi_mc_lock = this.wifiMgr.createMulticastLock("ccm_wifi_mc_lock");
	    	wifi_mc_lock.acquire();
	    	
	    	while(true) {
	    		
	    		try {
	    			this.controlSocket.receive(rcvPkt);
	    		} catch (Exception e) {
	    			Log.i("ControlComReceiver", "Exception"+e);
	    			continue;
	    		}
	    		String rcvStr = new String(rcvPkt.getData(), 0, rcvPkt.getLength());
	    		InetAddress senderAddr = rcvPkt.getAddress();
	    		if (senderAddr.equals(this.localInetIpAddr)) {
	    			/* skip the packets sent by itself */
	    			continue;
	    		}
	    		int senderPort = rcvPkt.getPort();
	    		Log.i("ControlComReceiver", "Msg rcvd from <"+senderAddr+":"+senderPort+">, contents("+rcvStr+")");

				SystemClock.sleep(1000);
	    	}
	    	
	    	/* WiFi multicast disable */
	    	//wifi_mc_lock.release();

		} catch (Exception e) {
			Log.i("ControlComReceiver", "Exception"+e);
		}
	}
	

}