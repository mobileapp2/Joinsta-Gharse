package in.oriange.joinstagharse.fragments;

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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import java.util.List;

import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.activities.BookOrderCartProductsActivity;
import in.oriange.joinstagharse.activities.SelectCityActivity;
import in.oriange.joinstagharse.adapters.SearchBusinessAdapter;
import in.oriange.joinstagharse.models.SearchDetailsModel;
import in.oriange.joinstagharse.utilities.APICall;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.UserSessionManager;
import in.oriange.joinstagharse.utilities.Utilities;

public class SearchFragment extends Fragment {

    private Context context;
    private TextView tv_location;
    private UserSessionManager session;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rv_searchlist;
    private LinearLayout ll_nopreview;
    private ImageButton ib_cart;
    private EditText edt_search;
    private TextView tv_cart_count;
    private CheckBox cb_favourite;
    private SpinKitView progressBar;
    private List<SearchDetailsModel.ResultBean.BusinessesBean> businessList;
    private String userId;
    private SearchBusinessAdapter searchBusinessAdapter;
    private LocalBroadcastManager localBroadcastManager;
    private LocalBroadcastManager localBroadcastManager2;
    private boolean isFavouriteSelected;

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
        ib_cart = rootView.findViewById(R.id.ib_cart);
        tv_cart_count = rootView.findViewById(R.id.tv_cart_count);
        progressBar = rootView.findViewById(R.id.progressBar);
        edt_search = rootView.findViewById(R.id.edt_search);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        tv_location = rootView.findViewById(R.id.tv_location);
        ll_nopreview = rootView.findViewById(R.id.ll_nopreview);
        cb_favourite = rootView.findViewById(R.id.cb_favourite);
        rv_searchlist = rootView.findViewById(R.id.rv_searchlist);
        rv_searchlist.setLayoutManager(new LinearLayoutManager(context));

        businessList = new ArrayList<>();

        searchBusinessAdapter = new SearchBusinessAdapter(context, businessList, "1");
        rv_searchlist.setAdapter(searchBusinessAdapter);
    }

    private void setDefault() {
        try {
            UserSessionManager session = new UserSessionManager(context);
            tv_location.setText(session.getLocation().get(ApplicationConstants.KEY_LOCATION_INFO));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Utilities.isNetworkAvailable(context)) {
            new GetSearchList().execute(session.getLocation().get(ApplicationConstants.KEY_LOCATION_INFO));
        }

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("SearchFragment");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);

        localBroadcastManager2 = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter2 = new IntentFilter("SearchFragmentUpdateCartCount");
        localBroadcastManager2.registerReceiver(broadcastReceiver2, intentFilter2);

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

        tv_location.setOnClickListener(v -> {
            startActivity(new Intent(context, SelectCityActivity.class)
                    .putExtra("requestCode", 1));
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            edt_search.setText("");
            if (Utilities.isNetworkAvailable(context)) {
                edt_search.setText("");
                cb_favourite.setChecked(false);
                new GetSearchList().execute(session.getLocation().get(ApplicationConstants.KEY_LOCATION_INFO));
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        ib_cart.setOnClickListener(v -> {
            startActivity(new Intent(context, BookOrderCartProductsActivity.class)
                    .putExtra("particularBusinessId", "0"));
        });

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setDataToRecyclerView(isFavouriteSelected, edt_search.getText().toString().trim(), businessList);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cb_favourite.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isFavouriteSelected = isChecked;
            setDataToRecyclerView(isFavouriteSelected, edt_search.getText().toString().trim(), businessList);
        });
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
            obj.addProperty("search_term", "");
//            obj.addProperty("location", params[0]);
            obj.addProperty("location", "");
            res = APICall.JSONAPICall(ApplicationConstants.SEARCHAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    businessList = new ArrayList<>();
                    SearchDetailsModel pojoDetails = new Gson().fromJson(result, SearchDetailsModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        businessList = pojoDetails.getResult().getBusinesses();
                        if (businessList.size() != 0) {
                            setDataToRecyclerView(isFavouriteSelected, edt_search.getText().toString().trim(), businessList);
                        } else {
                            ll_nopreview.setVisibility(View.VISIBLE);
                            rv_searchlist.setVisibility(View.GONE);
                        }
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

    private void setDataToRecyclerView(boolean isFavouriteSelected, String searchTerm, List<SearchDetailsModel.ResultBean.BusinessesBean> businessList) {
        if (businessList.size() == 0)
            return;

        List<SearchDetailsModel.ResultBean.BusinessesBean> filteredBusinessList = new ArrayList<>(businessList);

        if (isFavouriteSelected) {
            List<SearchDetailsModel.ResultBean.BusinessesBean> favBusinessList = new ArrayList<>();

            for (int i = 0; i < filteredBusinessList.size(); i++) {
                if (filteredBusinessList.get(i).getIsFavourite().equals("1"))
                    favBusinessList.add(filteredBusinessList.get(i));
            }

            filteredBusinessList.clear();
            filteredBusinessList.addAll(favBusinessList);
        } else {
            List<SearchDetailsModel.ResultBean.BusinessesBean> cityBusinessList = new ArrayList<>();

            for (int i = 0; i < filteredBusinessList.size(); i++) {
                if (filteredBusinessList.get(i).getCity().equalsIgnoreCase(session.getLocation().get(ApplicationConstants.KEY_LOCATION_INFO)))
                    cityBusinessList.add(filteredBusinessList.get(i));
            }

            filteredBusinessList.clear();
            filteredBusinessList.addAll(cityBusinessList);
        }

        if (!searchTerm.equals("")) {
            ArrayList<SearchDetailsModel.ResultBean.BusinessesBean> searchBusinessList = new ArrayList<>();
            if (filteredBusinessList.size() == 0)
                filteredBusinessList.addAll(businessList);
            for (SearchDetailsModel.ResultBean.BusinessesBean businessDetails : filteredBusinessList) {

                StringBuilder tag = new StringBuilder();
                if (businessDetails.getTag().get(0) != null)
                    for (SearchDetailsModel.ResultBean.BusinessesBean.TagBean tags : businessDetails.getTag().get(0)) {
                        if (tags != null)
                            tag.append(tags.getTag_name());
                    }

                String businessToBeSearched = businessDetails.getBusiness_code().toLowerCase() +
                        businessDetails.getBusiness_name().toLowerCase() +
                        businessDetails.getCity().toLowerCase() + tag.toString().toLowerCase();
                if (businessToBeSearched.contains(searchTerm.toLowerCase())) {
                    searchBusinessList.add(businessDetails);
                }
            }

            filteredBusinessList.clear();
            filteredBusinessList.addAll(searchBusinessList);
        }

        if (filteredBusinessList.size() > 0) {
            searchBusinessAdapter.refreshList(filteredBusinessList);
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

    private BroadcastReceiver broadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int cartCount = intent.getIntExtra("cartCount", 0);
            if (cartCount == 0) {
                tv_cart_count.setVisibility(View.GONE);
                tv_cart_count.setText("");
            } else {
                tv_cart_count.setVisibility(View.VISIBLE);
                tv_cart_count.setText(String.valueOf(cartCount));
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
        localBroadcastManager2.unregisterReceiver(broadcastReceiver2);
    }
}
