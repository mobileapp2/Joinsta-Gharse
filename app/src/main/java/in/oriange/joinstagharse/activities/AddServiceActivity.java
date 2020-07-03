package in.oriange.joinstagharse.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import in.oriange.joinstagharse.fragments.AddServiceAddressFragment;
import in.oriange.joinstagharse.fragments.AddServiceContactFragment;
import in.oriange.joinstagharse.fragments.AddServiceDocumentFragment;
import in.oriange.joinstagharse.fragments.AddServiceGeneralFragment;
import in.oriange.joinstagharse.fragments.AddServiceSettingsFragment;
import in.oriange.joinstagharse.utilities.APICall;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.NonSwipeableViewPager;
import in.oriange.joinstagharse.utilities.UserSessionManager;
import in.oriange.joinstagharse.utilities.Utilities;

import static in.oriange.joinstagharse.utilities.RuntimePermissions.CALL_PHONE_PERMISSION_REQUEST;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.CAMERA_AND_STORAGE_PERMISSION_REQUEST;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.LOCATION_PERMISSION_REQUEST;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.READ_CONTACTS_PERMISSION_REQUEST;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.STORAGE_PERMISSION_REQUEST;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.callPermissionMsg;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.cameraStoragePermissionMsg;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.locationPermissionMsg;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.manualPermission;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.readContactsPermissionMsg;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.storagePermissionMsg;
import static in.oriange.joinstagharse.utilities.Utilities.changeStatusBar;

public class AddServiceActivity extends AppCompatActivity {

    @BindView(R.id.btn_save)
    MaterialButton btnSave;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.status_progress_bar)
    StateProgressBar statusProgressBar;
    @BindView(R.id.view_pager)
    NonSwipeableViewPager viewPager;
    @BindView(R.id.btn_skip)
    MaterialButton btnSkip;

    private Context context;
    private ProgressDialog pd;

    private String userId;
    private int currentPosition = 0;

    private LocalBroadcastManager localBroadcastManager1, localBroadcastManager2, localBroadcastManager3,
            localBroadcastManager4, localBroadcastManager5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventListner();
        setUpToolbar();

    }

    private void init() {
        context = AddServiceActivity.this;
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        changeStatusBar(context, getWindow());

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new AddServiceGeneralFragment(), "");
        adapter.addFrag(new AddServiceAddressFragment(), "");
        adapter.addFrag(new AddServiceContactFragment(), "");
        adapter.addFrag(new AddServiceDocumentFragment(), "");
        adapter.addFrag(new AddServiceSettingsFragment(), "");

        viewPager.setOffscreenPageLimit(5);
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

        String[] descriptionData = {"General", "Address", "Contact", "Document", "Settings"};
        statusProgressBar.setStateDescriptionData(descriptionData);
        updatePosition();

        localBroadcastManager1 = LocalBroadcastManager.getInstance(context);
        localBroadcastManager2 = LocalBroadcastManager.getInstance(context);
        localBroadcastManager3 = LocalBroadcastManager.getInstance(context);
        localBroadcastManager4 = LocalBroadcastManager.getInstance(context);
        localBroadcastManager5 = LocalBroadcastManager.getInstance(context);

        IntentFilter intentFilter1 = new IntentFilter("AddServiceGeneralActivity");
        IntentFilter intentFilter2 = new IntentFilter("AddServiceAddressActivity");
        IntentFilter intentFilter3 = new IntentFilter("AddServiceContactActivity");
        IntentFilter intentFilter4 = new IntentFilter("AddServiceDocumentActivity");
        IntentFilter intentFilter5 = new IntentFilter("AddServiceSettingActivity");

        localBroadcastManager1.registerReceiver(broadcastReceiver1, intentFilter1);
        localBroadcastManager2.registerReceiver(broadcastReceiver2, intentFilter2);
        localBroadcastManager3.registerReceiver(broadcastReceiver3, intentFilter3);
        localBroadcastManager4.registerReceiver(broadcastReceiver4, intentFilter4);
        localBroadcastManager5.registerReceiver(broadcastReceiver5, intentFilter5);
    }

    private void setEventListner() {
        btnSave.setOnClickListener(v -> {
            if (currentPosition < 4) {
                if (currentPosition == 0) {
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("AddServiceGeneralFragment"));
                } else if (currentPosition == 1) {
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("AddServiceAddressFragment"));
                } else if (currentPosition == 2) {
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("AddServiceContactFragment"));
                } else if (currentPosition == 3) {
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("AddServiceDocumentFragment"));
                }
            } else if (currentPosition == 4) {
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("AddServiceSettingFragment"));
            }
        });

        btnSkip.setOnClickListener(v -> {
            if (currentPosition == 2) {
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("AddServiceContactFragmentSkip"));
            } else if (currentPosition == 3) {
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("AddServiceDocumentFragmentSkip"));
            }
        });
    }

    private void updatePosition() {
        viewPager.setCurrentItem(currentPosition, true);

        switch (currentPosition) {
            case 0:
                btnSkip.setVisibility(View.GONE);
                btnSave.setText("Next");
                statusProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
                break;
            case 1:
                btnSkip.setVisibility(View.GONE);
                btnSave.setText("Next");
                statusProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                break;
            case 2:
                btnSkip.setVisibility(View.VISIBLE);
                btnSave.setText("Next");
                statusProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                break;
            case 3:
                btnSkip.setVisibility(View.VISIBLE);
                btnSave.setText("Next");
                statusProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR);
                break;
            case 4:
                btnSkip.setVisibility(View.GONE);
                btnSave.setText("Save");
                statusProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FIVE);
                break;
        }
    }

    public void removeLandlineLayout(View view) {
        AddServiceContactFragment.removeLandlineLayout(view);
    }

    public void removeMobileLayout(View view) {
        AddServiceContactFragment.removeMobileLayout(view);
    }

    public void selectContryCode(View view) {
        AddServiceContactFragment.selectContryCode(view);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.icon_backarrow_black);
        toolbar.setNavigationOnClickListener(view -> exitScreenPopup());
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
            exitScreenPopup();
        } else {
            currentPosition = currentPosition - 1;
            updatePosition();
        }
    }

