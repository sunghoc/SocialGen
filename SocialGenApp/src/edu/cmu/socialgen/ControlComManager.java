package edu.cmu.socialgen;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.net.wifi.WifiInfo;
import android.os.SystemClock;
import android.util.Log;



public class ControlComManager implements Runnable{
	
	/* definitions */
	public static final int CONTROL_PORT = 15369;
	public static final int CTLSKT_TIMEOUT = 1000; /* millisecond */
	public static final int BUF_SIZE = 1024;

	public InetAddress WCARD_ADDR;
	public InetAddress BCAST_ADDR;
	public InetAddress localInetIpAddr;
	private DatagramSocket controlSocket;
	
	public ControlComManager(WifiInfo wifiInfo) {
		int intIpAddr = wifiInfo.getIpAddress();
		byte[] byteIpAddr = ByteBuffer.allocate(4).order(ByteOrder.nativeOrder()).putInt(intIpAddr).array();
	
		try {
			localInetIpAddr = InetAddress.getByAddress(byteIpAddr);
			Log.i("SendTest", "My IP:"+localInetIpAddr.toString());
			
			WCARD_ADDR = InetAddress.getByName("0.0.0.0");
			BCAST_ADDR = InetAddress.getByName("255.255.255.255");
			this.controlSocket = new DatagramSocket(CONTROL_PORT, localInetIpAddr);
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
	    		
	    		this.controlSocket.send(sndPkt);
	    		Log.i("SndTest", "Msg sent!");
	    		
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
	    	}

		} catch (Exception e) {
			Log.i("Exception", "Exception"+e);
		}
	}
	

}