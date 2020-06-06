package in.oriange.joinstagharse.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.activities.ViewBookOrderMyOrderActivity;
import in.oriange.joinstagharse.models.BookOrderGetMyOrdersModel;

import static android.Manifest.permission.CALL_PHONE;
import static in.oriange.joinstagharse.utilities.Utilities.getCommaSeparatedNumber;
import static in.oriange.joinstagharse.utilities.Utilities.provideCallPremission;

public class BookOrderMyOrdersAdapter extends RecyclerView.Adapter<BookOrderMyOrdersAdapter.MyViewHolder> {

    private Context context;
    private List<BookOrderGetMyOrdersModel.ResultBean> orderList;

    public BookOrderMyOrdersAdapter(Context context, List<BookOrderGetMyOrdersModel.ResultBean> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_orders, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        BookOrderGetMyOrdersModel.ResultBean orderDetails = orderList.get(position);

        holder.tv_order_id.setText("Order ID # " + orderDetails.getOrder_id());
        holder.tv_supplier.setText(orderDetails.getOwner_business_code() + " - " + orderDetails.getOwner_business_name());

        switch (orderDetails.getOrder_type()) {         //order_type = 'order_with_product' - 1, 'order_by_image' - 2,'order_by_text' - 3
            case "1": {
                holder.tv_oder_by.setText("Order by - Product");
                int price = 0;
                for (BookOrderGetMyOrdersModel.ResultBean.ProductDetailsBean productDetailsBean : orderDetails.getProduct_details())
                    if (orderDetails.getStatus_details().get(orderDetails.getStatus_details().size() - 1).getStatus().equals("1"))
                        price = price + (Integer.parseInt(productDetailsBean.getCurrent_amount())
                                * Integer.parseInt(productDetailsBean.getQuantity()));
                    else
                        price = price + (Integer.parseInt(productDetailsBean.getAmount())
                                * Integer.parseInt(productDetailsBean.getQuantity()));
                holder.tv_price.setText(Html.fromHtml("Total Amount - <font color=\"#000000\"> <b>₹ " + getCommaSeparatedNumber(price) + "</b></font>"));
            }
            break;
            case "2": {
                holder.tv_oder_by.setText("Order by - Image");
                holder.tv_price.setVisibility(View.GONE);
            }
            break;
            case "3": {
                holder.tv_oder_by.setText("Order by - Text");
                holder.tv_price.setVisibility(View.GONE);
            }
            break;
        }

        holder.tv_mobile.setText(orderDetails.getOwner_country_code() + orderDetails.getOwner_mobile());

        if (orderDetails.getStatus_details().get(orderDetails.getStatus_details().size() - 1).getStatus().equals("1")) {
            holder.tv_delivery_type.setVisibility(View.GONE);
        } else {
            if (orderDetails.getDelivery_option().equals("store_pickup"))
                holder.tv_delivery_type.setText("Store Pickup");
            else if (orderDetails.getDelivery_option().equals("home_delivery"))
                holder.tv_delivery_type.setText("Home Delivery");
        }

        switch (orderDetails.getStatus_details().get(orderDetails.getStatus_details().size() - 1).getStatus()) {
            //  status = 'IN CART' - 1,'PLACED'-2,'ACCEPTED'-3,'IN PROGRESS'-4,'DELIVERED'-5,'BILLED'-6,'CANCEL'-7
            case "1":
                holder.tv_order_status.setText("In Cart");
                holder.tv_order_status.setBackground(context.getResources().getDrawable(R.drawable.button_focusfilled_blue));
                break;
            case "2":
                holder.tv_order_status.setText("Placed");
                holder.tv_order_status.setBackground(context.getResources().getDrawable(R.drawable.button_focusfilled_yellow));
                break;
            case "3":
                holder.tv_order_status.setText("Accepted");
                holder.tv_order_status.setBackground(context.getResources().getDrawable(R.drawable.button_focusfilled_green));
                break;
            case "4":
                holder.tv_order_status.setText("In Progress");
                holder.tv_order_status.setBackground(context.getResources().getDrawable(R.drawable.button_focusfilled_orange));
                break;
            case "5":
                holder.tv_order_status.setText("Delivered");
                holder.tv_order_status.setBackground(context.getResources().getDrawable(R.drawable.button_focusfilled_green));
                break;
            case "6":
                holder.tv_order_status.setText("Billing");
                holder.tv_order_status.setBackground(context.getResources().getDrawable(R.drawable.button_focusfilled_green));
                break;
            case "7":
                holder.tv_order_status.setText("Cancelled");
                holder.tv_order_status.setBackground(context.getResources().getDrawable(R.drawable.button_focusfilled_red));
                break;
        }

        holder.ib_call.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(context, CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                provideCallPremission(context);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setMessage("Are you sure you want to make a call?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.icon_call);
                builder.setCancelable(false);
                builder.setPositiveButton("YES", (dialog, id) ->
                        context.startActivity(new Intent(Intent.ACTION_CALL,
                                Uri.parse("tel:" + holder.tv_mobile.getText().toString().trim())))
                );
                builder.setNegativeButton("NO", (dialog, which) ->
                        dialog.dismiss()
                );
                AlertDialog alertD = builder.create();
                alertD.show();
            }
        });

        holder.cv_mainlayout.setOnClickListener(v -> {
//            if (orderDetails.getStatus_details().get(orderDetails.getStatus_details().size() - 1).getStatus().equals("1"))
//                context.startActivity(new Intent(context, BookOrderSelectDeliveryTypeActivity.class)
//                        .putExtra("businessOwnerId", orderDetails.getOwner_business_id())
//                        .putExtra("businessOwnerAddress", orderDetails.getOwner_address())
//                        .putExtra("isHomeDeliveryAvailable", orderDetails.getIs_home_delivery_available())
//                        .putExtra("isPickUpAvailable", orderDetails.getIs_pick_up_available())
//                        .putExtra("orderType", "1")
//                        .putExtra("orderText", "")
//                        .putExtra("orderDetails", orderDetails)
//                        .putExtra("orderImageArray", new JsonArray().toString()));
//            else
            context.startActivity(new Intent(context, ViewBookOrderMyOrderActivity.class)
                    .putExtra("orderDetails", orderDetails));
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_mainlayout;
        private TextView tv_order_id, tv_supplier, tv_oder_by, tv_price, tv_mobile, tv_delivery_type, tv_order_status;
        private ImageButton ib_call;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cv_mainlayout = itemView.findViewById(R.id.cv_mainlayout);
            tv_order_id = itemView.findViewById(R.id.tv_order_id);
            tv_supplier = itemView.findViewById(R.id.tv_supplier);
            tv_oder_by = itemView.findViewById(R.id.tv_oder_by);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_mobile = itemView.findViewById(R.id.tv_mobile);
            tv_delivery_type = itemView.findViewById(R.id.tv_delivery_type);
            tv_order_status = itemView.findViewById(R.id.tv_order_status);
            ib_call = itemView.findViewById(R.id.ib_call);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
