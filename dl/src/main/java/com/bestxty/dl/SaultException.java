package com.bestxty.dl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author xty
 *         Created by xty on 2016/12/8.
 */
public class SaultException extends RuntimeException implements Parcelable {


    private String key;

    private String url;

    private String reason;

    public SaultException() {
    }

    public SaultException(String key, String url, String reason) {
        this.key = key;
        this.url = url;
        this.reason = reason;
    }


    public SaultException(String key, String url, Throwable throwable) {
        super(throwable);
        this.key = key;
        this.url = url;
    }


    public SaultException(String key, String url, String reason, Throwable throwable) {
        super(throwable);
        this.key = key;
        this.url = url;
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public String getKey() {
        return key;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String getMessage() {
        return "download failed for :" + key + "," + url + "," + reason;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeString(this.url);
        dest.writeString(this.reason);
    }


    @SuppressWarnings("WeakerAccess")
    public SaultException readFromParcel(Parcel in) {
        return new SaultException(in);
    }

    protected SaultException(Parcel in) {
        this.key = in.readString();
        this.url = in.readString();
        this.reason = in.readString();
    }


    public static final Creator<SaultException> CREATOR = new Creator<SaultException>() {
        @Override
        public SaultException createFromParcel(Parcel source) {
            return new SaultException(source);
        }

        @Override
        public SaultException[] newArray(int size) {
            return new SaultException[size];
        }
    };
}
