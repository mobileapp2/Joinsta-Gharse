package in.oriange.joinstagharse.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;

import de.hdodenhof.circleimageview.CircleImageView;
import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.models.MasterModel;
import in.oriange.joinstagharse.models.MasterPojo;
import in.oriange.joinstagharse.models.PrimaryPublicMobileSelectionModel;
import in.oriange.joinstagharse.models.PrimarySelectionModel;
import in.oriange.joinstagharse.models.UserProfileDetailsModel;
import in.oriange.joinstagharse.utilities.APICall;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.CountryCodeSelection;
import in.oriange.joinstagharse.utilities.MultipartUtility;
import in.oriange.joinstagharse.utilities.ParamsPojo;
import in.oriange.joinstagharse.utilities.UserSessionManager;
import in.oriange.joinstagharse.utilities.Utilities;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static in.oriange.joinstagharse.utilities.ApplicationConstants.IMAGE_LINK;
import static in.oriange.joinstagharse.utilities.PermissionUtil.PERMISSION_ALL;
import static in.oriange.joinstagharse.utilities.PermissionUtil.doesAppNeedPermissions;
import static in.oriange.joinstagharse.utilities.Utilities.changeStatusBar;
import static in.oriange.joinstagharse.utilities.Utilities.hideSoftKeyboard;
import static in.oriange.joinstagharse.utilities.Utilities.isLocationEnabled;
import static in.oriange.joinstagharse.utilities.Utilities.provideLocationAccess;
import static in.oriange.joinstagharse.utilities.Utilities.turnOnLocation;

