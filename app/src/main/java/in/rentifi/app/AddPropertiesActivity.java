package in.rentifi.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import in.example.item.ItemType;
import in.example.util.Constant;
import in.example.util.JsonUtils;
import in.example.util.NothingSelectedSpinnerAdapter;

import com.androidbuts.multispinnerfilter.MultiSpinner;
import com.androidbuts.multispinnerfilter.MultiSpinnerListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.squareup.picasso.Picasso;
import com.zanjou.http.debug.Logger;
import com.zanjou.http.request.FileUploadListener;
import com.zanjou.http.request.Request;
import com.zanjou.http.request.RequestStateListener;
import com.zanjou.http.response.JsonResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch;
import com.androidbuts.multispinnerfilter.SingleSpinner;
import com.androidbuts.multispinnerfilter.SingleSpinnerSearch;
import com.androidbuts.multispinnerfilter.SpinnerListener;


import static com.google.android.gms.location.places.ui.PlacePicker.getPlace;

public class AddPropertiesActivity extends AppCompatActivity implements Validator.ValidationListener, GoogleApiClient.OnConnectionFailedListener{

    //Code For Location Picker - Added on 20/04/19
    private GoogleApiClient mGoogleApiClient;
    private int PLACE_PICKER_REQUEST = 1;
    TextView tvPlaceDetails;
    String city1, state1, country1, pincode1, latitudenew, longitudenew;
    //Ends here

    Toolbar toolbar;
    Spinner spinner_cat, spinner_pupose, spinner_fur;
    //Spinner spinner_suitable_for;
    MultiSpinnerSearch spinner_suitable_for;


    ArrayList<ItemType> mListType;
    ArrayList<String> mPropertyName, mPropertyPurpose;
    //ArrayList<String>  mPropertySuitableFor;
    ImageView img_gallery;
    RelativeLayout lay_gallery;
    Button lay_submit, locationbutton;
    TextView txtSelect3;
    private int REQUEST_FEATURED_PICKER_GALLERY = 2003;
    boolean isFeaturedGallery = false;
    private ArrayList<Image> featuredImages_gallery = new ArrayList<>();
    ProgressDialog pDialog;
    String strMessage;
    String srt_type[],srt_fur[], srt_suitable_for[];

    @Required(order = 1, message = "Enter Property Name")
    EditText edtPurposeName;

    @Required(order = 2, message = "Enter Property Description")
    EditText edtPurposeDesc;

    @Required(order = 3, message = "Enter Phone")
    EditText edtPurposePhone;

    @Required(order = 4, message = "Enter Address")
    EditText edtPurposeAddress;

    TextView edtPurposeLatitude;
    TextView edtPurposeLongitude;

    @Required(order = 5, message = "Enter Pincode")
    EditText edtPurposePincode;

    @Required(order = 6, message = "Enter City")
    EditText edtPurposeCity;

    @Required(order = 7, message = "Enter State")
    EditText edtPurposeState;

    @Required(order = 8, message = "Enter Bedrooms")
    EditText edtPurposeBedroom;

    @Required(order = 9, message = "Enter Bathrooms")
    EditText edtPurposeBathroom;

    @Required(order = 10, message = "Enter Area")
    EditText edtPurposeArea;

    EditText edtPurposeAmenity;

    @Required(order = 11, message = "Enter Price")
    EditText edtPurposePrice;
    private Validator validator;
    MyApplication MyApp;
    JsonUtils jsonUtils;

    private static final String TAG = "Harish_Test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_properties);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.menu_add_properties));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        MyApp = MyApplication.getInstance();




        //Spinner code
        final List<String> list = Arrays.asList(getResources().getStringArray(R.array.suitable_for_array));

