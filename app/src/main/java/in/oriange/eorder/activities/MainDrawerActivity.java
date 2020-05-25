package in.oriange.eorder.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import in.oriange.eorder.R;
import in.oriange.eorder.adapters.BotNavViewPagerAdapter;
import in.oriange.eorder.models.MapAddressListModel;
import in.oriange.eorder.utilities.APICall;
import in.oriange.eorder.utilities.ApplicationConstants;
import in.oriange.eorder.utilities.UserSessionManager;
import in.oriange.eorder.utilities.Utilities;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static in.oriange.eorder.utilities.Utilities.changeStatusBar;
import static in.oriange.eorder.utilities.Utilities.isLocationEnabled;

public class MainDrawerActivity extends AppCompatActivity {

    private static Context context;
    private AHBottomNavigation bottomNavigation;
    private Fragment currentFragment;
    private BotNavViewPagerAdapter adapter;
    private AHBottomNavigationViewPager view_pager;

    private UserSessionManager session;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);

        context = MainDrawerActivity.this;
        session = new UserSessionManager(context);
        if (!Utilities.isNetworkAvailable(context)) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View promptView = layoutInflater.inflate(R.layout.dialog_layout_error, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
            alertDialogBuilder.setView(promptView);

            LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
            TextView tv_title = promptView.findViewById(R.id.tv_title);
            Button btn_ok = promptView.findViewById(R.id.btn_ok);

            animation_view.playAnimation();
            tv_title.setText("Please check your internet connection");
            btn_ok.setText("Retry");
            alertDialogBuilder.setCancelable(false);
            final AlertDialog alertD = alertDialogBuilder.create();

            btn_ok.setOnClickListener(v -> {
                alertD.dismiss();
                startActivity(new Intent(context, MainDrawerActivity.class)
                        .putExtra("startOrigin", 0));
                finish();
            });

            alertD.show();
            return;
        }

        init();
        setUpBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!session.isLocationSet()) {
            startActivity(new Intent(context, SelectCityActivity.class)
                    .putExtra("requestCode", 0));
        }
    }

    private void init() {
        changeStatusBar(context, getWindow());

        bottomNavigation = findViewById(R.id.bottomNavigation);
        view_pager = findViewById(R.id.view_pager);
        view_pager.setOffscreenPageLimit(4);
        adapter = new BotNavViewPagerAdapter(getSupportFragmentManager());
        view_pager.setAdapter(adapter);

        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            userId = json.getString("userid");

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Utilities.isNetworkAvailable(context)) {
            new RefreshSession().execute();
        }

        if (ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED /*&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED*/) {
            return;
        }

        if (!isLocationEnabled(context)) {
            return;
        }

        startLocationUpdates();

    }

    private void setUpBottomNavigation() {
        //Create items

        AHBottomNavigationItem bot1 = new AHBottomNavigationItem("Home", R.drawable.icon_home, R.color.colorPrimaryDark);
        AHBottomNavigationItem bot2 = new AHBottomNavigationItem("Search", R.drawable.icon_search, R.color.colorPrimaryDark);
        AHBottomNavigationItem bot3 = new AHBottomNavigationItem("Vendor/Customer", R.drawable.icon_group, R.color.colorPrimaryDark);
        AHBottomNavigationItem bot4 = new AHBottomNavigationItem("My Orders", R.drawable.icon_cart_256, R.color.colorPrimaryDark);
        AHBottomNavigationItem bot5 = new AHBottomNavigationItem("More", R.drawable.icon_more_1, R.color.colorPrimaryDark);

        //Add items

        bottomNavigation.addItem(bot1);
        bottomNavigation.addItem(bot2);
        bottomNavigation.addItem(bot3);
        bottomNavigation.addItem(bot4);
        bottomNavigation.addItem(bot5);

        bottomNavigation.setDefaultBackgroundColor(getResources().getColor(R.color.lightGray));
        bottomNavigation.setAccentColor(getResources().getColor(R.color.red));
        bottomNavigation.setInactiveColor(getResources().getColor(R.color.red));
        bottomNavigation.setForceTint(true);
        bottomNavigation.setTranslucentNavigationEnabled(true);
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE);

        bottomNavigation.setOnTabSelectedListener((position, wasSelected) -> {
            if (currentFragment == null) {
                currentFragment = adapter.getCurrentFragment();
            }

            view_pager.setCurrentItem(position, true);

            if (currentFragment == null) {
                return true;
            }

            currentFragment = adapter.getCurrentFragment();
            return true;
        });

        int startOrigin = getIntent().getIntExtra("startOrigin", 0);
        bottomNavigation.setCurrentItem(startOrigin);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 0 || requestCode == 1 || requestCode == 3) {
                MapAddressListModel addressList = (MapAddressListModel) data.getSerializableExtra("addressList");
                if (addressList != null) {
                    session.setLocation(addressList.getDistrict());
                    finishAffinity();
                    startActivity(new Intent(context, MainDrawerActivity.class)
                            .putExtra("startOrigin", requestCode));
                } else {
                    Utilities.showMessage("Address not found, please try again", context, 3);
                }
            }
        }
    }

    private class RefreshSession extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getUserDetails");
            obj.addProperty("user_id", userId);
            res = APICall.JSONAPICall(ApplicationConstants.USERSAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    if (type.equalsIgnoreCase("success")) {

                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                session.createUserLoginSession(jsonarr.toString());
                            }
                        }
                    } else {
                        Utilities.showMessage("User details failed to update", context, 3);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static LatLng latLng;

    @SuppressLint("RestrictedApi")
    public static void startLocationUpdates() {

        // Create the location request to start receiving updates
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        /* 10 secs */
        long UPDATE_INTERVAL = 10 * 10000000;
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        /* 2 sec */
        long FASTEST_INTERVAL = 20000;
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(context);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        getFusedLocationProviderClient(context).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    public static void onLocationChanged(Location location) {
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

}
