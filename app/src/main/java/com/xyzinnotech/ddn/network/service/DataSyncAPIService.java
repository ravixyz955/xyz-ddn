package com.xyzinnotech.ddn.network.service;

import com.google.gson.JsonArray;

import retrofit.Call;
import retrofit2.http.Body;

public interface DataSyncAPIService {

    Call<Object> getDwellingList(String apikey);

//        Call<Void> getPutList(@Body okhttp3.RequestBody requestBody, String apikey);
    Call<Void> getPutList(@Body JsonArray requestBody, String apikey);

}