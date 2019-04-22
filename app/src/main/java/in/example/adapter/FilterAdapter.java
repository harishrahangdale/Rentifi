package in.example.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import in.rentifi.app.R;
import in.example.item.ItemType;
import in.example.util.Constant;

import java.util.ArrayList;

/**
 * Created by laxmi.
 */
public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ItemRowHolder> {

    public ArrayList<ItemType> dataList;
    private Context mContext;
    private int lastSelectedPosition = -1;
    private CompoundButton lastCheckedRB = null;

    public FilterAdapter(Context context, ArrayList<ItemType> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_filter_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(final ItemRowHolder holder, final int position) {
        final ItemType singleItem = dataList.get(position);

        holder.radioButtonType.setText(singleItem.getTypeName());
        holder.radioButtonType.setTag(position);
        holder.radioButtonType.setTag(R.id.filter_type, singleItem);


        holder.radioButtonType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                int tag = (int) compoundButton.getTag();
                if (lastCheckedRB == null) {
                    lastCheckedRB = compoundButton;
                } else if (tag != (int) lastCheckedRB.getTag()) {
                    lastCheckedRB.setChecked(false);
                    lastCheckedRB = compoundButton;
                }
                 Constant.SEARCH_FIL_ID = singleItem.getTypeId();
            }
        });


    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        private TextView text;
        private RelativeLayout relativeLayout;
        RadioGroup checkbox_fil_type;
        RadioButton radioButtonType;

        private ItemRowHolder(View itemView) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.rootLayout);
            checkbox_fil_type = itemView.findViewById(R.id.myRadioGroupType);
            radioButtonType = itemView.findViewById(R.id.filter_type);

        }
    }
}
