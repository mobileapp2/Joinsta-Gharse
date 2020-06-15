package in.oriange.joinstagharse.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.models.BookOrderBusinessOwnerModel;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.UserSessionManager;

import static in.oriange.joinstagharse.utilities.Utilities.changeDateFormat;

public class BookOrderStatusAdapter extends RecyclerView.Adapter<BookOrderStatusAdapter.MyViewHolder> {

    private Context context;
    private List<BookOrderBusinessOwnerModel.ResultBean.StatusDetailsBean> statusList;
    private String userId, updateByUserId, type;   //  1 = From Received Orders   2 = Sent Orders

    public BookOrderStatusAdapter(Context context, List<BookOrderBusinessOwnerModel.ResultBean.StatusDetailsBean> statusList, String type, String updateByUserId) {
        this.context = context;
        this.statusList = statusList;
        this.type = type;
        this.updateByUserId = updateByUserId;

        try {
            JSONArray user_info = new JSONArray(new UserSessionManager(context).getUserDetails().get(ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);

            userId = json.getString("userid");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_book_order_status, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        final BookOrderBusinessOwnerModel.ResultBean.StatusDetailsBean statusDetails = statusList.get(position);

        holder.tv_date.setText(changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd-MM-yyyy", statusDetails.getDate()));
        holder.tv_time.setText(changeDateFormat("yyyy-MM-dd HH:mm:ss", "hh:mm a", statusDetails.getDate()));

        switch (statusDetails.getStatus()) {
            //  status = 'IN CART' - 1,'PLACED'-2,'ACCEPTED'-3,'Ready to Deliver'-4,'DELIVERED'-5,'BILLED'-6,'CANCEL'-7
            case "1":
                holder.tv_status.setText("In Cart");
                holder.imv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_green_check));
                break;
            case "2":
                holder.tv_status.setText("Placed");
                holder.imv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_green_check));
                break;
            case "3":
                holder.tv_status.setText("Accepted");
                holder.imv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_green_check));
                break;
            case "4":
                holder.tv_status.setText("Ready to Deliver");
                holder.imv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_green_check));
                break;
            case "5":
                holder.tv_status.setText("Delivered");
                holder.imv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_green_check));
                break;
            case "6":
                holder.tv_status.setText("Billed");
                holder.imv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_green_check));
                break;
            case "7":
                if (type.equals("2"))
                    if (!updateByUserId.equals(userId))
                        holder.tv_status.setText("Rejected");
                    else
                        holder.tv_status.setText("Cancelled");
                else
                    holder.tv_status.setText("Cancelled");
                holder.imv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_red_cross));
                break;
        }

        if (position == statusList.size() - 1) {
            holder.view_divider.setVisibility(View.GONE);
//            if (!statusDetails.getStatus().equals("7")) {
//                holder.imv_status.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_green_check));
//            }
        }
    }

    @Override
    public int getItemCount() {
        return statusList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imv_status;
        private TextView tv_date, tv_time, tv_status;
        private View view_divider;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imv_status = itemView.findViewById(R.id.imv_status);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_status = itemView.findViewById(R.id.tv_status);
            view_divider = itemView.findViewById(R.id.view_divider);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
