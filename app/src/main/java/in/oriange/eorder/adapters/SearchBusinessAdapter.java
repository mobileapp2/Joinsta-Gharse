package in.oriange.eorder.adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;
import in.oriange.eorder.R;
import in.oriange.eorder.activities.AddCustomerActivity;
import in.oriange.eorder.activities.AddVendorActivity;
import in.oriange.eorder.activities.BookOrderProductsListActivity;
import in.oriange.eorder.activities.OffersForParticularRecordActivity;
import in.oriange.eorder.activities.ViewSearchBizDetailsActivity;
import in.oriange.eorder.models.SearchDetailsModel;
import in.oriange.eorder.utilities.APICall;
import in.oriange.eorder.utilities.ApplicationConstants;
import in.oriange.eorder.utilities.UserSessionManager;
import in.oriange.eorder.utilities.Utilities;

public class SearchBusinessAdapter extends RecyclerView.Adapter<SearchBusinessAdapter.MyViewHolder> {

    private Context context;
    private String type;            //  1 = from search  // 2 = from favorite  // 3 = from home
    private String userId, name, mobile, countryCode;
    private List<SearchDetailsModel.ResultBean.BusinessesBean> resultArrayList;
    private JSONArray emailJsonArray;

    public SearchBusinessAdapter(Context context, List<SearchDetailsModel.ResultBean.BusinessesBean> resultArrayList, String type) {
        this.context = context;
        this.resultArrayList = resultArrayList;
        this.type = type;
        emailJsonArray = new JSONArray();

        try {
            UserSessionManager session = new UserSessionManager(context);
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);
            userId = json.getString("userid");
            name = json.getString("first_name");
            mobile = json.getString("mobile");
            if (!json.getString("email").equals("null"))
                emailJsonArray = new JSONArray(json.getString("email"));
            try {
                countryCode = json.getString("country_code");
            } catch (Exception e) {
                countryCode = "91";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_search, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int pos) {
        final int position = holder.getAdapterPosition();
        final SearchDetailsModel.ResultBean.BusinessesBean searchDetails = resultArrayList.get(position);

        holder.tv_business_name.setText(searchDetails.getBusiness_code() + " - " + searchDetails.getBusiness_name());
        holder.tv_address.setText(searchDetails.getAddressCityPincode());


        List<String> tagsList = searchDetails.getSubTypesTagsList("1");
        List<String> topFiveTagsList = new ArrayList<>();

        try {
            for (int i = 0; i < 5; i++)
                topFiveTagsList.add(tagsList.get(i));
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.container_tags.setTags(topFiveTagsList);


        if (!searchDetails.getOffer_count().equals("0")) {
            if (Integer.parseInt(searchDetails.getOffer_count()) == 1) {
                holder.tv_offers.setText("1 Offer");
            } else {
                holder.tv_offers.setText(searchDetails.getOffer_count() + " Offers");
            }
        } else {
            holder.tv_offers.setText("No Offers");
        }

        if (!searchDetails.getDeliveryType().equals(""))
            holder.tv_delivery_type.setText(searchDetails.getDeliveryType());
        else
            holder.tv_delivery_type.setVisibility(View.GONE);

        holder.cv_mainlayout.setOnClickListener(v ->
                context.startActivity(new Intent(context, ViewSearchBizDetailsActivity.class)
                        .putExtra("searchDetails", searchDetails)
                        .putExtra("type", type))
        );

        holder.ll_add.setOnClickListener(v -> {
            showContextMenu(v, searchDetails);
        });

        holder.ll_enquire.setOnClickListener(v -> {
            if (searchDetails.getIs_enquiry_available().equals("1"))
                openEnquiryDialog(searchDetails);
            else
                Utilities.showMessage("Enquire not available", context, 2);
        });

        holder.ll_offer.setOnClickListener(v -> {
            if (searchDetails.getOffer_count().equals("1"))
                context.startActivity(new Intent(context, OffersForParticularRecordActivity.class)
                        .putExtra("recordId", searchDetails.getId())
                        .putExtra("categoryType", "1"));
            else
                Utilities.showMessage("Offers not available", context, 2);
        });

        holder.ll_book_order.setOnClickListener(v -> {
            if (searchDetails.getCan_book_order().equals("1"))
//                context.startActivity(new Intent(context, BookOrderOrderTypeSelectActivity.class)
//                        .putExtra("businessOwnerId", searchDetails.getId()));
                context.startActivity(new Intent(context, BookOrderProductsListActivity.class)
                        .putExtra("businessOwnerId", searchDetails.getId()));
            else
                Utilities.showMessage("Book order facility not available", context, 2);
        });
    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_mainlayout;
        private TagContainerLayout container_tags;
        private TextView tv_business_name, tv_address, tv_offers, tv_delivery_type;
        private LinearLayout ll_add, ll_enquire, ll_offer, ll_book_order;

        public MyViewHolder(View view) {
            super(view);
            cv_mainlayout = view.findViewById(R.id.cv_mainlayout);
            container_tags = view.findViewById(R.id.container_tags);
            tv_business_name = view.findViewById(R.id.tv_business_name);
            tv_address = view.findViewById(R.id.tv_address);
            tv_delivery_type = view.findViewById(R.id.tv_delivery_type);
            tv_offers = view.findViewById(R.id.tv_offers);
            ll_add = view.findViewById(R.id.ll_add);
            ll_enquire = view.findViewById(R.id.ll_enquire);
            ll_offer = view.findViewById(R.id.ll_offer);
            ll_book_order = view.findViewById(R.id.ll_book_order);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @SuppressLint("RestrictedApi")
    private void showContextMenu(View view, SearchDetailsModel.ResultBean.BusinessesBean searchDetails) {
        PopupMenu popup = new PopupMenu(context, view);
        popup.inflate(R.menu.menu_vendor_cudtomer);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_vendor:
                    context.startActivity(new Intent(context, AddVendorActivity.class)
                            .putExtra("businessCode", searchDetails.getBusiness_code())
                            .putExtra("businessName", searchDetails.getBusiness_name())
                            .putExtra("name", "")
                            .putExtra("mobile", ""));
                    break;
                case R.id.menu_customer:
                    context.startActivity(new Intent(context, AddCustomerActivity.class)
                            .putExtra("businessCode", searchDetails.getBusiness_code())
                            .putExtra("businessName", searchDetails.getBusiness_name())
                            .putExtra("name", "")
                            .putExtra("mobile", ""));
                    break;
            }
            return false;
        });

        MenuPopupHelper menuHelper = new MenuPopupHelper(context, (MenuBuilder) popup.getMenu(), view);
        menuHelper.setForceShowIcon(true);
        menuHelper.show();
    }

    private void openEnquiryDialog(SearchDetailsModel.ResultBean.BusinessesBean searchDetails) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_layout_enquiry, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        alertDialogBuilder.setView(promptView);

        final EditText edt_name = promptView.findViewById(R.id.edt_name);
        final RadioGroup rg_communicationmode = promptView.findViewById(R.id.rg_communicationmode);
        final RadioButton rb_mobile = promptView.findViewById(R.id.rb_mobile);
        final RadioButton rb_email = promptView.findViewById(R.id.rb_email);
        final EditText edt_mobile = promptView.findViewById(R.id.edt_mobile);
        final LinearLayout ll_mobile = promptView.findViewById(R.id.ll_mobile);
        final LinearLayout ll_email = promptView.findViewById(R.id.ll_email);
        final EditText edt_email = promptView.findViewById(R.id.edt_email);
        final EditText edt_subject = promptView.findViewById(R.id.edt_subject);
        final EditText edt_details = promptView.findViewById(R.id.edt_details);
        final Button btn_save = promptView.findViewById(R.id.btn_save);
        final ImageButton ib_close = promptView.findViewById(R.id.ib_close);

        edt_name.setText(name);

        final AlertDialog alertD = alertDialogBuilder.create();

        rb_mobile.setOnClickListener(v -> {
            ll_mobile.setVisibility(View.VISIBLE);
            ll_email.setVisibility(View.GONE);

            edt_email.setText("");
            edt_mobile.setText("+" + countryCode + mobile);
        });

        rb_email.setOnClickListener(v -> {

            ll_mobile.setVisibility(View.GONE);
            edt_mobile.setText("");

            if (emailJsonArray == null) {
                Utilities.showAlertDialogNormal(context, "Please add a primary email and verify it from Basic Information");
                rg_communicationmode.clearCheck();
                return;
            }

            if (emailJsonArray.length() == 0) {
                Utilities.showAlertDialogNormal(context, "Please add a primary email and verify it from Basic Information");
                rg_communicationmode.clearCheck();
                return;

            }

            try {
                for (int i = 0; i < emailJsonArray.length(); i++) {
                    JSONObject emailObj = emailJsonArray.getJSONObject(0);
                    if (emailObj.getString("is_primary").equals("1")) {
                        if (emailObj.getString("email_verification").equals("1")) {
                            if (emailObj.getString("email").equals("")) {
                                Utilities.showAlertDialogNormal(context, "Please update your primary email from Basic Information");
                                rg_communicationmode.clearCheck();
                                return;
                            }

                            edt_email.setText(emailObj.getString("email"));
                        } else {
                            Utilities.showAlertDialogNormal(context, "Please verify your primary email from Basic Information");
                            rg_communicationmode.clearCheck();
                            return;
                        }
                    } else {
                        Utilities.showAlertDialogNormal(context, "Please set a primary email from Basic Information");
                        rg_communicationmode.clearCheck();
                        return;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Utilities.showAlertDialogNormal(context, "We have made some changes related to user email, please kindly logout and login again to refresh your session");
                rg_communicationmode.clearCheck();
                return;

            }

            ll_email.setVisibility(View.VISIBLE);

        });

        btn_save.setOnClickListener(v -> {

            if (edt_name.getText().toString().trim().isEmpty()) {
                edt_name.setError("Please enter name");
                edt_name.requestFocus();
                return;
            }

            if (rb_mobile.isChecked()) {
                if (edt_mobile.getText().toString().trim().isEmpty()) {
                    edt_mobile.setError("Please enter valid mobile");
                    edt_mobile.requestFocus();
                    return;
                }
                edt_email.setText("");
            } else if (rb_email.isChecked()) {
                if (!Utilities.isEmailValid(edt_email.getText().toString().trim())) {
                    edt_email.setError("Please enter valid email");
                    edt_email.requestFocus();
                    return;
                }
                edt_mobile.setText("");
            } else {
                Utilities.showMessage("Please select communication mode", context, 2);
                return;
            }

            if (edt_subject.getText().toString().trim().isEmpty()) {
                edt_subject.setError("Please enter subject");
                edt_subject.requestFocus();
                return;
            }

            if (edt_details.getText().toString().trim().isEmpty()) {
                edt_details.setError("Please enter details");
                edt_details.requestFocus();
                return;
            }

            if (Utilities.isNetworkAvailable(context)) {
                alertD.dismiss();
                new SendEnquiryDetails().execute(
                        userId,
                        edt_name.getText().toString().trim(),
                        edt_mobile.getText().toString().trim(),
                        edt_email.getText().toString().trim(),
                        edt_subject.getText().toString().trim(),
                        edt_details.getText().toString().trim(),
                        "1",
                        searchDetails.getId()

                );
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }
        });

        ib_close.setOnClickListener(v -> alertD.dismiss());

        alertD.show();
    }

    private class SendEnquiryDetails extends AsyncTask<String, Void, String> {

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
            obj.addProperty("type", "createenquiry");
            obj.addProperty("userid", params[0]);
            obj.addProperty("name", params[1]);
            obj.addProperty("mobile", params[2]);
            obj.addProperty("email", params[3]);
            obj.addProperty("subject", params[4]);
            obj.addProperty("message", params[5]);
            obj.addProperty("category_type_id", params[6]);
            obj.addProperty("record_id", params[7]);
            obj.addProperty("msg_type", "notification");
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
                        Utilities.showMessage("Enquiry sent successfully", context, 1);
                    } else {

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
