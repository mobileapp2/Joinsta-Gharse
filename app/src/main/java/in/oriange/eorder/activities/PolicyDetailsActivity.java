package in.oriange.eorder.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import in.oriange.eorder.R;

public class PolicyDetailsActivity extends AppCompatActivity {

    private Context context;
    private WebView wv_policies_details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy_details);

        init();
        setDefault();
        setUpToolbar();
    }

    private void init() {
        context = PolicyDetailsActivity.this;
        wv_policies_details = findViewById(R.id.wv_policies_details);
    }

    private void setDefault() {
        wv_policies_details.loadUrl("file:///android_asset/" + getIntent().getStringExtra("filePath"));
    }

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText(getIntent().getStringExtra("title"));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow);
        mToolbar.setNavigationOnClickListener(view -> finish());
    }
}
