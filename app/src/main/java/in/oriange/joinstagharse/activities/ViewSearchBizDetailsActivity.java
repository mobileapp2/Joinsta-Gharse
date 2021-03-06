package in.oriange.joinstagharse.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
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
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.Objects;

import co.lujun.androidtagview.TagContainerLayout;
import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.models.RatingAndReviewModel;
import in.oriange.joinstagharse.models.SearchDetailsModel;
import in.oriange.joinstagharse.utilities.APICall;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.DownloadFileAndMessageShare;
import in.oriange.joinstagharse.utilities.UserSessionManager;
import in.oriange.joinstagharse.utilities.Utilities;

import static in.oriange.joinstagharse.utilities.ApplicationConstants.IMAGE_LINK;
import static in.oriange.joinstagharse.utilities.PermissionUtil.doesAppNeedPermissions;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.CALL_PHONE_PERMISSION_REQUEST;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.CAMERA_AND_STORAGE_PERMISSION_REQUEST;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.LOCATION_PERMISSION_REQUEST;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.READ_CONTACTS_PERMISSION_REQUEST;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.STORAGE_PERMISSION;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.STORAGE_PERMISSION_REQUEST;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.callPermissionMsg;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.cameraStoragePermissionMsg;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.isStoragePermissionGiven;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.locationPermissionMsg;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.manualPermission;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.readContactsPermissionMsg;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.storagePermissionMsg;
import static in.oriange.joinstagharse.utilities.Utilities.changeStatusBar;
import static in.oriange.joinstagharse.utilities.Utilities.showCallDialog;

