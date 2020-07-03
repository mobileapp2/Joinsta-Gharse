package in.oriange.joinstagharse.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;
import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.activities.BookOrderBusinessOwnerOrdersActivity;
import in.oriange.joinstagharse.activities.BusinessProductsActivity;
import in.oriange.joinstagharse.activities.BusinessSettingsActivity;
import in.oriange.joinstagharse.activities.EnquiriesActivity;
import in.oriange.joinstagharse.activities.OffersForParticularRecordActivity;
import in.oriange.joinstagharse.activities.ProductCategoriesActivity;
import in.oriange.joinstagharse.activities.ViewMyBizDetailsActivity;
import in.oriange.joinstagharse.activities.ViewMyServiceDetailsActivity;
import in.oriange.joinstagharse.models.GetServiceModel;

public class MyServicesAdapter extends RecyclerView.Adapter<MyServicesAdapter.MyViewHolder> {

    private List<GetServiceModel.ResultBean> resultArrayList;
    private Context context;

    public MyServicesAdapter(Context context, List<GetServiceModel.ResultBean> resultArrayList) {
        this.context = context;
        this.resultArrayList = resultArrayList;
    }

    @Override
    public MyServicesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_my_service, parent, false);
        return new MyServicesAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyServicesAdapter.MyViewHolder holder, final int pos) {
        final int position = holder.getAdapterPosition();
        final GetServiceModel.ResultBean searchDetails = resultArrayList.get(position);

        holder.tv_business_name.setText(searchDetails.getBusiness_code() + " - " + searchDetails.getBusiness_name());
        holder.tv_service_name.setText(searchDetails.getAddressCityPincode());

        if (searchDetails.getIs_pick_up_available().equals("1")) {
            holder.imv_store_pickup.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_check));
            holder.imv_store_pickup.setColorFilter(ContextCompat.getColor(context, R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            holder.imv_store_pickup.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_cross));
            holder.imv_store_pickup.setColorFilter(ContextCompat.getColor(context, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
        }

        if (searchDetails.getIs_home_delivery_available().equals("1")) {
            holder.imv_home_delivery.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_check));
            holder.imv_home_delivery.setColorFilter(ContextCompat.getColor(context, R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            holder.imv_home_delivery.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_cross));
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

        holder.cv_mainlayout.setOnClickListener(v -> context.startActivity(new Intent(context, ViewMyServiceDetailsActivity.class)
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

        holder.ll_settings.setOnClickListener(v -> {
            context.startActivity(new Intent(context, BusinessSettingsActivity.class)
                    .putExtra("searchDetails", searchDetails));
        });
    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_mainlayout;
        private TagContainerLayout container_tags;
        private TextView tv_business_name, tv_service_name;
        private LinearLayout ll_offer, ll_orders, ll_products, ll_enquire, ll_categories, ll_settings;
        private ImageView imv_store_pickup, imv_home_delivery;

        public MyViewHolder(View view) {
            super(view);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
            container_tags = view.findViewById(R.id.container_tags);
            tv_business_name = view.findViewById(R.id.tv_business_name);
            tv_service_name = view.findViewById(R.id.tv_service_name);
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

}
