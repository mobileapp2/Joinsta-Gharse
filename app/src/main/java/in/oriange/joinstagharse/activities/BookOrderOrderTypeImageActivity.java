package in.oriange.joinstagharse.activities;

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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonArray;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

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
import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.models.MasterModel;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.MultipartUtility;
import in.oriange.joinstagharse.utilities.Utilities;

import static in.oriange.joinstagharse.utilities.Utilities.changeStatusBar;
import static in.oriange.joinstagharse.utilities.Utilities.setPaddingForView;

public class BookOrderOrderTypeImageActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_images)
    RecyclerView rvImages;
    @BindView(R.id.btn_add_image)
    Button btnAddImage;
    @BindView(R.id.btn_save)
    MaterialButton btnSave;

    private Context context;
    private ProgressDialog pd;
    private String businessOwnerId, businessOwnerAddress;

    private LocalBroadcastManager localBroadcastManager;
    private ArrayList<MasterModel> imageList;

    private Uri photoURI;
    private File orderFileFolder;
    private int latestPosition;
    private final int CAMERA_REQUEST = 100, GALLERY_REQUEST = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_order_type_image);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = BookOrderOrderTypeImageActivity.this;

        pd = new ProgressDialog(context, R.style.CustomDialogTheme);
        pd.setMessage("Please wait ...");
        pd.setCancelable(false);
        changeStatusBar(context, getWindow());

        rvImages.setLayoutManager(new GridLayoutManager(context, 3));
        imageList = new ArrayList<>();

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

    }

    private void setDefault() {
        businessOwnerId = getIntent().getStringExtra("businessOwnerId");
        businessOwnerAddress = getIntent().getStringExtra("businessOwnerAddress");

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("BookOrderOrderTypeImageActivity");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void setEventHandler() {
        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageList.add(new MasterModel("", ""));
                rvImages.setAdapter(new ImagesAdapter());
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
            }
        });
    }

    private void submitData() {
        boolean atleaseOnePhotoIsAvailable = false;
        JsonArray imageJsonArray = new JsonArray();

        for (int i = 0; i < imageList.size(); i++) {
            if (!imageList.get(i).getName().equals("")) {
                atleaseOnePhotoIsAvailable = true;
                break;
            }
        }

        if (!atleaseOnePhotoIsAvailable) {
            Utilities.showMessage("Please pick atleast one image", context, 2);
            return;
        }


        for (int i = 0; i < imageList.size(); i++) {
            if (!imageList.get(i).getName().equals("")) {
                imageJsonArray.add(imageList.get(i).getName());
            }
        }

//        startActivity(new Intent(context, BookOrderPurchaseTypeSelectionActivity.class)
//                .putExtra("businessOwnerId", businessOwnerId)
//                .putExtra("orderType", "2")
//                .putExtra("orderText", "")
//                .putExtra("orderImageArray", imageJsonArray.toString()));

        startActivity(new Intent(context, BookOrderSelectDeliveryTypeActivity.class)
                .putExtra("businessOwnerId", businessOwnerId)
                .putExtra("businessOwnerAddress", businessOwnerAddress)
                .putExtra("isHomeDeliveryAvailable", getIntent().getStringExtra("isHomeDeliveryAvailable"))
                .putExtra("isPickUpAvailable", getIntent().getStringExtra("isPickUpAvailable"))
                .putExtra("orderType", "2")
                .putExtra("orderText", "")
                .putExtra("orderImageArray", imageJsonArray.toString()));

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
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(BookOrderOrderTypeImageActivity.this);
            }

            if (requestCode == CAMERA_REQUEST) {
                CropImage.activity(photoURI).setGuidelines(CropImageView.Guidelines.ON).start(BookOrderOrderTypeImageActivity.this);
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

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.icon_backarrow_black);
        toolbar.setNavigationOnClickListener(view -> finish());
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
