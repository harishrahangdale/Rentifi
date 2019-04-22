package in.rentifi.app;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import in.example.util.Constant;
import in.example.util.JsonUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.gavinliu.android.lib.shapedimageview.ShapedImageView;


public class ProfileActivity extends AppCompatActivity {


    TextView edtFullName;
    TextView edtEmail;
    TextView edtMobile,text_address;
    MyApplication MyApp;
    JsonUtils jsonUtils;
    Toolbar toolbar;
    ShapedImageView image_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.menu_profile));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        jsonUtils = new JsonUtils(this);
        jsonUtils.forceRTLIfSupported(getWindow());

        MyApp = MyApplication.getInstance();

        edtFullName = findViewById(R.id.text_name);
        edtEmail = findViewById(R.id.text_email);
        edtMobile = findViewById(R.id.text_telephone);
        image_profile=findViewById(R.id.image_profile);
        text_address=findViewById(R.id.text_address);

        if (JsonUtils.isNetworkAvailable(ProfileActivity.this)) {
            new MyTask().execute(Constant.USER_PROFILE_URL + MyApp.getUserId());
        } else {
            showToast(getString(R.string.conne_msg1));
        }

    }

    @SuppressLint("StaticFieldLeak")
    private class MyTask extends AsyncTask<String, Void, String> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(ProfileActivity.this);
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
                showToast(getString(R.string.nodata));
            } else {
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        edtFullName.setText(objJson.getString(Constant.USER_NAME));
                        edtEmail.setText(objJson.getString(Constant.USER_EMAIL));
                        edtMobile.setText(objJson.getString(Constant.USER_PHONE));
                        text_address.setText(objJson.getString(Constant.USER_ADDRESS));
                        String str_image=objJson.getString(Constant.USER_IMAGE);
                        if(str_image.isEmpty()) {
                            Picasso.get().load(R.drawable.ic_profile).into(image_profile);
                         }else {
                            Picasso.get().load("" +objJson.getString(Constant.USER_IMAGE)).into(image_profile);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void showToast(String msg) {
        Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_edit:
                Intent intent_profile = new Intent(ProfileActivity.this, ProfileEditActivity.class);
                startActivity(intent_profile);
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }

        return true;
    }
}
