package com.example.studentera;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Student implements Parcelable {
    private String mFIO, mFaculty, mGroup;
    private ArrayList<Subject> mSubjects;

    public Student(String FIO, String faculty, String group) {
        mFIO = FIO;
        mFaculty = faculty;
        mGroup = group;
        mSubjects = new ArrayList<>();

    }

    public Student(Parcel in) {
        mFIO = in.readString();
        mFaculty = in.readString();
        mGroup = in.readString();
        mSubjects = in.createTypedArrayList(Subject.CREATOR);
    }

    public Student() {
        mFIO = "";
        mFaculty = "";
        mGroup = "";
        mSubjects = new ArrayList<>();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mFIO);
        parcel.writeString(this.mFaculty);
        parcel.writeString(this.mGroup);
        parcel.writeTypedList(mSubjects);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Student> CREATOR = new Creator<Student>() {

        @Override
        public Student createFromParcel(Parcel parcel) {
            return new Student(parcel);
        }

        @Override
        public Student[] newArray(int i) {
            return new Student[i];
        }
    };

    @Override
    public String toString() {
        return "Student{" +
                "ФИО:'" + mFIO + '\'' +
                ", факультет: '" + mFaculty + '\'' +
                ", группа: '" + mGroup + '\'' +
                ", subjects count: " + mSubjects.size() +
                '}';
    }

    public String getFIO() {
        return mFIO;
    }

    public void setFIO(String mFIO) {
        this.mFIO = mFIO;
    }

    public boolean isFioEmpty() {
        return mFIO == null || mFIO.isEmpty();
    }

    public String getFaculty() {
        return mFaculty;
    }

    public void setFaculty(String mFaculty) {
        this.mFaculty = mFaculty;
    }

    public boolean isFacultyEmpty() {
        return mFaculty == null || mFaculty.isEmpty();
    }

    public String getGroup() {
        return mGroup;
    }

    public void setGroup(String mGroup) {
        this.mGroup = mGroup;
    }

    public boolean isGroupEmpty() {
        return mGroup == null || mGroup.isEmpty();
    }

    public ArrayList<Subject> getmSubjects() {
        return mSubjects;
    }
}
