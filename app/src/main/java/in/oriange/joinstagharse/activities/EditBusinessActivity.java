package in.oriange.joinstagharse.activities;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
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
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Callback;
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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.models.BizProfEmpProfileDesignationsModel;
import in.oriange.joinstagharse.models.CategotyListModel;
import in.oriange.joinstagharse.models.CategotyListPojo;
import in.oriange.joinstagharse.models.GetBusinessModel;
import in.oriange.joinstagharse.models.GetTagsListModel;
import in.oriange.joinstagharse.models.MapAddressListModel;
import in.oriange.joinstagharse.models.SubCategotyListModel;
import in.oriange.joinstagharse.models.SubCategotyListPojo;
import in.oriange.joinstagharse.models.TagsListModel;
import in.oriange.joinstagharse.utilities.APICall;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.CountryCodeSelection;
import in.oriange.joinstagharse.utilities.MultipartUtility;
import in.oriange.joinstagharse.utilities.UserSessionManager;
import in.oriange.joinstagharse.utilities.Utilities;

import static in.oriange.joinstagharse.utilities.ApplicationConstants.IMAGE_LINK;
import static in.oriange.joinstagharse.utilities.PermissionUtil.PERMISSION_ALL;
import static in.oriange.joinstagharse.utilities.PermissionUtil.doesAppNeedPermissions;
import static in.oriange.joinstagharse.utilities.Utilities.changeStatusBar;

public class EditBusinessActivity extends AppCompatActivity {

