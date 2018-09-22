package com.xyzinnotech.ddn.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.xyzinnotech.ddn.DDNMapActivity;
import com.xyzinnotech.ddn.R;
import com.xyzinnotech.ddn.model.Dwelling;

import java.util.ArrayList;

/**
 * Created by apple on 23/01/18.
 */

public class DwellingsListAdapter extends RecyclerView.Adapter<DwellingsListAdapter.RegionViewHolder>
        implements Filterable {

    private Context mContext;

    private ArrayList<Dwelling> mDwellings;

    private ArrayList<Dwelling> mFilteredDwellings;

    public DwellingsListAdapter(Context mContext, ArrayList<Dwelling> regions) {
        this.mContext = mContext;
        this.mDwellings = regions;
        this.mFilteredDwellings = mDwellings;
    }

    @Override
    public RegionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dwelling_list_item_view, null);
        return new RegionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RegionViewHolder holder, int position) {
        Dwelling mDwelling = this.mFilteredDwellings.get(position);
        holder.name.setText(mDwelling.getOwnerName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, DDNMapActivity.class);
                intent.putExtra(Dwelling.class.getName(), mDwelling);
//                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.mFilteredDwellings.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mFilteredDwellings = mDwellings;
                } else {
                    ArrayList<Dwelling> filteredList = new ArrayList<>();
                    for (Dwelling row : mDwellings) {
                        if (row.getOwnerName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    mFilteredDwellings = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredDwellings;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredDwellings = (ArrayList<Dwelling>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class RegionViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        TextView name;

        RegionViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            name = itemView.findViewById(R.id.dwelling_list_item_name);
        }
    }
}
