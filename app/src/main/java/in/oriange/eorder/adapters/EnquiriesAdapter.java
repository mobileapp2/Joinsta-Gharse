package in.oriange.eorder.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.List;

import in.oriange.eorder.R;
import in.oriange.eorder.activities.EnquiriesActivity;
import in.oriange.eorder.models.EnquiriesListModel;
import in.oriange.eorder.utilities.APICall;
import in.oriange.eorder.utilities.ApplicationConstants;
import in.oriange.eorder.utilities.UserSessionManager;
import in.oriange.eorder.utilities.Utilities;

import static android.Manifest.permission.CALL_PHONE;
import static in.oriange.eorder.utilities.Utilities.changeDateFormat;
import static in.oriange.eorder.utilities.Utilities.provideCallPremission;

public class EnquiriesAdapter extends RecyclerView.Adapter<EnquiriesAdapter.MyViewHolder> {

    private List<EnquiriesListModel.ResultBean> resultArrayList;
    private Context context;
    private UserSessionManager session;
    private String userId;

    public EnquiriesAdapter(Context context, List<EnquiriesListModel.ResultBean> resultArrayList) {
        this.context = context;
        this.resultArrayList = resultArrayList;
        session = new UserSessionManager(context);

        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);

            userId = json.getString("userid");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_enquiry, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int pos) {
        final int position = holder.getAdapterPosition();
        final EnquiriesListModel.ResultBean enquiryDetails = resultArrayList.get(position);
        PrettyTime p = new PrettyTime();

        if (enquiryDetails.getCategory_type_id().equalsIgnoreCase("1")) {
            holder.tv_record_name.setText(enquiryDetails.getBusiness_name());
        } else if (enquiryDetails.getCategory_type_id().equalsIgnoreCase("2")) {
            holder.tv_record_name.setText(enquiryDetails.getOrganization_name());
        } else if (enquiryDetails.getCategory_type_id().equalsIgnoreCase("3")) {
            holder.tv_record_name.setText(enquiryDetails.getProfession_name());
        }

        holder.tv_subject.setText(enquiryDetails.getSubject());
        holder.tv_message.setText(enquiryDetails.getMessage());

        if (enquiryDetails.getCommunication_mode().equals("mobile")) {
            holder.tv_createdby.setText(enquiryDetails.getName() + " (" + enquiryDetails.getMobile() + ")");
        } else if (enquiryDetails.getCommunication_mode().equals("email")) {
            holder.tv_createdby.setText(enquiryDetails.getName() + " (" + enquiryDetails.getEmail() + ")");
        }

        holder.tv_time.setText(changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd-MM-yyyy HH:mm", enquiryDetails.getCreated_at()));

//        try {
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
//            holder.tv_createdby.setText(p.format(formatter.parse(enquiryDetails.getCreated_at())) + " | By - " + enquiryDetails.getName());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        if (enquiryDetails.getIs_attended().equals("1")) {
            holder.sw_attend.setChecked(true);
        } else {
            holder.sw_attend.setChecked(false);
        }

        if (enquiryDetails.getCommunication_mode().equals("mobile")) {
            holder.ib_call.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_call));
        } else if (enquiryDetails.getCommunication_mode().equals("email")) {
            holder.ib_call.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_email));
        }

        holder.ib_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enquiryDetails.getCommunication_mode().equals("mobile")) {

                    if (ActivityCompat.checkSelfPermission(context, CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        provideCallPremission(context);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                        builder.setMessage("Are you sure you want to make a call?");
                        builder.setTitle("Alert");
                        builder.setIcon(R.drawable.icon_call);
                        builder.setCancelable(false);
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                context.startActivity(new Intent(Intent.ACTION_CALL,
                                        Uri.parse("tel:" + enquiryDetails.getMobile())));
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
                    }

                } else if (enquiryDetails.getCommunication_mode().equals("email")) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", enquiryDetails.getEmail(), null));
                    context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
                }

            }
        });

        holder.cv_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.ll_buttons.getVisibility() == View.GONE) {
                    holder.ll_buttons.setVisibility(View.VISIBLE);
                } else if (holder.ll_buttons.getVisibility() == View.VISIBLE) {
                    holder.ll_buttons.setVisibility(View.GONE);
                }

            }
        });

        holder.sw_attend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String is_attended = "0";

                if (holder.sw_attend.isChecked()) {
                    is_attended = "1";
                }

                if (Utilities.isNetworkAvailable(context)) {
                    new AttendEnquiry().execute(enquiryDetails.getId(), is_attended);
                } else {
                    Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                }
            }
        });

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
                builder.setMessage("Are you sure you want to delete this enquiry?");
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.icon_alertred);
                builder.setCancelable(false);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Utilities.isNetworkAvailable(context)) {
                            new DeleteEnquiry().execute(enquiryDetails.getId());
                        } else {
                            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
                        }
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
            }
        });

        holder.btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder sb = new StringBuilder();

                if (enquiryDetails.getCategory_type_id().equalsIgnoreCase("1")) {
                    sb.append(enquiryDetails.getBusiness_name() + "\n");
                } else if (enquiryDetails.getCategory_type_id().equalsIgnoreCase("2")) {
                    sb.append(enquiryDetails.getOrganization_name() + "\n");
                } else if (enquiryDetails.getCategory_type_id().equalsIgnoreCase("3")) {
                    sb.append(enquiryDetails.getProfession_name() + "\n");
                }

                sb.append(enquiryDetails.getSubject() + " - " + enquiryDetails.getMessage() + "\n");

                sb.append("By - " + enquiryDetails.getName() + "\n");

                if (enquiryDetails.getCommunication_mode().equals("mobile")) {
                    sb.append("Mobile - " + enquiryDetails.getMobile() + "\n");
                } else if (enquiryDetails.getCommunication_mode().equals("email")) {
                    sb.append("Email - " + enquiryDetails.getEmail() + "\n");
                }

                String details = sb.toString() + "\n" + "Joinsta Gharse\n" + "Click Here - " + ApplicationConstants.JOINSTA_PLAYSTORELINK;

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, details);
                context.startActivity(Intent.createChooser(sharingIntent, "Choose from following"));

            }
        });
    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_mainlayout;
        private ImageButton ib_call;
        private LinearLayout ll_buttons;
        private TextView tv_record_name, tv_subject, tv_message, tv_createdby, tv_time;
        private Switch sw_attend;
        private Button btn_delete, btn_share;

        public MyViewHolder(View view) {
            super(view);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
            ib_call = view.findViewById(R.id.ib_call);
            tv_record_name = view.findViewById(R.id.tv_record_name);
            tv_subject = view.findViewById(R.id.tv_subject);
            tv_message = view.findViewById(R.id.tv_message);
            tv_createdby = view.findViewById(R.id.tv_createdby);
            tv_time = view.findViewById(R.id.tv_time);
            ll_buttons = view.findViewById(R.id.ll_buttons);
            sw_attend = view.findViewById(R.id.sw_attend);
            btn_delete = view.findViewById(R.id.btn_delete);
            btn_share = view.findViewById(R.id.btn_share);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private class DeleteEnquiry extends AsyncTask<String, Void, String> {

        ProgressDialog pd;

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
            obj.addProperty("type", "deleteenquiry");
            obj.addProperty("id", params[0]);
            res = APICall.JSONAPICall(ApplicationConstants.ENQUIRYAPI, obj.toString());
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
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("EnquiriesActivity"));
                        Utilities.showMessage("Enquiry deleted successfully", context, 1);
                    } else {

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class AttendEnquiry extends AsyncTask<String, Void, String> {

        ProgressDialog pd;

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
            obj.addProperty("type", "attendedenquiry");
            obj.addProperty("id", params[0]);
            obj.addProperty("is_attended", params[1]);
            res = APICall.JSONAPICall(ApplicationConstants.ENQUIRYAPI, obj.toString());
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

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
