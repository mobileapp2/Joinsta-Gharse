package in.oriange.joinstagharse.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.view.ViewAnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.stfalcon.chatkit.messages.MessageInput;
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
import in.oriange.joinstagharse.adapters.ChatAdapter;
import in.oriange.joinstagharse.models.ChatModel;
import in.oriange.joinstagharse.utilities.APICall;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.MultipartUtility;
import in.oriange.joinstagharse.utilities.UserSessionManager;
import in.oriange.joinstagharse.utilities.Utilities;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_title)
    AppCompatEditText toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_chat)
    RecyclerView rvChat;
    @BindView(R.id.edt_chat)
    EditText edtChat;
    @BindView(R.id.ib_attachment)
    ImageButton ibAttachment;
    @BindView(R.id.ib_send)
    ImageButton ibSend;
    @BindView(R.id.progressBar)
    SpinKitView progressBar;
    @BindView(R.id.cv_camera)
    CardView cvCamera;
    @BindView(R.id.cv_gallery)
    CardView cvGallery;
    @BindView(R.id.cv_document)
    CardView cvDocument;
    @BindView(R.id.cv_revel_attachments)
    CardView cvRevelAttachments;
    @BindView(R.id.input)
    MessageInput input;

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;
    private String userId, orderId, name, sendTo;
    boolean hidden = true;
    private File photoFileFolder;
    private Uri photoURI;
    private final int DOCUMENT_REQUEST = 100, CAMERA_REQUEST = 200, GALLERY_REQUEST = 300;
    private LocalBroadcastManager localBroadcastManager;
    public static boolean isChatActivityRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();

        isChatActivityRunning = true;
    }

    private void init() {
        context = ChatActivity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);
        Utilities.changeStatusBar(context, getWindow());
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setReverseLayout(true);
        rvChat.setLayoutManager(linearLayoutManager);
        cvRevelAttachments.setBackground(getResources().getDrawable(R.drawable.button_focusfilled_white));

        photoFileFolder = new File(Environment.getExternalStorageDirectory() + "/Joinsta Gharse/" + "Chat");
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
        orderId = getIntent().getStringExtra("orderId");
        name = getIntent().getStringExtra("name");
        sendTo = getIntent().getStringExtra("sendTo");

        if (Utilities.isNetworkAvailable(context)) {
            progressBar.setVisibility(View.VISIBLE);
            rvChat.setVisibility(View.GONE);
            new GetChat().execute();
        } else
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);

        cvRevelAttachments.setVisibility(View.INVISIBLE);

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("ChatActivity");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void setEventHandler() {
        ibAttachment.setOnClickListener(v -> animateAttachmentLayout());

        cvCamera.setOnClickListener(v -> {
            if (!Utilities.isNetworkAvailable(context)) {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                return;
            }

            File file = new File(photoFileFolder, "doc_image.png");
            photoURI = Uri.fromFile(file);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(intent, CAMERA_REQUEST);
            animateAttachmentLayout();
        });

        cvGallery.setOnClickListener(v -> {
            if (!Utilities.isNetworkAvailable(context)) {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                return;
            }

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_REQUEST);
            animateAttachmentLayout();
        });

        cvDocument.setOnClickListener(v -> {
            if (!Utilities.isNetworkAvailable(context)) {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                return;
            }

            Intent intent = new Intent(context, NormalFilePickActivity.class);
            intent.putExtra(Constant.MAX_NUMBER, 1);
            intent.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf"});
            startActivityForResult(intent, DOCUMENT_REQUEST);
            animateAttachmentLayout();
        });

        ibSend.setOnClickListener(v -> {
            if (edtChat.getText().toString().trim().isEmpty())
                return;

            JsonObject mainObj = new JsonObject();
            mainObj.addProperty("type", "addChat");
            mainObj.addProperty("order_id", orderId);
            mainObj.addProperty("chat_text", edtChat.getText().toString().trim());
            mainObj.addProperty("chat_image", "");
            mainObj.addProperty("chat_doc_type", "0");
            mainObj.addProperty("sent_by", userId);
            mainObj.addProperty("sent_to", sendTo);

            if (Utilities.isNetworkAvailable(context))
                new AddChat().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
            else
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 3);
        });

        input.setAttachmentsListener(this::animateAttachmentLayout);

        input.setInputListener(input -> {
            JsonObject mainObj = new JsonObject();
            mainObj.addProperty("type", "addChat");
            mainObj.addProperty("order_id", orderId);
            mainObj.addProperty("chat_text", input.toString());
            mainObj.addProperty("chat_image", "");
            mainObj.addProperty("chat_doc_type", "0");
            mainObj.addProperty("sent_by", userId);
            mainObj.addProperty("sent_to", sendTo);

            if (Utilities.isNetworkAvailable(context))
                new AddChat().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
            else
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 3);
            return true;
        });
    }

    private void animateAttachmentLayout() {
        int cx = (cvRevelAttachments.getLeft() /*+ cvRevelAttachments.getRight()*/);
        int cy = cvRevelAttachments.getBottom();

        int radius = Math.max(cvRevelAttachments.getWidth(), cvRevelAttachments.getHeight());

        if (hidden) {
            Animator anim = ViewAnimationUtils.createCircularReveal(cvRevelAttachments, cx, cy, 0, radius);
            cvRevelAttachments.setVisibility(View.VISIBLE);
            anim.start();
            hidden = false;

        } else {
            Animator anim = ViewAnimationUtils.createCircularReveal(cvRevelAttachments, cx, cy, radius, 0);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    cvRevelAttachments.setVisibility(View.GONE);
                    hidden = true;
                }
            });
            anim.start();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST) {
                Uri imageUri = data.getData();
                CropImage.activity(imageUri).setActivityMenuIconColor(getResources().getColor(R.color.black)).setGuidelines(CropImageView.Guidelines.ON).start(ChatActivity.this);
            }

            if (requestCode == CAMERA_REQUEST) {
                CropImage.activity(photoURI).setActivityMenuIconColor(getResources().getColor(R.color.black)).setGuidelines(CropImageView.Guidelines.ON).start(ChatActivity.this);
            }

            if (requestCode == DOCUMENT_REQUEST) {
                ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                new UploadImageAndDocument().execute(list.get(0).getPath(), "2");
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
                + "Chat/" + "uplimg.png";

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
        new UploadImageAndDocument().execute(photoFileToUpload.getPath(), "1");
    }

    private class GetChat extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getChat");
            obj.addProperty("order_id", orderId);
            res = APICall.JSONAPICall(ApplicationConstants.CHATAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    ChatModel pojoDetails = new Gson().fromJson(result, ChatModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        List<ChatModel.ResultBean> chatList = pojoDetails.getResult();
                        if (chatList.size() != 0) {
                            rvChat.setVisibility(View.VISIBLE);
                            rvChat.setAdapter(new ChatAdapter(context, chatList));
                            updateReadCount(chatList);
                        } else {
                            rvChat.setVisibility(View.GONE);
                        }
                    } else {
                        rvChat.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                rvChat.setVisibility(View.GONE);
            }
        }
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
            TYPE = params[1];
            StringBuilder res = new StringBuilder();
            try {
                MultipartUtility multipart = new MultipartUtility(ApplicationConstants.FILEUPLOADAPI, "UTF-8");

                multipart.addFormField("request_type", "uploadChatImage");
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
            String type = "";
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    if (type.equalsIgnoreCase("success")) {
                        JSONObject jsonObject = mainObj.getJSONObject("result");
                        showAttachmentDialog(jsonObject.getString("name"), jsonObject.getString("document_url"), TYPE);
                    } else {
                        Utilities.showMessage("Image upload failed", context, 3);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showAttachmentDialog(String name, String documentUrl, String type) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_layout_chat_attachment, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialogBuilder.setView(promptView);

        final ImageView imv_incoming_image = promptView.findViewById(R.id.imv_incoming_image);
        final LinearLayout ll_incoming_doc = promptView.findViewById(R.id.ll_incoming_doc);
        final TextView tv_incoming_doc_name = promptView.findViewById(R.id.tv_incoming_doc_name);
        final EditText edt_message = promptView.findViewById(R.id.edt_message);
        final MaterialButton btn_save = promptView.findViewById(R.id.btn_save);

        if (type.equals("1")) {
            Glide.with(context)
                    .load(documentUrl)
                    .into(imv_incoming_image);
            ll_incoming_doc.setVisibility(View.GONE);
        } else if (type.equals("2")) {
            tv_incoming_doc_name.setText(name);
            imv_incoming_image.setVisibility(View.GONE);
        }

        final AlertDialog alertD = alertDialogBuilder.create();

        btn_save.setOnClickListener(v -> {
            JsonObject mainObj = new JsonObject();

            mainObj.addProperty("type", "addChat");
            mainObj.addProperty("order_id", orderId);
            mainObj.addProperty("chat_text", edt_message.getText().toString().trim());
            mainObj.addProperty("chat_image", name);
            mainObj.addProperty("chat_doc_type", type);
            mainObj.addProperty("sent_by", userId);
            mainObj.addProperty("sent_to", sendTo);

            if (Utilities.isNetworkAvailable(context))
                new AddChat().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
            else
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 3);
            alertD.dismiss();
        });

        alertD.show();
    }

    private class AddChat extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
