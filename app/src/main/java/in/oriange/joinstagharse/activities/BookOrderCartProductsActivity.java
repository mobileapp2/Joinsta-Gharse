package in.oriange.joinstagharse.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.models.BookOrderGetMyOrdersModel;
import in.oriange.joinstagharse.utilities.APICall;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.UserSessionManager;
import in.oriange.joinstagharse.utilities.Utilities;

import static in.oriange.joinstagharse.utilities.ApplicationConstants.IMAGE_LINK;
import static in.oriange.joinstagharse.utilities.Utilities.changeStatusBar;
import static in.oriange.joinstagharse.utilities.Utilities.getCommaSeparatedNumber;

public class BookOrderCartProductsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_orders)
    RecyclerView rvOrders;
    @BindView(R.id.ll_nopreview)
    LinearLayout llNopreview;
    @BindView(R.id.progressBar)
    SpinKitView progressBar;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;

    private List<BookOrderGetMyOrdersModel.ResultBean> ordersList;
    private LocalBroadcastManager localBroadcastManager;
    private BookOrderListAdapter bookOrderListAdapter;
    private String userId, particularBusinessId = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_order_cart_products);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = BookOrderCartProductsActivity.this;
        session = new UserSessionManager(context);
        changeStatusBar(context, getWindow());
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);
        pd.setMessage("Please wait ...");
        pd.setCancelable(false);

        bookOrderListAdapter = new BookOrderListAdapter();
        rvOrders.setLayoutManager(new LinearLayoutManager(context));

        ordersList = new ArrayList<>();
        rvOrders.setAdapter(bookOrderListAdapter);
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
        particularBusinessId = getIntent().getStringExtra("particularBusinessId");

        if (Utilities.isNetworkAvailable(context)) {
            new GetOrders().execute();
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("BookOrderCartProductsActivity");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void setEventHandler() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetOrders().execute();
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }
            }
        });
    }

    private class BookOrderListAdapter extends RecyclerView.Adapter<BookOrderListAdapter.MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_book_order_cart, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
            int position = holder.getAdapterPosition();
            BookOrderGetMyOrdersModel.ResultBean orderDetails = ordersList.get(position);

            holder.tv_order_for.setText(orderDetails.getOwner_business_code() + " - " + orderDetails.getOwner_business_name());
            holder.rv_products.setLayoutManager(new LinearLayoutManager(context));

            holder.rv_products.setAdapter(new BookOrderProductsListAdapter(orderDetails.getProduct_details()));

            holder.btn_save.setOnClickListener(v -> {
                startActivity(new Intent(context, BookOrderSelectDeliveryTypeActivity.class)
                        .putExtra("businessOwnerId", orderDetails.getOwner_business_id())
                        .putExtra("businessOwnerAddress", orderDetails.getOwner_address())
                        .putExtra("businessOwnerCode", orderDetails.getOwner_business_code())
                        .putExtra("businessOwnerName", orderDetails.getOwner_business_name())
                        .putExtra("isHomeDeliveryAvailable", orderDetails.getIs_home_delivery_available())
                        .putExtra("isPickUpAvailable", orderDetails.getIs_pick_up_available())
                        .putExtra("storePickUpInstructions", orderDetails.getOwner_store_pickup_instructions())
                        .putExtra("homeDeliveryInstructions", orderDetails.getOwner_home_delivery_instructions())
                        .putExtra("orderType", "1")
                        .putExtra("orderText", "")
                        .putExtra("purchaseType", "")
                        .putExtra("deliveryType", "")
                        .putExtra("userAddressId", "")
                        .putExtra("userBusinessId", "")
                        .putExtra("orderDetails", orderDetails)
                        .putExtra("orderImageArray", new JsonArray().toString()));
            });

            holder.btn_reject.setOnClickListener(v -> {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                alertDialogBuilder.setTitle("Alert");
                alertDialogBuilder.setMessage("Are you sure you want to cancel this order?");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton("Yes", (dialog, which) -> {
                    if (Utilities.isNetworkAvailable(context)) {
                        new ChangeOrderStatus().execute(orderDetails.getId(), "7");
                    } else {
                        Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                    }
                });
                alertDialogBuilder.setNegativeButton("No", (dialog, which) -> {

                });
                alertDialogBuilder.create().show();
            });

        }

        @Override
        public int getItemCount() {
            return ordersList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView tv_order_for;
            private RecyclerView rv_products;
            private MaterialButton btn_save, btn_reject;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_order_for = itemView.findViewById(R.id.tv_order_for);
                rv_products = itemView.findViewById(R.id.rv_products);
                btn_save = itemView.findViewById(R.id.btn_save);
                btn_reject = itemView.findViewById(R.id.btn_reject);
            }
        }

        void updateRv() {
            notifyDataSetChanged();
        }
    }

    private class BookOrderProductsListAdapter extends RecyclerView.Adapter<BookOrderProductsListAdapter.MyViewHolder> {

        List<BookOrderGetMyOrdersModel.ResultBean.ProductDetailsBean> productsList;

        private BookOrderProductsListAdapter(List<BookOrderGetMyOrdersModel.ResultBean.ProductDetailsBean> productsList) {
            this.productsList = productsList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_book_order_cart_products, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
            int position = holder.getAdapterPosition();
            final BookOrderGetMyOrdersModel.ResultBean.ProductDetailsBean productDetails = productsList.get(position);
            final int[] quantity = {Integer.parseInt(productDetails.getQuantity())};

            BookOrderGetMyOrdersModel.ResultBean businessOwnerOrderDetails = null;
            for (BookOrderGetMyOrdersModel.ResultBean resultBean : ordersList)
                if (resultBean.getId().equals(productDetails.getOrder_details_id()))
                    businessOwnerOrderDetails = resultBean;
            BookOrderGetMyOrdersModel.ResultBean finalBusinessOwnerOrderDetails = businessOwnerOrderDetails;

            if (productDetails.getProduct_images().size() != 0) {
                Picasso.with(context)
                        .load(IMAGE_LINK + "product/" + productDetails.getProduct_images().get(0))
                        .into(holder.imv_productimage, new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.imv_image_not_available.setVisibility(View.GONE);
                                holder.imv_productimage.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError() {
                                holder.imv_image_not_available.setVisibility(View.VISIBLE);
                                holder.imv_productimage.setVisibility(View.GONE);
                            }
                        });

            } else {
                holder.imv_image_not_available.setVisibility(View.VISIBLE);
                holder.imv_productimage.setVisibility(View.GONE);
            }

            holder.tv_product_name.setText(productDetails.getName());
            holder.tv_product_info.setText(productDetails.getDescription());

//            if (holder.tv_product_info.getText().toString().trim().equals(""))
            holder.tv_product_info.setVisibility(View.GONE);

            int sellingPrice = (int) Float.parseFloat(productDetails.getCurrent_amount());

            if (sellingPrice != 0) {
                holder.tv_no_price_available.setVisibility(View.GONE);
                holder.tv_total_product_price.setText("₹" + getCommaSeparatedNumber(sellingPrice * quantity[0]));
            } else {
                holder.tv_total_product_price.setVisibility(View.GONE);
            }

            holder.tv_quantity.setText(String.valueOf(quantity[0]));

            holder.btn_remove.setOnClickListener(v -> {
                if (quantity[0] == 1) {
                    return;
                }
                quantity[0] = quantity[0] - 1;
                holder.tv_total_product_price.setText("₹" + getCommaSeparatedNumber(sellingPrice * quantity[0]));
                holder.tv_quantity.setText(String.valueOf(quantity[0]));
                updateProductQuantity(finalBusinessOwnerOrderDetails, productDetails, quantity[0]);
            });

            holder.btn_add.setOnClickListener(v -> {
                quantity[0] = quantity[0] + 1;
                holder.tv_total_product_price.setText("₹" + getCommaSeparatedNumber(sellingPrice * quantity[0]));
                holder.tv_quantity.setText(String.valueOf(quantity[0]));
                updateProductQuantity(finalBusinessOwnerOrderDetails, productDetails, quantity[0]);
            });

//            holder.btn_addtocart.setOnClickListener(v -> {
//                updateProductQuantity(finalBusinessOwnerOrderDetails, productDetails, quantity[0]);
//            });

            holder.btn_delete.setOnClickListener(v -> {

                if (productsList.size() == 1) {
                    Utilities.showMessage("There must be atleast one product in the order", context, 2);
                    return;
                }

                deleteProduct(finalBusinessOwnerOrderDetails, productDetails.getId());
            });

            if (position == productsList.size() - 1)
                holder.view_divider.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            return productsList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private ImageView imv_productimage, imv_image_not_available;
            private TextView tv_product_name, tv_product_info, tv_total_product_price, tv_no_price_available, tv_quantity;
            private ImageButton btn_remove, btn_add, btn_delete;
            private Button btn_addtocart;
            private View view_divider;

            public MyViewHolder(@NonNull View view) {
                super(view);
                imv_productimage = view.findViewById(R.id.imv_productimage);
                imv_image_not_available = view.findViewById(R.id.imv_image_not_available);
                tv_product_name = view.findViewById(R.id.tv_product_name);
                tv_product_info = view.findViewById(R.id.tv_product_info);
                tv_total_product_price = view.findViewById(R.id.tv_total_product_price);
                tv_no_price_available = view.findViewById(R.id.tv_no_price_available);
                tv_quantity = view.findViewById(R.id.tv_quantity);
                btn_remove = view.findViewById(R.id.btn_remove);
                btn_add = view.findViewById(R.id.btn_add);
                btn_addtocart = view.findViewById(R.id.btn_addtocart);
                btn_delete = view.findViewById(R.id.btn_delete);
                view_divider = view.findViewById(R.id.view_divider);
            }
        }
    }

    private void updateProductQuantity(BookOrderGetMyOrdersModel.ResultBean businessOwnerOrderDetails,
                                       BookOrderGetMyOrdersModel.ResultBean.ProductDetailsBean selectedProduct,
                                       int quantity) {

        List<BookOrderGetMyOrdersModel.ResultBean.ProductDetailsBean> productsToBeAddedList = new ArrayList<>();

        for (BookOrderGetMyOrdersModel.ResultBean.ProductDetailsBean orderProduct : businessOwnerOrderDetails.getProduct_details()) {
            productsToBeAddedList.add(new BookOrderGetMyOrdersModel.ResultBean.ProductDetailsBean(
                    "0",
                    orderProduct.getProduct_id(),
                    businessOwnerOrderDetails.getId(),
                    orderProduct.getQuantity(),
                    orderProduct.getCurrent_amount()
            ));
        }

        boolean isProductAlreadyAddedInOrder = false;

        for (int i = 0; i < productsToBeAddedList.size(); i++) {
            if (productsToBeAddedList.get(i).getProduct_id().equals(selectedProduct.getProduct_id())) {
                isProductAlreadyAddedInOrder = true;
                productsToBeAddedList.get(i).setQuantity(String.valueOf(quantity));
                productsToBeAddedList.get(i).setAmount(selectedProduct.getCurrent_amount());
            }
        }

        if (!isProductAlreadyAddedInOrder) {
            productsToBeAddedList.add(new BookOrderGetMyOrdersModel.ResultBean.ProductDetailsBean(
                    "0",
                    selectedProduct.getId(),
                    businessOwnerOrderDetails.getId(),
                    String.valueOf(quantity),
                    selectedProduct.getCurrent_amount()
            ));
        }

        JsonArray productsDetailsArray = new JsonArray();

        for (int i = 0; i < productsToBeAddedList.size(); i++) {
            if (i <= businessOwnerOrderDetails.getProduct_details().size() - 1) {
                JsonObject productObject = new JsonObject();
                productObject.addProperty("id", businessOwnerOrderDetails.getProduct_details().get(i).getId());
                productObject.addProperty("product_id", productsToBeAddedList.get(i).getProduct_id());
                productObject.addProperty("quantity", productsToBeAddedList.get(i).getQuantity());
                productObject.addProperty("amount", productsToBeAddedList.get(i).getAmount());
                productsDetailsArray.add(productObject);
            } else {
                JsonObject productObject = new JsonObject();
                productObject.addProperty("id", "0");
                productObject.addProperty("product_id", productsToBeAddedList.get(i).getProduct_id());
                productObject.addProperty("quantity", productsToBeAddedList.get(i).getQuantity());
                productObject.addProperty("amount", productsToBeAddedList.get(i).getAmount());
                productsDetailsArray.add(productObject);
            }
        }

        updateJson(businessOwnerOrderDetails, productsDetailsArray);
    }

    private void deleteProduct(BookOrderGetMyOrdersModel.ResultBean businessOwnerOrderDetails, String id) {
        JsonArray productsDetailsArray = new JsonArray();

        List<BookOrderGetMyOrdersModel.ResultBean.ProductDetailsBean> productsList = businessOwnerOrderDetails.getProduct_details();
        List<BookOrderGetMyOrdersModel.ResultBean.ProductDetailsBean> newProductList = new ArrayList<>();

        for (BookOrderGetMyOrdersModel.ResultBean.ProductDetailsBean productDetails : productsList) {
            if (!productDetails.getId().equals(id)) {
                newProductList.add(productDetails);
            }
        }

        for (int i = 0; i < newProductList.size(); i++) {
            JsonObject productObject = new JsonObject();
            productObject.addProperty("id", newProductList.get(i).getId());
            productObject.addProperty("product_id", newProductList.get(i).getProduct_id());
            productObject.addProperty("quantity", newProductList.get(i).getQuantity());
            productObject.addProperty("amount", newProductList.get(i).getAmount());
            productsDetailsArray.add(productObject);
        }

        updateJson(businessOwnerOrderDetails, productsDetailsArray);
    }

    private void updateJson(BookOrderGetMyOrdersModel.ResultBean businessOwnerOrderDetails, JsonArray productsDetailsArray) {
        JsonObject mainObj = new JsonObject();
        JsonArray orderImageJsonArray = new JsonArray();

        mainObj.addProperty("type", "updateOrder");
        mainObj.addProperty("id", businessOwnerOrderDetails.getId());
        mainObj.addProperty("order_id", businessOwnerOrderDetails.getOrder_id());
        mainObj.addProperty("owner_business_id", businessOwnerOrderDetails.getOwner_business_id());
        mainObj.addProperty("order_type", "1");
        mainObj.addProperty("order_text", "");
        mainObj.add("product_details", productsDetailsArray);
        mainObj.addProperty("status", "1");    // status = 'IN CART'-2
        mainObj.addProperty("purchase_order_type", "1");
        mainObj.addProperty("business_id", "0");
        mainObj.addProperty("delivery_option", "home_delivery");
        mainObj.addProperty("user_address_id", "0");
        mainObj.add("order_image", orderImageJsonArray);
        mainObj.addProperty("user_id", userId);

        if (Utilities.isNetworkAvailable(context)) {
            new UpdateOrder().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }
    }

    private class GetOrders extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            llNopreview.setVisibility(View.GONE);
            rvOrders.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getAllOrders");
            obj.addProperty("user_id", userId);
            res = APICall.JSONAPICall(ApplicationConstants.BOOKORDERAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            progressBar.setVisibility(View.GONE);
            try {
                if (!result.equals("")) {
                    BookOrderGetMyOrdersModel pojoDetails = new Gson().fromJson(result, BookOrderGetMyOrdersModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        ordersList = pojoDetails.getResult();

                        if (ordersList.size() != 0) {
                            updateProductsList();
                        } else {
                            llNopreview.setVisibility(View.VISIBLE);
                            rvOrders.setVisibility(View.GONE);
                        }
                    } else {
                        llNopreview.setVisibility(View.VISIBLE);
                        rvOrders.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                llNopreview.setVisibility(View.VISIBLE);
                rvOrders.setVisibility(View.GONE);
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
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    BookOrderGetMyOrdersModel pojoDetails = new Gson().fromJson(result, BookOrderGetMyOrdersModel.class);
                    type = pojoDetails.getType();

                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("BookOrderProductsListActivityRefreshOrderList"));
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("HomeFragment"));
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("SentOrdersFragment"));

                    if (type.equalsIgnoreCase("success")) {
                        ordersList = pojoDetails.getResult();
                        updateProductsList();
                    } else {
                        Utilities.showMessage(message, context, 3);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
            obj.addProperty("id", params[0]);
            obj.addProperty("status", params[1]);    //status = 'CANCEL'-7
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

                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("HomeFragment"));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("SentOrdersFragment"));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("BookOrderProductsListActivityRefreshOrderList"));

                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Order cancelled successfully");
                        alertDialogBuilder.setCancelable(false);
                        final AlertDialog alertD = alertDialogBuilder.create();

                        btn_ok.setOnClickListener(v -> {
                            alertD.dismiss();
                            finish();
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

    private void updateProductsList() {
        List<BookOrderGetMyOrdersModel.ResultBean> filteredOrderList = new ArrayList<>();

        for (BookOrderGetMyOrdersModel.ResultBean orderDetail : ordersList)
            if (orderDetail.getStatus_details().size() == 1)
                if (orderDetail.getStatus_details().get(0).getStatus().equals("1")) {
                    filteredOrderList.add(orderDetail);
                }

        ordersList.clear();
        ordersList.addAll(filteredOrderList);

        if (!particularBusinessId.equals("0")) {
            List<BookOrderGetMyOrdersModel.ResultBean> filteredOrderListForParticularBusiness = new ArrayList<>();
            for (BookOrderGetMyOrdersModel.ResultBean orderDetail : ordersList)
                if (orderDetail.getStatus_details().size() == 1)
                    if (orderDetail.getStatus_details().get(0).getStatus().equals("1"))
                        if (orderDetail.getOwner_business_id().equals(particularBusinessId))
                            filteredOrderListForParticularBusiness.add(orderDetail);


            ordersList.clear();
            ordersList.addAll(filteredOrderListForParticularBusiness);
        }


        if (ordersList.size() != 0) {
            llNopreview.setVisibility(View.GONE);
            rvOrders.setVisibility(View.VISIBLE);
            bookOrderListAdapter.updateRv();
        } else {
            llNopreview.setVisibility(View.VISIBLE);
            rvOrders.setVisibility(View.GONE);
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
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }
}
