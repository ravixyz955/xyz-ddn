package com.xyzinnotech.ddn.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xyzinnotech.ddn.R;
import com.xyzinnotech.ddn.adapter.DwellingsListAdapter;
import com.xyzinnotech.ddn.model.Dwelling;
import com.xyzinnotech.ddn.utils.ItemOffsetDecoration;

import io.realm.Realm;
import io.realm.RealmResults;

public class DwellingsListFragment extends Fragment {

    private static final String ARG_DDN = "ddn";
    private FrameLayout mProgressBar;
    private TextView dwellingsEmptyMsg;
    private RecyclerView mDwellingsRecycler;
    private DwellingsListAdapter dwellingsListAdapter;
    private String ddn;
    private Realm mRealm;

    public DwellingsListFragment() {

    }

    public static DwellingsListFragment newInstance() {
        return new DwellingsListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity().getIntent().hasExtra(ARG_DDN)) {
            ddn = getActivity().getIntent().getStringExtra(ARG_DDN);
            getActivity().setTitle(ddn);
        }
        mRealm = Realm.getDefaultInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dwellings_list, container, false);
        mProgressBar = view.findViewById(R.id.progress_bar);
        dwellingsEmptyMsg = view.findViewById(R.id.dwelling_list_empty_msg);
        mDwellingsRecycler = view.findViewById(R.id.dwellings_list_recycler);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mDwellingsRecycler.setLayoutManager(llm);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.list_margin);
        mDwellingsRecycler.addItemDecoration(itemDecoration);

        mProgressBar.setVisibility(View.VISIBLE);
        RealmResults<Dwelling> dwellingsList = mRealm.where(Dwelling.class).equalTo("ddn", ddn).findAll();
        if (dwellingsList.isEmpty()) {
            dwellingsEmptyMsg.setVisibility(View.VISIBLE);
        } else {
            dwellingsEmptyMsg.setVisibility(View.GONE);
        }
        dwellingsListAdapter = new DwellingsListAdapter(dwellingsList, getActivity(), ddn);
        mDwellingsRecycler.setAdapter(dwellingsListAdapter);
        mProgressBar.setVisibility(View.GONE);

        return view;
    }

    public void applyFilter(String query) {
        if (dwellingsListAdapter != null) {
            dwellingsListAdapter.getFilter().filter(query);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.ddn_map, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (getActivity() != null) {
                getActivity().finish();
            }
            return true;
        } else if (id == R.id.action_download_map) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
