package in.example.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import in.rentifi.app.HomeMoreActivity;
import in.rentifi.app.MainActivity;
import in.rentifi.app.PropertyDetailsActivity;
import in.rentifi.app.R;
import in.rentifi.app.SearchActivity;
import in.example.adapter.HomeAdapter;
import in.example.db.DatabaseHelper;
import in.example.item.ItemProperty;
import in.example.item.ItemType;
import in.example.util.Constant;
import in.example.util.EnchantedViewPager;
import in.example.util.ItemOffsetDecoration;
import in.example.util.JsonUtils;
import in.example.util.NothingSelectedSpinnerAdapter;
import com.github.ornolfr.ratingview.RatingView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

public class HomeFragment extends Fragment {

    ScrollView mScrollView;
    ProgressBar mProgressBar;
    ArrayList<ItemProperty> mSliderList;
    RecyclerView mPopularView, mLatestView;
    HomeAdapter mPopularAdapter, mLatestAdapter;
    ArrayList<ItemProperty> mPopularList, mLatestList;
    Button btnPopular, btnLatest;
    RelativeLayout lytRecent;
    DatabaseHelper databaseHelper;
    EnchantedViewPager mViewPager;
    CustomViewPagerAdapter mAdapter;
    CircleIndicator circleIndicator;
    int currentCount = 0;
    ArrayList<ItemType> mListType;
    ArrayList<String> mPropertyName;
    EditText edtSearch;
    Button btnSubmit;
    Spinner spinnerType, spinnerPurpose;
    String srt_type[];
    LinearLayout lay_home_bottom;
    private FragmentManager fragmentManager;
    View home_view_1,home_view_2;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        databaseHelper = new DatabaseHelper(getActivity());
        mScrollView = rootView.findViewById(R.id.scrollView);
        mProgressBar = rootView.findViewById(R.id.progressBar);
        mPopularView = rootView.findViewById(R.id.rv_featured);
        mLatestView = rootView.findViewById(R.id.rv_latest);
        btnPopular = rootView.findViewById(R.id.btn_latest);
        btnLatest = rootView.findViewById(R.id.btn_featured);
        lytRecent = rootView.findViewById(R.id.lyt_recent_view);

        mSliderList = new ArrayList<>();
        mPopularList = new ArrayList<>();
        mLatestList = new ArrayList<>();
        mListType = new ArrayList<>();
        mPropertyName = new ArrayList<>();
        fragmentManager = requireActivity().getSupportFragmentManager();
        srt_type = getResources().getStringArray(R.array.purpose_array);

        mPopularView.setHasFixedSize(false);
        mPopularView.setNestedScrollingEnabled(false);
        mPopularView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(requireActivity(), R.dimen.item_offset);
        mPopularView.addItemDecoration(itemDecoration);
        lay_home_bottom = rootView.findViewById(R.id.lay_home_bottom);
        home_view_1=rootView.findViewById(R.id.home_view_1);
        home_view_2=rootView.findViewById(R.id.home_view_2);
        if (getActivity().getResources().getString(R.string.isRTL).equals("true")) {
            home_view_1.setBackgroundResource(R.drawable.bg_gradient_home_shadow_white_right);
            home_view_2.setBackgroundResource(R.drawable.bg_gradient_home_shadow_white_right);
         }else {
            home_view_1.setBackgroundResource(R.drawable.bg_gradient_home_shadow_white_left);
            home_view_2.setBackgroundResource(R.drawable.bg_gradient_home_shadow_white_left);
         }

        mLatestView.setHasFixedSize(false);
        mLatestView.setNestedScrollingEnabled(false);
        mLatestView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mLatestView.addItemDecoration(itemDecoration);
        mViewPager = rootView.findViewById(R.id.viewPager);
        circleIndicator = rootView.findViewById(R.id.indicator_unselected_background);

        mViewPager.useScale();
        mViewPager.removeAlpha();
        mAdapter = new CustomViewPagerAdapter();

        edtSearch = rootView.findViewById(R.id.edt_name);
        btnSubmit = rootView.findViewById(R.id.btn_submit);
        spinnerType = rootView.findViewById(R.id.spPropertyType);
        spinnerPurpose = rootView.findViewById(R.id.spPropertyPurpose);


