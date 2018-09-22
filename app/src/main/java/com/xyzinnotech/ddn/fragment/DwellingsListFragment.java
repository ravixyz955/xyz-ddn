package com.xyzinnotech.ddn.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mapbox.geojson.Feature;
import com.xyzinnotech.ddn.R;
import com.xyzinnotech.ddn.adapter.DwellingsListAdapter;
import com.xyzinnotech.ddn.model.Dwelling;
import com.xyzinnotech.ddn.utils.ItemOffsetDecoration;

import java.util.ArrayList;

public class DwellingsListFragment extends Fragment {

    private FrameLayout mProgressBar;

    private RecyclerView mDwellingsRecycler;
    private DwellingsListAdapter dwellingsListAdapter;
    private Feature feature;

    public DwellingsListFragment() {

    }

    public static DwellingsListFragment newInstance(String param1, String param2) {
        return new DwellingsListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getActivity().getIntent().hasExtra("feature")) {
            feature = Feature.fromJson(getActivity().getIntent().getStringExtra("feature"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dwellings_list, container, false);
        mProgressBar = view.findViewById(R.id.progress_bar);
        mDwellingsRecycler = view.findViewById(R.id.dwellings_list_recycler);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mDwellingsRecycler.setLayoutManager(llm);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.fab_margin_bottom);
        mDwellingsRecycler.addItemDecoration(itemDecoration);

        mProgressBar.setVisibility(View.VISIBLE);
        ArrayList<Dwelling> dwellingsList = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            Dwelling dwelling = new Dwelling();
            dwelling.setOwnerName("Owner " + i + 1);
            dwellingsList.add(dwelling);
        }
        dwellingsListAdapter = new DwellingsListAdapter(getActivity(), dwellingsList);
        mDwellingsRecycler.setAdapter(dwellingsListAdapter);

        return view;
    }

    public void applyFilter(String query) {
        if(dwellingsListAdapter != null) {
            dwellingsListAdapter.getFilter().filter(query);
        }
    }
}
