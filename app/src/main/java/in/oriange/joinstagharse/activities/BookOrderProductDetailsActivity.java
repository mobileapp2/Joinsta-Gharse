package in.oriange.joinstagharse.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.adapters.OfferImageSliderAdapter;
import in.oriange.joinstagharse.models.BannerListModel;
import in.oriange.joinstagharse.models.BookOrderGetMyOrdersModel;
import in.oriange.joinstagharse.models.BookOrderProductsListModel;
import in.oriange.joinstagharse.utilities.APICall;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.DownloadFile;
import in.oriange.joinstagharse.utilities.DownloadFileAndMessageShare;
import in.oriange.joinstagharse.utilities.UserSessionManager;
import in.oriange.joinstagharse.utilities.Utilities;

import static in.oriange.joinstagharse.utilities.ApplicationConstants.IMAGE_LINK;
import static in.oriange.joinstagharse.utilities.PermissionUtil.doesAppNeedPermissions;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.CALL_PHONE_PERMISSION_REQUEST;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.CAMERA_AND_STORAGE_PERMISSION_REQUEST;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.LOCATION_PERMISSION_REQUEST;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.READ_CONTACTS_PERMISSION_REQUEST;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.STORAGE_PERMISSION;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.STORAGE_PERMISSION_REQUEST;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.callPermissionMsg;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.cameraStoragePermissionMsg;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.isStoragePermissionGiven;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.locationPermissionMsg;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.manualPermission;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.readContactsPermissionMsg;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.storagePermissionMsg;
import static in.oriange.joinstagharse.utilities.Utilities.changeStatusBar;
import static in.oriange.joinstagharse.utilities.Utilities.getCommaSeparatedNumber;
import static in.oriange.joinstagharse.utilities.Utilities.linkifyTextView;

public class BookOrderProductDetailsActivity extends AppCompatActivity {

    @BindView(R.id.view_shadow)
    View viewShadow;
    @BindView(R.id.ll_nopreview)
    LinearLayout llNopreview;
    @BindView(R.id.imageSlider)
    SliderView imageSlider;
    @BindView(R.id.tv_productname)
    TextView tvProductname;
    @BindView(R.id.tv_productprice)
    TextView tvProductprice;
    @BindView(R.id.tv_is_tax_inclusive)
    TextView tvIsTaxInclusive;
    @BindView(R.id.tv_total_price)
    TextView tvTotalPrice;
    @BindView(R.id.tv_saved)
    TextView tvSaved;
    @BindView(R.id.tv_no_price_available)
    TextView tvNoPriceAvailable;
    @BindView(R.id.tv_productinfo)
    TextView tvProductinfo;
    @BindView(R.id.ll_description)
    LinearLayout llDescription;
    @BindView(R.id.btn_remove)
    ImageButton btnRemove;
    @BindView(R.id.tv_quantity)
    TextView tvQuantity;
    @BindView(R.id.btn_add)
    ImageButton btnAdd;
    @BindView(R.id.ll_add_to_cart)
    LinearLayout llAddToCart;
    @BindView(R.id.tv_out_of_stock)
    TextView tvOutOfStock;
    @BindView(R.id.anim_toolbar)
    Toolbar animToolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.cl_root)
    CoordinatorLayout clRoot;
    @BindView(R.id.ll_prices)
    LinearLayout llPrices;
    @BindView(R.id.tv_brochure)
    TextView tvBrochure;
    @BindView(R.id.ll_brochure)
    LinearLayout llBrochure;
    @BindView(R.id.btn_share)
    FloatingActionButton btnShare;
    @BindView(R.id.tv_add_to_cart)
    TextView tvAddToCart;

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private String userId, businessOwnerId, canShareProduct, businessCodeName;
    private int quantity;
    private int sellingPrice, maxRetailPrice, savedAmount, applicablePrice = 0;
    private boolean appBarExpanded = true;

    private List<BookOrderGetMyOrdersModel.ResultBean> ordersList;
    private BookOrderProductsListModel.ResultBean productDetails;
    private Menu collapsedMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_order_product_details);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = BookOrderProductDetailsActivity.this;
        session = new UserSessionManager(context);
