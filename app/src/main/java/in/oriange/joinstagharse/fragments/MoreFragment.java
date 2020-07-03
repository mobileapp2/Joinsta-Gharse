package in.oriange.joinstagharse.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.activities.ContactUsActivity;
import in.oriange.joinstagharse.activities.EnquiriesActivity;
import in.oriange.joinstagharse.activities.MyAddedOffersActivity;
import in.oriange.joinstagharse.activities.MyAddressActivity;
import in.oriange.joinstagharse.activities.MyBusinessActivity;
import in.oriange.joinstagharse.activities.MyServicesActivity;
import in.oriange.joinstagharse.activities.NotificationActivity;
import in.oriange.joinstagharse.activities.PolicyActivity;
import in.oriange.joinstagharse.activities.SettingsActivity;
import in.oriange.joinstagharse.activities.ViewBasicInformationActivity;

public class MoreFragment extends Fragment {

    @BindView(R.id.ib_notifications)
    ImageButton ibNotifications;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.cv_basicinfo)
    CardView cvBasicinfo;
    @BindView(R.id.cv_mybusiness)
    CardView cvMybusiness;
    @BindView(R.id.cv_myservice)
    CardView cvMyservice;
    @BindView(R.id.cv_myoffres)
    CardView cvMyoffres;
    @BindView(R.id.cv_myaddress)
    CardView cvMyaddress;
    @BindView(R.id.cv_enquires)
    CardView cvEnquires;
    @BindView(R.id.cv_contactus)
    CardView cvContactus;
    @BindView(R.id.cv_policies)
    CardView cvPolicies;
    @BindView(R.id.cv_settings)
    CardView cvSettings;

    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_more, container, false);
        ButterKnife.bind(this, rootView);

        context = getActivity();
        init();
        setDefault();
        setEventHandler();
        return rootView;
    }

    private void init() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void setDefault() {

    }

    private void setEventHandler() {
        cvBasicinfo.setOnClickListener(v -> startActivity(new Intent(context, ViewBasicInformationActivity.class)));

        cvMyaddress.setOnClickListener(v -> startActivity(new Intent(context, MyAddressActivity.class)));

        cvMybusiness.setOnClickListener(v -> startActivity(new Intent(context, MyBusinessActivity.class)));

        cvSettings.setOnClickListener(v -> startActivity(new Intent(context, SettingsActivity.class)));

        cvEnquires.setOnClickListener(v -> startActivity(new Intent(context, EnquiriesActivity.class)));

        cvContactus.setOnClickListener(v -> startActivity(new Intent(context, ContactUsActivity.class)));

        cvMyoffres.setOnClickListener(v -> startActivity(new Intent(context, MyAddedOffersActivity.class)));

        cvPolicies.setOnClickListener(v -> startActivity(new Intent(context, PolicyActivity.class)));

        cvMyservice.setOnClickListener(v -> startActivity(new Intent(context, MyServicesActivity.class)));

        ibNotifications.setOnClickListener(v -> startActivity(new Intent(context, NotificationActivity.class)));

    }
}
