package com.xyzinnotech.ddn.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.xyzinnotech.ddn.R;
import com.xyzinnotech.ddn.adapter.RegionsListAdapter;
import com.xyzinnotech.ddn.network.model.Region;
import com.xyzinnotech.ddn.network.service.DDNAPIService;
import com.xyzinnotech.ddn.utils.ItemOffsetDecoration;
import com.xyzinnotech.ddn.utils.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class RegionsListFragment extends Fragment {

    private FrameLayout mProgressBar;

    private RecyclerView mRegionsRecycler;
    private RegionsListAdapter regionsListAdapter;

    public RegionsListFragment() {
        // Required empty public constructor
    }

    public static RegionsListFragment newInstance(String param1, String param2) {
        return new RegionsListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_regions_list, container, false);
        mProgressBar = view.findViewById(R.id.progress_bar);
        mRegionsRecycler = view.findViewById(R.id.regions_list_recycler);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRegionsRecycler.setLayoutManager(llm);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.fab_margin_bottom);
        mRegionsRecycler.addItemDecoration(itemDecoration);

        mProgressBar.setVisibility(View.VISIBLE);
        DDNAPIService ddnapiService = NetworkUtils.provideDDNAPIService(getActivity());
        ddnapiService.getRegionsList().enqueue(new Callback<ArrayList<Region>>() {
            @Override
            public void onResponse(Response<ArrayList<Region>> response, Retrofit retrofit) {
                mProgressBar.setVisibility(View.GONE);
                if (response.isSuccess()) {
                    Log.i("TKTR", response.body().toString());
                    regionsListAdapter = new RegionsListAdapter(getActivity(), response.body());
                    mRegionsRecycler.setAdapter(regionsListAdapter);
                } else {
                    try {
                        Log.i("TKTR", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
        return view;
    }

    public void applyFilter(String query) {
        if (regionsListAdapter != null) {
            regionsListAdapter.getFilter().filter(query);
        }
    }
}