public class ViewSearchBizDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private CoordinatorLayout cl_root;
    private ImageView imv_image;
    private ProgressBar progressBar;
    private CheckBox cb_like;
    private FloatingActionButton btn_share;
    private LinearLayout ll_direction, ll_mobile, ll_whatsapp, ll_landline, ll_email, ll_nopreview, ll_email_1,
            ll_website, ll_address;
    private ImageButton ib_email, ib_website, ib_address;
    private TextView tv_name, tv_nature, tv_designation, tv_email, tv_website, tv_address, tv_total_rating;
    private RatingBar rb_post_rating;
    private CardView cv_tabs, cv_contact_details, cv_address, cv_post_review, cv_add, cv_enquire, cv_offers;
    private TagContainerLayout container_tags;
    private RecyclerView rv_mobilenos;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    private SearchDetailsModel.ResultBean.BusinessesBean searchDetails;
    private String userId, typeFrom, isFav, name, mobile, countryCode;

    private JSONArray emailJsonArray;
    private LocalBroadcastManager localBroadcastManager;
    private Menu collapsedMenu;
    private boolean appBarExpanded = true;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewsearch_bizdetails);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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

        cl_root = findViewById(R.id.cl_root);
        toolbar = findViewById(R.id.anim_toolbar);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        ll_nopreview = findViewById(R.id.ll_nopreview);
        ll_direction = findViewById(R.id.ll_direction);
        ll_mobile = findViewById(R.id.ll_mobile);
        ll_whatsapp = findViewById(R.id.ll_whatsapp);
        ll_landline = findViewById(R.id.ll_landline);
        ll_email = findViewById(R.id.ll_email);
        ll_email_1 = findViewById(R.id.ll_email_1);
        ll_website = findViewById(R.id.ll_website);
        ll_address = findViewById(R.id.ll_address);
        ib_email = findViewById(R.id.ib_email);
        ib_website = findViewById(R.id.ib_website);
        ib_address = findViewById(R.id.ib_address);
        cb_like = findViewById(R.id.cb_like);
        imv_image = findViewById(R.id.imv_image);
        progressBar = findViewById(R.id.progressBar);
        cv_tabs = findViewById(R.id.cv_tabs);
        cv_contact_details = findViewById(R.id.cv_contact_details);
        cv_address = findViewById(R.id.cv_address);
        cv_post_review = findViewById(R.id.cv_post_review);
        cv_add = findViewById(R.id.cv_add);
        cv_enquire = findViewById(R.id.cv_enquire);
        cv_offers = findViewById(R.id.cv_offers);

        tv_name = findViewById(R.id.tv_name);
        tv_nature = findViewById(R.id.tv_nature);
        tv_designation = findViewById(R.id.tv_designation);
        tv_email = findViewById(R.id.tv_email);
        tv_website = findViewById(R.id.tv_website);
        tv_address = findViewById(R.id.tv_address);
        tv_total_rating = findViewById(R.id.tv_total_rating);
        rb_post_rating = findViewById(R.id.rb_post_rating);

        btn_share = findViewById(R.id.btn_share);
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
//                            Utilities.changeStatusBar(context, getWindow());
//                            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
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

        if (!searchDetails.getBusiness_name().trim().isEmpty())
            tv_name.setText(searchDetails.getBusiness_code() + " - " + searchDetails.getBusiness_name());
        else
            tv_name.setVisibility(View.GONE);

        if (searchDetails.getTotal_number_review().equals("0")) {
            tv_total_rating.setVisibility(View.GONE);
        } else {
            float averageRating = Float.parseFloat(searchDetails.getAvg_rating());
            averageRating = Float.parseFloat(new DecimalFormat("#.#").format(averageRating));

            tv_total_rating.setText(String.valueOf(averageRating) + "\u2605");
            if (averageRating >= 4 && averageRating <= 5)
                tv_total_rating.setBackground(context.getResources().getDrawable(R.drawable.button_focusfilled_green));
            else if (averageRating >= 3 && averageRating <= 4.9)
                tv_total_rating.setBackground(context.getResources().getDrawable(R.drawable.button_focusfilled_orange));
            else if (averageRating >= 2 && averageRating <= 3.9)
                tv_total_rating.setBackground(context.getResources().getDrawable(R.drawable.button_focusfilled_yellow));
            else if (averageRating >= 1 && averageRating <= 1.9)
                tv_total_rating.setBackground(context.getResources().getDrawable(R.drawable.button_focusfilled_red));
        }

        if (!searchDetails.getTypeSubTypeName().equals(""))
            tv_nature.setText(searchDetails.getTypeSubTypeName());
        else
            tv_nature.setVisibility(View.GONE);

        if (!searchDetails.getDesignation().trim().isEmpty())
            tv_designation.setText(searchDetails.getDesignation());
        else
            tv_designation.setVisibility(View.GONE);

        if (searchDetails.getSubTypesTagsList("2").size() != 0)
            container_tags.setTags(searchDetails.getSubTypesTagsList("2"));
        else
            cv_tabs.setVisibility(View.GONE);

        if ((searchDetails.getMobiles().get(0) == null || searchDetails.getMobiles().get(0).size() == 0)
                && searchDetails.getEmail().trim().isEmpty()
                && searchDetails.getWebsite().trim().isEmpty()) {
            cv_contact_details.setVisibility(View.GONE);
        } else {
            if (searchDetails.getMobiles().get(0) != null)
                if (searchDetails.getMobiles().get(0).size() > 0)
                    rv_mobilenos.setAdapter(new MobileNumbersAdapter(searchDetails.getMobiles().get(0)));
                else
                    rv_mobilenos.setVisibility(View.GONE);
            else
                rv_mobilenos.setVisibility(View.GONE);

            if (!searchDetails.getEmail().trim().isEmpty())
                tv_email.setText(searchDetails.getEmail());
            else
                ll_email_1.setVisibility(View.GONE);


            if (!searchDetails.getWebsite().trim().isEmpty())
                tv_website.setText(searchDetails.getWebsite());
            else
                ll_website.setVisibility(View.GONE);
        }

        if (searchDetails.getAddress().trim().isEmpty() && (searchDetails.getLatitude().trim().isEmpty() || searchDetails.getLongitude().trim().isEmpty())) {
            cv_address.setVisibility(View.GONE);
        } else {
            if (!searchDetails.getAddress().trim().isEmpty())
                tv_address.setText(searchDetails.getAddress());
            else
                ll_address.setVisibility(View.GONE);
        }

        if (!searchDetails.getRating_by_user().equals("0"))
            cv_post_review.setVisibility(View.GONE);

        if (searchDetails.getIsFavourite().equals("1"))
            cb_like.setChecked(true);

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
        cb_like.setOnClickListener(v -> {
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

        });

        ll_direction.setOnClickListener(v -> {
            if (searchDetails.getLatitude().trim().isEmpty() || searchDetails.getLongitude().trim().isEmpty()) {
                Utilities.showMessage("Location not added", context, 2);
                return;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=&daddr=" + searchDetails.getLatitude() + "," + searchDetails.getLongitude()));
            startActivity(intent);
        });

        ib_address.setOnClickListener(v -> {
            if (searchDetails.getLatitude().trim().isEmpty() || searchDetails.getLongitude().trim().isEmpty()) {
                Utilities.showMessage("Location not added", context, 2);
                return;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=&daddr=" + searchDetails.getLatitude() + "," + searchDetails.getLongitude()));
            startActivity(intent);
        });

        ll_mobile.setOnClickListener(v -> {
            if (searchDetails.getMobiles().get(0) != null)
                if (searchDetails.getMobiles().get(0).size() > 0)
                    showMobileListDialog(searchDetails.getMobiles().get(0));
                else
                    Utilities.showMessage("Mobile number not added", context, 2);

            else
                Utilities.showMessage("Mobile number not added", context, 2);

        });

        ll_whatsapp.setOnClickListener(v -> {
            if (searchDetails.getMobiles().get(0) != null)
                if (searchDetails.getMobiles().get(0).size() > 0)
                    showWhatsAppListDialog(searchDetails.getMobiles().get(0));
                else
                    Utilities.showMessage("Mobile number not added", context, 2);
            else
                Utilities.showMessage("Mobile number not added", context, 2);
        });

        ll_landline.setOnClickListener(v -> {
            if (searchDetails.getLandline().get(0) != null)
                if (searchDetails.getLandline().get(0).size() > 0)
                    showLandlineListDialog(searchDetails.getLandline().get(0));
                else
                    Utilities.showMessage("Landline number not added", context, 2);
            else
                Utilities.showMessage("Landline number not added", context, 2);

        });

        ll_email.setOnClickListener(v -> {
            if (!searchDetails.getEmail().trim().isEmpty()) {
                sendEmail();
            } else {
                Utilities.showMessage("Email not added", context, 2);
            }
        });

        ib_email.setOnClickListener(v -> {
            if (!searchDetails.getEmail().trim().isEmpty()) {
                sendEmail();
            } else {
                Utilities.showMessage("Email not added", context, 2);
            }
        });

        ib_website.setOnClickListener(v -> {
            String url = searchDetails.getWebsite();

            if (!url.startsWith("https://") || !url.startsWith("http://")) {
                url = "http://" + url;
            }
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        mapFragment.getView().setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=&daddr=" + searchDetails.getLatitude() + "," + searchDetails.getLongitude()));
            startActivity(intent);
        });

        cv_enquire.setOnClickListener(v -> {
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

            rb_mobile.setOnClickListener(v1 -> {
                edt_mobile.setVisibility(View.VISIBLE);
                edt_email.setVisibility(View.GONE);

                edt_email.setText("");

                edt_mobile.setText("+" + countryCode + mobile);
            });

            rb_email.setOnClickListener(v12 -> {

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
        });

        btn_share.setOnClickListener(v -> shareDetails());

        tv_total_rating.setOnClickListener(v -> {
            if (Utilities.isNetworkAvailable(context))
                new GetRatingsAndReviews().execute("1", searchDetails.getId());
            else
                Utilities.showMessage("Please check your internet connection", context, 2);
        });

        rb_post_rating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (rating == 0)
                return;

            startActivity(new Intent(context, AddRatingAndReviewActivity.class)
                    .putExtra("recordId", searchDetails.getId())
                    .putExtra("profileName", tv_name.getText().toString().trim())
                    .putExtra("categoryTypeId", "1")
                    .putExtra("rating", (int) rb_post_rating.getRating()));
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

        builderSingle.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
            showCallDialog(context, mobileList.get(which).getCountry_code() + mobileList.get(which).getMobile_number());
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

        builderSingle.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
            showCallDialog(context, landlineList.get(which).getCountry_code() + landlineList.get(which).getLandline_number());
        });
        builderSingle.show();
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

    private class MobileNumbersAdapter extends RecyclerView.Adapter<MobileNumbersAdapter.MyViewHolder> {

        private List<SearchDetailsModel.ResultBean.BusinessesBean.MobilesBeanXX> resultArrayList;

        MobileNumbersAdapter(List<SearchDetailsModel.ResultBean.BusinessesBean.MobilesBeanXX> resultArrayList) {
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

            holder.ib_call.setOnClickListener(v -> {
                showCallDialog(context, searchDetails.getCountry_code() + searchDetails.getMobile_number());
            });
        }

        @Override
        public int getItemCount() {
            return resultArrayList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView tv_mobile;
            private ImageButton ib_call;

            public MyViewHolder(View view) {
                super(view);
                tv_mobile = view.findViewById(R.id.tv_mobile);
                ib_call = view.findViewById(R.id.ib_call);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
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
                            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("SearchFragment"));
                        } else if (typeFrom.equals("3")) {        // 3 = from home
                            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("BizProfEmpDetailsListActivity"));
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

    private void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", searchDetails.getEmail(), null));
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    private void shareDetails() {
        if (!searchDetails.getImage_url().equals("")) {
            if (doesAppNeedPermissions()) {
                if (!isStoragePermissionGiven(context, STORAGE_PERMISSION)) {
                    return;
                }
            }

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

    private void setUpToolbar() {
        AppBarLayout appBarLayout = findViewById(R.id.appbar);

        appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
            //  Vertical offset == 0 indicates appBar is fully expanded.
            if (Math.abs(verticalOffset) > 200) {
                appBarExpanded = false;
                invalidateOptionsMenu();
            } else {
                appBarExpanded = true;
                invalidateOptionsMenu();
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(view -> finish());

//        collapsingToolbar.setTitle("");
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (collapsedMenu != null && (!appBarExpanded)) {
            toolbar.setNavigationIcon(R.drawable.icon_backarrow_black);
            collapsedMenu.add("Share")
                    .setIcon(R.drawable.icon_share)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            collapsingToolbar.setTitle(searchDetails.getBusiness_name());
            changeStatusBar(context, getWindow());
        } else {
            //Expanded
            collapsingToolbar.setTitle("");
            toolbar.setNavigationIcon(R.drawable.icon_backarrow_black);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        collapsingToolbar.setCollapsedTitleTextColor(context.getResources().getColor(R.color.black));
        collapsingToolbar.setExpandedTitleColor(context.getResources().getColor(R.color.black));
        return super.onPrepareOptionsMenu(collapsedMenu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        collapsedMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        if (item.getTitle() == "Share") {
            shareDetails();
        }

        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_AND_STORAGE_PERMISSION_REQUEST: {
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED)) {
                    manualPermission(context, cameraStoragePermissionMsg, permissions, requestCode);
                }
            }
            break;
            case STORAGE_PERMISSION_REQUEST: {
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    manualPermission(context, storagePermissionMsg, permissions, requestCode);
                }
            }
            break;
            case CALL_PHONE_PERMISSION_REQUEST: {
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    manualPermission(context, callPermissionMsg, permissions, requestCode);
                }
            }
            break;
            case LOCATION_PERMISSION_REQUEST: {
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    manualPermission(context, locationPermissionMsg, permissions, requestCode);
                }
            }
            break;
            case READ_CONTACTS_PERMISSION_REQUEST: {
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    manualPermission(context, readContactsPermissionMsg, permissions, requestCode);
                }
            }
            break;
        }
    }

}
