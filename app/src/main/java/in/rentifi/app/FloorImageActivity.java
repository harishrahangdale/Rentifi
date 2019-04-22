package in.rentifi.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import in.example.util.JsonUtils;
import in.example.util.TouchImageView;
import com.squareup.picasso.Picasso;

public class FloorImageActivity extends AppCompatActivity {

    String image_floor;
    Toolbar toolbar;
    JsonUtils jsonUtils;
    TouchImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.row_floor_image);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.property_floor_plan));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        jsonUtils = new JsonUtils(this);
        jsonUtils.forceRTLIfSupported(getWindow());

        Intent intent = getIntent();
        image_floor = intent.getStringExtra("ImageF");

        imageView = findViewById(R.id.iv_wall_details);
        Picasso.get().load(image_floor).placeholder(R.drawable.header_top_logo).into(imageView);

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
