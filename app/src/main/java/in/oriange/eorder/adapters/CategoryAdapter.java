package in.oriange.eorder.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import in.oriange.eorder.R;
import in.oriange.eorder.activities.BizProfEmpDetailsListActivity;
import in.oriange.eorder.models.CategotyListModel;
import in.oriange.eorder.models.SubCategotyListModel;
import in.oriange.eorder.models.SubCategotyListPojo;
import in.oriange.eorder.utilities.APICall;
import in.oriange.eorder.utilities.ApplicationConstants;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    private List<CategotyListModel> resultArrayList;
    private Context context;
    private ArrayList<SubCategotyListModel> subCategoryList;
    private ImageButton imv_arrow1;
    private String mainCategoryTypeId;
    private RecyclerView rv_sub_catrgory1;
    private SpinKitView progressBar1;
    private TextView tv_subcst_notavailable1;


    public CategoryAdapter(Context context, List<CategotyListModel> resultArrayList, String mainCategoryTypeId) {
        this.context = context;
        this.resultArrayList = resultArrayList;
        this.mainCategoryTypeId = mainCategoryTypeId;
        subCategoryList = new ArrayList<>();
    }

    @Override
    public CategoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.hori_row_category, parent, false);
        return new CategoryAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CategoryAdapter.MyViewHolder holder, int pos) {
        final int position = holder.getAdapterPosition();
        final CategotyListModel categotyDetails = resultArrayList.get(position);

        holder.tv_categoty.setText(categotyDetails.getName());

        holder.cv_main_row.setOnClickListener(v -> {
            context.startActivity(new Intent(context, BizProfEmpDetailsListActivity.class)
                    .putExtra("mainCategoryTypeId", mainCategoryTypeId)
                    .putExtra("categoryTypeId", categotyDetails.getId())
                    .putExtra("subCategoryTypeId", "NA")
                    .putExtra("categoryTypeName", categotyDetails.getName()));
        });

        if (!categotyDetails.getPopular_category_icon().trim().isEmpty()) {
//            Picasso.with(context)
//                    .load(categotyDetails.getCategory_icon().trim())
//                    .into(holder.imv_category, new Callback() {
//                        @Override
//                        public void onSuccess() {
//
//                        }
//
//                        @Override
//                        public void onError() {
//                            holder.imv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_preview));
//                        }
//                    });

            Glide.with(context)
                    .load(categotyDetails.getPopular_category_icon().trim())
//                    .placeholder(context.getResources().getDrawable(R.drawable.icon_preview))
                    .into(holder.imv_category);
        } else {
            holder.imv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_preview));
        }

    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_main_row;
        private ImageView imv_category;
        //        private ImageButton imv_arrow;
        private TextView tv_categoty;
//        private RecyclerView rv_sub_catrgory;
//        private SpinKitView progressBar;
//        private TextView tv_subcst_notavailable;

        public MyViewHolder(View view) {
            super(view);
            cv_main_row = view.findViewById(R.id.cv_main_row);
            imv_category = view.findViewById(R.id.imv_category);
//            imv_arrow = view.findViewById(R.id.imv_arrow);
            tv_categoty = view.findViewById(R.id.tv_categoty);
//            rv_sub_catrgory = view.findViewById(R.id.rv_sub_catrgory);
//            progressBar = view.findViewById(R.id.progressBar);
//            tv_subcst_notavailable = view.findViewById(R.id.tv_subcst_notavailable);
        }
    }

    private void animateExpand() {
        RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        rotate.setInterpolator(new LinearInterpolator());
        imv_arrow1.startAnimation(rotate);
    }

    private void animateCollapse() {
        RotateAnimation rotate = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        rotate.setInterpolator(new LinearInterpolator());
        imv_arrow1.startAnimation(rotate);
    }

    public class GetSubCategotyList extends AsyncTask<String, Void, String> {
        String categoryTypeId = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar1.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            categoryTypeId = params[0];
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getcategory");
            obj.addProperty("parent_id", params[0]);
            obj.addProperty("level", params[1]);
            obj.addProperty("category_type_id", params[2]);
            res = APICall.JSONAPICall(ApplicationConstants.CATEGORYAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar1.setVisibility(View.GONE);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    SubCategotyListPojo pojoDetails = new Gson().fromJson(result, SubCategotyListPojo.class);
                    type = pojoDetails.getType();
                    message = pojoDetails.getMessage();

                    if (type.equalsIgnoreCase("success")) {
                        subCategoryList = pojoDetails.getResult();
                        if (subCategoryList.size() > 0) {
                            rv_sub_catrgory1.setVisibility(View.VISIBLE);
                            tv_subcst_notavailable1.setVisibility(View.GONE);
                            rv_sub_catrgory1.setLayoutManager(new LinearLayoutManager(context));
                            rv_sub_catrgory1.setAdapter(new SubCategoryAdapter(context, subCategoryList, mainCategoryTypeId, categoryTypeId));
                        }
                    } else {
                        rv_sub_catrgory1.setVisibility(View.GONE);
                        tv_subcst_notavailable1.setVisibility(View.VISIBLE);
                    }
                }
            } catch (Exception e) {
                rv_sub_catrgory1.setVisibility(View.GONE);
                tv_subcst_notavailable1.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}

