package com.xyzinnotech.ddn.network;

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
}
