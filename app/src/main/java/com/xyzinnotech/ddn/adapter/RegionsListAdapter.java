package com.xyzinnotech.ddn.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xyzinnotech.ddn.DDNMapActivity;
import com.xyzinnotech.ddn.R;
import com.xyzinnotech.ddn.network.model.Region;

import java.util.ArrayList;

/**
 * Created by apple on 23/01/18.
 */

public class RegionsListAdapter extends RecyclerView.Adapter<RegionsListAdapter.RegionViewHolder>
        implements Filterable {

    private Context mContext;

    private ArrayList<Region> mRegions;

    private ArrayList<Region> mFilteredRegions;

    public RegionsListAdapter(Context mContext, ArrayList<Region> regions) {
        this.mContext = mContext;
        this.mRegions = regions;
        this.mFilteredRegions = mRegions;
    }

    @Override
    public RegionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.region_list_item_view, null);
        return new RegionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RegionViewHolder holder, int position) {
        Region mRegion = this.mFilteredRegions.get(position);
        holder.name.setText(mRegion.getName());
        Picasso.get().load(mRegion.getImage()).into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, DDNMapActivity.class);
                intent.putExtra(Region.class.getName(), mRegion);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.mFilteredRegions.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mFilteredRegions = mRegions;
                } else {
                    ArrayList<Region> filteredList = new ArrayList<>();
                    for (Region row : mRegions) {
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    mFilteredRegions = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredRegions;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredRegions = (ArrayList<Region>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class RegionViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        TextView name;
        ImageView image;

        RegionViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            name = itemView.findViewById(R.id.region_list_item_name);
            image = itemView.findViewById(R.id.region_list_item_image);
        }
    }
}
