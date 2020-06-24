package in.oriange.joinstagharse.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import in.oriange.joinstagharse.R;

import static in.oriange.joinstagharse.utilities.Utilities.changeStatusBar;

public class PolicyActivity extends AppCompatActivity {

    private Context context;
    private CardView cv_tandc, cv_tandc_vendor, cv_tandc_customer, cv_ed, cv_pp, cv_smp, cv_ipp, cv_g, cv_mad, cv_ua;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);

        init();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = PolicyActivity.this;
        changeStatusBar(context, getWindow());
        cv_tandc = findViewById(R.id.cv_tandc);
        cv_tandc_vendor = findViewById(R.id.cv_tandc_vendor);
        cv_tandc_customer = findViewById(R.id.cv_tandc_customer);
        cv_ed = findViewById(R.id.cv_ed);
        cv_pp = findViewById(R.id.cv_pp);
        cv_smp = findViewById(R.id.cv_smp);
        cv_ipp = findViewById(R.id.cv_ipp);
        cv_g = findViewById(R.id.cv_g);
        cv_mad = findViewById(R.id.cv_mad);
        cv_ua = findViewById(R.id.cv_ua);
    }

    private void setEventHandler() {

        cv_tandc.setOnClickListener(v -> startActivity(new Intent(context, PolicyDetailsActivity.class)
                .putExtra("title", "Terms and Conditions")
                .putExtra("filePath", "termsandconditions.html")));

        cv_tandc_vendor.setOnClickListener(v -> startActivity(new Intent(context, PolicyDetailsActivity.class)
                .putExtra("title", "Terms and Conditions - Vendor")
                .putExtra("filePath", "termsandconditionsvendors.html")));

        cv_tandc_customer.setOnClickListener(v -> startActivity(new Intent(context, PolicyDetailsActivity.class)
                .putExtra("title", "Terms and Conditions - Customer")
                .putExtra("filePath", "termsandconditionscustomers.html")));

        cv_ed.setOnClickListener(v -> startActivity(new Intent(context, PolicyDetailsActivity.class)
                .putExtra("title", "Email Disclaimer")
                .putExtra("filePath", "emaildisclaimer.html")));

        cv_pp.setOnClickListener(v -> startActivity(new Intent(context, PolicyDetailsActivity.class)
                .putExtra("title", "Privacy Policy")
                .putExtra("filePath", "privacypolicy.html")));

        cv_smp.setOnClickListener(v -> startActivity(new Intent(context, PolicyDetailsActivity.class)
                .putExtra("title", "Social Media Policy")
                .putExtra("filePath", "socialmedia.html")));

        cv_ipp.setOnClickListener(v -> startActivity(new Intent(context, PolicyDetailsActivity.class)
                .putExtra("title", "IP Policy")
                .putExtra("filePath", "ippolicy.html")));

        cv_g.setOnClickListener(v -> startActivity(new Intent(context, PolicyDetailsActivity.class)
                .putExtra("title", "Grievance")
                .putExtra("filePath", "grievance.html")));

        cv_mad.setOnClickListener(v -> startActivity(new Intent(context, PolicyDetailsActivity.class)
                .putExtra("title", "Mobile App Disclaimer")
                .putExtra("filePath", "appdisclaimer.html")));

        cv_ua.setOnClickListener(v -> startActivity(new Intent(context, PolicyDetailsActivity.class)
                .putExtra("title", "User Agreement")
                .putExtra("filePath", "useragreement.html")));
    }

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_black);
        mToolbar.setNavigationOnClickListener(view -> finish());
    }
}
