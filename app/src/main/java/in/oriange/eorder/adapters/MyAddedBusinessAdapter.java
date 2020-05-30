package in.oriange.eorder.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;
import in.oriange.eorder.R;
import in.oriange.eorder.activities.BookOrderBusinessOwnerOrdersActivity;
import in.oriange.eorder.activities.BusinessProductsActivity;
import in.oriange.eorder.activities.ViewMyBizDetailsActivity;
import in.oriange.eorder.models.GetBusinessModel;

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

        if (!searchDetails.getDeliveryType().equals(""))
            holder.tv_delivery_type.setText(searchDetails.getDeliveryType());
        else
            holder.tv_delivery_type.setVisibility(View.GONE);

        List<String> tagsList = searchDetails.getSubTypesTagsList("1");
        List<String> topFiveTagsList = new ArrayList<>();

        try {
            for (int i = 0; i < 5; i++)
                topFiveTagsList.add(tagsList.get(i));
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.container_tags.setTags(topFiveTagsList);

        holder.cv_mainlayout.setOnClickListener(v -> context.startActivity(new Intent(context, ViewMyBizDetailsActivity.class)
                .putExtra("searchDetails", searchDetails)));

        holder.ll_offer.setOnClickListener(v -> {

        });

        holder.ll_orders.setOnClickListener(v -> context.startActivity(new Intent(context, BookOrderBusinessOwnerOrdersActivity.class)
                .putExtra("businessId", searchDetails.getId())));

        holder.ll_book_order.setOnClickListener(v -> context.startActivity(new Intent(context, BusinessProductsActivity.class)
                .putExtra("businessId", searchDetails.getId())));
    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_mainlayout;
        private TagContainerLayout container_tags;
        private TextView tv_business_name, tv_address, tv_delivery_type;
        private LinearLayout ll_offer, ll_orders, ll_book_order;

        public MyViewHolder(View view) {
            super(view);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
            container_tags = view.findViewById(R.id.container_tags);
            tv_business_name = view.findViewById(R.id.tv_business_name);
            tv_address = view.findViewById(R.id.tv_address);
            tv_delivery_type = view.findViewById(R.id.tv_delivery_type);
            ll_offer = view.findViewById(R.id.ll_offer);
            ll_orders = view.findViewById(R.id.ll_orders);
            ll_book_order = view.findViewById(R.id.ll_book_order);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}