package in.oriange.joinstagharse.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.models.BusinessCategoryMasterModel;
import in.oriange.joinstagharse.models.BusinessDocumentModel;
import in.oriange.joinstagharse.utilities.APICall;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.MultipartUtility;
import in.oriange.joinstagharse.utilities.UserSessionManager;
import in.oriange.joinstagharse.utilities.Utilities;

import static android.app.Activity.RESULT_OK;
import static in.oriange.joinstagharse.utilities.PermissionUtil.doesAppNeedPermissions;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.STORAGE_PERMISSION;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.isStoragePermissionGiven;

public class AddBusinessDocumentFragment extends Fragment {

    @BindView(R.id.rv_documents)
    RecyclerView rvDocuments;
    @BindView(R.id.btn_add_document)
    Button btnAddDocument;
    @BindView(R.id.sv_scroll)
    NestedScrollView svScroll;

    private Context context;
    private ProgressDialog pd;
    private UserSessionManager session;
    private String userId;
    private List<BusinessDocumentModel> businessDocumentList;
    private List<BusinessCategoryMasterModel.ResultBean> documentMasterList;

    private DocumentAdapter documentAdapter;
    private int documentPosition;
    private final int DOCUMENT_REQUEST = 100;

    private LocalBroadcastManager localBroadcastManager1, localBroadcastManager2;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_business_document, container, false);
        ButterKnife.bind(this, rootView);

        context = getActivity();
        init();
        setDefault();
        getSessionDetails();
        setEventHandler();
        return rootView;
    }

    private void init() {
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        rvDocuments.setLayoutManager(new LinearLayoutManager(context));

        businessDocumentList = new ArrayList<>();
        documentMasterList = new ArrayList<>();

        documentAdapter = new DocumentAdapter();
        rvDocuments.setAdapter(documentAdapter);
    }

    private void setDefault() {
        addDocument();

        localBroadcastManager1 = LocalBroadcastManager.getInstance(context);
        localBroadcastManager2 = LocalBroadcastManager.getInstance(context);

        IntentFilter intentFilter1 = new IntentFilter("AddBusinessDocumentFragment");
        IntentFilter intentFilter2 = new IntentFilter("AddBusinessDocumentFragmentSkip");

        localBroadcastManager1.registerReceiver(broadcastReceiver1, intentFilter1);
        localBroadcastManager2.registerReceiver(broadcastReceiver2, intentFilter2);
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
        btnAddDocument.setOnClickListener(v -> {
            addDocument();
        });
    }

    private void addDocument() {
        businessDocumentList.add(new BusinessDocumentModel("", "", ""));
        documentAdapter.refreshData();
    }

    private void removeDocument(int position) {
        businessDocumentList.remove(position);
        documentAdapter.refreshData();
    }

    private void submitData() {
        JsonArray documentJsonArray = new JsonArray();

        for (BusinessDocumentModel businessDocument : businessDocumentList) {

            if (!businessDocument.getName().equals("") && !businessDocument.getType().equals("")) {
                if (businessDocument.getName().equals("") && !businessDocument.getType().equals("")) {
                    Utilities.showMessage("Please select document file", context, 2);
                    return;
                }
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("doc_type_id", businessDocument.getId());
                jsonObject.addProperty("document", businessDocument.getName());
                documentJsonArray.add(jsonObject);
            }
        }

        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("AddBusinessDocumentActivity")
                .putExtra("documentJsonArray", documentJsonArray.toString()));

    }

    private class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.MyViewHolder> {

        @NonNull
        @Override
        public DocumentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_row_document, parent, false);
            return new DocumentAdapter.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DocumentAdapter.MyViewHolder holder, int pos) {
            int position = holder.getAdapterPosition();
            BusinessDocumentModel businessDetails = businessDocumentList.get(position);

            holder.edt_document_type.setText(businessDetails.getType());
            holder.edt_document_file.setText(businessDetails.getName());

            holder.ib_remove_document.setOnClickListener(v -> {
                removeDocument(position);
            });

            holder.edt_document_type.setOnClickListener(v -> {
                documentPosition = position;
                if (documentMasterList.size() == 0)
                    if (Utilities.isNetworkAvailable(context))
                        new GetDocumentList().execute();
                    else
                        Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                else
                    showBusinessListDialog();
            });

            holder.edt_document_file.setOnClickListener(v -> {
                if (holder.edt_document_type.getText().toString().trim().isEmpty()) {
                    Utilities.showMessage("Please select document type", context, 2);
                    return;
                }

                if (doesAppNeedPermissions()) {
                    if (!isStoragePermissionGiven(context, STORAGE_PERMISSION)) {
                        return;
                    }
                }

                documentPosition = position;
                if (Utilities.isNetworkAvailable(context)) {
                    Intent intent = new Intent(context, NormalFilePickActivity.class);
                    intent.putExtra(Constant.MAX_NUMBER, 1);
                    intent.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf"});
                    startActivityForResult(intent, DOCUMENT_REQUEST);
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }

            });
        }

        @Override
        public int getItemCount() {
            return businessDocumentList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private ImageButton ib_remove_document;
            private TextView edt_document_type, edt_document_file;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                ib_remove_document = itemView.findViewById(R.id.ib_remove_document);
                edt_document_type = itemView.findViewById(R.id.edt_document_type);
                edt_document_file = itemView.findViewById(R.id.edt_document_file);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        private void refreshData() {
            notifyDataSetChanged();
        }
    }

    private class GetDocumentList extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "getBusinessDocumentTypes");
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
                    documentMasterList = new ArrayList<>();
                    BusinessCategoryMasterModel pojoDetails = new Gson().fromJson(result, BusinessCategoryMasterModel.class);
                    type = pojoDetails.getType();
                    message = pojoDetails.getMessage();

                    if (type.equalsIgnoreCase("success")) {
                        documentMasterList = pojoDetails.getResult();
                        if (documentMasterList.size() > 0) {
                            showBusinessListDialog();
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

    private void showBusinessListDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builderSingle.setTitle("Select Document Type");
        builderSingle.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.list_row);

        for (int i = 0; i < documentMasterList.size(); i++) {
            arrayAdapter.add(String.valueOf(documentMasterList.get(i).getType()));
        }

        builderSingle.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
            businessDocumentList.get(documentPosition).setId(documentMasterList.get(which).getId());
            businessDocumentList.get(documentPosition).setType(documentMasterList.get(which).getType());
            documentAdapter.refreshData();
        });
        builderSingle.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == DOCUMENT_REQUEST) {
                ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                new UploadDocument().execute(list.get(0).getPath());
            }
        }
    }

    private class UploadDocument extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "";
            try {
                MultipartUtility multipart = new MultipartUtility(ApplicationConstants.FILEUPLOADAPI, "UTF-8");

                multipart.addFormField("request_type", "uploadFile");
                multipart.addFormField("user_id", userId);
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
            String type = "", message = "";
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {
                        JSONObject jsonObject = mainObj.getJSONObject("result");
                        String imageName = jsonObject.getString("name");
                        businessDocumentList.get(documentPosition).setName(imageName);
                        documentAdapter.notifyDataSetChanged();
                    } else {
                        Utilities.showMessage("Image upload failed", context, 3);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private BroadcastReceiver broadcastReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            submitData();
        }
    };

    private BroadcastReceiver broadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            JsonArray documentJsonArray = new JsonArray();

            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("AddBusinessDocumentActivity")
                    .putExtra("documentJsonArray", documentJsonArray.toString()));
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        localBroadcastManager1.unregisterReceiver(broadcastReceiver1);
        localBroadcastManager2.unregisterReceiver(broadcastReceiver2);
    }
}
