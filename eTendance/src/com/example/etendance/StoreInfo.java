package com.example.etendance;

import android.os.Parcel;
import android.os.Parcelable;


public class StoreInfo implements Parcelable{
	public StoreInfo(String id, String name) {
		this.id=id;
		this.name=name;
	}
	public StoreInfo(Parcel in) {
	    this.id=in.readString();
	    this.name=in.readString();
	            // and all other elements
	}
	String id;
	String name;
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(id);
		dest.writeString(name);
		
	}
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() 
	{
	    @Override
		public StoreInfo createFromParcel(Parcel in) { return new StoreInfo(in); }
	    @Override
		public StoreInfo[] newArray(int size) { return new StoreInfo[size]; }
	};
}