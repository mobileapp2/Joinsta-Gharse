package in.oriange.eorder.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.JsonObject;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.eorder.R;
import in.oriange.eorder.adapters.OfferImageSliderAdapter;
import in.oriange.eorder.models.BannerListModel;
import in.oriange.eorder.models.BookOrderProductsListModel;
import in.oriange.eorder.utilities.APICall;
import in.oriange.eorder.utilities.ApplicationConstants;
import in.oriange.eorder.utilities.UserSessionManager;
import in.oriange.eorder.utilities.Utilities;

import static in.oriange.eorder.utilities.ApplicationConstants.IMAGE_LINK;
import static in.oriange.eorder.utilities.Utilities.changeStatusBar;
import static in.oriange.eorder.utilities.Utilities.getCommaSeparatedNumber;

public class ViewMyProductDetailsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_title)
    AppCompatEditText toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
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
    @BindView(R.id.tv_saved)
    TextView tvSaved;
    @BindView(R.id.tv_no_price_available)
    TextView tvNoPriceAvailable;
    @BindView(R.id.tv_stock_availability)
    TextView tvStockAvailability;
    @BindView(R.id.tv_productinfo)
    TextView tvProductinfo;
    @BindView(R.id.ll_description)
    LinearLayout llDescription;
    @BindView(R.id.ll_prices)
    LinearLayout llPrices;

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private String userId;
    private BookOrderProductsListModel.ResultBean productDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_product_details);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }


    private void init() {
        context = ViewMyProductDetailsActivity.this;
        session = new UserSessionManager(context);
        changeStatusBar(context, getWindow());

        pd = new ProgressDialog(context, R.style.CustomDialogTheme);
        pd.setMessage("Please wait ...");
        pd.setCancelable(false);
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
        productDetails = (BookOrderProductsListModel.ResultBean) getIntent().getSerializableExtra("productDetails");

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

        tvProductname.setText(productDetails.getName());
        tvProductprice.setText("₹" + getCommaSeparatedNumber(Integer.parseInt(productDetails.getSelling_price())) + " / " + productDetails.getUnit_of_measure());
        tvProductinfo.setText(productDetails.getDescription());

        if (tvProductinfo.getText().toString().trim().equals(""))
            llDescription.setVisibility(View.GONE);

        if (productDetails.getIs_inclusive_tax().equals("1"))
            tvIsTaxInclusive.setVisibility(View.VISIBLE);
        else
            tvIsTaxInclusive.setVisibility(View.GONE);

        if (productDetails.getIn_stock().equals("1")) {
            tvStockAvailability.setText("In Stock");
            tvStockAvailability.setTextColor(context.getResources().getColor(R.color.green));
        } else if (productDetails.getIn_stock().equals("0")) {
            tvStockAvailability.setText("Not In Stock");
            tvStockAvailability.setTextColor(context.getResources().getColor(R.color.red));
        }

        int maxRetailPrice = (int) Float.parseFloat(productDetails.getMax_retail_price());
        int sellingPrice = (int) Float.parseFloat(productDetails.getSelling_price());
        int savedAmount = maxRetailPrice - sellingPrice;

        if (savedAmount <= 0) {
            tvSaved.setVisibility(View.GONE);
        } else {
            tvSaved.setText(Html.fromHtml("<strike>₹" + getCommaSeparatedNumber(maxRetailPrice) + "</strike> <font color=\"#ff0000\"> <i>Discount - ₹" + getCommaSeparatedNumber(savedAmount) + "</i></font>"));
        }

        if (sellingPrice == 0) {
            llPrices.setVisibility(View.GONE);
            tvNoPriceAvailable.setVisibility(View.VISIBLE);
        } else {
            llPrices.setVisibility(View.VISIBLE);
            tvNoPriceAvailable.setVisibility(View.GONE);
        }

    }

    private void setEventHandler() {

    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_backarrow_black);
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menus_edit_delete, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setMessage("Are you sure you want to delete this item?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.icon_alertred);
                builder.setCancelable(false);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Utilities.isNetworkAvailable(context))
                            new DeleteProduct().execute();
                        else
                            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertD = builder.create();
                alertD.show();
                break;
            case R.id.action_edit:
                context.startActivity(new Intent(context, EditProductActivity.class)
                        .putExtra("productDetails", productDetails));
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    private class DeleteProduct extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "deleteProduct");
            obj.addProperty("id", productDetails.getId());
            res = APICall.JSONAPICall(ApplicationConstants.PRODUCTSAPI, obj.toString());
            return res;
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
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("BusinessProductsActivity"));
                        Utilities.showMessage("Product deleted successfully", context, 1);
                        finish();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}