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
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.github.angads25.toggle.widget.LabeledSwitch;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.UserSessionManager;
import in.oriange.joinstagharse.utilities.Utilities;

public class AddBusinessSettingsFragment extends Fragment {


    @BindView(R.id.sv_scroll)
    NestedScrollView svScroll;
    @BindView(R.id.sw_is_enquiry_available)
    LabeledSwitch swIsEnquiryAvailable;
    @BindView(R.id.sw_is_pick_up_available)
    LabeledSwitch swIsPickUpAvailable;
    @BindView(R.id.sw_is_home_delivery_available)
    LabeledSwitch swIsHomeDeliveryAvailable;
    @BindView(R.id.ib_is_enquiry_available)
    ImageButton ibIsEnquiryAvailable;
    @BindView(R.id.ib_is_pick_up_available)
    ImageButton ibIsPickUpAvailable;
    @BindView(R.id.ib_is_home_delivery_available)
    ImageButton ibIsHomeDeliveryAvailable;

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
        swIsEnquiryAvailable.setOnToggledListener((toggleableView, isOn) -> {
            if (isOn) {
                swIsEnquiryAvailable.setColorBorder(getResources().getColor(R.color.LimeGreen));
                swIsEnquiryAvailable.setColorOn(getResources().getColor(R.color.LimeGreen));
            } else {
                swIsEnquiryAvailable.setColorBorder(getResources().getColor(R.color.colorPrimary));
                swIsEnquiryAvailable.setColorOn(getResources().getColor(R.color.colorPrimary));
            }
        });

        swIsPickUpAvailable.setOnToggledListener((toggleableView, isOn) -> {
            if (isOn) {
                swIsPickUpAvailable.setColorBorder(getResources().getColor(R.color.LimeGreen));
                swIsPickUpAvailable.setColorOn(getResources().getColor(R.color.LimeGreen));
            } else {
                swIsPickUpAvailable.setColorBorder(getResources().getColor(R.color.colorPrimary));
                swIsPickUpAvailable.setColorOn(getResources().getColor(R.color.colorPrimary));
            }
        });

        swIsHomeDeliveryAvailable.setOnToggledListener((toggleableView, isOn) -> {
            if (isOn) {
                swIsHomeDeliveryAvailable.setColorBorder(getResources().getColor(R.color.LimeGreen));
                swIsHomeDeliveryAvailable.setColorOn(getResources().getColor(R.color.LimeGreen));
            } else {
                swIsHomeDeliveryAvailable.setColorBorder(getResources().getColor(R.color.colorPrimary));
                swIsHomeDeliveryAvailable.setColorOn(getResources().getColor(R.color.colorPrimary));
            }
        });

        ibIsEnquiryAvailable.setOnClickListener(v -> Utilities.showAlertDialogNormal(context, "If you allow enquiries, Enquire will be enabled in Search Business section. Joinsta Gharse users will be able to send you enquiries. You can view enquiries in my business section."));

        ibIsPickUpAvailable.setOnClickListener(v -> Utilities.showAlertDialogNormal(context, "If you enable store pickup, Joinsta Gharse users can choose store pickup option while placing order online for your business."));

        ibIsHomeDeliveryAvailable.setOnClickListener(v -> Utilities.showAlertDialogNormal(context, "If you enable home delivery, Joinsta Gharse users can choose home delivery option while placing order online for your business. Enable this option only if you do home delivery for the placed orders."));
    }

    private void submitData() {

        String isVisible = "1";
        String isEnquiryAvailable = swIsEnquiryAvailable.isOn() ? "1" : "0";
        String isPickUpAvailable = swIsPickUpAvailable.isOn() ? "1" : "0";
        String isHomeDeliveryAvailable = swIsHomeDeliveryAvailable.isOn() ? "1" : "0";

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
