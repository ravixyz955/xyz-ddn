package com.xyzinnotech.ddn.network.service;

import com.google.gson.JsonArray;
import com.mapbox.geojson.Feature;
import com.xyzinnotech.ddn.network.model.Region;

import java.util.ArrayList;

import retrofit.Call;
import retrofit2.http.Body;

public interface DDNAPIService {

    Call<ArrayList<Region>> getRegionsList();

    Call<ArrayList<Feature>> getFeaturesList(String projectId);

    Call<Void> putAddressList(@Body JsonArray requestBody);

    Call<Object> getDwellingList(String apikey);

    Call<Object> getDwelling(String ddnCompositeKey);

    Call<Void> getPutList(@Body JsonArray requestBody);
}
