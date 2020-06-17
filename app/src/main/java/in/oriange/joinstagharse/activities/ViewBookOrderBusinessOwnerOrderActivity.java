package in.oriange.joinstagharse.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.adapters.BookOrderOrderImagesAdapter;
import in.oriange.joinstagharse.adapters.BookOrderProductsAdapter;
import in.oriange.joinstagharse.adapters.BookOrderStatusAdapter;
import in.oriange.joinstagharse.models.BookOrderBusinessOwnerModel;
import in.oriange.joinstagharse.utilities.APICall;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.UserSessionManager;
import in.oriange.joinstagharse.utilities.Utilities;

import static android.Manifest.permission.CALL_PHONE;
import static in.oriange.joinstagharse.utilities.ApplicationConstants.IMAGE_LINK;
import static in.oriange.joinstagharse.utilities.Utilities.changeStatusBar;
import static in.oriange.joinstagharse.utilities.Utilities.getCommaSeparatedNumber;
import static in.oriange.joinstagharse.utilities.Utilities.provideCallPremission;

public class ViewBookOrderBusinessOwnerOrderActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_title)
    AppCompatEditText toolbarTitle;
    @BindView(R.id.btn_action)
    MaterialButton btnAction;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_order_id)
    TextView tvOrderId;
    @BindView(R.id.tv_order_by)
    TextView tvOrderBy;
    @BindView(R.id.tv_purchase_order_type)
    TextView tvPurchaseOrderType;
    @BindView(R.id.tv_customer_name)
    TextView tvCustomerName;
    @BindView(R.id.tv_customer_mobile)
    TextView tvCustomerMobile;
    @BindView(R.id.ib_customer_mobile_call)
    ImageButton ibCustomerMobileCall;
    @BindView(R.id.tv_delivery_type)
    TextView tvDeliveryType;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_text)
    TextView tvText;
    @BindView(R.id.cv_text)
    CardView cvText;
    @BindView(R.id.rv_products)
    RecyclerView rvProducts;
    @BindView(R.id.cv_products)
    CardView cvProducts;
    @BindView(R.id.rv_images)
    RecyclerView rvImages;
    @BindView(R.id.tv_more_order_images)
    TextView tvMoreOrderImages;
    @BindView(R.id.cv_images)
    CardView cvImages;
    @BindView(R.id.rv_status)
    RecyclerView rvStatus;
    @BindView(R.id.cv_status)
    CardView cvStatus;
    @BindView(R.id.btn_reject)
    Button btnReject;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.ib_chat)
    ImageButton ibChat;
    @BindView(R.id.tv_unread_count)
    TextView tvUnreadCount;
    @BindView(R.id.fl_cart)
    FrameLayout flCart;
    @BindView(R.id.tv_delivery_type_status)
    TextView tvDeliveryTypeStatus;
    @BindView(R.id.ib_address)
    ImageButton ibAddress;
    @BindView(R.id.ll_address)
    LinearLayout llAddress;
    @BindView(R.id.tv_received_for)
    TextView tvReceivedFor;

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private String userId, latitude, longitude, callType;   // callType 1 = All Received Orders 2 = Business-wise Received Orders
    private BookOrderBusinessOwnerModel.ResultBean orderDetails;
    private boolean isOrderImagesExpanded = true;
    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_book_order_business_owner_order);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = ViewBookOrderBusinessOwnerOrderActivity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);
        changeStatusBar(context, getWindow());
        rvImages.setLayoutManager(new GridLayoutManager(context, 3));
        rvProducts.setLayoutManager(new LinearLayoutManager(context));
        rvStatus.setLayoutManager(new LinearLayoutManager(context));

        orderDetails = (BookOrderBusinessOwnerModel.ResultBean) getIntent().getSerializableExtra("orderDetails");

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("ViewBookOrderBusinessOwnerOrderActivity");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);

        callType = getIntent().getStringExtra("callType");
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
        tvOrderId.setText("Order ID # " + orderDetails.getOrder_id());
        tvReceivedFor.setText("Received For - " + orderDetails.getOwner_business_code() + " - " + orderDetails.getOwner_business_name());

        switch (orderDetails.getOrder_type()) {
            case "1": {
                tvOrderBy.setText("Order by - Product");
                cvText.setVisibility(View.GONE);
                cvImages.setVisibility(View.GONE);
                cvProducts.setVisibility(View.VISIBLE);

                List<BookOrderBusinessOwnerModel.ResultBean.ProductDetailsBean> productsList = new ArrayList<>();

                int price = 0;
                for (BookOrderBusinessOwnerModel.ResultBean.ProductDetailsBean productDetails : orderDetails.getProduct_details()) {
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
                    if (orderDetails.getStatus_details().get(orderDetails.getStatus_details().size() - 1).getStatus().equals("1"))
                        price = price + (Integer.parseInt(productDetails.getCurrent_amount())
                                * Integer.parseInt(productDetails.getQuantity()));
                    else
                        price = price + (Integer.parseInt(productDetails.getAmount())
                                * Integer.parseInt(productDetails.getQuantity()));
                }

                rvProducts.setAdapter(new BookOrderProductsAdapter(context, productsList));
                tvPrice.setText("₹ " + getCommaSeparatedNumber(price));
            }
            break;
            case "2": {
                tvOrderBy.setText("Order by - Image");
                cvText.setVisibility(View.GONE);
                cvImages.setVisibility(View.VISIBLE);
                cvProducts.setVisibility(View.GONE);
                tvPrice.setVisibility(View.GONE);
            }
            break;
            case "3": {
                tvOrderBy.setText("Order by - Text");
                cvText.setVisibility(View.VISIBLE);
                cvImages.setVisibility(View.GONE);
                cvProducts.setVisibility(View.GONE);
                tvPrice.setVisibility(View.GONE);

                tvText.setText(orderDetails.getOrder_text());
            }
            break;
        }

        switch (orderDetails.getPurchase_order_type()) {      //purchase_order_type = 'individual' - 1, 'business' -2
            case "1": {
                tvPurchaseOrderType.setText("This order is for individual");
                tvCustomerName.setText("Orderer - " + orderDetails.getCustomer_name());
            }
            break;
            case "2": {
                tvPurchaseOrderType.setText("This order is for business");
                tvCustomerName.setText("Orderer - " + orderDetails.getCustomer_business_code() + " - " + orderDetails.getCustomer_business_name());
            }
            break;
        }

        tvOrderBy.setText(orderDetails.getOrderTypePurchaseType());
        tvDeliveryTypeStatus.setText(orderDetails.getDeliveryAndStatus());
        tvCustomerMobile.setText(orderDetails.getCustomer_country_code() + orderDetails.getCustomer_mobile());

        if (orderDetails.getDelivery_option().equals("store_pickup")) {
            tvDeliveryType.setText("Store Pickup");

            if (!orderDetails.getOwner_address().equals(""))
                tvAddress.setText("Pick up address - " + orderDetails.getOwner_address());
            else
                llAddress.setVisibility(View.GONE);

            latitude = orderDetails.getOwner_business_latitude();
            longitude = orderDetails.getOwner_business_longitude();

        } else if (orderDetails.getDelivery_option().equals("home_delivery")) {
            tvDeliveryType.setText("Home Delivery");

            if (!orderDetails.getUser_address_line_one().equals(""))
                tvAddress.setText("Delivery address - " + orderDetails.getUser_address_line_one());
            else
                tvAddress.setVisibility(View.GONE);

            latitude = orderDetails.getUser_address_latitude();
            longitude = orderDetails.getUser_address_longitude();

        }

        if (latitude.equals("") || longitude.equals("") || latitude.equals("0") || longitude.equals("0"))
            ibAddress.setVisibility(View.GONE);

        if (orderDetails.getVendor_unread_msg_count().equals("0")) {
            tvUnreadCount.setVisibility(View.GONE);
        } else {
            tvUnreadCount.setVisibility(View.VISIBLE);
            tvUnreadCount.setText(orderDetails.getVendor_unread_msg_count());
        }

        ArrayList<String> orderImagesList = new ArrayList<>();

        for (int i = 0; i < orderDetails.getOrder_images().size(); i++)
            orderImagesList.add(IMAGE_LINK + "orders/" + orderDetails.getOrder_images().get(i));

        if (orderImagesList.size() != 0) {
            cvImages.setVisibility(View.VISIBLE);
            rvImages.setAdapter(new BookOrderOrderImagesAdapter(context, orderImagesList));
        }

        if (orderImagesList.size() > 3) {
            tvMoreOrderImages.setVisibility(View.VISIBLE);
        } else {
            tvMoreOrderImages.setVisibility(View.GONE);
        }

        rvStatus.setAdapter(new BookOrderStatusAdapter(context, orderDetails.getStatus_details(), "1", orderDetails.getUpdated_by()));

        switch (orderDetails.getStatus_details().get(orderDetails.getStatus_details().size() - 1).getStatus()) {
            //  status = 'IN CART' - 1,'PLACED'-2,'ACCEPTED'-3,'Ready to Deliver'-4,'DELIVERED'-5,'BILLED'-6,'CANCEL'-7
            case "1":
                btnAction.setVisibility(View.GONE);
                btnReject.setVisibility(View.GONE);
                break;
            case "2":
                btnAction.setText("Accept");
                break;
            case "3":
                btnAction.setText("Ready to Deliver");
                break;
            case "4":
                btnAction.setText("Delivered");
                break;
            case "5":
                btnAction.setText("Billed");
                btnReject.setVisibility(View.GONE);
                break;
            case "6":
                btnAction.setVisibility(View.GONE);
                btnReject.setVisibility(View.GONE);
                break;
            case "7":
                btnAction.setVisibility(View.GONE);
                btnReject.setVisibility(View.GONE);
                break;
        }
    }

    private void setEventHandler() {
        ibChat.setOnClickListener(v -> context.startActivity(new Intent(context, ChatActivity.class)
                .putExtra("orderId", orderDetails.getId())
                .putExtra("name", orderDetails.getCustomer_name())
                .putExtra("sendTo", orderDetails.getCustomer_id())));

        ibAddress.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=&daddr=" + latitude + "," + longitude));
            startActivity(intent);
        });

        btnAction.setOnClickListener(v -> {
            String status = "";

            switch (orderDetails.getStatus_details().get(orderDetails.getStatus_details().size() - 1).getStatus()) {
                //  status = 'IN CART' - 1,'PLACED'-2,'ACCEPTED'-3,'Ready to Deliver'-4,'DELIVERED'-5,'BILLED'-6,'CANCEL'-7
//                case "1":
//                case "6":
//                case "7":
//                    btnAction.setVisibility(View.GONE);
//                    btnReject.setVisibility(View.GONE);
//                    break;
                case "2":
                    status = "3";
                    break;
                case "3":
                    status = "4";
                    break;
                case "4":
                    status = "5";
                    break;
                case "5":
                    status = "6";
//                    btnReject.setVisibility(View.GONE);
                    break;
            }
            if (Utilities.isNetworkAvailable(context)) {
                new ChangeOrderStatus().execute(status);
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }
        });

        btnReject.setOnClickListener(v -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
            alertDialogBuilder.setTitle("Alert");
            alertDialogBuilder.setMessage("Are you sure you want to cancel this order?");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Yes", (dialog, which) -> {
                if (Utilities.isNetworkAvailable(context)) {
                    new ChangeOrderStatus().execute("7");
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }
            });
            alertDialogBuilder.setNegativeButton("No", (dialog, which) -> {

            });
            alertDialogBuilder.create().show();
        });

        ibCustomerMobileCall.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(context, CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                provideCallPremission(context);
            } else {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setMessage("Are you sure you want to make a call?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.icon_call);
                builder.setCancelable(false);
                builder.setPositiveButton("YES", (dialog, id) ->
                        context.startActivity(new Intent(Intent.ACTION_CALL,
                                Uri.parse("tel:" + tvCustomerMobile.getText().toString().trim())))
                );
                builder.setNegativeButton("NO", (dialog, which) ->
                        dialog.dismiss()
                );
                androidx.appcompat.app.AlertDialog alertD = builder.create();
                alertD.show();
            }
        });

        tvMoreOrderImages.setOnClickListener(v -> {
            if (!isOrderImagesExpanded) {
                isOrderImagesExpanded = true;
                tvMoreOrderImages.setText("View More");
                rvImages.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            } else {
                isOrderImagesExpanded = false;
                tvMoreOrderImages.setText("View Less");
                rvImages.setLayoutManager(new GridLayoutManager(context, 3));
            }
        });
    }

    private class ChangeOrderStatus extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "changeOrderStatus");
            obj.addProperty("id", orderDetails.getId());
            obj.addProperty("status", params[0]);    //status = 'CANCEL'-7
            obj.addProperty("user_id", userId);
            res = APICall.JSONAPICall(ApplicationConstants.BOOKORDERAPI, obj.toString());
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

                        if (callType.equals("1")) {
                            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("ReceivedOrdersFragment"));
                        } else if (callType.equals("2")) {
                            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("BookOrderBusinessOwnerOrdersActivity"));
                        }


                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Order status changed successfully");
                        alertDialogBuilder.setCancelable(false);
                        final AlertDialog alertD = alertDialogBuilder.create();

                        btn_ok.setOnClickListener(v -> alertD.dismiss());

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
        toolbar.setNavigationIcon(R.drawable.icon_backarrow_black);
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            orderDetails = (BookOrderBusinessOwnerModel.ResultBean) intent.getSerializableExtra("orderDetails");
            setDefault();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }
}
