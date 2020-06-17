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
import in.oriange.joinstagharse.activities.AddCustomerActivity;
import in.oriange.joinstagharse.adapters.CustomersAdapter;
import in.oriange.joinstagharse.models.CustomerModel;
import in.oriange.joinstagharse.utilities.APICall;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.UserSessionManager;
import in.oriange.joinstagharse.utilities.Utilities;

public class CustomersFragment extends Fragment {

    private Context context;
    private UserSessionManager session;
    private FloatingActionButton btn_add;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rv_customers;
    private CheckBox cb_prime;
    private LinearLayout ll_nopreview;
    private EditText edt_search;
    private SpinKitView progressBar;
    private List<CustomerModel.ResultBean> customersList, mFilteredCustomersList;

    private String userId;
    private ProgressDialog pd;

    private LocalBroadcastManager localBroadcastManager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_customer, container, false);
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
        rv_customers = rootView.findViewById(R.id.rv_customers);
        rv_customers.setLayoutManager(new LinearLayoutManager(context));

        customersList = new ArrayList<>();
        mFilteredCustomersList = new ArrayList<>();
    }

    private void setDefault() {
        if (Utilities.isNetworkAvailable(context)) {
            new GetCustomerList().execute();
        }

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("CustomersFragment");
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
                new GetCustomerList().execute();
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
                    rv_customers.setAdapter(new CustomersAdapter(context, mFilteredCustomersList));
                    return;
                }

                if (mFilteredCustomersList.size() == 0) {
                    rv_customers.setVisibility(View.GONE);
                    return;
                }

                if (!query.toString().equals("")) {
                    ArrayList<CustomerModel.ResultBean> customerSearchedList = new ArrayList<>();
                    for (CustomerModel.ResultBean customerDetails : mFilteredCustomersList) {

                        String customerToBeSearched = customerDetails.getName().toLowerCase() +
                                customerDetails.getMobile().toLowerCase();

                        if (customerToBeSearched.contains(query.toString().toLowerCase())) {
                            customerSearchedList.add(customerDetails);
                        }
                    }
                    rv_customers.setAdapter(new CustomersAdapter(context, customerSearchedList));
                } else {
                    rv_customers.setAdapter(new CustomersAdapter(context, mFilteredCustomersList));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cb_prime.setOnCheckedChangeListener((buttonView, isChecked) -> showOnlyPrimeCustomer(isChecked));

        btn_add.setOnClickListener(v ->
                context.startActivity(new Intent(context, AddCustomerActivity.class)
                        .putExtra("businessCode", "")
                        .putExtra("businessName", "")
                        .putExtra("name", "")
                        .putExtra("countryCode", "+91")
                        .putExtra("mobile", "")
                        .putExtra("email", "")
                        .putExtra("city", "")));
    }

    private void showOnlyPrimeCustomer(boolean isChecked) {
        if (isChecked) {
            List<CustomerModel.ResultBean> filteredCustomersList = new ArrayList<>();

            for (CustomerModel.ResultBean customerDetails : customersList)
                if (customerDetails.getIs_prime_customer().equals("1"))
                    filteredCustomersList.add(customerDetails);

            mFilteredCustomersList = filteredCustomersList;
            rv_customers.setAdapter(new CustomersAdapter(context, mFilteredCustomersList));
        } else {
            mFilteredCustomersList = customersList;
            rv_customers.setAdapter(new CustomersAdapter(context, customersList));
        }
    }

    private class GetCustomerList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            rv_customers.setVisibility(View.GONE);
            ll_nopreview.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getCustomer");
            obj.addProperty("user_id", userId);
            res = APICall.JSONAPICall(ApplicationConstants.CUSTOMERAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            edt_search.setText("");
            progressBar.setVisibility(View.GONE);
            cb_prime.setChecked(false);
            edt_search.setText("");
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    customersList = new ArrayList<>();
                    CustomerModel pojoDetails = new Gson().fromJson(result, CustomerModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        customersList = pojoDetails.getResult();
                        mFilteredCustomersList = customersList;
                        rv_customers.setVisibility(View.VISIBLE);
                        rv_customers.setAdapter(new CustomersAdapter(context, customersList));
                    } else {
                        ll_nopreview.setVisibility(View.VISIBLE);
                        rv_customers.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ll_nopreview.setVisibility(View.VISIBLE);
                rv_customers.setVisibility(View.GONE);
            }
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Utilities.isNetworkAvailable(context)) {
                new GetCustomerList().execute();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }

}
