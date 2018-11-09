package com.xyzinnotech.ddn.network;

import com.google.gson.JsonArray;
import com.mapbox.geojson.Feature;
import com.squareup.okhttp.RequestBody;
import com.xyzinnotech.ddn.network.model.ActivateUserRequest;
import com.xyzinnotech.ddn.network.model.AuthenticateUserRequest;
import com.xyzinnotech.ddn.network.model.Region;
import com.xyzinnotech.ddn.network.model.RegisterUserRequest;
import com.xyzinnotech.ddn.network.model.User;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by KT on 23/12/15.
 */
public interface RemoteServerAPI {

    String BASE_CONTEXT = "/api";

    @Multipart
    @POST(BASE_CONTEXT + "/users/image")
    Call<Void> uploadImage(@Header("x-auth-token") String fcmId, @Part("file") RequestBody request);

    @POST(BASE_CONTEXT + "/register")
    Call<User> registerUser(@Body RegisterUserRequest request);

    @POST(BASE_CONTEXT + "/authenticate")
    Call<User> authenticate(@Body AuthenticateUserRequest request);

    @POST("/activate")
    Call<Void> activateUser(@Body ActivateUserRequest request);

    @GET("https://s3.ap-south-1.amazonaws.com/xyz-projects/{project}/ddn.json")
    Call<ArrayList<Feature>> getFeaturesList(@Path("project") String projectId);

    @GET("https://s3.ap-south-1.amazonaws.com/xyz-config/ddn.json")
    Call<ArrayList<Region>> loadRegionsList();

    @GET("https://api.mlab.com/api/1/databases/xyz-ddn/collections/dwellings")
    Call<Object> getDwellingList(@Query("apiKey") String apiKey);

    @POST(BASE_CONTEXT + "/dwellings")
    Call<Void> getPutList(@Body com.google.gson.JsonArray requestBody);

    @POST(BASE_CONTEXT + "/projects/5b878744d49b6489989d2946/update_ddn_address")
    Call<Void> putAddressList(@Body JsonArray requestBody);

    @GET(BASE_CONTEXT + "/dwellings/{CompositePrimaryKey}")
    Call<Object> getDwelling(@Path("CompositePrimaryKey") String ddnCompositeKey);

}