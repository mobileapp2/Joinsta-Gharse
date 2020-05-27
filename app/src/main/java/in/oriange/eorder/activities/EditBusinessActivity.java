package in.oriange.eorder.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Callback;
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
import java.util.regex.Matcher;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import in.oriange.eorder.R;
import in.oriange.eorder.models.BizProfEmpProfileDesignationsModel;
import in.oriange.eorder.models.CategotyListModel;
import in.oriange.eorder.models.CategotyListPojo;
import in.oriange.eorder.models.ContryCodeModel;
import in.oriange.eorder.models.GetBusinessModel;
import in.oriange.eorder.models.GetTagsListModel;
import in.oriange.eorder.models.MapAddressListModel;
import in.oriange.eorder.models.SubCategotyListModel;
import in.oriange.eorder.models.SubCategotyListPojo;
import in.oriange.eorder.models.TagsListModel;
import in.oriange.eorder.utilities.APICall;
import in.oriange.eorder.utilities.ApplicationConstants;
import in.oriange.eorder.utilities.MultipartUtility;
import in.oriange.eorder.utilities.UserSessionManager;
import in.oriange.eorder.utilities.Utilities;

import static in.oriange.eorder.utilities.ApplicationConstants.IMAGE_LINK;
import static in.oriange.eorder.utilities.PermissionUtil.PERMISSION_ALL;
import static in.oriange.eorder.utilities.PermissionUtil.doesAppNeedPermissions;
import static in.oriange.eorder.utilities.Utilities.hideSoftKeyboard;
import static in.oriange.eorder.utilities.Utilities.loadJSONForCountryCode;