//            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            res = APICall.JSONAPICall(ApplicationConstants.CHATAPI, params[0]);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
//            pd.dismiss();
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    ChatModel pojoDetails = new Gson().fromJson(result, ChatModel.class);
                    type = pojoDetails.getType();
                    edtChat.setText("");
                    if (type.equalsIgnoreCase("success")) {
                        List<ChatModel.ResultBean> chatList = pojoDetails.getResult();
                        if (chatList.size() != 0) {
                            rvChat.setVisibility(View.VISIBLE);
                            rvChat.setAdapter(new ChatAdapter(context, chatList));
                            updateReadCount(chatList);
                        } else {
                            rvChat.setVisibility(View.GONE);
                        }
                    } else {
                        rvChat.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                rvChat.setVisibility(View.GONE);
            }
        }
    }

    private void updateReadCount(List<ChatModel.ResultBean> chatList) {
        JsonArray unreadChatIds = new JsonArray();

        for (ChatModel.ResultBean resultBean : chatList) {
            if (!resultBean.getSend_by().equals(userId))
                if (resultBean.getIs_read().equals("0"))
                    unreadChatIds.add(resultBean.getId());
        }

        if (unreadChatIds.size() != 0) {
            JsonObject mainObj = new JsonObject();
            mainObj.addProperty("type", "updateChatRead");
            mainObj.addProperty("is_read", "1");
            mainObj.add("id", unreadChatIds);

            if (Utilities.isNetworkAvailable(context))
                new UpdateChatRead().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
            else
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 3);
        }
    }

    private class UpdateChatRead extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            res = APICall.JSONAPICall(ApplicationConstants.CHATAPI, params[0]);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type = "", message = "";
            try {
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    if (type.equalsIgnoreCase("success")) {
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("ReceivedOrdersFragment"));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("SentOrdersFragment"));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("BookOrderBusinessOwnerOrdersActivity"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        toolbarTitle.setText(name);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.icon_backarrow_black);
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Utilities.isNetworkAvailable(context)) {
                new GetChat().execute();
            } else
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
        isChatActivityRunning = false;
    }
}