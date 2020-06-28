package in.oriange.joinstagharse.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;

import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.utilities.APICall;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.MultipartUtility;
import in.oriange.joinstagharse.utilities.UserSessionManager;
import in.oriange.joinstagharse.utilities.Utilities;

import static in.oriange.joinstagharse.utilities.PermissionUtil.doesAppNeedPermissions;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.CALL_PHONE_PERMISSION_REQUEST;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.CAMERA_AND_STORAGE_PERMISSION;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.CAMERA_AND_STORAGE_PERMISSION_REQUEST;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.LOCATION_PERMISSION_REQUEST;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.READ_CONTACTS_PERMISSION_REQUEST;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.STORAGE_PERMISSION_REQUEST;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.callPermissionMsg;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.cameraStoragePermissionMsg;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.isCameraStoragePermissionGiven;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.locationPermissionMsg;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.manualPermission;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.readContactsPermissionMsg;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.storagePermissionMsg;
import static in.oriange.joinstagharse.utilities.Utilities.changeDateFormat;
import static in.oriange.joinstagharse.utilities.Utilities.changeStatusBar;
import static in.oriange.joinstagharse.utilities.Utilities.hideSoftKeyboard;
import static in.oriange.joinstagharse.utilities.Utilities.setPaddingForView;
import static in.oriange.joinstagharse.utilities.Utilities.yyyyMMddDate;

