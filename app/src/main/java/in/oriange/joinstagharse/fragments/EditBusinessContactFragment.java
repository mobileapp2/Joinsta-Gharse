package in.oriange.joinstagharse.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.models.GetBusinessModel;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.CountryCodeSelection;
import in.oriange.joinstagharse.utilities.UserSessionManager;
import in.oriange.joinstagharse.utilities.Utilities;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class EditBusinessContactFragment extends Fragment {

    @BindView(R.id.btn_add_mobile)
    Button btnAddMobile;
    @BindView(R.id.btn_add_landline)
    Button btnAddLandline;
    @BindView(R.id.edt_email)
    EditText edtEmail;
    @BindView(R.id.edt_website)
    EditText edtWebsite;
    @BindView(R.id.sv_scroll)
    ScrollView svScroll;

    private static Context context;
    private static LinearLayout llMobile, llLandline;

    private UserSessionManager session;
    private static ArrayList<LinearLayout> mobileLayoutsList, landlineLayoutsList;
    private String userId;

    private LocalBroadcastManager localBroadcastManager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_business_contact_details, container, false);
        ButterKnife.bind(this, rootView);

        context = getActivity();
        init(rootView);
        getSessionDetails();
        setDefault();
        setEventHandler();
        return rootView;
    }

    private void init(View rootView) {
        session = new UserSessionManager(context);

        llMobile = rootView.findViewById(R.id.ll_mobile);
        llLandline = rootView.findViewById(R.id.ll_landline);

        mobileLayoutsList = new ArrayList<>();
        landlineLayoutsList = new ArrayList<>();
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
        GetBusinessModel.ResultBean searchDetails = (GetBusinessModel.ResultBean) this.getArguments().getSerializable("searchDetails");

        edtEmail.setText(searchDetails.getEmail());
        edtWebsite.setText(searchDetails.getWebsite());

        List<GetBusinessModel.ResultBean.MobilesBean> mobilesList = searchDetails.getMobiles().get(0);

        if (mobilesList != null)
            if (mobilesList.size() > 0)
                for (int i = 0; i < mobilesList.size(); i++) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                    final View rowView = inflater.inflate(R.layout.layout_add_mobile, null);
                    LinearLayout ll = (LinearLayout) rowView;
                    mobileLayoutsList.add(ll);
                    llMobile.addView(rowView, llMobile.getChildCount() - 1);
                    ((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).setText(mobilesList.get(i).getMobile_number());
                    ((TextView) mobileLayoutsList.get(i).findViewById(R.id.tv_countrycode_mobile)).setText(mobilesList.get(i).getCountry_code());
                }
            else
                addMobileLayout();
        else
            addMobileLayout();

        List<GetBusinessModel.ResultBean.LandlineBean> landlineList = searchDetails.getLandline().get(0);

        if (landlineList != null)
            if (landlineList.size() > 0)
                for (int i = 0; i < landlineList.size(); i++) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                    final View rowView = inflater.inflate(R.layout.layout_add_landline, null);
                    LinearLayout ll = (LinearLayout) rowView;
                    landlineLayoutsList.add(ll);
                    llLandline.addView(rowView, llLandline.getChildCount() - 1);

                    ((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).setText(landlineList.get(i).getLandline_number());
                    ((TextView) landlineLayoutsList.get(i).findViewById(R.id.tv_countrycode_landline)).setText(landlineList.get(i).getCountry_code());
                }
            else
                addLandlineLayout();
        else
            addLandlineLayout();


        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("EditBusinessContactFragment");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void setEventHandler() {
        btnAddMobile.setOnClickListener(v -> addMobileLayout());

        btnAddLandline.setOnClickListener(v -> addLandlineLayout());
    }

    private void addMobileLayout() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.layout_add_mobile, null);
        LinearLayout ll = (LinearLayout) rowView;
        mobileLayoutsList.add(ll);
        llMobile.addView(rowView, llMobile.getChildCount() - 1);
    }

    private void addLandlineLayout() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.layout_add_landline, null);
        LinearLayout ll = (LinearLayout) rowView;
        landlineLayoutsList.add(ll);
        llLandline.addView(rowView, llLandline.getChildCount() - 1);
    }

    public static void removeLandlineLayout(View view) {
        llLandline.removeView((View) view.getParent());
        landlineLayoutsList.remove(view.getParent());
    }

    public static void removeMobileLayout(View v) {
        llMobile.removeView((View) v.getParent());
        mobileLayoutsList.remove(v.getParent());
    }

    public static void selectContryCode(View v) {
        TextView tv_country_code = (TextView) v;
        new CountryCodeSelection(context, tv_country_code);
    }

    private void submitData() {
        JsonArray mobileJSONArray = new JsonArray();
        JsonArray landlineJSONArray = new JsonArray();

        for (int i = 0; i < mobileLayoutsList.size(); i++) {
            if (!((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).getText().toString().trim().isEmpty()) {
                if (!Utilities.isValidMobileno(((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).getText().toString().trim())) {
                    ((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).setError("Please enter mobile number");
                    (mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).requestFocus();
                    return;
                }
            }
        }

        for (int i = 0; i < landlineLayoutsList.size(); i++) {
            if (!((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).getText().toString().trim().isEmpty()) {
                if (!Utilities.isLandlineValid(((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).getText().toString().trim())) {
                    ((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).setError("Please enter valid landline number");
                    (landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).requestFocus();
                    return;
                }
            }
        }

        if (!edtEmail.getText().toString().trim().isEmpty()) {
            if (!Utilities.isEmailValid(edtEmail.getText().toString().trim())) {
                edtEmail.setError("Please enter valid email");
                edtEmail.requestFocus();
                edtEmail.getParent().requestChildFocus(edtEmail, edtEmail);
                return;
            }
        }

        if (!edtWebsite.getText().toString().trim().isEmpty()) {
            if (!Utilities.isWebsiteValid(edtWebsite.getText().toString().trim())) {
                edtWebsite.setError("Please enter valid website");
                edtWebsite.requestFocus();
                return;
            }
        }

        for (int i = 0; i < mobileLayoutsList.size(); i++) {
            if (!((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).getText().toString().trim().equals("")) {
                JsonObject mobileJSONObj = new JsonObject();
                mobileJSONObj.addProperty("mobile", ((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).getText().toString().trim());
                mobileJSONObj.addProperty("country_code", ((TextView) mobileLayoutsList.get(i).findViewById(R.id.tv_countrycode_mobile)).getText().toString());
                mobileJSONArray.add(mobileJSONObj);
            }
        }

        for (int i = 0; i < landlineLayoutsList.size(); i++) {
            if (!((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).getText().toString().trim().equals("")) {
                JsonObject landlineJSONObj = new JsonObject();
                landlineJSONObj.addProperty("landlinenumbers", ((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).getText().toString().trim());
                landlineJSONObj.addProperty("country_code", ((TextView) landlineLayoutsList.get(i).findViewById(R.id.tv_countrycode_landline)).getText().toString());
                landlineJSONArray.add(landlineJSONObj);
            }
        }

        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("EditBusinessContactActivity")
                .putExtra("mobilesJsonArray", mobileJSONArray.toString())
                .putExtra("landlineJsonArray", landlineJSONArray.toString())
                .putExtra("email", edtEmail.getText().toString().trim())
                .putExtra("website", edtWebsite.getText().toString().trim()));
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("EditBusinessDocumentFragment"));
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            submitData();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }


}
