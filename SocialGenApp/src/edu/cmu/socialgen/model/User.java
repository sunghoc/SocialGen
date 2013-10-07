package edu.cmu.socialgen.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

	private String username;
	private int imgRes;
	
	public User(String username, int imgRes) {
		this.username = username;
		this.imgRes = imgRes;
	}
	
	public User(Parcel src) {
		this.username = src.readString();
		this.imgRes = src.readInt();
	}
	
	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		@Override
		public User createFromParcel(Parcel source) {
			return new User(source);
		}

		@Override
		public User[] newArray(int size) {
			return new User[size];
		}
	};

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getImgRes() {
		return imgRes;
	}

	public void setImgRes(int imgRes) {
		this.imgRes = imgRes;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(username);
		dest.writeInt(imgRes);
	}
	
}
