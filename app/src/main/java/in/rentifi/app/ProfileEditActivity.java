package in.rentifi.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import in.example.util.Constant;
import in.example.util.JsonUtils;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
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
import java.util.ArrayList;
import cn.gavinliu.android.lib.shapedimageview.ShapedImageView;


public class ProfileEditActivity extends AppCompatActivity implements Validator.ValidationListener {

    EditText edtFullName;
    EditText edtEmail;
    EditText edtPassword;
    EditText edtMobile;
    EditText edt_address;
    private Validator validator;
    MyApplication MyApp;
    String strFullname, strEmail, strPassword, strMobi, strAddress, strMessage;
    JsonUtils jsonUtils;
    Toolbar toolbar;
    ImageView imageUpload;
    ShapedImageView image_profile;
    private ArrayList<Image> userImage = new ArrayList<>();
    boolean isImage = false;
    ProgressDialog pDialog;
    private int REQUEST_FEATURED_PICKER = 2003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
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
        pDialog = new ProgressDialog(ProfileEditActivity.this);

        edtFullName = findViewById(R.id.edt_name);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        edtMobile = findViewById(R.id.edt_phone);
        imageUpload = findViewById(R.id.imageUpload);
        image_profile = findViewById(R.id.image_profile);
        edt_address = findViewById(R.id.edt_address);
        validator = new Validator(this);
        validator.setValidationListener(this);

        if (JsonUtils.isNetworkAvailable(ProfileEditActivity.this)) {
            new MyTask().execute(Constant.USER_PROFILE_URL + MyApp.getUserId());
        } else {
            showToast(getString(R.string.conne_msg1));
        }
        imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFeaturedImage();
            }
        });

    }

    public void chooseFeaturedImage() {
        ImagePicker.create(this)
                .folderMode(true)
                .folderTitle("Folder")
                .imageTitle("Tap to select")
                .single()
                .limit(1)
                .showCamera(false)
                .imageDirectory("Camera")
                .start(REQUEST_FEATURED_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FEATURED_PICKER) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                userImage = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
                Uri uri = Uri.fromFile(new File(userImage.get(0).getPath()));
                Picasso.get().load(uri).into(image_profile);
                isImage = true;

            }
        }
     }

    @Override
    public void onValidationSucceeded() {
        strPassword = edtPassword.getText().toString();
        if (strPassword.isEmpty()) {
            edtPassword.setError("Enter Password");
        } else {
            if (JsonUtils.isNetworkAvailable(ProfileEditActivity.this)) {
                uploadData();
            } else {
                showToast(getString(R.string.conne_msg1));
            }
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

    private class MyTask extends AsyncTask<String, Void, String> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(ProfileEditActivity.this);
            pDialog.setMessage(getString(R.string.loading_title));
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
                        edt_address.setText(objJson.getString(Constant.USER_ADDRESS));
                        String str_image = objJson.getString(Constant.USER_IMAGE);
                        if (str_image.isEmpty()) {
                            Picasso.get().load(R.drawable.ic_profile).into(image_profile);
                        } else {
                            Picasso.get().load("" + objJson.getString(Constant.USER_IMAGE)).into(image_profile);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void showToast(String msg) {
        Toast.makeText(ProfileEditActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    public void uploadData() {

        strFullname = edtFullName.getText().toString();
        strEmail = edtEmail.getText().toString();
        strPassword = edtPassword.getText().toString();
        strMobi = edtMobile.getText().toString();
        strAddress = edt_address.getText().toString();

        Request request = Request.create(Constant.USER_PROFILE_UPDATE_URL);
        request.setMethod("POST")
                .setTimeout(120)
                .setLogger(new Logger(Logger.ERROR))
                .addParameter("user_id", MyApp.getUserId())
                .addParameter("name", strFullname)
                .addParameter("email", strEmail)
                .addParameter("phone", strMobi)
                .addParameter("address", strAddress)
                .addParameter("password", strPassword);

        if (isImage) {
            request.addParameter("user_image", new File(userImage.get(0).getPath()));
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

    public void showProgressDialog() {
        pDialog.setMessage(getString(R.string.loading_title));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    public void dismissProgressDialog() {
        pDialog.dismiss();
    }

    public void setResult() {

        if (Constant.GET_SUCCESS_MSG == 0) {
            showToast(strMessage);
        } else {
            showToast(strMessage);
            onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
             case R.id.action_edit:
                validator.validateAsync();
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