        final List<KeyPairBoolData> listArray0 = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            KeyPairBoolData h = new KeyPairBoolData();
            h.setId(i + 1);
            h.setName(list.get(i));
            h.setSelected(false);
            listArray0.add(h);
        }

        spinner_suitable_for = (MultiSpinnerSearch) findViewById(R.id.spPropertySuitableFor);
        spinner_suitable_for.setItems(listArray0, -1, new SpinnerListener() {

            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {

                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isSelected()) {
                        Log.i(TAG, i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                    }
                }
            }
        });


        jsonUtils = new JsonUtils(this);
        jsonUtils.forceRTLIfSupported(getWindow());

        mListType = new ArrayList<>();
        mPropertyName = new ArrayList<>();
        mPropertyPurpose = new ArrayList<>();
        //mPropertySuitableFor = new ArrayList<>();
        srt_type=getResources().getStringArray(R.array.purpose_array);
        srt_suitable_for=getResources().getStringArray(R.array.suitable_for_array);
        srt_fur=getResources().getStringArray(R.array.fur_array);

        pDialog = new ProgressDialog(AddPropertiesActivity.this);
        spinner_cat = findViewById(R.id.spPropertyType);
        spinner_pupose = findViewById(R.id.spPropertyPurpose);
        //spinner_suitable_for = findViewById(R.id.spPropertySuitableFor);
        spinner_fur=findViewById(R.id.spPropertyFur);
        edtPurposeName = findViewById(R.id.edt_Purpose_name);
        edtPurposeDesc = findViewById(R.id.edt_Purpose_desc);
        edtPurposePhone = findViewById(R.id.edt_Purpose_phone);

        edtPurposeAddress = findViewById(R.id.edt_Purpose_address);
        edtPurposeLatitude = findViewById(R.id.edt_Purpose_latitude);
        edtPurposeLongitude = findViewById(R.id.edt_Purpose_longitude);
        edtPurposePincode = findViewById(R.id.edt_Purpose_pincode);
        edtPurposeState = findViewById(R.id.edt_Purpose_state);
        edtPurposeCity = findViewById(R.id.edt_Purpose_city);

        edtPurposeBedroom = findViewById(R.id.edt_Purpose_bedroom);
        edtPurposeBathroom = findViewById(R.id.edt_Purpose_bathroom);
        edtPurposeArea = findViewById(R.id.edt_Purpose_area);
        edtPurposeAmenity = findViewById(R.id.edt_Purpose_amenity);
        edtPurposePrice = findViewById(R.id.edt_Purpose_price);

        img_gallery = findViewById(R.id.image_add_gallery);
        lay_gallery = findViewById(R.id.lay_rel_gallery);
        lay_submit = findViewById(R.id.btn_sub);
        locationbutton = findViewById(R.id.locationbutton);

        txtSelect3 = findViewById(R.id.text_select3);

        txtSelect3.setVisibility(View.VISIBLE);


        //maps
        tvPlaceDetails = findViewById(R.id.placeDetails);

        if (JsonUtils.isNetworkAvailable(AddPropertiesActivity.this)) {
            new getType().execute(Constant.PROPERTIES_TYPE);
        }

        if(edtPurposeLatitude.getText().toString().length() == 0 )
        {
            edtPurposeLatitude.setText(getString(R.string.default_latitude));
        }

        if(edtPurposeLongitude.getText().toString().length() == 0 )
        {
            edtPurposeLongitude.setText(getString(R.string.default_longitude));
        }

        lay_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validator.validateAsync();
            }

        });

        lay_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFeaturedImageGallery();
            }
        });

        validator = new Validator(this);
        validator.setValidationListener(this);

        //Code For Maps

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();


        locationbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(AddPropertiesActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Snackbar.make(locationbutton, connectionResult.getErrorMessage() + "", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onValidationSucceeded() {
        if(isFeaturedGallery)
        {
            if (JsonUtils.isNetworkAvailable(AddPropertiesActivity.this)) {
                uploadData();
            } else {
                showToast(getString(R.string.network_msg));
            }
        }
        else
        {
            Toast.makeText(AddPropertiesActivity.this,getResources().getString(R.string.select_image),Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onValidationFailed(View failedView, Rule<?> failedRule) {
        String message = failedRule.getFailureMessage();
        if (failedView instanceof EditText) {
            failedView.requestFocus();
            ((EditText) failedView).setError(message);
        } else {
            Toast.makeText(this, "Record Not Saved", Toast.LENGTH_SHORT).show();
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
                displayData();
            }
        }
    }

    private void displayData() {
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(AddPropertiesActivity.this, R.layout.spinner_item, mPropertyName);
        typeAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner_cat.setAdapter(
                new NothingSelectedSpinnerAdapter(typeAdapter,
                        R.layout.contact_spinner_row_nothing_selected,
                        // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                        AddPropertiesActivity.this));
        spinner_cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                if (position == 0) {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.add_properties_text));
                    ((TextView) parent.getChildAt(0)).setTextSize(14);

                } else {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.add_properties_text));
                    ((TextView) parent.getChildAt(0)).setTextSize(14);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        ArrayAdapter<String> typeAdapter2 = new ArrayAdapter<>(AddPropertiesActivity.this, R.layout.spinner_item, srt_type);
        typeAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner_pupose.setAdapter(
                new NothingSelectedSpinnerAdapter(typeAdapter2,
                        R.layout.contact_spinner_row_nothing_selected_cat,
                        // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                        AddPropertiesActivity.this));
        spinner_pupose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                if (position == 0) {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.add_properties_text));
                    ((TextView) parent.getChildAt(0)).setTextSize(14);

                } else {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.add_properties_text));
                    ((TextView) parent.getChildAt(0)).setTextSize(14);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });


        //Code For Suitable For
        /*ArrayAdapter<String> typeAdapter25 = new ArrayAdapter<>(AddPropertiesActivity.this, R.layout.spinner_item, srt_suitable_for);
        typeAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner_suitable_for.setAdapter(
                new NothingSelectedSpinnerAdapter(typeAdapter25,
                        R.layout.contact_spinner_row_nothing_selected_suitable,
                        // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                        AddPropertiesActivity.this));
        spinner_suitable_for.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                if (position == 0) {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.add_properties_text));
                    ((TextView) parent.getChildAt(0)).setTextSize(14);

                } else {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.add_properties_text));
                    ((TextView) parent.getChildAt(0)).setTextSize(14);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });*/

        //Ends Here

        ArrayAdapter<String> typeAdapterFur = new ArrayAdapter<>(AddPropertiesActivity.this, R.layout.spinner_item, srt_fur);
        typeAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner_fur.setAdapter(typeAdapterFur);
        spinner_fur.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                if (position == 0) {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.add_properties_text));
                    ((TextView) parent.getChildAt(0)).setTextSize(14);

                } else {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.add_properties_text));
                    ((TextView) parent.getChildAt(0)).setTextSize(14);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
    }


    /*public void chooseFeaturedImage() {
        ImagePicker.create(this)
                .folderMode(true)
                .folderTitle("Folder")
                .imageTitle("Tap to select")
                .single()
                .limit(1)
                .showCamera(false)
                .imageDirectory("Camera")
                .start(REQUEST_FEATURED_PICKER);
    }*/

    /*public void chooseFeaturedImagePlan() {
        ImagePicker.create(this)
                .folderMode(true)
                .folderTitle("Folder")
                .imageTitle("Tap to select")
                .single()
                .limit(1)
                .showCamera(false)
                .imageDirectory("Camera")
                .start(REQUEST_FEATURED_PICKER_PLAN);
    }*/

    public void chooseFeaturedImageGallery() {
        ImagePicker.create(this)
                .folderMode(true)
                .folderTitle("Folder")
                .imageTitle("Tap to select")
                .multi()
                .limit(5)
                .showCamera(false)
                .imageDirectory("Camera")
                .start(REQUEST_FEATURED_PICKER_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Maps
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = getPlace(this, data);
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());

                StringBuilder stBuilder = new StringBuilder();

                latitudenew = String.valueOf(place.getLatLng().latitude);
                longitudenew = String.valueOf(place.getLatLng().longitude);
                String address = String.format("%s", place.getAddress());


                try
                {
                    List<Address> addresses = geocoder.getFromLocation(place.getLatLng().latitude,place.getLatLng().longitude, 1);
                    city1 = addresses.get(0).getLocality();
                    state1 = addresses.get(0).getAdminArea();
                    country1 = addresses.get(0).getCountryName();
                    pincode1 = addresses.get(0).getPostalCode();

                } catch (IOException e)
                {

                    e.printStackTrace();
                }

                stBuilder.append("\n");
                stBuilder.append("Latitude: ");
                stBuilder.append(latitudenew);
                stBuilder.append("\n");
                stBuilder.append("Logitude: ");
                stBuilder.append(longitudenew);
                stBuilder.append("\n");
                stBuilder.append("Address: ");
                stBuilder.append(address);

                /*tvPlaceDetails.setText(stBuilder.toString());
                edtPurposeLatitude.setText(latitudenew);
                edtPurposeLongitude.setText(longitudenew);
                */
                edtPurposeCity.setText(city1);
                edtPurposeState.setText(state1);
                edtPurposePincode.setText(pincode1);

            }
        }
        //Ends here

        /*else if (requestCode == REQUEST_FEATURED_PICKER) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                featuredImages = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
                Uri uri = Uri.fromFile(new File(featuredImages.get(0).getPath()));
                Picasso.get().load(uri).into(img_feature);
                isFeatured = true;
                txtSelect1.setVisibility(View.GONE);

            }
        }*/ /*else if (requestCode == REQUEST_FEATURED_PICKER_PLAN) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                featuredImages_plan = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
                Uri uri = Uri.fromFile(new File(featuredImages_plan.get(0).getPath()));
                Picasso.get().load(uri).into(img_plan);
                isFeaturedPlan = true;
                txtSelect2.setVisibility(View.GONE);

            }
        }*/ else if (requestCode == REQUEST_FEATURED_PICKER_GALLERY) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                featuredImages_gallery = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
                Uri uri = Uri.fromFile(new File(featuredImages_gallery.get(0).getPath()));
                Picasso.get().load(uri).into(img_gallery);
                isFeaturedGallery = true;
                txtSelect3.setVisibility(View.GONE);
            }
        }

    }

    public void uploadData() {

        Request request = Request.create(Constant.UPLOAD_PROPERTIES_URL);
        request.setMethod("POST")
                .setTimeout(120)
                .setLogger(new Logger(Logger.ERROR))
                .addParameter("user_id", MyApp.getUserId())
                .addParameter("type_id", mListType.get(spinner_cat.getSelectedItemPosition()).getTypeId())
                .addParameter("place_purpose", String.valueOf(spinner_pupose.getSelectedItem()))
                .addParameter("suitable_for", String.valueOf(spinner_suitable_for.getSelectedItem()))
                .addParameter("furnishing", String.valueOf(spinner_fur.getSelectedItem()))
                .addParameter("place_name", edtPurposeName.getText().toString())
                .addParameter("place_description", edtPurposeDesc.getText().toString())
                .addParameter("place_bed", edtPurposeBedroom.getText().toString())
                .addParameter("place_bath", edtPurposeBathroom.getText().toString())
                .addParameter("place_area", edtPurposeArea.getText().toString())
                .addParameter("place_amenities", "Water, Electricity")
                .addParameter("place_price", edtPurposePrice.getText().toString())
                .addParameter("place_phone", edtPurposePhone.getText().toString())
                .addParameter("place_address", edtPurposeAddress.getText().toString())
                .addParameter("place_pincode", edtPurposePincode.getText().toString())
                .addParameter("place_city", edtPurposeCity.getText().toString())
                .addParameter("place_state", edtPurposeState.getText().toString())
                .addParameter("place_map_latitude", latitudenew)
                .addParameter("place_map_longitude", longitudenew);
        request.addParameter("place_image", new File(featuredImages_gallery.get(0).getPath()));


        /*if (isFeatured) {

        }*/
        /*if (isFeaturedPlan) {
            request.addParameter("place_floor_plan", new File(featuredImages_plan.get(0).getPath()));
        }*/
        if (isFeaturedGallery) {
            request.addParameter("place_gallery_image[]", featuredImages_gallery);
        }
        request.setFileUploadListener(new FileUploadListener() {
            @Override
            public void onUploadingFile(File file, long size, long uploaded) {

            }
        })
                .setRequestStateListener(new RequestStateListener() {
                    @Override
                    public void onStart() {
                        showProgressDialog();
                    }

                    @Override
                    public void onFinish() {
                        dismissProgressDialog();
                    }

                    @Override
                    public void onConnectionError(Exception e) {
                        e.printStackTrace();
                    }
                })
                .setResponseListener(new JsonResponseListener() {
                    @Override
                    public void onOkResponse(JSONObject jsonObject) throws JSONException {
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.ARRAY_NAME);
                        JSONObject objJson = jsonArray.getJSONObject(0);
                        Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                        strMessage = objJson.getString(Constant.MSG);
                        setResult();
                    }

                    @Override
                    public void onErrorResponse(JSONObject jsonObject) {

                    }

                    @Override
                    public void onParseError(JSONException e) {

                    }
                }).execute();
    }

    public void setResult() {

        if (Constant.GET_SUCCESS_MSG == 0) {

            showToast(strMessage);
        } else {
            showToast(strMessage);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    public void showProgressDialog() {
        pDialog.setMessage("Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    public void dismissProgressDialog() {
        pDialog.dismiss();
    }

    public void showToast(String msg) {
        Toast.makeText(AddPropertiesActivity.this, msg, Toast.LENGTH_LONG).show();
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

