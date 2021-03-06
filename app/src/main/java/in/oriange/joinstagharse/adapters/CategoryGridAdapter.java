package in.oriange.joinstagharse.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.activities.BizProfEmpDetailsListActivity;
import in.oriange.joinstagharse.models.CategotyListModel;

public class CategoryGridAdapter extends RecyclerView.Adapter<CategoryGridAdapter.MyViewHolder> {

    private List<CategotyListModel> resultArrayList;
    private Context context;
    private String mainCategoryTypeId;

    public CategoryGridAdapter(Context context, List<CategotyListModel> resultArrayList, String mainCategoryTypeId) {
        this.context = context;
        this.resultArrayList = resultArrayList;
        this.mainCategoryTypeId = mainCategoryTypeId;
    }

    @Override
    public CategoryGridAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.grid_row_category, parent, false);
        return new CategoryGridAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CategoryGridAdapter.MyViewHolder holder, int pos) {
        final int position = holder.getAdapterPosition();
        final CategotyListModel categotyDetails = resultArrayList.get(position);

        holder.tv_categoty.setText(categotyDetails.getName());

        holder.cv_main_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                setUpSubcategoryData(holder, categotyDetails);
                context.startActivity(new Intent(context, BizProfEmpDetailsListActivity.class)
                        .putExtra("mainCategoryTypeId", mainCategoryTypeId)
                        .putExtra("categoryTypeId", categotyDetails.getId())
                        .putExtra("subCategoryTypeId", "NA")
                        .putExtra("categoryTypeName", categotyDetails.getName()));
            }
        });

        if (!categotyDetails.getCategory_icon().trim().isEmpty()) {
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
                    .load(categotyDetails.getCategory_icon().trim())
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
        private TextView tv_categoty;

        public MyViewHolder(View view) {
            super(view);
            cv_main_row = view.findViewById(R.id.cv_main_row);
            imv_category = view.findViewById(R.id.imv_category);
            tv_categoty = view.findViewById(R.id.tv_categoty);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}

