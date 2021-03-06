package in.oriange.joinstagharse.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.activities.ViewMyProductDetailsActivity;
import in.oriange.joinstagharse.models.BookOrderProductsListModel;
import in.oriange.joinstagharse.utilities.Utilities;

import static in.oriange.joinstagharse.utilities.ApplicationConstants.IMAGE_LINK;

public class BusinessProductsListAdapter extends RecyclerView.Adapter<BusinessProductsListAdapter.MyViewHolder> {

    private Context context;
    private List<BookOrderProductsListModel.ResultBean> productsList;

    public BusinessProductsListAdapter(Context context, List<BookOrderProductsListModel.ResultBean> productsList) {
        this.context = context;
        this.productsList = productsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_business_product, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        BookOrderProductsListModel.ResultBean productDetails = productsList.get(position);

        if (productDetails.getProduct_images().size() != 0) {
            String url = IMAGE_LINK + "product/" + productDetails.getProduct_images().get(0);
            Picasso.with(context)
                    .load(url)
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

        holder.tv_productname.setText(productDetails.getName());
        holder.tv_productinfo.setText(productDetails.getDescription());

        if (productDetails.getSelling_price().equals("0")) {
            holder.tv_price.setVisibility(View.GONE);
            holder.tv_no_price_available.setVisibility(View.VISIBLE);
        } else {
            holder.tv_no_price_available.setVisibility(View.GONE);
            holder.tv_price.setVisibility(View.VISIBLE);
            holder.tv_price.setText("₹ " + Utilities.getCommaSeparatedNumber(Integer.parseInt(productDetails.getSelling_price())) + "/" + productDetails.getUnit_of_measure());
        }

        if (holder.tv_productinfo.getText().toString().trim().equals(""))
            holder.tv_productinfo.setVisibility(View.GONE);

        if (productDetails.getIn_stock().equals("1")) {
            holder.tv_in_stock.setText("In stock");
            holder.tv_in_stock.setBackground(context.getResources().getDrawable(R.drawable.button_focusfilled_green));
        } else if (productDetails.getIn_stock().equals("0")) {
            holder.tv_in_stock.setText("Out of stock");
            holder.tv_in_stock.setBackground(context.getResources().getDrawable(R.drawable.button_focusfilled_red));
        }

        holder.cv_mainlayout.setOnClickListener(v -> context.startActivity(new Intent(context, ViewMyProductDetailsActivity.class)
                .putExtra("productDetails", productDetails)));
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_mainlayout;
        private TextView tv_productname, tv_productinfo, tv_in_stock, tv_price, tv_no_price_available;
        private ImageView imv_productimage, imv_image_not_available;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cv_mainlayout = itemView.findViewById(R.id.cv_mainlayout);
            tv_productname = itemView.findViewById(R.id.tv_productname);
            tv_productinfo = itemView.findViewById(R.id.tv_productinfo);
            tv_in_stock = itemView.findViewById(R.id.tv_in_stock);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_no_price_available = itemView.findViewById(R.id.tv_no_price_available);
            imv_image_not_available = itemView.findViewById(R.id.imv_image_not_available);
            imv_productimage = itemView.findViewById(R.id.imv_productimage);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
