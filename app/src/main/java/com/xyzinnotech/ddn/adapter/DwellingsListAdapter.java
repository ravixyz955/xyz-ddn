package com.xyzinnotech.ddn.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.xyzinnotech.ddn.R;
import com.xyzinnotech.ddn.SingleFragmentActivity;
import com.xyzinnotech.ddn.model.Dwelling;
import java.text.SimpleDateFormat;
import java.util.Date;
import io.realm.OrderedRealmCollection;
import io.realm.RealmList;
import io.realm.RealmRecyclerViewAdapter;

import static com.xyzinnotech.ddn.SingleFragmentActivity.KEY_SINGLE_FRAGMENT;

/**
 * Created by apple on 23/01/18.
 */

public class DwellingsListAdapter extends RealmRecyclerViewAdapter<Dwelling, DwellingsListAdapter.DwellingViewHolder>
        implements Filterable {

    private Context mContext;

    private String ddn;

    private OrderedRealmCollection<Dwelling> mDwellings;

    private OrderedRealmCollection<Dwelling> mFilteredDwellings;

    public DwellingsListAdapter(@Nullable OrderedRealmCollection<Dwelling> data, Context mContext, String ddn) {
        super(data, true);
        this.mDwellings = data;
        this.mFilteredDwellings = data;
        this.mContext = mContext;
        this.ddn = ddn;
    }

    @NonNull
    @Override
    public DwellingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dwelling_list_item_view, null);
        return new DwellingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DwellingViewHolder holder, int position) {
        Dwelling mDwelling = this.mFilteredDwellings.get(position);
        holder.prefix.setText(String.format("%s%s%s", mDwelling.getBlock(), mDwelling.getFloor(), mDwelling.getFlatNo()));
        holder.name.setText(mDwelling.getOwnerName());
        holder.type.setText(mDwelling.getDwellignType());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        holder.date.setText(formatter.format(new Date(mDwelling.getUpdatedAt())));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SingleFragmentActivity.class);
                intent.putExtra("ddn", ddn);
                intent.putExtra("editable", true);
                intent.putExtra(Dwelling.class.getName(), mDwelling);
                intent.putExtra(KEY_SINGLE_FRAGMENT, SingleFragmentActivity.FragmentName.DWELLINGDETAILS);
                mContext.startActivity(intent);
            }
        });
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
                    OrderedRealmCollection<Dwelling> filteredList = new RealmList<>();
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
                mFilteredDwellings = (OrderedRealmCollection<Dwelling>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    class DwellingViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        TextView prefix;
        TextView name;
        TextView type;
        TextView date;

        DwellingViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            prefix = itemView.findViewById(R.id.dwelling_list_item_prefix);
            name = itemView.findViewById(R.id.dwelling_list_item_name);
            type = itemView.findViewById(R.id.dwelling_list_item_type);
            date = itemView.findViewById(R.id.dwelling_list_item_date);
        }
    }
}
