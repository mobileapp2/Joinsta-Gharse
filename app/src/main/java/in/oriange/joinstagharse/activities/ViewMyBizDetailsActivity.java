package in.oriange.joinstagharse.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.lujun.androidtagview.TagContainerLayout;
import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.models.GetBusinessModel;
import in.oriange.joinstagharse.models.RatingAndReviewModel;
import in.oriange.joinstagharse.utilities.APICall;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.DownloadFileAndMessageShare;
import in.oriange.joinstagharse.utilities.UserSessionManager;
import in.oriange.joinstagharse.utilities.Utilities;

import static in.oriange.joinstagharse.utilities.ApplicationConstants.IMAGE_LINK;
import static in.oriange.joinstagharse.utilities.Utilities.changeStatusBar;

public class ViewMyBizDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.ll_nopreview)
    LinearLayout llNopreview;
    @BindView(R.id.imv_image)
    ImageView imvImage;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.imv_share)
    ImageView imvShare;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_total_rating)
    TextView tvTotalRating;
    @BindView(R.id.tv_nature)
    TextView tvNature;
    @BindView(R.id.tv_designation)
    TextView tvDesignation;
    @BindView(R.id.cv_add_offer)
    CardView cvAddOffer;
    @BindView(R.id.cv_view_offer)
    CardView cvViewOffer;
    @BindView(R.id.cv_view_orders)
    CardView cvViewOrders;
    @BindView(R.id.cv_products)
    CardView cvProducts;
    @BindView(R.id.container_tags)
    TagContainerLayout containerTags;
    @BindView(R.id.cv_tabs)
    CardView cvTabs;
    @BindView(R.id.rv_mobilenos)
    RecyclerView rvMobilenos;
    @BindView(R.id.tv_email)
    TextView tvEmail;
    @BindView(R.id.tv_website)
    TextView tvWebsite;
    @BindView(R.id.cv_contact_details)
    CardView cvContactDetails;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.cv_address)
    CardView cvAddress;
    @BindView(R.id.ib_email)
    ImageButton ibEmail;
    @BindView(R.id.ll_email_1)
    LinearLayout llEmail1;
    @BindView(R.id.ib_website)
    ImageButton ibWebsite;
    @BindView(R.id.ll_website)
    LinearLayout llWebsite;
    @BindView(R.id.ll_address)
    LinearLayout llAddress;
    @BindView(R.id.ib_address)
    ImageButton ibAddress;

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private GetBusinessModel.ResultBean searchDetails;
    private String userId;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_biz_details);
        ButterKnife.bind(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        init();
        setDefault();
        getSessionDetails();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = ViewMyBizDetailsActivity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);
        changeStatusBar(context, getWindow());
        rvMobilenos.setLayoutManager(new LinearLayoutManager(context));
    }

    private void setDefault() {
        searchDetails = (GetBusinessModel.ResultBean) getIntent().getSerializableExtra("searchDetails");

        if (!searchDetails.getImage_url().trim().isEmpty()) {
            String url = IMAGE_LINK + "" + searchDetails.getCreated_by() + "/" + searchDetails.getImage_url();
            Picasso.with(context)
                    .load(url)
                    .into(imvImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            llNopreview.setVisibility(View.GONE);
                            imvImage.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
//                            Utilities.changeStatusBar(context, getWindow());
//                            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                        }

                        @Override
                        public void onError() {
                            imvImage.setVisibility(View.GONE);
                            llNopreview.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        } else {
            imvImage.setVisibility(View.GONE);
            llNopreview.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }

        if (!searchDetails.getBusiness_name().trim().isEmpty())
            tvName.setText(searchDetails.getBusiness_code() + " - " + searchDetails.getBusiness_name());
        else
            tvName.setVisibility(View.GONE);

        if (searchDetails.getTotal_number_review().equals("0")) {
            tvTotalRating.setVisibility(View.GONE);
        } else {
            float averageRating = Float.parseFloat(searchDetails.getAvg_rating());
            averageRating = Float.parseFloat(new DecimalFormat("#.#").format(averageRating));

            tvTotalRating.setText(String.valueOf(averageRating) + "\u2605");
            if (averageRating >= 4 && averageRating <= 5)
                tvTotalRating.setBackground(context.getResources().getDrawable(R.drawable.button_focusfilled_green));
            else if (averageRating >= 3 && averageRating <= 4.9)
                tvTotalRating.setBackground(context.getResources().getDrawable(R.drawable.button_focusfilled_orange));
            else if (averageRating >= 2 && averageRating <= 3.9)
                tvTotalRating.setBackground(context.getResources().getDrawable(R.drawable.button_focusfilled_yellow));
            else if (averageRating >= 1 && averageRating <= 1.9)
                tvTotalRating.setBackground(context.getResources().getDrawable(R.drawable.button_focusfilled_red));
        }

        if (!searchDetails.getTypeSubTypeName().equals(""))
            tvNature.setText(searchDetails.getTypeSubTypeName());
        else
            tvNature.setVisibility(View.GONE);

        if (!searchDetails.getDesignation().trim().isEmpty())
            tvDesignation.setText(searchDetails.getDesignation());
        else
            tvDesignation.setVisibility(View.GONE);

        if (searchDetails.getSubTypesTagsList("2").size() != 0)
            containerTags.setTags(searchDetails.getSubTypesTagsList("2"));
        else
            cvTabs.setVisibility(View.GONE);

        if ((searchDetails.getMobiles().get(0) == null || searchDetails.getMobiles().get(0).size() == 0)
                && searchDetails.getEmail().trim().isEmpty()
                && searchDetails.getWebsite().trim().isEmpty()) {
            cvContactDetails.setVisibility(View.GONE);
        } else {
            if (searchDetails.getMobiles().get(0) != null)
                if (searchDetails.getMobiles().get(0).size() > 0)
                    rvMobilenos.setAdapter(new MobileNumbersAdapter(searchDetails.getMobiles().get(0)));
                else
                    rvMobilenos.setVisibility(View.GONE);
            else
                rvMobilenos.setVisibility(View.GONE);

            if (!searchDetails.getEmail().trim().isEmpty())
                tvEmail.setText(searchDetails.getEmail());
            else
                llEmail1.setVisibility(View.GONE);

            if (!searchDetails.getWebsite().trim().isEmpty())
                tvWebsite.setText(searchDetails.getWebsite());
            else
                llWebsite.setVisibility(View.GONE);
        }

        if (searchDetails.getAddress().trim().isEmpty() && (searchDetails.getLatitude().trim().isEmpty() || searchDetails.getLongitude().trim().isEmpty())) {
            cvAddress.setVisibility(View.GONE);
        } else {
            if (!searchDetails.getAddress().trim().isEmpty())
                tvAddress.setText(searchDetails.getAddress());
            else
                llAddress.setVisibility(View.GONE);
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
        imvShare.setOnClickListener(v -> shareDetails());

        ibEmail.setOnClickListener(v -> {
            if (!searchDetails.getEmail().trim().isEmpty()) {
                sendEmail();
            } else {
                Utilities.showMessage("Email not added", context, 2);
            }
        });

        ibWebsite.setOnClickListener(v -> {
            String url = searchDetails.getWebsite();

            if (!url.startsWith("https://") || !url.startsWith("http://")) {
                url = "http://" + url;
            }
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        ibAddress.setOnClickListener(v -> {
            if (searchDetails.getLatitude().trim().isEmpty() || searchDetails.getLongitude().trim().isEmpty()) {
                Utilities.showMessage("Location not added", context, 2);
                return;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=&daddr=" + searchDetails.getLatitude() + "," + searchDetails.getLongitude()));
            startActivity(intent);
        });


        tvTotalRating.setOnClickListener(v -> {
            if (Utilities.isNetworkAvailable(context))
                new GetRatingsAndReviews().execute("1", searchDetails.getId());
            else
                Utilities.showMessage("Please check your internet connection", context, 2);
        });

//        cvAddOffer.setOnClickListener(v ->
//                startActivity(new Intent(context, BookOrderBusinessOwnerOrdersActivity.class)
//                        .putExtra("businessId", searchDetails.getId()))
//        );

//        cvViewOffer.setOnClickListener(v ->
//                startActivity(new Intent(context, BookOrderBusinessOwnerOrdersActivity.class)
//                        .putExtra("businessId", searchDetails.getId()))
//        );

        cvViewOrders.setOnClickListener(v ->
                startActivity(new Intent(context, BookOrderBusinessOwnerOrdersActivity.class)
                        .putExtra("businessId", searchDetails.getId()))
        );

        cvProducts.setOnClickListener(v ->
                startActivity(new Intent(context, BusinessProductsActivity.class)
                        .putExtra("businessId", searchDetails.getId()))
        );

    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_backarrow_black);
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (searchDetails.getLatitude().trim().isEmpty() || searchDetails.getLongitude().trim().isEmpty()) {
            Objects.requireNonNull(mapFragment.getView()).setVisibility(View.GONE);
        } else {
            LatLng latLng = new LatLng(Double.parseDouble(searchDetails.getLatitude().trim()),
                    Double.parseDouble(searchDetails.getLongitude().trim()));
            mMap.addMarker(new MarkerOptions().position(latLng));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)
                    .zoom(10).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menus_edit_delete, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setMessage("Are you sure you want to delete this item?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.icon_alertred);
                builder.setCancelable(false);
                builder.setPositiveButton("YES", (dialog, id) -> {
                    if (Utilities.isNetworkAvailable(context))
                        new DeleteBusiness().execute();
                    else
                        Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                });
                builder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
                AlertDialog alertD = builder.create();
                alertD.show();
                break;
            case R.id.action_edit:
                startActivity(new Intent(context, EditBusinessActivity.class)
                        .putExtra("searchDetails", searchDetails));
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    private void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", searchDetails.getEmail(), null));
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    private void shareDetails() {
        if (!searchDetails.getImage_url().equals("")) {
            new DownloadFileAndMessageShare(context, "Business", IMAGE_LINK + searchDetails.getCreated_by() + "/" + searchDetails.getImage_url(), businessDetails());
        } else {
            String message = businessDetails();
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, message);
            context.startActivity(Intent.createChooser(sharingIntent, "Choose from following"));
        }
    }

    private String businessDetails() {
        StringBuilder sb = new StringBuilder();

        if (!searchDetails.getBusiness_name().equals("")) {
            sb.append("Business Name - " + searchDetails.getBusiness_code() + " - " + searchDetails.getBusiness_name() + "\n");
        }

        if (!searchDetails.getTypeSubTypeName().equals("")) {
            sb.append("Nature of Business - " + searchDetails.getType_description() + "/" + searchDetails.getTypeSubTypeName() + "\n");
        } else {
            sb.append("Nature of Business - " + searchDetails.getType_description() + "\n");
        }

        if (searchDetails.getTag().get(0) != null)
            if (searchDetails.getTag().get(0).size() != 0) {
                StringBuilder tags = new StringBuilder();
                for (int i = 0; i < searchDetails.getTag().get(0).size(); i++) {
                    tags.append(searchDetails.getTag().get(0).get(i).getTag_name() + ", ");
                }

                sb.append("Products - " + tags.toString().substring(0, tags.toString().length() - 2) + "\n");
            }

        if (!searchDetails.getAddress().equals("")) {
            sb.append("Address - " + searchDetails.getAddress() + "\n");
        }

        if (searchDetails.getMobiles().get(0) != null)
            if (searchDetails.getMobiles().get(0).size() != 0) {
                StringBuilder mobile = new StringBuilder();
                for (int i = 0; i < searchDetails.getMobiles().get(0).size(); i++) {
                    mobile.append(searchDetails.getMobiles().get(0).get(i).getMobile_number() + ", ");
                }

                sb.append("Mobile - " + mobile.toString().substring(0, mobile.toString().length() - 2) + "\n");
            }

        if (!searchDetails.getEmail().equals("")) {
            sb.append("Email - " + searchDetails.getEmail() + "\n");
        }

        if (!searchDetails.getLatitude().equals("") || !searchDetails.getLongitude().equals("")) {
            sb.append("Location - " + "https://www.google.com/maps/?q="
                    + searchDetails.getLatitude() + "," + searchDetails.getLongitude() + "\n");

        }

        if (!searchDetails.getWebsite().equals("")) {
            sb.append("Website - " + searchDetails.getWebsite() + "\n");
        }

        return sb.toString() + "\n" + "Joinsta Gharse\n" + "Click Here - " + ApplicationConstants.JOINSTA_PLAYSTORELINK;

    }

    public class MobileNumbersAdapter extends RecyclerView.Adapter<MobileNumbersAdapter.MyViewHolder> {

        private List<GetBusinessModel.ResultBean.MobilesBean> resultArrayList;

        public MobileNumbersAdapter(List<GetBusinessModel.ResultBean.MobilesBean> resultArrayList) {
            this.resultArrayList = resultArrayList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_mobile, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int pos) {
            final int position = holder.getAdapterPosition();
            final GetBusinessModel.ResultBean.MobilesBean searchDetails = resultArrayList.get(position);

            holder.tv_mobile.setText(searchDetails.getCountry_code() + searchDetails.getMobile_number());

            holder.tv_mobile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                    builder.setMessage("Are you sure you want to make a call?");
                    builder.setTitle("Alert");
                    builder.setIcon(R.drawable.icon_call);
                    builder.setCancelable(false);
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(Intent.ACTION_CALL,
                                    Uri.parse("tel:" + searchDetails.getCountry_code() + searchDetails.getMobile_number())));
                        }
                    });
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertD = builder.create();
                    alertD.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return resultArrayList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView tv_mobile;

            public MyViewHolder(View view) {
                super(view);
                tv_mobile = view.findViewById(R.id.tv_mobile);
            }
        }
    }

    public class DeleteBusiness extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "deletebusiness");
            obj.addProperty("business_id", searchDetails.getId());
            res = APICall.JSONAPICall(ApplicationConstants.BUSINESSAPI, obj.toString());
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
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("MyBusinessActivity"));
                        Utilities.showMessage(message, context, 1);
                        finish();
                    } else {
                        Utilities.showMessage(message, context, 2);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class GetRatingsAndReviews extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "GetProfileRating");
            obj.addProperty("category_type_id", params[0]);
            obj.addProperty("record_id", params[1]);
            res = APICall.JSONAPICall(ApplicationConstants.RATINGANDREVIEWAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "";
            try {
                if (!result.equals("")) {
                    RatingAndReviewModel pojoDetails = new Gson().fromJson(result, RatingAndReviewModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        startActivity(new Intent(context, RatingAndReviewListActivity.class)
                                .putExtra("recordId", searchDetails.getId())
                                .putExtra("profileName", tvName.getText().toString().trim())
                                .putExtra("reviewResult", result)
                                .putExtra("categoryTypeId", "1"));

                    } else {
                        Utilities.showAlertDialog(context, "Ratings and reviews not available", false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showAlertDialog(context, "Ratings and reviews not available", false);
            }
        }
    }

}
