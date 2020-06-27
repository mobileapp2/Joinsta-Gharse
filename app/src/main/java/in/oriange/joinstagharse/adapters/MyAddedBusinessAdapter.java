package in.oriange.joinstagharse.adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;
import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.activities.AddCustomerActivity;
import in.oriange.joinstagharse.activities.AddVendorActivity;
import in.oriange.joinstagharse.activities.BookOrderBusinessOwnerOrdersActivity;
import in.oriange.joinstagharse.activities.BusinessProductsActivity;
import in.oriange.joinstagharse.activities.EnquiriesActivity;
import in.oriange.joinstagharse.activities.OffersForParticularRecordActivity;
import in.oriange.joinstagharse.activities.ProductCategoriesActivity;
import in.oriange.joinstagharse.activities.ViewMyBizDetailsActivity;
import in.oriange.joinstagharse.models.GetBusinessModel;
import in.oriange.joinstagharse.models.SearchDetailsModel;
import in.oriange.joinstagharse.utilities.APICall;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.Utilities;

public class MyAddedBusinessAdapter extends RecyclerView.Adapter<MyAddedBusinessAdapter.MyViewHolder> {

    private List<GetBusinessModel.ResultBean> resultArrayList;
    private Context context;

    public MyAddedBusinessAdapter(Context context, List<GetBusinessModel.ResultBean> resultArrayList) {
        this.context = context;
        this.resultArrayList = resultArrayList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_myadded, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int pos) {
        final int position = holder.getAdapterPosition();
        final GetBusinessModel.ResultBean searchDetails = resultArrayList.get(position);

        holder.tv_business_name.setText(searchDetails.getBusiness_code() + " - " + searchDetails.getBusiness_name());
        holder.tv_address.setText(searchDetails.getAddressCityPincode());

        if (searchDetails.getIs_pick_up_available().equals("1")) {
//            holder.tv_store_pickup.setTextColor(context.getResources().getColor(R.color.green));
            holder.imv_store_pickup.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_check));
//            holder.ll_store_pickup.setBackground(context.getResources().getDrawable(R.drawable.button_focusfilled_green));
            holder.imv_store_pickup.setColorFilter(ContextCompat.getColor(context, R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
//            holder.tv_store_pickup.setTextColor(context.getResources().getColor(R.color.red));
            holder.imv_store_pickup.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_cross));
//            holder.ll_store_pickup.setBackground(context.getResources().getDrawable(R.drawable.button_focusfilled_red));
            holder.imv_store_pickup.setColorFilter(ContextCompat.getColor(context, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
        }

        if (searchDetails.getIs_home_delivery_available().equals("1")) {
//            holder.tv_home_delivery.setTextColor(context.getResources().getColor(R.color.green));
            holder.imv_home_delivery.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_check));
//            holder.ll_home_delivery.setBackground(context.getResources().getDrawable(R.drawable.button_focusfilled_green));
            holder.imv_home_delivery.setColorFilter(ContextCompat.getColor(context, R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
//            holder.tv_home_delivery.setTextColor(context.getResources().getColor(R.color.red));
            holder.imv_home_delivery.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_cross));
//            holder.ll_home_delivery.setBackground(context.getResources().getDrawable(R.drawable.button_focusfilled_red));
            holder.imv_home_delivery.setColorFilter(ContextCompat.getColor(context, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
        }

        List<String> tagsList = searchDetails.getSubTypesTagsList("1");
        List<String> topFiveTagsList = new ArrayList<>();

        try {
            for (int i = 0; i < 5; i++)
                topFiveTagsList.add(tagsList.get(i));
        } catch (Exception e) {
//            e.printStackTrace();
        }

        holder.container_tags.setTags(topFiveTagsList);

        holder.cv_mainlayout.setOnClickListener(v -> context.startActivity(new Intent(context, ViewMyBizDetailsActivity.class)
                .putExtra("searchDetails", searchDetails)));

        holder.ll_offer.setOnClickListener(v ->
                context.startActivity(new Intent(context, OffersForParticularRecordActivity.class)
                        .putExtra("recordId", searchDetails.getId())
                        .putExtra("categoryType", "1")
                        .putExtra("CALLTYPE", "1")
                        .putExtra("APITYPE", "getOfferDetails")
                        .putExtra("categoryId", searchDetails.getType_id()))
        );

        holder.ll_orders.setOnClickListener(v -> context.startActivity(new Intent(context, BookOrderBusinessOwnerOrdersActivity.class)
                .putExtra("businessId", searchDetails.getId())));

        holder.ll_products.setOnClickListener(v -> context.startActivity(new Intent(context, BusinessProductsActivity.class)
                .putExtra("businessId", searchDetails.getId())
                .putExtra("businessCategoryId", searchDetails.getType_id())));

        holder.ll_enquire.setOnClickListener(v -> context.startActivity(new Intent(context, EnquiriesActivity.class)
                .putExtra("businessId", searchDetails.getId())));

        holder.ll_categories.setOnClickListener(v -> context.startActivity(new Intent(context, ProductCategoriesActivity.class)
                .putExtra("businessCategoryId", searchDetails.getType_id())
                .putExtra("businessCategoryName", searchDetails.getType_description())));

        holder.ll_settings.setOnClickListener(v -> showSettingsDialog(searchDetails));
    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_mainlayout;
        private TagContainerLayout container_tags;
        private TextView tv_business_name, tv_address;
        private LinearLayout ll_offer, ll_orders, ll_products, ll_enquire, ll_categories, ll_settings;
        private ImageView imv_store_pickup, imv_home_delivery;

        public MyViewHolder(View view) {
            super(view);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
            container_tags = view.findViewById(R.id.container_tags);
            tv_business_name = view.findViewById(R.id.tv_business_name);
            tv_address = view.findViewById(R.id.tv_address);
            imv_store_pickup = view.findViewById(R.id.imv_store_pickup);
            imv_home_delivery = view.findViewById(R.id.imv_home_delivery);
            ll_offer = view.findViewById(R.id.ll_offer);
            ll_orders = view.findViewById(R.id.ll_orders);
            ll_products = view.findViewById(R.id.ll_products);
            ll_enquire = view.findViewById(R.id.ll_enquire);
            ll_categories = view.findViewById(R.id.ll_categories);
            ll_settings = view.findViewById(R.id.ll_settings);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void showSettingsDialog(GetBusinessModel.ResultBean searchDetails) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_layout_business_settings, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialogBuilder.setView(promptView);

        final SwitchCompat sw_allow_book_order = promptView.findViewById(R.id.sw_allow_book_order);
        final ImageButton ib_close = promptView.findViewById(R.id.ib_close);

        if (searchDetails.getAllow_all_to_book_order().equals("1"))
            sw_allow_book_order.setChecked(true);

        final AlertDialog alertD = alertDialogBuilder.create();

        sw_allow_book_order.setOnClickListener(v -> {
            String allowBookOrder = "0";

            if (sw_allow_book_order.isChecked()) {
                allowBookOrder = "1";
            }

            if (Utilities.isNetworkAvailable(context)) {
                new ChangeAllowBookOrderFlag().execute(searchDetails.getId(), allowBookOrder);
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }
        });

        ib_close.setOnClickListener(v -> alertD.dismiss());

        alertD.show();
    }

    private class ChangeAllowBookOrderFlag extends AsyncTask<String, Void, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "updateAllBookOrder");
            obj.addProperty("business_id", params[0]);
            obj.addProperty("allow_all_to_book_order", params[1]);
            res = APICall.JSONAPICall(ApplicationConstants.BUSINESSAPI, obj.toString());
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
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("MyBusinessActivity"));
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


}