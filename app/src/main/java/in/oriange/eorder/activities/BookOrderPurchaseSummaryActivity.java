package in.oriange.eorder.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import in.oriange.eorder.R;
import in.oriange.eorder.adapters.BookOrderOrderImagesAdapter;
import in.oriange.eorder.adapters.BookOrderProductsAdapter;
import in.oriange.eorder.models.BookOrderBusinessOwnerModel;
import in.oriange.eorder.models.BookOrderGetMyOrdersModel;
import in.oriange.eorder.models.GetBusinessModel;
import in.oriange.eorder.utilities.APICall;
import in.oriange.eorder.utilities.ApplicationConstants;
import in.oriange.eorder.utilities.UserSessionManager;
import in.oriange.eorder.utilities.Utilities;

import static in.oriange.eorder.utilities.ApplicationConstants.IMAGE_LINK;

public class BookOrderPurchaseSummaryActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_order_text)
    TextView tvOrderText;
    @BindView(R.id.cv_text)
    CardView cvText;
    @BindView(R.id.imv_user)
    CircleImageView imvUser;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_mobile)
    TextView tvMobile;
    @BindView(R.id.ll_individual_details)
    LinearLayout llIndividualDetails;
    @BindView(R.id.tv_business_name)
    TextView tvBusinessName;
    @BindView(R.id.tv_business_type_subtype)
    TextView tvBusinessTypeSubtype;
    @BindView(R.id.ll_business)
    LinearLayout llBusiness;
    @BindView(R.id.cv_purchase_type)
    CardView cvPurchaseType;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.rv_order_images)
    RecyclerView rvOrderImages;
    @BindView(R.id.cv_images)
    CardView cvImages;
    @BindView(R.id.rv_products)
    RecyclerView rvProducts;
    @BindView(R.id.cv_products)
    CardView cvProducts;
    @BindView(R.id.tv_more_order_images)
    TextView tvMoreOrderImages;

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private String userId, businessOwnerId = "0", purchaseOrderType, orderType, orderText = "", status, customerBusinessId = "0", orderImageArray = "";
    private GetBusinessModel.ResultBean businessDetails;
    private BookOrderGetMyOrdersModel.ResultBean orderDetails;
    private JsonArray orderImageJsonArray;
    private boolean isOrderImagesExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_order_purchase_summary);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = BookOrderPurchaseSummaryActivity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        rvOrderImages.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        rvProducts.setLayoutManager(new LinearLayoutManager(context));
    }

    private void getSessionDetails() {
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);

            userId = json.getString("userid");
            String imageUrl = json.getString("image_url");
            String name = json.getString("first_name");
            String country_code = json.getString("country_code");
            String mobile = json.getString("mobile");

            if (!imageUrl.equals("")) {
                String url = IMAGE_LINK + userId + "/" + imageUrl;
                Picasso.with(context)
                        .load(url)
                        .placeholder(R.drawable.icon_userphoto)
                        .into(imvUser);
            }

            tvName.setText(name);
            tvMobile.setText("+" + country_code + mobile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDefault() {
        businessOwnerId = getIntent().getStringExtra("businessOwnerId");
        purchaseOrderType = getIntent().getStringExtra("purchaseOrderType");     // purchase_order_type = 'individual' - 1, 'business' -2
        orderType = getIntent().getStringExtra("orderType");                     //order_type = 'order_with_product' - 1, 'order_by_image' - 2,'order_by_text' - 3
        orderText = getIntent().getStringExtra("orderText");
        orderImageArray = getIntent().getStringExtra("orderImageArray");
        businessDetails = (GetBusinessModel.ResultBean) getIntent().getSerializableExtra("businessDetails");

        orderDetails = (BookOrderGetMyOrdersModel.ResultBean) getIntent().getSerializableExtra("orderDetails");

        if (orderDetails == null) {
            orderDetails = new BookOrderGetMyOrdersModel.ResultBean();
        }

        if (purchaseOrderType.equals("1")) {
            llIndividualDetails.setVisibility(View.VISIBLE);
            llBusiness.setVisibility(View.GONE);
        } else if (purchaseOrderType.equals("2")) {
            llIndividualDetails.setVisibility(View.GONE);
            llBusiness.setVisibility(View.VISIBLE);

            customerBusinessId = businessDetails.getId();
            tvBusinessName.setText(businessDetails.getBusiness_code() + " - " + businessDetails.getBusiness_name());
            if (!businessDetails.getTypeSubTypeName().isEmpty())
                tvBusinessTypeSubtype.setText(businessDetails.getTypeSubTypeName());
            else
                tvBusinessTypeSubtype.setText(businessDetails.getType_description());
        }

        switch (orderType) {
            case "1":
                cvText.setVisibility(View.GONE);
                cvImages.setVisibility(View.GONE);
                cvProducts.setVisibility(View.VISIBLE);

                List<BookOrderBusinessOwnerModel.ResultBean.ProductDetailsBean> productsList = new ArrayList<>();

                for (BookOrderGetMyOrdersModel.ResultBean.ProductDetailsBean productDetails : orderDetails.getProduct_details()) {
                    productsList.add(new BookOrderBusinessOwnerModel.ResultBean.ProductDetailsBean(
                            productDetails.getId(),
                            productDetails.getOrder_details_id(),
                            productDetails.getProduct_id(),
                            productDetails.getQuantity(),
                            productDetails.getAmount(),
                            productDetails.getCurrent_amount(),
                            productDetails.getName(),
                            productDetails.getDescription(),
                            productDetails.getBusiness_id(),
                            productDetails.getUnit_of_measure(),
                            productDetails.getProduct_images()
                    ));
                }
                rvProducts.setAdapter(new BookOrderProductsAdapter(context, productsList));

                break;
            case "2":
                cvText.setVisibility(View.GONE);
                cvImages.setVisibility(View.VISIBLE);
                cvProducts.setVisibility(View.GONE);

                break;
            case "3":
                cvText.setVisibility(View.VISIBLE);
                cvImages.setVisibility(View.GONE);
                cvProducts.setVisibility(View.GONE);

                tvOrderText.setText(orderText);
                break;
        }

        orderImageJsonArray = (JsonArray) new JsonParser().parse(orderImageArray);
        ArrayList<String> orderImagesList = new ArrayList<>();

        for (int i = 0; i < orderImageJsonArray.size(); i++)
            orderImagesList.add(IMAGE_LINK + "orders/" + orderImageJsonArray.get(i).getAsString());

        if (orderImagesList.size() != 0) {
            cvImages.setVisibility(View.VISIBLE);
            rvOrderImages.setAdapter(new BookOrderOrderImagesAdapter(context, orderImagesList));
        } else {
            cvImages.setVisibility(View.GONE);
        }

        if (orderImagesList.size() > 3) {
            tvMoreOrderImages.setVisibility(View.VISIBLE);
        } else {
            tvMoreOrderImages.setVisibility(View.GONE);
        }
    }

    private void setEventHandler() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orderType.equals("2") || orderType.equals("3")) {
                    addOrder();
                } else if (orderType.equals("1")) {
                    updateOrder(orderImageJsonArray);
                }
            }
        });

        tvMoreOrderImages.setOnClickListener(v -> {
            if (!isOrderImagesExpanded) {
                isOrderImagesExpanded = true;
                tvMoreOrderImages.setText("View More");
                rvOrderImages.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            } else {
                isOrderImagesExpanded = false;
                tvMoreOrderImages.setText("View Less");
                rvOrderImages.setLayoutManager(new GridLayoutManager(context, 3));
            }
        });
    }

    private void addOrder() {
        JsonObject mainObj = new JsonObject();

        JsonArray productsDetailsArray = new JsonArray();
        JsonArray orderImageJsonArray = new JsonArray();

        if (!orderImageArray.equals("")) {
            orderImageJsonArray = (JsonArray) new JsonParser().parse(orderImageArray);
        }

        mainObj.addProperty("type", "addOrder");
        mainObj.addProperty("owner_business_id", businessOwnerId);
        mainObj.addProperty("order_type", orderType);
        mainObj.addProperty("order_text", orderText);
        mainObj.add("product_details", productsDetailsArray);
        mainObj.addProperty("status", "2");    // status = 'PLACED'-2
        mainObj.addProperty("purchase_order_type", purchaseOrderType);
        mainObj.addProperty("business_id", customerBusinessId);
        mainObj.add("order_image", orderImageJsonArray);
        mainObj.addProperty("user_id", userId);

        if (Utilities.isNetworkAvailable(context)) {
            new AddOrder().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }

    }

    private void updateOrder(JsonArray orderImageJsonArray) {
        JsonObject mainObj = new JsonObject();

        JsonArray productsDetailsArray = new JsonArray();

        for (int i = 0; i < orderDetails.getProduct_details().size(); i++) {
            JsonObject productObject = new JsonObject();
            productObject.addProperty("id", orderDetails.getProduct_details().get(i).getId());
            productObject.addProperty("product_id", orderDetails.getProduct_details().get(i).getProduct_id());
            productObject.addProperty("quantity", orderDetails.getProduct_details().get(i).getQuantity());
            productObject.addProperty("amount", orderDetails.getProduct_details().get(i).getAmount());
            productsDetailsArray.add(productObject);
        }

        mainObj.addProperty("type", "updateOrder");
        mainObj.addProperty("id", orderDetails.getId());
        mainObj.addProperty("order_id", orderDetails.getOrder_id());
        mainObj.addProperty("owner_business_id", orderDetails.getOwner_business_id());
        mainObj.addProperty("order_type", "1");
        mainObj.addProperty("order_text", "");
        mainObj.add("product_details", productsDetailsArray);
        mainObj.addProperty("status", "2");    // status = 'IN CART'-2
        mainObj.addProperty("purchase_order_type", purchaseOrderType);
        mainObj.addProperty("business_id", customerBusinessId);
        mainObj.add("order_image", orderImageJsonArray);
        mainObj.addProperty("user_id", userId);

        if (Utilities.isNetworkAvailable(context)) {
            new UpdateOrder().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }
    }

    private class AddOrder extends AsyncTask<String, Void, String> {

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
            res = APICall.JSONAPICall(ApplicationConstants.BOOKORDERAPI, params[0]);
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

                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("BookOrderPurchaseTypeSelectionActivity"));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("BookOrderOrderTypeTextActivity"));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("BookOrderOrderTypeImageActivity"));

                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Your order is placed successfully, thank you");
                        alertDialogBuilder.setCancelable(false);
                        final AlertDialog alertD = alertDialogBuilder.create();

                        btn_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertD.dismiss();
                                finish();
                            }
                        });

                        alertD.show();
                    } else {
                        Utilities.showMessage(message, context, 3);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class UpdateOrder extends AsyncTask<String, Void, String> {

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
            res = APICall.JSONAPICall(ApplicationConstants.BOOKORDERAPI, params[0]);
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

                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("HomeFragment"));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("BookOrderMyOrdersActivity"));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("BookOrderCartProductsActivity"));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("BookOrderPurchaseTypeSelectionActivity"));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("BookOrderOrderTypeTextActivity"));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("BookOrderOrderTypeImageActivity"));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("BookOrderImageUploadActivity"));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("ViewBookOrderMyOrderActivity"));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("BookOrderProductsListActivity"));

                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Your order is placed successfully, thank you");
                        alertDialogBuilder.setCancelable(false);
                        final AlertDialog alertD = alertDialogBuilder.create();

                        btn_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertD.dismiss();
                                finish();
                            }
                        });

                        alertD.show();
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
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.icon_backarrow);
        toolbar.setNavigationOnClickListener(view -> finish());
    }
}
