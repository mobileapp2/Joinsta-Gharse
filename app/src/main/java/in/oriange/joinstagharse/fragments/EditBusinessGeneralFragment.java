package in.oriange.joinstagharse.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import in.oriange.joinstagharse.models.SubCategotyListModel;
import in.oriange.joinstagharse.models.SubCategotyListPojo;
import in.oriange.joinstagharse.models.TagsListModel;
import in.oriange.joinstagharse.utilities.APICall;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.MultipartUtility;
import in.oriange.joinstagharse.utilities.UserSessionManager;
import in.oriange.joinstagharse.utilities.Utilities;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static in.oriange.joinstagharse.utilities.ApplicationConstants.IMAGE_LINK;
import static in.oriange.joinstagharse.utilities.PermissionUtil.doesAppNeedPermissions;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.CAMERA_AND_STORAGE_PERMISSION;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.isCameraStoragePermissionGiven;

public class EditBusinessGeneralFragment extends Fragment {

    @BindView(R.id.imv_photo1)
    ImageView imvPhoto1;
    @BindView(R.id.imv_photo2)
    ImageView imvPhoto2;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
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
    @BindView(R.id.sv_scroll)
    ScrollView svScroll;
    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;

    private ArrayList<CategotyListModel> categotyList;
    private ArrayList<SubCategotyListModel> subCategoryList;
    private ArrayList<GetTagsListModel.ResultBean> tagsListFromAPI;
    private ArrayList<TagsListModel> tagsListTobeSubmitted;
    private JsonArray subCategoryJsonArray;

    private String userId, imageName = "", categoryId = "0";
    private Uri photoURI;
    private final int CAMERA_REQUEST = 100, GALLERY_REQUEST = 200;
    private File file, profilPicFolder;
    private CheckBox cb_select_all;

    private LocalBroadcastManager localBroadcastManager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_business_general_details, container, false);
        ButterKnife.bind(this, rootView);

        context = getActivity();
        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        return rootView;
    }

    private void init() {
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        categotyList = new ArrayList<>();
        subCategoryList = new ArrayList<>();
        tagsListTobeSubmitted = new ArrayList<>();
        tagsListFromAPI = new ArrayList<>();
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
        GetBusinessModel.ResultBean searchDetails = (GetBusinessModel.ResultBean) this.getArguments().getSerializable("searchDetails");

        categoryId = searchDetails.getType_id();

        if (!searchDetails.getImage_url().isEmpty()) {
            Uri uri = Uri.parse(searchDetails.getImage_url());
            imageName = uri.getLastPathSegment();
        }

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

        List<GetBusinessModel.ResultBean.TagBean> tagsList = searchDetails.getTag().get(0);
        if (tagsList != null)
            if (tagsList.size() > 0)
                for (int i = 0; i < tagsList.size(); i++) {
                    if (!tagsList.get(i).getTag_name().trim().equals("")) {
                        tagsListTobeSubmitted.add(new TagsListModel(tagsList.get(i).getTag_id(), tagsList.get(i).getTag_name(), tagsList.get(i).getIs_approved()));
                        tagContainer.addTag(tagsList.get(i).getTag_name());
                    }
                }

        if (Utilities.isNetworkAvailable(context)) {
            new GetTagsList().execute("0");
        }

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("EditBusinessGeneralFragment");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void setEventHandler() {
        imvPhoto1.setOnClickListener(view -> {selectImage();
        });

        imvPhoto2.setOnClickListener(view -> {
            selectImage();
        });

        edtNature.setOnClickListener(v -> {
            if (categotyList.size() == 0) {
                if (Utilities.isNetworkAvailable(context)) {
                    new GetCategotyList().execute("0", "0", "1");
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }
            } else {
                showCategoryListDialog();
            }
        });

        edtSubtype.setOnClickListener(v -> {

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
        });

        edtDesignation.setOnClickListener(v -> {
            if (Utilities.isNetworkAvailable(context)) {
                new GetDesignationList().execute("1");
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }

        });

        btnAddTag.setOnClickListener(v -> {

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

        edtTag.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
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
        });
    }

    private void selectImage() {
        if (Utilities.isNetworkAvailable(context)) {
            if (doesAppNeedPermissions()) {
                if (!isCameraStoragePermissionGiven(context, CAMERA_AND_STORAGE_PERMISSION)) {
                    return;
                }
            }
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            return;
        }

        final CharSequence[] options = {"Take a Photo", "Choose from Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builder.setCancelable(false);
        builder.setItems(options, (dialog, item) -> {
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
        });
        builder.setPositiveButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog alertD = builder.create();
        alertD.show();
    }

    private void submitData() {
        JsonArray tagJSONArray = new JsonArray();

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

        for (int i = 0; i < tagsListTobeSubmitted.size(); i++) {
            JsonObject tagsJSONObj = new JsonObject();
            tagsJSONObj.addProperty("tag_id", tagsListTobeSubmitted.get(i).getTag_id());
            tagsJSONObj.addProperty("tag_name", tagsListTobeSubmitted.get(i).getTag_name());
            tagsJSONObj.addProperty("is_approved", tagsListTobeSubmitted.get(i).getIs_approved());
            tagJSONArray.add(tagsJSONObj);
        }

        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("EditBusinessBusinessActivity")
                .putExtra("imageName", imageName)
                .putExtra("businessName", edtName.getText().toString().trim())
                .putExtra("categoryId", categoryId)
                .putExtra("subCategoryJsonArray", subCategoryJsonArray.toString())
                .putExtra("designation", edtDesignation.getText().toString().trim())
                .putExtra("tagsJsonArray", tagJSONArray.toString()));
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("EditBusinessAddressFragment"));

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

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST) {
                Uri imageUri = data.getData();
                CropImage.activity(imageUri).setActivityMenuIconColor(getResources().getColor(R.color.black)).setGuidelines(CropImageView.Guidelines.ON).start(getContext(), this);
            }

            if (requestCode == CAMERA_REQUEST) {
                CropImage.activity(photoURI).setActivityMenuIconColor(getResources().getColor(R.color.black)).setGuidelines(CropImageView.Guidelines.ON).start(getContext(), this);
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

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            submitData();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }
}
