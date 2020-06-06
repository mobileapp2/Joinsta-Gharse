package in.oriange.joinstagharse.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.regex.Matcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.utilities.APICall;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.CountryCodeSelection;
import in.oriange.joinstagharse.utilities.UserSessionManager;
import in.oriange.joinstagharse.utilities.Utilities;

import static in.oriange.joinstagharse.utilities.Utilities.changeStatusBar;

public class AddVendorActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edt_code)
    EditText edtCode;
    @BindView(R.id.edt_name)
    EditText edtName;
    @BindView(R.id.tv_countrycode)
    TextView tvCountrycode;
    @BindView(R.id.edt_email)
    EditText edtEmail;
    @BindView(R.id.edt_city)
    EditText edtCity;
    @BindView(R.id.edt_business_code)
    EditText edtBusinessCode;
    @BindView(R.id.ll_business_code)
    LinearLayout llBusinessCode;
    @BindView(R.id.edt_business_name)
    EditText edtBusinessName;
    @BindView(R.id.ll_business_name)
    LinearLayout llBusinessName;
    @BindView(R.id.edt_mobile)
    EditText edtMobile;
    @BindView(R.id.cb_is_prime_vendor)
    CheckBox cbIsPrimeVendor;
    @BindView(R.id.btn_save)
    MaterialButton btnSave;

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vendor);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventListner();
        setUpToolbar();
    }

    private void init() {
        context = AddVendorActivity.this;
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
        edtName.setText(getIntent().getStringExtra("name"));

        String mobile = getIntent().getStringExtra("mobile");
        try {
            if (mobile.length() > 10) {
                mobile = mobile.substring(mobile.length() - 10);
                edtMobile.setText(mobile);
            } else {
                edtMobile.setText(mobile);
            }
        } catch (Exception e) {
            edtMobile.setText("");
        }

        edtBusinessCode.setText(getIntent().getStringExtra("businessCode"));
        edtBusinessName.setText(getIntent().getStringExtra("businessName"));
    }

    private void setEventListner() {
        tvCountrycode.setOnClickListener(v -> new CountryCodeSelection(context, tvCountrycode));

        btnSave.setOnClickListener(v -> submitData());
    }

    private void submitData() {
        if (edtName.getText().toString().trim().isEmpty()) {
            edtName.setError("Please enter vendor name");
            edtName.requestFocus();
            edtName.getParent().requestChildFocus(edtName, edtName);
            return;
        }

        if (!Utilities.isValidMobileno(edtMobile.getText().toString().trim())) {
            edtMobile.setError("Please enter valid mobile number");
            edtMobile.requestFocus();
            edtMobile.getParent().requestChildFocus(edtMobile, edtMobile);
            return;
        }

        if (!edtEmail.getText().toString().trim().isEmpty()) {
            if (!Utilities.isEmailValid(edtEmail.getText().toString().trim())) {
                edtEmail.setError("Please enter valid email");
                edtEmail.requestFocus();
                edtEmail.getParent().requestChildFocus(edtEmail, edtEmail);
                return;
            }
        }

        String isPrimeVendor = cbIsPrimeVendor.isChecked() ? "1" : "0";

        JsonObject mainObj = new JsonObject();
        mainObj.addProperty("type", "addVendor");
        mainObj.addProperty("vendor_code", edtCode.getText().toString().trim());
        mainObj.addProperty("name", edtName.getText().toString().trim());
        mainObj.addProperty("country_code", tvCountrycode.getText().toString().trim());
        mainObj.addProperty("mobile", edtMobile.getText().toString().trim());
        mainObj.addProperty("email", edtEmail.getText().toString().trim());
        mainObj.addProperty("city", edtCity.getText().toString().trim());
        mainObj.addProperty("business_code", edtBusinessCode.getText().toString().trim());
        mainObj.addProperty("business_name", edtBusinessName.getText().toString().trim());
        mainObj.addProperty("is_prime_vendor", isPrimeVendor);
        mainObj.addProperty("user_id", userId);

        if (Utilities.isNetworkAvailable(context))
            new AddVendor().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
        else
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
    }

    private class AddVendor extends AsyncTask<String, Void, String> {

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
            res = APICall.JSONAPICall(ApplicationConstants.VENDORAPI, params[0]);
            return res.trim();
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

                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Vendor details added successfully");
                        alertDialogBuilder.setCancelable(false);
                        final AlertDialog alertD = alertDialogBuilder.create();

                        btn_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertD.dismiss();
                                finish();
                            }
                        });
                        alertD.show();
                    } else {
                        Utilities.showMessage(message, context, 3);
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
