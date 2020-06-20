package in.oriange.joinstagharse.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.models.ProductCategoriesModel;
import in.oriange.joinstagharse.utilities.APICall;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.UserSessionManager;
import in.oriange.joinstagharse.utilities.Utilities;

import static in.oriange.joinstagharse.utilities.Utilities.changeStatusBar;

public class AddProductSubCategoryActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edt_category_name)
    EditText edtCategoryName;
    @BindView(R.id.ll_sub_categories)
    LinearLayout llSubCategories;
    @BindView(R.id.btn_save)
    MaterialButton btnSave;

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private String userId;
    private ArrayList<LinearLayout> subCategoryLayoutsList;
    private ProductCategoriesModel.ResultBean productCategoryDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_sub_category);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = AddProductSubCategoryActivity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        changeStatusBar(context, getWindow());
        subCategoryLayoutsList = new ArrayList<>();
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
        productCategoryDetails = (ProductCategoriesModel.ResultBean) getIntent().getSerializableExtra("productCategoryDetails");
        addSubCategoryLayout();
    }

    private void addSubCategoryLayout() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.layout_add_sub_category, null);
        LinearLayout ll = (LinearLayout) rowView;
        subCategoryLayoutsList.add(ll);
        llSubCategories.addView(rowView, llSubCategories.getChildCount() - 1);
    }

    private void setEventHandler() {
        btnSave.setOnClickListener(v -> {
            if (((EditText) subCategoryLayoutsList.get(subCategoryLayoutsList.size() - 1).findViewById(R.id.edt_category_name)).getText().toString().trim().isEmpty()) {
                ((EditText) subCategoryLayoutsList.get(subCategoryLayoutsList.size() - 1).findViewById(R.id.edt_category_name)).setError("Please enter subcategory name");
                return;
            }

            if (Utilities.isNetworkAvailable(context)) {
                new AddCategory().execute(
                        ((EditText) subCategoryLayoutsList.get(subCategoryLayoutsList.size() - 1).findViewById(R.id.edt_category_name)).getText().toString().trim(),
                        productCategoryDetails.getBusiness_category_id(),
                        "1",
                        productCategoryDetails.getId(),
                        userId
                );
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }
        });
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
                        addSubCategoryLayout();
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
}