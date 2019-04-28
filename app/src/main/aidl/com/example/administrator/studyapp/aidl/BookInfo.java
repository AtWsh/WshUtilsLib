package com.example.administrator.studyapp.aidl;

import android.os.Parcel;
import android.os.Parcelable;

public class BookInfo implements Parcelable{

    public String mBookName;

    public BookInfo() {}
    protected BookInfo(Parcel in) {
        mBookName = in.readString();
    }

    public static final Creator<BookInfo> CREATOR = new Creator<BookInfo>() {
        @Override
        public BookInfo createFromParcel(Parcel in) {
            return new BookInfo(in);
        }

        @Override
        public BookInfo[] newArray(int size) {
            return new BookInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(mBookName);
    }

    /**
     * 参数是一个Parcel,用它来存储与传输数据
     * @param dest
     */
    public void readFromParcel(Parcel dest) {
        //注意，此处的读值顺序应当是和writeToParcel()方法中一致的
        mBookName = dest.readString();
    }
}
