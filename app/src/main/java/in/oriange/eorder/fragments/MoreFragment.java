package in.oriange.eorder.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import in.oriange.eorder.R;
import in.oriange.eorder.activities.ContactUsActivity;
import in.oriange.eorder.activities.EnquiriesActivity;
import in.oriange.eorder.activities.MyAddressActivity;
import in.oriange.eorder.activities.MyBusinessActivity;
import in.oriange.eorder.activities.SettingsActivity;
import in.oriange.eorder.activities.ViewBasicInformationActivity;

public class MoreFragment extends Fragment {

    private Context context;
    private CardView cv_basicinfo, cv_mybusiness, cv_myaddress, cv_settings, cv_enquires, cv_contactus;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_more, container, false);
        context = getActivity();
        init(rootView);
        setDefault();
        setEventHandler();
        return rootView;
    }

    private void init(View rootView) {
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        cv_basicinfo = rootView.findViewById(R.id.cv_basicinfo);
        cv_mybusiness = rootView.findViewById(R.id.cv_mybusiness);
        cv_myaddress = rootView.findViewById(R.id.cv_myaddress);
        cv_settings = rootView.findViewById(R.id.cv_settings);
        cv_enquires = rootView.findViewById(R.id.cv_enquires);
        cv_contactus = rootView.findViewById(R.id.cv_contactus);
    }

    private void setDefault() {

    }

    private void setEventHandler() {
        cv_basicinfo.setOnClickListener(v -> startActivity(new Intent(context, ViewBasicInformationActivity.class)));

        cv_myaddress.setOnClickListener(v -> startActivity(new Intent(context, MyAddressActivity.class)));

        cv_mybusiness.setOnClickListener(v -> startActivity(new Intent(context, MyBusinessActivity.class)));

        cv_settings.setOnClickListener(v -> startActivity(new Intent(context, SettingsActivity.class)));

        cv_enquires.setOnClickListener(v -> startActivity(new Intent(context, EnquiriesActivity.class)));

        cv_contactus.setOnClickListener(v -> startActivity(new Intent(context, ContactUsActivity.class)));
    }
}
