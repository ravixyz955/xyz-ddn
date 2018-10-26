package com.xyzinnotech.ddn.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xyzinnotech.ddn.R;
import com.xyzinnotech.ddn.adapter.RegionsListAdapter;
import com.xyzinnotech.ddn.network.model.Region;
import com.xyzinnotech.ddn.network.service.DDNAPIService;
import com.xyzinnotech.ddn.utils.ItemOffsetDecoration;
import com.xyzinnotech.ddn.utils.NetworkUtils;

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
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class RegionsListFragment extends Fragment {

    private FrameLayout mProgressBar;

    private RecyclerView mRegionsRecycler;
    private RegionsListAdapter regionsListAdapter;
    private static final String TAG = "RegionListFragment";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String base64;
    private Bitmap bitmap;
    private static Toast t;
    private TextView offline_txt;
    private BroadcastReceiver networkStateReceiver;

    public RegionsListFragment() {
        // Required empty public constructor
    }

    public static RegionsListFragment newInstance(String param1, String param2) {
        return new RegionsListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        t = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
        t.getView().setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#009688")));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_regions_list, container, false);
        mProgressBar = view.findViewById(R.id.progress_bar);
        mRegionsRecycler = view.findViewById(R.id.regions_list_recycler);
        offline_txt = view.findViewById(R.id.offline_text);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRegionsRecycler.setLayoutManager(llm);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.fab_margin_bottom);
        mRegionsRecycler.addItemDecoration(itemDecoration);

        mProgressBar.setVisibility(VISIBLE);

        networkStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo ni = manager.getActiveNetworkInfo();
                if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {

                    offline_txt.setVisibility(View.GONE);
                    DDNAPIService ddnapiService = NetworkUtils.provideDDNAPIService(getActivity());
                    ddnapiService.getRegionsList().enqueue(new Callback<ArrayList<Region>>() {
                        @Override
                        public void onResponse(Response<ArrayList<Region>> response, Retrofit retrofit) {
                            mProgressBar.setVisibility(GONE);
                            if (response.isSuccess()) {
                                Log.i("TKTR", response.body().toString());
                                editor = pref.edit();
                                editor.clear().commit();
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

                    mProgressBar.setVisibility(GONE);
                    String jsonArray = pref.getString("jsonArray", null);
                    if (jsonArray == null) {
                        t.setText("No Offline Regions found");
                        t.show();
                    } else {
                        offline_txt.setVisibility(VISIBLE);
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<Region>>() {
                        }.getType();
                        ArrayList<Region> regions = gson.fromJson(jsonArray, type);
                        regionsListAdapter = new RegionsListAdapter(getActivity(), regions);
                        mRegionsRecycler.setAdapter(regionsListAdapter);
                    }
                }
            }
        };
        return view;
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
        JSONObject obj = null;
        JSONArray jsonArray = new JSONArray();
        editor = pref.edit();
        for (Region region : regions) {
            obj = new JSONObject();
            try {
                obj.put("name", region.getName());
                obj.put("tileset_id", region.getTilesetId());
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
                obj.put("image", imgStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            jsonArray.put(obj);
        }
        editor.putString("jsonArray", jsonArray.toString());
        editor.commit();
    }

    public void applyFilter(String query) {
        if (regionsListAdapter != null) {
            regionsListAdapter.getFilter().filter(query);
        }
    }
}