package in.example.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import in.rentifi.app.PropertyDetailsActivity;
import in.rentifi.app.R;
import in.example.db.DatabaseHelper;
import in.example.item.ItemProperty;
import in.example.util.Constant;
import in.example.util.JsonUtils;
import com.github.ornolfr.ratingview.RatingView;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/**
 * Created by laxmi.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ItemRowHolder> {

    private ArrayList<ItemProperty> dataList;
    private Context mContext;
    private InterstitialAd mInterstitial;
    private int AD_COUNT = 0;
    private DatabaseHelper databaseHelper;

    public HomeAdapter(Context context, ArrayList<ItemProperty> dataList) {
        this.dataList = dataList;
        this.mContext = context;
        databaseHelper = new DatabaseHelper(mContext);
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_home_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(final ItemRowHolder holder, final int position) {
        final ItemProperty singleItem = dataList.get(position);
        holder.text.setText(singleItem.getPropertyName());
        holder.textPrice.setText(mContext.getString(R.string.currency_symbol)+singleItem.getPropertyPrice());
        holder.textAddress.setText(singleItem.getPropertyAddress());
         holder.textBed.setText(singleItem.getPropertyBed()+" "+mContext.getString(R.string.bed_bath));
        holder.textBath.setText(singleItem.getPropertyBath()+" "+mContext.getString(R.string.bed_bath2));
        holder.textSquare.setText(singleItem.getPropertyArea());
        holder.ratingView.setRating(Float.parseFloat(singleItem.getRateAvg()));
        Picasso.get().load(singleItem.getPropertyThumbnailB()).placeholder(R.drawable.header_top_logo).into(holder.image);
        holder.textTotalRate.setText("("+singleItem.getpropertyTotalRate()+")");

        if (databaseHelper.getFavouriteById(singleItem.getPId())) {
            holder.ic_home_fav.setImageResource(R.drawable.ic_fav_hover);
        } else {
            holder.ic_home_fav.setImageResource(R.drawable.ic_fav);
        }

        holder.ic_home_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues fav = new ContentValues();
                if (databaseHelper.getFavouriteById(singleItem.getPId())) {
                    databaseHelper.removeFavouriteById(singleItem.getPId());
                    holder.ic_home_fav.setImageResource(R.drawable.ic_fav);
                    Toast.makeText(mContext, mContext.getString(R.string.favourite_remove), Toast.LENGTH_SHORT).show();
                } else {
                    fav.put(DatabaseHelper.KEY_ID, singleItem.getPId());
                    fav.put(DatabaseHelper.KEY_TITLE, singleItem.getPropertyName());
                    fav.put(DatabaseHelper.KEY_IMAGE, singleItem.getPropertyThumbnailB());
                    fav.put(DatabaseHelper.KEY_RATE, singleItem.getRateAvg());
                    fav.put(DatabaseHelper.KEY_BED, singleItem.getPropertyBed());
                    fav.put(DatabaseHelper.KEY_BATH, singleItem.getPropertyBath());
                    fav.put(DatabaseHelper.KEY_ADDRESS, singleItem.getPropertyAddress());
                    fav.put(DatabaseHelper.KEY_AREA, singleItem.getPropertyArea());
                    fav.put(DatabaseHelper.KEY_PRICE, singleItem.getPropertyPrice());
                    fav.put(DatabaseHelper.KEY_PURPOSE, singleItem.getPropertyPurpose());
                    databaseHelper.addFavourite(DatabaseHelper.TABLE_FAVOURITE_NAME, fav, null);
                    holder.ic_home_fav.setImageResource(R.drawable.ic_fav_hover);
                    Toast.makeText(mContext, mContext.getString(R.string.favourite_add), Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Constant.SAVE_ADS_FULL_ON_OFF.equals("true")) {
                    AD_COUNT++;
                    if (AD_COUNT == Integer.parseInt(Constant.SAVE_ADS_CLICK)) {
                        AD_COUNT = 0;
                        mInterstitial = new InterstitialAd(mContext);
                        mInterstitial.setAdUnitId(Constant.SAVE_ADS_FULL_ID);
                        AdRequest adRequest;
                        if (JsonUtils.personalization_ad) {
                            adRequest = new AdRequest.Builder()
                                    .build();
                        } else {
                            Bundle extras = new Bundle();
                            extras.putString("npa", "1");
                            adRequest = new AdRequest.Builder()
                                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                                    .build();
                        }
                        mInterstitial.loadAd(adRequest);
                        mInterstitial.setAdListener(new AdListener() {
                            @Override
                            public void onAdLoaded() {
                                // TODO Auto-generated method stub
                                super.onAdLoaded();
                                if (mInterstitial.isLoaded()) {
                                    mInterstitial.show();
                                }
                            }

                            public void onAdClosed() {
                                Intent intent = new Intent(mContext, PropertyDetailsActivity.class);
                                intent.putExtra("Id", singleItem.getPId());
                                mContext.startActivity(intent);

                            }

                            @Override
                            public void onAdFailedToLoad(int errorCode) {
                                Intent intent = new Intent(mContext, PropertyDetailsActivity.class);
                                intent.putExtra("Id", singleItem.getPId());
                                mContext.startActivity(intent);
                            }
                        });
                    } else {
                        Intent intent = new Intent(mContext, PropertyDetailsActivity.class);
                        intent.putExtra("Id", singleItem.getPId());
                        mContext.startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(mContext, PropertyDetailsActivity.class);
                    intent.putExtra("Id", singleItem.getPId());
                    mContext.startActivity(intent);
                }
            }
        });

        holder.txtPurpose.setText(singleItem.getPropertyPurpose());
        if (mContext.getResources().getString(R.string.isRTL).equals("true")) {
            holder.txtPurpose.setBackgroundResource(singleItem.getPropertyPurpose().equals("Rent") ? R.drawable.rent_right_button : R.drawable.sale_right_button);
        }else {
            holder.txtPurpose.setBackgroundResource(singleItem.getPropertyPurpose().equals("Rent") ? R.drawable.rent_left_button : R.drawable.sale_left_button);
        }
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        public ImageView image,ic_home_fav;
        private TextView text, textPrice, textAddress, textBed,textBath,textSquare, txtPurpose,textTotalRate;
        private LinearLayout lyt_parent;
        public RatingView ratingView;

        private ItemRowHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
            textPrice = itemView.findViewById(R.id.textPrice);
            textAddress = itemView.findViewById(R.id.textAddress);
            txtPurpose = itemView.findViewById(R.id.textPurpose);
            textBed = itemView.findViewById(R.id.textBed);
            lyt_parent = itemView.findViewById(R.id.rootLayout);
            ratingView = itemView.findViewById(R.id.ratingView);
            textBath=itemView.findViewById(R.id.textBath);
            textSquare=itemView.findViewById(R.id.textSquare);
            ic_home_fav=itemView.findViewById(R.id.ic_home_fav);
            textTotalRate=itemView.findViewById(R.id.textAvg);
        }
    }
}
