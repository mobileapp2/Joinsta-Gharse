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
import in.oriange.joinstagharse.activities.EnquiriesActivity;
import in.oriange.joinstagharse.activities.OffersForParticularRecordActivity;
import in.oriange.joinstagharse.activities.ViewMyBizDetailsActivity;
import in.oriange.joinstagharse.models.GetBusinessModel;

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

        holder.ll_offer.setOnClickListener(v -> {
            context.startActivity(new Intent(context, OffersForParticularRecordActivity.class)
                    .putExtra("recordId", searchDetails.getId())
                    .putExtra("categoryType", "1")
                    .putExtra("CALLTYPE", "1")
                    .putExtra("APITYPE", "getOfferDetails")
                    .putExtra("categoryId", searchDetails.getType_id()));

        });

        holder.ll_orders.setOnClickListener(v -> context.startActivity(new Intent(context, BookOrderBusinessOwnerOrdersActivity.class)
                .putExtra("businessId", searchDetails.getId())));

        holder.ll_book_order.setOnClickListener(v -> context.startActivity(new Intent(context, BusinessProductsActivity.class)
                .putExtra("businessId", searchDetails.getId())));

        holder.ll_enquire.setOnClickListener(v -> context.startActivity(new Intent(context, EnquiriesActivity.class)
                .putExtra("businessId", searchDetails.getId())));
    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_mainlayout;
        private TagContainerLayout container_tags;
        private TextView tv_business_name, tv_address;
        private LinearLayout ll_offer, ll_orders, ll_book_order, ll_enquire;
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
            ll_book_order = view.findViewById(R.id.ll_book_order);
            ll_enquire = view.findViewById(R.id.ll_enquire);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}