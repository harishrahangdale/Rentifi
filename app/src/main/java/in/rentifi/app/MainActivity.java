package in.rentifi.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import in.example.fragment.AllPropertiesFragment;
import in.example.fragment.FavouriteFragment;
import in.example.fragment.HomeFragment;
import in.example.fragment.LatestFragment;
import in.example.fragment.MyPropertiesFragment;
import in.example.fragment.SettingFragment;
import in.example.item.ItemAbout;
import in.example.item.ItemType;
import in.example.util.Constant;
import in.example.util.JsonUtils;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.consent.DebugGeography;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.irfaan008.irbottomnavigation.SpaceItem;
import com.irfaan008.irbottomnavigation.SpaceNavigationView;
import com.irfaan008.irbottomnavigation.SpaceOnClickListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private DrawerLayout drawerLayout;
    private FragmentManager fragmentManager;
    MyApplication MyApp;
    NavigationView navigationView;
    Toolbar toolbar;
    ArrayList<ItemType> mListType;
    ArrayList<String> mPropertyName;
    JsonUtils jsonUtils;
    ArrayList<ItemAbout> mListItem;
    LinearLayout adLayout;
    private ConsentForm form;
    public SpaceNavigationView spaceNavigationView;
    private Location mylocation;
    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        jsonUtils = new JsonUtils(this);
        jsonUtils.forceRTLIfSupported(getWindow());

        fragmentManager = getSupportFragmentManager();
        MyApp = MyApplication.getInstance();

        //Code for navigation header
        navigationView = findViewById(R.id.navigation_view);
        View hView =  navigationView.inflateHeaderView(R.layout.left_nav_header);
        ImageView imgvw = (ImageView)hView.findViewById(R.id.header_image);
        TextView tv = (TextView)hView.findViewById(R.id.header_name);
        TextView tv1 = (TextView)hView.findViewById(R.id.header_email);
        imgvw .setImageResource(R.drawable.ic_profile);
        tv.setText("Harish");


        drawerLayout = findViewById(R.id.drawer_layout);
        adLayout = findViewById(R.id.adview);
        setUpGClient();
        mListType = new ArrayList<>();
        mPropertyName = new ArrayList<>();
        mListItem = new ArrayList<>();

        HomeFragment homeFragment = new HomeFragment();
        fragmentManager.beginTransaction().replace(R.id.Container, homeFragment).commit();

        spaceNavigationView = findViewById(R.id.space);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem(getString(R.string.menu_home), R.drawable.ic_home));
        spaceNavigationView.addSpaceItem(new SpaceItem(getString(R.string.menu_latest), R.drawable.ic_latest));
        spaceNavigationView.addSpaceItem(new SpaceItem(getString(R.string.menu_favourite), R.drawable.ic_favourite));
        spaceNavigationView.addSpaceItem(new SpaceItem(getString(R.string.menu_setting), R.drawable.ic_setting));

        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Intent intent = new Intent(MainActivity.this, AddPropertiesActivity.class);
                startActivity(intent);
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                switch (itemIndex) {
                    case 0:
                        toolbar.setTitle(getString(R.string.menu_home));
                        HomeFragment homeFragment = new HomeFragment();
                        fragmentManager.beginTransaction().replace(R.id.Container, homeFragment).commit();
                        highLightNavigation(0, getString(R.string.menu_home));
                         break;
                    case 1:
                        toolbar.setTitle(getString(R.string.menu_latest));
                        LatestFragment latestFragment = new LatestFragment();
                        fragmentManager.beginTransaction().replace(R.id.Container, latestFragment).commit();
                        highLightNavigation(1 ,getString(R.string.menu_latest));
                         break;
                    case 2:
                        toolbar.setTitle(getString(R.string.menu_favourite));
                        FavouriteFragment favouriteFragment = new FavouriteFragment();
                        fragmentManager.beginTransaction().replace(R.id.Container, favouriteFragment).commit();
                        highLightNavigation(3, getString(R.string.menu_favourite));
                         break;
                    case 3:
                        toolbar.setTitle(getString(R.string.menu_setting));
                        SettingFragment settingFragment = new SettingFragment();
                        fragmentManager.beginTransaction().replace(R.id.Container, settingFragment).commit();
                        highLightNavigation(8, getString(R.string.menu_setting));
                        break;
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.menu_go_home:
                        toolbar.setTitle(getString(R.string.menu_home));
                        HomeFragment homeFragment = new HomeFragment();
                        fragmentManager.beginTransaction().replace(R.id.Container, homeFragment).commit();
                        spaceNavigationView.changeCurrentItem(0);
                        return true;
                    case R.id.menu_go_latest:
                        toolbar.setTitle(getString(R.string.menu_latest));
                        LatestFragment latestFragment = new LatestFragment();
                        fragmentManager.beginTransaction().replace(R.id.Container, latestFragment).commit();
                        spaceNavigationView.changeCurrentItem(1);
                        return true;
                    case R.id.menu_go_property:
                        toolbar.setTitle(getString(R.string.menu_property));
                        AllPropertiesFragment allPropertiesFragment = new AllPropertiesFragment();
                        fragmentManager.beginTransaction().replace(R.id.Container, allPropertiesFragment).commit();
                        spaceNavigationView.changeCurrentItem(-1);
                        return true;
                    case R.id.menu_go_favourite:
                        toolbar.setTitle(getString(R.string.menu_favourite));
                        FavouriteFragment favouriteFragment = new FavouriteFragment();
                        fragmentManager.beginTransaction().replace(R.id.Container, favouriteFragment).commit();
                        spaceNavigationView.changeCurrentItem(2);
                        return true;
                     case R.id.menu_go_my_properties:
                        if (MyApp.getIsLogin()) {
                            toolbar.setTitle(getString(R.string.menu_my_properties));
                            MyPropertiesFragment myPropertiesFragment = new MyPropertiesFragment();
                            fragmentManager.beginTransaction().replace(R.id.Container, myPropertiesFragment).commit();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                            startActivity(intent);
                            finish();
                        }
                         spaceNavigationView.changeCurrentItem(-1);
                        return true;
                    case R.id.menu_go_add_properties:
                        if (MyApp.getIsLogin()) {
                            Intent intent = new Intent(MainActivity.this, AddPropertiesActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        return true;

                    case R.id.menu_go_profile:
                        Intent profile = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(profile);
                        return true;
                    case R.id.menu_go_contact:
                        Intent contact = new Intent(MainActivity.this, ContactUsActivity.class);
                        startActivity(contact);
                        return true;
                    case R.id.menu_go_setting:
                        toolbar.setTitle(getString(R.string.menu_setting));
                        SettingFragment settingFragment = new SettingFragment();
                        fragmentManager.beginTransaction().replace(R.id.Container, settingFragment).commit();
                        spaceNavigationView.changeCurrentItem(3);
                        return true;
                    case R.id.menu_go_login:
                        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        return true;
                      case R.id.menu_go_logout:
                        Logout();
                        return true;

                    default:
                        return true;
                }
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        if (MyApp.getIsLogin()) {
            navigationView.getMenu().findItem(R.id.menu_go_login).setVisible(false);
            navigationView.getMenu().findItem(R.id.menu_go_logout).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.menu_go_login).setVisible(true);
            navigationView.getMenu().findItem(R.id.menu_go_logout).setVisible(false);
        }

        if (!MyApp.getIsLogin()) {
            navigationView.getMenu().findItem(R.id.menu_go_profile).setVisible(false);
            navigationView.getMenu().findItem(R.id.menu_go_logout).setVisible(false);
        }

        if (JsonUtils.isNetworkAvailable(MainActivity.this)) {
            new MyTaskAbout().execute(Constant.ABOUT_URL);
        }
    }

    public void highLightNavigation(int position, String name) {

        navigationView.getMenu().getItem(position).setChecked(true);
        toolbar.setTitle(name);
    }

    @SuppressLint("StaticFieldLeak")
    private class MyTaskAbout extends AsyncTask<String, Void, String> {

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
                showToast(getString(R.string.nodata));
            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        ItemAbout itemAbout = new ItemAbout();

                        itemAbout.setappBannerId(objJson.getString(Constant.ADS_BANNER_ID));
                        itemAbout.setappFullId(objJson.getString(Constant.ADS_FULL_ID));
                        itemAbout.setappBannerOn(objJson.getString(Constant.ADS_BANNER_ON_OFF));
                        itemAbout.setappFullOn(objJson.getString(Constant.ADS_FULL_ON_OFF));
                        itemAbout.setappFullPub(objJson.getString(Constant.ADS_PUB_ID));
                        itemAbout.setappFullAdsClick(objJson.getString(Constant.ADS_CLICK));
                        mListItem.add(itemAbout);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult();
            }
        }
    }

    private void setResult() {

        ItemAbout itemAbout = mListItem.get(0);

        Constant.SAVE_ADS_BANNER_ID = itemAbout.getappBannerId();
        Constant.SAVE_ADS_FULL_ID = itemAbout.getappFullId();
        Constant.SAVE_ADS_BANNER_ON_OFF = itemAbout.getappBannerOn();
        Constant.SAVE_ADS_FULL_ON_OFF = itemAbout.getappFullOn();
        Constant.SAVE_ADS_PUB_ID = itemAbout.getappFullPub();
        Constant.SAVE_ADS_CLICK = itemAbout.getappFullAdsClick();

        checkForConsent();
        if (JsonUtils.isNetworkAvailable(MainActivity.this)) {
            new getType().execute(Constant.PROPERTIES_TYPE);
        }
    }

    public void showToast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        ExitApp();
     }

    private void ExitApp() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.exit_msg))
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    private void Logout() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.menu_logout))
                .setMessage(getString(R.string.logout_msg))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MyApp.saveIsLogin(false);
                        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                //  .setIcon(R.drawable.ic_logout)
                .show();
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
            }
        }
    }


    public void checkForConsent() {

       // ConsentInformation.getInstance(MainActivity.this).addTestDevice("65C855CE481F45A609DAC8C6E8951D53");
        // Geography appears as in EEA for test devices.
       // ConsentInformation.getInstance(MainActivity.this).setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);
        // Geography appears as not in EEA for debug devices.
        ConsentInformation consentInformation = ConsentInformation.getInstance(MainActivity.this);
        String[] publisherIds = {Constant.SAVE_ADS_PUB_ID};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                Log.d("consentStatus", consentStatus.toString());
                // User's consent status successfully updated.
                switch (consentStatus) {
                    case PERSONALIZED:
                        JsonUtils.personalization_ad = true;
                        JsonUtils.showPersonalizedAds(adLayout, MainActivity.this);
                        break;
                    case NON_PERSONALIZED:
                        JsonUtils.personalization_ad = false;
                        JsonUtils.showNonPersonalizedAds(adLayout, MainActivity.this);
                        break;
                    case UNKNOWN:
                        if (ConsentInformation.getInstance(getBaseContext())
                                .isRequestLocationInEeaOrUnknown()) {
                            requestConsent();
                        } else {
                            JsonUtils.personalization_ad = true;
                            JsonUtils.showPersonalizedAds(adLayout, MainActivity.this);
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update.
            }
        });

    }

    public void requestConsent() {
        URL privacyUrl = null;
        try {
            // TODO: Replace with your app's privacy policy URL.
            privacyUrl = new URL(getString(R.string.gdpr_privacy_link));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            // Handle error.
        }
        form = new ConsentForm.Builder(MainActivity.this, privacyUrl)
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        showForm();
                        // Consent form loaded successfully.
                    }

                    @Override
                    public void onConsentFormOpened() {
                        // Consent form was displayed.
                    }

                    @Override
                    public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                        Log.d("consentStatus_form", consentStatus.toString());
                        switch (consentStatus) {
                            case PERSONALIZED:
                                JsonUtils.personalization_ad = true;
                                JsonUtils.showPersonalizedAds(adLayout, MainActivity.this);
                                break;
                            case NON_PERSONALIZED:
                                JsonUtils.personalization_ad = false;
                                JsonUtils.showNonPersonalizedAds(adLayout, MainActivity.this);
                                break;
                            case UNKNOWN:
                                JsonUtils.personalization_ad = false;
                                JsonUtils.showNonPersonalizedAds(adLayout, MainActivity.this);
                        }
                    }

                    @Override
                    public void onConsentFormError(String errorDescription) {
                        Log.d("errorDescription", errorDescription);
                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .build();
        form.load();
    }

    private void showForm() {
        if (form != null) {
            form.show();
        }
    }

    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mylocation = location;
        if (mylocation != null) {
            Double latitude = mylocation.getLatitude();
            Double longitude = mylocation.getLongitude();
            Constant.USER_LATITUDE = String.valueOf(latitude);
            Constant.USER_LONGITUDE = String.valueOf(longitude);
         }
    }

    @Override
    public void onConnected(Bundle bundle) {
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS_GPS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getMyLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        permissionReject();
                        break;
                }
                break;
        }
    }

    private void checkPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        } else {
            getMyLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        } else {
            permissionReject();
        }
    }

    private void permissionReject() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.location_permission))
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

    private void getMyLocation() {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(1000 * 1000);
                    locationRequest.setFastestInterval(1000 * 1000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(googleApiClient, locationRequest, this);
                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(MainActivity.this,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        status.startResolutionForResult(MainActivity.this,
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied.
                                    // However, we have no way
                                    // to fix the
                                    // settings so we won't show the dialog.
                                    // finish();
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }
}
