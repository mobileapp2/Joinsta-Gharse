package in.oriange.joinstagharse.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;

import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.activities.EditAddressActivity;
import in.oriange.joinstagharse.models.AddressModel;
import in.oriange.joinstagharse.utilities.APICall;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.Utilities;

public class MyAddressAdapter extends RecyclerView.Adapter<MyAddressAdapter.MyViewHolder> {

    private Context context;
    private List<AddressModel.ResultBean> addressList;

    public MyAddressAdapter(Context context, List<AddressModel.ResultBean> addressList) {
        this.context = context;
        this.addressList = addressList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_address, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        AddressModel.ResultBean addressDetails = addressList.get(position);

        holder.tv_address_name.setText(addressDetails.getFull_name());
        holder.tv_address.setText(addressDetails.getAddress_line1());

        holder.btn_edit.setOnClickListener(v -> {
            context.startActivity(new Intent(context, EditAddressActivity.class).putExtra("addressDetails", addressDetails));
        });

        holder.btn_delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
            builder.setMessage("Are you sure you want to delete this address?");
            builder.setTitle("Alert");
            builder.setIcon(R.drawable.icon_alertred);
            builder.setCancelable(false);
            builder.setPositiveButton("YES", (dialog, id) -> {
                if (Utilities.isNetworkAvailable(context)) {
                    new DeleteAddress().execute(addressDetails.getUser_address_id());
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }
            });
            builder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
            AlertDialog alertD = builder.create();
            alertD.show();
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_address_name, tv_address;
        private Button btn_edit, btn_delete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_address_name = itemView.findViewById(R.id.tv_address_name);
            tv_address = itemView.findViewById(R.id.tv_address);
            btn_edit = itemView.findViewById(R.id.btn_edit);
            btn_delete = itemView.findViewById(R.id.btn_delete);
        }
    }

    private class DeleteAddress extends AsyncTask<String, Void, String> {

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context, R.style.CustomDialogTheme);
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "deleteUserAddress");
            obj.addProperty("id", params[0]);
            res = APICall.JSONAPICall(ApplicationConstants.ADDRESSAPI, obj.toString());
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("MyAddressActivity"));
                        Utilities.showMessage(message, context, 1);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
