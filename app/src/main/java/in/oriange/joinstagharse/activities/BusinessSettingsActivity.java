package in.oriange.joinstagharse.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.github.angads25.toggle.widget.LabeledSwitch;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.regex.Matcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.models.GetBusinessModel;
import in.oriange.joinstagharse.utilities.APICall;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.UserSessionManager;
import in.oriange.joinstagharse.utilities.Utilities;

import static in.oriange.joinstagharse.utilities.Utilities.changeStatusBar;

public class BusinessSettingsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.sw_allow_book_order)
    LabeledSwitch swAllowBookOrder;
    @BindView(R.id.ib_allow_book_order)
    ImageButton ibAllowBookOrder;
    @BindView(R.id.ib_can_product_share)
    ImageButton ibCanProductShare;
    @BindView(R.id.sw_can_product_share)
    LabeledSwitch swCanProductShare;
    @BindView(R.id.edt_store_pickup_instructions)
    EditText edtStorePickupInstructions;
    @BindView(R.id.ll_store_pickup_instructions)
    LinearLayout llStorePickupInstructions;
    @BindView(R.id.edt_home_delivery_instructions)
    EditText edtHomeDeliveryInstructions;
    @BindView(R.id.ll_home_delivery_instructions)
    LinearLayout llHomeDeliveryInstructions;
    @BindView(R.id.btn_save_instructions)
    MaterialButton btnSaveInstructions;

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private String userId;
    private GetBusinessModel.ResultBean searchDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_settings);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = BusinessSettingsActivity.this;
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
        searchDetails = (GetBusinessModel.ResultBean) getIntent().getSerializableExtra("searchDetails");

        if (searchDetails.getAllow_all_to_book_order().equals("1")) {
            swAllowBookOrder.setColorBorder(getResources().getColor(R.color.green));
            swAllowBookOrder.setColorOn(getResources().getColor(R.color.green));
            swAllowBookOrder.setOn(true);
        }

        if (searchDetails.getCan_product_share().equals("1")) {
            swCanProductShare.setColorBorder(getResources().getColor(R.color.green));
            swCanProductShare.setColorOn(getResources().getColor(R.color.green));
            swCanProductShare.setOn(true);
        }

        edtHomeDeliveryInstructions.setText(searchDetails.getHome_delivery_instructions());
        edtStorePickupInstructions.setText(searchDetails.getStore_pickup_instructions());
    }

    private void setEventHandler() {
        swAllowBookOrder.setOnToggledListener((toggleableView, isOn) -> {
            String allowBookOrder;

            if (isOn) {
                allowBookOrder = "1";
                swAllowBookOrder.setColorBorder(getResources().getColor(R.color.green));
                swAllowBookOrder.setColorOn(getResources().getColor(R.color.green));
            } else {
                allowBookOrder = "0";
                swAllowBookOrder.setColorBorder(getResources().getColor(R.color.colorPrimary));
                swAllowBookOrder.setColorOn(getResources().getColor(R.color.colorPrimary));
            }

            if (Utilities.isNetworkAvailable(context)) {
                new ChangeAllowBookOrder().execute(searchDetails.getId(), allowBookOrder);
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }
        });

        swCanProductShare.setOnToggledListener((toggleableView, isOn) -> {
            String canProductShare;

            if (isOn) {
                canProductShare = "1";
                swCanProductShare.setColorBorder(getResources().getColor(R.color.green));
                swCanProductShare.setColorOn(getResources().getColor(R.color.green));
            } else {
                canProductShare = "0";
                swCanProductShare.setColorBorder(getResources().getColor(R.color.colorPrimary));
                swCanProductShare.setColorOn(getResources().getColor(R.color.colorPrimary));
            }

            if (Utilities.isNetworkAvailable(context)) {
                new ChangeCanProductShare().execute(searchDetails.getId(), canProductShare);
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }
        });

        btnSaveInstructions.setOnClickListener(v -> {

            JsonObject mainObj = new JsonObject();
            mainObj.addProperty("type", "storehomeInstructions");
            mainObj.addProperty("business_id", searchDetails.getId());
            mainObj.addProperty("store_pickup_instructions", edtStorePickupInstructions.getText().toString().trim());
            mainObj.addProperty("home_delivery_instructions", edtHomeDeliveryInstructions.getText().toString().trim());

            if (Utilities.isNetworkAvailable(context))
                new StorePickUpHomeDeliveryInstructions().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
            else
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);

        });

        ibAllowBookOrder.setOnClickListener(v -> Utilities.showAlertDialogNormal(context, "By making this feature ON, you allow all Gharse Users to place order online. Book Order in Search Section for your business will be enabled. Once you enable this option, please add products for your business, if not added already."));

        ibCanProductShare.setOnClickListener(v -> Utilities.showAlertDialogNormal(context, ""));
    }

    private class ChangeAllowBookOrder extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "updateAllBookOrder");
            obj.addProperty("business_id", params[0]);
            obj.addProperty("allow_all_to_book_order", params[1]);
            res = APICall.JSONAPICall(ApplicationConstants.BUSINESSAPI, obj.toString());
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
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("MyBusinessActivity"));
                        Utilities.showMessage(message, context, 1);
                    } else {
                        Utilities.showMessage(message, context, 3);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class ChangeCanProductShare extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "canProductShare");
            obj.addProperty("business_id", params[0]);
            obj.addProperty("can_product_share", params[1]);
            res = APICall.JSONAPICall(ApplicationConstants.BUSINESSAPI, obj.toString());
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
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("MyBusinessActivity"));
                        Utilities.showMessage(message, context, 1);
                    } else {
                        Utilities.showMessage(message, context, 3);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class StorePickUpHomeDeliveryInstructions extends AsyncTask<String, Void, String> {

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
            res = APICall.JSONAPICall(ApplicationConstants.BUSINESSAPI, params[0]);
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
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("MyBusinessActivity"));
                        Utilities.showMessage(message, context, 1);
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