//////////////////////////////////// Code to get values for add service api////////////////////////////////////////////

    private String imageName, serviceName, categoryId, designation, email, website, address, pincode,
            city, district, state, country, latitude, longitude;

    private String subCategoryJsonArray, tagsJsonArray, mobilesJsonArray, landlineJsonArray, documentJsonArray;

    private BroadcastReceiver broadcastReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            currentPosition = currentPosition + 1;
            updatePosition();

            imageName = intent.getStringExtra("imageName");
            serviceName = intent.getStringExtra("serviceName");
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
            currentPosition = currentPosition + 1;
            updatePosition();

            mobilesJsonArray = intent.getStringExtra("mobilesJsonArray");
            landlineJsonArray = intent.getStringExtra("landlineJsonArray");
            email = intent.getStringExtra("email");
            website = intent.getStringExtra("website");
        }
    };

    private BroadcastReceiver broadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            currentPosition = currentPosition + 1;
            updatePosition();

            documentJsonArray = intent.getStringExtra("documentJsonArray");
        }
    };

    private BroadcastReceiver broadcastReceiver5 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String isVisible = intent.getStringExtra("isVisible");
            String isEnquiryAvailable = intent.getStringExtra("isEnquiryAvailable");
            String isPickUpAvailable = intent.getStringExtra("isPickUpAvailable");
            String isHomeDeliveryAvailable = intent.getStringExtra("isHomeDeliveryAvailable");
            String isOnlineServiceAvailable = intent.getStringExtra("isOnlineServiceAvailable");

            JsonObject mainObj = new JsonObject();

            mainObj.addProperty("type", "createServices");
            mainObj.addProperty("address", address);
            mainObj.addProperty("services_name", serviceName);
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
            mainObj.add("services_docs", new JsonParser().parse(documentJsonArray).getAsJsonArray());

            Log.i("ADDSERVICE", mainObj.toString());

            if (Utilities.isNetworkAvailable(context)) {
                new AddService().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }
        }
    };

    private class AddService extends AsyncTask<String, Void, String> {

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
            res = APICall.JSONAPICall(ApplicationConstants.SERVICESAPI, params[0]);
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
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("MyServicesActivity"));

                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Service details submitted successfully");
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
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager1.unregisterReceiver(broadcastReceiver1);
        localBroadcastManager2.unregisterReceiver(broadcastReceiver2);
        localBroadcastManager3.unregisterReceiver(broadcastReceiver3);
        localBroadcastManager4.unregisterReceiver(broadcastReceiver4);
        localBroadcastManager5.unregisterReceiver(broadcastReceiver5);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_AND_STORAGE_PERMISSION_REQUEST: {
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED)) {
                    manualPermission(context, cameraStoragePermissionMsg, permissions, requestCode);
                }
            }
            break;
            case STORAGE_PERMISSION_REQUEST: {
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    manualPermission(context, storagePermissionMsg, permissions, requestCode);
                }
            }
            break;
            case CALL_PHONE_PERMISSION_REQUEST: {
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    manualPermission(context, callPermissionMsg, permissions, requestCode);
                }
            }
            break;
            case LOCATION_PERMISSION_REQUEST: {
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    manualPermission(context, locationPermissionMsg, permissions, requestCode);
                }
            }
            break;
            case READ_CONTACTS_PERMISSION_REQUEST: {
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    manualPermission(context, readContactsPermissionMsg, permissions, requestCode);
                }
            }
            break;
        }
    }

    private void exitScreenPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builder.setTitle("Alert");
        builder.setIcon(R.drawable.icon_alertred);
        builder.setMessage("Are you sure you want to exit? Any unsaved data will be lost.");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            finish();
        });
        builder.setNegativeButton("NO", (dialog, which) -> {

        });
        builder.create().show();
    }
}