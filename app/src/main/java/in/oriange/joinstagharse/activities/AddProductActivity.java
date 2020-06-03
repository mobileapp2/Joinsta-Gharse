package in.oriange.joinstagharse.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

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
import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.models.MasterModel;
import in.oriange.joinstagharse.models.ProductsCategoryModel;
import in.oriange.joinstagharse.models.UnitOfMeasuresModel;
import in.oriange.joinstagharse.utilities.APICall;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.MultipartUtility;
import in.oriange.joinstagharse.utilities.ParamsPojo;
import in.oriange.joinstagharse.utilities.UserSessionManager;
import in.oriange.joinstagharse.utilities.Utilities;

import static in.oriange.joinstagharse.utilities.Utilities.changeStatusBar;
import static in.oriange.joinstagharse.utilities.Utilities.setPaddingForView;

public class AddProductActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edt_code)
    EditText edtCode;
    @BindView(R.id.edt_name)
    EditText edtName;
    @BindView(R.id.edt_description)
    EditText edtDescription;
    @BindView(R.id.edt_category)
    EditText edtCategory;
    @BindView(R.id.edt_unit_of_measure)
    EditText edtUnitOfMeasure;
    @BindView(R.id.edt_max_retail_price)
    EditText edtMaxRetailPrice;
    @BindView(R.id.edt_selling_price)
    EditText edtSellingPrice;
    @BindView(R.id.edt_stock)
    EditText edtStock;
    @BindView(R.id.cb_is_in_stock)
    CheckBox cbIsInStock;
    @BindView(R.id.cb_is_featured)
    CheckBox cbIsFeatured;
    @BindView(R.id.cb_is_show_in_list)
    CheckBox cbIsShowInList;
    @BindView(R.id.cb_is_inclusive_tax)
    CheckBox cbIsInclusiveTax;
    @BindView(R.id.rv_images)
    RecyclerView rvImages;
    @BindView(R.id.edt_brochure)
    EditText edtBrochure;
    @BindView(R.id.btn_save)
    MaterialButton btnSave;

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private String userId, businessId, productCategoryId, unitOfMeasureId;
    private ArrayList<MasterModel> imageList;
    private List<ProductsCategoryModel.ResultBean> productCategoriesList;
    private List<UnitOfMeasuresModel.ResultBean> unitOfMeasuresList;
    private int latestPosition;
    private File photoFileFolder;
    private Uri photoURI;
    private final int DOCUMENT_REQUEST = 100, CAMERA_REQUEST = 200, GALLERY_REQUEST = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventListner();
        setUpToolbar();
    }

    private void init() {
        context = AddProductActivity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);
        changeStatusBar(context, getWindow());
        rvImages.setLayoutManager(new GridLayoutManager(context, 3));

        imageList = new ArrayList<>();
        productCategoriesList = new ArrayList<>();
        unitOfMeasuresList = new ArrayList<>();

        photoFileFolder = new File(Environment.getExternalStorageDirectory() + "/Joinsta eOrder/" + "Products");
        if (!photoFileFolder.exists())
            photoFileFolder.mkdirs();

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
        businessId = getIntent().getStringExtra("businessId");

        imageList.add(new MasterModel("", ""));
        imageList.add(new MasterModel("", ""));
        imageList.add(new MasterModel("", ""));
        rvImages.setAdapter(new ImagesAdapter());
    }

    private void setEventListner() {
        edtCategory.setOnClickListener(v -> {

            if (productCategoriesList.size() == 0)
                if (Utilities.isNetworkAvailable(context))
                    new GetProductCategories().execute();
                else
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            else
                showProductCategoriesListDialog();
        });

        edtUnitOfMeasure.setOnClickListener(v -> {
            if (unitOfMeasuresList.size() == 0)
                if (Utilities.isNetworkAvailable(context))
                    new GetUnitOfMeasures().execute();
                else
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            else
                showUnitOfMeasuresListDialog();
        });

        edtBrochure.setOnClickListener(v -> {
            if (Utilities.isNetworkAvailable(context)) {
                Intent intent = new Intent(context, NormalFilePickActivity.class);
                intent.putExtra(Constant.MAX_NUMBER, 1);
                intent.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf"});
                startActivityForResult(intent, DOCUMENT_REQUEST);
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }

        });

        btnSave.setOnClickListener(v -> submitData());
    }

    private void submitData() {
        if (edtCode.getText().toString().trim().isEmpty()) {
            edtCode.setError("Please enter product code");
            edtCode.requestFocus();
            edtCode.getParent().requestChildFocus(edtCode, edtCode);
            return;
        }

        if (edtName.getText().toString().trim().isEmpty()) {
            edtName.setError("Please enter product name");
            edtName.requestFocus();
            edtName.getParent().requestChildFocus(edtName, edtName);
            return;
        }

        if (edtDescription.getText().toString().trim().isEmpty()) {
            edtDescription.setError("Please enter product description");
            edtDescription.requestFocus();
            edtDescription.getParent().requestChildFocus(edtDescription, edtDescription);
            return;
        }

        if (edtUnitOfMeasure.getText().toString().trim().isEmpty()) {
            Utilities.showMessage("Please select unit of measurement", context, 2);
            return;
        }

        if (edtMaxRetailPrice.getText().toString().trim().isEmpty())
            edtMaxRetailPrice.setText("0");

        if (edtSellingPrice.getText().toString().trim().isEmpty())
            edtSellingPrice.setText("0");

        if (Integer.parseInt(edtSellingPrice.getText().toString().trim()) >
                Integer.parseInt(edtMaxRetailPrice.getText().toString().trim())) {
            Utilities.showMessage("Selling price cannot be greater than MRP", context, 2);
            return;
        }

        if (edtStock.getText().toString().trim().isEmpty()) {
            edtStock.setError("Please enter available stock count");
            edtStock.requestFocus();
            edtStock.getParent().requestChildFocus(edtStock, edtStock);
            return;
        }

        String isInStock = cbIsInStock.isChecked() ? "1" : "0";
        String isFeatured = cbIsFeatured.isChecked() ? "1" : "0";
        String isInclusiveTax = cbIsInclusiveTax.isChecked() ? "1" : "0";
        String isShowInList = cbIsShowInList.isChecked() ? "1" : "0";

        JsonArray documentsArray = new JsonArray();

        for (int i = 0; i < imageList.size(); i++) {
            if (!imageList.get(i).getName().equals("")) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("image", imageList.get(i).getName());
                jsonObject.addProperty("type", "image");
                documentsArray.add(jsonObject);
            }
        }

        if (!edtBrochure.getText().toString().trim().isEmpty()) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("image", edtBrochure.getText().toString().trim());
            jsonObject.addProperty("type", "product_brouchure");
            documentsArray.add(jsonObject);
        }

        JsonObject mainObj = new JsonObject();

        mainObj.addProperty("type", "addProduct");
        mainObj.addProperty("name", edtName.getText().toString().trim());
        mainObj.addProperty("product_code", edtCode.getText().toString().trim());
        mainObj.addProperty("category_id", productCategoryId);
        mainObj.addProperty("description", edtDescription.getText().toString().trim());
        mainObj.addProperty("unit_of_measure", unitOfMeasureId);
        mainObj.addProperty("max_retail_price", edtMaxRetailPrice.getText().toString().trim());
        mainObj.addProperty("selling_price", edtSellingPrice.getText().toString().trim());
        mainObj.addProperty("stock", edtStock.getText().toString().trim());
        mainObj.addProperty("in_stock", isInStock);
        mainObj.addProperty("is_featured", isFeatured);
        mainObj.addProperty("is_show_in_list", isShowInList);
        mainObj.addProperty("is_inclusive_tax", isInclusiveTax);
        mainObj.addProperty("business_id", businessId);
        mainObj.addProperty("user_id", userId);
        mainObj.add("product_images", documentsArray);

        if (Utilities.isNetworkAvailable(context))
            new AddProduct().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
        else
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 3);
    }

    private class GetProductCategories extends AsyncTask<String, Void, String> {

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
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "getProductCategories"));
            param.add(new ParamsPojo("business_id", businessId));
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
                    productCategoriesList = new ArrayList<>();
                    ProductsCategoryModel pojoDetails = new Gson().fromJson(result, ProductsCategoryModel.class);
                    type = pojoDetails.getType();
                    message = pojoDetails.getMessage();

                    if (type.equalsIgnoreCase("success")) {
                        productCategoriesList = pojoDetails.getResult();
                        if (productCategoriesList.size() > 0) {
                            showProductCategoriesListDialog();
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

    private void showProductCategoriesListDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Product Category");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row);

        for (int i = 0; i < productCategoriesList.size(); i++) {
            arrayAdapter.add(String.valueOf(productCategoriesList.get(i).getProduct_category()));
        }

        builderSingle.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
            ProductsCategoryModel.ResultBean obj = productCategoriesList.get(which);
            edtCategory.setText(obj.getProduct_category());
            productCategoryId = obj.getId();
        });
        builderSingle.show();
    }

    private class GetUnitOfMeasures extends AsyncTask<String, Void, String> {

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
            List<ParamsPojo> param = new ArrayList<ParamsPojo>();
            param.add(new ParamsPojo("type", "getUnitOfMeasures"));
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
                    unitOfMeasuresList = new ArrayList<>();
                    UnitOfMeasuresModel pojoDetails = new Gson().fromJson(result, UnitOfMeasuresModel.class);
                    type = pojoDetails.getType();
                    message = pojoDetails.getMessage();

                    if (type.equalsIgnoreCase("success")) {
                        unitOfMeasuresList = pojoDetails.getResult();
                        if (unitOfMeasuresList.size() > 0) {
                            showUnitOfMeasuresListDialog();
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

    private void showUnitOfMeasuresListDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Post Type");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row);

        for (int i = 0; i < unitOfMeasuresList.size(); i++) {
            arrayAdapter.add(String.valueOf(unitOfMeasuresList.get(i).getUnitOfMeasure()));
        }

        builderSingle.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
            UnitOfMeasuresModel.ResultBean obj = unitOfMeasuresList.get(which);
            edtUnitOfMeasure.setText(obj.getUnitOfMeasure());
            unitOfMeasureId = obj.getId();
        });
        builderSingle.show();
    }

    private class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.MyViewHolder> {

        ImagesAdapter() {

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.grid_row_images, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int pos) {
            final int position = holder.getAdapterPosition();

            if (!imageList.get(position).getId().isEmpty()) {
                Glide.with(context)
                        .load(imageList.get(position).getId())
                        .into(holder.imv_image);
                setPaddingForView(context, holder.imv_image, 0);
                holder.imv_image_delete.setVisibility(View.VISIBLE);
                holder.imv_image_delete.bringToFront();
            }

            holder.imv_image.setOnClickListener(v -> {
                if (Utilities.isNetworkAvailable(context)) {
                    latestPosition = position;
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
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }
            });

            holder.imv_image_delete.setOnClickListener(v -> {
                imageList.set(position, new MasterModel("", ""));
                rvImages.setAdapter(new ImagesAdapter());
            });
        }

        @Override
        public int getItemCount() {
            return imageList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private ImageView imv_image, imv_image_delete;

            private MyViewHolder(View view) {
                super(view);
                imv_image = view.findViewById(R.id.imv_image);
                imv_image_delete = view.findViewById(R.id.imv_image_delete);

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST) {
                Uri imageUri = data.getData();
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(AddProductActivity.this);
            }

            if (requestCode == CAMERA_REQUEST) {
                CropImage.activity(photoURI).setGuidelines(CropImageView.Guidelines.ON).start(AddProductActivity.this);
            }

            if (requestCode == DOCUMENT_REQUEST) {
                ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                new UploadImageAndDocument().execute("uploadProductImage", list.get(0).getPath(), "1");
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
        String destinationFile = Environment.getExternalStorageDirectory() + "/Joinsta eOrder/"
                + "Products/" + "uplimg.png";

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
        new UploadImageAndDocument().execute("uploadProductImage", photoFileToUpload.getPath(), "0");
    }

    private class UploadImageAndDocument extends AsyncTask<String, Integer, String> {
        private String TYPE = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            TYPE = params[2];
            StringBuilder res = new StringBuilder();
            try {
                MultipartUtility multipart = new MultipartUtility(ApplicationConstants.FILEUPLOADAPI, "UTF-8");

                multipart.addFormField("request_type", params[0]);
                multipart.addFilePart("document", new File(params[1]));

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
            String type = "";
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    if (type.equalsIgnoreCase("success")) {
                        JSONObject jsonObject = mainObj.getJSONObject("result");
                        if (TYPE.equals("1")) {
                            edtBrochure.setText(jsonObject.getString("name"));
                        } else if (TYPE.equals("0")) {
                            imageList.set(latestPosition, new MasterModel(jsonObject.getString("name"), jsonObject.getString("document_url")));
                            rvImages.setAdapter(new ImagesAdapter());
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

    private class AddProduct extends AsyncTask<String, Void, String> {

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
            res = APICall.JSONAPICall(ApplicationConstants.PRODUCTSAPI, params[0]);
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
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("BusinessProductsActivity"));

                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Product added successfully");
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
                        Utilities.showMessage(message, context, 3);
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


}
