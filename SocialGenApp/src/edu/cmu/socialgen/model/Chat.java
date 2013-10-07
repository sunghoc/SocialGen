package edu.cmu.socialgen.model;

import java.text.DateFormat;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class Chat implements Parcelable {

	private String chatTitle;
	private int imgRes;
	private int numUsers;
	private Date lastActiveDate;
	
	public Chat(String chatTitle, int imgRes) {
		this.chatTitle = chatTitle;
		this.imgRes = imgRes;
		this.numUsers = 1;
		this.lastActiveDate = new Date();
	}
	
	public Chat(Parcel src) {
		this.chatTitle = src.readString();
		this.imgRes = src.readInt();
		this.numUsers = src.readInt();
		this.lastActiveDate = (Date) src.readSerializable();
	}
	
	public static final Parcelable.Creator<Chat> CREATOR = new Parcelable.Creator<Chat>() {
		@Override
		public Chat createFromParcel(Parcel source) {
			return new Chat(source);
		}

		@Override
		public Chat[] newArray(int size) {
			return new Chat[size];
		}
	};

	public String getChatTitle() {
		return chatTitle;
	}

	public void setChatTitle(String chatTitle) {
		this.chatTitle = chatTitle;
	}

	public int getImgRes() {
		return imgRes;
	}

	public void setImgRes(int imgRes) {
		this.imgRes = imgRes;
	}

	public int getNumUsers() {
		return numUsers;
	}

	public void setNumUsers(int numUsers) {
		this.numUsers = numUsers;
	}

	public Date getLastActiveDate() {
		return lastActiveDate;
	}

	public void setLastActiveDate(Date lastActiveDate) {
		this.lastActiveDate = lastActiveDate;
	}
	
	public String getFormattedLastActiveDate() {
		return DateFormat.getDateInstance(DateFormat.SHORT).format(lastActiveDate);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(chatTitle);
		dest.writeInt(imgRes);
		dest.writeInt(numUsers);
		dest.writeSerializable(lastActiveDate);
	}
	
}
