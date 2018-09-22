package com.xyzinnotech.ddn.network.service;

import com.mapbox.geojson.Feature;
import com.xyzinnotech.ddn.network.model.Region;

import java.util.ArrayList;

import retrofit.Call;

public interface DDNAPIService {

    Call<ArrayList<Region>> getRegionsList();

    Call<ArrayList<Feature>> getFeaturesList(String projectId);
}
