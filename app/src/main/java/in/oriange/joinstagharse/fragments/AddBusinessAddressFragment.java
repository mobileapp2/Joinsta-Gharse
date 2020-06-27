package in.oriange.joinstagharse.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.activities.PickMapLocationActivity;
import in.oriange.joinstagharse.models.MapAddressListModel;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.UserSessionManager;
import in.oriange.joinstagharse.utilities.Utilities;

import static android.app.Activity.RESULT_OK;

public class AddBusinessAddressFragment extends Fragment {


    @BindView(R.id.btn_select)
    MaterialButton btnSelect;
    @BindView(R.id.edt_address)
    EditText edtAddress;
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
    @BindView(R.id.sv_scroll)
    ScrollView svScroll;


    private static Context context;
    private UserSessionManager session;
    private String userId, latitude = "", longitude = "";
    private final int LOCATION_REQUEST = 300;

    private LocalBroadcastManager localBroadcastManager;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_business_address, container, false);
        ButterKnife.bind(this, rootView);

        context = getActivity();
        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        return rootView;
    }

    private void init() {
        session = new UserSessionManager(context);
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
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("AddBusinessAddressFragment");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void setEventHandler() {
        btnSelect.setOnClickListener(v -> startActivityForResult(new Intent(context, PickMapLocationActivity.class), LOCATION_REQUEST));
    }

    private void submitData() {
        if (edtAddress.getText().toString().trim().isEmpty()) {
            edtAddress.setError("Please select address");
            edtAddress.requestFocus();
            edtAddress.getParent().requestChildFocus(edtAddress, edtAddress);
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

        if (edtCity.getText().toString().trim().isEmpty()) {
            edtCity.setError("Please select city");
            edtCity.requestFocus();
            edtCity.getParent().requestChildFocus(edtCity, edtCity);
            return;
        }

        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("AddBusinessAddressActivity")
                .putExtra("address", edtAddress.getText().toString().trim())
                .putExtra("pincode", edtPincode.getText().toString().trim())
                .putExtra("city", edtCity.getText().toString().trim())
                .putExtra("district", edtDistrict.getText().toString().trim())
                .putExtra("state", edtState.getText().toString().trim())
                .putExtra("country", edtCountry.getText().toString().trim())
                .putExtra("latitude", latitude)
                .putExtra("longitude", longitude));
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
                    edtCity.setText(addressList.getDistrict());
                } else {
                    Utilities.showMessage("Address not found, please try again", context, 3);
                }
            }
        }
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
