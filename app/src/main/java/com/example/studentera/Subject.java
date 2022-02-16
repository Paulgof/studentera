package com.example.studentera;

import android.os.Parcel;
import android.os.Parcelable;

public class Subject implements Parcelable {
    private String mName;
    private String mMark;

    public Subject(String name, String mark) {
        mName = name;
        mMark = mark;
    }

    public Subject(Parcel parcel) {
        mName = parcel.readString();
        mMark = parcel.readString();
    }

    public static final Creator<Subject> CREATOR = new Creator<Subject>() {
        @Override
        public Subject createFromParcel(Parcel in) {
            return new Subject(in);
        }

        @Override
        public Subject[] newArray(int size) {
            return new Subject[size];
        }
    };

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmMark() {
        return mMark;
    }

    public void setmMark(String mMark) {
        this.mMark = mMark;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mName);
        parcel.writeString(mMark);
    }

    @Override
    public String toString() {
        return mName + ": " + mMark;
    }
}
