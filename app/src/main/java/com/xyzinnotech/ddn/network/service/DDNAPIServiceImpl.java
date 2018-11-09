package com.xyzinnotech.ddn.network.service;

import com.google.gson.JsonArray;
import com.mapbox.geojson.Feature;
import com.xyzinnotech.ddn.model.Dwellinginfo;
import com.xyzinnotech.ddn.network.RemoteServerAPI;
import com.xyzinnotech.ddn.network.model.Region;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;

public class DDNAPIServiceImpl implements DDNAPIService {

    private final RemoteServerAPI remoteServerAPI;

    public DDNAPIServiceImpl(RemoteServerAPI remoteServerAPI) {
        this.remoteServerAPI = remoteServerAPI;
    }

    @Override
    public Call<ArrayList<Region>> getRegionsList() {
        return remoteServerAPI.loadRegionsList();
    }

    @Override
    public Call<ArrayList<Feature>> getFeaturesList(String projectId) {
        return remoteServerAPI.getFeaturesList(projectId);
    }

    @Override
    public Call<Void> putAddressList(JsonArray requestBody) {
        return remoteServerAPI.putAddressList(requestBody);
    }

    @Override
    public Call<Object> getDwellingList(String apikey) {
        return remoteServerAPI.getDwellingList(apikey);
    }

    @Override
    public Call<Object> getDwelling(String ddnCompositeKey) {
        return remoteServerAPI.getDwelling(ddnCompositeKey);
    }

    @Override
    public Call<Void> getPutList(com.google.gson.JsonArray requestBody) {
        return remoteServerAPI.getPutList(requestBody);
    }
}