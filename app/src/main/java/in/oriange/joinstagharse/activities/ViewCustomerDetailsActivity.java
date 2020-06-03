package in.oriange.joinstagharse.activities;

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
import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.models.CustomerModel;
import in.oriange.joinstagharse.utilities.APICall;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.UserSessionManager;
import in.oriange.joinstagharse.utilities.Utilities;

import static in.oriange.joinstagharse.utilities.Utilities.changeStatusBar;

public class ViewCustomerDetailsActivity extends AppCompatActivity {

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
    private CustomerModel.ResultBean customersDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_customer_details);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventListner();
        setUpToolbar();
    }

    private void init() {
        context = ViewCustomerDetailsActivity.this;
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
        customersDetails = (CustomerModel.ResultBean) getIntent().getSerializableExtra("customersDetails");

        tvName.setText(customersDetails.getCustomerCodeName());
        tvCity.setText(customersDetails.getCity());

        if (!customersDetails.getCity().equals(""))
            tvCity.setText(customersDetails.getCity());
        else
            tvCity.setVisibility(View.GONE);

        if (!customersDetails.getBusiness_name().equals(""))
            tvBusinessCodeName.setText("Business - " + customersDetails.getBusiness_code() + " | " + customersDetails.getBusiness_name());
        else
            tvBusinessCodeName.setVisibility(View.GONE);

        if (!customersDetails.getEmail().equals(""))
            tvEmail.setText(customersDetails.getEmail());
        else
            tvEmail.setVisibility(View.GONE);

        tvMobile.setText(customersDetails.getCountry_code() + customersDetails.getMobile());

        if (customersDetails.getIs_prime_customer().equals("1"))
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
                "mailto", customersDetails.getEmail(), null));
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
                builder.setMessage("Are you sure you want to delete this customer?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.icon_alertred);
                builder.setCancelable(false);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new DeleteCustomer().execute();
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
                startActivity(new Intent(context, EditCustomerActivity.class)
                        .putExtra("customersDetails", customersDetails));
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    public class DeleteCustomer extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "deleteCustomer");
            obj.addProperty("id", customersDetails.getId());
            res = APICall.JSONAPICall(ApplicationConstants.CUSTOMERAPI, obj.toString());
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
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("CustomersFragment"));
                        Utilities.showMessage("Customer deleted successfully", context, 1);
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
