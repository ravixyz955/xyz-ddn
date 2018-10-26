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

    private String address;

    private String town;

    private String mandal;

    private String district;

    private String pincode;

    public Dwellinginfo() {
    }


    private Dwellinginfo(Parcel in) {
        ddn = in.readString();
        address = in.readString();
        town = in.readString();
        mandal = in.readString();
        district = in.readString();
        pincode = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ddn);
        dest.writeString(address);
        dest.writeString(town);
        dest.writeString(mandal);
        dest.writeString(district);
        dest.writeString(pincode);
    }

    public String getDdn() {
        return ddn;
    }

    public void setDdn(String ddn) {
        this.ddn = ddn;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getMandal() {
        return mandal;
    }

    public void setMandal(String mandal) {
        this.mandal = mandal;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }
}
