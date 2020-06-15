package in.oriange.joinstagharse.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.adapters.BannerSliderAdapter;
import in.oriange.joinstagharse.adapters.SearchBusinessAdapter;
import in.oriange.joinstagharse.models.BannerListModel;
import in.oriange.joinstagharse.models.SearchDetailsModel;
import in.oriange.joinstagharse.utilities.APICall;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.UserSessionManager;
import in.oriange.joinstagharse.utilities.Utilities;

import static in.oriange.joinstagharse.utilities.Utilities.changeStatusBar;

public class BizProfEmpDetailsListActivity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private CardView cv_banner;
    private SliderView imageSlider;
    private RecyclerView rv_searchlist;
    private SpinKitView progressBar;
    private LinearLayout ll_nopreview;

    public static List<SearchDetailsModel.ResultBean.BusinessesBean> businessList;
    private String userId, categoryTypeId;
    private GetSearchList getSearchList = new GetSearchList();
    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bizprofemp_detailslist);

        init();
        getSessionDetails();
        setDefault();
        setUpToolbar();
    }

    private void init() {
        context = BizProfEmpDetailsListActivity.this;
        session = new UserSessionManager(context);
        changeStatusBar(context, getWindow());
        cv_banner = findViewById(R.id.cv_banner);
        imageSlider = findViewById(R.id.imageSlider);
        ll_nopreview = findViewById(R.id.ll_nopreview);
        progressBar = findViewById(R.id.progressBar);
        rv_searchlist = findViewById(R.id.rv_searchlist);
        rv_searchlist.setLayoutManager(new LinearLayoutManager(context));

        businessList = new ArrayList<>();
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

    private void setDefault() {
        categoryTypeId = getIntent().getStringExtra("categoryTypeId");

        if (Utilities.isNetworkAvailable(context)) {
            new GetBannersForCategory().execute(categoryTypeId, session.getLocation().get(ApplicationConstants.KEY_LOCATION_INFO));

            getSearchList.execute(session.getLocation().get(ApplicationConstants.KEY_LOCATION_INFO));
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("BizProfEmpDetailsListActivity");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private class GetSearchList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            rv_searchlist.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getDetailsByLocation");
            obj.addProperty("user_id", userId);
            obj.addProperty("search_term", "");
            obj.addProperty("location", params[0]);
            res = APICall.JSONAPICall(ApplicationConstants.SEARCHAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            rv_searchlist.setVisibility(View.VISIBLE);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    businessList = new ArrayList<>();
                    SearchDetailsModel pojoDetails = new Gson().fromJson(result, SearchDetailsModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        businessList = pojoDetails.getResult().getBusinesses();
                        setDataToRecyclerView();
                    } else {
                        Utilities.showAlertDialog(context, "Categories not available", false);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showAlertDialog(context, "Server Not Responding", false);
            }
        }
    }

    private class GetBannersForCategory extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getCategoryBanners");
            obj.addProperty("category_id", params[0]);
            obj.addProperty("location", params[1]);
            res = APICall.JSONAPICall(ApplicationConstants.BANNERSAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type;
            try {
                if (!result.equals("")) {
                    List<BannerListModel.ResultBean> bannerList = new ArrayList<>();
                    BannerListModel pojoDetails = new Gson().fromJson(result, BannerListModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        bannerList = pojoDetails.getResult();
                        if (bannerList.size() > 0) {
                            cv_banner.setVisibility(View.VISIBLE);

                            BannerSliderAdapter adapter = new BannerSliderAdapter(context, bannerList);
                            imageSlider.setSliderAdapter(adapter);
                            imageSlider.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                            imageSlider.setSliderTransformAnimation(SliderAnimations.VERTICALFLIPTRANSFORMATION);
                            imageSlider.setIndicatorSelectedColor(Color.WHITE);
                            imageSlider.setIndicatorUnselectedColor(Color.GRAY);
                            imageSlider.setAutoCycle(true);
                            imageSlider.setScrollTimeInSec(10);
                        }
                    } else {
                        cv_banner.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                cv_banner.setVisibility(View.GONE);

            }
        }
    }

    private void setDataToRecyclerView() {
        if (businessList.size() > 0) {
            ArrayList<SearchDetailsModel.ResultBean.BusinessesBean> foundbiz = new ArrayList<SearchDetailsModel.ResultBean.BusinessesBean>();
            for (SearchDetailsModel.ResultBean.BusinessesBean bizdetails : businessList) {
                if (!bizdetails.getType_id().equals(categoryTypeId)) {
                    foundbiz.add(bizdetails);
                }
            }
            businessList.removeAll(foundbiz);
            if (businessList.size() == 0) {
                ll_nopreview.setVisibility(View.VISIBLE);
                rv_searchlist.setVisibility(View.GONE);
            } else {
                rv_searchlist.setAdapter(new SearchBusinessAdapter(context, businessList, "3"));
            }
        }
    }

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        AppCompatEditText toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText(getIntent().getStringExtra("categoryTypeName"));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_black);
        mToolbar.setNavigationOnClickListener(view -> finish());
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Utilities.isNetworkAvailable(context)) {
                getSearchList.execute(session.getLocation().get(ApplicationConstants.KEY_LOCATION_INFO));
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSearchList.cancel(true);
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }
}