    @BindView(R.id.btn_save)
    MaterialButton btnSave;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.imv_photo1)
    ImageView imvPhoto1;
    @BindView(R.id.imv_photo2)
    ImageView imvPhoto2;
    @BindView(R.id.edt_name)
    EditText edtName;
    @BindView(R.id.edt_nature)
    EditText edtNature;
    @BindView(R.id.edt_subtype)
    EditText edtSubtype;
    @BindView(R.id.edt_designation)
    EditText edtDesignation;
    @BindView(R.id.edt_tag)
    AutoCompleteTextView edtTag;
    @BindView(R.id.btn_add_tag)
    Button btnAddTag;
    @BindView(R.id.tag_container)
    TagContainerLayout tagContainer;
    @BindView(R.id.btn_add_mobile)
    Button btnAddMobile;
    @BindView(R.id.ll_mobile)
    LinearLayout llMobile;
    @BindView(R.id.btn_add_landline)
    Button btnAddLandline;
    @BindView(R.id.ll_landline)
    LinearLayout llLandline;
    @BindView(R.id.edt_email)
    EditText edtEmail;
    @BindView(R.id.edt_website)
    EditText edtWebsite;
    @BindView(R.id.btn_select)
    MaterialButton btnSelectAddress;
    @BindView(R.id.edt_address)
    EditText edtAddress;
    @BindView(R.id.edt_pincode)
    EditText edtPincode;
    @BindView(R.id.edt_city)
    EditText edtCity;
    @BindView(R.id.edt_district)
    EditText edtDistrict;
    @BindView(R.id.edt_state)
    EditText edtState;
    @BindView(R.id.edt_country)
    EditText edtCountry;
    @BindView(R.id.cb_is_enquiry_available)
    CheckBox cbIsEnquiryAvailable;
    @BindView(R.id.cb_is_pick_up_available)
    CheckBox cbIsPickUpAvailable;
    @BindView(R.id.cb_is_home_delivery_available)
    CheckBox cbIsHomeDeliveryAvailable;
    @BindView(R.id.cb_show_in_search)
    CheckBox cbShowInSearch;
    @BindView(R.id.sv_scroll)
    ScrollView svScroll;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;

    private ArrayList<CategotyListModel> categotyList;
    private ArrayList<SubCategotyListModel> subCategoryList;
    private ArrayList<GetTagsListModel.ResultBean> tagsListFromAPI;
    private ArrayList<TagsListModel> tagsListTobeSubmitted;
    private ArrayList<LinearLayout> mobileLayoutsList, landlineLayoutsList;
    private JsonArray mobileJSONArray, landlineJSONArray, tagJSONArray, subCategoryJsonArray;

    private String userId, imageName = "", categoryId = "", latitude = "", longitude = "";
    private Uri photoURI;
    private final int CAMERA_REQUEST = 100, GALLERY_REQUEST = 200, LOCATION_REQUEST = 300;
    private File file, profilPicFolder;
    private String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    private GetBusinessModel.ResultBean searchDetails;
    private CheckBox cb_select_all;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_business);
        ButterKnife.bind(this);

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
        changeStatusBar(context, getWindow());
        categotyList = new ArrayList<>();
        subCategoryList = new ArrayList<>();
        tagsListTobeSubmitted = new ArrayList<>();
        tagsListFromAPI = new ArrayList<>();
        mobileLayoutsList = new ArrayList<>();
        landlineLayoutsList = new ArrayList<>();
        mobileJSONArray = new JsonArray();
        landlineJSONArray = new JsonArray();
        tagJSONArray = new JsonArray();
        subCategoryJsonArray = new JsonArray();

        profilPicFolder = new File(Environment.getExternalStorageDirectory() + "/Joinsta Gharse/" + "Business");
        if (!profilPicFolder.exists())
            profilPicFolder.mkdirs();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
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
        searchDetails = (GetBusinessModel.ResultBean) getIntent().getSerializableExtra("searchDetails");

        if (!searchDetails.getImage_url().trim().isEmpty()) {

            String url = IMAGE_LINK + "" + searchDetails.getCreated_by() + "/" + searchDetails.getImage_url();
            Picasso.with(context)
                    .load(url)
                    .into(imvPhoto1, new Callback() {
                        @Override
                        public void onSuccess() {
                            imvPhoto1.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            imvPhoto2.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            imvPhoto2.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            imvPhoto1.setVisibility(View.GONE);
                        }
                    });
        } else {
            imvPhoto2.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            imvPhoto1.setVisibility(View.GONE);
        }

        edtName.setText(searchDetails.getBusiness_name());
        edtNature.setText(searchDetails.getType_description());

        StringBuilder subTypeNameSb = new StringBuilder();
        String subTypeNameStr = "";

        if (searchDetails.getSub_categories().get(0) != null) {
            for (GetBusinessModel.ResultBean.SubCategoriesBean subCategoriesBean : searchDetails.getSub_categories().get(0)) {
                subTypeNameSb.append(subCategoriesBean.getSubtype_description()).append(", ");
                subCategoryJsonArray.add(subCategoriesBean.getSub_type_id());
            }
            subTypeNameStr = subTypeNameSb.toString();
            if (!subTypeNameStr.equals(""))
                edtSubtype.setText(subTypeNameStr.substring(0, subTypeNameStr.length() - 2));
        }

        edtDesignation.setText(searchDetails.getDesignation());
        edtEmail.setText(searchDetails.getEmail());
        edtWebsite.setText(searchDetails.getWebsite());
        edtAddress.setText(searchDetails.getAddress());
        edtPincode.setText(searchDetails.getPincode());
        edtCity.setText(searchDetails.getCity());
        edtDistrict.setText(searchDetails.getDistrict());
        edtState.setText(searchDetails.getState());
        edtCountry.setText(searchDetails.getCountry());

        List<GetBusinessModel.ResultBean.TagBean> tagsList = new ArrayList<>();
        tagsList = searchDetails.getTag().get(0);

        if (tagsList != null)
            if (tagsList.size() > 0)
                for (int i = 0; i < tagsList.size(); i++) {
                    if (!tagsList.get(i).getTag_name().trim().equals("")) {
                        tagsListTobeSubmitted.add(new TagsListModel(tagsList.get(i).getTag_id(), tagsList.get(i).getTag_name(), tagsList.get(i).getIs_approved()));
                        tagContainer.addTag(tagsList.get(i).getTag_name());
                    }
                }

        List<GetBusinessModel.ResultBean.MobilesBean> mobilesList = searchDetails.getMobiles().get(0);

        if (mobilesList != null)
            if (mobilesList.size() > 0)
                for (int i = 0; i < mobilesList.size(); i++) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View rowView = inflater.inflate(R.layout.layout_add_mobile, null);
                    LinearLayout ll = (LinearLayout) rowView;
                    mobileLayoutsList.add(ll);
                    llMobile.addView(rowView, llMobile.getChildCount() - 1);
                    ((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).setText(mobilesList.get(i).getMobile_number());
                    ((TextView) mobileLayoutsList.get(i).findViewById(R.id.tv_countrycode_mobile)).setText(mobilesList.get(i).getCountry_code());
                }
            else
                addMobileLayout();
        else
            addMobileLayout();

        List<GetBusinessModel.ResultBean.LandlineBean> landlineList = searchDetails.getLandline().get(0);

        if (landlineList != null)
            if (landlineList.size() > 0)
                for (int i = 0; i < landlineList.size(); i++) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View rowView = inflater.inflate(R.layout.layout_add_landline, null);
                    LinearLayout ll = (LinearLayout) rowView;
                    landlineLayoutsList.add(ll);
                    llLandline.addView(rowView, llLandline.getChildCount() - 1);

                    ((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).setText(landlineList.get(i).getLandline_number());
                    ((TextView) landlineLayoutsList.get(i).findViewById(R.id.tv_countrycode_landline)).setText(landlineList.get(i).getCountry_code());
                }
            else
                addLandlineLayout();
        else
            addLandlineLayout();

        categoryId = searchDetails.getType_id();
        latitude = searchDetails.getLatitude();
        longitude = searchDetails.getLongitude();

        if (searchDetails.getIs_visible().equals("1"))
            cbShowInSearch.setChecked(true);

        if (searchDetails.getIs_enquiry_available().equals("1"))
            cbIsEnquiryAvailable.setChecked(true);

        if (searchDetails.getIs_pick_up_available().equals("1"))
            cbIsPickUpAvailable.setChecked(true);

        if (searchDetails.getIs_home_delivery_available().equals("1"))
            cbIsHomeDeliveryAvailable.setChecked(true);

        if (!searchDetails.getImage_url().isEmpty()) {
            Uri uri = Uri.parse(searchDetails.getImage_url());
            imageName = uri.getLastPathSegment();
        }

        if (Utilities.isNetworkAvailable(context)) {
            new GetTagsList().execute("0");
        }
    }

    private void setEventListner() {
        imvPhoto1.setOnClickListener(new View.OnClickListener() {
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

        imvPhoto2.setOnClickListener(new View.OnClickListener() {
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

        edtNature.setOnClickListener(new View.OnClickListener() {
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

        edtSubtype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtNature.getText().toString().trim().isEmpty()) {
                    Utilities.showMessage("Please select the nature of business", context, 2);
                    return;
                }

                if (subCategoryList.size() == 0) {
                    if (Utilities.isNetworkAvailable(context)) {
                        new GetSubCategotyList().execute(categoryId, "1", "1");
                    } else {
                        Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                    }
                } else {
                    showSubCategoryListDialog();
                }
            }
        });

        edtDesignation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetDesignationList().execute("1");
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }

            }
        });

        btnAddMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMobileLayout();
            }
        });

        btnAddLandline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLandlineLayout();
            }
        });

        btnSelectAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(context, PickMapLocationActivity.class), LOCATION_REQUEST);
            }
        });

        btnAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtTag.getText().toString().trim().isEmpty()) {
                    edtTag.setError("Please enter tag");
                    edtTag.requestFocus();
                    return;
                }

                boolean isTagSelected = false;

                for (TagsListModel tagObj : tagsListTobeSubmitted) {

                    if (tagObj.getTag_name().equalsIgnoreCase(edtTag.getText().toString().trim())) {
                        isTagSelected = true;
                        break;

                    }

                }

                if (!isTagSelected) {

                    boolean isTagPresent = false;

                    for (GetTagsListModel.ResultBean tagObj : tagsListFromAPI) {
                        if (tagObj.getTag_name().equalsIgnoreCase(edtTag.getText().toString().trim())) {

                            tagsListTobeSubmitted.add(new TagsListModel(tagObj.getTagid(), tagObj.getTag_name(), tagObj.getIs_approved()));

                            isTagPresent = true;

                            break;
                        }
                    }

                    if (!isTagPresent) {
                        tagsListTobeSubmitted.add(new TagsListModel("0", edtTag.getText().toString().trim(), "0"));
                    }

                    tagContainer.addTag(edtTag.getText().toString().trim());
                } else {
                    Utilities.showMessage("Tag already added", context, 2);
                }

                edtTag.setText("");

            }
        });

        tagContainer.setOnTagClickListener(new TagView.OnTagClickListener() {
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
                if (position < tagContainer.getChildCount()) {
                    tagContainer.removeTag(position);
                    tagsListTobeSubmitted.remove(position);
                }
            }
        });

        edtTag.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                GetTagsListModel.ResultBean tagObj = (GetTagsListModel.ResultBean) arg0.getAdapter().getItem(arg2);


                boolean isTagSelected = false;

                for (TagsListModel tagObj1 : tagsListTobeSubmitted) {
                    if (tagObj1.getTag_name().equalsIgnoreCase(edtTag.getText().toString().trim())) {
                        isTagSelected = true;
                        break;

                    }
                }

                if (!isTagSelected) {
                    tagsListTobeSubmitted.add(new TagsListModel(tagObj.getTagid(), tagObj.getTag_name(), tagObj.getIs_approved()));
                    tagContainer.addTag(edtTag.getText().toString().trim());
                } else {
                    Utilities.showMessage("Tag already added", context, 2);
                }

                edtTag.setText("");
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
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

    public void removeMobileLayout(View v) {
        llMobile.removeView((View) v.getParent());
        mobileLayoutsList.remove(v.getParent());
    }

    private void addMobileLayout() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.layout_add_mobile, null);
        LinearLayout ll = (LinearLayout) rowView;
        mobileLayoutsList.add(ll);
        llMobile.addView(rowView, llMobile.getChildCount() - 1);
    }

    private void addLandlineLayout() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.layout_add_landline, null);
        LinearLayout ll = (LinearLayout) rowView;
        landlineLayoutsList.add(ll);
        llLandline.addView(rowView, llLandline.getChildCount() - 1);
    }

    public void removeLandlineLayout(View view) {
        llLandline.removeView((View) view.getParent());
        landlineLayoutsList.remove(view.getParent());
    }

    public void selectContryCode(View v) {
        TextView tv_country_code = (TextView) v;
        new CountryCodeSelection(context, tv_country_code);
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
                        edtTag.setAdapter(adapter);

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

        builderSingle.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
            CategotyListModel categoty = categotyList.get(which);
            edtNature.setText(categoty.getName());
            categoryId = categoty.getId();
            edtSubtype.setText("");
            subCategoryList.clear();
            subCategoryJsonArray = new JsonArray();
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
                    subCategoryList.clear();
                    if (type.equalsIgnoreCase("success")) {
                        subCategoryList = pojoDetails.getResult();
                        if (subCategoryList.size() > 0) {
                            showSubCategoryListDialog();
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

    private void showSubCategoryListDialog() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_check_list, null);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setView(view);
        builder.setTitle("Select Subtype");
        builder.setCancelable(false);

        RecyclerView rv_checklist = view.findViewById(R.id.rv_checklist);
        cb_select_all = view.findViewById(R.id.cb_select_all);
        rv_checklist.setLayoutManager(new LinearLayoutManager(context));
        rv_checklist.setAdapter(new SubCategoryAdapter());

        boolean areAllLabsChecked = true;

        for (int i = 0; i < subCategoryList.size(); i++) {
            if (!subCategoryList.get(i).isChecked()) {
                areAllLabsChecked = false;
                break;
            }
        }

        cb_select_all.setChecked(areAllLabsChecked);

        cb_select_all.setOnClickListener(v -> {
            if (cb_select_all.isChecked())
                for (int i = 0; i < subCategoryList.size(); i++)
                    subCategoryList.get(i).setChecked(true);
            else
                for (int i = 0; i < subCategoryList.size(); i++)
                    subCategoryList.get(i).setChecked(false);

            rv_checklist.setAdapter(new SubCategoryAdapter());
        });

        builder.setPositiveButton("Select", (dialog, which) -> {
            subCategoryJsonArray = new JsonArray();
            edtSubtype.setText("");

            StringBuilder selectedSubCategories = new StringBuilder();

            for (SubCategotyListModel sample : subCategoryList) {
                if (sample.isChecked()) {
                    selectedSubCategories.append(sample.getName()).append(", ");
                    subCategoryJsonArray.add(sample.getId());
                }
            }

            if (selectedSubCategories.toString().length() != 0) {
                String selectedLabsStr = selectedSubCategories.substring(0, selectedSubCategories.toString().length() - 2);
                edtSubtype.setText(selectedLabsStr);
            }

            if (cb_select_all.isChecked())
                edtSubtype.setText("All subtypes selected");
        });

        builder.setNegativeButton("clear", (dialog, which) -> {
            for (int i = 0; i < subCategoryList.size(); i++)
                subCategoryList.get(i).setChecked(false);

            cb_select_all.setChecked(false);
            edtSubtype.setText("");
            subCategoryJsonArray = new JsonArray();
        });

        builder.create().show();
    }

    private class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_checklist, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
            final int position = holder.getAdapterPosition();

            holder.cb_select.setText(subCategoryList.get(position).getName());

            if (subCategoryList.get(position).isChecked()) {
                holder.cb_select.setChecked(true);
            }

            holder.cb_select.setOnCheckedChangeListener((buttonView, isChecked) -> {
                subCategoryList.get(position).setChecked(isChecked);

                boolean areAllLabsChecked = true;

                for (int i = 0; i < subCategoryList.size(); i++) {
                    if (!subCategoryList.get(i).isChecked()) {
                        areAllLabsChecked = false;
                        break;
                    }
                }

                cb_select_all.setChecked(areAllLabsChecked);
            });
        }

        @Override
        public int getItemCount() {
            return subCategoryList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private CheckBox cb_select;

            public MyViewHolder(@NonNull View view) {
                super(view);
                cb_select = view.findViewById(R.id.cb_select);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }
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
                edtDesignation.setText(designation.getDesignation_name());
            }
        });
        builderSingle.show();
    }

    private void submitData() {
        mobileJSONArray = new JsonArray();
        landlineJSONArray = new JsonArray();
        tagJSONArray = new JsonArray();

        if (edtName.getText().toString().trim().isEmpty()) {
            edtName.setError("Please enter the name of business");
            edtName.requestFocus();
            edtName.getParent().requestChildFocus(edtName, edtName);
            return;
        }

        if (edtNature.getText().toString().trim().isEmpty()) {
            Utilities.showMessage("Please select the nature of business", context, 2);
            return;
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

        if (!edtEmail.getText().toString().trim().isEmpty()) {
            if (!Utilities.isEmailValid(edtEmail.getText().toString().trim())) {
                edtEmail.setError("Please enter valid email");
                edtEmail.requestFocus();
                edtEmail.getParent().requestChildFocus(edtEmail, edtEmail);
                return;
            }
        }

        if (!edtWebsite.getText().toString().trim().isEmpty()) {
            if (!Utilities.isWebsiteValid(edtWebsite.getText().toString().trim())) {
                edtWebsite.setError("Please enter valid website");
                edtWebsite.requestFocus();
                return;
            }
        }

        if (edtAddress.getText().toString().trim().isEmpty()) {
            edtAddress.setError("Please select address");
            edtAddress.requestFocus();
            edtAddress.getParent().requestChildFocus(edtAddress, edtAddress);
            return;
        }

        if (!edtPincode.getText().toString().trim().isEmpty()) {
            if (edtPincode.getText().toString().trim().length() != 6) {
                edtPincode.setError("Please enter pincode");
                edtPincode.requestFocus();
                edtPincode.getParent().requestChildFocus(edtPincode, edtPincode);
                return;
            }
        }

        if (edtCity.getText().toString().trim().isEmpty()) {
            edtCity.setError("Please select city");
            edtCity.requestFocus();
            edtCity.getParent().requestChildFocus(edtCity, edtCity);
            return;
        }

        for (int i = 0; i < mobileLayoutsList.size(); i++) {
            if (!((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).getText().toString().trim().equals("")) {
                JsonObject mobileJSONObj = new JsonObject();
                mobileJSONObj.addProperty("mobile", ((EditText) mobileLayoutsList.get(i).findViewById(R.id.edt_mobile)).getText().toString().trim());
                mobileJSONObj.addProperty("country_code", ((TextView) mobileLayoutsList.get(i).findViewById(R.id.tv_countrycode_mobile)).getText().toString());
                mobileJSONArray.add(mobileJSONObj);
            }
        }

        for (int i = 0; i < landlineLayoutsList.size(); i++) {
            if (!((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).getText().toString().trim().equals("")) {
                JsonObject landlineJSONObj = new JsonObject();
                landlineJSONObj.addProperty("landlinenumbers", ((EditText) landlineLayoutsList.get(i).findViewById(R.id.edt_landline)).getText().toString().trim());
                landlineJSONObj.addProperty("country_code", ((TextView) landlineLayoutsList.get(i).findViewById(R.id.tv_countrycode_landline)).getText().toString());
                landlineJSONArray.add(landlineJSONObj);
            }
        }

        for (int i = 0; i < tagsListTobeSubmitted.size(); i++) {
            JsonObject tagsJSONObj = new JsonObject();
            tagsJSONObj.addProperty("tag_id", tagsListTobeSubmitted.get(i).getTag_id());
            tagsJSONObj.addProperty("tag_name", tagsListTobeSubmitted.get(i).getTag_name());
            tagsJSONObj.addProperty("is_approved", tagsListTobeSubmitted.get(i).getIs_approved());
            tagJSONArray.add(tagsJSONObj);
        }

//        String isVisible = cbShowInSearch.isChecked() ? "1" : "0";
        String isVisible = "1";
        String isEnquiryAvailable = cbIsEnquiryAvailable.isChecked() ? "1" : "0";
        String isPickUpAvailable = cbIsPickUpAvailable.isChecked() ? "1" : "0";
        String isHomeDeliveryAvailable = cbIsHomeDeliveryAvailable.isChecked() ? "1" : "0";

        if (isPickUpAvailable.equals("0") && isHomeDeliveryAvailable.equals("0")) {
            Utilities.showMessage("Please select delivery type", context, 2);
            return;
        }

        JsonObject mainObj = new JsonObject();

        mainObj.addProperty("type", "updatebusiness");
        mainObj.addProperty("address", edtAddress.getText().toString().trim());
        mainObj.addProperty("business_name", edtName.getText().toString().trim());
        mainObj.addProperty("district", edtDistrict.getText().toString().trim());
        mainObj.addProperty("state", edtState.getText().toString().trim());
        mainObj.addProperty("city", edtCity.getText().toString().trim());
        mainObj.addProperty("country", edtCountry.getText().toString().trim());
        mainObj.addProperty("pincode", edtPincode.getText().toString().trim());
        mainObj.addProperty("longitude", longitude);
        mainObj.addProperty("latitude", latitude);
        mainObj.addProperty("landmark", "");
        mainObj.addProperty("locality", edtCity.getText().toString().trim());
        mainObj.addProperty("email", edtEmail.getText().toString().trim());
        mainObj.addProperty("designation", edtDesignation.getText().toString().trim());
        mainObj.addProperty("record_statusid", "1");
        mainObj.addProperty("website", edtWebsite.getText().toString().trim());
        mainObj.addProperty("order_online", "");
        mainObj.addProperty("image_url", imageName);
        mainObj.addProperty("type_id", categoryId);
        mainObj.add("sub_categories", subCategoryJsonArray);
        mainObj.addProperty("user_id", userId);
        mainObj.addProperty("business_id", searchDetails.getId());
        mainObj.addProperty("organization_name", "");
        mainObj.addProperty("other_details", "");
        mainObj.addProperty("tax_name", "");
        mainObj.addProperty("tax_alias", "");
        mainObj.addProperty("pan_number", "");
        mainObj.addProperty("gst_number", "");
        mainObj.addProperty("tax_status", "online");
        mainObj.addProperty("account_holder_name", "");
        mainObj.addProperty("alias", "");
        mainObj.addProperty("bank_name", "");
        mainObj.addProperty("ifsc_code", "");
        mainObj.addProperty("account_no", "");
        mainObj.addProperty("status", "online");
        mainObj.addProperty("is_visible", isVisible);
        mainObj.addProperty("is_enquiry_available", isEnquiryAvailable);
        mainObj.addProperty("is_pick_up_available", isPickUpAvailable);
        mainObj.addProperty("is_home_delivery_available", isHomeDeliveryAvailable);
        mainObj.add("mobile_number", mobileJSONArray);
        mainObj.add("landline_number", landlineJSONArray);
        mainObj.add("tag_name", tagJSONArray);

        Log.i("ADDBUSINESS", mainObj.toString());

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
                CropImage.activity(imageUri).setActivityMenuIconColor(getResources().getColor(R.color.black)).setGuidelines(CropImageView.Guidelines.ON).start(EditBusinessActivity.this);
            }

            if (requestCode == CAMERA_REQUEST) {
                CropImage.activity(photoURI).setActivityMenuIconColor(getResources().getColor(R.color.black)).setGuidelines(CropImageView.Guidelines.ON).start(EditBusinessActivity.this);
            }

            if (requestCode == LOCATION_REQUEST) {
                MapAddressListModel addressList = (MapAddressListModel) data.getSerializableExtra("addressList");
                if (addressList != null) {
                    latitude = addressList.getMap_location_lattitude();
                    longitude = addressList.getMap_location_logitude();
                    edtAddress.setText(addressList.getAddress_line_one());
                    edtCountry.setText(addressList.getCountry());
                    edtState.setText(addressList.getState());
                    edtDistrict.setText(addressList.getDistrict());
                    edtPincode.setText(addressList.getPincode());
                    edtCity.setText(addressList.getDistrict());
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
        String destinationFile = Environment.getExternalStorageDirectory() + "/Joinsta Gharse/"
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

        File photoFileToUpload = new File(destinationFile);
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
                        String imageUrl = jsonObject.getString("document_url");
                        imageName = jsonObject.getString("name");

                        if (!imageUrl.equals("")) {
                            Picasso.with(context)
                                    .load(imageUrl)
                                    .into(imvPhoto1);
                            imvPhoto2.setVisibility(View.GONE);
                            imvPhoto1.setVisibility(View.VISIBLE);
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
                        tv_title.setText("Business details submitted successfully");
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

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.icon_backarrow_black);
        toolbar.setNavigationOnClickListener(view -> finish());
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


}
