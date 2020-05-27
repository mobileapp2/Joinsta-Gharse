package in.oriange.eorder.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.eorder.R;
import in.oriange.eorder.models.VendorModel;
import in.oriange.eorder.utilities.APICall;
import in.oriange.eorder.utilities.ApplicationConstants;
import in.oriange.eorder.utilities.UserSessionManager;
import in.oriange.eorder.utilities.Utilities;

import static in.oriange.eorder.utilities.Utilities.changeStatusBar;

public class ViewVendorDetailsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_city)
    TextView tvCity;
    @BindView(R.id.tv_prime)
    TextView tvPrime;
    @BindView(R.id.tv_business_code_name)
    TextView tvBusinessCodeName;
    @BindView(R.id.tv_mobile)
    TextView tvMobile;
    @BindView(R.id.tv_email)
    TextView tvEmail;
    @BindView(R.id.cv_contact_details)
    CardView cvContactDetails;

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private String userId;
    private VendorModel.ResultBean vendorDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_vendor_details);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventListner();
        setUpToolbar();
    }

    private void init() {
        context = ViewVendorDetailsActivity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        changeStatusBar(context, getWindow());
    }

    private void getSessionDetails() {
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);

            userId = json.getString("userid");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDefault() {
        vendorDetails = (VendorModel.ResultBean) getIntent().getSerializableExtra("vendorDetails");

        tvName.setText(vendorDetails.getVendorCodeName());
        tvCity.setText(vendorDetails.getCity());

        if (!vendorDetails.getBusiness_name().equals(""))
            tvBusinessCodeName.setText("Business: " + vendorDetails.getBusiness_code() + " - " + vendorDetails.getBusiness_name());
        else
            tvBusinessCodeName.setVisibility(View.GONE);

        if (!vendorDetails.getEmail().equals(""))
            tvEmail.setText(vendorDetails.getEmail());
        else
            tvEmail.setVisibility(View.GONE);

        tvMobile.setText(vendorDetails.getCountry_code() + vendorDetails.getMobile());

        if (vendorDetails.getIs_prime_vendor().equals("1"))
            tvPrime.setVisibility(View.VISIBLE);
        else
            tvPrime.setVisibility(View.GONE);
    }

    private void setEventListner() {
        tvMobile.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
            builder.setMessage("Are you sure you want to make a call?");
            builder.setTitle("Alert");
            builder.setIcon(R.drawable.icon_call);
            builder.setCancelable(false);
            builder.setPositiveButton("YES", (dialog, id) -> startActivity(new Intent(Intent.ACTION_CALL,
                    Uri.parse("tel:" + tvMobile.getText().toString().trim()))));
            builder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
            AlertDialog alertD = builder.create();
            alertD.show();
        });

        tvEmail.setOnClickListener(v -> sendEmail());
    }

    private void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", vendorDetails.getEmail(), null));
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menus_edit_delete, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setMessage("Are you sure you want to delete this vendor?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.icon_alertred);
                builder.setCancelable(false);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new DeleteVendor().execute();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertD = builder.create();
                alertD.show();
                break;
            case R.id.action_edit:
                startActivity(new Intent(context, EditVendorActivity.class)
                        .putExtra("vendorDetails", vendorDetails));
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    public class DeleteVendor extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "deleteVendor");
            obj.addProperty("id", vendorDetails.getId());
            res = APICall.JSONAPICall(ApplicationConstants.VENDORAPI, obj.toString());
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("VendorsFragment"));
                        Utilities.showMessage("Vendor deleted successfully", context, 1);
                        finish();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.icon_backarrow_black);
        toolbar.setNavigationOnClickListener(view -> finish());
    }
}