public class EditBasicInformationActivity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CircleImageView imv_user;
    private EditText edt_fname, edt_mname, edt_lname, edt_bloodgroup, edt_education,
            edt_specify, edt_nativeplace, edt_reg_mobile, edt_about;
    private RadioButton rb_male, rb_female;
    private LinearLayout ll_mobile, ll_landline, ll_email;
    private Button btn_add_mobile, btn_add_landline, btn_add_email;
    private ImageButton ib_location;
    private MaterialButton btn_save;

    private ArrayList<MasterModel> bloodGroupList, educationList;
    private ArrayList<LinearLayout> mobileLayoutsList, landlineLayoutsList, emailLayoutsList;

    private String userId, password, bloodGroupId = "0", educationId = "0", genderId = "0", imageName = "", isActive, referralCode, latitude, longitude;

    private ArrayList<PrimaryPublicMobileSelectionModel> mobileList;
    private ArrayList<PrimarySelectionModel> landlineList, emailList;
    private int lastSelectedMobilePrimary = -1, lastSelectedLandlinePrimary = -1, lastSelectedEmailPrimary = -1;

    private Uri photoURI;
    private final int CAMERA_REQUEST = 100;
    private final int GALLERY_REQUEST = 200;
    private File file, photoFileToUpload, profilPicFolder;
    private String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    List<UserProfileDetailsModel.ResultBean.MobileNumbersBean> mobileSessionList;
    List<UserProfileDetailsModel.ResultBean.LandlineNumbersBean> landlineSessionList;
    List<UserProfileDetailsModel.ResultBean.EmailBean> emailSessionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_basic_information);

        init();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = EditBasicInformationActivity.this;
        session = new UserSessionManager(context);
        changeStatusBar(context, getWindow());
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        imv_user = findViewById(R.id.imv_user);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        edt_fname = findViewById(R.id.edt_fname);
        edt_mname = findViewById(R.id.edt_mname);
        edt_lname = findViewById(R.id.edt_lname);
        edt_bloodgroup = findViewById(R.id.edt_bloodgroup);
        edt_education = findViewById(R.id.edt_education);
        edt_specify = findViewById(R.id.edt_specify);
        edt_reg_mobile = findViewById(R.id.edt_reg_mobile);
        edt_nativeplace = findViewById(R.id.edt_nativeplace);
        edt_about = findViewById(R.id.edt_about);

        rb_male = findViewById(R.id.rb_male);
        rb_female = findViewById(R.id.rb_female);

        ll_mobile = findViewById(R.id.ll_mobile);
        ll_landline = findViewById(R.id.ll_landline);
        ll_email = findViewById(R.id.ll_email);

        btn_add_mobile = findViewById(R.id.btn_add_mobile);
        btn_add_landline = findViewById(R.id.btn_add_landline);
        btn_add_email = findViewById(R.id.btn_add_email);
        ib_location = findViewById(R.id.ib_location);
        btn_save = findViewById(R.id.btn_save);

        bloodGroupList = new ArrayList<>();
        educationList = new ArrayList<>();
        mobileLayoutsList = new ArrayList<>();
        landlineLayoutsList = new ArrayList<>();
        emailLayoutsList = new ArrayList<>();

        mobileList = new ArrayList<>();
        landlineList = new ArrayList<>();
        emailList = new ArrayList<>();

        mobileSessionList = new ArrayList<>();
        landlineSessionList = new ArrayList<>();
        emailSessionList = new ArrayList<>();

        profilPicFolder = new File(Environment.getExternalStorageDirectory() + "/Joinsta Gharse/" + "Basic Info");
        if (!profilPicFolder.exists()) {
            profilPicFolder.mkdirs();
        }

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    private void setDefault() {
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);

            userId = json.getString("userid");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Utilities.isNetworkAvailable(context)) {
            new RefreshSession().execute(userId);
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            getSessionDetails();
        }
    }

    private void getSessionDetails() {

        String s;
        JSONObject json = null;
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            json = user_info.getJSONObject(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        UserProfileDetailsModel.ResultBean userDetails = new Gson().fromJson(json.toString(), UserProfileDetailsModel.ResultBean.class);
//        UserProfileDetailsModel.ResultBean userDetails = userDetailsList.get(0);

        userId = userDetails.getUserid();
        password = userDetails.getPassword();
        bloodGroupId = userDetails.getBlood_group_id();
        educationId = userDetails.getEducation_id();
        genderId = userDetails.getGender_id();
        imageName = userDetails.getImage_url();
        isActive = userDetails.getIs_active();
        referralCode = userDetails.getReferral_code();
        latitude = userDetails.getLatitude();
        longitude = userDetails.getLongitude();

        edt_bloodgroup.setText(userDetails.getBlood_group_description());
        edt_education.setText(userDetails.getEducation_description());
        edt_specify.setText(userDetails.getSpecific_education());
        edt_nativeplace.setText(userDetails.getNative_place());
        edt_fname.setText(userDetails.getFirst_name());
        edt_mname.setText(userDetails.getMiddle_name());
        edt_lname.setText(userDetails.getLast_name());
        edt_about.setText(userDetails.getAbout());
        edt_reg_mobile.setText(userDetails.getCountry_code() + userDetails.getMobile());


        if (!imageName.equals("")) {
            String url = IMAGE_LINK + userId + "/" + imageName;
            Picasso.with(context)
                    .load(url)
                    .placeholder(R.drawable.icon_userphoto)
                    .into(imv_user);
        }

        if (genderId.equals("1")) {
            rb_male.setChecked(true);
        } else if (genderId.equals("2")) {
            rb_female.setChecked(true);
        }

        mobileSessionList = userDetails.getMobile_numbers();
        landlineSessionList = userDetails.getLandline_numbers();
        emailSessionList = userDetails.getEmail();

        if (mobileSessionList != null)
            if (mobileSessionList.size() > 0)
                for (int i = 0; i < mobileSessionList.size(); i++) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View rowView = inflater.inflate(R.layout.layout_add_mobile, null);
                    LinearLayout ll = (LinearLayout) rowView;
                    mobileLayoutsList.add(ll);
                    ll_mobile.addView(rowView, ll_mobile.getChildCount() - 1);
                    ((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).setText(mobileSessionList.get(i).getMobile());
                    ((TextView) mobileLayoutsList.get(i).findViewById(R.id.tv_countrycode_mobile)).setText(mobileSessionList.get(i).getCountry_code());
                }
            else
                addMobileLayout();
        else
            addMobileLayout();


        if (landlineSessionList != null)
            if (landlineSessionList.size() > 0)
                for (int i = 0; i < landlineSessionList.size(); i++) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View rowView = inflater.inflate(R.layout.layout_add_landline, null);
                    LinearLayout ll = (LinearLayout) rowView;
                    landlineLayoutsList.add(ll);
                    ll_landline.addView(rowView, ll_landline.getChildCount() - 1);
                    ((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).setText(landlineSessionList.get(i).getLandline_number());
                    ((TextView) landlineLayoutsList.get(i).findViewById(R.id.tv_countrycode_landline)).setText(landlineSessionList.get(i).getCountry_code());
                }
            else
                addLandLineLayout();
        else
            addLandLineLayout();

        if (emailSessionList != null)
            if (emailSessionList.size() > 0)
                for (int i = 0; i < emailSessionList.size(); i++) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View rowView = inflater.inflate(R.layout.layout_add_email, null);
                    emailLayoutsList.add((LinearLayout) rowView);
                    ll_email.addView(rowView, ll_email.getChildCount() - 1);
                    ((EditText) emailLayoutsList.get(i).findViewById(R.id.edt_email)).setText(emailSessionList.get(i).getEmail());
                    if (emailSessionList.get(i).getEmail_verification().equals("0")) {
                        (emailLayoutsList.get(i).findViewById(R.id.tv_verify)).setVisibility(View.VISIBLE);
                        (emailLayoutsList.get(i).findViewById(R.id.tv_verified)).setVisibility(View.GONE);
                    } else {
                        (emailLayoutsList.get(i).findViewById(R.id.tv_verified)).setVisibility(View.VISIBLE);
                        (emailLayoutsList.get(i).findViewById(R.id.tv_verify)).setVisibility(View.GONE);
                    }
                }
            else
                addEmailLayout();
        else
            addEmailLayout();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setEventHandler() {

        imv_user.setOnClickListener(view -> {
            if (Utilities.isNetworkAvailable(context)) {
                if (doesAppNeedPermissions()) {
                    askPermission();
                } else {
                    selectImage();
                }
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (Utilities.isNetworkAvailable(context)) {
                new RefreshSession().execute(userId);
                swipeRefreshLayout.setRefreshing(false);
            } else {
                swipeRefreshLayout.setRefreshing(false);
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }

        });

        edt_bloodgroup.setOnClickListener(v -> {
            if (Utilities.isNetworkAvailable(context)) {
                new GetBloodGroupList().execute();
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }
        });

        edt_education.setOnClickListener(v -> {
            if (Utilities.isNetworkAvailable(context)) {
                new GetEducationList().execute();
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }
        });

        btn_add_mobile.setOnClickListener(v -> {
            addMobileLayout();
        });

        btn_add_landline.setOnClickListener(v -> {
            addLandLineLayout();
        });

        btn_add_email.setOnClickListener(v -> {
            addEmailLayout();
        });

        ib_location.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED /*&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED*/) {
                provideLocationAccess(context);
            } else {
                if (!isLocationEnabled(context)) {
                    turnOnLocation(context);
                } else {
                    startLocationUpdates();
                    if (MainDrawerActivity.latLng != null) {
                        latLng = MainDrawerActivity.latLng;
                        if (Utilities.isNetworkAvailable(context)) {
                            new GetAddress().execute(
                                    String.valueOf(latLng.latitude),
                                    String.valueOf(latLng.longitude));
                        } else {
                            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                        }
                    } else {
                        if (latLng != null) {
                            if (Utilities.isNetworkAvailable(context)) {
                                new GetAddress().execute(
                                        String.valueOf(latLng.latitude),
                                        String.valueOf(latLng.longitude));
                            } else {
                                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                            }
                        } else {
                            edt_nativeplace.setError("Unable to get address from this location. Please try again or entry your current city manually");
                        }
                    }


                }
            }
        });

        btn_save.setOnClickListener(v -> {
            validateData();
        });
    }

    private void selectImage() {
        final CharSequence[] options = {"Take a Photo", "Choose from Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builder.setCancelable(false);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take a Photo")) {
                    file = new File(profilPicFolder, "doc_image.png");
                    photoURI = Uri.fromFile(file);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(intent, CAMERA_REQUEST);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, GALLERY_REQUEST);
                }
            }
        });
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertD = builder.create();
        alertD.show();
    }

    private class RefreshSession extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "getUserDetails");
            obj.addProperty("user_id", userId);
            res = APICall.JSONAPICall(ApplicationConstants.USERSAPI, obj.toString());
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
                    if (type.equalsIgnoreCase("success")) {

                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                session.createUserLoginSession(jsonarr.toString());
                            }
                        }
                        getSessionDetails();
                    } else {
                        Utilities.showMessage("User details failed to update", context, 3);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST) {
                Uri imageUri = data.getData();
                CropImage.activity(imageUri).setActivityMenuIconColor(getResources().getColor(R.color.black)).setGuidelines(CropImageView.Guidelines.ON).start(EditBasicInformationActivity.this);
            }
            if (requestCode == CAMERA_REQUEST) {
                CropImage.activity(photoURI).setActivityMenuIconColor(getResources().getColor(R.color.black)).setGuidelines(CropImageView.Guidelines.ON).start(EditBasicInformationActivity.this);
            }

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                savefile(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void savefile(Uri sourceuri) {
        Log.i("sourceuri1", "" + sourceuri);
        String sourceFilename = sourceuri.getPath();
        String destinationFile = Environment.getExternalStorageDirectory() + "/Joinsta Gharse/"
                + "Basic Info/" + "uplimg.png";

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(sourceFilename));
            bos = new BufferedOutputStream(new FileOutputStream(destinationFile, false));
            byte[] buf = new byte[1024];
            bis.read(buf);
            do {
                bos.write(buf);
            } while (bis.read(buf) != -1);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        photoFileToUpload = new File(destinationFile);
        new UploadUserImage().execute(photoFileToUpload);

    }

    private class UploadUserImage extends AsyncTask<File, Integer, String> {
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
        protected String doInBackground(File... params) {
            String res = "";
            try {
                MultipartUtility multipart = new MultipartUtility(ApplicationConstants.FILEUPLOADAPI, "UTF-8");

                multipart.addFormField("request_type", "uploadFile");
                multipart.addFormField("user_id", userId);
                multipart.addFilePart("document", params[0]);

                List<String> response = multipart.finish();
                for (String line : response) {
                    res = res + line;
                }
                return res;
            } catch (IOException ex) {
                return ex.toString();
            }
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
                        JSONObject jsonObject = mainObj.getJSONObject("result");
                        String imageUrl = jsonObject.getString("document_url");
                        imageName = jsonObject.getString("name");

                        if (!imageUrl.equals("")) {
                            Picasso.with(context)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.icon_userphoto)
                                    .into(imv_user);
                        }
                    } else {
                        Utilities.showMessage("Image upload failed", context, 3);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class GetBloodGroupList extends AsyncTask<String, Void, String> {

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
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "AllBloodGroup"));
            res = APICall.FORMDATAAPICall(ApplicationConstants.MASTERAPI, param);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    MasterPojo pojoDetails = new Gson().fromJson(result, MasterPojo.class);
                    type = pojoDetails.getType();
                    message = pojoDetails.getMessage();

                    if (type.equalsIgnoreCase("success")) {
                        bloodGroupList = pojoDetails.getResult();
                        if (bloodGroupList.size() > 0) {
                            showBloodGroupListDialog();
                        }
                    } else {
                        Utilities.showAlertDialog(context, message, false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showAlertDialog(context, "Server Not Responding", false);
            }
        }
    }

    private void showBloodGroupListDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Blood Group");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.list_row);

        for (int i = 0; i < bloodGroupList.size(); i++) {

            arrayAdapter.add(String.valueOf(bloodGroupList.get(i).getName()));
        }

        builderSingle.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setNeutralButton("None of Above", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                edt_bloodgroup.setText("");
                bloodGroupId = "0";
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                edt_bloodgroup.setText(bloodGroupList.get(which).getName());
                bloodGroupId = bloodGroupList.get(which).getId();

            }
        });
        AlertDialog alertD = builderSingle.create();
        alertD.show();
    }

    private class GetEducationList extends AsyncTask<String, Void, String> {

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
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "AllEducationList"));
            res = APICall.FORMDATAAPICall(ApplicationConstants.MASTERAPI, param);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    MasterPojo pojoDetails = new Gson().fromJson(result, MasterPojo.class);
                    type = pojoDetails.getType();
                    message = pojoDetails.getMessage();

                    if (type.equalsIgnoreCase("success")) {
                        educationList = pojoDetails.getResult();
                        if (educationList.size() > 0) {
                            showEducationDialog();
                        }
                    } else {
                        Utilities.showAlertDialog(context, message, false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showAlertDialog(context, "Server Not Responding", false);
            }
        }
    }

    private void showEducationDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Education");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.list_row);

        for (int i = 0; i < educationList.size(); i++) {

            arrayAdapter.add(String.valueOf(educationList.get(i).getName()));
        }

        builderSingle.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setNeutralButton("None of Above", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                edt_education.setText("");
                educationId = "0";
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                edt_education.setText(educationList.get(which).getName());
                educationId = educationList.get(which).getId();

            }
        });
        AlertDialog alertD = builderSingle.create();
        alertD.show();
    }

    private void addMobileLayout() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.layout_add_mobile, null);
        LinearLayout ll = (LinearLayout) rowView;
        mobileLayoutsList.add(ll);
        ll_mobile.addView(rowView, ll_mobile.getChildCount() - 1);
    }

    private void addLandLineLayout() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.layout_add_landline, null);
        LinearLayout ll = (LinearLayout) rowView;
        landlineLayoutsList.add(ll);
        ll_landline.addView(rowView, ll_landline.getChildCount() - 1);
    }

    private void addEmailLayout() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.layout_add_email, null);
        LinearLayout ll = (LinearLayout) rowView;
        emailLayoutsList.add(ll);
        ll_email.addView(rowView, ll_email.getChildCount() - 1);
    }

    public void removeMobileLayout(View view) {
        ll_mobile.removeView((View) view.getParent());
        mobileLayoutsList.remove(view.getParent());
    }

    public void removeLandlineLayout(View view) {
        ll_landline.removeView((View) view.getParent());
        landlineLayoutsList.remove(view.getParent());
    }

    public void removeEmailLayout(View view) {
        ll_email.removeView((View) view.getParent());
        emailLayoutsList.remove(view.getParent());
    }

    public void verifyEmail(View view) {
        LinearLayout ll_email = (LinearLayout) view.getParent();
        EditText edt_email = ll_email.findViewById(R.id.edt_email);

        JsonObject obj = new JsonObject();
        obj.addProperty("type", "sendverificationlink");
        obj.addProperty("email_id", edt_email.getText().toString().trim());
        obj.addProperty("user_id", userId);

        if (Utilities.isNetworkAvailable(context)) {
            new SendVerificationLink().execute(obj.toString());
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }
    }

    private void validateData() {

        mobileList = new ArrayList<>();
        landlineList = new ArrayList<>();
        emailList = new ArrayList<>();

        if (edt_fname.getText().toString().trim().isEmpty()) {
            edt_fname.setError("Please enter name");
            edt_fname.requestFocus();
            return;
        }

        if (rb_male.isChecked()) {
            genderId = "1";
        } else if (rb_female.isChecked()) {
            genderId = "2";
        }

        for (int i = 0; i < mobileLayoutsList.size(); i++) {
            if (!((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).getText().toString().trim().isEmpty()) {
                if (!Utilities.isValidMobileno(((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).getText().toString().trim())) {
                    ((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).setError("Please enter mobile number");
                    (mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).requestFocus();
                    return;
                }
            }
        }

        for (int i = 0; i < landlineLayoutsList.size(); i++) {
            if (!((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).getText().toString().trim().isEmpty()) {
                if (!Utilities.isLandlineValid(((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).getText().toString().trim())) {
                    ((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).setError("Please enter valid landline number");
                    (landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).requestFocus();
                    return;
                }
            }
        }

        for (int i = 0; i < emailLayoutsList.size(); i++) {
            if (!((EditText) emailLayoutsList.get(i).findViewById(R.id.edt_email)).getText().toString().trim().isEmpty()) {
                if (!Utilities.isEmailValid(((EditText) emailLayoutsList.get(i).findViewById(R.id.edt_email)).getText().toString().trim())) {
                    ((EditText) emailLayoutsList.get(i).findViewById(R.id.edt_email)).setError("Please enter valid email");
                    (emailLayoutsList.get(i).findViewById(R.id.edt_email)).requestFocus();
                    return;
                }
            }
        }

        for (int i = 0; i < mobileLayoutsList.size(); i++) {
            if (!((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).getText().toString().trim().equals("")) {
                if (i < mobileSessionList.size() - 1) {
                    mobileList.add(new PrimaryPublicMobileSelectionModel(((TextView) mobileLayoutsList.get(i).findViewById(R.id.tv_countrycode_mobile)).getText().toString(),
                            ((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).getText().toString().trim(), "0", "0", mobileSessionList.get(i).getUser_moblie_id()));
                } else {
                    mobileList.add(new PrimaryPublicMobileSelectionModel(((TextView) mobileLayoutsList.get(i).findViewById(R.id.tv_countrycode_mobile)).getText().toString(),
                            ((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).getText().toString().trim(), "0", "0", "0"));
                }
            }
        }

        for (int i = 0; i < landlineLayoutsList.size(); i++) {
            if (!((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).getText().toString().trim().equals("")) {
                if (i < landlineSessionList.size() - 1) {
                    landlineList.add(new PrimarySelectionModel(((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).getText().toString().trim(), "0",
                            landlineSessionList.get(i).getUser_landline_id(), ((TextView) landlineLayoutsList.get(i).findViewById(R.id.tv_countrycode_landline)).getText().toString()));
                } else {
                    landlineList.add(new PrimarySelectionModel(((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).getText().toString().trim(), "0",
                            "0", ((TextView) landlineLayoutsList.get(i).findViewById(R.id.tv_countrycode_landline)).getText().toString()));
                }
            }
        }

        for (int i = 0; i < emailLayoutsList.size(); i++) {
            if (!((EditText) emailLayoutsList.get(i).findViewById(R.id.edt_email)).getText().toString().trim().equals("")) {
                if (i < emailSessionList.size() - 1) {
                    emailList.add(new PrimarySelectionModel(((EditText) emailLayoutsList.get(i).findViewById(R.id.edt_email)).getText().toString().trim(), emailSessionList.get(i).getIs_primary(), emailSessionList.get(i).getUser_email_id(), ""));
                } else {
                    emailList.add(new PrimarySelectionModel(((EditText) emailLayoutsList.get(i).findViewById(R.id.edt_email)).getText().toString().trim(), "0", "0", ""));
                }
            }
        }

        if (emailList.size() != 0) {
            boolean isAlreadyPrimEmailExists = false;
            for (int i = 0; i < emailList.size(); i++) {
                if (emailList.get(i).getIsPrimary().equals("1")) {
                    isAlreadyPrimEmailExists = true;
                    submitData();
                }
            }

            if (!isAlreadyPrimEmailExists)
                showPrimaryEmailDialog();
        } else {
            submitData();
        }
    }

    public void selectContryCode(View v) {
        TextView tv_selected_forconcode = (TextView) v;
        new CountryCodeSelection(context, tv_selected_forconcode);
    }

    private void showPrimaryMobileDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_primary_selection, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialogBuilder.setTitle("Select Primary Mobile");
        alertDialogBuilder.setView(promptView);

        final RecyclerView rv_list = promptView.findViewById(R.id.rv_list);
        Button btn_next = promptView.findViewById(R.id.btn_next);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rv_list.setLayoutManager(layoutManager);
        rv_list.setAdapter(new MobilePrimaryAdapter());


        alertDialogBuilder.setCancelable(false);
        final AlertDialog alertD = alertDialogBuilder.create();

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < mobileList.size(); i++) {

                    MobilePrimaryAdapter.MyViewHolder myViewHolder =
                            (MobilePrimaryAdapter.MyViewHolder) rv_list.findViewHolderForAdapterPosition(i);

                    if (myViewHolder.rb_selectone.isChecked()) {
                        mobileList.get(i).setIsPrimary("1");
                    }
                }

                alertD.dismiss();
                showPublicMobileDialog();
            }
        });

        alertD.show();
    }

    private class MobilePrimaryAdapter extends RecyclerView.Adapter<MobilePrimaryAdapter.MyViewHolder> {
        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public MobilePrimaryAdapter() {

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_primary, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.rb_selectone.setText(mobileList.get(position).getCountry_code() + mobileList.get(position).getMobile());

            holder.rb_selectone.setChecked(lastSelectedMobilePrimary == position);
        }

        @Override
        public int getItemCount() {
            return mobileList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private RadioButton rb_selectone;

            public MyViewHolder(View view) {
                super(view);

                rb_selectone = view.findViewById(R.id.rb_selectone);

                rb_selectone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lastSelectedMobilePrimary = getAdapterPosition();
                        notifyDataSetChanged();
                    }
                });


            }
        }
    }

    private void showPublicMobileDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_primary_selection, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialogBuilder.setTitle("Select Public Mobile");
        alertDialogBuilder.setView(promptView);

        final RecyclerView rv_list = promptView.findViewById(R.id.rv_list);
        Button btn_next = promptView.findViewById(R.id.btn_next);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rv_list.setLayoutManager(layoutManager);
        rv_list.setAdapter(new MobilePublicAdapter());

        alertDialogBuilder.setCancelable(false);
        final AlertDialog alertD = alertDialogBuilder.create();

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < mobileList.size(); i++) {

                    MobilePublicAdapter.MyViewHolder myViewHolder =
                            (MobilePublicAdapter.MyViewHolder) rv_list.findViewHolderForAdapterPosition(i);

                    if (myViewHolder.cb_selected.isChecked()) {
                        mobileList.get(i).setIsPublic("1");
                    }
                }

                alertD.dismiss();
                showPrimaryLandlineDialog();
            }
        });

        alertD.show();
    }

    private class MobilePublicAdapter extends RecyclerView.Adapter<MobilePublicAdapter.MyViewHolder> {
        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public MobilePublicAdapter() {

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_public, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.cb_selected.setText(mobileList.get(position).getCountry_code() + mobileList.get(position).getMobile());
        }

        @Override
        public int getItemCount() {
            return mobileList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private CheckBox cb_selected;

            public MyViewHolder(View view) {
                super(view);

                cb_selected = view.findViewById(R.id.cb_selected);

            }
        }
    }

    private void showPrimaryLandlineDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_primary_selection, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialogBuilder.setTitle("Select Primary Landline");
        alertDialogBuilder.setView(promptView);

        final RecyclerView rv_list = promptView.findViewById(R.id.rv_list);
        Button btn_next = promptView.findViewById(R.id.btn_next);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rv_list.setLayoutManager(layoutManager);
        rv_list.setAdapter(new LandLinePrimaryAdapter());


        alertDialogBuilder.setCancelable(false);
        final AlertDialog alertD = alertDialogBuilder.create();

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < landlineList.size(); i++) {

                    LandLinePrimaryAdapter.MyViewHolder myViewHolder =
                            (LandLinePrimaryAdapter.MyViewHolder) rv_list.findViewHolderForAdapterPosition(i);

                    if (myViewHolder.rb_selectone.isChecked()) {
                        landlineList.get(i).setIsPrimary("1");
                    }
                }

                alertD.dismiss();
                showPrimaryEmailDialog();
            }
        });

        alertD.show();
    }

    private class LandLinePrimaryAdapter extends RecyclerView.Adapter<LandLinePrimaryAdapter.MyViewHolder> {
        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public LandLinePrimaryAdapter() {

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_primary, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.rb_selectone.setText(landlineList.get(position).getDetails());

            holder.rb_selectone.setChecked(lastSelectedLandlinePrimary == position);
        }

        @Override
        public int getItemCount() {
            return landlineList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private RadioButton rb_selectone;

            public MyViewHolder(View view) {
                super(view);

                rb_selectone = view.findViewById(R.id.rb_selectone);

                rb_selectone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lastSelectedLandlinePrimary = getAdapterPosition();
                        notifyDataSetChanged();
                    }
                });


            }
        }
    }

    private void showPrimaryEmailDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_primary_selection, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialogBuilder.setTitle("Select Primary Email");
        alertDialogBuilder.setView(promptView);

        final RecyclerView rv_list = promptView.findViewById(R.id.rv_list);
        Button btn_next = promptView.findViewById(R.id.btn_next);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rv_list.setLayoutManager(layoutManager);
        rv_list.setAdapter(new EmailPrimaryAdapter());


        alertDialogBuilder.setCancelable(false);
        final AlertDialog alertD = alertDialogBuilder.create();

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < emailList.size(); i++) {

                    EmailPrimaryAdapter.MyViewHolder myViewHolder =
                            (EmailPrimaryAdapter.MyViewHolder) rv_list.findViewHolderForAdapterPosition(i);

                    if (myViewHolder.rb_selectone.isChecked()) {
                        emailList.get(i).setIsPrimary("1");
                    }
                }

                alertD.dismiss();
                submitData();
            }
        });

        alertD.show();
    }

    private class EmailPrimaryAdapter extends RecyclerView.Adapter<EmailPrimaryAdapter.MyViewHolder> {
        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public EmailPrimaryAdapter() {

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_primary, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.rb_selectone.setText(emailList.get(position).getDetails());
            holder.rb_selectone.setChecked(lastSelectedEmailPrimary == position);
        }

        @Override
        public int getItemCount() {
            return emailList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private RadioButton rb_selectone;

            public MyViewHolder(View view) {
                super(view);

                rb_selectone = view.findViewById(R.id.rb_selectone);

                rb_selectone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lastSelectedEmailPrimary = getAdapterPosition();
                        notifyDataSetChanged();
                    }
                });


            }
        }
    }

    private void submitData() {
        JsonArray mobileJSONArray = new JsonArray();
        JsonArray landlineJSONArray = new JsonArray();
        JsonArray emailJSONArray = new JsonArray();

        JsonObject mainObj = new JsonObject();

        for (int i = 0; i < mobileList.size(); i++) {
            JsonObject mobileJSONObj = new JsonObject();
            mobileJSONObj.addProperty("country_code", mobileList.get(i).getCountry_code());
            mobileJSONObj.addProperty("mobile", mobileList.get(i).getMobile());
            mobileJSONObj.addProperty("is_primary", "1");
            mobileJSONObj.addProperty("is_public", "1");
            mobileJSONObj.addProperty("id", mobileList.get(i).getId());
            mobileJSONArray.add(mobileJSONObj);
        }

        for (int i = 0; i < landlineList.size(); i++) {
            JsonObject landlineJSONObj = new JsonObject();
            landlineJSONObj.addProperty("country_code", landlineList.get(i).getCountry_code());
            landlineJSONObj.addProperty("landline_number", landlineList.get(i).getDetails());
            landlineJSONObj.addProperty("is_primary", "1");
            landlineJSONObj.addProperty("id", landlineList.get(i).getId());
            landlineJSONArray.add(landlineJSONObj);
        }

        for (int i = 0; i < emailList.size(); i++) {
            JsonObject emailJSONObj = new JsonObject();
            emailJSONObj.addProperty("email_id", emailList.get(i).getDetails());
            emailJSONObj.addProperty("is_primary", emailList.get(i).getIsPrimary());
            emailJSONObj.addProperty("id", emailList.get(i).getId());
            emailJSONArray.add(emailJSONObj);
        }

        mainObj.addProperty("type", "updateusers");
        mainObj.addProperty("first_name", edt_fname.getText().toString().trim());
        mainObj.addProperty("last_name", edt_lname.getText().toString().trim());
        mainObj.addProperty("middle_name", edt_mname.getText().toString().trim());
        mainObj.addProperty("gender_id", genderId);
        mainObj.addProperty("blood_group_id", bloodGroupId);
        mainObj.addProperty("education_id", educationId);
        mainObj.addProperty("specific_education", edt_specify.getText().toString().trim());
        mainObj.addProperty("referral_code", referralCode);
        mainObj.addProperty("is_active", isActive);
        mainObj.addProperty("password", password);
        mainObj.addProperty("image_url", imageName);
        mainObj.addProperty("native_place", edt_nativeplace.getText().toString().trim());
        mainObj.addProperty("latitude", latitude);
        mainObj.addProperty("longitude", longitude);
        mainObj.addProperty("about", edt_about.getText().toString().trim());
        mainObj.add("mobile1", mobileJSONArray);
        mainObj.add("landline_number", landlineJSONArray);
        mainObj.add("email", emailJSONArray);
        mainObj.addProperty("user_id", userId);

        Log.i("BASICINFOJSON", mainObj.toString());
        if (Utilities.isNetworkAvailable(context)) {
            new UpdateUser().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }

    }

    private class UpdateUser extends AsyncTask<String, Void, String> {

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
            res = APICall.JSONAPICall(ApplicationConstants.USERSAPI, params[0]);
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

                        JSONArray jsonarr = mainObj.getJSONArray("result");
                        if (jsonarr.length() > 0) {
                            for (int i = 0; i < jsonarr.length(); i++) {
                                session.createUserLoginSession(jsonarr.toString());

                                LayoutInflater layoutInflater = LayoutInflater.from(context);
                                View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                                alertDialogBuilder.setView(promptView);

                                LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                                TextView tv_title = promptView.findViewById(R.id.tv_title);
                                Button btn_ok = promptView.findViewById(R.id.btn_ok);

                                animation_view.playAnimation();
                                tv_title.setText("User details updated successfully");
                                alertDialogBuilder.setCancelable(false);
                                final AlertDialog alertD = alertDialogBuilder.create();

                                btn_ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertD.dismiss();
                                        finish();
                                    }
                                });

                                alertD.show();
                            }
                        }

                    } else {
                        Utilities.showMessage("User details failed to update", context, 3);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class SendVerificationLink extends AsyncTask<String, Void, String> {

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
            res = APICall.JSONAPICall(ApplicationConstants.EMAILVERIFYAPI, params[0]);
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
                        Utilities.showAlertDialog(context, "Verification link is sent on your email, " +
                                "when you have successfully verified your email through that link please kindly swipe down " +
                                "to refresh your email verification status", true);
                    } else {
                        Utilities.showMessage("User details failed to update", context, 3);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_black);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void askPermission() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(PERMISSIONS, PERMISSION_ALL);
            return;
        } else {
            selectImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    selectImage();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                    builder.setTitle("Alert");
                    builder.setMessage("Please provide permission for Camera and Gallery");
                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", context.getPackageName(), null)));
                        }
                    });
                    builder.create();
                    AlertDialog alertD = builder.create();
                    alertD.show();
                }
            }

        }
    }

    private class GetAddress extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Address> doInBackground(String... params) {
            Geocoder geocoder;
            List<Address> addresses = null;

            try {
                geocoder = new Geocoder(context, Locale.getDefault());
                addresses = geocoder.getFromLocation(Double.parseDouble(params[0]), Double.parseDouble(params[1]), 1); // Here 1 represent max icon_location result to returned, by documents it recommended 1 to 5
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {
            super.onPostExecute(addresses);

            if (addresses != null && !addresses.isEmpty()) {
                edt_nativeplace.setText(addresses.get(0).getLocality());
                if (latLng != null) {
                    longitude = String.valueOf(latLng.longitude);
                    latitude = String.valueOf(latLng.latitude);
                }
            } else {
                edt_nativeplace.setError("Unable to get address from this location. Please try again or entry your current city manually");
            }

        }
    }

    private LatLng latLng;

    @SuppressLint("RestrictedApi")
    private void startLocationUpdates() {

        // Create the location request to start receiving updates
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        /* 10 secs */
        long UPDATE_INTERVAL = 10 * 10000000;
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        /* 2 sec */
        long FASTEST_INTERVAL = 20000;
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(context);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        getFusedLocationProviderClient(context).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    private void onLocationChanged(Location location) {
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftKeyboard(EditBasicInformationActivity.this);
    }


}
