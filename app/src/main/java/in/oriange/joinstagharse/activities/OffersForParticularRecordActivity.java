package in.oriange.joinstagharse.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.adapters.OffersAdapter;
import in.oriange.joinstagharse.models.MyOffersListModel;
import in.oriange.joinstagharse.utilities.APICall;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.ParamsPojo;
import in.oriange.joinstagharse.utilities.UserSessionManager;
import in.oriange.joinstagharse.utilities.Utilities;

import static android.view.View.GONE;
import static in.oriange.joinstagharse.utilities.Utilities.changeStatusBar;

public class OffersForParticularRecordActivity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private RecyclerView rv_myoffers;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SpinKitView progressBar;
    private LinearLayout ll_nopreview;
    private FloatingActionButton btn_add;
    private String userId, CALLTYPE, APITYPE, recordId, categoryType, categoryId;   //CALLTYPE  1 == My Businesss Offer and My Offers  2 == Search Business offer

    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offersfor_particularrecord);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {

        context = OffersForParticularRecordActivity.this;
        changeStatusBar(context, getWindow());
        session = new UserSessionManager(context);

        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        ll_nopreview = findViewById(R.id.ll_nopreview);
        btn_add = findViewById(R.id.btn_add);

        rv_myoffers = findViewById(R.id.rv_myoffers);
        rv_myoffers.setLayoutManager(new LinearLayoutManager(context));
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
        CALLTYPE = getIntent().getStringExtra("CALLTYPE");
        recordId = getIntent().getStringExtra("recordId");
        categoryType = getIntent().getStringExtra("categoryType");
        categoryId = getIntent().getStringExtra("categoryId");
        APITYPE = getIntent().getStringExtra("APITYPE");

        if (Utilities.isNetworkAvailable(context)) {
            new GetAddedOffers().execute();
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }

        if (CALLTYPE.equals("2"))
            btn_add.setVisibility(GONE);

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("OffersForParticularRecordActivity");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void setEventHandler() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (Utilities.isNetworkAvailable(context)) {
                new GetAddedOffers().execute();
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        btn_add.setOnClickListener(v -> {
            startActivity(new Intent(context, AddOffersActivity.class)
                    .putExtra("recordId", recordId)
                    .putExtra("categoryTypeId", "1")
                    .putExtra("categoryTypeName", "business")
                    .putExtra("categoryId", categoryId));
        });
    }

    private class GetAddedOffers extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            ll_nopreview.setVisibility(GONE);
            rv_myoffers.setVisibility(GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String res;
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", APITYPE));
            param.add(new ParamsPojo("record_id", recordId));
            param.add(new ParamsPojo("category_type_id", categoryType));
            res = APICall.FORMDATAAPICall(ApplicationConstants.OFFERSAPI, param);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(GONE);
            rv_myoffers.setVisibility(View.VISIBLE);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    List<MyOffersListModel.ResultBean> myOffersList = new ArrayList<>();
                    MyOffersListModel pojoDetails = new Gson().fromJson(result, MyOffersListModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        myOffersList = pojoDetails.getResult();

                        if (myOffersList.size() > 0) {
                            rv_myoffers.setVisibility(View.VISIBLE);
                            ll_nopreview.setVisibility(GONE);
                            rv_myoffers.setAdapter(new OffersAdapter(context, myOffersList, CALLTYPE));
                        } else {
                            ll_nopreview.setVisibility(View.VISIBLE);
                            rv_myoffers.setVisibility(GONE);
                        }

                    } else {
                        ll_nopreview.setVisibility(View.VISIBLE);
                        rv_myoffers.setVisibility(GONE);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ll_nopreview.setVisibility(View.VISIBLE);
                rv_myoffers.setVisibility(GONE);
            }
        }
    }

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_black);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Utilities.isNetworkAvailable(context)) {
                new GetAddedOffers().execute();
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }
}
