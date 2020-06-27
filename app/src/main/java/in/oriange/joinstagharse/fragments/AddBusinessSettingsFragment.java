package in.oriange.joinstagharse.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.UserSessionManager;
import in.oriange.joinstagharse.utilities.Utilities;

public class AddBusinessSettingsFragment extends Fragment {


    @BindView(R.id.cb_is_enquiry_available)
    CheckBox cbIsEnquiryAvailable;
    @BindView(R.id.cb_is_pick_up_available)
    CheckBox cbIsPickUpAvailable;
    @BindView(R.id.cb_is_home_delivery_available)
    CheckBox cbIsHomeDeliveryAvailable;
    @BindView(R.id.cb_show_in_search)
    CheckBox cbShowInSearch;
    @BindView(R.id.sv_scroll)
    NestedScrollView svScroll;

    private Context context;
    private ProgressDialog pd;
    private UserSessionManager session;
    private String userId;

    private LocalBroadcastManager localBroadcastManager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_business_settings, container, false);
        ButterKnife.bind(this, rootView);

        context = getActivity();
        init();
        setDefault();
        getSessionDetails();
        setEventHandler();
        return rootView;
    }

    private void init() {
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);
    }

    private void setDefault() {
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("AddBusinessSettingFragment");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
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

    private void setEventHandler() {

    }

    private void submitData() {

        String isVisible = "1";
        String isEnquiryAvailable = cbIsEnquiryAvailable.isChecked() ? "1" : "0";
        String isPickUpAvailable = cbIsPickUpAvailable.isChecked() ? "1" : "0";
        String isHomeDeliveryAvailable = cbIsHomeDeliveryAvailable.isChecked() ? "1" : "0";

        if (isPickUpAvailable.equals("0") && isHomeDeliveryAvailable.equals("0")) {
            Utilities.showMessage("Please select delivery type", context, 2);
            return;
        }

        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("AddBusinessSettingActivity")
                .putExtra("isVisible", isVisible)
                .putExtra("isEnquiryAvailable", isEnquiryAvailable)
                .putExtra("isPickUpAvailable", isPickUpAvailable)
                .putExtra("isHomeDeliveryAvailable", isHomeDeliveryAvailable));

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
