package in.rentifi.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import in.example.adapter.PropertyAdapterLatest;
import in.example.item.ItemProperty;
import in.example.util.Constant;
import in.example.util.ItemOffsetDecoration;
import in.example.util.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdvanceSearchActivity extends AppCompatActivity {

    ArrayList<ItemProperty> mListItem;
    public RecyclerView recyclerView;
    PropertyAdapterLatest adapter;
    private ProgressBar progressBar;
    private LinearLayout lyt_not_found;
    String typeMax,typeMin, typeId, typeVerify,typeFur;
    Toolbar toolbar;
    JsonUtils jsonUtils;
    LinearLayout adLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_item);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.menu_search));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        Intent intent = getIntent();
        jsonUtils = new JsonUtils(this);
        jsonUtils.forceRTLIfSupported(getWindow());

        typeMax = intent.getStringExtra("PriceMax");
        typeId = intent.getStringExtra("TypeId");
        typeMin = intent.getStringExtra("PriceMin");
        typeVerify = intent.getStringExtra("Verify");
        typeFur = intent.getStringExtra("Furnishing");

        mListItem = new ArrayList<>();

        lyt_not_found = findViewById(R.id.lyt_not_found);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.vertical_courses_list);
        adLayout = findViewById(R.id.adview);
        if (JsonUtils.personalization_ad) {
            JsonUtils.showPersonalizedAds(adLayout, AdvanceSearchActivity.this);
        } else {
            JsonUtils.showNonPersonalizedAds(adLayout, AdvanceSearchActivity.this);
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(AdvanceSearchActivity.this, 1));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(AdvanceSearchActivity.this, R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        if (JsonUtils.isNetworkAvailable(AdvanceSearchActivity.this)) {
            new getSearch().execute(Constant.ADVANCE_SEARCH_URL +typeVerify  + "&price_min=" + typeMin + "&price_max=" + typeMax+"&furnishing="+typeFur+"&type_id="+typeId);
            Log.e("url",""+Constant.ADVANCE_SEARCH_URL +typeVerify  + "&price_min=" + typeMin + "&price_max=" + typeMax+"&furnishing="+typeFur+"&type_id="+typeId);
         }
    }

    @SuppressLint("StaticFieldLeak")
    private class getSearch extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            showProgress(false);
            if (null == result || result.length() == 0) {
                lyt_not_found.setVisibility(View.VISIBLE);
            } else {
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        ItemProperty objItem = new ItemProperty();
                        objItem.setPId(objJson.getString(Constant.PROPERTY_ID));
                        objItem.setPropertyName(objJson.getString(Constant.PROPERTY_TITLE));
                        objItem.setPropertyThumbnailB(objJson.getString(Constant.PROPERTY_IMAGE));
                        objItem.setRateAvg(objJson.getString(Constant.PROPERTY_RATE));
                        objItem.setPropertyPrice(objJson.getString(Constant.PROPERTY_PRICE));
                        objItem.setPropertyBed(objJson.getString(Constant.PROPERTY_BED));
                        objItem.setPropertyBath(objJson.getString(Constant.PROPERTY_BATH));
                        objItem.setPropertyArea(objJson.getString(Constant.PROPERTY_AREA));
                        objItem.setPropertyAddress(objJson.getString(Constant.PROPERTY_ADDRESS));
                        objItem.setPropertyPurpose(objJson.getString(Constant.PROPERTY_PURPOSE));
                        objItem.setpropertyTotalRate(objJson.getString(Constant.PROPERTY_TOTAL_RATE));
                        if (i % 2 == 0) {
                            objItem.setRight(true);
                        }
                        mListItem.add(objItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                displayData();
            }
        }
    }


    private void displayData() {
        adapter = new PropertyAdapterLatest(AdvanceSearchActivity.this, mListItem);
        recyclerView.setAdapter(adapter);

        if (adapter.getItemCount() == 0) {
            lyt_not_found.setVisibility(View.VISIBLE);
        } else {
            lyt_not_found.setVisibility(View.GONE);
        }
    }


    private void showProgress(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            lyt_not_found.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }
}
