package in.oriange.joinstagharse.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.adapters.ProductCategoryParentAdapter;
import in.oriange.joinstagharse.models.ProductCategoriesModel;
import in.oriange.joinstagharse.utilities.APICall;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.UserSessionManager;
import in.oriange.joinstagharse.utilities.Utilities;

import static in.oriange.joinstagharse.utilities.Utilities.changeStatusBar;

public class ProductCategoriesActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_product_categories)
    RecyclerView rvProductCategories;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.progressBar)
    SpinKitView progressBar;
    @BindView(R.id.ll_nopreview)
    LinearLayout llNopreview;
    @BindView(R.id.btn_add)
    FloatingActionButton btnAdd;

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private String userId, businessCategoryId, businessCategoryName;

    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_categories);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = ProductCategoriesActivity.this;
        session = new UserSessionManager(context);
        changeStatusBar(context, getWindow());
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        rvProductCategories.setLayoutManager(new LinearLayoutManager(context));
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
        businessCategoryId = getIntent().getStringExtra("businessCategoryId");
        businessCategoryName = getIntent().getStringExtra("businessCategoryName");

        apiCall();

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("ProductCategoriesActivity");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void apiCall() {
        if (Utilities.isNetworkAvailable(context)) {
            new GetProductCategories().execute(userId, businessCategoryId);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }
    }

    private void setEventHandler() {
        btnAdd.setOnClickListener(v -> openAddCategoryDialog());

        swipeRefreshLayout.setOnRefreshListener(() -> {
            apiCall();
        });
    }

    private void openAddCategoryDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_layout_category, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialogBuilder.setView(promptView);

        final TextView tv_title = promptView.findViewById(R.id.tv_title);
        final EditText edt_category_name = promptView.findViewById(R.id.edt_category_name);
        final ImageButton ib_close = promptView.findViewById(R.id.ib_close);
        final MaterialButton btn_save = promptView.findViewById(R.id.btn_save);

        final AlertDialog alertD = alertDialogBuilder.create();

        tv_title.setText("Add Product Category");

        btn_save.setOnClickListener(v -> {

            if (edt_category_name.getText().toString().trim().isEmpty()) {
                edt_category_name.setError("Please enter name");
                edt_category_name.requestFocus();
                return;
            }

            if (Utilities.isNetworkAvailable(context)) {
                alertD.dismiss();
                new AddCategory().execute(
                        edt_category_name.getText().toString().trim(),
                        businessCategoryId,
                        "0",
                        "0",
                        userId
                );
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }
        });

        ib_close.setOnClickListener(v -> alertD.dismiss());

        alertD.show();
    }

    private class GetProductCategories extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            llNopreview.setVisibility(View.GONE);
            rvProductCategories.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getAllProductCategory");
            obj.addProperty("user_id", params[0]);
            obj.addProperty("category_id", params[1]);
            res = APICall.JSONAPICall(ApplicationConstants.PRODUCTCATEGORYAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    ProductCategoriesModel pojoDetails = new Gson().fromJson(result, ProductCategoriesModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        List<ProductCategoriesModel.ResultBean> productCategoryList = pojoDetails.getResult();
                        if (productCategoryList.size() > 0) {
                            rvProductCategories.setVisibility(View.VISIBLE);
                            llNopreview.setVisibility(View.GONE);
                            rvProductCategories.setAdapter(new ProductCategoryParentAdapter(context, productCategoryList));
                        } else {
                            llNopreview.setVisibility(View.VISIBLE);
                            rvProductCategories.setVisibility(View.GONE);
                        }
                    } else {
                        llNopreview.setVisibility(View.VISIBLE);
                        rvProductCategories.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                llNopreview.setVisibility(View.VISIBLE);
                rvProductCategories.setVisibility(View.GONE);
            }
        }
    }

    private class AddCategory extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "addProductCategory");
            obj.addProperty("name", params[0]);
            obj.addProperty("category_id", params[1]);
            obj.addProperty("level", params[2]);
            obj.addProperty("parent_id", params[3]);
            obj.addProperty("user_id", params[4]);
            res = APICall.JSONAPICall(ApplicationConstants.PRODUCTCATEGORYAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("ProductCategoriesActivity"));
                        Utilities.showMessage(message, context, 1);
                    } else {
                        Utilities.showMessage(message, context, 3);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_black);
        mToolbar.setNavigationOnClickListener(view -> finish());
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            apiCall();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }
}