public class EditBusinessActivity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private ScrollView sv_scroll;
    private ProgressBar progressBar;
    private ImageView imv_photo1, imv_photo2;
    private MaterialEditText edt_name, edt_nature, edt_subtype, edt_designation, edt_mobile, edt_landline,
            edt_email, edt_website, edt_order_online, edt_select_area, edt_address, edt_pincode, edt_city, edt_district, edt_state, edt_country,
            edt_tax_alias, edt_pan, edt_gst, edt_accholdername, edt_bank_alias, edt_bank_name, edt_ifsc, edt_account_no;
    private AutoCompleteTextView edt_tag;
    private LinearLayout ll_mobile, ll_landline;
    private TextView tv_countrycode_mobile, tv_countrycode_landline;
    private ImageButton ib_add_mobile, ib_add_landline, imv_show_hide_tax, imv_show_hide_bank;
    private LinearLayout ll_tax_details, ll_bank_details;
    private TagContainerLayout tag_container;
    private Button btn_save, btn_add_tag;
    private Switch sw_isvisible;

    private ArrayList<CategotyListModel> categotyList;
    private ArrayList<GetTagsListModel.ResultBean> tagsListFromAPI;
    private ArrayList<TagsListModel> tagsListTobeSubmitted;
    private ArrayList<LinearLayout> mobileLayoutsList, landlineLayoutsList;
    private ArrayList<String> mobileList, landlineList;
    private JsonArray mobileJSONArray, landlineJSONArray, tagJSONArray;
    private ArrayList<ContryCodeModel> countryCodeList;

    private String userId, imageUrl = "", imageName = "", categoryId = "0", subCategoryId = "0", latitude = "", longitude = "";
    private Uri photoURI;
    private final int CAMERA_REQUEST = 100;
    private final int GALLERY_REQUEST = 200;
    private File file, photoFileToUpload, profilPicFolder;
    private String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    private static TextView tv_selected_forconcode = null;

    private GetBusinessModel.ResultBean searchDetails;
    private static AlertDialog countryCodeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_business);

        init();
        getSessionDetails();
        setDefault();
        setEventListner();
        setUpToolbar();
    }

    private void init() {
        context = EditBusinessActivity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        sv_scroll = findViewById(R.id.sv_scroll);
        progressBar = findViewById(R.id.progressBar);
        imv_photo1 = findViewById(R.id.imv_photo1);
        imv_photo2 = findViewById(R.id.imv_photo2);
        edt_name = findViewById(R.id.edt_name);
        edt_nature = findViewById(R.id.edt_nature);
        edt_subtype = findViewById(R.id.edt_subtype);
        edt_designation = findViewById(R.id.edt_designation);
        edt_mobile = findViewById(R.id.edt_mobile);
        edt_landline = findViewById(R.id.edt_landline);
        edt_email = findViewById(R.id.edt_email);
        edt_website = findViewById(R.id.edt_website);
        edt_order_online = findViewById(R.id.edt_order_online);
        edt_select_area = findViewById(R.id.edt_select_area);
        edt_address = findViewById(R.id.edt_address);
        edt_pincode = findViewById(R.id.edt_pincode);
        edt_city = findViewById(R.id.edt_city);
        edt_district = findViewById(R.id.edt_district);
        edt_state = findViewById(R.id.edt_state);
        edt_country = findViewById(R.id.edt_country);
        edt_tag = findViewById(R.id.edt_tag);
        edt_tax_alias = findViewById(R.id.edt_tax_alias);
        edt_pan = findViewById(R.id.edt_pan);
        edt_gst = findViewById(R.id.edt_gst);
        edt_accholdername = findViewById(R.id.edt_accholdername);
        edt_bank_alias = findViewById(R.id.edt_bank_alias);
        edt_bank_name = findViewById(R.id.edt_bank_name);
        edt_ifsc = findViewById(R.id.edt_ifsc);
        edt_account_no = findViewById(R.id.edt_account_no);
        sw_isvisible = findViewById(R.id.sw_isvisible);
        imv_show_hide_tax = findViewById(R.id.imv_show_hide_tax);
        imv_show_hide_bank = findViewById(R.id.imv_show_hide_bank);
        ll_tax_details = findViewById(R.id.ll_tax_details);
        ll_bank_details = findViewById(R.id.ll_bank_details);

        tag_container = findViewById(R.id.tag_container);
        tv_countrycode_mobile = findViewById(R.id.tv_countrycode_mobile);
        tv_countrycode_landline = findViewById(R.id.tv_countrycode_landline);

        ll_mobile = findViewById(R.id.ll_mobile);
        ll_landline = findViewById(R.id.ll_landline);

        ib_add_mobile = findViewById(R.id.ib_add_mobile);
        ib_add_landline = findViewById(R.id.ib_add_landline);

        btn_add_tag = findViewById(R.id.btn_add_tag);
        btn_save = findViewById(R.id.btn_save);

        categotyList = new ArrayList<>();
        tagsListTobeSubmitted = new ArrayList<>();
        tagsListFromAPI = new ArrayList<>();
        mobileLayoutsList = new ArrayList<>();
        landlineLayoutsList = new ArrayList<>();
        mobileJSONArray = new JsonArray();
        landlineJSONArray = new JsonArray();
        tagJSONArray = new JsonArray();
        mobileList = new ArrayList<>();
        landlineList = new ArrayList<>();

        profilPicFolder = new File(Environment.getExternalStorageDirectory() + "/Joinsta eOrder/" + "Business");
        if (!profilPicFolder.exists())
            profilPicFolder.mkdirs();

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

        try {
            JSONArray m_jArry = new JSONArray(loadJSONForCountryCode(context));
            countryCodeList = new ArrayList<>();

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                countryCodeList.add(new ContryCodeModel(
                        jo_inside.getString("name"),
                        jo_inside.getString("dial_code"),
                        jo_inside.getString("code")
                ));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        searchDetails = (GetBusinessModel.ResultBean) getIntent().getSerializableExtra("searchDetails");

        if (!searchDetails.getImage_url().trim().isEmpty()) {

            String url = IMAGE_LINK + "" + searchDetails.getCreated_by() + "/" + searchDetails.getImage_url();
            Picasso.with(context)
                    .load(url)
                    .into(imv_photo1, new Callback() {
                        @Override
                        public void onSuccess() {
                            imv_photo1.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            imv_photo2.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            imv_photo2.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            imv_photo1.setVisibility(View.GONE);
                        }
                    });
        } else {
            imv_photo2.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            imv_photo1.setVisibility(View.GONE);
        }

        edt_name.setText(searchDetails.getBusiness_name());
        edt_nature.setText(searchDetails.getType_description());
        edt_subtype.setText(searchDetails.getSubtype_description());
        edt_designation.setText(searchDetails.getDesignation());
        edt_email.setText(searchDetails.getEmail());
        edt_website.setText(searchDetails.getWebsite());
        edt_order_online.setText(searchDetails.getOrder_online());
        edt_select_area.setText(searchDetails.getLandmark());
        edt_address.setText(searchDetails.getAddress());
        edt_pincode.setText(searchDetails.getPincode());
        edt_city.setText(searchDetails.getCity());
        edt_district.setText(searchDetails.getDistrict());
        edt_state.setText(searchDetails.getState());
        edt_country.setText(searchDetails.getCountry());
        edt_tax_alias.setText(searchDetails.getTax_alias());
        edt_pan.setText(searchDetails.getPan_number());
        edt_gst.setText(searchDetails.getGst_number());
        edt_accholdername.setText(searchDetails.getAccount_holder_name());
        edt_bank_alias.setText(searchDetails.getBank_alias());
        edt_bank_name.setText(searchDetails.getBank_name());
        edt_ifsc.setText(searchDetails.getIfsc_code());
        edt_account_no.setText(searchDetails.getAccount_no());

        if (searchDetails.getIs_visible().equals("1")) {
            sw_isvisible.setChecked(true);
        }

        ArrayList<GetBusinessModel.ResultBean.TagBean> tagsList = new ArrayList<>();
        tagsList = searchDetails.getTag().get(0);

        if (tagsList != null)
            if (tagsList.size() > 0)
                for (int i = 0; i < tagsList.size(); i++) {

                    if (!tagsList.get(i).getTag_name().trim().equals("")) {
                        tagsListTobeSubmitted.add(new TagsListModel(tagsList.get(i).getTag_id(), tagsList.get(i).getTag_name(), tagsList.get(i).getIs_approved()));
                        tag_container.addTag(tagsList.get(i).getTag_name());
                    }
                }

        ArrayList<GetBusinessModel.ResultBean.MobilesBean> mobilesList = new ArrayList<>();
        mobilesList = searchDetails.getMobiles().get(0);

        if (mobilesList != null)
            if (mobilesList.size() > 0)
                for (int i = 0; i < mobilesList.size(); i++) {
                    if (i == mobilesList.size() - 1) {
                        edt_mobile.setText(mobilesList.get(i).getMobile_number());
                        tv_countrycode_mobile.setText(mobilesList.get(i).getCountry_code());
                    } else {
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View rowView = inflater.inflate(R.layout.layout_add_mobile1, null);
                        LinearLayout ll = (LinearLayout) rowView;
                        mobileLayoutsList.add(ll);
                        ll_mobile.addView(rowView, ll_mobile.getChildCount() - 1);
                        ((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).setText(mobilesList.get(i).getMobile_number());
                        ((TextView) mobileLayoutsList.get(i).findViewById(R.id.tv_countrycode_mobile)).setText(mobilesList.get(i).getCountry_code());
                    }
                }

        ArrayList<GetBusinessModel.ResultBean.LandlineBean> landlineList = new ArrayList<>();
        landlineList = searchDetails.getLandline().get(0);

        if (landlineList != null)
            if (landlineList.size() > 0)
                for (int i = 0; i < landlineList.size(); i++) {
                    if (i == landlineList.size() - 1) {
                        edt_landline.setText(landlineList.get(i).getLandline_number());
                        tv_countrycode_landline.setText(landlineList.get(i).getCountry_code());
                    } else {
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View rowView = inflater.inflate(R.layout.layout_add_landline1, null);
                        LinearLayout ll = (LinearLayout) rowView;
                        landlineLayoutsList.add(ll);
                        ll_landline.addView(rowView, ll_landline.getChildCount() - 1);

                        ((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).setText(landlineList.get(i).getLandline_number());
                        ((TextView) landlineLayoutsList.get(i).findViewById(R.id.tv_countrycode_landline)).setText(landlineList.get(i).getCountry_code());
                    }
                }

        categoryId = searchDetails.getType_id();
        subCategoryId = searchDetails.getSub_type_id();
        latitude = searchDetails.getLatitude();
        longitude = searchDetails.getLongitude();

        if (!searchDetails.getImage_url().isEmpty()) {
            Uri uri = Uri.parse(searchDetails.getImage_url());
            imageName = uri.getLastPathSegment();
        }

        if (Utilities.isNetworkAvailable(context)) {
            new GetTagsList().execute("0");
        }
    }

    private void setEventListner() {
        imv_photo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utilities.isNetworkAvailable(context)) {
                    if (doesAppNeedPermissions()) {
                        askPermission();
                    } else {
                        selectImage();
                    }
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }
            }
        });

        imv_photo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utilities.isNetworkAvailable(context)) {
                    if (doesAppNeedPermissions()) {
                        askPermission();
                    } else {
                        selectImage();
                    }
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }
            }
        });

        edt_nature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categotyList.size() == 0) {
                    if (Utilities.isNetworkAvailable(context)) {
                        new GetCategotyList().execute("0", "0", "1");
                    } else {
                        Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                    }
                } else {
                    showCategoryListDialog();
                }
            }
        });

        edt_subtype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edt_nature.getText().toString().trim().isEmpty()) {
                    edt_nature.setError("Please select the nature of business");
                    edt_nature.requestFocus();
                    return;
                }

                if (Utilities.isNetworkAvailable(context)) {
                    new GetSubCategotyList().execute(categoryId, "1", "1");
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }

            }
        });

        edt_designation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Utilities.isNetworkAvailable(context)) {
                    new GetDesignationList().execute("1");
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }

            }
        });

        tv_countrycode_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContryCodeDialog("1");
            }
        });

        tv_countrycode_landline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContryCodeDialog("2");
            }
        });

        ib_add_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.layout_add_mobile1, null);
                LinearLayout ll = (LinearLayout) rowView;
                mobileLayoutsList.add(ll);
                ll_mobile.addView(rowView, ll_mobile.getChildCount() - 1);
            }
        });

        ib_add_landline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.layout_add_landline1, null);
                LinearLayout ll = (LinearLayout) rowView;
                landlineLayoutsList.add(ll);
                ll_landline.addView(rowView, ll_landline.getChildCount() - 1);
            }
        });

        edt_select_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(context, PickMapLocationActivity.class), 10001);
            }
        });

        btn_add_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edt_tag.getText().toString().trim().isEmpty()) {
                    edt_tag.setError("Please enter tag");
                    edt_tag.requestFocus();
                    return;
                }

                boolean isTagSelected = false;

                for (TagsListModel tagObj : tagsListTobeSubmitted) {

                    if (tagObj.getTag_name().equalsIgnoreCase(edt_tag.getText().toString().trim())) {
                        isTagSelected = true;
                        break;

                    }

                }

                if (!isTagSelected) {

                    boolean isTagPresent = false;

                    for (GetTagsListModel.ResultBean tagObj : tagsListFromAPI) {
                        if (tagObj.getTag_name().equalsIgnoreCase(edt_tag.getText().toString().trim())) {

                            tagsListTobeSubmitted.add(new TagsListModel(tagObj.getTagid(), tagObj.getTag_name(), tagObj.getIs_approved()));

                            isTagPresent = true;

                            break;
                        }
                    }

                    if (!isTagPresent) {
                        tagsListTobeSubmitted.add(new TagsListModel("0", edt_tag.getText().toString().trim(), "0"));
                    }

                    tag_container.addTag(edt_tag.getText().toString().trim());
                } else {
                    Utilities.showMessage("Tag already added", context, 2);
                }

                edt_tag.setText("");

            }
        });

        tag_container.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {

            }

            @Override
            public void onTagLongClick(int position, String text) {

            }

            @Override
            public void onSelectedTagDrag(int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {
                if (position < tag_container.getChildCount()) {
                    tag_container.removeTag(position);
                    tagsListTobeSubmitted.remove(position);
                }
            }
        });

        edt_tag.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                GetTagsListModel.ResultBean tagObj = (GetTagsListModel.ResultBean) arg0.getAdapter().getItem(arg2);


                boolean isTagSelected = false;

                for (TagsListModel tagObj1 : tagsListTobeSubmitted) {
                    if (tagObj1.getTag_name().equalsIgnoreCase(edt_tag.getText().toString().trim())) {
                        isTagSelected = true;
                        break;

                    }
                }

                if (!isTagSelected) {
                    tagsListTobeSubmitted.add(new TagsListModel(tagObj.getTagid(), tagObj.getTag_name(), tagObj.getIs_approved()));
                    tag_container.addTag(edt_tag.getText().toString().trim());
                } else {
                    Utilities.showMessage("Tag already added", context, 2);
                }

                edt_tag.setText("");
            }
        });

        imv_show_hide_tax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sv_scroll.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sv_scroll.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                }, 1);

                if (ll_tax_details.getVisibility() == View.VISIBLE) {
                    Utilities.animateCollapse(imv_show_hide_tax);
                    ll_tax_details.setVisibility(View.GONE);
                } else {
                    Utilities.animateExpand(imv_show_hide_tax);
                    ll_tax_details.setVisibility(View.VISIBLE);
                }
            }
        });

        imv_show_hide_bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sv_scroll.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sv_scroll.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                }, 1);

                if (ll_bank_details.getVisibility() == View.VISIBLE) {
                    Utilities.animateCollapse(imv_show_hide_bank);
                    ll_bank_details.setVisibility(View.GONE);
                } else {
                    Utilities.animateExpand(imv_show_hide_bank);
                    ll_bank_details.setVisibility(View.VISIBLE);
                }
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
            }
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

    public void removeMobileLayoutBiz(View v) {
        ll_mobile.removeView((View) v.getParent());
        mobileLayoutsList.remove(v.getParent());
    }

    public void removeLandlineLayoutBiz(View view) {
        ll_landline.removeView((View) view.getParent());
        landlineLayoutsList.remove(view.getParent());
    }

    public void selectContryCodeBiz(View v) {
        tv_selected_forconcode = (TextView) v;
        showContryCodeDialog("3");
    }

    private class GetTagsList extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "getTags");
            obj.addProperty("category_type_id", params[0]);
            res = APICall.JSONAPICall(ApplicationConstants.TAGSAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    tagsListFromAPI = new ArrayList<>();
                    GetTagsListModel pojoDetails = new Gson().fromJson(result, GetTagsListModel.class);
                    type = pojoDetails.getType();
                    message = pojoDetails.getMessage();

                    if (type.equalsIgnoreCase("success")) {
                        tagsListFromAPI = pojoDetails.getResult();

                        ArrayAdapter<GetTagsListModel.ResultBean> adapter = new ArrayAdapter<GetTagsListModel.ResultBean>(
                                context, R.layout.list_row, tagsListFromAPI);
                        edt_tag.setAdapter(adapter);

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

    private class GetCategotyList extends AsyncTask<String, Void, String> {

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
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    categotyList = new ArrayList<>();
                    CategotyListPojo pojoDetails = new Gson().fromJson(result, CategotyListPojo.class);
                    type = pojoDetails.getType();
                    message = pojoDetails.getMessage();

                    if (type.equalsIgnoreCase("success")) {
                        categotyList = pojoDetails.getResult();
                        if (categotyList.size() > 0) {
                            showCategoryListDialog();
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

    private void showCategoryListDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Nature of Business");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row);

        for (int i = 0; i < categotyList.size(); i++) {
            arrayAdapter.add(String.valueOf(categotyList.get(i).getName()));
        }

        builderSingle.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CategotyListModel categoty = categotyList.get(which);
                edt_nature.setText(categoty.getName());
                categoryId = categoty.getId();
                edt_subtype.setText("");
            }
        });
        builderSingle.show();
    }

    public class GetSubCategotyList extends AsyncTask<String, Void, String> {

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
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    SubCategotyListPojo pojoDetails = new Gson().fromJson(result, SubCategotyListPojo.class);
                    type = pojoDetails.getType();
                    message = pojoDetails.getMessage();

                    if (type.equalsIgnoreCase("success")) {
                        ArrayList<SubCategotyListModel> subCategoryList = pojoDetails.getResult();
                        if (subCategoryList.size() > 0) {
                            showSubCategoryListDialog(subCategoryList);
                        }
                    } else {
                        Utilities.showAlertDialog(context, "Subtype not available", false);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showAlertDialog(context, "Server Not Responding", false);
            }
        }
    }

    private void showSubCategoryListDialog(final ArrayList<SubCategotyListModel> subCategoryList) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Subtype");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row);

        for (int i = 0; i < subCategoryList.size(); i++) {
            arrayAdapter.add(String.valueOf(subCategoryList.get(i).getName()));
        }

        builderSingle.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SubCategotyListModel subCategoty = subCategoryList.get(which);
                edt_subtype.setText(subCategoty.getName());
                subCategoryId = subCategoty.getId();
            }
        });
        builderSingle.show();
    }

    private class GetDesignationList extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "getDesignation");
            obj.addProperty("category_type_id", params[0]);
            res = APICall.JSONAPICall(ApplicationConstants.DESIGNATIONAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    BizProfEmpProfileDesignationsModel pojoDetails = new Gson().fromJson(result, BizProfEmpProfileDesignationsModel.class);
                    type = pojoDetails.getType();
                    message = pojoDetails.getMessage();

                    if (type.equalsIgnoreCase("success")) {
                        List<BizProfEmpProfileDesignationsModel.ResultBean> designationList = pojoDetails.getResult();
                        if (designationList.size() > 0) {
                            showDesignationListDialog(designationList);
                        }
                    } else {
                        Utilities.showAlertDialog(context, "Designations not available", false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showAlertDialog(context, "Server Not Responding", false);
            }
        }
    }

    private void showDesignationListDialog(final List<BizProfEmpProfileDesignationsModel.ResultBean> designationList) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Designation");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row);

        for (int i = 0; i < designationList.size(); i++) {
            arrayAdapter.add(String.valueOf(designationList.get(i).getDesignation_name()));
        }

        builderSingle.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BizProfEmpProfileDesignationsModel.ResultBean designation = designationList.get(which);
                edt_designation.setText(designation.getDesignation_name());
            }
        });
        builderSingle.show();
    }

    private void showContryCodeDialog(final String type) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_countrycodes_list, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builder.setView(view);
        builder.setTitle("Select Country");
        builder.setCancelable(false);

        final RecyclerView rv_country = view.findViewById(R.id.rv_country);
        EditText edt_search = view.findViewById(R.id.edt_search);
        rv_country.setLayoutManager(new LinearLayoutManager(context));
        rv_country.setAdapter(new CountryCodeAdapter(countryCodeList, type));

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {

                if (query.toString().isEmpty()) {
                    rv_country.setAdapter(new CountryCodeAdapter(countryCodeList, type));
                    return;
                }

                if (countryCodeList.size() == 0) {
                    rv_country.setVisibility(View.GONE);
                    return;
                }

                if (!query.toString().equals("")) {
                    ArrayList<ContryCodeModel> searchedCountryList = new ArrayList<>();
                    for (ContryCodeModel countryDetails : countryCodeList) {

                        String countryToBeSearched = countryDetails.getName().toLowerCase();

                        if (countryToBeSearched.contains(query.toString().toLowerCase())) {
                            searchedCountryList.add(countryDetails);
                        }
                    }
                    rv_country.setAdapter(new CountryCodeAdapter(searchedCountryList, type));
                } else {
                    rv_country.setAdapter(new CountryCodeAdapter(countryCodeList, type));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        countryCodeDialog = builder.create();
        countryCodeDialog.show();
    }

    private class CountryCodeAdapter extends RecyclerView.Adapter<CountryCodeAdapter.MyViewHolder> {

        private ArrayList<ContryCodeModel> countryCodeList;
        private String type;

        public CountryCodeAdapter(ArrayList<ContryCodeModel> countryCodeList, String type) {
            this.countryCodeList = countryCodeList;
            this.type = type;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_1, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, final int pos) {
            final int position = holder.getAdapterPosition();

            holder.tv_name.setText(countryCodeList.get(position).getName());

            holder.tv_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type.equals("1")) {
                        tv_countrycode_mobile.setText(countryCodeList.get(position).getDial_code());
                    } else if (type.equals("2")) {
                        tv_countrycode_landline.setText(countryCodeList.get(position).getDial_code());
                    } else if (type.equals("3")) {
                        tv_selected_forconcode.setText(countryCodeList.get(position).getDial_code());
                    }
                    countryCodeDialog.dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return countryCodeList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView tv_name;

            public MyViewHolder(@NonNull View view) {
                super(view);
                tv_name = view.findViewById(R.id.tv_name);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }
    }

    private void submitData() {
        mobileList = new ArrayList<>();
        landlineList = new ArrayList<>();
        mobileJSONArray = new JsonArray();
        landlineJSONArray = new JsonArray();
        tagJSONArray = new JsonArray();

        if (edt_name.getText().toString().trim().isEmpty()) {
            edt_name.setError("Please enter the name of business");
            edt_name.requestFocus();
            edt_name.getParent().requestChildFocus(edt_name, edt_name);
            return;
        }

        if (edt_nature.getText().toString().trim().isEmpty()) {
            edt_nature.setError("Please select the nature of business");
            edt_nature.requestFocus();
            edt_nature.getParent().requestChildFocus(edt_nature, edt_nature);
            return;
        }

//        if (edt_subtype.getText().toString().trim().isEmpty()) {
//            edt_subtype.setError("Please select subtype");
//            edt_subtype.requestFocus();
//            return;
//        }

        for (int i = 0; i < mobileLayoutsList.size(); i++) {
            if (!((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).getText().toString().trim().isEmpty()) {
                if (!Utilities.isValidMobileno(((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).getText().toString().trim())) {
                    ((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).setError("Please enter mobile number");
                    (mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).requestFocus();
                    return;
                }
            }
        }

        if (!edt_mobile.getText().toString().trim().isEmpty()) {
            if (!Utilities.isValidMobileno(edt_mobile.getText().toString().trim())) {
                edt_mobile.setError("Please enter valid mobile number");
                edt_mobile.requestFocus();
                edt_mobile.getParent().requestChildFocus(edt_mobile, edt_mobile);
                return;
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

        if (!edt_landline.getText().toString().trim().isEmpty()) {
            if (!Utilities.isLandlineValid(edt_landline.getText().toString().trim())) {
                edt_landline.setError("Please enter valid landline number");
                edt_landline.requestFocus();
                edt_landline.getParent().requestChildFocus(edt_landline, edt_landline);
                return;
            }
        }

        if (!edt_email.getText().toString().trim().isEmpty()) {
            if (!Utilities.isEmailValid(edt_email.getText().toString().trim())) {
                edt_email.setError("Please enter valid email");
                edt_email.requestFocus();
                edt_email.getParent().requestChildFocus(edt_email, edt_email);
                return;
            }
        }

        if (!edt_website.getText().toString().trim().isEmpty()) {
            if (!Utilities.isWebsiteValid(edt_website.getText().toString().trim())) {
                edt_website.setError("Please enter valid website");
                edt_website.requestFocus();
                return;
            }
        }

//        if (!edt_order_online.getText().toString().trim().isEmpty()) {
//            if (!Utilities.isWebsiteValid(edt_order_online.getText().toString().trim())) {
//                edt_order_online.setError("Please enter valid url");
//                edt_order_online.requestFocus();
//                return;
//            }
//        }

//        if (edt_select_area.getText().toString().trim().isEmpty()) {
//            edt_select_area.setError("Please select area");
//            edt_select_area.requestFocus();
//            return;
//        }

        if (!edt_pincode.getText().toString().trim().isEmpty()) {
            if (edt_pincode.getText().toString().trim().length() != 6) {
                edt_pincode.setError("Please enter pincode");
                edt_pincode.requestFocus();
                edt_pincode.getParent().requestChildFocus(edt_pincode, edt_pincode);
                return;
            }
        }

        if (edt_city.getText().toString().trim().isEmpty()) {
            edt_city.setError("Please select area");
            edt_city.requestFocus();
            edt_city.getParent().requestChildFocus(edt_city, edt_city);
            return;
        }

        if (!edt_pan.getText().toString().trim().isEmpty()) {
            if (!Utilities.isValidPanNum(edt_pan.getText().toString().trim())) {
                edt_pan.setError("Please enter valid PAN ");
                edt_pan.requestFocus();
                edt_pan.getParent().requestChildFocus(edt_pan, edt_pan);
                return;
            }
        }

        if (!edt_gst.getText().toString().trim().isEmpty()) {
            if (!Utilities.isGSTValid(edt_gst.getText().toString().trim())) {
                edt_gst.setError("Please enter valid GST number");
                edt_gst.requestFocus();
                edt_gst.getParent().requestChildFocus(edt_gst, edt_gst);
                return;
            }
        }

        if (!edt_ifsc.getText().toString().trim().isEmpty()) {
            if (!Utilities.isIfscValid(edt_ifsc.getText().toString().trim())) {
                edt_ifsc.setError("Please enter valid IFSC code");
                edt_ifsc.requestFocus();
                edt_ifsc.getParent().requestChildFocus(edt_gst, edt_ifsc);
                return;
            }
        }

        for (int i = 0; i < mobileLayoutsList.size(); i++) {
            if (!((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).getText().toString().trim().equals("")) {
                JsonObject mobileJSONObj = new JsonObject();
                mobileJSONObj.addProperty("mobile", ((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).getText().toString().trim());
                mobileJSONObj.addProperty("country_code", ((TextView) mobileLayoutsList.get(i).findViewById(R.id.tv_countrycode_mobile)).getText().toString());
                mobileJSONArray.add(mobileJSONObj);
            }
        }

        if (!edt_mobile.getText().toString().trim().isEmpty()) {
            JsonObject mobileJSONObj = new JsonObject();
            mobileJSONObj.addProperty("mobile", edt_mobile.getText().toString().trim());
            mobileJSONObj.addProperty("country_code", tv_countrycode_mobile.getText().toString());
            mobileJSONArray.add(mobileJSONObj);
        }

        for (int i = 0; i < landlineLayoutsList.size(); i++) {
            if (!((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).getText().toString().trim().equals("")) {
                JsonObject landlineJSONObj = new JsonObject();
                landlineJSONObj.addProperty("landlinenumbers", ((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).getText().toString().trim());
                landlineJSONObj.addProperty("country_code", ((TextView) landlineLayoutsList.get(i).findViewById(R.id.tv_countrycode_landline)).getText().toString());
                landlineJSONArray.add(landlineJSONObj);
            }
        }

        if (!edt_landline.getText().toString().trim().isEmpty()) {
            JsonObject landlineJSONObj = new JsonObject();
            landlineJSONObj.addProperty("landlinenumbers", edt_landline.getText().toString());
            landlineJSONObj.addProperty("country_code", tv_countrycode_landline.getText().toString().trim());
            landlineJSONArray.add(landlineJSONObj);
        }

        for (int i = 0; i < tagsListTobeSubmitted.size(); i++) {
            JsonObject tagsJSONObj = new JsonObject();
            tagsJSONObj.addProperty("tag_id", tagsListTobeSubmitted.get(i).getTag_id());
            tagsJSONObj.addProperty("tag_name", tagsListTobeSubmitted.get(i).getTag_name());
            tagsJSONObj.addProperty("is_approved", tagsListTobeSubmitted.get(i).getIs_approved());
            tagJSONArray.add(tagsJSONObj);
        }

        JsonObject mainObj = new JsonObject();

        String isVisible = "0";

        if (sw_isvisible.isChecked()) {
            isVisible = "1";
        }

        mainObj.addProperty("type", "updatebusiness");
        mainObj.addProperty("address", edt_address.getText().toString().trim());
        mainObj.addProperty("business_name", edt_name.getText().toString().trim());
        mainObj.addProperty("district", edt_district.getText().toString().trim());
        mainObj.addProperty("state", edt_state.getText().toString().trim());
        mainObj.addProperty("city", edt_city.getText().toString().trim());
        mainObj.addProperty("country", edt_country.getText().toString().trim());
        mainObj.addProperty("pincode", edt_pincode.getText().toString().trim());
        mainObj.addProperty("longitude", longitude);
        mainObj.addProperty("latitude", latitude);
        mainObj.addProperty("landmark", edt_select_area.getText().toString().trim());
        mainObj.addProperty("locality", edt_city.getText().toString().trim());
        mainObj.addProperty("email", edt_email.getText().toString().trim());
        mainObj.addProperty("designation", edt_designation.getText().toString().trim());
        mainObj.addProperty("record_statusid", "1");
        mainObj.addProperty("website", edt_website.getText().toString().trim());
        mainObj.addProperty("order_online", edt_order_online.getText().toString().trim());
        mainObj.addProperty("image_url", imageName);
        mainObj.addProperty("busi_type_id", "2");
        mainObj.addProperty("type_id", categoryId);
        mainObj.addProperty("sub_type_id", subCategoryId);
        mainObj.addProperty("type_description", edt_nature.getText().toString().trim());
        mainObj.addProperty("subtype_description", edt_subtype.getText().toString().trim());
        mainObj.addProperty("created_by", userId);
        mainObj.addProperty("updated_by", userId);
        mainObj.addProperty("business_id", searchDetails.getId());
        mainObj.addProperty("is_active", "0");
        mainObj.addProperty("is_deleted", "0");
        mainObj.addProperty("organization_name", "");
        mainObj.addProperty("other_details", "");
        mainObj.addProperty("tax_name", edt_tax_alias.getText().toString().trim());
        mainObj.addProperty("tax_alias", edt_tax_alias.getText().toString().trim());
        mainObj.addProperty("pan_number", edt_pan.getText().toString().trim());
        mainObj.addProperty("gst_number", edt_gst.getText().toString().trim());
        mainObj.addProperty("tax_status", "online");
        mainObj.addProperty("account_holder_name", edt_accholdername.getText().toString().trim());
        mainObj.addProperty("alias", edt_bank_alias.getText().toString().trim());
        mainObj.addProperty("bank_name", edt_bank_name.getText().toString().trim());
        mainObj.addProperty("ifsc_code", edt_ifsc.getText().toString().trim());
        mainObj.addProperty("account_no", edt_account_no.getText().toString().trim());
        mainObj.addProperty("status", "online");
        mainObj.addProperty("is_visible", isVisible);
        mainObj.add("mobile_number", mobileJSONArray);
        mainObj.add("landline_number", landlineJSONArray);
        mainObj.add("tag_name", tagJSONArray);

        Log.i("EDITBUSINESS", mainObj.toString());

        if (Utilities.isNetworkAvailable(context)) {
            new EditBusiness().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST) {
                Uri imageUri = data.getData();
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(EditBusinessActivity.this);
            }

            if (requestCode == CAMERA_REQUEST) {
                CropImage.activity(photoURI).setGuidelines(CropImageView.Guidelines.ON).start(EditBusinessActivity.this);
            }

            if (requestCode == 10001) {
                MapAddressListModel addressList = (MapAddressListModel) data.getSerializableExtra("addressList");
                if (addressList != null) {
                    latitude = addressList.getMap_location_lattitude();
                    longitude = addressList.getAddress_line_one();
                    edt_address.setText(addressList.getAddress_line_one());
                    edt_country.setText(addressList.getCountry());
                    edt_state.setText(addressList.getState());
                    edt_district.setText(addressList.getDistrict());
                    edt_pincode.setText(addressList.getPincode());
                    edt_select_area.setText(addressList.getName());
                    edt_city.setText(addressList.getDistrict());
                } else {
                    Utilities.showMessage("Address not found, please try again", context, 3);
                }
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
        String destinationFile = Environment.getExternalStorageDirectory() + "/Joinsta eOrder/"
                + "Business/" + "uplimg.png";

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
        new UploadImage().execute(photoFileToUpload);

    }

    private class UploadImage extends AsyncTask<File, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                        imageUrl = jsonObject.getString("document_url");
                        imageName = jsonObject.getString("name");

                        if (!imageUrl.equals("")) {
                            Picasso.with(context)
                                    .load(imageUrl)
                                    .into(imv_photo1);
                            imv_photo1.setVisibility(View.VISIBLE);
                            imv_photo2.setVisibility(View.GONE);
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

    private class EditBusiness extends AsyncTask<String, Void, String> {

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
            res = APICall.JSONAPICall(ApplicationConstants.BUSINESSAPI, params[0]);
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
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("MyBusinessActivity"));

                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Business details updated successfully");
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
                        Utilities.showMessage("Failed to submit the details", context, 3);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void askPermission() {
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

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftKeyboard(EditBusinessActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menus_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                submitData();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
