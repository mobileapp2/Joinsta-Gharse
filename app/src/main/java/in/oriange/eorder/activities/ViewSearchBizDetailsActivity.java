package in.oriange.eorder.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;
import in.oriange.eorder.R;
import in.oriange.eorder.fragments.SearchFragment;
import in.oriange.eorder.models.RatingAndReviewModel;
import in.oriange.eorder.models.SearchDetailsModel;
import in.oriange.eorder.utilities.APICall;
import in.oriange.eorder.utilities.ApplicationConstants;
import in.oriange.eorder.utilities.CalculateDistanceTime;
import in.oriange.eorder.utilities.UserSessionManager;
import in.oriange.eorder.utilities.Utilities;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static in.oriange.eorder.activities.MainDrawerActivity.startLocationUpdates;
import static in.oriange.eorder.utilities.ApplicationConstants.IMAGE_LINK;
import static in.oriange.eorder.utilities.Utilities.isLocationEnabled;
import static in.oriange.eorder.utilities.Utilities.provideCallPremission;
import static in.oriange.eorder.utilities.Utilities.provideLocationAccess;
import static in.oriange.eorder.utilities.Utilities.turnOnLocation;

public class ViewSearchBizDetailsActivity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private ImageView imv_image;
    private ProgressBar progressBar;
    private CheckBox cb_like;
    private ImageView imv_share;
    private LinearLayout ll_direction, ll_mobile, ll_whatsapp, ll_landline, ll_email, ll_nopreview;
    private TextView tv_name, tv_nature, tv_designation, tv_email, tv_website, tv_address, tv_tax_alias, tv_pan, tv_gst, tv_accholder_name,
            tv_bank_alias, tv_bank_name, tv_acc_no, tv_ifsc, tv_mutual_groups, tv_order_online,
            tv_total_rating, tv_total_reviews;
    private RelativeLayout rl_rating;
    private RatingBar rb_feedback_stars, rb_post_rating;
    private Button btn_enquire, btn_caldist;
    private CardView cv_tabs, cv_contact_details, cv_address, cv_tax, cv_bank, cv_mutual_groups, cv_order_online,
            cv_post_review;
    private TagContainerLayout container_tags;
    private RecyclerView rv_mobilenos;

    private SearchDetailsModel.ResultBean.BusinessesBean searchDetails;
    private String userId, isFav, typeFrom, name, mobile, countryCode;

    private JSONArray emailJsonArray;
    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewsearch_bizdetails);

        init();
        setDefault();
        getSessionDetails();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = ViewSearchBizDetailsActivity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        ll_nopreview = findViewById(R.id.ll_nopreview);
        ll_direction = findViewById(R.id.ll_direction);
        ll_mobile = findViewById(R.id.ll_mobile);
        ll_whatsapp = findViewById(R.id.ll_whatsapp);
        ll_landline = findViewById(R.id.ll_landline);
        ll_email = findViewById(R.id.ll_email);
        cb_like = findViewById(R.id.cb_like);
        imv_image = findViewById(R.id.imv_image);
        progressBar = findViewById(R.id.progressBar);
        cv_tabs = findViewById(R.id.cv_tabs);
        cv_contact_details = findViewById(R.id.cv_contact_details);
        cv_address = findViewById(R.id.cv_address);
        cv_tax = findViewById(R.id.cv_tax);
        cv_bank = findViewById(R.id.cv_bank);
        cv_mutual_groups = findViewById(R.id.cv_mutual_groups);
        cv_order_online = findViewById(R.id.cv_order_online);
        cv_post_review = findViewById(R.id.cv_post_review);

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
        tv_mutual_groups = findViewById(R.id.tv_mutual_groups);
        tv_order_online = findViewById(R.id.tv_order_online);

        tv_total_rating = findViewById(R.id.tv_total_rating);
        tv_total_reviews = findViewById(R.id.tv_total_reviews);
        rl_rating = findViewById(R.id.rl_rating);
        rb_feedback_stars = findViewById(R.id.rb_feedback_stars);
        rb_post_rating = findViewById(R.id.rb_post_rating);

        imv_share = findViewById(R.id.imv_share);

        btn_enquire = findViewById(R.id.btn_enquire);
        btn_caldist = findViewById(R.id.btn_caldist);
        container_tags = findViewById(R.id.container_tags);
        rv_mobilenos = findViewById(R.id.rv_mobilenos);
        rv_mobilenos.setLayoutManager(new LinearLayoutManager(context));

        emailJsonArray = new JSONArray();
    }

    private void setDefault() {
        searchDetails = (SearchDetailsModel.ResultBean.BusinessesBean) getIntent().getSerializableExtra("searchDetails");
        typeFrom = getIntent().getStringExtra("type");

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

        if (searchDetails.getIsFavourite().equals("1"))
            cb_like.setChecked(true);

        if (!searchDetails.getBusiness_name().trim().isEmpty()) {
            tv_name.setText(searchDetails.getBusiness_code() + " - " + searchDetails.getBusiness_name());
        } else {
            tv_name.setVisibility(View.GONE);
        }

        if (!searchDetails.getTypeSubTypeName().equals(""))
            tv_nature.setText(searchDetails.getTypeSubTypeName());
        else
            tv_nature.setVisibility(View.GONE);

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

        cv_mutual_groups.setVisibility(View.GONE);

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

        if (!searchDetails.getRating_by_user().equals("0")) {
            cv_post_review.setVisibility(View.GONE);
        }

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("ViewSearchBizDetailsActivity");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void getSessionDetails() {

        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);

            userId = json.getString("userid");
            name = json.getString("first_name");
            mobile = json.getString("mobile");
            emailJsonArray = new JSONArray(json.getString("email"));
            try {
                countryCode = json.getString("country_code");
            } catch (Exception e) {
                countryCode = "91";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setEventHandler() {
        cb_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFav = searchDetails.getIsFavourite();

                if (cb_like.isChecked())
                    isFav = "1";
                else
                    isFav = "0";

                JsonObject mainObj = new JsonObject();

                mainObj.addProperty("type", "createfav");
                mainObj.addProperty("info_id", searchDetails.getId());
                mainObj.addProperty("info_type", "1");
                mainObj.addProperty("user_id", userId);
                mainObj.addProperty("record_status_id", isFav);

                if (Utilities.isNetworkAvailable(context)) {
                    new SetFavourite().execute(mainObj.toString());
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }

            }
        });

        ll_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchDetails.getLatitude().trim().isEmpty() || searchDetails.getLongitude().trim().isEmpty()) {
                    Utilities.showMessage("Location not added", context, 2);
                    return;
                }

                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=&daddr=" + searchDetails.getLatitude() + "," + searchDetails.getLongitude()));
                startActivity(intent);
            }
        });

        ll_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchDetails.getMobiles().get(0) != null)
                    if (searchDetails.getMobiles().get(0).size() > 0) {
                        if (ActivityCompat.checkSelfPermission(context, CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            provideCallPremission(context);
                        } else {
                            showMobileListDialog(searchDetails.getMobiles().get(0));
                        }
                    } else
                        Utilities.showMessage("Mobile number not added", context, 2);

                else
                    Utilities.showMessage("Mobile number not added", context, 2);

            }
        });

        ll_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (searchDetails.getMobiles().get(0) != null)
                    if (searchDetails.getMobiles().get(0).size() > 0) {
                        if (ActivityCompat.checkSelfPermission(context, CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            provideCallPremission(context);
                        } else {
                            showWhatsAppListDialog(searchDetails.getMobiles().get(0));
                        }
                    } else
                        Utilities.showMessage("Mobile number not added", context, 2);

                else
                    Utilities.showMessage("Mobile number not added", context, 2);
            }
        });

        ll_landline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchDetails.getLandline().get(0) != null)
                    if (searchDetails.getLandline().get(0).size() > 0) {
                        if (ActivityCompat.checkSelfPermission(context, CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            provideCallPremission(context);
                        } else {
                            showLandlineListDialog(searchDetails.getLandline().get(0));
                        }
                    } else
                        Utilities.showMessage("Landline number not added", context, 2);
                else
                    Utilities.showMessage("Landline number not added", context, 2);

            }
        });

        ll_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!searchDetails.getEmail().trim().isEmpty()) {
                    sendEmail();
                } else {
                    Utilities.showMessage("Email not added", context, 2);
                }
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

        btn_caldist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (searchDetails.getLatitude().equals("") || searchDetails.getLongitude().equals("")) {
                    return;
                }

                if (ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED /*&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED*/) {
                    provideLocationAccess(context);
                    return;
                }

                if (!isLocationEnabled(context)) {
                    turnOnLocation(context);
                    return;
                }

                if (MainDrawerActivity.latLng == null) {
                    btn_caldist.setText(Html.fromHtml("<font color=\"#C62828\"> <b>Try again</b></font>"));
                    return;
                }

                startLocationUpdates();

                LatLng currentLocation = new LatLng(MainDrawerActivity.latLng.latitude, MainDrawerActivity.latLng.longitude);
                LatLng destinationLocation = new LatLng(Double.parseDouble(searchDetails.getLatitude()), Double.parseDouble(searchDetails.getLongitude()));

                CalculateDistanceTime distance_task = new CalculateDistanceTime(context);

                distance_task.getDirectionsUrl(currentLocation, destinationLocation);

                distance_task.setLoadListener(new CalculateDistanceTime.taskCompleteListener() {
                    @Override
                    public void taskCompleted(String[] time_distance) {
                        btn_caldist.setText(time_distance[0]);
//                        holder.tv_distance.setText(Html.fromHtml("<font color=\"#FFA000\"> <b>" + time_distance[0] + "</b></font> <font color=\"#616161\">from current location</font>"));

                    }

                });
            }
        });

        btn_enquire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                View promptView = layoutInflater.inflate(R.layout.dialog_layout_enquiry, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                alertDialogBuilder.setTitle("Enquiry");
                alertDialogBuilder.setView(promptView);

                final MaterialEditText edt_name = promptView.findViewById(R.id.edt_name);
                final RadioGroup rg_communicationmode = promptView.findViewById(R.id.rg_communicationmode);
                final RadioButton rb_mobile = promptView.findViewById(R.id.rb_mobile);
                final RadioButton rb_email = promptView.findViewById(R.id.rb_email);
                final MaterialEditText edt_mobile = promptView.findViewById(R.id.edt_mobile);
                final MaterialEditText edt_email = promptView.findViewById(R.id.edt_email);
                final MaterialEditText edt_subject = promptView.findViewById(R.id.edt_subject);
                final EditText edt_details = promptView.findViewById(R.id.edt_details);
                final Button btn_save = promptView.findViewById(R.id.btn_save);

                edt_name.setText(name);

                final AlertDialog alertD = alertDialogBuilder.create();

                rb_mobile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edt_mobile.setVisibility(View.VISIBLE);
                        edt_email.setVisibility(View.GONE);

                        edt_email.setText("");

                        edt_mobile.setText("+" + countryCode + mobile);
                    }
                });

                rb_email.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        edt_mobile.setVisibility(View.GONE);
                        edt_mobile.setText("");

                        if (emailJsonArray == null) {
                            Utilities.showAlertDialogNormal(context, "Please add a primary email and verify it from Basic Information");
                            rg_communicationmode.clearCheck();
                            return;
                        }

                        if (emailJsonArray.length() == 0) {
                            Utilities.showAlertDialogNormal(context, "Please add a primary email and verify it from Basic Information");
                            rg_communicationmode.clearCheck();
                            return;

                        }

                        try {
                            for (int i = 0; i < emailJsonArray.length(); i++) {
                                JSONObject emailObj = emailJsonArray.getJSONObject(0);
                                if (emailObj.getString("is_primary").equals("1")) {
                                    if (emailObj.getString("email_verification").equals("1")) {
                                        if (emailObj.getString("email").equals("")) {
                                            Utilities.showAlertDialogNormal(context, "Please update your primary email from Basic Information");
                                            rg_communicationmode.clearCheck();
                                            return;
                                        }

                                        edt_email.setText(emailObj.getString("email"));
                                    } else {
                                        Utilities.showAlertDialogNormal(context, "Please verify your primary email from Basic Information");
                                        rg_communicationmode.clearCheck();
                                        return;
                                    }
                                } else {
                                    Utilities.showAlertDialogNormal(context, "Please set a primary email from Basic Information");
                                    rg_communicationmode.clearCheck();
                                    return;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Utilities.showAlertDialogNormal(context, "We have made some changes related to user email, please kindly logout and login again to refresh your session");
                            rg_communicationmode.clearCheck();
                            return;

                        }

                        edt_email.setVisibility(View.VISIBLE);

                    }
                });

                btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (edt_name.getText().toString().trim().isEmpty()) {
                            edt_name.setError("Please enter name");
                            edt_name.requestFocus();
                            return;
                        }

                        if (rb_mobile.isChecked()) {
                            if (edt_mobile.getText().toString().trim().isEmpty()) {
                                edt_mobile.setError("Please enter valid mobile");
                                edt_mobile.requestFocus();
                                return;
                            }
                            edt_email.setText("");
                        } else if (rb_email.isChecked()) {
                            if (!Utilities.isEmailValid(edt_email.getText().toString().trim())) {
                                edt_email.setError("Please enter valid email");
                                edt_email.requestFocus();
                                return;
                            }
                            edt_mobile.setText("");
                        } else {
                            Utilities.showMessage("Please select communication mode", context, 2);
                            return;
                        }

                        if (edt_subject.getText().toString().trim().isEmpty()) {
                            edt_subject.setError("Please enter subject");
                            edt_subject.requestFocus();
                            return;
                        }

                        if (edt_details.getText().toString().trim().isEmpty()) {
                            edt_details.setError("Please enter details");
                            edt_details.requestFocus();
                            return;
                        }

                        if (Utilities.isNetworkAvailable(context)) {
                            alertD.dismiss();
                            new SendEnquiryDetails().execute(
                                    userId,
                                    edt_name.getText().toString().trim(),
                                    edt_mobile.getText().toString().trim(),
                                    edt_email.getText().toString().trim(),
                                    edt_subject.getText().toString().trim(),
                                    edt_details.getText().toString().trim(),
                                    "1",
                                    searchDetails.getId()

                            );
                        } else {
                            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                        }
                    }
                });

                alertD.show();
            }
        });

        imv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder sb = new StringBuilder();

                if (!searchDetails.getBusiness_name().equals("")) {
                    sb.append("Business Name - " + searchDetails.getBusiness_name() + "\n");
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

                String details = sb.toString() + "\n" + "shared via Joinsta\n" + "Click Here - " + ApplicationConstants.JOINSTA_PLAYSTORELINK;

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, details);
                context.startActivity(Intent.createChooser(sharingIntent, "Choose from following"));

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

        rb_post_rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating == 0)
                    return;

                startActivity(new Intent(context, AddRatingAndReviewActivity.class)
                        .putExtra("recordId", searchDetails.getId())
                        .putExtra("profileName", tv_name.getText().toString().trim())
                        .putExtra("categoryTypeId", "1")
                        .putExtra("rating", (int) rb_post_rating.getRating()));
            }
        });
    }

    private void showMobileListDialog(final List<SearchDetailsModel.ResultBean.BusinessesBean.MobilesBeanXX> mobileList) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select mobile number to make a call");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row);

        for (int i = 0; i < mobileList.size(); i++) {
            arrayAdapter.add(mobileList.get(i).getCountry_code() + mobileList.get(i).getMobile_number());
        }

        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Intent.ACTION_CALL,
                        Uri.parse("tel:" + mobileList.get(which).getCountry_code() + mobileList.get(which).getMobile_number())));
            }
        });
        builderSingle.show();
    }

    private void showWhatsAppListDialog(final List<SearchDetailsModel.ResultBean.BusinessesBean.MobilesBeanXX> mobileList) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select mobile number");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row);

        for (int i = 0; i < mobileList.size(); i++) {
            arrayAdapter.add(mobileList.get(i).getCountry_code() + mobileList.get(i).getMobile_number());
        }

        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String URL = "https://wa.me/" + mobileList.get(which).getCountry_code() + mobileList.get(which).getMobile_number();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL)));
            }
        });
        builderSingle.show();
    }

    private void showLandlineListDialog(final List<SearchDetailsModel.ResultBean.BusinessesBean.LandlineBeanXX> landlineList) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select landline number to make a call");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row);

        for (int i = 0; i < landlineList.size(); i++) {
            arrayAdapter.add(landlineList.get(i).getCountry_code() + landlineList.get(i).getLandline_number());
        }

        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Intent.ACTION_CALL,
                        Uri.parse("tel:" + landlineList.get(which).getCountry_code() + landlineList.get(which).getLandline_number())));
            }
        });
        builderSingle.show();
    }

    public class MobileNumbersAdapter extends RecyclerView.Adapter<MobileNumbersAdapter.MyViewHolder> {

        private List<SearchDetailsModel.ResultBean.BusinessesBean.MobilesBeanXX> resultArrayList;

        public MobileNumbersAdapter(List<SearchDetailsModel.ResultBean.BusinessesBean.MobilesBeanXX> resultArrayList) {
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
            final SearchDetailsModel.ResultBean.BusinessesBean.MobilesBeanXX searchDetails = resultArrayList.get(position);

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

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView tv_mobile;

            public MyViewHolder(View view) {
                super(view);
                tv_mobile = view.findViewById(R.id.tv_mobile);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }
    }

    private class SetFavourite extends AsyncTask<String, Void, String> {

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
            res = APICall.JSONAPICall(ApplicationConstants.FAVOURITEAPI, params[0]);
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

                        if (typeFrom.equals("1")) {               //  1 = from search
                            int position = SearchFragment.businessList.indexOf(searchDetails);
                            SearchFragment.businessList.get(position).setIsFavourite(isFav);
                        } else if (typeFrom.equals("2")) {        // 2 = from favorite
                            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("SearchFragment"));
                        } else if (typeFrom.equals("3")) {        // 3 = from home
                            int position = BizProfEmpDetailsListActivity.businessList.indexOf(searchDetails);
                            BizProfEmpDetailsListActivity.businessList.get(position).setIsFavourite(isFav);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("SearchFragment"));
                        }
                    } else {
                        cb_like.setChecked(false);
                    }
                }
            } catch (Exception e) {
                cb_like.setChecked(false);
                e.printStackTrace();
            }
        }
    }

    private class SendEnquiryDetails extends AsyncTask<String, Void, String> {

        ProgressDialog pd;

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
            obj.addProperty("type", "createenquiry");
            obj.addProperty("userid", params[0]);
            obj.addProperty("name", params[1]);
            obj.addProperty("mobile", params[2]);
            obj.addProperty("email", params[3]);
            obj.addProperty("subject", params[4]);
            obj.addProperty("message", params[5]);
            obj.addProperty("category_type_id", params[6]);
            obj.addProperty("record_id", params[7]);
            obj.addProperty("msg_type", "notification");
            res = APICall.JSONAPICall(ApplicationConstants.ENQUIRYAPI, obj.toString());
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
                        Utilities.showMessage("Enquiry sent successfully", context, 1);
                    } else {

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

    private void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", searchDetails.getEmail(), null));
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.anim_toolbar);
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.icon_backarrow);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        collapsingToolbar.setTitle("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        rb_post_rating.setRating(0);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }
}
