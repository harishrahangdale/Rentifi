package in.example.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import in.rentifi.app.R;

import java.util.ArrayList;

/**
 * Created by laxmi.
 */
public class AmenitiesAdapter extends RecyclerView.Adapter<AmenitiesAdapter.ItemRowHolder> {

    private ArrayList<String> dataList;
    private Context mContext;

    public AmenitiesAdapter(Context context, ArrayList<String> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_amenities, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(ItemRowHolder holder, final int position) {
        holder.text.setText(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        public TextView text;
        private ItemRowHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.textAmenities);
        }
    }
}
