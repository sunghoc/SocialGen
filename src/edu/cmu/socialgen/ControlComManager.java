package edu.cmu.socialgen;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.os.SystemClock;
import android.util.Log;


public class ControlComManager implements Runnable{
	
	/* definitions */
	public static final int CONTROL_PORT = 15369;

	public InetAddress WCARD_ADDR;
	public InetAddress BCAST_ADDR;
	private DatagramSocket controlSocket;
	
	public ControlComManager() throws SocketException, UnknownHostException, IOException {
		try {
			WCARD_ADDR = InetAddress.getByName("0.0.0.0");
			BCAST_ADDR = InetAddress.getByName("255.255.255.255");
			this.controlSocket = new DatagramSocket(CONTROL_PORT, WCARD_ADDR);
			this.controlSocket.setBroadcast(true);
		} catch (Exception e) {
			
		}
	}
	
	public void run() {
		try {
	    	Log.i("SenderTest", "thread run1!");
			/* beaconing test now */
			String msgStr = "I'm sending";
			int msgLen = msgStr.length();
			byte[] msg = msgStr.getBytes();
			DatagramPacket pkt = new DatagramPacket(msg, msgLen, BCAST_ADDR, CONTROL_PORT);
	    	Log.i("SenderTest", "thread run2!");
			while(true) {
		    	Log.i("SenderTest", "thread run3!");
				this.controlSocket.send(pkt);
				Log.i("SenderTest", "msg sent!");
				SystemClock.sleep(1000);
			}
			
		} catch (Exception e) {
			
		}
	}
	
}