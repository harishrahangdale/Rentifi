package in.example.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import in.rentifi.app.AdvanceSearchActivity;
import in.rentifi.app.R;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import in.example.adapter.FilterAdapter;
import in.example.adapter.PropertyAdapterLatest;
import in.example.item.ItemProperty;
import in.example.item.ItemType;
import in.example.util.Constant;
import in.example.util.ItemOffsetDecoration;
import in.example.util.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by laxmi.
 */
public class AllPropertiesFragment extends Fragment {

    ArrayList<ItemProperty> mListItem;
    public RecyclerView recyclerView;
    PropertyAdapterLatest adapter;
    private ProgressBar progressBar;
    private LinearLayout lyt_not_found;
    ArrayList<ItemType> mListType;
    ArrayList<String> mPropertyName;
    FilterAdapter filterAdapter;
    String string_very, string_fur,final_value_min,final_value_max,string_sort;
    int save_sort=1;
    JsonUtils jsonUtils;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.row_recyclerview_home, container, false);
        mListItem = new ArrayList<>();
        mListType = new ArrayList<>();
        mPropertyName = new ArrayList<>();

        lyt_not_found = rootView.findViewById(R.id.lyt_not_found);
        progressBar = rootView.findViewById(R.id.progressBar);
        recyclerView = rootView.findViewById(R.id.vertical_courses_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(requireActivity(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        string_sort="DESC";
        if (JsonUtils.isNetworkAvailable(requireActivity())) {
            new getLatest().execute(Constant.ALL_PROPERTIES_URL);
        }
        setHasOptionsMenu(true);
        return rootView;
    }

    @SuppressLint("StaticFieldLeak")
    private class getLatest extends AsyncTask<String, Void, String> {

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
        adapter = new PropertyAdapterLatest(getActivity(), mListItem);
        recyclerView.setAdapter(adapter);

        if (adapter.getItemCount() == 0) {
            lyt_not_found.setVisibility(View.VISIBLE);
        } else {
            lyt_not_found.setVisibility(View.GONE);
        }
        if (JsonUtils.isNetworkAvailable(requireActivity())) {
            new getType().execute(Constant.PROPERTIES_TYPE);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.search:
                showSearch();
                break;
            case R.id.search_sort:
                showSearchSort();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSearch() {
        final Dialog mDialog = new Dialog(requireActivity(), R.style.Theme_AppCompat_Translucent);
        mDialog.setContentView(R.layout.search_dialog);
        jsonUtils = new JsonUtils(getActivity());
        jsonUtils.forceRTLIfSupported(getActivity().getWindow());
        RecyclerView recyclerView = mDialog.findViewById(R.id.rv_fil_recycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(requireActivity(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        filterAdapter = new FilterAdapter(getActivity(), mListType);
        recyclerView.setAdapter(filterAdapter);

        ImageView image_fil_close = mDialog.findViewById(R.id.image_fil_close);
        image_fil_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        CrystalRangeSeekbar appCompatSeekBar = mDialog.findViewById(R.id.rangeSeekbar3);
        final Button buttonPriceMin = mDialog.findViewById(R.id.btn_seek_price_min);
        final Button buttonPriceMax = mDialog.findViewById(R.id.btn_seek_price_max);
        buttonPriceMax.setText(getResources().getString(R.string.max_value) + getString(R.string.max_value_price));
        buttonPriceMin.setText(getResources().getString(R.string.min_value) + getString(R.string.min_value_price));
        appCompatSeekBar.setMaxValue(Integer.parseInt(getString(R.string.min_value_price)));
        appCompatSeekBar.setMinValue(Integer.parseInt(getString(R.string.max_value_price)));
        appCompatSeekBar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                buttonPriceMin.setText(getResources().getString(R.string.min_value) + String.valueOf(minValue));
                buttonPriceMax.setText(getResources().getString(R.string.max_value) + String.valueOf(maxValue));
                final_value_max=String.valueOf(maxValue);
                final_value_min=String.valueOf(minValue);
            }
        });

        RadioGroup radioGroup = mDialog.findViewById(R.id.myRadioGroup);
        RadioButton fil_non_very = mDialog.findViewById(R.id.filter_recommended_non_very);
        RadioButton fil_very = mDialog.findViewById(R.id.filter_recommended_very);
        fil_non_very.setChecked(true);
        string_very = "0";
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.filter_recommended_non_very) {
                    string_very = "0";
                } else if (checkedId == R.id.filter_recommended_very) {
                    string_very = "1";
                }
            }

        });

        RadioGroup radioGroupFur = mDialog.findViewById(R.id.myRadioGroupFur);
        RadioButton filter_fur = mDialog.findViewById(R.id.filter_fur);
        RadioButton filter_semi = mDialog.findViewById(R.id.filter_semi);
        RadioButton filter_un_semi = mDialog.findViewById(R.id.filter_un_semi);
        filter_fur.setChecked(true);
        string_fur = getString(R.string.filter_furnishing);
        radioGroupFur.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int iCheck) {
                if (iCheck == R.id.filter_fur) {
                    string_fur = getString(R.string.filter_furnishing);
                } else if (iCheck == R.id.filter_semi) {
                    string_fur = getString(R.string.filter_furnishing_semi);
                } else if (iCheck == R.id.filter_un_semi) {
                    string_fur = getString(R.string.filter_furnishing_un_fur);
                }
            }
        });

        Button btn_submit_apply = mDialog.findViewById(R.id.btn_submit);
        btn_submit_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Constant.SEARCH_FIL_ID.isEmpty()) {
                    Toast.makeText(requireActivity(), getString(R.string.choose_one_type), Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(requireActivity(), AdvanceSearchActivity.class);
                    intent.putExtra("Verify", string_very);
                    intent.putExtra("PriceMin", final_value_min);
                    intent.putExtra("PriceMax", final_value_max);
                    intent.putExtra("Furnishing", string_fur);
                    intent.putExtra("TypeId", Constant.SEARCH_FIL_ID);
                    startActivity(intent);
                    Constant.SEARCH_FIL_ID = "";
                    mDialog.dismiss();
                }

            }
        });

        mDialog.show();
    }

    private class getType extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (null == result || result.length() == 0) {

            } else {
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        ItemType objItem = new ItemType();
                        objItem.setTypeId(objJson.getString(Constant.TYPE_ID));
                        objItem.setTypeName(objJson.getString(Constant.TYPE_NAME));
                        mPropertyName.add(objJson.getString(Constant.TYPE_NAME));
                        mListType.add(objItem);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showSearchSort() {
        final Dialog mDialog = new Dialog(requireActivity(), R.style.Theme_AppCompat_Translucent);
        mDialog.setContentView(R.layout.search_dialog_sort);
        jsonUtils = new JsonUtils(getActivity());
        jsonUtils.forceRTLIfSupported(getActivity().getWindow());
        RadioGroup radioGroupSort = mDialog.findViewById(R.id.myRadioGroup);
        RadioButton filter_dis = mDialog.findViewById(R.id.sort_distance);
        RadioButton filter_low = mDialog.findViewById(R.id.sort_law);
        RadioButton filter_high = mDialog.findViewById(R.id.sort_high);
        RelativeLayout rel_other=mDialog.findViewById(R.id.rel_other);
        RadioButton filter_all=mDialog.findViewById(R.id.sort_all);

        rel_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        if(save_sort==1) {
            filter_all.setChecked(true);
        }else if(save_sort==2){
            filter_high.setChecked(true);
        }else if(save_sort==3){
            filter_low.setChecked(true);
        }
        else if(save_sort==4){
            filter_dis.setChecked(true);
        }
        string_sort = "DESC";
        radioGroupSort.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int iCheck) {
                mListItem.clear();
                if (iCheck == R.id.sort_distance) {
                    save_sort=4;
                    string_sort = getString(R.string.sort_by_distance);
                    if (JsonUtils.isNetworkAvailable(requireActivity())) {
                        new getLatest().execute(Constant.DISTANCE_URL+Constant.USER_LATITUDE+"&user_long="+Constant.USER_LONGITUDE);
                    }
                } else if (iCheck == R.id.sort_high) {
                    save_sort=2;
                    string_sort = "DESC";
                    if (JsonUtils.isNetworkAvailable(requireActivity())) {
                        new getLatest().execute(Constant.PRICE_URL+string_sort);
                    }
                } else if (iCheck == R.id.sort_law) {
                    save_sort=3;
                    string_sort = "ASC";
                    if (JsonUtils.isNetworkAvailable(requireActivity())) {
                        new getLatest().execute(Constant.PRICE_URL+string_sort);
                    }
                }
                else if (iCheck == R.id.sort_all) {
                    save_sort=1;
                    string_sort = getString(R.string.sort_by_all);
                    if (JsonUtils.isNetworkAvailable(requireActivity())) {
                        new getLatest().execute(Constant.ALL_PROPERTIES_URL);
                    }}

                mDialog.dismiss();
            }
        });

        mDialog.show();
    }
}
