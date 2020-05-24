package in.oriange.eorder.activities;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.goodiebag.pinview.Pinview;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.oriange.eorder.R;
import in.oriange.eorder.models.ContryCodeModel;
import in.oriange.eorder.utilities.APICall;
import in.oriange.eorder.utilities.ApplicationConstants;
import in.oriange.eorder.utilities.ParamsPojo;
import in.oriange.eorder.utilities.UserSessionManager;
import in.oriange.eorder.utilities.Utilities;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static in.oriange.eorder.utilities.ApplicationConstants.NUMVERIFY_ACCESS_TOKEN;
import static in.oriange.eorder.utilities.Utilities.changeStatusBar;
import static in.oriange.eorder.utilities.Utilities.hideSoftKeyboard;
import static in.oriange.eorder.utilities.Utilities.loadJSONForCountryCode;

public class RegisterActivity extends AppCompatActivity {

    private Context context;
    private ProgressDialog pd;
    private TextView tv_already_registered;
    private TextInputEditText edt_name, edt_mobile, edt_password, edt_referral_code;
    private CheckBox cb_iagree;
    private TextView tv_countrycode_mobile, tv_tandc;
    private Button btn_register;
    private UserSessionManager session;
    private ArrayList<ContryCodeModel> countryCodeList;
    private AlertDialog countryCodeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();
        setDefault();
        setEventHandler();
    }

    private void init() {
        context = RegisterActivity.this;
        changeStatusBar(context, getWindow());
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        tv_already_registered = findViewById(R.id.tv_already_registered);
        edt_name = findViewById(R.id.edt_name);
        edt_mobile = findViewById(R.id.edt_mobile);
        edt_password = findViewById(R.id.edt_password);
        edt_referral_code = findViewById(R.id.edt_referral_code);
        tv_countrycode_mobile = findViewById(R.id.tv_countrycode_mobile);
        cb_iagree = findViewById(R.id.cb_iagree);
        tv_tandc = findViewById(R.id.tv_tandc);
        btn_register = findViewById(R.id.btn_register);
    }

    private void setDefault() {

        try {
            JSONArray m_jArry = new JSONArray(loadJSONForCountryCode(context));
            countryCodeList = new ArrayList<>();

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                countryCodeList.add(new ContryCodeModel(
                        jo_inside.getString("name"),
                        jo_inside.getString("dial_code"),
                        jo_inside.getString("code")
                ));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        tv_tandc.setText(Html.fromHtml("I agree to your" + "<font color=\"#01579B\"> <b> Terms and Conditions </b> </font>"));

    }

    private void setEventHandler() {
//        edt_mobile.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (edt_mobile.getText().toString().trim().equals("")) {
//                    return;
//                }
//
//                if (edt_mobile.getText().toString().trim().length() == 10) {
//                    if (!Utilities.isValidMobileno(edt_mobile.getText().toString().trim())) {
//                        edt_mobile.setError("Please enter valid mobile number");
//                        edt_mobile.requestFocus();
//                        return;
//                    }
//
//                    if (Utilities.isNetworkAvailable(context)) {
//                        new VerifyMobile().execute(edt_mobile.getText().toString().trim());
//                    } else {
//                        Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
//                    }
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });

        tv_already_registered.setOnClickListener(v -> finish());

        tv_countrycode_mobile.setOnClickListener(v -> showCountryCodesListDialog());

        btn_register.setOnClickListener(v -> {
            if (edt_name.getText().toString().trim().isEmpty()) {
                edt_name.setError("Please enter name");
                return;
            }

            if (tv_countrycode_mobile.getText().toString().trim().equals("+91")) {
                if (!Utilities.isValidMobileno(edt_mobile.getText().toString().trim())) {
                    edt_mobile.setError("Please enter valid mobile number");
                    edt_mobile.requestFocus();
                    return;
                }

                if (edt_password.getText().toString().trim().isEmpty()) {
                    edt_password.setError("Please enter password");
                    return;
                }

                if (!cb_iagree.isChecked()) {
                    Utilities.showMessage("Please check that you agree to our Terms and Conditions", context, 2);
                    return;
                }

                if (Utilities.isNetworkAvailable(context)) {
                    new VerifyMobile().execute(edt_mobile.getText().toString().trim());
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }

            } else {
                if (Utilities.isNetworkAvailable(context)) {
                    String number = tv_countrycode_mobile.getText().toString().trim().replace("+", "") +
                            edt_mobile.getText().toString().trim();
                    new NumVerifyApi().execute(number);
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }
            }
        });

        tv_tandc.setOnClickListener(v -> startActivity(new Intent(context, PolicyDetailsActivity.class)
                .putExtra("title", "Terms and Conditions")
                .putExtra("filePath", "termsandconditions.html")));
    }

    private void createDialogForOTP(final String otp) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_layout_otp, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialogBuilder.setView(promptView);

        Pinview pinview_opt = promptView.findViewById(R.id.pinview_opt);
        Button btn_cancel = promptView.findViewById(R.id.btn_cancel);
        pinview_opt.setPinLength(otp.length());

        alertDialogBuilder.setCancelable(false);
        final AlertDialog alertD = alertDialogBuilder.create();

        pinview_opt.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {

                if (pinview.getValue().length() == otp.length()) {
                    if (pinview.getValue().equals(otp)) {
                        if (Utilities.isNetworkAvailable(context)) {
                            alertD.dismiss();
                            new RegisterUser().execute(
                                    edt_name.getText().toString().trim(),
                                    edt_mobile.getText().toString().trim(),
                                    tv_countrycode_mobile.getText().toString().trim().replace("+", ""),
                                    edt_password.getText().toString().trim(),
                                    edt_referral_code.getText().toString().trim());
                        } else {
                            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                        }
                    } else {
                        Utilities.showMessage("OTP did not match", context, 3);
                    }
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertD.dismiss();
            }
        });

        alertD.show();
    }

    private void saveRegistrationID() {
        String user_id = "", regToken = "";
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));

            for (int j = 0; j < user_info.length(); j++) {
                JSONObject json = user_info.getJSONObject(j);
                user_id = json.getString("userid");
            }

            regToken = session.getAndroidToken().get(ApplicationConstants.KEY_ANDROIDTOKETID);

            if (regToken != null && !regToken.isEmpty() && !regToken.equals("null"))
                new SendRegistrationToken().execute(user_id, regToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showCountryCodesListDialog() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_countrycodes_list, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builder.setView(view);
        builder.setTitle("Select Country");
        builder.setCancelable(false);

        final RecyclerView rv_country = view.findViewById(R.id.rv_country);
        EditText edt_search = view.findViewById(R.id.edt_search);
        rv_country.setLayoutManager(new LinearLayoutManager(context));
        rv_country.setAdapter(new CountryCodeAdapter(countryCodeList));

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {

                if (query.toString().isEmpty()) {
                    rv_country.setAdapter(new CountryCodeAdapter(countryCodeList));
                    return;
                }

                if (countryCodeList.size() == 0) {
                    rv_country.setVisibility(View.GONE);
                    return;
                }

                if (!query.toString().equals("")) {
                    ArrayList<ContryCodeModel> searchedCountryList = new ArrayList<>();
                    for (ContryCodeModel countryDetails : countryCodeList) {

                        String countryToBeSearched = countryDetails.getName().toLowerCase();

                        if (countryToBeSearched.contains(query.toString().toLowerCase())) {
                            searchedCountryList.add(countryDetails);
                        }
                    }
                    rv_country.setAdapter(new CountryCodeAdapter(searchedCountryList));
                } else {
                    rv_country.setAdapter(new CountryCodeAdapter(countryCodeList));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        countryCodeDialog = builder.create();
        countryCodeDialog.show();
    }

    private class CountryCodeAdapter extends RecyclerView.Adapter<CountryCodeAdapter.MyViewHolder> {

        private ArrayList<ContryCodeModel> countryCodeList;

        public CountryCodeAdapter(ArrayList<ContryCodeModel> countryCodeList) {
            this.countryCodeList = countryCodeList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_1, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, final int pos) {
            final int position = holder.getAdapterPosition();

            holder.tv_name.setText(countryCodeList.get(position).getName());

            holder.tv_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_countrycode_mobile.setText(countryCodeList.get(position).getDial_code());
                    countryCodeDialog.dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return countryCodeList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView tv_name;

            public MyViewHolder(@NonNull View view) {
                super(view);
                tv_name = view.findViewById(R.id.tv_name);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }
    }

    private class NumVerifyApi extends AsyncTask<String, Void, String> {

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
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("access_key", NUMVERIFY_ACCESS_TOKEN));
            param.add(new ParamsPojo("number", params[0]));
            param.add(new ParamsPojo("format", "1"));
            res = APICall.FORMDATAAPICall(ApplicationConstants.NUMVERIFYAPI, param);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject jsonObject = new JSONObject(result);
                    boolean isMobileNumValid = jsonObject.getBoolean("valid");

                    if (isMobileNumValid) {
                        if (Utilities.isNetworkAvailable(context)) {
                            new VerifyMobile().execute(edt_mobile.getText().toString().trim());
                        } else {
                            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                        }
                    } else {
                        edt_mobile.setError("Please enter valid mobile number");
                        edt_mobile.requestFocus();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (!Utilities.isValidMobileno(edt_mobile.getText().toString().trim())) {
                    edt_mobile.setError("Please enter valid mobile number");
                    edt_mobile.requestFocus();
                    return;
                }

                if (Utilities.isNetworkAvailable(context)) {
                    new VerifyMobile().execute(edt_mobile.getText().toString().trim());
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }

            }
        }
    }

    private class VerifyMobile extends AsyncTask<String, Void, String> {

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
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("mobile", params[0]));
            res = APICall.FORMDATAAPICall(ApplicationConstants.LOGINAPI, param);
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
                        JSONObject resultObj = mainObj.getJSONObject("result");

                        String is_registered = resultObj.getString("is_registered");

                        if (is_registered.equals("1")) {
                            Utilities.showAlertDialog(context, "Mobile number is already registered", false);
                            edt_mobile.setText("");
                        } else {
                            if (edt_referral_code.getText().toString().trim().isEmpty()) {
                                if (Utilities.isNetworkAvailable(context)) {
                                    new SendOTP().execute(edt_mobile.getText().toString().trim(), tv_countrycode_mobile.getText().toString().trim().replace("+", ""));
                                } else {
                                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                                }
                            } else {
                                if (Utilities.isNetworkAvailable(context)) {
                                    new VerifyReferralCode().execute(edt_referral_code.getText().toString().trim());
                                } else {
                                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                                }
                            }

                        }
                    } else if (type.equalsIgnoreCase("failure")) {
                        Utilities.showAlertDialog(context, "Failed to verify. Please try again", false);
                        edt_mobile.setText("");
                    }
                }
            } catch (Exception e) {
                Utilities.showAlertDialog(context, "Server Not Responding", false);
                edt_mobile.setText("");
                e.printStackTrace();
            }
        }
    }

    private class VerifyReferralCode extends AsyncTask<String, Void, String> {

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
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("referral_code", params[0]));
            res = APICall.FORMDATAAPICall(ApplicationConstants.REFERRALCODEAPI, param);
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
                        if (Utilities.isNetworkAvailable(context)) {
                            new SendOTP().execute(edt_mobile.getText().toString().trim(), tv_countrycode_mobile.getText().toString().trim().replace("+", ""));
                        } else {
                            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                        }
                    } else if (type.equalsIgnoreCase("failure")) {
                        Utilities.showAlertDialog(context, message, false);
                        edt_referral_code.setText("");
                    }
                }
            } catch (Exception e) {
                Utilities.showAlertDialog(context, "Server Not Responding", false);
                edt_referral_code.setText("");
                e.printStackTrace();
            }
        }
    }

    private class SendOTP extends AsyncTask<String, Void, String> {

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
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "send"));
            param.add(new ParamsPojo("mobile", params[0]));
            param.add(new ParamsPojo("country_code", params[1]));
            res = APICall.FORMDATAAPICall(ApplicationConstants.OTPAPI, param);
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
                    if (type.equalsIgnoreCase("success")) {
                        JSONObject otpObj = mainObj.getJSONObject("OTP");
                        String OTP = otpObj.getString("otp");
                        createDialogForOTP(OTP);
                    } else if (type.equalsIgnoreCase("failure")) {
                        Utilities.showAlertDialog(context, "Failed to send otp. Please try again", false);
                    }

                }
            } catch (Exception e) {
                Utilities.showAlertDialog(context, "Server Not Responding", false);
                e.printStackTrace();
            }
        }
    }

    private class RegisterUser extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "registeruser");
            obj.addProperty("name", params[0]);
            obj.addProperty("mobile", params[1]);
            obj.addProperty("country_code", params[2]);
            obj.addProperty("password", params[3]);
            obj.addProperty("referal_code", params[4]);
            res = APICall.JSONAPICall(ApplicationConstants.USERSAPI, obj.toString());
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
                        new LoginUser().execute(edt_mobile.getText().toString().trim(), edt_password.getText().toString().trim());
                    } else if (type.equalsIgnoreCase("failure")) {
                        Utilities.showAlertDialog(context, message, false);
                    }
                }
            } catch (Exception e) {
                Utilities.showAlertDialog(context, "Server Not Responding", false);
                e.printStackTrace();
            }
        }
    }

    private class LoginUser extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";

            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("logintype", "loginwithpassword")
                    .add("mobile", params[0])
                    .add("password", params[1])
                    .add("is_registered", "1")
                    .build();
            Request request = new Request.Builder()
                    .url(ApplicationConstants.LOGINAPI)
                    .post(formBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                res = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            session.createUserLoginSession(jsonarr.toString());
//                            saveRegistrationID();
                            startActivity(new Intent(context, MainDrawerActivity.class)
                                    .putExtra("startOrigin", 0));
                        }
                    } else {
                        Utilities.showMessage("Username or password is invalid", context, 3);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class SendRegistrationToken extends AsyncTask<String, Integer, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            String s = "";
            JSONObject obj = new JSONObject();
            try {
                obj.put("type", "registerDevice");
                obj.put("device_type", "Android");
                obj.put("ram", totalRAMSize());
                obj.put("processor", Build.CPU_ABI);
                obj.put("device_os", Build.VERSION.RELEASE);
                obj.put("location", "0.0, 0.0");
                obj.put("device_model", Build.MODEL);
                obj.put("manufacturer", Build.MANUFACTURER);
                obj.put("customers_id", params[0]);
                obj.put("device_id", params[1]);
                s = obj.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            res = APICall.JSONAPICall(ApplicationConstants.DEVICEREGAPI, s);
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            if (result != null && result.length() > 0 && !result.equalsIgnoreCase("[]")) {
                try {
                    int c = 0;
                    JSONObject obj1 = new JSONObject(result);
                    String success = obj1.getString("success");
                    String message = obj1.getString("message");
                    if (success.equalsIgnoreCase("1")) {
//                        if (session.isLocationSet())
                        startActivity(new Intent(context, MainDrawerActivity.class)
                                .putExtra("startOrigin", 0));
//                        else
//                            startActivity(new Intent(context, SelectLocationActivity.class)
//                                    .putExtra("startOrigin", 0));
                        finish();
                    } else {
                        Utilities.showMessage("Please Try After Sometime", context, 3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String totalRAMSize() {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(memoryInfo);
        double totalRAM = memoryInfo.totalMem / 1048576.0;
        return String.valueOf(totalRAM);
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftKeyboard(RegisterActivity.this);
    }
}
