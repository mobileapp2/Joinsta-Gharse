package in.oriange.joinstagharse.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kofigyan.stateprogressbar.StateProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.adapters.AddBusinessViewPagerAdapter;
import in.oriange.joinstagharse.fragments.AddBusinessContactDetailsFragment;
import in.oriange.joinstagharse.fragments.AddBusinessGeneralDetailsFragment;
import in.oriange.joinstagharse.fragments.AddBusinessOtherDetailsFragment;
import in.oriange.joinstagharse.fragments.MyOrdersFragment;
import in.oriange.joinstagharse.fragments.ReceivedOrdersFragment;
import in.oriange.joinstagharse.fragments.SentOrdersFragment;
import in.oriange.joinstagharse.utilities.APICall;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.NonSwipeableViewPager;
import in.oriange.joinstagharse.utilities.UserSessionManager;
import in.oriange.joinstagharse.utilities.Utilities;

import static in.oriange.joinstagharse.utilities.Utilities.changeStatusBar;

public class AddBusinessActivity_v2 extends AppCompatActivity {

    @BindView(R.id.btn_save)
    MaterialButton btnSave;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    AppCompatEditText toolbarTitle;
    @BindView(R.id.status_progress_bar)
    StateProgressBar statusProgressBar;
    @BindView(R.id.view_pager)
    NonSwipeableViewPager viewPager;

    private Context context;
    private ProgressDialog pd;

    private String userId;
    private int currentPosition = 0;

    private LocalBroadcastManager localBroadcastManager1;
    private LocalBroadcastManager localBroadcastManager2;
    private LocalBroadcastManager localBroadcastManager3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_business_v2);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventListner();
        setUpToolbar();

    }

    private void init() {
        context = AddBusinessActivity_v2.this;
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        changeStatusBar(context, getWindow());

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new AddBusinessGeneralDetailsFragment(), "");
        adapter.addFrag(new AddBusinessContactDetailsFragment(), "");
        adapter.addFrag(new AddBusinessOtherDetailsFragment(), "");

        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);
    }

    private void getSessionDetails() {
        try {
            JSONArray user_info = new JSONArray(new UserSessionManager(context).getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            userId = json.getString("userid");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDefault() {

        String[] descriptionData = {"General", "Contact", "Other"};
        statusProgressBar.setStateDescriptionData(descriptionData);
        updatePosition();

        localBroadcastManager1 = LocalBroadcastManager.getInstance(context);
        localBroadcastManager2 = LocalBroadcastManager.getInstance(context);
        localBroadcastManager3 = LocalBroadcastManager.getInstance(context);

        IntentFilter intentFilter1 = new IntentFilter("AddBusinessActivityBusiness");
        IntentFilter intentFilter2 = new IntentFilter("AddBusinessActivityContact");
        IntentFilter intentFilter3 = new IntentFilter("AddBusinessActivityOther");

        localBroadcastManager1.registerReceiver(broadcastReceiver1, intentFilter1);
        localBroadcastManager2.registerReceiver(broadcastReceiver2, intentFilter2);
        localBroadcastManager3.registerReceiver(broadcastReceiver3, intentFilter3);
    }

    private void setEventListner() {
        btnSave.setOnClickListener(v -> {
            if (currentPosition < 2) {
                if (currentPosition == 0) {
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("AddBusinessGeneralDetailsFragment"));
                } else if (currentPosition == 1) {
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("AddBusinessContactDetailsFragment"));
                }
            } else if (currentPosition == 2) {
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("AddBusinessOtherDetailsFragment"));
            }
        });
    }

    private void updatePosition() {
        viewPager.setCurrentItem(currentPosition, true);

        switch (currentPosition) {
            case 0:
                btnSave.setText("Next");
                statusProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
                break;
            case 1:
                btnSave.setText("Next");
                statusProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                break;
            case 2:
                btnSave.setText("Save");
                statusProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                break;
        }
    }

    public void removeLandlineLayout(View view) {
        AddBusinessContactDetailsFragment.removeLandlineLayout(view);
    }

    public void removeMobileLayout(View view) {
        AddBusinessContactDetailsFragment.removeMobileLayout(view);
    }

    public void selectContryCode(View view) {
        AddBusinessContactDetailsFragment.selectContryCode(view);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.icon_backarrow_black);
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    public void onBackPressed() {
        if (currentPosition == 0) {
            finish();
        } else {
            currentPosition = currentPosition - 1;
            updatePosition();
        }
    }

