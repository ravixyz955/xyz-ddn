package com.xyzinnotech.ddn.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Region implements Parcelable {

    public static final Creator<Region> CREATOR = new Creator<Region>() {
        @Override
        public Region createFromParcel(Parcel in) {
            return new Region(in);
        }

        @Override
        public Region[] newArray(int size) {
            return new Region[size];
        }
    };

    @SerializedName("name")
    private String name;

    @SerializedName("project_id")
    private String projectId;

    @SerializedName("tileset_id")
    private String tilesetId;

    @SerializedName("center")
    private double[] center;

    @SerializedName("image")
    private String image;

    @SerializedName("type")
    private String type;

    public Region() {
    }

    private Region(Parcel in) {
        name = in.readString();
        projectId = in.readString();
        tilesetId = in.readString();
        center = in.createDoubleArray();
        image = in.readString();
        type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(projectId);
        dest.writeString(tilesetId);
        dest.writeDoubleArray(center);
        dest.writeString(image);
        dest.writeString(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getTilesetId() {
        return tilesetId;
    }

    public void setTilesetId(String tilesetId) {
        this.tilesetId = tilesetId;
    }

    public double[] getCenter() {
        return center;
    }

    public void setCenter(double[] center) {
        this.center = center;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
