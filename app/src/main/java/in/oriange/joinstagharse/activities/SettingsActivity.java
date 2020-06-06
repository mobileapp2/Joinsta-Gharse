package in.oriange.joinstagharse.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.airbnb.lottie.LottieAnimationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.utilities.APICall;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.UserSessionManager;
import in.oriange.joinstagharse.utilities.Utilities;

import static in.oriange.joinstagharse.utilities.ApplicationConstants.JOINSTA_PLAYSTORELINK;
import static in.oriange.joinstagharse.utilities.Utilities.changeStatusBar;
import static in.oriange.joinstagharse.utilities.Utilities.getMd5;
import static in.oriange.joinstagharse.utilities.Utilities.hideSoftKeyboard;

public class SettingsActivity extends AppCompatActivity {

    private Context context;
    private ProgressDialog pd;
    private CardView cv_logout, cv_feedback, cv_invite, cv_password, cv_report_issue;
    private UserSessionManager session;
    private String userId, password, mobile, referral_code, country_code, genderId, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        init();
        setDefault();
        getSessionDetails();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = SettingsActivity.this;
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        changeStatusBar(context, getWindow());

        cv_password = findViewById(R.id.cv_password);
        cv_invite = findViewById(R.id.cv_invite);
        cv_feedback = findViewById(R.id.cv_feedback);
        cv_logout = findViewById(R.id.cv_logout);
        cv_report_issue = findViewById(R.id.cv_report_issue);
    }

    private void setDefault() {
    }

    private void getSessionDetails() {
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            userId = json.getString("userid");
            password = json.getString("password");
            mobile = json.getString("mobile");
            referral_code = json.getString("referral_code");
            country_code = json.getString("country_code");
            genderId = json.getString("gender_id");
            name = json.getString("first_name");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setEventHandler() {
        cv_password.setOnClickListener(v -> changePasswordAlert());

        cv_invite.setOnClickListener(v -> {
            String shareMessage;
            String salutation = "";
            if (genderId.equals("1")) {
                salutation = "Mr. ";
            } else if (genderId.equals("2")) {
                salutation = "Ms. ";
            }
            if (!referral_code.trim().equals("")) {
                shareMessage = "Welcome to Joinsta Gharse\n\n" +
                        "Connect with businesses, employees and professionals all over the world to collaborate and grow together.\n" +
                        "Enter referral code of " + salutation + name + " - " + referral_code + "\n" +
                        "Below is the link to download the app.\n" +
                        "Google play store: " + JOINSTA_PLAYSTORELINK + "\n\n" +
                        "Joinsta - Team";
            } else {
                shareMessage = "Welcome to Joinsta Gharse\n\n" +
                        "Connect with businesses, employees and professionals all over the world to collaborate and grow together.\n" +
                        "Below is the link to download the app.\n" +
                        "Google play store: " + JOINSTA_PLAYSTORELINK + "\n\n" +
                        "Joinsta - Team";
            }

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            context.startActivity(Intent.createChooser(sharingIntent, "Choose from following"));
        });

        cv_feedback.setOnClickListener(v -> startActivity(new Intent(context, UserFeedbackActivity.class)));

        cv_report_issue.setOnClickListener(v -> startActivity(new Intent(context, ReportIssueActivity.class)));

        cv_logout.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
            builder.setMessage("Are you sure you want to log out?");
            builder.setTitle("Alert");
            builder.setIcon(R.drawable.icon_alertred);
            builder.setCancelable(false);
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    session.logoutUser();
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertD = builder.create();
            alertD.show();
        });

    }

    private void changePasswordAlert() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_change_password, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
//        alertDialogBuilder.setTitle("Change Password");
        alertDialogBuilder.setView(promptView);

        final EditText edt_oldpassword = promptView.findViewById(R.id.edt_oldpassword);
        final EditText edt_newpassword = promptView.findViewById(R.id.edt_newpassword);
        final Button btn_save = promptView.findViewById(R.id.btn_save);
        final ImageButton ib_close = promptView.findViewById(R.id.ib_close);

        final AlertDialog alertD = alertDialogBuilder.create();

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPwdMdfStr = getMd5(edt_oldpassword.getText().toString().trim());

                if (!oldPwdMdfStr.equals(password)) {
                    edt_oldpassword.setError("Please enter correct old password");
                    edt_oldpassword.requestFocus();
                    return;
                }

                if (edt_newpassword.getText().toString().trim().isEmpty()) {
                    edt_newpassword.setError("Please enter new password");
                    edt_newpassword.requestFocus();
                    return;
                }

                if (Utilities.isNetworkAvailable(context)) {
                    alertD.dismiss();
                    new ChangePassword().execute(
                            edt_oldpassword.getText().toString().trim(),
                            edt_newpassword.getText().toString().trim()
                    );
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }
            }
        });

        ib_close.setOnClickListener(v -> {
            alertD.dismiss();
        });

        alertD.show();

    }

    private class ChangePassword extends AsyncTask<String, Void, String> {

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
            JSONObject obj = new JSONObject();
            try {
                obj.put("type", "changepassword");
                obj.put("userId", userId);
                obj.put("mobile", mobile);
                obj.put("country_code", country_code);
                obj.put("oldpassword", params[0]);
                obj.put("newpasssword", params[1]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            res = APICall.JSONAPICall(ApplicationConstants.USERSAPI, obj.toString());
            return res;
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
                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View promptView = layoutInflater.inflate(R.layout.dialog_layout_success, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        alertDialogBuilder.setView(promptView);

                        LottieAnimationView animation_view = promptView.findViewById(R.id.animation_view);
                        TextView tv_title = promptView.findViewById(R.id.tv_title);
                        Button btn_ok = promptView.findViewById(R.id.btn_ok);

                        animation_view.playAnimation();
                        tv_title.setText("Password changed successfully. Please login again with new password.");
                        alertDialogBuilder.setCancelable(false);
                        final AlertDialog alertD = alertDialogBuilder.create();

                        btn_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertD.dismiss();
                                session.logoutUser();
                            }
                        });

                        alertD.show();
                    } else if (type.equalsIgnoreCase("failure")) {

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

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftKeyboard(SettingsActivity.this);
    }
}
