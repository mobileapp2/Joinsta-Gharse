package in.oriange.eorder.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.oriange.eorder.R;
import in.oriange.eorder.activities.BookOrderCartProductsActivity;
import in.oriange.eorder.activities.SelectCityActivity;
import in.oriange.eorder.adapters.BannerSliderAdapter;
import in.oriange.eorder.adapters.CategoryAdapter;
import in.oriange.eorder.adapters.CategoryGridAdapter;
import in.oriange.eorder.models.BannerListModel;
import in.oriange.eorder.models.BookOrderGetMyOrdersModel;
import in.oriange.eorder.models.CategotyListModel;
import in.oriange.eorder.models.CategotyListPojo;
import in.oriange.eorder.utilities.APICall;
import in.oriange.eorder.utilities.ApplicationConstants;
import in.oriange.eorder.utilities.LinearLayoutPagerManager;
import in.oriange.eorder.utilities.UserSessionManager;
import in.oriange.eorder.utilities.Utilities;

public class HomeFragment extends Fragment {

    private Context context;
    //    private AppCompatEditText edt_location;
    private UserSessionManager session;
    private RecyclerView rv_popular_category, rv_main_category;
    private ImageButton ib_location, ib_notifications, ib_cart;
    private TextView tv_cart_count;
    private LinearLayout ll_categories;
    private EditText edt_search;
    private CardView cv_banner;
    private SliderView imageSlider;
    private SpinKitView progressBar;
    private String categoryTypeId, userId;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        context = getActivity();
        init(rootView);
        setDefault();
        getSessionDetails();
        setEventHandler();
        return rootView;
    }

    private void init(View rootView) {
//        edt_location = rootView.findViewById(R.id.edt_location);
        session = new UserSessionManager(context);
        cv_banner = rootView.findViewById(R.id.cv_banner);
        imageSlider = rootView.findViewById(R.id.imageSlider);

        edt_search = rootView.findViewById(R.id.edt_search);
        progressBar = rootView.findViewById(R.id.progressBar);
        ll_categories = rootView.findViewById(R.id.ll_categories);
        ib_location = rootView.findViewById(R.id.ib_location);
        ib_notifications = rootView.findViewById(R.id.ib_notifications);
        ib_cart = rootView.findViewById(R.id.ib_cart);
        tv_cart_count = rootView.findViewById(R.id.tv_cart_count);
        rv_popular_category = rootView.findViewById(R.id.rv_popular_category);
        rv_main_category = rootView.findViewById(R.id.rv_main_category);
        rv_popular_category.setLayoutManager(new LinearLayoutPagerManager(context, LinearLayoutManager.HORIZONTAL, false, 4));
        rv_popular_category.setHasFixedSize(true);
        rv_main_category.setLayoutManager(new GridLayoutManager(context, 3));

    }

    private void setDefault() {
        categoryTypeId = "1";
        if (Utilities.isNetworkAvailable(context)) {
            new GetBanners().execute(categoryTypeId, new UserSessionManager(context).getLocation().get(ApplicationConstants.KEY_LOCATION_INFO));
            new GetCategotyList().execute("0", "0", categoryTypeId);
            new GetOrders().execute();
        }
    }

    private void getSessionDetails() {
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            userId = json.getString("userid");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setEventHandler() {
        ib_location.setOnClickListener(v -> {
            startActivity(new Intent(context, SelectCityActivity.class)
                    .putExtra("requestCode", 0));
        });

        ib_cart.setOnClickListener(v -> {
            startActivity(new Intent(context, BookOrderCartProductsActivity.class));
        });

        edt_search.setOnClickListener(v -> {

        });
    }

    private class GetBanners extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getCategoryTypeBanners");
            obj.addProperty("category_type_id", params[0]);
            obj.addProperty("location", params[1]);
            res = APICall.JSONAPICall(ApplicationConstants.BANNERSAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type;
            try {
                if (!result.equals("")) {
                    List<BannerListModel.ResultBean> bannerList = new ArrayList<>();
                    BannerListModel pojoDetails = new Gson().fromJson(result, BannerListModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        bannerList = pojoDetails.getResult();
                        if (bannerList.size() > 0) {
                            cv_banner.setVisibility(View.VISIBLE);

                            BannerSliderAdapter adapter = new BannerSliderAdapter(context, bannerList);
                            imageSlider.setSliderAdapter(adapter);
                            imageSlider.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                            imageSlider.setSliderTransformAnimation(SliderAnimations.VERTICALFLIPTRANSFORMATION);
                            imageSlider.setIndicatorSelectedColor(Color.WHITE);
                            imageSlider.setIndicatorUnselectedColor(Color.GRAY);
                            imageSlider.setAutoCycle(true);
                            imageSlider.setScrollTimeInSec(10);
                        }
                    } else {
                        cv_banner.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                cv_banner.setVisibility(View.GONE);
            }
        }
    }

    private class GetCategotyList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
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
            progressBar.setVisibility(View.GONE);
            ll_categories.setVisibility(View.VISIBLE);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    ArrayList<CategotyListModel> categotyList = new ArrayList<>();
                    CategotyListPojo pojoDetails = new Gson().fromJson(result, CategotyListPojo.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        categotyList = pojoDetails.getResult();
                        if (categotyList.size() > 0) {
                            rv_popular_category.setAdapter(new CategoryAdapter(context, categotyList, categoryTypeId));
                            rv_main_category.setAdapter(new CategoryGridAdapter(context, categotyList, categoryTypeId));
//                            Drawable horizontalDivider = ContextCompat.getDrawable(context, R.drawable.line_divider);
//                            Drawable verticalDivider = ContextCompat.getDrawable(context, R.drawable.line_divider);
//                            rv_main_category.addItemDecoration(new GridDividerItemDecoration(horizontalDivider, verticalDivider, 3));
                        }
                    } else {
                        rv_popular_category.setAdapter(new CategoryAdapter(context, categotyList, categoryTypeId));
                        rv_main_category.setAdapter(new CategoryGridAdapter(context, categotyList, categoryTypeId));
                        Utilities.showAlertDialog(context, "Categories not available", false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showAlertDialog(context, "Server Not Responding", false);
            }
        }
    }

    private class GetOrders extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getAllOrders");
            obj.addProperty("user_id", userId);
            res = APICall.JSONAPICall(ApplicationConstants.BOOKORDERAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    BookOrderGetMyOrdersModel pojoDetails = new Gson().fromJson(result, BookOrderGetMyOrdersModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {

                        int numberOfProducts = 0;

                        List<BookOrderGetMyOrdersModel.ResultBean> ordersList = pojoDetails.getResult();
                        for (BookOrderGetMyOrdersModel.ResultBean orderDetail : ordersList)
                            if (orderDetail.getStatus_details().size() == 1)
                                if (orderDetail.getStatus_details().get(0).getStatus().equals("1"))
                                    numberOfProducts = numberOfProducts + orderDetail.getProduct_details().size();


                        if (numberOfProducts != 0) {
                            tv_cart_count.setVisibility(View.VISIBLE);
                            tv_cart_count.setText(String.valueOf(numberOfProducts));
                        } else {
                            tv_cart_count.setVisibility(View.GONE);
                            tv_cart_count.setText("");
                        }
                    } else {
                        tv_cart_count.setVisibility(View.GONE);
                        tv_cart_count.setText("");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                tv_cart_count.setVisibility(View.GONE);
                tv_cart_count.setText("");
            }
        }
    }

}
