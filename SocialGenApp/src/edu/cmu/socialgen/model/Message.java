package edu.cmu.socialgen.model;

public class Message {

	private String message;
	private boolean isMine;
	private boolean isStatusMessage;
	
	public Message(String message, boolean isMine) {
		this.message = message;
		this.isMine = isMine;
		this.isStatusMessage = false;
	}
	
	public Message(boolean isStatusMessage, String message) {
		this.message = message;
		this.isMine = false;
		this.isStatusMessage = isStatusMessage;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isMine() {
		return isMine;
	}

	public void setMine(boolean isMine) {
		this.isMine = isMine;
	}

	public boolean isStatusMessage() {
		return isStatusMessage;
	}

	public void setStatusMessage(boolean isStatusMessage) {
		this.isStatusMessage = isStatusMessage;
	}
	
}
