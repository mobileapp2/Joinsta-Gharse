package in.oriange.joinstagharse.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.activities.AddVendorActivity;
import in.oriange.joinstagharse.adapters.VendorsAdapter;
import in.oriange.joinstagharse.models.VendorModel;
import in.oriange.joinstagharse.utilities.APICall;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.UserSessionManager;
import in.oriange.joinstagharse.utilities.Utilities;

public class VendorsFragment extends Fragment {

    private Context context;
    private UserSessionManager session;
    private FloatingActionButton btn_add;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rv_vendors;
    private CheckBox cb_prime;
    private LinearLayout ll_nopreview;
    private EditText edt_search;
    private SpinKitView progressBar;
    private List<VendorModel.ResultBean> vendorsList, mFilteredVendorsList;

    private String userId;
    private ProgressDialog pd;

    private LocalBroadcastManager localBroadcastManager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_vendors, container, false);
        context = getActivity();
        init(rootView);
        getSessionDetails();
        setDefault();
        setEventHandler();
        return rootView;
    }

    private void init(View rootView) {
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        btn_add = rootView.findViewById(R.id.btn_add);
        cb_prime = rootView.findViewById(R.id.cb_prime);
        progressBar = rootView.findViewById(R.id.progressBar);
        edt_search = rootView.findViewById(R.id.edt_search);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        ll_nopreview = rootView.findViewById(R.id.ll_nopreview);
        rv_vendors = rootView.findViewById(R.id.rv_vendors);
        rv_vendors.setLayoutManager(new LinearLayoutManager(context));

        vendorsList = new ArrayList<>();
        mFilteredVendorsList = new ArrayList<>();
    }

    private void setDefault() {
        if (Utilities.isNetworkAvailable(context)) {
            new GetVendorList().execute();
        }

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("VendorsFragment");
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
        swipeRefreshLayout.setOnRefreshListener(() -> {
            edt_search.setText("");
            if (Utilities.isNetworkAvailable(context)) {
                new GetVendorList().execute();
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {

                if (query.toString().isEmpty()) {
                    rv_vendors.setAdapter(new VendorsAdapter(context, mFilteredVendorsList));
                    return;
                }

                if (mFilteredVendorsList.size() == 0) {
                    rv_vendors.setVisibility(View.GONE);
                    return;
                }

                if (!query.toString().equals("")) {
                    ArrayList<VendorModel.ResultBean> vendorSearchedList = new ArrayList<>();
                    for (VendorModel.ResultBean vendorDetails : mFilteredVendorsList) {

                        String vendorToBeSearched = vendorDetails.getName().toLowerCase() +
                                vendorDetails.getMobile().toLowerCase();

                        if (vendorToBeSearched.contains(query.toString().toLowerCase())) {
                            vendorSearchedList.add(vendorDetails);
                        }
                    }
                    rv_vendors.setAdapter(new VendorsAdapter(context, vendorSearchedList));
                } else {
                    rv_vendors.setAdapter(new VendorsAdapter(context, mFilteredVendorsList));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cb_prime.setOnCheckedChangeListener((buttonView, isChecked) -> showOnlyPrimeVendors(isChecked));

        btn_add.setOnClickListener(v -> context.startActivity(new Intent(context, AddVendorActivity.class)
                .putExtra("businessCode", "")
                .putExtra("businessName", "")
                .putExtra("name", "")
                .putExtra("mobile", "")));
    }

    private void showOnlyPrimeVendors(boolean isChecked) {
        if (isChecked) {
            List<VendorModel.ResultBean> filteredVendorsList = new ArrayList<>();

            for (VendorModel.ResultBean vendorDetails : vendorsList)
                if (vendorDetails.getIs_prime_vendor().equals("1"))
                    filteredVendorsList.add(vendorDetails);

            mFilteredVendorsList = filteredVendorsList;
            rv_vendors.setAdapter(new VendorsAdapter(context, mFilteredVendorsList));
        } else {
            mFilteredVendorsList = vendorsList;
            rv_vendors.setAdapter(new VendorsAdapter(context, vendorsList));
        }
    }

    private class GetVendorList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            rv_vendors.setVisibility(View.GONE);
            ll_nopreview.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getVendor");
            obj.addProperty("user_id", userId);
            res = APICall.JSONAPICall(ApplicationConstants.VENDORAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            edt_search.setText("");
            progressBar.setVisibility(View.GONE);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    vendorsList = new ArrayList<>();
                    VendorModel pojoDetails = new Gson().fromJson(result, VendorModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        vendorsList = pojoDetails.getResult();
                        mFilteredVendorsList = vendorsList;
                        rv_vendors.setVisibility(View.VISIBLE);
                        rv_vendors.setAdapter(new VendorsAdapter(context, vendorsList));
                    } else {
                        ll_nopreview.setVisibility(View.VISIBLE);
                        rv_vendors.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ll_nopreview.setVisibility(View.VISIBLE);
                rv_vendors.setVisibility(View.GONE);
            }
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Utilities.isNetworkAvailable(context)) {
                new GetVendorList().execute();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }

}
