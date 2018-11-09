package com.xyzinnotech.ddn.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Dwelling extends RealmObject implements Parcelable {

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

    @PrimaryKey
    private String CompositePrimaryKey;

    private long createdAt;

    private long updatedAt;

    private String ddn;

    private int offset;

    private String block;

    private String floor;

    private String flatNo;

    private String ownerName;

    private String ownerAadhar;

    private String dwellignType;

    private String assessmentNo;

    private String structuralType;

    private RealmList<String> amenities;

    public Dwelling() {
    }

    private Dwelling(Parcel in) {
        CompositePrimaryKey = in.readString();
        createdAt = in.readLong();
        updatedAt = in.readLong();
        ddn = in.readString();
        block = in.readString();
        floor = in.readString();
        flatNo = in.readString();
        ownerName = in.readString();
        ownerAadhar = in.readString();
        dwellignType = in.readString();
        assessmentNo = in.readString();
        structuralType = in.readString();
        amenities = new RealmList<>();
        amenities.addAll(in.createStringArrayList());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(CompositePrimaryKey);
        dest.writeLong(createdAt);
        dest.writeLong(updatedAt);
        dest.writeString(ddn);
        dest.writeString(block);
        dest.writeString(floor);
        dest.writeString(flatNo);
        dest.writeString(ownerName);
        dest.writeString(ownerAadhar);
        dest.writeString(dwellignType);
        dest.writeString(assessmentNo);
        dest.writeString(structuralType);
        dest.writeStringList(amenities);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDdn() {
        return ddn;
    }

    public void setDdn(String ddn) {
        this.ddn = ddn;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getFlatNo() {
        return flatNo;
    }

    public void setFlatNo(String flatNo) {
        this.flatNo = flatNo;
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

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
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

    public RealmList<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(RealmList<String> amenities) {
        this.amenities = amenities;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public String getCompositePrimaryKey() {
        return CompositePrimaryKey;
    }

    public void setCompositePrimaryKey(String compositePrimaryKey) {
        CompositePrimaryKey = compositePrimaryKey;
    }
}
