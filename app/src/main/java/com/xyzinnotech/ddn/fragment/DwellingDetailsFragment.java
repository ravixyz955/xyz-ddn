package com.xyzinnotech.ddn.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.xyzinnotech.ddn.R;
import com.xyzinnotech.ddn.SingleFragmentActivity;
import com.xyzinnotech.ddn.model.Dwelling;

import io.realm.Realm;


public class DwellingDetailsFragment extends Fragment {

    private Dwelling mDwelling;
    private String ddn;
    private Spinner block;
    private Spinner floor;
    private Spinner flat;
    private EditText ownerName;
    private EditText ownerAadhar;
    private EditText address;
    private EditText landmark;
    private EditText town;
    private EditText mandal;
    private EditText district;
    private EditText pincode;
    private Spinner dwellingType;
    private Spinner structuralType;
    private EditText assessmentNo;

    private Realm mRealm;

    public DwellingDetailsFragment() {
        // Required empty public constructor
    }

    public static DwellingDetailsFragment newInstance(String param1, String param2) {
        return new DwellingDetailsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity().getIntent().hasExtra(Dwelling.class.getName())) {
            mDwelling = getActivity().getIntent().getParcelableExtra(Dwelling.class.getName());
        }
        if (getActivity().getIntent().hasExtra("ddn")) {
            ddn = getActivity().getIntent().getStringExtra("ddn");
            setTitle(ddn);
            if (mDwelling == null) {
                mDwelling = new Dwelling();
                mDwelling.setDdn(ddn);
                mDwelling.setCreatedAt(System.currentTimeMillis());
            }
        }

        mRealm = Realm.getDefaultInstance();
    }

    private void setTitle(String title) {
        ((SingleFragmentActivity) getActivity()).setToolbarTitle(title);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_dwelling_details, container, false);
        block = inflate.findViewById(R.id.block);
        floor = inflate.findViewById(R.id.floor);
        flat = inflate.findViewById(R.id.flat);
        ownerName = inflate.findViewById(R.id.owner_name);
        ownerAadhar = inflate.findViewById(R.id.owner_aadhar);
        address = inflate.findViewById(R.id.full_address);
        landmark = inflate.findViewById(R.id.landmark);
        town = inflate.findViewById(R.id.town_name);
        mandal = inflate.findViewById(R.id.mandal_name);
        district = inflate.findViewById(R.id.district_name);
        pincode = inflate.findViewById(R.id.pincode);
        dwellingType = inflate.findViewById(R.id.dwelling_type);
        structuralType = inflate.findViewById(R.id.structural_type);
        assessmentNo = inflate.findViewById(R.id.assessment_number);

        FloatingActionButton fab = (FloatingActionButton) inflate.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDwelling();
            }
        });

        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mDwelling != null) {
            block.setSelection(getIndexOfItem(mDwelling.getBlock(), getResources().getStringArray(R.array.block)));
            floor.setSelection(getIndexOfItem(mDwelling.getFloor(), getResources().getStringArray(R.array.floor)));
            flat.setSelection(getIndexOfItem(mDwelling.getFlatNo(), getResources().getStringArray(R.array.flat)));
            ownerName.setText(mDwelling.getOwnerName());
            ownerAadhar.setText(mDwelling.getOwnerAadhar());
            address.setText(mDwelling.getAddress());
            landmark.setText(mDwelling.getLandmark());
            town.setText(mDwelling.getTown());
            mandal.setText(mDwelling.getMandal());
            district.setText(mDwelling.getDistrict());
            pincode.setText(mDwelling.getPincode());
            dwellingType.setSelection(getIndexOfItem(mDwelling.getDwellignType(), getResources().getStringArray(R.array.dwelling_type)));
            structuralType.setSelection(getIndexOfItem(mDwelling.getStructuralType(), getResources().getStringArray(R.array.structural_type)));
            assessmentNo.setText(mDwelling.getAssessmentNo());
        }
    }

    private void saveDwelling() {
        mDwelling.setBlock(block.getSelectedItem().toString());
        mDwelling.setFloor(floor.getSelectedItem().toString());
        mDwelling.setFlatNo(flat.getSelectedItem().toString());
        mDwelling.setOwnerName(ownerName.getText().toString());
        mDwelling.setOwnerAadhar(ownerAadhar.getText().toString());
        mDwelling.setAddress(address.getText().toString());
        mDwelling.setLandmark(landmark.getText().toString());
        mDwelling.setTown(town.getText().toString());
        mDwelling.setMandal(mandal.getText().toString());
        mDwelling.setDistrict(district.getText().toString());
        mDwelling.setPincode(pincode.getText().toString());
        mDwelling.setDwellignType(dwellingType.getSelectedItem().toString());
        mDwelling.setStructuralType(structuralType.getSelectedItem().toString());
        mDwelling.setAssessmentNo(assessmentNo.getText().toString());
        mDwelling.setUpdatedAt(System.currentTimeMillis());

        mRealm.beginTransaction();
        mRealm.insertOrUpdate(mDwelling);
        mRealm.commitTransaction();

        Toast.makeText(getActivity(), "Saved Successfully!", Toast.LENGTH_LONG).show();
        getActivity().finish();
    }

    private int getIndexOfItem(String item, String[] items) {
        for (int i = 0; i < items.length; i++) {
            if (items[i].equalsIgnoreCase(item)) {
                return i;
            }
        }
        return 0;
    }
}
