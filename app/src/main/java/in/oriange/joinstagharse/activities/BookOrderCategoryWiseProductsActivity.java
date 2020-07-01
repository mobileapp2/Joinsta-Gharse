package in.oriange.joinstagharse.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.models.BookOrderGetMyOrdersModel;
import in.oriange.joinstagharse.models.BookOrderProductsListModel;
import in.oriange.joinstagharse.utilities.APICall;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.UserSessionManager;
import in.oriange.joinstagharse.utilities.Utilities;

import static in.oriange.joinstagharse.utilities.ApplicationConstants.IMAGE_LINK;
import static in.oriange.joinstagharse.utilities.Utilities.changeStatusBar;
import static in.oriange.joinstagharse.utilities.Utilities.getCommaSeparatedNumber;

public class BookOrderCategoryWiseProductsActivity extends AppCompatActivity {

    @BindView(R.id.ib_back)
    ImageButton ibBack;
    @BindView(R.id.edt_search)
    EditText edtSearch;
    @BindView(R.id.ib_cart)
    ImageButton ibCart;
    @BindView(R.id.tv_cart_count)
    TextView tvCartCount;
    @BindView(R.id.fl_cart)
    FrameLayout flCart;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_products)
    RecyclerView rvProducts;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.progressBar)
    SpinKitView progressBar;
    @BindView(R.id.ll_nopreview)
    LinearLayout llNopreview;

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;

    private String userId, businessOwnerId, businessOwnerUserId, categoryId, subcategoryId, canShareProduct, businessCodeName;
    private List<BookOrderProductsListModel.ResultBean> productsList, searchedProductsList;
    private List<BookOrderGetMyOrdersModel.ResultBean> ordersList;
    private BookOrderProductsListAdapter bookOrderProductsListAdapter;

    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_order_category_wise_products);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = BookOrderCategoryWiseProductsActivity.this;
        session = new UserSessionManager(context);
        changeStatusBar(context, getWindow());

        pd = new ProgressDialog(context, R.style.CustomDialogTheme);
        pd.setMessage("Please wait ...");
        pd.setCancelable(false);

        rvProducts.setLayoutManager(new LinearLayoutManager(context));
