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
import in.oriange.joinstagharse.activities.ViewCustomerDetailsActivity;
import in.oriange.joinstagharse.models.CustomerModel;

import static android.Manifest.permission.CALL_PHONE;
import static in.oriange.joinstagharse.utilities.Utilities.provideCallPremission;

public class CustomersAdapter extends RecyclerView.Adapter<CustomersAdapter.MyViewHolder> {


    private Context context;
    private List<CustomerModel.ResultBean> customersList;

    public CustomersAdapter(Context context, List<CustomerModel.ResultBean> customersList) {
        this.context = context;
        this.customersList = customersList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_customer, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        CustomerModel.ResultBean customersDetails = customersList.get(position);

        holder.tv_name.setText(customersDetails.getName());
        holder.tv_mobile.setText(customersDetails.getCountry_code() + customersDetails.getMobile());
        holder.tv_city.setText(customersDetails.getCity());

        if (holder.tv_city.getText().toString().trim().isEmpty())
            holder.tv_city.setVisibility(View.GONE);

        if (customersDetails.getIs_prime_customer().equals("1"))
            holder.tv_prime.setVisibility(View.VISIBLE);
        else
            holder.tv_prime.setVisibility(View.GONE);

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
            context.startActivity(new Intent(context, ViewCustomerDetailsActivity.class)
                    .putExtra("customersDetails", customersDetails));
        });
    }

    @Override
    public int getItemCount() {
        return customersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_mainlayout;
        private TextView tv_name, tv_prime, tv_mobile, tv_city;
        private ImageButton ib_call;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cv_mainlayout = itemView.findViewById(R.id.cv_mainlayout);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_prime = itemView.findViewById(R.id.tv_prime);
            tv_mobile = itemView.findViewById(R.id.tv_mobile);
            tv_city = itemView.findViewById(R.id.tv_city);
            ib_call = itemView.findViewById(R.id.ib_call);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
