package com.xyzinnotech.ddn.network.service;

import com.mapbox.geojson.Feature;
import com.xyzinnotech.ddn.network.RemoteServerAPI;
import com.xyzinnotech.ddn.network.model.Region;

import java.util.ArrayList;

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
}