//        rvProducts.setLayoutManager(new GridLayoutManager(context, 2));
        bookOrderProductsListAdapter = new BookOrderProductsListAdapter();

        productsList = new ArrayList<>();
        ordersList = new ArrayList<>();
        searchedProductsList = new ArrayList<>();
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
        businessOwnerId = getIntent().getStringExtra("businessOwnerId");
        businessOwnerUserId = getIntent().getStringExtra("businessOwnerUserId");
        categoryId = getIntent().getStringExtra("categoryId");
        subcategoryId = getIntent().getStringExtra("subcategoryId");
        canShareProduct = getIntent().getStringExtra("canShareProduct");
        businessCodeName = getIntent().getStringExtra("businessCodeName");

        if (Utilities.isNetworkAvailable(context)) {
            new GetAllProducts().execute(userId, businessOwnerId, categoryId, subcategoryId);
            new GetOrders().execute();
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("BookOrderCategoryWiseProductsActivity");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);

    }

    private void setEventHandler() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {

                if (query.toString().isEmpty()) {
                    searchedProductsList = productsList;
                    rvProducts.setAdapter(bookOrderProductsListAdapter);
                    return;
                }

                if (ordersList.size() == 0) {
                    searchedProductsList = productsList;
                    rvProducts.setVisibility(View.GONE);
                    return;
                }

                if (!query.toString().equals("")) {
                    ArrayList<BookOrderProductsListModel.ResultBean> searchedList = new ArrayList<>();
                    for (BookOrderProductsListModel.ResultBean orderDetails : productsList) {

                        String orderToBeSearched = orderDetails.getName().toLowerCase() +
                                orderDetails.getDescription().toLowerCase();

                        if (orderToBeSearched.contains(query.toString().toLowerCase())) {
                            searchedList.add(orderDetails);
                        }
                    }
                    searchedProductsList = searchedList;
                    rvProducts.setAdapter(bookOrderProductsListAdapter);
                } else {
                    searchedProductsList = productsList;
                    rvProducts.setAdapter(bookOrderProductsListAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ibCart.setOnClickListener(v -> {
            startActivity(new Intent(context, BookOrderCartProductsActivity.class)
                    .putExtra("particularBusinessId", "0"));
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (Utilities.isNetworkAvailable(context)) {
                new GetAllProducts().execute(userId, businessOwnerId, categoryId, subcategoryId);
                new GetOrders().execute();
            } else {
                swipeRefreshLayout.setRefreshing(false);
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }
        });
    }

    private class GetAllProducts extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            llNopreview.setVisibility(View.GONE);
            rvProducts.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getCategoriesProducts");
            obj.addProperty("user_id", params[0]);
            obj.addProperty("business_id", params[1]);
            obj.addProperty("category_id", params[2]);
            obj.addProperty("sub_category_id", params[3]);
            res = APICall.JSONAPICall(ApplicationConstants.PRODUCTSAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            rvProducts.setVisibility(View.VISIBLE);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    BookOrderProductsListModel pojoDetails = new Gson().fromJson(result, BookOrderProductsListModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        productsList = pojoDetails.getResult();

                        List<BookOrderProductsListModel.ResultBean> filterProductList = new ArrayList<>();
                        for (BookOrderProductsListModel.ResultBean resultBean : productsList)
                            if (resultBean.getIs_show_in_list().equals("1"))
                                filterProductList.add(resultBean);

                        productsList.clear();
                        searchedProductsList.clear();

                        productsList.addAll(filterProductList);
                        searchedProductsList.addAll(filterProductList);

                        if (productsList.size() != 0) {
                            rvProducts.setAdapter(bookOrderProductsListAdapter);
                        } else {
                            llNopreview.setVisibility(View.VISIBLE);
                            rvProducts.setVisibility(View.GONE);
                        }
                    } else {
                        llNopreview.setVisibility(View.VISIBLE);
                        rvProducts.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                llNopreview.setVisibility(View.VISIBLE);
                rvProducts.setVisibility(View.GONE);
            }
        }
    }

    private class GetOrders extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.show();
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
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    BookOrderGetMyOrdersModel pojoDetails = new Gson().fromJson(result, BookOrderGetMyOrdersModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        ordersList = pojoDetails.getResult();
                        showCartCount();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showCartCount() {
        int numberOfProducts = 0;

        BookOrderGetMyOrdersModel.ResultBean thisBusinessOrder = null;

        for (BookOrderGetMyOrdersModel.ResultBean orderDetail : ordersList)
            if (orderDetail.getStatus_details().size() == 1)
                if (orderDetail.getStatus_details().get(0).getStatus().equals("1")) {
                    numberOfProducts = numberOfProducts + orderDetail.getProduct_details().size();
                    if (orderDetail.getOwner_business_id().equals(businessOwnerId))
                        thisBusinessOrder = orderDetail;
                }

        if (numberOfProducts != 0) {
            flCart.setVisibility(View.VISIBLE);
            tvCartCount.setText(String.valueOf(numberOfProducts));
        } else {
            flCart.setVisibility(View.GONE);
            tvCartCount.setText("");
        }

        if (thisBusinessOrder != null)
            showAlreadyAddedStatusForProducts(thisBusinessOrder);
        else {
            for (int i = 0; i < productsList.size(); i++) {
                productsList.get(i).setAlreadyAddedInCart(false);
                productsList.get(i).setQuantity(1);
            }

            searchedProductsList = productsList;
            bookOrderProductsListAdapter.notifyDataSet();
        }
    }

    private void showAlreadyAddedStatusForProducts(BookOrderGetMyOrdersModel.ResultBean thisBusinessOrder) {
        for (int i = 0; i < productsList.size(); i++) {
            for (BookOrderGetMyOrdersModel.ResultBean.ProductDetailsBean productDetailsBean : thisBusinessOrder.getProduct_details()) {
                if (productDetailsBean.getProduct_id().equals(productsList.get(i).getId())) {
                    productsList.get(i).setAlreadyAddedInCart(true);
                    productsList.get(i).setQuantity(Integer.parseInt(productDetailsBean.getQuantity()));
                }
            }
        }
        searchedProductsList = productsList;
        bookOrderProductsListAdapter.notifyDataSet();
    }

    private class BookOrderProductsListAdapter extends RecyclerView.Adapter<BookOrderProductsListAdapter.MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_book_order_products, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
            int position = holder.getAdapterPosition();
            final BookOrderProductsListModel.ResultBean productDetails = searchedProductsList.get(position);
            final int[] quantity = {productDetails.getQuantity()};

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

            float maxRetailPrice = Float.parseFloat(productDetails.getMax_retail_price());
            float sellingPrice = Float.parseFloat(productDetails.getSelling_price());

            if (productDetails.getIn_stock().equals("1"))
                holder.tv_out_of_stock.setVisibility(View.GONE);
            else
                holder.btn_addtocart.setVisibility(View.GONE);

            if (sellingPrice != 0) {
                holder.tv_no_price_available.setVisibility(View.GONE);
                float savedAmount = maxRetailPrice - sellingPrice;
                if (savedAmount <= 0) {
                    holder.tv_selling_price.setText("₹" + getCommaSeparatedNumber((int) sellingPrice));
                    holder.tv_max_retail_price.setVisibility(View.GONE);
                    holder.tv_precentage_off.setVisibility(View.GONE);
                    holder.tv_you_save.setVisibility(View.GONE);
                } else {
                    float divide = sellingPrice / maxRetailPrice;
                    int percent = (int) (divide * 100);
                    holder.tv_selling_price.setText("₹" + getCommaSeparatedNumber((int) sellingPrice));
                    holder.tv_max_retail_price.setText(Html.fromHtml("<strike>₹" + getCommaSeparatedNumber((int) maxRetailPrice) + "</strike>"));
                    holder.tv_precentage_off.setText(100 - percent + "% off");
                    holder.tv_you_save.setText("You Save ₹" + getCommaSeparatedNumber((int) savedAmount));
                }
            } else {
                holder.ll_prices.setVisibility(View.GONE);
            }

            if (productDetails.isAlreadyAddedInCart()) {
                holder.tv_added_in_cart.setVisibility(View.VISIBLE);

                if (quantity[0] == 1) {
                    holder.tv_added_in_cart.setText(quantity[0] + " item in cart");
                } else {
                    holder.tv_added_in_cart.setText(quantity[0] + " items in cart");
                }

            } else
                holder.tv_added_in_cart.setVisibility(View.GONE);

            holder.tv_quantity.setText(String.valueOf(quantity[0]));

            holder.cv_mainlayout.setOnClickListener(v -> {
//                showProductDetailsDialog(productDetails);
                startActivity(new Intent(context, BookOrderProductDetailsActivity.class)
                        .putExtra("productDetails", productDetails)
                        .putExtra("ordersList", (Serializable) ordersList)
                        .putExtra("businessOwnerId", businessOwnerId)
                        .putExtra("canShareProduct", canShareProduct)
                        .putExtra("businessCodeName", businessCodeName));
            });

            holder.btn_remove.setOnClickListener(v -> {
                if (quantity[0] == 1) {
                    return;
                }
                quantity[0] = quantity[0] - 1;
                holder.tv_quantity.setText(String.valueOf(quantity[0]));
            });

            holder.btn_add.setOnClickListener(v -> {
                quantity[0] = quantity[0] + 1;
                holder.tv_quantity.setText(String.valueOf(quantity[0]));
            });

            holder.btn_addtocart.setOnClickListener(v -> {
                if (productDetails.getIn_stock().equals("1"))
                    findValidOrderId(productDetails, quantity[0]);
            });

        }

        @Override
        public int getItemCount() {
            return searchedProductsList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private CardView cv_mainlayout;
            private ImageView imv_productimage, imv_image_not_available;
            private LinearLayout ll_prices;
            private TextView tv_added_in_cart, tv_product_name, tv_selling_price, tv_max_retail_price,
                    tv_precentage_off, tv_quantity, tv_no_price_available, tv_you_save, tv_out_of_stock;
            private ImageButton btn_remove, btn_add;
            private Button btn_addtocart;

            public MyViewHolder(@NonNull View view) {
                super(view);
                cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
                imv_productimage = view.findViewById(R.id.imv_productimage);
                ll_prices = view.findViewById(R.id.ll_prices);
                imv_image_not_available = view.findViewById(R.id.imv_image_not_available);
                tv_added_in_cart = view.findViewById(R.id.tv_added_in_cart);
                tv_product_name = view.findViewById(R.id.tv_product_name);
                tv_selling_price = view.findViewById(R.id.tv_selling_price);
                tv_max_retail_price = view.findViewById(R.id.tv_max_retail_price);
                tv_precentage_off = view.findViewById(R.id.tv_precentage_off);
                tv_no_price_available = view.findViewById(R.id.tv_no_price_available);
                tv_you_save = view.findViewById(R.id.tv_you_save);
                tv_quantity = view.findViewById(R.id.tv_quantity);
                tv_out_of_stock = view.findViewById(R.id.tv_out_of_stock);
                btn_remove = view.findViewById(R.id.btn_remove);
                btn_add = view.findViewById(R.id.btn_add);
                btn_addtocart = view.findViewById(R.id.btn_addtocart);
            }
        }

        private void notifyDataSet() {
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

    }

    private void findValidOrderId(BookOrderProductsListModel.ResultBean productDetails, int quantity) {
        if (ordersList.size() == 0) {
            addOrderJsonCreation(productDetails, quantity);
        } else {
            boolean isPendingOrderAvailable = false;

            for (BookOrderGetMyOrdersModel.ResultBean orderDetails : ordersList) {
                if (orderDetails.getStatus_details().size() == 1) {
                    if (orderDetails.getStatus_details().get(0).getStatus().equals("1")) {
                        if (orderDetails.getOwner_business_id().equals(businessOwnerId)) {
                            isPendingOrderAvailable = true;
                            updateOrderWithNewProduct(productDetails, orderDetails, quantity);
                            break;
                        }
                    }
                }
            }

            if (!isPendingOrderAvailable) {
                addOrderJsonCreation(productDetails, quantity);
            }
        }
    }

    private void addOrderJsonCreation(BookOrderProductsListModel.ResultBean productDetails, int quantity) {
        JsonObject mainObj = new JsonObject();

        JsonArray productsDetailsArray = new JsonArray();
        JsonArray orderImageJsonArray = new JsonArray();

        JsonObject productObject = new JsonObject();
        productObject.addProperty("product_id", productDetails.getId());
        productObject.addProperty("quantity", String.valueOf(quantity));
        productObject.addProperty("amount", productDetails.getSelling_price());
        productsDetailsArray.add(productObject);

        mainObj.addProperty("type", "addOrder");
        mainObj.addProperty("owner_business_id", businessOwnerId);
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
            new AddOrder().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }
    }

    private void updateOrderWithNewProduct(BookOrderProductsListModel.ResultBean selectedProduct, BookOrderGetMyOrdersModel.ResultBean orderDetails, int quantity) {
        JsonObject mainObj = new JsonObject();

        List<BookOrderGetMyOrdersModel.ResultBean.ProductDetailsBean> productsToBeAddedList = new ArrayList<>();

        for (BookOrderGetMyOrdersModel.ResultBean.ProductDetailsBean orderProduct : orderDetails.getProduct_details()) {
            productsToBeAddedList.add(new BookOrderGetMyOrdersModel.ResultBean.ProductDetailsBean(
                    "0",
                    orderProduct.getProduct_id(),
                    orderDetails.getId(),
                    orderProduct.getQuantity(),
                    orderProduct.getCurrent_amount()
            ));
        }

        boolean isProductAlreadyAddedInOrder = false;

        for (int i = 0; i < productsToBeAddedList.size(); i++) {
            if (productsToBeAddedList.get(i).getProduct_id().equals(selectedProduct.getId())) {
                isProductAlreadyAddedInOrder = true;
                productsToBeAddedList.get(i).setQuantity(String.valueOf(quantity));
                productsToBeAddedList.get(i).setAmount(selectedProduct.getSelling_price());
            }
        }

        if (!isProductAlreadyAddedInOrder) {
            productsToBeAddedList.add(new BookOrderGetMyOrdersModel.ResultBean.ProductDetailsBean(
                    "0",
                    selectedProduct.getId(),
                    orderDetails.getId(),
                    String.valueOf(quantity),
                    selectedProduct.getSelling_price()
            ));
        }

        JsonArray productsDetailsArray = new JsonArray();

        for (int i = 0; i < productsToBeAddedList.size(); i++) {
            if (i <= orderDetails.getProduct_details().size() - 1) {
                JsonObject productObject = new JsonObject();
                productObject.addProperty("id", orderDetails.getProduct_details().get(i).getId());
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

        JsonArray orderImageJsonArray = new JsonArray();

        mainObj.addProperty("type", "updateOrder");
        mainObj.addProperty("id", orderDetails.getId());
        mainObj.addProperty("order_id", orderDetails.getOrder_id());
        mainObj.addProperty("owner_business_id", businessOwnerId);
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
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    BookOrderGetMyOrdersModel pojoDetails = new Gson().fromJson(result, BookOrderGetMyOrdersModel.class);
                    type = pojoDetails.getType();
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("HomeFragment"));
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("BookOrderProductsListActivityRefreshOrderList"));

                    if (type.equalsIgnoreCase("success")) {
                        Utilities.showMessage("Product added to cart", context, 1);
                        ordersList = pojoDetails.getResult();
                        showCartCount();
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
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    BookOrderGetMyOrdersModel pojoDetails = new Gson().fromJson(result, BookOrderGetMyOrdersModel.class);
                    type = pojoDetails.getType();
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("HomeFragment"));
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("BookOrderProductsListActivityRefreshOrderList"));

                    if (type.equalsIgnoreCase("success")) {
                        Utilities.showMessage("Product added to cart", context, 1);
                        ordersList = pojoDetails.getResult();
                        showCartCount();
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
        ibBack.setOnClickListener(v -> finish());
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