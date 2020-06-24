package in.oriange.joinstagharse.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.joinstagharse.R;

public class AddBusinessOtherDetails extends Fragment {

    @BindView(R.id.cb_is_enquiry_available)
    CheckBox cbIsEnquiryAvailable;
    @BindView(R.id.cb_is_pick_up_available)
    CheckBox cbIsPickUpAvailable;
    @BindView(R.id.cb_is_home_delivery_available)
    CheckBox cbIsHomeDeliveryAvailable;
    @BindView(R.id.cb_show_in_search)
    CheckBox cbShowInSearch;
    @BindView(R.id.sv_scroll)
    ScrollView svScroll;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_business_other_details, container, false);
        ButterKnife.bind(this, rootView);

        context = getActivity();
        init(rootView);
        setDefault();
        getSessionDetails();
        setEventHandler();
        return rootView;
    }

    private void init(View rootView) {

    }

    private void setDefault() {

    }

    private void getSessionDetails() {

    }

    private void setEventHandler() {

    }


}
