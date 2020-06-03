package in.oriange.eorder.activities;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
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
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import in.oriange.eorder.R;
import in.oriange.eorder.models.AddressModel;
import in.oriange.eorder.models.BookOrderGetMyOrdersModel;
import in.oriange.eorder.models.GetBusinessModel;
import in.oriange.eorder.models.MasterModel;
import in.oriange.eorder.utilities.APICall;
import in.oriange.eorder.utilities.ApplicationConstants;
import in.oriange.eorder.utilities.MultipartUtility;
import in.oriange.eorder.utilities.UserSessionManager;
import in.oriange.eorder.utilities.Utilities;

import static in.oriange.eorder.utilities.ApplicationConstants.IMAGE_LINK;
import static in.oriange.eorder.utilities.Utilities.changeStatusBar;
import static in.oriange.eorder.utilities.Utilities.setPaddingForView;

public class BookOrderSelectDeliveryTypeActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rb_individual)
    RadioButton rbIndividual;
    @BindView(R.id.ll_ind)
    LinearLayout llInd;
    @BindView(R.id.rb_business)
    RadioButton rbBusiness;
    @BindView(R.id.ll_biz)
    LinearLayout llBiz;
    @BindView(R.id.imv_user)
    CircleImageView imvUser;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_mobile)
    TextView tvMobile;
    @BindView(R.id.ll_individual_details)
    LinearLayout llIndividualDetails;
    @BindView(R.id.rv_business)
    RecyclerView rvBusiness;
    @BindView(R.id.ll_business)
    LinearLayout llBusiness;
    @BindView(R.id.rb_store_pickup)
    RadioButton rbStorePickup;
    @BindView(R.id.ll_store)
    LinearLayout llStore;
    @BindView(R.id.rb_home_delivery)
    RadioButton rbHomeDelivery;
    @BindView(R.id.ll_home)
    LinearLayout llHome;
    @BindView(R.id.tv_store_address)
    TextView tvStoreAddress;
    @BindView(R.id.ll_store_pickup)
    LinearLayout llStorePickup;
    @BindView(R.id.rv_address)
    RecyclerView rvAddress;
    @BindView(R.id.ll_home_delivery)
    LinearLayout llHomeDelivery;
    @BindView(R.id.rv_images)
    RecyclerView rvImages;
    @BindView(R.id.cv_images)
    CardView cvImages;
    @BindView(R.id.btn_save)
    MaterialButton btnSave;
    @BindView(R.id.btn_address)
    Button btnAddress;

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private String userId, businessOwnerId, businessOwnerAddress, isHomeDeliveryAvailable, isPickUpAvailable,
            orderType, orderText = "", customerBusinessId = "", customerAddressId = "0", customerAddress = "", orderImageArray = "";

    private List<GetBusinessModel.ResultBean> businessList;
    private List<AddressModel.ResultBean> addressList;
    private GetBusinessModel.ResultBean businessDetails;
    private ArrayList<MasterModel> imageList;
    private AddressModel.ResultBean addressDetails;
    private LocalBroadcastManager localBroadcastManager, localBroadcastManager1;
    private BookOrderGetMyOrdersModel.ResultBean orderDetails;

    private Uri photoURI;
    private File orderFileFolder;
    private int latestPosition;
    private final int CAMERA_REQUEST = 100, GALLERY_REQUEST = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_order_select_delivery_type);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = BookOrderSelectDeliveryTypeActivity.this;
        session = new UserSessionManager(context);
        changeStatusBar(context, getWindow());
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);
        pd.setMessage("Please wait ...");
        pd.setCancelable(false);

        rvImages.setLayoutManager(new GridLayoutManager(context, 3));
        rvBusiness.setLayoutManager(new LinearLayoutManager(context));
        rvAddress.setLayoutManager(new LinearLayoutManager(context));


        imageList = new ArrayList<>();
        businessList = new ArrayList<>();
        addressList = new ArrayList<>();

        imageList.add(new MasterModel("", ""));
        imageList.add(new MasterModel("", ""));
        imageList.add(new MasterModel("", ""));
        rvImages.setAdapter(new ImagesAdapter());

        orderFileFolder = new File(Environment.getExternalStorageDirectory() + "/Joinsta eOrder/" + "Book Order");
        if (!orderFileFolder.exists())
            orderFileFolder.mkdirs();

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
            String imageUrl = json.getString("image_url");
            String name = json.getString("first_name");
            String country_code = json.getString("country_code");
            String mobile = json.getString("mobile");

            if (!imageUrl.equals("")) {
                String url = IMAGE_LINK + userId + "/" + imageUrl;
                Picasso.with(context)
                        .load(url)
                        .placeholder(R.drawable.icon_userphoto)
                        .into(imvUser);
            }

            tvName.setText(name);
            tvMobile.setText("+" + country_code + mobile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDefault() {
        businessOwnerId = getIntent().getStringExtra("businessOwnerId");
        businessOwnerAddress = getIntent().getStringExtra("businessOwnerAddress");
        isHomeDeliveryAvailable = getIntent().getStringExtra("isHomeDeliveryAvailable");
        isPickUpAvailable = getIntent().getStringExtra("isPickUpAvailable");
        orderType = getIntent().getStringExtra("orderType");  //order_type = 'order_with_product' - 1, 'order_by_image' - 2,'order_by_text' - 3
        orderText = getIntent().getStringExtra("orderText");
        orderImageArray = getIntent().getStringExtra("orderImageArray");
        orderDetails = (BookOrderGetMyOrdersModel.ResultBean) getIntent().getSerializableExtra("orderDetails");

        if (orderDetails == null) {
            orderDetails = new BookOrderGetMyOrdersModel.ResultBean();
        }

        if (!businessOwnerAddress.equals("")) {
            tvStoreAddress.setText(businessOwnerAddress);
            tvStoreAddress.setTextColor(context.getResources().getColor(R.color.black));
        } else {
            tvStoreAddress.setText("Store address not available");
            tvStoreAddress.setTextColor(context.getResources().getColor(R.color.red));
        }

        if (isHomeDeliveryAvailable.equals("0"))
            llHome.setVisibility(View.GONE);

        if (isPickUpAvailable.equals("0"))
            llStore.setVisibility(View.GONE);

        if (!orderType.equals("1"))
            cvImages.setVisibility(View.GONE);

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("BookOrderSelectDeliveryTypeActivity");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);

        localBroadcastManager1 = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter1 = new IntentFilter("BookOrderSelectDeliveryTypeActivityAddressRefresh");
        localBroadcastManager1.registerReceiver(broadcastReceiver1, intentFilter1);
    }

    private void setEventHandler() {
        rbIndividual.setOnClickListener(v -> {
            if (rbIndividual.isChecked()) {
                llInd.setBackgroundColor(context.getResources().getColor(R.color.lightred));
                llBiz.setBackgroundColor(context.getResources().getColor(R.color.white));

                rbBusiness.setChecked(false);

                llIndividualDetails.setVisibility(View.VISIBLE);
                llBusiness.setVisibility(View.GONE);
            }
        });

        rbBusiness.setOnClickListener(v -> {
            if (rbBusiness.isChecked()) {
                rbIndividual.setChecked(false);
                llBiz.setBackgroundColor(context.getResources().getColor(R.color.lightred));
                llInd.setBackgroundColor(context.getResources().getColor(R.color.white));
                llIndividualDetails.setVisibility(View.GONE);
                if (businessList.size() == 0) {
                    if (Utilities.isNetworkAvailable(context))
                        new GetBusiness().execute();
                    else
                        Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                } else {
                    llBusiness.setVisibility(View.VISIBLE);
                    rvBusiness.setAdapter(new BusinessAdapter());
                }
            }
        });

        rbStorePickup.setOnClickListener(v -> {
            if (rbStorePickup.isChecked()) {
                llStore.setBackgroundColor(context.getResources().getColor(R.color.lightred));
                llHome.setBackgroundColor(context.getResources().getColor(R.color.white));

                rbHomeDelivery.setChecked(false);

                llStorePickup.setVisibility(View.VISIBLE);
                llHomeDelivery.setVisibility(View.GONE);
            }
        });

        rbHomeDelivery.setOnClickListener(v -> {
            if (rbHomeDelivery.isChecked()) {
                llHome.setBackgroundColor(context.getResources().getColor(R.color.lightred));
                llStore.setBackgroundColor(context.getResources().getColor(R.color.white));

                rbStorePickup.setChecked(false);

                llStorePickup.setVisibility(View.GONE);
                if (addressList.size() == 0) {
                    if (Utilities.isNetworkAvailable(context))
                        new GetAddress().execute();
                    else
                        Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                } else {
                    llHomeDelivery.setVisibility(View.VISIBLE);
                    rvAddress.setAdapter(new AddressAdapter());
                }
            }
        });

        btnAddress.setOnClickListener(v -> startActivity(new Intent(context, AddAddressActivity.class)));

        btnSave.setOnClickListener(v -> submitData());
    }

    private void submitData() {
        String purchaseOrderType = "0";
        String deliveryStatus = "";
        JsonArray imageJsonArray = new JsonArray();

        if (rbIndividual.isChecked()) {
            purchaseOrderType = "1";
        } else if (rbBusiness.isChecked()) {
            if (customerBusinessId.equals("")) {
                Utilities.showMessage("Please select business", context, 2);
                return;
            }
            purchaseOrderType = "2";
        } else {
            Utilities.showMessage("Please select purchase type", context, 2);
            return;
        }

        if (rbStorePickup.isChecked()) {
            deliveryStatus = "store_pickup";
        } else if (rbHomeDelivery.isChecked()) {
            if (customerAddressId.equals("0")) {
                Utilities.showMessage("Please select address", context, 2);
                return;
            }
            deliveryStatus = "home_delivery";
        } else {
            Utilities.showMessage("Please select delivery type", context, 2);
            return;
        }

        if (orderType.equals("1")) {
            for (int i = 0; i < imageList.size(); i++) {
                if (!imageList.get(i).getName().equals("")) {
                    imageJsonArray.add(imageList.get(i).getName());
                }
            }
            orderImageArray = imageJsonArray.toString();
        }

        startActivity(new Intent(context, BookOrderPurchaseSummaryActivity.class)
                .putExtra("purchaseOrderType", purchaseOrderType)            // purchase_order_type = 'individual' - 1, 'business' -2
                .putExtra("deliveryStatus", deliveryStatus)
                .putExtra("businessOwnerId", businessOwnerId)
                .putExtra("businessOwnerAddress", businessOwnerAddress)
                .putExtra("customerAddressId", customerAddressId)
                .putExtra("customerAddress", customerAddress)
                .putExtra("orderType", orderType)                   //order_type = 'order_with_product' - 1, 'order_by_image' - 2,'order_by_text' - 3
                .putExtra("orderText", orderText)
                .putExtra("businessDetails", businessDetails)
                .putExtra("orderImageArray", orderImageArray)
                .putExtra("orderDetails", orderDetails));
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.icon_backarrow_black);
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
        localBroadcastManager1.unregisterReceiver(broadcastReceiver1);
    }

    private class GetBusiness extends AsyncTask<String, Void, String> {

        private ProgressDialog pd;

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
            obj.addProperty("type", "getbusiness");
            obj.addProperty("user_id", userId);
            obj.addProperty("current_user_id", userId);
            res = APICall.JSONAPICall(ApplicationConstants.BUSINESSAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    businessList = new ArrayList<>();
                    GetBusinessModel pojoDetails = new Gson().fromJson(result, GetBusinessModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        businessList = pojoDetails.getResult();
                        if (businessList.size() > 0) {
                            llBusiness.setVisibility(View.VISIBLE);
                            rvBusiness.setAdapter(new BusinessAdapter());
                        } else {
                            Utilities.showMessage("There are no businesses added by you", context, 2);
                            rbBusiness.setChecked(false);
                            rbIndividual.setChecked(true);
                        }
                    } else {
                        Utilities.showMessage("There are no businesses added by you", context, 2);
                        rbBusiness.setChecked(false);
                        rbIndividual.setChecked(true);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_book_order_business_select, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
            final int position = holder.getAdapterPosition();
            final GetBusinessModel.ResultBean searchDetails = businessList.get(position);

            if (searchDetails.isChecked()) {
                holder.cv_mainlayout.setBackgroundColor(context.getResources().getColor(R.color.lightred));
                holder.tv_heading.setTextColor(context.getResources().getColor(R.color.black));
            } else {
                holder.cv_mainlayout.setBackgroundColor(context.getResources().getColor(R.color.white));
                holder.tv_heading.setTextColor(context.getResources().getColor(R.color.darkGray));
            }

            holder.tv_heading.setText(searchDetails.getBusiness_code() + " - " + searchDetails.getBusiness_name());

            holder.cv_mainlayout.setOnClickListener((View.OnClickListener) v -> {
                for (int i = 0; i < businessList.size(); i++) {
                    businessList.get(i).setChecked(false);
                }
                businessDetails = businessList.get(position);
                customerBusinessId = businessList.get(position).getId();
                businessList.get(position).setChecked(true);
                notifyDataSetChanged();
            });

            if (position == businessList.size() - 1)
                holder.view_divider.setVisibility(View.GONE);

        }

        @Override
        public int getItemCount() {
            return businessList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private LinearLayout cv_mainlayout;
            private TextView tv_heading, tv_subheading;
            private View view_divider;

            public MyViewHolder(View view) {
                super(view);
                cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
                tv_heading = view.findViewById(R.id.tv_heading);
                tv_subheading = view.findViewById(R.id.tv_subheading);
                view_divider = view.findViewById(R.id.view_divider);
                tv_subheading.setVisibility(View.GONE);
            }
        }
    }

    private class GetAddress extends AsyncTask<String, Void, String> {

        private ProgressDialog pd;

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
            obj.addProperty("type", "getUserAddress");
            obj.addProperty("user_id", userId);
            res = APICall.JSONAPICall(ApplicationConstants.ADDRESSAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    addressList = new ArrayList<>();
                    AddressModel pojoDetails = new Gson().fromJson(result, AddressModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        addressList = pojoDetails.getResult();
                        if (addressList.size() > 0) {
                            llHomeDelivery.setVisibility(View.VISIBLE);
                            rvAddress.setAdapter(new AddressAdapter());
                        } else {
                            showAddAddressDialog();
                        }
                    } else {
                        showAddAddressDialog();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showAddAddressDialog() {
        llHome.setBackgroundColor(context.getResources().getColor(R.color.white));
        rbHomeDelivery.setChecked(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builder.setTitle("Add Address");
        builder.setMessage("Please add address before you proceed for home delivery option");
        builder.setPositiveButton("Add", (dialog, which) -> {
            startActivity(new Intent(context, AddAddressActivity.class));
        });
        builder.create().show();
    }

    private class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_book_order_business_select, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
            final int position = holder.getAdapterPosition();
            final AddressModel.ResultBean searchDetails = addressList.get(position);

            if (searchDetails.isChecked()) {
                holder.cv_mainlayout.setBackgroundColor(context.getResources().getColor(R.color.lightred));
                holder.tv_heading.setTextColor(context.getResources().getColor(R.color.black));
                holder.tv_subheading.setTextColor(context.getResources().getColor(R.color.black));
            } else {
                holder.cv_mainlayout.setBackgroundColor(context.getResources().getColor(R.color.white));
                holder.tv_heading.setTextColor(context.getResources().getColor(R.color.darkGray));
                holder.tv_subheading.setTextColor(context.getResources().getColor(R.color.darkGray));
            }

            holder.tv_heading.setText(searchDetails.getFull_name());
            holder.tv_subheading.setText(searchDetails.getAddress_line1());

            holder.cv_mainlayout.setOnClickListener(v -> {
                for (int i = 0; i < addressList.size(); i++) {
                    addressList.get(i).setChecked(false);
                }
                addressDetails = addressList.get(position);
                customerAddressId = addressList.get(position).getUser_address_id();
                customerAddress = addressList.get(position).getAddress_line1();
                addressList.get(position).setChecked(true);
                notifyDataSetChanged();
            });

            if (position == addressList.size() - 1)
                holder.view_divider.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            return addressList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private LinearLayout cv_mainlayout;
            private TextView tv_heading, tv_subheading;
            private View view_divider;

            public MyViewHolder(View view) {
                super(view);
                cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
                tv_heading = view.findViewById(R.id.tv_heading);
                tv_subheading = view.findViewById(R.id.tv_subheading);
                view_divider = view.findViewById(R.id.view_divider);
            }
        }
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

            holder.imv_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Utilities.isNetworkAvailable(context)) {
                        Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                        return;
                    }

                    latestPosition = position;

                    final CharSequence[] options = {"Take a Photo", "Choose from Gallery"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                    builder.setCancelable(false);
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (options[item].equals("Take a Photo")) {
                                File file = new File(orderFileFolder, "doc_image.png");
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
            });

            holder.imv_image_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageList.set(position, new MasterModel("", ""));
                    rvImages.setAdapter(new ImagesAdapter());
                }
            });
        }

        @Override
        public int getItemCount() {
            return imageList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
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
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(this);
            }

            if (requestCode == CAMERA_REQUEST) {
                CropImage.activity(photoURI).setGuidelines(CropImageView.Guidelines.ON).start(this);
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
                + "Book Order/" + "uplimg.png";

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
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "";
            try {
                MultipartUtility multipart = new MultipartUtility(ApplicationConstants.FILEUPLOADAPI, "UTF-8");

                multipart.addFormField("request_type", "uploadOrderImage");
                multipart.addFilePart("document", new File(params[0]));

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
            String type = "";
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    if (type.equalsIgnoreCase("success")) {
                        JSONObject jsonObject = mainObj.getJSONObject("result");
                        imageList.set(latestPosition, new MasterModel(jsonObject.getString("name"), jsonObject.getString("document_url")));
                        rvImages.setAdapter(new ImagesAdapter());
                    } else {
                        Utilities.showMessage("Image upload failed", context, 3);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.showMessage(e.toString(), context, 3);
            }
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    private BroadcastReceiver broadcastReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Utilities.isNetworkAvailable(context))
                new GetAddress().execute();
            else
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }
    };

}