package in.rentifi.app;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import in.example.db.DatabaseHelper;
import in.example.fragment.AmenitiesFragment;
import in.example.fragment.GalleryFragment;
import in.example.item.ItemProperty;
import in.example.util.Constant;
import in.example.util.JsonUtils;
import com.github.ornolfr.ratingview.RatingView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class PropertyDetailsActivity extends AppCompatActivity {

    ImageView imageMap, imageCall, imageChat, imageRating,image_rate_close;
    TextView txtName, txtAddress, txtPrice, txtBed, txtBath, txtArea, txtPhone, txtAmenities;
    WebView webView;
    Toolbar toolbar;
    ScrollView mScrollView;
    ProgressBar mProgressBar;
    ItemProperty objBean;
    String Id;
    ArrayList<String> mGallery, mAmenities;
    private FragmentManager fragmentManager;
    RatingView ratingView;
    String rateMsg;
    Menu menu;
    DatabaseHelper databaseHelper;
    View view, view1;
    JsonUtils jsonUtils;
    LinearLayout adLayout;
    boolean iswhichscreen;
    ImageView image_fur, image_very, image_boys, image_girls, image_family, image_married_couples, image_unmarried_couples;
    TextView textFur, textVery, text_boys, text_girls, text_family, text_married_couples, text_unmarried_couples;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estate_details);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setTitle(getString(R.string.property_details));

        jsonUtils = new JsonUtils(this);
        jsonUtils.forceRTLIfSupported(getWindow());

        databaseHelper = new DatabaseHelper(getApplicationContext());
        Intent i = getIntent();
        Id = i.getStringExtra("Id");

        fragmentManager = getSupportFragmentManager();

        objBean = new ItemProperty();
        mGallery = new ArrayList<>();
        mAmenities = new ArrayList<>();

        //Code For SUitable For
        image_boys = findViewById(R.id.image_boys);
        text_boys = findViewById(R.id.textBoys);
        image_girls = findViewById(R.id.image_girls);
        text_girls = findViewById(R.id.textGirls);
        image_family = findViewById(R.id.image_family);
        text_family = findViewById(R.id.textFamily);
        image_married_couples = findViewById(R.id.image_married_couple);
        text_married_couples = findViewById(R.id.textMarried);
        image_unmarried_couples = findViewById(R.id.image_unmarried_couple);
        text_unmarried_couples = findViewById(R.id.textUnmarried);
        //Ends Here

        image_fur = findViewById(R.id.image_fur);
        textFur = findViewById(R.id.textFur);
        image_very = findViewById(R.id.image_very);
        textVery = findViewById(R.id.textVery);
        //imageFloor = findViewById(R.id.image_floor);
        imageMap = findViewById(R.id.imageMap);
        imageCall = findViewById(R.id.imageCall);
        imageChat = findViewById(R.id.imageChat);
        txtName = findViewById(R.id.text);
        txtAddress = findViewById(R.id.textAddress);
        txtPrice = findViewById(R.id.textPrice);
        txtBed = findViewById(R.id.textBed);
        txtBath = findViewById(R.id.textBath);
        txtArea = findViewById(R.id.textSquare);
        txtPhone = findViewById(R.id.textPhone);
        txtAmenities = findViewById(R.id.txtAmenities);
        view = findViewById(R.id.viewAmenities);
        view1 = findViewById(R.id.viewAmenities1);
        ratingView = findViewById(R.id.ratingView);
        imageRating = findViewById(R.id.image_rating);
        webView = findViewById(R.id.property_description);

        mScrollView = findViewById(R.id.scrollView);
        mProgressBar = findViewById(R.id.progressBar1);
        webView.setBackgroundColor(Color.TRANSPARENT);
        adLayout = findViewById(R.id.adview);
        Intent intent = getIntent();
        iswhichscreen = intent.getBooleanExtra("isNotification", false);
        if (!iswhichscreen) {
            if (JsonUtils.personalization_ad) {
                JsonUtils.showPersonalizedAds(adLayout, PropertyDetailsActivity.this);
            } else {
                JsonUtils.showNonPersonalizedAds(adLayout, PropertyDetailsActivity.this);
            }

        }

        if (JsonUtils.isNetworkAvailable(PropertyDetailsActivity.this)) {
            new getProperty().execute(Constant.SINGLE_PROPERTY_URL + Id);
        } else {
            showToast(getString(R.string.conne_msg1));
        }

        imageRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRating();
            }
        });

        imageMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String geoUri = "http://maps.google.com/maps?q=loc:" + objBean.getPropertyMapLatitude() + "," + objBean.getPropertyMapLongitude() + " (" + objBean.getPropertyName() + ")";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                startActivity(intent);
            }
        });

        imageCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + objBean.getPropertyPhone()));
                startActivity(callIntent);
            }
        });

        imageChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://wa.me/91" + objBean.getPropertyPhone() + "?text=I'm%20interested%20in%20your%20property%20on%20Rentifi%20App...";
                try {
                    PackageManager pm = getApplicationContext().getPackageManager();
                    pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(PropertyDetailsActivity.this, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class getProperty extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
            mScrollView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mProgressBar.setVisibility(View.GONE);
            mScrollView.setVisibility(View.VISIBLE);
            if (null == result || result.length() == 0) {
                showToast(getString(R.string.nodata));
            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        objBean.setPId(objJson.getString(Constant.PROPERTY_ID));
                        objBean.setPropertyName(objJson.getString(Constant.PROPERTY_TITLE));
                        objBean.setPropertyThumbnailB(objJson.getString(Constant.PROPERTY_IMAGE));
                        objBean.setRateAvg(objJson.getString(Constant.PROPERTY_RATE));
                        objBean.setPropertyPrice(objJson.getString(Constant.PROPERTY_PRICE));
                        objBean.setPropertyBed(objJson.getString(Constant.PROPERTY_BED));
                        objBean.setPropertyBath(objJson.getString(Constant.PROPERTY_BATH));
                        objBean.setPropertyArea(objJson.getString(Constant.PROPERTY_AREA));
                        objBean.setPropertyAddress(objJson.getString(Constant.PROPERTY_ADDRESS));
                        objBean.setPropertyPhone(objJson.getString(Constant.PROPERTY_PHONE));
                        objBean.setPropertyDescription(objJson.getString(Constant.PROPERTY_DESC));
                        objBean.setPropertyFloorPlan(objJson.getString(Constant.PROPERTY_FLOOR_PLAN));
                        objBean.setPropertyAmenities(objJson.getString(Constant.PROPERTY_AMENITIES));
                        objBean.setPropertyPurpose(objJson.getString(Constant.PROPERTY_PURPOSE));
                        objBean.setPropertyMapLatitude(objJson.getString(Constant.PROPERTY_LATITUDE));
                        objBean.setPropertyMapLongitude(objJson.getString(Constant.PROPERTY_LONGITUDE));
                        objBean.setPropertyVery(objJson.getString(Constant.PROPERTY_VERY));
                        objBean.setPropertyFur(objJson.getString(Constant.PROPERTY_FUR));
                        objBean.setPropertySuitableFor(objJson.getString(Constant.PROPERTY_SUITABLE_FOR));

                        JSONArray jsonArrayGallery = objJson.getJSONArray(Constant.GALLERY_ARRAY_NAME);
                        if (jsonArrayGallery.length() != 0) {
                            for (int j = 0; j < jsonArrayGallery.length(); j++) {
                                JSONObject objChild = jsonArrayGallery.getJSONObject(j);
                                if (!objChild.has(Constant.SUCCESS)) {
                                    mGallery.add(objChild.getString(Constant.GALLERY_IMAGE_NAME));
                                } else {
                                    mGallery.add(objJson.getString(Constant.PROPERTY_IMAGE));
                                }

                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult();
            }
        }
    }

    private void setResult() {

        txtName.setText(objBean.getPropertyName());
        txtAddress.setText(objBean.getPropertyAddress());
        txtBath.setText(objBean.getPropertyBath() + getString(R.string.bed_bath2));
        txtBed.setText(objBean.getPropertyBed() + getString(R.string.bed_bath));
        txtArea.setText(objBean.getPropertyArea());
        txtPhone.setText(objBean.getPropertyPhone());
        txtPrice.setText(getString(R.string.currency_symbol) + objBean.getPropertyPrice());
        ratingView.setRating(Float.parseFloat(objBean.getRateAvg()));
        if (!objBean.getPropertyAmenities().isEmpty())
            mAmenities = new ArrayList<>(Arrays.asList(objBean.getPropertyAmenities().split(",")));

        /*Picasso.get().load(objBean.getPropertyFloorPlan()).placeholder(R.drawable.header_top_logo).into(imageFloor);

        imageFloor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PropertyDetailsActivity.this, FloorImageActivity.class);
                intent.putExtra("ImageF", objBean.getPropertyFloorPlan());
                startActivity(intent);

            }
        });*/

        String mimeType = "text/html";
        String encoding = "utf-8";
        String htmlText = objBean.getPropertyDescription();

        String text = "<html><head>"
                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/custom.ttf\")}body{font-family: MyFont;color: #868686;text-align:left;font-size:12px;margin-left:0px;line-height:1.8}"
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        webView.loadDataWithBaseURL(null, text, mimeType, encoding, null);

        if (objBean.getPropertyFur().equals(getString(R.string.detail_un_semi))) {
            image_fur.setImageResource(R.drawable.ic_unfurnished);
            image_fur.setBackgroundResource(R.drawable.circle_gray_detail);
            textFur.setText(getString(R.string.detail_un_semi));

        } else if (objBean.getPropertyFur().equals(getString(R.string.detail_fur))) {
            image_fur.setImageResource(R.drawable.ic_furnished);
            image_fur.setBackgroundResource(R.drawable.circle_green_detail);
            textFur.setText(getString(R.string.detail_fur));
        } else if (objBean.getPropertyFur().equals(getString(R.string.detail_semi))) {
            image_fur.setImageResource(R.drawable.ic_semi_furnished);
            image_fur.setBackgroundResource(R.drawable.circle_orange_detail);
            textFur.setText(getString(R.string.detail_semi));
        }


        //Code For Suitable For

        if (objBean.getPropertySuitableFor().contains(getString(R.string.sfb))) {
            image_boys.setBackgroundResource(R.drawable.circle_orange_detail);

        } if (objBean.getPropertySuitableFor().contains(getString(R.string.sfg))) {
            image_girls.setBackgroundResource(R.drawable.circle_orange_detail);

        } if (objBean.getPropertySuitableFor().contains(getString(R.string.sff))) {
            image_family.setBackgroundResource(R.drawable.circle_orange_detail);

        } if (objBean.getPropertySuitableFor().contains(getString(R.string.sfmc))) {
            image_married_couples.setBackgroundResource(R.drawable.circle_orange_detail);

        } if (objBean.getPropertySuitableFor().contains(getString(R.string.sfuc))) {
            image_unmarried_couples.setBackgroundResource(R.drawable.circle_orange_detail);

        }
        //Ends Here

        if (objBean.getPropertyVery().equals("1"))//verify
        {
            image_very.setBackgroundResource(R.drawable.circle_green_detail);
            image_very.setImageResource(R.drawable.ic_verified_properties);
            textVery.setText(getString(R.string.detail_very));
        } else {
            image_very.setBackgroundResource(R.drawable.circle_gray_detail);
            image_very.setImageResource(R.drawable.ic_non_verified_properties);
            textVery.setText(getString(R.string.detail_un_very));
        }

        if (!mGallery.isEmpty()) {
            GalleryFragment sliderFragment = GalleryFragment.newInstance(mGallery);
            fragmentManager.beginTransaction().replace(R.id.ContainerGallery, sliderFragment).commit();
        }

        if (!objBean.getPropertyAmenities().isEmpty()) {
            AmenitiesFragment amenitiesFragment = AmenitiesFragment.newInstance(mAmenities);
            fragmentManager.beginTransaction().replace(R.id.ContainerAmenities, amenitiesFragment).commit();
        } else {
            txtAmenities.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
            view1.setVisibility(View.GONE);
        }

    }

    public void showToast(String msg) {
        Toast.makeText(PropertyDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void showRating() {
        final String deviceId;
        final Dialog mDialog = new Dialog(PropertyDetailsActivity.this, R.style.Theme_AppCompat_Translucent);
        mDialog.setContentView(R.layout.rate_dialog);
        jsonUtils = new JsonUtils(this);
        jsonUtils.forceRTLIfSupported(getWindow());
        deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        final RatingView ratingView = mDialog.findViewById(R.id.ratingView);
        image_rate_close=mDialog.findViewById(R.id.image_rate_close);
        ratingView.setRating(0);
        Button button = mDialog.findViewById(R.id.btn_submit);

        image_rate_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (JsonUtils.isNetworkAvailable(PropertyDetailsActivity.this)) {
                    new SentRating().execute(Constant.RATING_URL + Id + "&rate=" + ratingView.getRating() + "&device_id=" + deviceId);
                } else {
                    showToast(getString(R.string.conne_msg1));
                }
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    @SuppressLint("StaticFieldLeak")
    private class SentRating extends AsyncTask<String, Void, String> {

        ProgressDialog pDialog;
        String Rate;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(PropertyDetailsActivity.this);
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (null != pDialog && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (null == result || result.length() == 0) {
                showToast("No data found from web!!!");

            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        rateMsg = objJson.getString("MSG");
                        if (objJson.has(Constant.PROPERTY_RATE)) {
                            Rate = objJson.getString(Constant.PROPERTY_RATE);
                        } else {
                            Rate = "";
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setRate();
            }

        }

        private void setRate() {
            showToast(rateMsg);
            if (!Rate.isEmpty())
                ratingView.setRating(Float.parseFloat(Rate));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_property, menu);
        this.menu = menu;
        isFavourite();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_bookmark:
                ContentValues fav = new ContentValues();
                if (databaseHelper.getFavouriteById(Id)) {
                    databaseHelper.removeFavouriteById(Id);
                    menu.getItem(0).setIcon(R.drawable.ic_bookmark_border_white_24dp);
                    Toast.makeText(PropertyDetailsActivity.this, getString(R.string.favourite_remove), Toast.LENGTH_SHORT).show();
                } else {
                    fav.put(DatabaseHelper.KEY_ID, Id);
                    fav.put(DatabaseHelper.KEY_TITLE, objBean.getPropertyName());
                    fav.put(DatabaseHelper.KEY_IMAGE, objBean.getPropertyThumbnailB());
                    fav.put(DatabaseHelper.KEY_RATE, objBean.getRateAvg());
                    fav.put(DatabaseHelper.KEY_BED, objBean.getPropertyBed());
                    fav.put(DatabaseHelper.KEY_BATH, objBean.getPropertyBath());
                    fav.put(DatabaseHelper.KEY_ADDRESS, objBean.getPropertyAddress());
                    fav.put(DatabaseHelper.KEY_AREA, objBean.getPropertyArea());
                    fav.put(DatabaseHelper.KEY_PRICE, objBean.getPropertyPrice());
                    fav.put(DatabaseHelper.KEY_PURPOSE, objBean.getPropertyPurpose());
                    databaseHelper.addFavourite(DatabaseHelper.TABLE_FAVOURITE_NAME, fav, null);
                    menu.getItem(0).setIcon(R.drawable.ic_bookmark_white_24dp);
                    Toast.makeText(PropertyDetailsActivity.this, getString(R.string.favourite_add), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    private void isFavourite() {
        if (databaseHelper.getFavouriteById(Id)) {
            menu.getItem(0).setIcon(R.drawable.ic_bookmark_white_24dp);
        } else {
            menu.getItem(0).setIcon(R.drawable.ic_bookmark_border_white_24dp);
        }
    }
}
