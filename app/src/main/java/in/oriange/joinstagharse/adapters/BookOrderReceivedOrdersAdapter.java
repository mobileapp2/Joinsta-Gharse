package in.oriange.joinstagharse.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import in.oriange.joinstagharse.activities.ChatActivity;
import in.oriange.joinstagharse.activities.ViewBookOrderBusinessOwnerOrderActivity;
import in.oriange.joinstagharse.models.BookOrderBusinessOwnerModel;

import static android.Manifest.permission.CALL_PHONE;
import static in.oriange.joinstagharse.utilities.Utilities.getCommaSeparatedNumber;
import static in.oriange.joinstagharse.utilities.Utilities.provideCallPremission;
import static in.oriange.joinstagharse.utilities.Utilities.showCallDialog;

public class BookOrderReceivedOrdersAdapter extends RecyclerView.Adapter<BookOrderReceivedOrdersAdapter.MyViewHolder> {

    private Context context;
    private List<BookOrderBusinessOwnerModel.ResultBean> orderList;
    private String callType;      // 1 = All Received Orders 2 = Business-wise Received Orders
    public String selectedOrderId = "0";

    public BookOrderReceivedOrdersAdapter(Context context, List<BookOrderBusinessOwnerModel.ResultBean> orderList, String callType) {
        this.context = context;
        this.orderList = orderList;
        this.callType = callType;
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
        BookOrderBusinessOwnerModel.ResultBean orderDetails = orderList.get(position);
        holder.tv_order_id.setText("Order ID # " + orderDetails.getOrder_id());
        holder.tv_received_for.setText("Received For - " + orderDetails.getOwner_business_code() + " - " + orderDetails.getOwner_business_name());
        String orderer = "";
        switch (orderDetails.getPurchase_order_type()) {      //purchase_order_type = 'individual' - 1, 'business' -2
            case "1": {
                orderer = orderDetails.getCustomer_name();
                holder.tv_supplier_orderer.setText("Orderer - " + orderDetails.getCustomer_name());
            }
            break;
            case "2": {
                orderer = orderDetails.getCustomer_business_code() + " - " + orderDetails.getCustomer_business_name();
                holder.tv_supplier_orderer.setText("Orderer - " + orderDetails.getCustomer_business_code() + " - " + orderDetails.getCustomer_business_name());
            }
            break;
        }

        switch (orderDetails.getOrder_type()) {         //order_type = 'order_with_product' - 1, 'order_by_image' - 2,'order_by_text' - 3
            case "1": {
                holder.tv_oder_by.setText("Order by - Product");
                int price = 0;
                for (BookOrderBusinessOwnerModel.ResultBean.ProductDetailsBean productDetailsBean : orderDetails.getProduct_details())
                    if (orderDetails.getStatus_details().get(orderDetails.getStatus_details().size() - 1).getStatus().equals("1"))
                        price = price + (Integer.parseInt(productDetailsBean.getCurrent_amount())
                                * Integer.parseInt(productDetailsBean.getQuantity()));
                    else
                        price = price + (Integer.parseInt(productDetailsBean.getAmount())
                                * Integer.parseInt(productDetailsBean.getQuantity()));
//                holder.tv_price.setText(Html.fromHtml("Total Amount - <font color=\"#000000\"> <b>₹ " + getCommaSeparatedNumber(price) + "</b></font>"));
                holder.tv_price.setText("₹ " + getCommaSeparatedNumber(price));
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

        holder.tv_oder_by.setText(orderDetails.getOrderTypePurchaseType());
        holder.tv_delivery_type_status.setText(orderDetails.getDeliveryAndStatus());
        holder.tv_mobile.setText(orderDetails.getCustomer_country_code() + orderDetails.getCustomer_mobile());

        if (orderDetails.getStatus_details().get(orderDetails.getStatus_details().size() - 1).getStatus().equals("1")) {
            holder.tv_delivery_type.setVisibility(View.GONE);
        } else {
            if (orderDetails.getDelivery_option().equals("store_pickup"))
                holder.tv_delivery_type.setText("Store Pickup");
            else if (orderDetails.getDelivery_option().equals("home_delivery"))
                holder.tv_delivery_type.setText("Home Delivery");
        }

        switch (orderDetails.getStatus_details().get(orderDetails.getStatus_details().size() - 1).getStatus()) {
            //  status = 'IN CART' - 1,'PLACED'-2,'ACCEPTED'-3,'Ready to Deliver'-4,'DELIVERED'-5,'BILLED'-6,'CANCEL'-7
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
                holder.tv_order_status.setText("Ready to Deliver");
                holder.tv_order_status.setBackground(context.getResources().getDrawable(R.drawable.button_focusfilled_orange));
                break;
            case "5":
                holder.tv_order_status.setText("Delivered");
                holder.tv_order_status.setBackground(context.getResources().getDrawable(R.drawable.button_focusfilled_green));
                break;
            case "6":
                holder.tv_order_status.setText("Billed");
                holder.tv_order_status.setBackground(context.getResources().getDrawable(R.drawable.button_focusfilled_green));
                break;
            case "7":
                holder.tv_order_status.setText("Cancelled");
                holder.tv_order_status.setBackground(context.getResources().getDrawable(R.drawable.button_focusfilled_red));
                break;
        }

        if (orderDetails.getVendor_unread_msg_count().equals("0")) {
            holder.tv_unread_count.setVisibility(View.GONE);
        } else {
            holder.tv_unread_count.setVisibility(View.VISIBLE);
            holder.tv_unread_count.setText(orderDetails.getVendor_unread_msg_count());
        }

        holder.ib_call.setOnClickListener(v -> {
            showCallDialog(context, holder.tv_mobile.getText().toString().trim());
        });

        String finalOrderer = orderer;
        holder.ib_chat.setOnClickListener(v -> {
            context.startActivity(new Intent(context, ChatActivity.class)
                    .putExtra("orderId", orderDetails.getId())
                    .putExtra("name", finalOrderer)
                    .putExtra("sendTo", orderDetails.getCustomer_id()));
        });

        holder.cv_mainlayout.setOnClickListener(v -> {
            selectedOrderId = orderDetails.getId();
            context.startActivity(new Intent(context, ViewBookOrderBusinessOwnerOrderActivity.class)
                    .putExtra("orderDetails", orderDetails)
                    .putExtra("callType", callType));
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_mainlayout;
        private TextView tv_order_id, tv_supplier_orderer, tv_received_for, tv_oder_by, tv_delivery_type_status, tv_price, tv_mobile, tv_delivery_type,
                tv_order_status, tv_unread_count;
        private ImageButton ib_call, ib_chat;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cv_mainlayout = itemView.findViewById(R.id.cv_mainlayout);
            tv_order_id = itemView.findViewById(R.id.tv_order_id);
            tv_supplier_orderer = itemView.findViewById(R.id.tv_supplier_orderer);
            tv_received_for = itemView.findViewById(R.id.tv_received_for);
            tv_oder_by = itemView.findViewById(R.id.tv_oder_by);
            tv_delivery_type_status = itemView.findViewById(R.id.tv_delivery_type_status);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_mobile = itemView.findViewById(R.id.tv_mobile);
            tv_delivery_type = itemView.findViewById(R.id.tv_delivery_type);
            tv_order_status = itemView.findViewById(R.id.tv_order_status);
            tv_unread_count = itemView.findViewById(R.id.tv_unread_count);
            ib_call = itemView.findViewById(R.id.ib_call);
            ib_chat = itemView.findViewById(R.id.ib_chat);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void refreshList(List<BookOrderBusinessOwnerModel.ResultBean> orderList) {
        this.orderList = orderList;
        notifyDataSetChanged();
    }

}
