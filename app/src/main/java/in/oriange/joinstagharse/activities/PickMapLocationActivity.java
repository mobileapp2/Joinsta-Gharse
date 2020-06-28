package in.oriange.joinstagharse.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.models.MapAddressListModel;
import in.oriange.joinstagharse.utilities.FieldSelector;
import in.oriange.joinstagharse.utilities.Utilities;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static in.oriange.joinstagharse.utilities.PermissionUtil.doesAppNeedPermissions;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.CALL_PHONE_PERMISSION_REQUEST;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.CAMERA_AND_STORAGE_PERMISSION_REQUEST;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.LOCATION_PERMISSION;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.LOCATION_PERMISSION_REQUEST;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.READ_CONTACTS_PERMISSION_REQUEST;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.STORAGE_PERMISSION_REQUEST;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.callPermissionMsg;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.cameraStoragePermissionMsg;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.isLocationPermissionGiven;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.locationPermissionMsg;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.manualPermission;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.readContactsPermissionMsg;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.storagePermissionMsg;
import static in.oriange.joinstagharse.utilities.Utilities.changeStatusBar;
import static in.oriange.joinstagharse.utilities.Utilities.isLocationEnabled;
import static in.oriange.joinstagharse.utilities.Utilities.turnOnLocation;


public class PickMapLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private LatLng latLng1;
    private Context context;
    private GoogleMap mMap;
    private FloatingActionButton btn_pick;
    private FieldSelector fieldSelector;
    private Button btn_select_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_map_loaction);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        context = PickMapLocationActivity.this;
        btn_pick = findViewById(R.id.btn_pick);
        btn_select_location = findViewById(R.id.btn_select_location);
        changeStatusBar(context, getWindow());

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.places_api_key), Locale.US);
        }

        fieldSelector = new FieldSelector(findViewById(R.id.use_custom_fields), findViewById(R.id.custom_fields_list), savedInstanceState);
        setupAutocompleteSupportFragment();
    }

    private void setupAutocompleteSupportFragment() {
        final AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_support_fragment);
        autocompleteSupportFragment.setPlaceFields(getPlaceFields());
        autocompleteSupportFragment.setOnPlaceSelectedListener(getPlaceSelectionListener());
    }

    private List<Place.Field> getPlaceFields() {
        return fieldSelector.getAllFields();
    }

    @NonNull
    private PlaceSelectionListener getPlaceSelectionListener() {
        return new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                MapAddressListModel addressList = new MapAddressListModel();

                try {
                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
                    addressList.setAddress_line_one(place.getAddress());
                    addressList.setDistrict(addresses.get(0).getLocality());
                    addressList.setCountry(addresses.get(0).getCountryName());
                    addressList.setState(addresses.get(0).getAdminArea());
                    addressList.setPincode(addresses.get(0).getPostalCode());
                    addressList.setMap_location_lattitude(String.valueOf(place.getLatLng().latitude));
                    addressList.setMap_location_logitude(String.valueOf(place.getLatLng().longitude));

                    Intent intent = getIntent();
                    intent.putExtra("addressList", addressList);
                    setResult(RESULT_OK, intent);

                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                    finish();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(@NonNull Status status) {

            }
        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        init();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        }
    }

    private void init() {
        btn_pick.setOnClickListener(v -> {
            if (doesAppNeedPermissions()) {
                if (!isLocationPermissionGiven(context, LOCATION_PERMISSION)) {
                    return;
                }
            }

            if (!isLocationEnabled(context)) {
                turnOnLocation(context);
                return;
            }

            if (latLng == null) {
                Utilities.showAlertDialog(context, "Unable to get address from current location. Please try again or search manually", false);
                return;
            }

            addMapMarker(latLng);
        });

        btn_select_location.setOnClickListener(v -> {
            if (latLng1 != null) {
                try {
                    getAllAddress();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Utilities.showAlertDialog(context, "Please search and pick a location", false);
            }
        });

        mMap.setOnMapClickListener(latLng -> addMapMarker(latLng));
    }

    public void getAllAddress() throws IOException {
        MapAddressListModel addressList = new MapAddressListModel();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(latLng1.latitude, latLng1.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        if (addresses.get(0).getLocality() == null) {
            Utilities.showAlertDialog(context, "Unable to get address from this location. Please try again or search manually", false);
            return;
        }

        addressList.setAddress_line_one(addresses.get(0).getAddressLine(0));
        addressList.setDistrict(addresses.get(0).getLocality());
        addressList.setCountry(addresses.get(0).getCountryName());
        addressList.setState(addresses.get(0).getAdminArea());
        addressList.setPincode(addresses.get(0).getPostalCode());
        addressList.setMap_location_lattitude(String.valueOf(latLng1.latitude));
        addressList.setMap_location_logitude(String.valueOf(latLng1.longitude));

        Intent intent = getIntent();
        intent.putExtra("addressList", addressList);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void addMapMarker(LatLng latLng) {
        btn_select_location.setVisibility(View.VISIBLE);
        latLng1 = latLng;
        mMap.clear();
        if (latLng != null) {
            mMap.addMarker(new MarkerOptions().position(latLng));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)
                    .zoom(10).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private LocationRequest mLocationRequest;

    private long UPDATE_INTERVAL = 10 * 10000000;
    private long FASTEST_INTERVAL = 20000000;
    private LatLng latLng;

    @SuppressLint("RestrictedApi")
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    public void onLocationChanged(Location location) {
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
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
                } else {
                    if (!isLocationEnabled(context)) {
                        turnOnLocation(context);
                        return;
                    }
                    startLocationUpdates();
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

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        hideSoftKeyboard(PickMapLocationActivity.this);
//    }
}
