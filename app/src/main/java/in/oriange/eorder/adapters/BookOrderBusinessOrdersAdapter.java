package in.oriange.eorder.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.oriange.eorder.R;
import in.oriange.eorder.activities.ViewBookOrderBusinessOwnerOrderActivity;
import in.oriange.eorder.models.BookOrderBusinessOwnerModel;

import static android.Manifest.permission.CALL_PHONE;
import static in.oriange.eorder.utilities.Utilities.provideCallPremission;

public class BookOrderBusinessOrdersAdapter extends RecyclerView.Adapter<BookOrderBusinessOrdersAdapter.MyViewHolder> {

    private Context context;
    private List<BookOrderBusinessOwnerModel.ResultBean> orderList;

    public BookOrderBusinessOrdersAdapter(Context context, List<BookOrderBusinessOwnerModel.ResultBean> orderList) {
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
        BookOrderBusinessOwnerModel.ResultBean orderDetails = orderList.get(position);

        switch (orderDetails.getOrder_type()) {         //order_type = 'order_with_product' - 1, 'order_by_image' - 2,'order_by_text' - 3
            case "1":
                holder.tv_purchase_order_type.setText("Order with Product | " + "Order# - " + orderDetails.getOrder_id());
                break;
            case "2":
                holder.tv_purchase_order_type.setText("Order by Image | " + "Order# - " + orderDetails.getOrder_id());
                break;
            case "3":
                holder.tv_purchase_order_type.setText("Order by Text | " + "Order# - " + orderDetails.getOrder_id());
                break;
        }

        switch (orderDetails.getPurchase_order_type()) {      //purchase_order_type = 'individual' - 1, 'business' -2
            case "1":
                holder.tv_order_by.setText("Placed by - " + orderDetails.getCustomer_name());
                holder.tv_mobile.setText("+" + orderDetails.getCustomer_country_code() + orderDetails.getCustomer_mobile());
                break;
            case "2":
                holder.tv_order_by.setText("Placed by - " + orderDetails.getCustomer_business_name());

                List<BookOrderBusinessOwnerModel.ResultBean.CustomerBusinessMobileBean> mobilesList = orderDetails.getCustomer_business_mobile().get(0);
                if (mobilesList != null) {
                    if (mobilesList.size() != 0) {
                        holder.tv_mobile.setText(mobilesList.get(0).getCountry_code() + mobilesList.get(0).getMobile_number());
                    }
                }
                break;
        }

        if (holder.tv_mobile.getText().toString().trim().equals(""))
            holder.ll_mobile.setVisibility(View.GONE);

        switch (orderDetails.getStatus_details().get(orderDetails.getStatus_details().size() - 1).getStatus()) {
            //  status = 'IN CART' - 1,'PLACED'-2,'ACCEPTED'-3,'IN PROGRESS'-4,'DELIVERED'-5,'BILLED'-6,'CANCEL'-7
            case "1":
                holder.tv_order_status.setText("Order Added in Cart");
                holder.tv_order_status.setBackground(context.getResources().getDrawable(R.drawable.button_focusfilled_blue));
//                holder.tv_order_status.setTextColor(context.getResources().getColor(R.color.blue));
                break;
            case "2":
                holder.tv_order_status.setText("Order Placed");
                holder.tv_order_status.setBackground(context.getResources().getDrawable(R.drawable.button_focusfilled_yellow));
//                holder.tv_order_status.setTextColor(context.getResources().getColor(R.color.yellow));
                break;
            case "3":
                holder.tv_order_status.setText("Order Accepted");
                holder.tv_order_status.setBackground(context.getResources().getDrawable(R.drawable.button_focusfilled_green));
//                holder.tv_order_status.setTextColor(context.getResources().getColor(R.color.green));
                break;
            case "4":
                holder.tv_order_status.setText("Order in Progress");
                holder.tv_order_status.setBackground(context.getResources().getDrawable(R.drawable.button_focusfilled_orange));
//                holder.tv_order_status.setTextColor(context.getResources().getColor(R.color.orange));
                break;
            case "5":
                holder.tv_order_status.setText("Order Delivered");
                holder.tv_order_status.setBackground(context.getResources().getDrawable(R.drawable.button_focusfilled_green));
//                holder.tv_order_status.setTextColor(context.getResources().getColor(R.color.green));
                break;
            case "6":
                holder.tv_order_status.setText("Order Billing");
                holder.tv_order_status.setBackground(context.getResources().getDrawable(R.drawable.button_focusfilled_green));
//                holder.tv_order_status.setTextColor(context.getResources().getColor(R.color.red));
                break;
            case "7":
                holder.tv_order_status.setText("Order Cancelled");
                holder.tv_order_status.setBackground(context.getResources().getDrawable(R.drawable.button_focusfilled_red));
//                holder.tv_order_status.setTextColor(context.getResources().getColor(R.color.red));
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

        holder.cv_mainlayout.setOnClickListener(v ->
                context.startActivity(new Intent(context, ViewBookOrderBusinessOwnerOrderActivity.class)
                        .putExtra("orderDetails", orderDetails)));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_mainlayout;
        private TextView tv_purchase_order_type, tv_order_by, tv_mobile, tv_order_status;
        private LinearLayout ll_mobile;
        private ImageButton ib_call;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cv_mainlayout = itemView.findViewById(R.id.cv_mainlayout);
            tv_purchase_order_type = itemView.findViewById(R.id.tv_purchase_order_type);
            tv_order_by = itemView.findViewById(R.id.tv_order_by);
            tv_mobile = itemView.findViewById(R.id.tv_mobile);
            tv_order_status = itemView.findViewById(R.id.tv_order_status);
            ll_mobile = itemView.findViewById(R.id.ll_mobile);
            ib_call = itemView.findViewById(R.id.ib_call);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


}
