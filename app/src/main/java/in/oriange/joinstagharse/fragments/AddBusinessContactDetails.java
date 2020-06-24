package in.oriange.joinstagharse.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.joinstagharse.R;

public class AddBusinessContactDetails extends Fragment {


    @BindView(R.id.btn_add_mobile)
    Button btnAddMobile;
    @BindView(R.id.ll_mobile)
    LinearLayout llMobile;
    @BindView(R.id.btn_add_landline)
    Button btnAddLandline;
    @BindView(R.id.ll_landline)
    LinearLayout llLandline;
    @BindView(R.id.edt_email)
    EditText edtEmail;
    @BindView(R.id.edt_website)
    EditText edtWebsite;
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
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_business_contact_details, container, false);
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
