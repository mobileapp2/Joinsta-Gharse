package in.oriange.eorder.fragments;

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
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import in.oriange.eorder.R;
import in.oriange.eorder.adapters.SearchBusinessAdapter;
import in.oriange.eorder.models.SearchDetailsModel;
import in.oriange.eorder.utilities.APICall;
import in.oriange.eorder.utilities.ApplicationConstants;
import in.oriange.eorder.utilities.UserSessionManager;
import in.oriange.eorder.utilities.Utilities;

public class SearchFragment extends Fragment {

    private Context context;
    private UserSessionManager session;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rv_searchlist;
    private LinearLayout ll_nopreview;
    private EditText edt_search;
    private static SpinKitView progressBar;
    public static ArrayList<SearchDetailsModel.ResultBean.BusinessesBean> businessList;

    private static String userId;
    private static String categoryTypeId;
    private ProgressDialog pd;

    private LocalBroadcastManager localBroadcastManager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        context = getActivity();
        init(rootView);
        setDefault();
        getSessionDetails();
        setEventHandler();
        return rootView;
    }

    private void init(View rootView) {
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        progressBar = rootView.findViewById(R.id.progressBar);
        edt_search = rootView.findViewById(R.id.edt_search);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        ll_nopreview = rootView.findViewById(R.id.ll_nopreview);
        rv_searchlist = rootView.findViewById(R.id.rv_searchlist);
        rv_searchlist.setLayoutManager(new LinearLayoutManager(context));

        businessList = new ArrayList<>();
    }

    private void setDefault() {
        categoryTypeId = "1";

        if (Utilities.isNetworkAvailable(context)) {
            new GetSearchList().execute(session.getLocation().get(ApplicationConstants.KEY_LOCATION_INFO));
        }

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("SearchFragment");
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
                new GetSearchList().execute(session.getLocation().get(ApplicationConstants.KEY_LOCATION_INFO));
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
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchDetails(categoryTypeId, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void searchDetails(String categoryTypeId, String query) {
        if (businessList.size() == 0) {
            return;
        }

        if (!query.equals("")) {
            ArrayList<SearchDetailsModel.ResultBean.BusinessesBean> businessSearchedList = new ArrayList<>();
            for (SearchDetailsModel.ResultBean.BusinessesBean businessDetails : businessList) {

                StringBuilder tag = new StringBuilder();
                if (businessDetails.getTag().get(0) != null)
                    for (SearchDetailsModel.ResultBean.BusinessesBean.TagBeanXX tags : businessDetails.getTag().get(0)) {
                        if (tags != null)
                            tag.append(tags.getTag_name());
                    }

                String businessToBeSearched = businessDetails.getBusiness_code().toLowerCase() +
                        businessDetails.getBusiness_name().toLowerCase() +
                        businessDetails.getCity().toLowerCase() + tag.toString().toLowerCase();
                if (businessToBeSearched.contains(query.toLowerCase())) {
                    businessSearchedList.add(businessDetails);
                }
            }
            rv_searchlist.setAdapter(new SearchBusinessAdapter(context, businessSearchedList, "1"));
        } else {
            rv_searchlist.setAdapter(new SearchBusinessAdapter(context, businessList, "1"));
        }
    }

    private class GetSearchList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            rv_searchlist.setVisibility(View.GONE);
            ll_nopreview.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getDetailsByLocation");
            obj.addProperty("user_id", userId);
            obj.addProperty("location", params[0]);
            res = APICall.JSONAPICall(ApplicationConstants.SEARCHAPI, obj.toString());
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
                    businessList = new ArrayList<>();
                    SearchDetailsModel pojoDetails = new Gson().fromJson(result, SearchDetailsModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        businessList = pojoDetails.getResult().getBusinesses();
                        setDataToRecyclerView(categoryTypeId);
                    } else {
                        ll_nopreview.setVisibility(View.VISIBLE);
                        rv_searchlist.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ll_nopreview.setVisibility(View.VISIBLE);
                rv_searchlist.setVisibility(View.GONE);
            }
        }
    }

    private void setDataToRecyclerView(String categoryTypeId) {
//        edt_search.setText("");
        if (businessList.size() > 0) {
            rv_searchlist.setAdapter(new SearchBusinessAdapter(context, businessList, "1"));
            ll_nopreview.setVisibility(View.GONE);
            rv_searchlist.setVisibility(View.VISIBLE);
        } else {
            rv_searchlist.setVisibility(View.GONE);
            ll_nopreview.setVisibility(View.VISIBLE);
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Utilities.isNetworkAvailable(context)) {
                new GetSearchList().execute(session.getLocation().get(ApplicationConstants.KEY_LOCATION_INFO));
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }
}
