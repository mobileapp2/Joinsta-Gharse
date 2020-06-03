package in.oriange.eorder.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import in.oriange.eorder.R;
import in.oriange.eorder.models.MapAddressListModel;
import in.oriange.eorder.utilities.APICall;
import in.oriange.eorder.utilities.ApplicationConstants;
import in.oriange.eorder.utilities.UserSessionManager;
import in.oriange.eorder.utilities.Utilities;

import static in.oriange.eorder.utilities.Utilities.changeStatusBar;

public class AddAddressActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btn_select)
    MaterialButton btnSelect;
    @BindView(R.id.edt_area)
    EditText edtArea;
    @BindView(R.id.edt_pincode)
    EditText edtPincode;
    @BindView(R.id.edt_city)
    EditText edtCity;
    @BindView(R.id.edt_district)
    EditText edtDistrict;
    @BindView(R.id.edt_state)
    EditText edtState;
    @BindView(R.id.edt_country)
    EditText edtCountry;
    @BindView(R.id.edt_address)
    EditText edtAddress;
    @BindView(R.id.btn_save)
    MaterialButton btnSave;
    @BindView(R.id.edt_name)
    EditText edtName;

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private String userId, latitude, longitude;
    private final int LOCATION_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventListner();
        setUpToolbar();
    }

    private void init() {
        context = AddAddressActivity.this;
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

    }

    private void setEventListner() {
        btnSelect.setOnClickListener(v -> startActivityForResult(new Intent(context, PickMapLocationActivity.class), LOCATION_REQUEST));

        btnSave.setOnClickListener(v -> submitData());
    }

    private void submitData() {
        if (edtName.getText().toString().trim().isEmpty()) {
            edtName.setError("Please enter user name");
            edtName.requestFocus();
            edtName.getParent().requestChildFocus(edtName, edtName);
            return;
        }

        if (edtAddress.getText().toString().trim().isEmpty()) {
            edtAddress.setError("Please enter address");
            edtAddress.requestFocus();
            edtAddress.getParent().requestChildFocus(edtAddress, edtAddress);
            return;
        }

        if (edtCity.getText().toString().trim().isEmpty()) {
            edtCity.setError("Please enter city");
            edtCity.requestFocus();
            edtCity.getParent().requestChildFocus(edtCity, edtCity);
            return;
        }

        if (!edtPincode.getText().toString().trim().isEmpty()) {
            if (edtPincode.getText().toString().trim().length() != 6) {
                edtPincode.setError("Please enter pincode");
                edtPincode.requestFocus();
                edtPincode.getParent().requestChildFocus(edtPincode, edtPincode);
                return;
            }
        }

        JsonObject mainObj = new JsonObject();
        mainObj.addProperty("type", "addUserAddress");
        mainObj.addProperty("full_name", edtName.getText().toString().trim());
        mainObj.addProperty("area", edtArea.getText().toString().trim());
        mainObj.addProperty("address_line_one", edtAddress.getText().toString().trim());
        mainObj.addProperty("address_line_two", "");
        mainObj.addProperty("district", edtDistrict.getText().toString().trim());
        mainObj.addProperty("state", edtState.getText().toString().trim());
        mainObj.addProperty("city", edtCity.getText().toString().trim());
        mainObj.addProperty("country", edtCountry.getText().toString().trim());
        mainObj.addProperty("pincode", edtPincode.getText().toString().trim());
        mainObj.addProperty("latitude", latitude);
        mainObj.addProperty("langtitude", longitude);
        mainObj.addProperty("user_id", userId);

        if (Utilities.isNetworkAvailable(context))
            new AddAddress().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
        else
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);

    }

    private class AddAddress extends AsyncTask<String, Void, String> {

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
            res = APICall.JSONAPICall(ApplicationConstants.ADDRESSAPI, params[0]);
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
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("MyAddressActivity"));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("BookOrderSelectDeliveryTypeActivityAddressRefresh"));

                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Address added successfully");
                        alertDialogBuilder.setCancelable(false);
                        final AlertDialog alertD = alertDialogBuilder.create();

                        btn_ok.setOnClickListener(v -> {
                            alertD.dismiss();
                            finish();
                        });
                        alertD.show();
                    } else {
                        Utilities.showMessage("Failed to submit the details", context, 3);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == LOCATION_REQUEST) {
                MapAddressListModel addressList = (MapAddressListModel) data.getSerializableExtra("addressList");
                if (addressList != null) {
                    latitude = addressList.getMap_location_lattitude();
                    longitude = addressList.getMap_location_logitude();
                    edtAddress.setText(addressList.getAddress_line_one());
                    edtCountry.setText(addressList.getCountry());
                    edtState.setText(addressList.getState());
                    edtDistrict.setText(addressList.getDistrict());
                    edtPincode.setText(addressList.getPincode());
                    edtArea.setText(addressList.getName());
                    edtCity.setText(addressList.getDistrict());
                } else {
                    Utilities.showMessage("Address not found, please try again", context, 3);
                }
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
