package edu.cmu.socialgen;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

class WiFiNetworkService extends Service{
	
	public WiFiNetworkService() {
		
	}
	
	public IBinder onBind(Intent intent) {
		
		return null;
	}
	
	public int getLocalIpAddress() {
		int ipAddress;
		try {
			WifiManager wifiMan = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInf = wifiMan.getConnectionInfo();
			ipAddress = wifiInf.getIpAddress();
		} catch (Exception e) {
			ipAddress = 0;
			Log.i("Exception", "Exception in GetLocalIpAddress"+e);
		}
		return ipAddress;
	}
}


public class ControlComManager implements Runnable{
	
	/* definitions */
	public static final int CONTROL_PORT = 15369;
	public static final int CTLSKT_TIMEOUT = 1000; /* millisecond */
	public static final int BUF_SIZE = 1024;

	public InetAddress WCARD_ADDR;
	public InetAddress BCAST_ADDR;
	private DatagramSocket controlSocket;
	
	public ControlComManager() throws SocketException, UnknownHostException, IOException {
		WiFiNetworkService wifiNS = new WiFiNetworkService();
		Log.i("SendTest", "IP:"+wifiNS.getLocalIpAddress());

		try {
			WCARD_ADDR = InetAddress.getByName("0.0.0.0");
			BCAST_ADDR = InetAddress.getByName("255.255.255.255");
			this.controlSocket = new DatagramSocket(CONTROL_PORT, WCARD_ADDR);
			this.controlSocket.setBroadcast(true);
			this.controlSocket.setSoTimeout(CTLSKT_TIMEOUT);
		} catch (Exception e) {
			Log.i("Exception", "Exception"+e);
		}
	}
	
	public void run() {
		try {
	    	
	    	byte[] rcvBuf = new byte[BUF_SIZE];
	    	DatagramPacket rcvPkt = new DatagramPacket(rcvBuf, rcvBuf.length);
	    	
	    	/* beaconing test now */
			String msgStr = "I'm sending";
			int msgLen = msgStr.length();
			byte[] msg = msgStr.getBytes();
			DatagramPacket sndPkt = new DatagramPacket(msg, msgLen, BCAST_ADDR, CONTROL_PORT);
	    	Log.i("BeaconingTest", "start!");
	    	while(true) {
	    		try {
	    			this.controlSocket.receive(rcvPkt);
	    		} catch (SocketTimeoutException e) {
	    			Log.i("Exception", "timeout!");
	    			continue;
	    		}
	    		String rcvStr = new String(rcvPkt.getData(), 0, rcvPkt.getLength());
	    		InetAddress senderAddr = rcvPkt.getAddress();
	    		int senderPort = rcvPkt.getPort();
	    		Log.i("RcvTest", "Msg rcvd from <"+senderAddr+":"+senderPort+">, contents("+rcvStr+")");

				SystemClock.sleep(1000);
	    		
	    		this.controlSocket.send(sndPkt);
	    		Log.i("SndTest", "Msg sent!");
	    	}

		} catch (Exception e) {
			Log.i("Exception", "Exception"+e);
		}
	}
	

}