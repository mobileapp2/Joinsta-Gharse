package in.oriange.joinstagharse.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;
import com.hsalf.smilerating.SmileRating;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.regex.Matcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.utilities.APICall;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.UserSessionManager;
import in.oriange.joinstagharse.utilities.Utilities;

import static in.oriange.joinstagharse.utilities.Utilities.changeStatusBar;

public class AddRatingAndReviewActivity extends AppCompatActivity {

    @BindView(R.id.edt_profile_name)
    AppCompatEditText edtProfileName;
    @BindView(R.id.btn_save)
    MaterialButton btnSave;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edt_public_name)
    EditText edtPublicName;
    @BindView(R.id.ib_edit_public_name)
    ImageButton ibEditPublicName;
    @BindView(R.id.edt_title)
    EditText edtTitle;
    @BindView(R.id.edt_review)
    EditText edtReview;
    @BindView(R.id.smile_rating)
    SmileRating smileRating;

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;

    private String userId, recordId, profileName, categoryTypeId;
    private int rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rating_and_review);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = AddRatingAndReviewActivity.this;
        session = new UserSessionManager(context);
        changeStatusBar(context, getWindow());
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);
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
        profileName = getIntent().getStringExtra("profileName");
        categoryTypeId = getIntent().getStringExtra("categoryTypeId");
        rating = getIntent().getIntExtra("rating", 5);

        smileRating.setSelectedSmile(rating - 1);

        if (Utilities.isNetworkAvailable(context))
            new GetPublicName().execute();
        else
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);

//        switch (rating) {
//            case 1:
//                smileRating.setSelectedSmile(BaseRating.TERRIBLE);
//                break;
//            case 2:
//                smileRating.setSelectedSmile(BaseRating.BAD);
//                break;
//            case 3:
//                smileRating.setSelectedSmile(BaseRating.OKAY);
//                break;
//            case 4:
//                smileRating.setSelectedSmile(BaseRating.GOOD);
//                break;
//            case 5:
//                smileRating.setSelectedSmile(BaseRating.GREAT);
//                break;
//        }

    }

    private void setEventHandler() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
            }
        });

        ibEditPublicName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPublicNameDialog();
            }
        });
    }

    private void submitData() {
        if (edtPublicName.getText().toString().trim().isEmpty()) {
            Utilities.showMessage("Please set your public name", context, 2);
            setPublicNameDialog();
            return;
        }

        if (edtTitle.getText().toString().trim().isEmpty()) {
            edtTitle.setError("Please enter title");
            edtTitle.requestFocus();
            return;
        }

        if (edtReview.getText().toString().trim().isEmpty()) {
            edtReview.setError("Please enter description");
            edtReview.requestFocus();
            return;
        }

        JsonObject mainObj = new JsonObject();
        mainObj.addProperty("type", "CreateProfileRating");
        mainObj.addProperty("category_type_id", categoryTypeId);
        mainObj.addProperty("record_id", recordId);
        mainObj.addProperty("rating", String.valueOf(smileRating.getRating()));
        mainObj.addProperty("review_description", edtReview.getText().toString().trim());
        mainObj.addProperty("review_title", edtTitle.getText().toString().trim());
        mainObj.addProperty("user_id", userId);
        mainObj.addProperty("is_active", "1");

        if (Utilities.isNetworkAvailable(context)) {
            new PostRatingAndReview().execute(mainObj.toString().replace("\'", Matcher.quoteReplacement("\\\'")));
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }
    }

    private class PostRatingAndReview extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String res;
            res = APICall.JSONAPICall(ApplicationConstants.RATINGANDREVIEWAPI, params[0]);
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String type, message;
            try {
                pd.dismiss();
                if (!result.equals("")) {
                    JSONObject mainObj = new JSONObject(result);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("RatingAndReviewListActivity"));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("SearchFragment"));

                        switch (categoryTypeId) {
                            case "1":
                                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("ViewSearchBizDetailsActivity"));
                                break;
                        }

                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Your review is posted successfully, thank you!");
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

    private class GetPublicName extends AsyncTask<String, Void, String> {

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
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("type", "getPublicName");
            jsonObject.addProperty("user_id", userId);
            res = APICall.JSONAPICall(ApplicationConstants.RATINGANDREVIEWAPI, jsonObject.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String type, message;
            try {
                pd.dismiss();
                if (!s.equals("")) {
                    JSONObject mainObj = new JSONObject(s);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {
                        Object result = mainObj.get("result");
                        if (result instanceof Integer) {
                            setPublicNameDialog();
                        } else if (result instanceof JSONArray) {
                            JSONArray jsonArray = mainObj.getJSONArray("result");

                            if (jsonArray.length() > 0) {
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                String publicName = jsonObject.getString("public_name");

                                if (!publicName.trim().isEmpty())
                                    edtPublicName.setText(publicName);
                                else
                                    setPublicNameDialog();
                            } else {
                                setPublicNameDialog();
                            }
                        }
                    } else {
                        Utilities.showMessage(message, context, 3);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setPublicNameDialog() {
        final MaterialEditText edt_public_name = new MaterialEditText(context);
        edt_public_name.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        edt_public_name.setHighlightColor(getResources().getColor(R.color.colorPrimary));
        float dpi = context.getResources().getDisplayMetrics().density;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setTitle("Set Public Name");

        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Utilities.isNetworkAvailable(context)) {
                    new SetPublicName().execute(userId, edt_public_name.getText().toString().trim());
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }
            }
        });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog alertD = alertDialogBuilder.create();
        alertD.setView(edt_public_name, (int) (19 * dpi), (int) (5 * dpi), (int) (14 * dpi), (int) (5 * dpi));
        alertD.show();
        alertD.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        edt_public_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    alertD.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    alertD.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }
        });
    }

    private class SetPublicName extends AsyncTask<String, Void, String> {

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
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("type", "updatePublicName");
            jsonObject.addProperty("user_id", params[0]);
            jsonObject.addProperty("public_name", params[1]);
            res = APICall.JSONAPICall(ApplicationConstants.RATINGANDREVIEWAPI, jsonObject.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String type, message;
            try {
                pd.dismiss();
                if (!s.equals("")) {
                    JSONObject mainObj = new JSONObject(s);
                    type = mainObj.getString("type");
                    message = mainObj.getString("message");
                    if (type.equalsIgnoreCase("success")) {
                        Utilities.showMessage("Public name updated successfully", context, 1);
                        if (Utilities.isNetworkAvailable(context))
                            new GetPublicName().execute();
                        else
                            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
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
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        edtProfileName.setText(profileName);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.icon_backarrow_black);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
