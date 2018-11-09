package com.xyzinnotech.ddn.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import android.os.Parcel;
import android.os.Parcelable;

public class Dwellinginfo extends RealmObject implements Parcelable {

    public static final Creator<Dwellinginfo> CREATOR = new Creator<Dwellinginfo>() {
        @Override
        public Dwellinginfo createFromParcel(Parcel in) {
            return new Dwellinginfo(in);
        }

        @Override
        public Dwellinginfo[] newArray(int size) {
            return new Dwellinginfo[size];
        }
    };

    @PrimaryKey
    private String ddn;

    private String fullAddress;

    private String landmark;

    private String villageName;

    private String mandalName;

    private String districtName;

    private int offset;

    private String pincode;

    public Dwellinginfo() {
    }


    private Dwellinginfo(Parcel in) {
        ddn = in.readString();
        fullAddress = in.readString();
        villageName = in.readString();
        mandalName = in.readString();
        districtName = in.readString();
        pincode = in.readString();
        landmark = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ddn);
        dest.writeString(fullAddress);
        dest.writeString(landmark);
        dest.writeString(villageName);
        dest.writeString(mandalName);
        dest.writeString(districtName);
        dest.writeString(pincode);
    }

    public String getDdn() {
        return ddn;
    }

    public void setDdn(String ddn) {
        this.ddn = ddn;
    }

    public String getfullAddress() {
        return fullAddress;
    }

    public void setfullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getvillageName() {
        return villageName;
    }

    public void setvillageName(String villageName) {
        this.villageName = villageName;
    }

    public String getmandalName() {
        return mandalName;
    }

    public void setmandalName(String mandalName) {
        this.mandalName = mandalName;
    }

    public String getdistrictName() {
        return districtName;
    }

    public void setdistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getpincode() {
        return pincode;
    }

    public void setpincode(String pincode) {
        this.pincode = pincode;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
