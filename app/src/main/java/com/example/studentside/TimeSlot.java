package com.example.studentside;

public class TimeSlot {
    private String mId;
    private String mTime;
    private String mName;
    private Boolean mDone;
    private Boolean mBook;

    public TimeSlot(String time, String name, Boolean done) {
        mTime = time;
        mName = name;
        mDone = done;

    }

    public TimeSlot(){
        mTime = null;
        mName = "";
        mDone = false;
        mBook = false;
    }

    public Boolean getBook() {
        return mBook;
    }

    public void setBook(Boolean book) {
        mBook = book;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Boolean getDone() {
        return mDone;
    }

    public void setDone(Boolean done) {
        mDone = done;
    }
}
