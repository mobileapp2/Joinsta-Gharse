package in.oriange.joinstagharse.activities;

import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.adapters.OfferImageSliderAdapter;
import in.oriange.joinstagharse.models.BannerListModel;
import in.oriange.joinstagharse.models.MyOffersListModel;
import in.oriange.joinstagharse.utilities.APICall;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.DownloadFileAndMessageShare;
import in.oriange.joinstagharse.utilities.ParamsPojo;
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
import static in.oriange.joinstagharse.utilities.Utilities.changeDateFormat;
import static in.oriange.joinstagharse.utilities.Utilities.changeStatusBar;

public class ViewMyOfferDetailsActivity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private TextView tv_business_name, tv_title, tv_description, tv_validity, tv_url, tv_promo_code;
    private ImageButton ib_website;
    private CardView cv_url, cv_promo_code;
    private ImageButton imb_share;
    private SliderView imageSlider;

    private MyOffersListModel.ResultBean offerDetails;
    private String userId;

    private String CALLTYPE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_offer_details);

        init();
        setDefault();
        getSessionDetails();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = ViewMyOfferDetailsActivity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);
        changeStatusBar(context, getWindow());
        tv_business_name = findViewById(R.id.tv_business_name);
        tv_title = findViewById(R.id.tv_title);
        tv_description = findViewById(R.id.tv_description);
        tv_validity = findViewById(R.id.tv_validity);
        tv_url = findViewById(R.id.tv_url);
        ib_website = findViewById(R.id.ib_website);
        tv_promo_code = findViewById(R.id.tv_promo_code);
        imb_share = findViewById(R.id.imb_share);
        imageSlider = findViewById(R.id.imageSlider);

        cv_url = findViewById(R.id.cv_url);
        cv_promo_code = findViewById(R.id.cv_promo_code);
    }

    private void setDefault() {
        CALLTYPE = getIntent().getStringExtra("CALLTYPE");
        offerDetails = (MyOffersListModel.ResultBean) getIntent().getSerializableExtra("offerDetails");
//        if (!offerDetails.getRecord_name().isEmpty() && !offerDetails.getSub_category().isEmpty()) {
//            tv_business_name.setText(offerDetails.getRecord_name() + " (" + offerDetails.getSub_category() + ")");
//        } else if (offerDetails.getRecord_name().isEmpty() && offerDetails.getSub_category().isEmpty()) {
//            tv_business_name.setVisibility(View.GONE);
//        } else if (!offerDetails.getRecord_name().isEmpty()) {
//            tv_business_name.setText(offerDetails.getRecord_name());
//        }

        if (!offerDetails.getRecord_name().trim().equals("")) {
            tv_business_name.setText(offerDetails.getRecord_name());
        } else {
            tv_business_name.setVisibility(View.GONE);
        }

        tv_title.setText(offerDetails.getTitle());
        tv_description.setText(offerDetails.getDescription());

        if (!offerDetails.getStart_date().equals("") && !offerDetails.getEnd_date().equals("")) {
            tv_validity.setText("Offer valid from " + changeDateFormat("yyyy-MM-dd", "dd-MMM-yyyy", offerDetails.getStart_date()) + " to " +
                    changeDateFormat("yyyy-MM-dd", "dd-MMM-yyyy", offerDetails.getEnd_date()));
        } else {
            tv_validity.setVisibility(View.GONE);
        }

        if (!offerDetails.getUrl().equals("")) {
            tv_url.setText(offerDetails.getUrl());
        } else {
            cv_url.setVisibility(View.GONE);
        }

        if (!offerDetails.getPromo_code().equals("")) {
            tv_promo_code.setText(offerDetails.getPromo_code());
        } else {
            cv_promo_code.setVisibility(View.GONE);
        }

        if (offerDetails.getDocuments().size() == 0) {
            imageSlider.setVisibility(View.GONE);
        } else {
            List<BannerListModel.ResultBean> bannerList = new ArrayList<>();
            for (int i = 0; i < offerDetails.getDocuments().size(); i++) {
                bannerList.add(new BannerListModel.ResultBean("", "", IMAGE_LINK + "offerdoc/business/" + offerDetails.getDocuments().get(i).getDocument()));
            }

            OfferImageSliderAdapter adapter = new OfferImageSliderAdapter(context, bannerList);
            imageSlider.setSliderAdapter(adapter);
            imageSlider.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
            imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
            imageSlider.setIndicatorSelectedColor(Color.WHITE);
            imageSlider.setIndicatorUnselectedColor(Color.GRAY);
            imageSlider.setAutoCycle(true);
            imageSlider.setScrollTimeInSec(10);
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

        ib_website.setOnClickListener(v -> {
            String url = offerDetails.getUrl();

            if (!url.startsWith("https://") || !url.startsWith("http://")) {
                url = "http://" + url;
            }
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            context.startActivity(i);
        });

        cv_promo_code.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(offerDetails.getPromo_code());
            Utilities.showMessage("Promo code copied to clipboard", context, 1);
        });

        imb_share.setOnClickListener(v -> shareDetails());
    }

    private void shareDetails() {
        if (offerDetails.getDocuments().size() != 0) {
            if (doesAppNeedPermissions()) {
                if (!isStoragePermissionGiven(context, STORAGE_PERMISSION)) {
                    return;
                }
            }
            new DownloadFileAndMessageShare(context, "Offer Images", IMAGE_LINK + "offerdoc/business/" + offerDetails.getDocuments().get(0).getDocument(), getShareMessage());
        } else {
            String message = getShareMessage();
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, message);
            context.startActivity(Intent.createChooser(sharingIntent, "Choose from following"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (CALLTYPE.equals("1")) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menus_edit_delete, menu);

            if (offerDetails.getIs_approved().equals("1")) {
                menu.getItem(0).setVisible(false);
            }
            return true;
        } else
            return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setMessage("Are you sure you want to delete this offer?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.icon_alertred);
                builder.setCancelable(false);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Utilities.isNetworkAvailable(context)) {
                            new DeleteOffer().execute();
                        } else {
                            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                        }
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
                startActivity(new Intent(context, EditOffersActivity.class)
                        .putExtra("offerDetails", offerDetails)
                        .putExtra("categoryTypeId", "1")
                        .putExtra("categoryTypeName", "business"));
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    private class DeleteOffer extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res;
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "deleteOfferDetails"));
            param.add(new ParamsPojo("offer_id", offerDetails.getId()));
            res = APICall.FORMDATAAPICall(ApplicationConstants.OFFERSAPI, param);
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
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("MyAddedOffersActivity"));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("OffersForParticularRecordActivity"));
                        Utilities.showMessage("Offer deleted successfully", context, 1);
                        finish();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getShareMessage() {
        StringBuilder sb = new StringBuilder();

        if (!offerDetails.getRecord_name().equals("")) {
            sb.append(offerDetails.getRecord_name() + "\n");
        }

        sb.append(offerDetails.getTitle() + " - " + offerDetails.getDescription() + "\n");

        if (!offerDetails.getStart_date().equals("") && !offerDetails.getEnd_date().equals("")) {
            sb.append("Offer valid from " + changeDateFormat("yyyy-MM-dd", "dd-MMM-yyyy", offerDetails.getStart_date()) + " to " +
                    changeDateFormat("yyyy-MM-dd", "dd-MMM-yyyy", offerDetails.getEnd_date()) + "\n");
        }

        if (!offerDetails.getPromo_code().equals("")) {
            sb.append("Promo Code - " + offerDetails.getPromo_code() + "\n");
        }

        if (!offerDetails.getUrl().equals("")) {
            sb.append("Click Here - " + offerDetails.getUrl() + "\n");
        }

        return sb.toString() + "\n" + "shared via eorder\n" + "Click Here - " + ApplicationConstants.JOINSTA_PLAYSTORELINK;
    }

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_black);
        mToolbar.setNavigationOnClickListener(view -> finish());
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
