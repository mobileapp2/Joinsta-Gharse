package in.oriange.joinstagharse.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.models.ProductCategoriesModel;
import in.oriange.joinstagharse.utilities.APICall;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.UserSessionManager;
import in.oriange.joinstagharse.utilities.Utilities;

public class ProductCategoryChildAdapter extends RecyclerView.Adapter<ProductCategoryChildAdapter.MyViewHolder> {

    private Context context;
    private List<ProductCategoriesModel.ResultBean.SubCategoriesBean> subCategoryList;
    private String userId;

    public ProductCategoryChildAdapter(Context context, List<ProductCategoriesModel.ResultBean.SubCategoriesBean> subCategoryList) {
        this.context = context;
        this.subCategoryList = subCategoryList;

        try {
            JSONArray user_info = new JSONArray(new UserSessionManager(context).getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
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
        View view = inflater.inflate(R.layout.list_row_sub_product_categories, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        ProductCategoriesModel.ResultBean.SubCategoriesBean productCategoryDetails = subCategoryList.get(position);

        holder.tv_main_category.setText(productCategoryDetails.getProduct_category());

        holder.ib_edit.setOnClickListener(v -> {
            openUpdateCategoryDialog(productCategoryDetails);
        });

        holder.ib_cart.setOnClickListener(v -> {
            openDeleteCategoryDialog(productCategoryDetails);
        });
    }

    @Override
    public int getItemCount() {
        return subCategoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_main_category;
        private ImageButton ib_edit, ib_cart;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_main_category = itemView.findViewById(R.id.tv_main_category);
            ib_edit = itemView.findViewById(R.id.ib_edit);
            ib_cart = itemView.findViewById(R.id.ib_cart);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void openDeleteCategoryDialog(ProductCategoriesModel.ResultBean.SubCategoriesBean productCategoryDetails) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setIcon(R.drawable.icon_alertred);
        alertDialogBuilder.setMessage("Are you sure you want to cancel this subcategory?");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Yes", (dialog, which) -> {
            if (Utilities.isNetworkAvailable(context)) {
                new DeleteCategory().execute(productCategoryDetails.getId());
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }
        });
        alertDialogBuilder.setNegativeButton("No", (dialog, which) -> {

        });
        alertDialogBuilder.create().show();
    }

    private void openUpdateCategoryDialog(ProductCategoriesModel.ResultBean.SubCategoriesBean productCategoryDetails) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_layout_category, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialogBuilder.setView(promptView);

        final TextView tv_title = promptView.findViewById(R.id.tv_title);
        final EditText edt_category_name = promptView.findViewById(R.id.edt_category_name);
        final ImageButton ib_close = promptView.findViewById(R.id.ib_close);
        final MaterialButton btn_save = promptView.findViewById(R.id.btn_save);

        final AlertDialog alertD = alertDialogBuilder.create();

        tv_title.setText("Update Subcategory");
        edt_category_name.setText(productCategoryDetails.getProduct_category());

        btn_save.setOnClickListener(v -> {

            if (edt_category_name.getText().toString().trim().isEmpty()) {
                edt_category_name.setError("Please enter name");
                edt_category_name.requestFocus();
                return;
            }

            if (Utilities.isNetworkAvailable(context)) {
                alertD.dismiss();
                new UpdateCategory().execute(
                        edt_category_name.getText().toString().trim(),
                        productCategoryDetails.getBusiness_category_id(),
                        productCategoryDetails.getLevel(),
                        productCategoryDetails.getParent_id(),
                        userId,
                        productCategoryDetails.getId()
                );
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }
        });

        ib_close.setOnClickListener(v -> alertD.dismiss());

        alertD.show();
    }

    private class UpdateCategory extends AsyncTask<String, Void, String> {

        private ProgressDialog pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "updateProductCategory");
            obj.addProperty("name", params[0]);
            obj.addProperty("category_id", params[1]);
            obj.addProperty("level", params[2]);
            obj.addProperty("parent_id", params[3]);
            obj.addProperty("user_id", params[4]);
            obj.addProperty("id", params[5]);
            res = APICall.JSONAPICall(ApplicationConstants.PRODUCTCATEGORYAPI, obj.toString());
            return res.trim();
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
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("ProductCategoriesActivity"));
                        Utilities.showMessage(message, context, 1);
                    } else {
                        Utilities.showMessage(message, context, 3);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class DeleteCategory extends AsyncTask<String, Void, String> {

        private ProgressDialog pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "deleteProductCategory");
            obj.addProperty("id", params[0]);
            res = APICall.JSONAPICall(ApplicationConstants.PRODUCTCATEGORYAPI, obj.toString());
            return res.trim();
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
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("ProductCategoriesActivity"));
                        Utilities.showMessage(message, context, 1);
                    } else {
                        Utilities.showMessage(message, context, 3);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
