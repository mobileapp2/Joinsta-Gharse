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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.activities.AddProductSubCategoryActivity;
import in.oriange.joinstagharse.models.ProductCategoriesModel;
import in.oriange.joinstagharse.utilities.APICall;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.UserSessionManager;
import in.oriange.joinstagharse.utilities.Utilities;

public class ProductCategoryParentAdapter extends RecyclerView.Adapter<ProductCategoryParentAdapter.MyViewHolder> {

    private Context context;
    private List<ProductCategoriesModel.ResultBean> productCategoryList;
    private String userId;

    public ProductCategoryParentAdapter(Context context, List<ProductCategoriesModel.ResultBean> productCategoryList) {
        this.context = context;
        this.productCategoryList = productCategoryList;

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
        View view = inflater.inflate(R.layout.list_row_main_product_categories, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        ProductCategoriesModel.ResultBean productCategoryDetails = productCategoryList.get(position);

        holder.tv_main_category.setText(productCategoryDetails.getProduct_category());

        if (productCategoryDetails.getSub_categories().size() != 0) {
            holder.rv_sub_categories.setAdapter(new ProductCategoryChildAdapter(context, productCategoryDetails.getSub_categories()));
            holder.tv_sub_categories_not_available.setVisibility(View.GONE);
            holder.rv_sub_categories.setVisibility(View.VISIBLE);
        } else {
            holder.tv_sub_categories_not_available.setVisibility(View.VISIBLE);
            holder.rv_sub_categories.setVisibility(View.GONE);
        }

        holder.ib_add.setOnClickListener(v -> {
            context.startActivity(new Intent(context, AddProductSubCategoryActivity.class)
                    .putExtra("productCategoryDetails", productCategoryDetails));
        });

        holder.ib_edit.setOnClickListener(v -> {
            if (!productCategoryDetails.getCreated_by().equals("0"))
                openUpdateCategoryDialog(productCategoryDetails);
            else
                Utilities.showMessage("You cannot edit default category", context, 2);
        });

        holder.ib_delete.setOnClickListener(v -> {
            if (!productCategoryDetails.getCreated_by().equals("0"))
                openDeleteCategoryDialog(productCategoryDetails);
            else
                Utilities.showMessage("You cannot delete default category", context, 2);
        });

    }

    @Override
    public int getItemCount() {
        return productCategoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_main_category, tv_sub_categories_not_available;
        private ImageButton ib_add, ib_edit, ib_delete;
        private RecyclerView rv_sub_categories;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_main_category = itemView.findViewById(R.id.tv_main_category);
            tv_sub_categories_not_available = itemView.findViewById(R.id.tv_sub_categories_not_available);
            ib_add = itemView.findViewById(R.id.ib_add);
            ib_edit = itemView.findViewById(R.id.ib_edit);
            ib_delete = itemView.findViewById(R.id.ib_delete);
            rv_sub_categories = itemView.findViewById(R.id.rv_sub_categories);
            rv_sub_categories.setLayoutManager(new LinearLayoutManager(context));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void openDeleteCategoryDialog(ProductCategoriesModel.ResultBean productCategoryDetails) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setIcon(R.drawable.icon_alertred);
        alertDialogBuilder.setMessage("Are you sure you want to cancel this category?");
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

    private void openUpdateCategoryDialog(ProductCategoriesModel.ResultBean productCategoryDetails) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_layout_category, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialogBuilder.setView(promptView);

        final TextView tv_title = promptView.findViewById(R.id.tv_title);
        final EditText edt_category_name = promptView.findViewById(R.id.edt_category_name);
        final ImageButton ib_close = promptView.findViewById(R.id.ib_close);
        final MaterialButton btn_save = promptView.findViewById(R.id.btn_save);

        final AlertDialog alertD = alertDialogBuilder.create();

        tv_title.setText("Update Product Category");
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
