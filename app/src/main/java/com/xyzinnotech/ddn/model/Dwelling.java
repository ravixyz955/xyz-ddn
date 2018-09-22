package com.xyzinnotech.ddn.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

public class Dwelling implements Parcelable {

    public static final Creator<Dwelling> CREATOR = new Creator<Dwelling>() {
        @Override
        public Dwelling createFromParcel(Parcel in) {
            return new Dwelling(in);
        }

        @Override
        public Dwelling[] newArray(int size) {
            return new Dwelling[size];
        }
    };

    private String ownerName;

    private String ownerAadhar;

    private String address;

    private String landmark;

    private String town;

    private String mandal;

    private String district;

    private String pincode;

    private String dwellignType;

    private String assessmentNo;

    private String structuralType;

    private String emenities;

    public Dwelling() {
    }

    private Dwelling(Parcel in) {
        ownerName = in.readString();
        ownerAadhar = in.readString();
        address = in.readString();
        landmark = in.readString();
        town = in.readString();
        mandal = in.readString();
        district = in.readString();
        pincode = in.readString();
        dwellignType = in.readString();
        assessmentNo = in.readString();
        structuralType = in.readString();
        emenities = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ownerName);
        dest.writeString(ownerAadhar);
        dest.writeString(address);
        dest.writeString(landmark);
        dest.writeString(town);
        dest.writeString(mandal);
        dest.writeString(district);
        dest.writeString(pincode);
        dest.writeString(dwellignType);
        dest.writeString(assessmentNo);
        dest.writeString(structuralType);
        dest.writeString(emenities);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerAadhar() {
        return ownerAadhar;
    }

    public void setOwnerAadhar(String ownerAadhar) {
        this.ownerAadhar = ownerAadhar;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
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

    public String getDwellignType() {
        return dwellignType;
    }

    public void setDwellignType(String dwellignType) {
        this.dwellignType = dwellignType;
    }

    public String getAssessmentNo() {
        return assessmentNo;
    }

    public void setAssessmentNo(String assessmentNo) {
        this.assessmentNo = assessmentNo;
    }

    public String getStructuralType() {
        return structuralType;
    }

    public void setStructuralType(String structuralType) {
        this.structuralType = structuralType;
    }

    public String getEmenities() {
        return emenities;
    }

    public void setEmenities(String emenities) {
        this.emenities = emenities;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