//        changeStatusBar(context, getWindow());

        pd = new ProgressDialog(context, R.style.CustomDialogTheme);
        pd.setMessage("Please wait ...");
        pd.setCancelable(false);

        ordersList = new ArrayList<>();
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
        canShareProduct = getIntent().getStringExtra("canShareProduct");
        businessCodeName = getIntent().getStringExtra("businessCodeName");
        productDetails = (BookOrderProductsListModel.ResultBean) getIntent().getSerializableExtra("productDetails");
        ordersList = (List<BookOrderGetMyOrdersModel.ResultBean>) getIntent().getSerializableExtra("ordersList");

        quantity = productDetails.getQuantity();

        if (productDetails.getProduct_images().size() == 0) {
            imageSlider.setVisibility(View.GONE);
            llNopreview.setVisibility(View.VISIBLE);
        } else {
            List<BannerListModel.ResultBean> bannerList = new ArrayList<>();
            for (int i = 0; i < productDetails.getProduct_images().size(); i++) {
                bannerList.add(new BannerListModel.ResultBean("", "", IMAGE_LINK + "product/" + productDetails.getProduct_images().get(i)));
            }

            OfferImageSliderAdapter adapter = new OfferImageSliderAdapter(context, bannerList);
            imageSlider.setSliderAdapter(adapter);
            imageSlider.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
            imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
            imageSlider.setIndicatorSelectedColor(Color.WHITE);
            imageSlider.setIndicatorUnselectedColor(Color.GRAY);
            imageSlider.setAutoCycle(true);
            imageSlider.setScrollTimeInSec(10);

        }

        tvQuantity.setText(String.valueOf(quantity));
        tvProductname.setText(productDetails.getName());
        tvProductprice.setText("₹" + getCommaSeparatedNumber(Integer.parseInt(productDetails.getSelling_price())) + " / " + productDetails.getUnit_of_measure());
        tvProductinfo.setText(productDetails.getDescription());
        linkifyTextView(tvProductinfo);

        if (tvProductinfo.getText().toString().trim().equals(""))
            llDescription.setVisibility(View.GONE);

        if (productDetails.getIs_inclusive_tax().equals("1"))
            tvIsTaxInclusive.setVisibility(View.VISIBLE);
        else {
            tvIsTaxInclusive.setVisibility(View.GONE);
            tvProductprice.setGravity(Gravity.CENTER);
        }

        if (productDetails.getIn_stock().equals("1"))
            tvOutOfStock.setVisibility(View.GONE);
        else
            llAddToCart.setVisibility(View.GONE);

        maxRetailPrice = (int) Float.parseFloat(productDetails.getMax_retail_price());
        sellingPrice = (int) Float.parseFloat(productDetails.getSelling_price());
        savedAmount = maxRetailPrice - sellingPrice;

        if (savedAmount <= 0) {
            applicablePrice = sellingPrice;
            tvSaved.setVisibility(View.GONE);
        } else {
            applicablePrice = savedAmount;
            tvSaved.setText(Html.fromHtml("<strike>₹" + getCommaSeparatedNumber(maxRetailPrice) + "</strike> <font color=\"#ff0000\"> <i>You Saved ₹" + getCommaSeparatedNumber(savedAmount) + "</i></font>"));
        }
        tvTotalPrice.setText("₹" + getCommaSeparatedNumber(sellingPrice));

        if (sellingPrice == 0) {
            llPrices.setVisibility(View.GONE);
            tvNoPriceAvailable.setVisibility(View.VISIBLE);
        } else {
            llPrices.setVisibility(View.VISIBLE);
            tvNoPriceAvailable.setVisibility(View.GONE);
        }

        if (productDetails.getProduct_brouchure().size() != 0) {
            tvBrochure.setText(productDetails.getProduct_brouchure().get(0));
        } else {
            llBrochure.setVisibility(View.GONE);
        }

        if (canShareProduct.equals("1"))
            btnShare.setVisibility(View.VISIBLE);
        else
            btnShare.setVisibility(View.GONE);
    }

    private void setEventHandler() {

        btnRemove.setOnClickListener(v -> {
            if (quantity == 1) {
                return;
            }
            quantity = quantity - 1;
            tvQuantity.setText(String.valueOf(quantity));
            if (savedAmount <= 0) {
                applicablePrice = maxRetailPrice * quantity;
            } else {
                int earlybirdPrice = sellingPrice * quantity;
                int actualPrice = maxRetailPrice * quantity;
                applicablePrice = earlybirdPrice;
                int savedAmount1 = actualPrice - applicablePrice;
                tvSaved.setText(Html.fromHtml("<strike>₹" + getCommaSeparatedNumber(actualPrice) + "</strike> <font color=\"#ff0000\"> <i>You Saved ₹" + getCommaSeparatedNumber(savedAmount1) + "</i></font>"));
            }

            tvTotalPrice.setText("₹" + getCommaSeparatedNumber(applicablePrice));
        });

        btnAdd.setOnClickListener(v -> {
            quantity = quantity + 1;
            tvQuantity.setText(String.valueOf(quantity));
            if (savedAmount <= 0) {
                applicablePrice = maxRetailPrice * quantity;
            } else {
                int earlybirdPrice = sellingPrice * quantity;
                int actualPrice = maxRetailPrice * quantity;
                applicablePrice = earlybirdPrice;
                int savedAmount12 = actualPrice - applicablePrice;
                tvSaved.setText(Html.fromHtml("<strike>₹" + getCommaSeparatedNumber(actualPrice) + "</strike> <font color=\"#ff0000\"> <i>You Saved ₹" + getCommaSeparatedNumber(savedAmount12) + "</i></font>"));
            }

            tvTotalPrice.setText("₹" + getCommaSeparatedNumber(applicablePrice));
        });

        tvAddToCart.setOnClickListener(v -> {
            findValidOrderId(productDetails, quantity);
        });

        btnShare.setOnClickListener(v -> {
            shareDetails();
        });

        tvBrochure.setOnClickListener(v -> new DownloadFile(context, "Products", IMAGE_LINK + "product/" + productDetails.getProduct_brouchure().get(0)));
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
        appbar.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
            if (Math.abs(verticalOffset) > 200) {
                appBarExpanded = false;
            } else {
                appBarExpanded = true;
            }
            invalidateOptionsMenu();
        });

        setSupportActionBar(animToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        animToolbar.setNavigationOnClickListener(view -> finish());

        collapsingToolbar.setTitle("");
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (collapsedMenu != null && (!appBarExpanded)) {
            animToolbar.setNavigationIcon(R.drawable.icon_backarrow_black);

            if (canShareProduct.equals("1"))
                collapsedMenu.add("Share")
                        .setIcon(R.drawable.icon_share)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            collapsingToolbar.setTitle("Product Details");
            changeStatusBar(context, getWindow());
        } else {
            collapsingToolbar.setTitle("");
            animToolbar.setNavigationIcon(R.drawable.icon_backarrow_black);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        collapsingToolbar.setCollapsedTitleTextColor(context.getResources().getColor(R.color.black));
        collapsingToolbar.setExpandedTitleColor(context.getResources().getColor(R.color.black));
        return super.onPrepareOptionsMenu(collapsedMenu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        collapsedMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        if (item.getTitle() == "Share") {
            shareDetails();
        }

        return super.onOptionsItemSelected(item);
    }

    private void shareDetails() {
        if (productDetails.getProduct_brouchure().size() != 0) {
            if (doesAppNeedPermissions()) {
                if (!isStoragePermissionGiven(context, STORAGE_PERMISSION)) {
                    return;
                }
            }

            new DownloadFileAndMessageShare(context, "Products", IMAGE_LINK + "product/" + productDetails.getProduct_images().get(0), getStringShareMessage());
        } else {
            String message = getStringShareMessage();
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, message);
            context.startActivity(Intent.createChooser(sharingIntent, "Choose from following"));
        }
    }

    private String getStringShareMessage() {
        StringBuilder sb = new StringBuilder();

        sb.append("Product Name - " + productDetails.getName() + "\n");

        if (sellingPrice == 0)
            sb.append("Price Not Disclosed" + "\n");
        else {
            sb.append("Price - ₹" + sellingPrice + "/" + productDetails.getUnit_of_measure() + "\n");
            float savedAmount = maxRetailPrice - sellingPrice;
            if (savedAmount != 0) {
                float divide = (float) sellingPrice / maxRetailPrice;
                int percent = (int) (divide * 100);
                sb.append("Discount - " + (100 - percent) + "% off" + "\n");
            }
        }

        if (!productDetails.getDescription().equals("")) {
            sb.append("Description - " + productDetails.getDescription() + "\n");
        }

        sb.append("Business Name - " + businessCodeName + "\n");

        return sb.toString() + "\n" + "Joinsta Gharse\n" + "Click Here - " + ApplicationConstants.JOINSTA_PLAYSTORELINK;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_AND_STORAGE_PERMISSION_REQUEST: {
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED)) {
                    manualPermission(context, cameraStoragePermissionMsg, permissions, requestCode);
                }
            }
            break;
            case STORAGE_PERMISSION_REQUEST: {
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    manualPermission(context, storagePermissionMsg, permissions, requestCode);
                }
            }
            break;
            case CALL_PHONE_PERMISSION_REQUEST: {
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    manualPermission(context, callPermissionMsg, permissions, requestCode);
                }
            }
            break;
            case LOCATION_PERMISSION_REQUEST: {
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    manualPermission(context, locationPermissionMsg, permissions, requestCode);
                }
            }
            break;
            case READ_CONTACTS_PERMISSION_REQUEST: {
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    manualPermission(context, readContactsPermissionMsg, permissions, requestCode);
                }
            }
            break;
        }
    }

}