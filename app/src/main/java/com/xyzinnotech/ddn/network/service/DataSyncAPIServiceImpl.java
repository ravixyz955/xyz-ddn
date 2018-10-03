package com.xyzinnotech.ddn.network.service;

import com.xyzinnotech.ddn.network.RemoteServerAPI;


import okhttp3.RequestBody;
import retrofit.Call;

public class DataSyncAPIServiceImpl implements DataSyncAPIService {

    private final RemoteServerAPI remoteServerAPI;

    public DataSyncAPIServiceImpl(RemoteServerAPI remoteServerAPI) {
        this.remoteServerAPI = remoteServerAPI;
    }

    @Override
    public Call<Object> getDwellingList(String apikey) {
        return remoteServerAPI.getDwellingList(apikey);
    }

    @Override
    public Call<Void> getPutList(com.google.gson.JsonArray requestBody, String apikey) {
        return remoteServerAPI.getPutList(requestBody, apikey);
    }
}