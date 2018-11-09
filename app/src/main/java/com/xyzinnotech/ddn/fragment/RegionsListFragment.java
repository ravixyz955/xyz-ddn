package com.xyzinnotech.ddn.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xyzinnotech.ddn.R;
import com.xyzinnotech.ddn.adapter.RegionsListAdapter;
import com.xyzinnotech.ddn.network.model.Region;
import com.xyzinnotech.ddn.network.service.DDNAPIService;
import com.xyzinnotech.ddn.utils.ItemOffsetDecoration;
import com.xyzinnotech.ddn.utils.NetworkUtils;
import com.xyzinnotech.ddn.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class RegionsListFragment extends Fragment {

    private FrameLayout mProgressBar;

    private RecyclerView mRegionsRecycler;
    private RegionsListAdapter regionsListAdapter;
    private static final String TAG = "RegionListFragment";
    private String base64;
    private Bitmap bitmap;
    private TextView offline_txt;
    private TextView offline_region_txt;
    private BroadcastReceiver networkStateReceiver;
    private ArrayList<Region> regions;
    private Utils prefSharedUtils;

    public RegionsListFragment() {
        // Required empty public constructor
    }

    public static RegionsListFragment newInstance(String param1, String param2) {
        return new RegionsListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefSharedUtils = Utils.init(getActivity(), "MyPref");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_regions_list, container, false);
        mProgressBar = view.findViewById(R.id.progress_bar);
        mRegionsRecycler = view.findViewById(R.id.regions_list_recycler);
        offline_txt = view.findViewById(R.id.offline_text);
        offline_region_txt = view.findViewById(R.id.offline_region_text);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRegionsRecycler.setLayoutManager(llm);
        regionsListAdapter = new RegionsListAdapter(getActivity(), regions);
        mRegionsRecycler.setAdapter(regionsListAdapter);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.fab_margin_bottom);
        mRegionsRecycler.addItemDecoration(itemDecoration);

        mProgressBar.setVisibility(VISIBLE);

        if (!NetworkUtils.isConnectingToInternet(getActivity())) {
            offlineRegionList();
        }
        networkStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo ni = manager.getActiveNetworkInfo();
                if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {

                    offline_txt.setVisibility(View.GONE);
                    offline_region_txt.setVisibility(GONE);
                    DDNAPIService ddnapiService = NetworkUtils.provideDDNAPIService(getActivity());
                    ddnapiService.getRegionsList().enqueue(new Callback<ArrayList<Region>>() {
                        @Override
                        public void onResponse(Response<ArrayList<Region>> response, Retrofit retrofit) {
                            mProgressBar.setVisibility(GONE);
                            if (response.isSuccess()) {
                                prefSharedUtils.clear();
                                saveForOffline(response.body());
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

                } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                    offlineRegionList();
                }
            }
        };
        return view;
    }

    private void offlineRegionList() {
        mProgressBar.setVisibility(GONE);
        String regions_str = prefSharedUtils.get("regionsArray");
        if (regions_str == null) {
            Utils.showToast(getActivity(), "No Offline Regions found");
        } else {
            offline_txt.setVisibility(VISIBLE);
            Gson gson = new Gson();
            Type type = new TypeToken<List<Region>>() {
            }.getType();
            ArrayList<Region> regions = gson.fromJson(regions_str, type);
            if (regions.size() > 0) {
                offline_region_txt.setVisibility(GONE);
            }
            regionsListAdapter = new RegionsListAdapter(getActivity(), regions);
            mRegionsRecycler.setAdapter(regionsListAdapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(networkStateReceiver);
    }

    private void saveForOffline(ArrayList<Region> regions) {
        JSONObject regionObj = null;
        JSONArray offlineRegionsArray = new JSONArray();
        for (Region region : regions) {
            regionObj = new JSONObject();
            try {
                regionObj.put("name", region.getName());
                regionObj.put("tileset_id", region.getTilesetId());
                String imgStr = new AsyncTask<String, Void, String>() {

                    @Override
                    protected String doInBackground(String... params) {

                        try {
                            URL url = new URL(params[0]);
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setDoInput(true);
                            try {
                                connection.connect();
                            } catch (Exception e) {
                                Log.d("Exception", "getStringImg: " + e.getMessage());
                            }
                            InputStream is = connection.getInputStream();
                            byte[] buffer = new byte[1024];
                            int read = 0;
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            while ((read = is.read(buffer, 0, buffer.length)) != -1) {
                                baos.write(buffer, 0, read);
                            }
                            baos.flush();
                            base64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
                        } catch (Exception e) {
                            Log.d("Error", e.toString());
                        }
                        return base64;
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        if (bitmap != null && !bitmap.isRecycled()) {
                            bitmap.recycle();
                            bitmap = null;
                        }
                    }
                }.execute(region.getImage()).get();
                regionObj.put("image", imgStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            offlineRegionsArray.put(regionObj);
        }
        prefSharedUtils.put("regionsArray", offlineRegionsArray.toString());
        prefSharedUtils.finish();
    }

    public void applyFilter(String query) {
        if (regionsListAdapter != null) {
            regionsListAdapter.getFilter().filter(query);
        }
    }
}