//////////////////////////////////// Code to get values for add business api////////////////////////////////////////////

    private String imageName, businessName, categoryId, designation, email, website, address, pincode,
            city, district, state, country, latitude, longitude;

    private String subCategoryJsonArray, tagsJsonArray, mobilesJsonArray, landlineJsonArray;

    private BroadcastReceiver broadcastReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            currentPosition = currentPosition + 1;
            updatePosition();

            imageName = intent.getStringExtra("imageName");
            businessName = intent.getStringExtra("businessName");
            categoryId = intent.getStringExtra("categoryId");
            designation = intent.getStringExtra("designation");
            subCategoryJsonArray = intent.getStringExtra("subCategoryJsonArray");
            tagsJsonArray = intent.getStringExtra("tagsJsonArray");
        }
    };

    private BroadcastReceiver broadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            currentPosition = currentPosition + 1;
            updatePosition();

            mobilesJsonArray = intent.getStringExtra("mobilesJsonArray");
            landlineJsonArray = intent.getStringExtra("landlineJsonArray");
            email = intent.getStringExtra("email");
            website = intent.getStringExtra("website");
            address = intent.getStringExtra("address");
            pincode = intent.getStringExtra("pincode");
            city = intent.getStringExtra("city");
            district = intent.getStringExtra("district");
            state = intent.getStringExtra("state");
            country = intent.getStringExtra("country");
            latitude = intent.getStringExtra("latitude");
            longitude = intent.getStringExtra("longitude");
        }
    };

    private BroadcastReceiver broadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String isVisible = intent.getStringExtra("isVisible");
            String isEnquiryAvailable = intent.getStringExtra("isEnquiryAvailable");
            String isPickUpAvailable = intent.getStringExtra("isPickUpAvailable");
            String isHomeDeliveryAvailable = intent.getStringExtra("isHomeDeliveryAvailable");
            String documentJsonArray = intent.getStringExtra("documentJsonArray");

            JsonObject mainObj = new JsonObject();

            mainObj.addProperty("type", "createbusiness");
            mainObj.addProperty("address", address);
            mainObj.addProperty("business_name", businessName);
            mainObj.addProperty("district", district);
            mainObj.addProperty("state", state);
            mainObj.addProperty("city", city);
            mainObj.addProperty("country", country);
            mainObj.addProperty("pincode", pincode);
            mainObj.addProperty("longitude", longitude);
            mainObj.addProperty("latitude", latitude);
            mainObj.addProperty("landmark", "");
            mainObj.addProperty("locality", "");
            mainObj.addProperty("email", email);
            mainObj.addProperty("designation", designation);
            mainObj.addProperty("record_statusid", "1");
            mainObj.addProperty("website", website);
            mainObj.addProperty("order_online", "");
            mainObj.addProperty("image_url", imageName);
            mainObj.addProperty("type_id", categoryId);
            mainObj.addProperty("user_id", userId);
            mainObj.addProperty("organization_name", "");
            mainObj.addProperty("other_details", "");
            mainObj.addProperty("tax_name", "");
            mainObj.addProperty("tax_alias", "");
            mainObj.addProperty("pan_number", "");
            mainObj.addProperty("gst_number", "");
            mainObj.addProperty("tax_status", "online");
            mainObj.addProperty("account_holder_name", "");
            mainObj.addProperty("alias", "");
            mainObj.addProperty("bank_name", "");
            mainObj.addProperty("ifsc_code", "");
            mainObj.addProperty("account_no", "");
            mainObj.addProperty("status", "online");
            mainObj.addProperty("is_visible", isVisible);
            mainObj.addProperty("is_enquiry_available", isEnquiryAvailable);
            mainObj.addProperty("is_pick_up_available", isPickUpAvailable);
            mainObj.addProperty("is_home_delivery_available", isHomeDeliveryAvailable);
            mainObj.add("sub_categories", new JsonParser().parse(subCategoryJsonArray).getAsJsonArray());
            mainObj.add("mobile_number", new JsonParser().parse(mobilesJsonArray).getAsJsonArray());
            mainObj.add("landline_number", new JsonParser().parse(landlineJsonArray).getAsJsonArray());
            mainObj.add("tag_name", new JsonParser().parse(tagsJsonArray).getAsJsonArray());
            mainObj.add("business_docs", new JsonParser().parse(documentJsonArray).getAsJsonArray());

            Log.i("ADDBUSINESS", mainObj.toString());

            if (Utilities.isNetworkAvailable(context)) {
                new AddBusiness().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }


        }
    };

    private class AddBusiness extends AsyncTask<String, Void, String> {

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
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("BookOrderSelectDeliveryTypeActivityBusinessRefresh"));

                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Business details submitted successfully");
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
                        Utilities.showMessage("Failed to submit the details", context, 3);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager1.unregisterReceiver(broadcastReceiver1);
        localBroadcastManager2.unregisterReceiver(broadcastReceiver2);
        localBroadcastManager3.unregisterReceiver(broadcastReceiver3);
    }
}