        btnLatest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) requireActivity()).highLightNavigation(1, getString(R.string.menu_latest));
                ((MainActivity) requireActivity()).spaceNavigationView.changeCurrentItem(1);
                LatestFragment latestFragment = new LatestFragment();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.hide(HomeFragment.this);
                fragmentTransaction.add(R.id.Container, latestFragment);
                fragmentTransaction.commit();
            }
        });

        btnPopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HomeMoreActivity.class);
                startActivity(intent);
            }
        });

        if (JsonUtils.isNetworkAvailable(requireActivity())) {
            new Home().execute(Constant.HOME_URL);
        } else {
            showToast(getString(R.string.conne_msg1));
        }

        return rootView;
    }

    @SuppressLint("StaticFieldLeak")
    private class Home extends AsyncTask<String, Void, String> {

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

            if (null == result || result.length() == 0) {
                showToast(getString(R.string.nodata));
            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONObject jsonArray = mainJson.getJSONObject(Constant.ARRAY_NAME);

                    JSONArray jsonSlider = jsonArray.getJSONArray(Constant.HOME_FEATURED_ARRAY);
                    JSONObject objJsonSlider;
                    for (int i = 0; i < jsonSlider.length(); i++) {
                        objJsonSlider = jsonSlider.getJSONObject(i);
                        ItemProperty objItem = new ItemProperty();
                        objItem.setPId(objJsonSlider.getString(Constant.PROPERTY_ID));
                        objItem.setPropertyName(objJsonSlider.getString(Constant.PROPERTY_TITLE));
                        objItem.setPropertyThumbnailB(objJsonSlider.getString(Constant.PROPERTY_IMAGE));
                        objItem.setPropertyAddress(objJsonSlider.getString(Constant.PROPERTY_ADDRESS));
                        objItem.setPropertyPrice(objJsonSlider.getString(Constant.PROPERTY_PRICE));
                        objItem.setRateAvg(objJsonSlider.getString(Constant.PROPERTY_RATE));
                        objItem.setpropertyTotalRate(objJsonSlider.getString(Constant.PROPERTY_TOTAL_RATE));
                        mSliderList.add(objItem);
                    }

                    JSONArray jsonLatest = jsonArray.getJSONArray(Constant.HOME_LATEST_ARRAY);
                    JSONObject objJson;
                    for (int i = 0; i < jsonLatest.length(); i++) {
                        objJson = jsonLatest.getJSONObject(i);
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
                        mLatestList.add(objItem);
                    }

                    JSONArray jsonPopular = jsonArray.getJSONArray(Constant.HOME_POPULAR_ARRAY);
                    JSONObject objJsonPopular;
                    for (int i = 0; i < jsonPopular.length(); i++) {
                        objJsonPopular = jsonPopular.getJSONObject(i);
                        ItemProperty objItem = new ItemProperty();
                        objItem.setPId(objJsonPopular.getString(Constant.PROPERTY_ID));
                        objItem.setPropertyName(objJsonPopular.getString(Constant.PROPERTY_TITLE));
                        objItem.setPropertyThumbnailB(objJsonPopular.getString(Constant.PROPERTY_IMAGE));
                        objItem.setRateAvg(objJsonPopular.getString(Constant.PROPERTY_RATE));
                        objItem.setPropertyPrice(objJsonPopular.getString(Constant.PROPERTY_PRICE));
                        objItem.setPropertyBed(objJsonPopular.getString(Constant.PROPERTY_BED));
                        objItem.setPropertyBath(objJsonPopular.getString(Constant.PROPERTY_BATH));
                        objItem.setPropertyArea(objJsonPopular.getString(Constant.PROPERTY_AREA));
                        objItem.setPropertyAddress(objJsonPopular.getString(Constant.PROPERTY_ADDRESS));
                        objItem.setPropertyPurpose(objJsonPopular.getString(Constant.PROPERTY_PURPOSE));
                        objItem.setpropertyTotalRate(objJsonPopular.getString(Constant.PROPERTY_TOTAL_RATE));
                        mPopularList.add(objItem);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult();
            }
        }
    }

    private void setResult() {
        if (getActivity() != null) {
            mLatestAdapter = new HomeAdapter(getActivity(), mLatestList);
            mLatestView.setAdapter(mLatestAdapter);

            mPopularAdapter = new HomeAdapter(getActivity(), mPopularList);
            mPopularView.setAdapter(mPopularAdapter);

            if (!mSliderList.isEmpty()) {
                mViewPager.setAdapter(mAdapter);
                circleIndicator.setViewPager(mViewPager);
                autoPlay(mViewPager);
            }

            if (JsonUtils.isNetworkAvailable(getActivity())) {
                new getType().execute(Constant.PROPERTIES_TYPE);
            }
        }

    }

    @SuppressLint("StaticFieldLeak")
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
            mProgressBar.setVisibility(View.GONE);
            mScrollView.setVisibility(View.VISIBLE);
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
                setResult2();
            }
        }
    }

    private void setResult2() {

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, mPropertyName);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        ArrayAdapter<String> typeAdapter2 = new ArrayAdapter<>(requireActivity(), R.layout.spinner_item_home, srt_type);
        typeAdapter.setDropDownViewResource(R.layout.spinner_item_home);
        spinnerPurpose.setAdapter(
                new NothingSelectedSpinnerAdapter(typeAdapter2,
                        R.layout.contact_spinner_row_nothing_selected_home, requireActivity()));
        spinnerPurpose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                if (position == 0) {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.gray_light));
                    ((TextView) parent.getChildAt(0)).setTextSize(13);

                } else {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.gray_light));
                    ((TextView) parent.getChildAt(0)).setTextSize(13);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                if (position == 0) {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.gray_light));
                    ((TextView) parent.getChildAt(0)).setTextSize(13);

                } else {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.gray_light));
                    ((TextView) parent.getChildAt(0)).setTextSize(13);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = edtSearch.getText().toString();
                if (!search.isEmpty()) {
                    Intent intent = new Intent(getActivity(), SearchActivity.class);
                    intent.putExtra("purpose", String.valueOf(spinnerPurpose.getSelectedItem()));
                    intent.putExtra("TypeId", mListType.get(spinnerType.getSelectedItemPosition()).getTypeId());
                    intent.putExtra("searchText", search);
                    startActivity(intent);
                    edtSearch.getText().clear();
                }
            }
        });


    }

    private void autoPlay(final ViewPager viewPager) {

        viewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mAdapter != null && viewPager.getAdapter().getCount() > 0) {
                        int position = currentCount % mAdapter.getCount();
                        currentCount++;
                        viewPager.setCurrentItem(position);
                        autoPlay(viewPager);
                    }
                } catch (Exception e) {
                    Log.e("TAG", "auto scroll pager error.", e);
                }
            }
        }, 2500);
    }

    private class CustomViewPagerAdapter extends PagerAdapter {
        private LayoutInflater inflater;

        private CustomViewPagerAdapter() {
            // TODO Auto-generated constructor stub
            inflater = requireActivity().getLayoutInflater();
        }

        @Override
        public int getCount() {
            return mSliderList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View imageLayout = inflater.inflate(R.layout.row_slider_item, container, false);
            assert imageLayout != null;
            ImageView image = imageLayout.findViewById(R.id.image);
            TextView text = imageLayout.findViewById(R.id.text);
            TextView textAddress = imageLayout.findViewById(R.id.textAddress);
            LinearLayout lytParent = imageLayout.findViewById(R.id.rootLayout);
            RatingView ratingView = imageLayout.findViewById(R.id.ratingView);
            TextView text_count = imageLayout.findViewById(R.id.text_count);
            TextView text_price = imageLayout.findViewById(R.id.text_price);

            ratingView.setRating(Float.parseFloat(mSliderList.get(position).getRateAvg()));
            text_count.setText("(" + mSliderList.get(position).getpropertyTotalRate() + ")");
            text_price.setText(getString(R.string.currency_symbol) + mSliderList.get(position).getPropertyPrice());
            text.setText(mSliderList.get(position).getPropertyName());
            textAddress.setText(mSliderList.get(position).getPropertyAddress());

            Picasso.get().load(mSliderList.get(position).getPropertyThumbnailB()).placeholder(R.drawable.header_top_logo).into(image);
            imageLayout.setTag(EnchantedViewPager.ENCHANTED_VIEWPAGER_POSITION + position);
            lytParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), PropertyDetailsActivity.class);
                    intent.putExtra("Id", mSliderList.get(position).getPId());
                    startActivity(intent);
                }
            });
            container.addView(imageLayout, 0);

            return imageLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            (container).removeView((View) object);
        }
    }

    public void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

}
