package in.oriange.eorder.activities;

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
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;
import in.oriange.eorder.R;
import in.oriange.eorder.models.GetBusinessModel;
import in.oriange.eorder.models.RatingAndReviewModel;
import in.oriange.eorder.utilities.APICall;
import in.oriange.eorder.utilities.ApplicationConstants;
import in.oriange.eorder.utilities.UserSessionManager;
import in.oriange.eorder.utilities.Utilities;

import static in.oriange.eorder.utilities.ApplicationConstants.IMAGE_LINK;

public class ViewMyBizDetailsActivity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private ImageView imv_image;
    private ImageButton imb_share_tax, imb_share_bank;
    private ProgressBar progressBar;
    private LinearLayout ll_nopreview;
    private TextView tv_name, tv_nature, tv_designation, tv_email, tv_website, tv_address, tv_tax_alias,
            tv_pan, tv_gst, tv_accholder_name, tv_bank_alias, tv_bank_name, tv_acc_no, tv_ifsc, tv_order_online,
            tv_total_rating, tv_total_reviews;
    private ImageView imv_share;
    private RelativeLayout rl_rating;
    private RatingBar rb_feedback_stars;
    private CardView cv_tabs, cv_contact_details, cv_address, cv_tax, cv_bank, cv_texbank_notice, cv_add_offer,
            cv_view_offer, cv_order_online, cv_view_orders, cv_dummy;
    private TagContainerLayout container_tags;
    private RecyclerView rv_mobilenos;

    private GetBusinessModel.ResultBean searchDetails;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewmy_bizdetails);

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

        ll_nopreview = findViewById(R.id.ll_nopreview);
        imv_image = findViewById(R.id.imv_image);
        progressBar = findViewById(R.id.progressBar);
        cv_tabs = findViewById(R.id.cv_tabs);
        cv_contact_details = findViewById(R.id.cv_contact_details);
        cv_address = findViewById(R.id.cv_address);
        cv_tax = findViewById(R.id.cv_tax);
        cv_bank = findViewById(R.id.cv_bank);
        cv_texbank_notice = findViewById(R.id.cv_texbank_notice);
        cv_add_offer = findViewById(R.id.cv_add_offer);
        cv_view_offer = findViewById(R.id.cv_view_offer);
        cv_order_online = findViewById(R.id.cv_order_online);
        cv_view_orders = findViewById(R.id.cv_view_orders);
        cv_dummy = findViewById(R.id.cv_dummy);

        imb_share_tax = findViewById(R.id.imb_share_tax);
        imb_share_bank = findViewById(R.id.imb_share_bank);

        tv_name = findViewById(R.id.tv_name);
        tv_nature = findViewById(R.id.tv_nature);
        tv_designation = findViewById(R.id.tv_designation);
        tv_email = findViewById(R.id.tv_email);
        tv_website = findViewById(R.id.tv_website);
        tv_address = findViewById(R.id.tv_address);
        tv_tax_alias = findViewById(R.id.tv_tax_alias);
        tv_pan = findViewById(R.id.tv_pan);
        tv_gst = findViewById(R.id.tv_gst);
        tv_accholder_name = findViewById(R.id.tv_accholder_name);
        tv_bank_alias = findViewById(R.id.tv_bank_alias);
        tv_bank_name = findViewById(R.id.tv_bank_name);
        tv_acc_no = findViewById(R.id.tv_acc_no);
        tv_ifsc = findViewById(R.id.tv_ifsc);
        tv_order_online = findViewById(R.id.tv_order_online);
        imv_share = findViewById(R.id.imv_share);

        tv_total_rating = findViewById(R.id.tv_total_rating);
        tv_total_reviews = findViewById(R.id.tv_total_reviews);
        rl_rating = findViewById(R.id.rl_rating);
        rb_feedback_stars = findViewById(R.id.rb_feedback_stars);

        container_tags = findViewById(R.id.container_tags);
        rv_mobilenos = findViewById(R.id.rv_mobilenos);
        rv_mobilenos.setLayoutManager(new LinearLayoutManager(context));
    }

    private void setDefault() {
        searchDetails = (GetBusinessModel.ResultBean) getIntent().getSerializableExtra("searchDetails");

        if (!searchDetails.getImage_url().trim().isEmpty()) {
            String url = IMAGE_LINK + "" + searchDetails.getCreated_by() + "/" + searchDetails.getImage_url();
            Picasso.with(context)
                    .load(url)
                    .into(imv_image, new Callback() {
                        @Override
                        public void onSuccess() {
                            ll_nopreview.setVisibility(View.GONE);
                            imv_image.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            imv_image.setVisibility(View.GONE);
                            ll_nopreview.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        } else {
            imv_image.setVisibility(View.GONE);
            ll_nopreview.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }

        if (!searchDetails.getBusiness_name().trim().isEmpty()) {
            tv_name.setText(searchDetails.getBusiness_code() + " - " + searchDetails.getBusiness_name());
        } else {
            tv_name.setVisibility(View.GONE);
        }

        if (!searchDetails.getType_description().trim().isEmpty() && !searchDetails.getSubtype_description().trim().isEmpty()) {
            tv_nature.setText(searchDetails.getType_description() + ", " + searchDetails.getSubtype_description());
        } else if (searchDetails.getType_description().trim().isEmpty() && searchDetails.getSubtype_description().trim().isEmpty()) {
            tv_nature.setVisibility(View.GONE);
        } else if (!searchDetails.getType_description().trim().isEmpty()) {
            tv_nature.setText(searchDetails.getType_description());
        } else if (!searchDetails.getSubtype_description().trim().isEmpty()) {
            tv_nature.setText(searchDetails.getSubtype_description());
        }

        if (!searchDetails.getDesignation().trim().isEmpty()) {
            tv_designation.setText(searchDetails.getDesignation());
        } else {
            tv_designation.setVisibility(View.GONE);
        }

        if ((searchDetails.getMobiles().get(0) == null) && searchDetails.getEmail().trim().isEmpty() && searchDetails.getWebsite().trim().isEmpty()) {
            cv_contact_details.setVisibility(View.GONE);
        } else {

            if (searchDetails.getMobiles().get(0) != null) {
                if (searchDetails.getMobiles().get(0).size() > 0) {
                    rv_mobilenos.setAdapter(new MobileNumbersAdapter(searchDetails.getMobiles().get(0)));
                } else {
                    rv_mobilenos.setVisibility(View.GONE);
                }
            } else {
                rv_mobilenos.setVisibility(View.GONE);
            }

            if (!searchDetails.getEmail().trim().isEmpty()) {
                tv_email.setText(searchDetails.getEmail());
            } else {
                tv_email.setVisibility(View.GONE);
            }

            if (!searchDetails.getWebsite().trim().isEmpty()) {
                tv_website.setText(searchDetails.getWebsite());
            } else {
                tv_website.setVisibility(View.GONE);
            }
        }

        if (searchDetails.getTag().get(0) != null) {
            if (searchDetails.getTag().get(0).size() > 0) {
                for (int i = 0; i < searchDetails.getTag().get(0).size(); i++) {

                    if (!searchDetails.getTag().get(0).get(i).getTag_name().trim().equals("")) {
                        container_tags.addTag(searchDetails.getTag().get(0).get(i).getTag_name());
                    }
                }
            } else {
                cv_tabs.setVisibility(View.GONE);
            }
        } else {
            cv_tabs.setVisibility(View.GONE);
        }

        if (!searchDetails.getAddress().trim().isEmpty()) {
            tv_address.setText(searchDetails.getAddress());
        } else {
            cv_address.setVisibility(View.GONE);
        }

//        if (!searchDetails.getTax_alias().trim().isEmpty() ||
//                !searchDetails.getPan_number().trim().isEmpty() ||
//                !searchDetails.getGst_number().trim().isEmpty()) {
//
//            cv_texbank_notice.setVisibility(View.VISIBLE);
//
//            if (!searchDetails.getTax_alias().trim().isEmpty()) {
//                tv_tax_alias.setText("Alias - " + searchDetails.getTax_alias());
//            } else {
//                tv_tax_alias.setVisibility(View.GONE);
//            }
//
//            if (!searchDetails.getPan_number().trim().isEmpty()) {
//                tv_pan.setText("PAN - " + searchDetails.getPan_number());
//            } else {
//                tv_pan.setVisibility(View.GONE);
//            }
//
//            if (!searchDetails.getGst_number().trim().isEmpty()) {
//                tv_gst.setText("GST - " + searchDetails.getGst_number());
//            } else {
//                tv_gst.setVisibility(View.GONE);
//            }
//
//        } else {
        cv_tax.setVisibility(View.GONE);
//        }

//        if (!searchDetails.getAccount_holder_name().trim().isEmpty() ||
//                !searchDetails.getBank_alias().trim().isEmpty() ||
//                !searchDetails.getBank_name().trim().isEmpty() ||
//                !searchDetails.getAccount_no().trim().isEmpty() ||
//                !searchDetails.getIfsc_code().trim().isEmpty()) {
//
//            cv_texbank_notice.setVisibility(View.VISIBLE);
//
//            if (!searchDetails.getAccount_holder_name().trim().isEmpty()) {
//                tv_accholder_name.setText(searchDetails.getAccount_holder_name());
//            } else {
//                tv_accholder_name.setVisibility(View.GONE);
//            }
//
//            if (!searchDetails.getBank_alias().trim().isEmpty()) {
//                tv_bank_alias.setText("Alias - " + searchDetails.getBank_alias());
//            } else {
//                tv_bank_alias.setVisibility(View.GONE);
//            }
//
//            if (!searchDetails.getBank_name().trim().isEmpty()) {
//                tv_bank_name.setText("Bank Name - " + searchDetails.getBank_name());
//            } else {
//                tv_bank_name.setVisibility(View.GONE);
//            }
//
//            if (!searchDetails.getAccount_no().trim().isEmpty()) {
//                tv_acc_no.setText("Acc. No. - " + searchDetails.getAccount_no());
//            } else {
//                tv_acc_no.setVisibility(View.GONE);
//            }
//
//            if (!searchDetails.getIfsc_code().trim().isEmpty()) {
//                tv_ifsc.setText("IFSC Code - " + searchDetails.getIfsc_code());
//            } else {
//                tv_ifsc.setVisibility(View.GONE);
//            }
//
//        } else {
        cv_bank.setVisibility(View.GONE);
//        }

        if (!searchDetails.getOrder_online().trim().isEmpty()) {
            cv_order_online.setVisibility(View.VISIBLE);
            tv_order_online.setText(searchDetails.getOrder_online());
        } else {
            cv_order_online.setVisibility(View.GONE);
        }

        if (searchDetails.getTotal_number_review().equals("0")) {
            rl_rating.setVisibility(View.GONE);
        } else {
            rl_rating.setVisibility(View.VISIBLE);
            float averageRating = Float.parseFloat(searchDetails.getAvg_rating());
            averageRating = Float.parseFloat(new DecimalFormat("#.#").format(averageRating));

            tv_total_rating.setText(String.valueOf(averageRating));
            tv_total_reviews.setText("(" + searchDetails.getTotal_number_review() + ")");
            rb_feedback_stars.setRating(averageRating);
        }

        if (searchDetails.getCan_book_order().equals("1")) {
            cv_view_orders.setVisibility(View.VISIBLE);
        } else if (searchDetails.getCan_book_order().equals("0")) {
            cv_view_orders.setVisibility(View.GONE);
            cv_dummy.setVisibility(View.GONE);
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
        imv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder sb = new StringBuilder();

                if (!searchDetails.getBusiness_name().equals("")) {
                    sb.append("Business Name - " + searchDetails.getBusiness_name() + "\n");
                }

                if (!searchDetails.getSubtype_description().equals("")) {
                    sb.append("Nature of Business - " + searchDetails.getType_description() + "/" + searchDetails.getSubtype_description() + "\n");
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

                if (!searchDetails.getLatitude().equals("") || !searchDetails.getLongitude().equals("")) {
                    sb.append("Location - " + "https://www.google.com/maps/?q="
                            + searchDetails.getLatitude() + "," + searchDetails.getLongitude() + "\n");

                }

                if (!searchDetails.getEmail().equals("")) {
                    sb.append("Email - " + searchDetails.getEmail() + "\n");
                }

                if (!searchDetails.getWebsite().equals("")) {
                    sb.append("Website - " + searchDetails.getWebsite() + "\n");
                }

                String details = sb.toString() + "\n" + "shared via Joinsta\n" + "Click Here - " + ApplicationConstants.JOINSTA_PLAYSTORELINK;

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, details);
                context.startActivity(Intent.createChooser(sharingIntent, "Choose from following"));

            }
        });

        imb_share_tax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder sb = new StringBuilder();

                if (!searchDetails.getTax_alias().trim().isEmpty()) {
                    sb.append("Tax Alias - " + searchDetails.getTax_alias() + "\n");
                }

                if (!searchDetails.getPan_number().trim().isEmpty()) {
                    sb.append("PAN - " + searchDetails.getPan_number() + "\n");
                }

                if (!searchDetails.getGst_number().trim().isEmpty()) {
                    sb.append("GST - " + searchDetails.getGst_number() + "\n");
                }

                String details = sb.toString() + "\n" + "shared via Joinsta\n" + "Click Here - " + ApplicationConstants.JOINSTA_PLAYSTORELINK;

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, details);
                context.startActivity(Intent.createChooser(sharingIntent, "Choose from following"));

            }
        });

        imb_share_bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder sb = new StringBuilder();

                if (!searchDetails.getAccount_holder_name().trim().isEmpty()) {
                    sb.append("A/C Holder Name - " + searchDetails.getAccount_holder_name() + "\n");
                }

                if (!searchDetails.getBank_alias().trim().isEmpty()) {
                    sb.append("Bank Alias - " + searchDetails.getBank_alias() + "\n");
                }

                if (!searchDetails.getBank_name().trim().isEmpty()) {
                    sb.append("Bank Name - " + searchDetails.getBank_name() + "\n");
                }

                if (!searchDetails.getAccount_no().trim().isEmpty()) {
                    sb.append("A/C No. - " + searchDetails.getAccount_no() + "\n");
                }

                if (!searchDetails.getIfsc_code().trim().isEmpty()) {
                    sb.append("IFSC Code - " + searchDetails.getIfsc_code() + "\n");
                }

                String details = sb.toString() + "\n" + "shared via Joinsta\n" + "Click Here - " + ApplicationConstants.JOINSTA_PLAYSTORELINK;

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, details);
                context.startActivity(Intent.createChooser(sharingIntent, "Choose from following"));

            }
        });

        tv_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!searchDetails.getEmail().trim().isEmpty()) {
                    sendEmail();
                } else {
                    Utilities.showMessage("Email not added", context, 2);
                }
            }
        });

        tv_website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = searchDetails.getWebsite();

                if (!url.startsWith("https://") || !url.startsWith("http://")) {
                    url = "http://" + url;
                }
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        rl_rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isNetworkAvailable(context))
                    new GetRatingsAndReviews().execute("1", searchDetails.getId());
                else
                    Utilities.showMessage("Please check your internet connection", context, 2);
            }
        });

        cv_view_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, BookOrderBusinessOwnerOrdersActivity.class)
                        .putExtra("businessId", searchDetails.getId()));
            }
        });

    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_backarrow);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new DeleteBusiness().execute();
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
                        Utilities.showMessage("Business details deleted successfully", context, 1);
                        finish();
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
                                .putExtra("profileName", tv_name.getText().toString().trim())
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
