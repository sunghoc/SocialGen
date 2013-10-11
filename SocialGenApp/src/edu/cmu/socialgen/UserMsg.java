package edu.cmu.socialgen;

import java.net.InetAddress;

public class UserMsg{
	public InetAddress sourceIpAddr;
	public String userId;
	public String realId;
	public String msgContent;
	
	public UserMsg(InetAddress srcIpAddr, String userId, String msg){
		this.sourceIpAddr = srcIpAddr;
		this.userId = userId;
		this.msgContent = msg;
	}
	
	public UserMsg(InetAddress srcIpAddr, String userId, String realId, String msg){
		this.sourceIpAddr = srcIpAddr;
		this.userId = userId;
		this.realId = realId;
		this.msgContent = msg;
	}
}