public class AddOffersActivity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;

    private EditText edt_title, edt_description, edt_start_date, edt_end_date, edt_url, edt_promo_code;
    private ImageView imv_image_one, imv_image_one_delete, imv_image_two, imv_image_two_delete, imv_image_three, imv_image_three_delete;
    private MaterialButton btn_save;

    private int mYear, mMonth, mDay, mYear1, mMonth1, mDay1;
    private String userId, startDate, endDate, recordId, categoryTypeId, categoryTypeName;

    private File photoFileFolder;
    private Uri photoURI;
    private final int CAMERA_REQUEST = 100, GALLERY_REQUEST = 200;
    private int IMAGE_TYPE = 0;
    private String imageOneName = "", imageTwoName = "", imageThreeName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_offers);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = AddOffersActivity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);
        changeStatusBar(context, getWindow());
        edt_title = findViewById(R.id.edt_title);
        edt_start_date = findViewById(R.id.edt_start_date);
        edt_end_date = findViewById(R.id.edt_end_date);
        edt_url = findViewById(R.id.edt_url);
        edt_promo_code = findViewById(R.id.edt_promo_code);
        edt_description = findViewById(R.id.edt_description);
        imv_image_one = findViewById(R.id.imv_image_one);
        imv_image_one_delete = findViewById(R.id.imv_image_one_delete);
        imv_image_two = findViewById(R.id.imv_image_two);
        imv_image_two_delete = findViewById(R.id.imv_image_two_delete);
        imv_image_three = findViewById(R.id.imv_image_three);
        imv_image_three_delete = findViewById(R.id.imv_image_three_delete);
        btn_save = findViewById(R.id.btn_save);


        photoFileFolder = new File(Environment.getExternalStorageDirectory() + "/Joinsta Gharse/" + "Offer Images");
        if (!photoFileFolder.exists())
            photoFileFolder.mkdirs();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
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

    private void setDefault() {
        recordId = getIntent().getStringExtra("recordId");
        categoryTypeId = getIntent().getStringExtra("categoryTypeId");
        categoryTypeName = getIntent().getStringExtra("categoryTypeName");

        Calendar calendar = Calendar.getInstance();

        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        mYear1 = calendar.get(Calendar.YEAR);
        mMonth1 = calendar.get(Calendar.MONTH);
        mDay1 = calendar.get(Calendar.DAY_OF_MONTH);
    }

    private void setEventHandler() {
        edt_start_date.setOnClickListener(v -> {

            DatePickerDialog dialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                edt_end_date.setText("");
                startDate = yyyyMMddDate(dayOfMonth, month + 1, year);
                edt_start_date.setText(changeDateFormat("yyyy-MM-dd", "dd-MM-yyyy", startDate));

                mYear = year;
                mMonth = month;
                mDay = dayOfMonth;
            }, mYear, mMonth, mDay);
            try {
                Calendar c = Calendar.getInstance();
                c.set(mYear, mMonth + 1, mDay);

                dialog.getDatePicker().setCalendarViewShown(false);
                dialog.getDatePicker().setMinDate(System.currentTimeMillis());
                dialog.getDatePicker().setMaxDate(c.getTimeInMillis());
            } catch (Exception e) {
                e.printStackTrace();
            }
            dialog.show();
        });

        edt_end_date.setOnClickListener(v -> {
            if (edt_start_date.getText().toString().trim().isEmpty()) {
                edt_start_date.setError("Please select start date");
                edt_start_date.requestFocus();
                return;
            }

            DatePickerDialog dialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                endDate = yyyyMMddDate(dayOfMonth, month + 1, year);
                edt_end_date.setText(changeDateFormat("yyyy-MM-dd", "dd-MM-yyyy", endDate));

                mYear1 = year;
                mMonth1 = month;
                mDay1 = dayOfMonth;
            }, mYear1, mMonth1, mDay1);
            try {
                dialog.getDatePicker().setCalendarViewShown(false);
                Calendar calendar = Calendar.getInstance();
                calendar.set(mYear, mMonth, mDay);
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(mYear, mMonth + 1, mDay);
                dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                dialog.getDatePicker().setMaxDate(calendar1.getTimeInMillis());
            } catch (Exception e) {
                e.printStackTrace();
            }
            dialog.show();
        });

        imv_image_one.setOnClickListener(v -> {
            IMAGE_TYPE = 1;
            selectImage();
        });

        imv_image_two.setOnClickListener(v -> {
            IMAGE_TYPE = 2;
            selectImage();
        });

        imv_image_three.setOnClickListener(v -> {
            IMAGE_TYPE = 3;
            selectImage();
        });

        imv_image_one_delete.setOnClickListener(v -> {
            imageOneName = "";
            setPaddingForView(context, imv_image_one, 40);
            imv_image_one.setImageDrawable(getResources().getDrawable(R.drawable.icon_add_orange));
            imv_image_one_delete.setVisibility(View.GONE);
        });

        imv_image_two_delete.setOnClickListener(v -> {
            imageTwoName = "";
            setPaddingForView(context, imv_image_two, 40);
            imv_image_two.setImageDrawable(getResources().getDrawable(R.drawable.icon_add_orange));
            imv_image_two_delete.setVisibility(View.GONE);
        });

        imv_image_three_delete.setOnClickListener(v -> {
            imageThreeName = "";
            setPaddingForView(context, imv_image_three, 40);
            imv_image_three.setImageDrawable(getResources().getDrawable(R.drawable.icon_add_orange));
            imv_image_three_delete.setVisibility(View.GONE);
        });

        btn_save.setOnClickListener(v -> submitData());
    }

    private void selectImage() {
        if (doesAppNeedPermissions()) {
            if (!isCameraStoragePermissionGiven(context, CAMERA_AND_STORAGE_PERMISSION)) {
                return;
            }
        }

        final CharSequence[] options = {"Take a Photo", "Choose from Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builder.setCancelable(false);
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take a Photo")) {
                File file = new File(photoFileFolder, "doc_image.png");
                photoURI = Uri.fromFile(file);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, CAMERA_REQUEST);
            } else if (options[item].equals("Choose from Gallery")) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_REQUEST);
            }
        });
        builder.setPositiveButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog alertD = builder.create();
        alertD.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST) {
                Uri imageUri = data.getData();
                CropImage.activity(imageUri).setActivityMenuIconColor(getResources().getColor(R.color.black)).setGuidelines(CropImageView.Guidelines.ON).start(AddOffersActivity.this);
            }

            if (requestCode == CAMERA_REQUEST) {
                CropImage.activity(photoURI).setActivityMenuIconColor(getResources().getColor(R.color.black)).setGuidelines(CropImageView.Guidelines.ON).start(AddOffersActivity.this);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                savefile(resultUri);
            }
        }
    }

    private void savefile(Uri sourceuri) {
        Log.i("sourceuri1", "" + sourceuri);
        String sourceFilename = sourceuri.getPath();
        String destinationFile = Environment.getExternalStorageDirectory() + "/Joinsta Gharse/"
                + "Offer Images/" + "uplimg.png";

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

        File photoFileToUpload = new File(destinationFile);
        new UploadImage().execute(photoFileToUpload.getPath());

    }

    private class UploadImage extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuilder res = new StringBuilder();
            try {
                MultipartUtility multipart = new MultipartUtility(ApplicationConstants.FILEUPLOADAPI, "UTF-8");

                multipart.addFormField("request_type", "uploadOfferFile");
                multipart.addFormField("category_type", categoryTypeName);
                multipart.addFilePart("document", new File(params[0]));

                List<String> response = multipart.finish();
                for (String line : response) {
                    res.append(line);
                }
                return res.toString();
            } catch (IOException ex) {
                return ex.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type;
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    if (type.equalsIgnoreCase("success")) {
                        JSONObject jsonObject = mainObj.getJSONObject("result");

                        if (IMAGE_TYPE == 1) {
                            String imageUrl = jsonObject.getString("document_url");
                            imageOneName = jsonObject.getString("name");

                            if (!imageUrl.equals("")) {
                                Picasso.with(context)
                                        .load(imageUrl)
                                        .resize(100, 100)
                                        .into(imv_image_one);
                                setPaddingForView(context, imv_image_one, 0);
                                imv_image_one_delete.setVisibility(View.VISIBLE);
                                imv_image_one_delete.bringToFront();
                            }
                        } else if (IMAGE_TYPE == 2) {
                            String imageUrl = jsonObject.getString("document_url");
                            imageTwoName = jsonObject.getString("name");

                            if (!imageUrl.equals("")) {
                                Picasso.with(context)
                                        .load(imageUrl)
                                        .resize(100, 100)
                                        .into(imv_image_two);
                                setPaddingForView(context, imv_image_two, 0);
                                imv_image_two_delete.setVisibility(View.VISIBLE);
                                imv_image_two_delete.bringToFront();
                            }
                        } else if (IMAGE_TYPE == 3) {
                            String imageUrl = jsonObject.getString("document_url");
                            imageThreeName = jsonObject.getString("name");

                            if (!imageUrl.equals("")) {
                                Picasso.with(context)
                                        .load(imageUrl)
                                        .resize(100, 100)
                                        .into(imv_image_three);
                                setPaddingForView(context, imv_image_three, 0);
                                imv_image_three_delete.setVisibility(View.VISIBLE);
                                imv_image_three_delete.bringToFront();
                            }
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

    private void submitData() {
        if (edt_title.getText().toString().trim().isEmpty()) {
            edt_title.setError("Please enter offer title");
            edt_title.requestFocus();
            return;
        }

        if (edt_description.getText().toString().trim().isEmpty()) {
            edt_description.setError("Please enter offer description");
            edt_description.requestFocus();
            return;
        }

        if (edt_start_date.getText().toString().trim().isEmpty()) {
            Utilities.showMessage("Please select start date", context, 2);
            return;
        }

        if (edt_end_date.getText().toString().trim().isEmpty()) {
            Utilities.showMessage("Please select end date", context, 2);
            return;
        }

        JsonArray docsArray = new JsonArray();

        if (!imageOneName.isEmpty()) {
            docsArray.add(imageOneName);
        }

        if (!imageTwoName.isEmpty()) {
            docsArray.add(imageTwoName);
        }

        if (!imageThreeName.isEmpty()) {
            docsArray.add(imageThreeName);
        }

        JsonObject mainObj = new JsonObject();
        mainObj.addProperty("type", "addOfferDetails");
        mainObj.addProperty("category_type_id", categoryTypeId);
        mainObj.addProperty("record_id", recordId);
        mainObj.addProperty("user_id", userId);
        mainObj.addProperty("title", edt_title.getText().toString().trim());
        mainObj.addProperty("description", edt_description.getText().toString().trim());
        mainObj.addProperty("start_date", startDate);
        mainObj.addProperty("end_date", endDate);
        mainObj.addProperty("url", edt_url.getText().toString().trim());
        mainObj.addProperty("promo_code", edt_promo_code.getText().toString().trim());
        mainObj.add("document", docsArray);

        if (Utilities.isNetworkAvailable(context)) {
            new AddOffer().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }

    }

    private class AddOffer extends AsyncTask<String, Void, String> {

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
            res = APICall.JSONAPICall(ApplicationConstants.OFFERSAPI, params[0]);
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

                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText(message);
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
                    } else {
                        Utilities.showAlertDialog(context, message, false);
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

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftKeyboard(AddOffersActivity.this);
    }

}
