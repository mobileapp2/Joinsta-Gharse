package in.oriange.eorder.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.oriange.eorder.R;
import in.oriange.eorder.activities.ViewVendorDetailsActivity;
import in.oriange.eorder.models.VendorModel;

public class VendorsAdapter extends RecyclerView.Adapter<VendorsAdapter.MyViewHolder> {

    private Context context;
    private List<VendorModel.ResultBean> vendorList;

    public VendorsAdapter(Context context, List<VendorModel.ResultBean> vendorList) {
        this.context = context;
        this.vendorList = vendorList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_vendor, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        VendorModel.ResultBean vendorDetails = vendorList.get(position);

        holder.tv_name.setText(vendorDetails.getName());
        holder.tv_mobile.setText(vendorDetails.getCountry_code() + vendorDetails.getMobile());
        holder.tv_city.setText(vendorDetails.getCity());

        if (vendorDetails.getIs_prime_vendor().equals("1"))
            holder.tv_prime.setVisibility(View.VISIBLE);
        else
            holder.tv_prime.setVisibility(View.GONE);

        holder.cv_mainlayout.setOnClickListener(v -> {
            context.startActivity(new Intent(context, ViewVendorDetailsActivity.class)
                    .putExtra("vendorDetails", vendorDetails));
        });
    }

    @Override
    public int getItemCount() {
        return vendorList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_mainlayout;
        private TextView tv_name, tv_prime, tv_mobile, tv_city;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cv_mainlayout = itemView.findViewById(R.id.cv_mainlayout);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_prime = itemView.findViewById(R.id.tv_prime);
            tv_mobile = itemView.findViewById(R.id.tv_mobile);
            tv_city = itemView.findViewById(R.id.tv_city);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
