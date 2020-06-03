package in.oriange.eorder.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.oriange.eorder.R;
import in.oriange.eorder.adapters.BookOrderMyOrdersAdapter;
import in.oriange.eorder.models.BookOrderGetMyOrdersModel;
import in.oriange.eorder.models.MasterModel;
import in.oriange.eorder.utilities.APICall;
import in.oriange.eorder.utilities.ApplicationConstants;
import in.oriange.eorder.utilities.UserSessionManager;
import in.oriange.eorder.utilities.Utilities;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class SentOrdersFragment extends Fragment {

    private Context context;
    private UserSessionManager session;
    private ProgressDialog pd;

    private EditText edt_search;
    private ImageButton imb_filter;
    private TextView tv_filter_count;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rv_orders;
    private SpinKitView progressBar;
    private LinearLayout ll_nopreview;

    private List<MasterModel> orderStatusList;
    private List<BookOrderGetMyOrdersModel.ResultBean> orderList, mFilteredOrderList;
    private String userId;
    private LocalBroadcastManager localBroadcastManager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sent_orders, container, false);
        context = getActivity();
        init(rootView);
        getSessionDetails();
        setDefault();
        setEventHandler();
        return rootView;
    }

    private void init(View rootView) {
        session = new UserSessionManager(context);
        pd = new ProgressDialog(context, R.style.CustomDialogTheme);

        edt_search = rootView.findViewById(R.id.edt_search);
        imb_filter = rootView.findViewById(R.id.imb_filter);
        tv_filter_count = rootView.findViewById(R.id.tv_filter_count);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        rv_orders = rootView.findViewById(R.id.rv_orders);
        progressBar = rootView.findViewById(R.id.progressBar);
        ll_nopreview = rootView.findViewById(R.id.ll_nopreview);

        rv_orders.setLayoutManager(new LinearLayoutManager(context));

        orderStatusList = new ArrayList<>();
        orderList = new ArrayList<>();
        mFilteredOrderList = new ArrayList<>();

        // status = 'IN CART' - 1,'PLACED'-2,'ACCEPTED'-3,'IN PROGRESS'-4,'DELIVERED'-5,'BILLED'-6,'CANCEL'-7

        orderStatusList.add(new MasterModel("Added in Cart", "1", false));
        orderStatusList.add(new MasterModel("Placed", "2", false));
        orderStatusList.add(new MasterModel("Accepted", "3", false));
        orderStatusList.add(new MasterModel("In Progress", "4", false));
        orderStatusList.add(new MasterModel("Delivered", "5", false));
        orderStatusList.add(new MasterModel("Applicable for Billing", "6", false));
        orderStatusList.add(new MasterModel("Cancelled", "7", false));
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

        if (Utilities.isNetworkAvailable(context)) {
            new GetAllOrders().execute();
        } else {
            Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
        }

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("SentOrdersFragment");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void setEventHandler() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (Utilities.isNetworkAvailable(context)) {
                new GetAllOrders().execute();
            } else {
                swipeRefreshLayout.setRefreshing(false);
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }
        });

        imb_filter.setOnClickListener(v -> showOrderStatusListDialog());

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {

                if (query.toString().isEmpty()) {
                    rv_orders.setAdapter(new BookOrderMyOrdersAdapter(context, mFilteredOrderList));
                    return;
                }

                if (mFilteredOrderList.size() == 0) {
                    rv_orders.setVisibility(View.GONE);
                    return;
                }

                if (!query.toString().equals("")) {
                    ArrayList<BookOrderGetMyOrdersModel.ResultBean> ordersSearchedList = new ArrayList<>();
                    for (BookOrderGetMyOrdersModel.ResultBean orderDetails : mFilteredOrderList) {

                        String orderToBeSearched = orderDetails.getOwner_business_code().toLowerCase() +
                                orderDetails.getOwner_business_name().toLowerCase();

                        if (orderToBeSearched.contains(query.toString().toLowerCase())) {
                            ordersSearchedList.add(orderDetails);
                        }
                    }
                    rv_orders.setAdapter(new BookOrderMyOrdersAdapter(context, ordersSearchedList));
                } else {
                    rv_orders.setAdapter(new BookOrderMyOrdersAdapter(context, mFilteredOrderList));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void showOrderStatusListDialog() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_groups_list, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builder.setView(view);
        builder.setTitle("Select Order Status");
        builder.setCancelable(false);

        RecyclerView rv_groups = view.findViewById(R.id.rv_groups);
        rv_groups.setLayoutManager(new LinearLayoutManager(context));
        rv_groups.setAdapter(new OrderStatusAdapter());

        builder.setPositiveButton("Apply", (dialog, which) -> {
            List<BookOrderGetMyOrdersModel.ResultBean> filteredOrderList = new ArrayList<>();
            int selectedTypeCount = 0;

            for (MasterModel orderStatus : orderStatusList)
                if (orderStatus.isChecked()) {
                    selectedTypeCount = selectedTypeCount + 1;
                    for (BookOrderGetMyOrdersModel.ResultBean orderDetails : orderList)
                        if (orderStatus.getId().equals(orderDetails.getStatus_details().get(orderDetails.getStatus_details().size() - 1).getStatus()))
                            filteredOrderList.add(orderDetails);
                }

            if (selectedTypeCount == 0) {
                mFilteredOrderList = orderList;
                tv_filter_count.setVisibility(View.GONE);
                rv_orders.setAdapter(new BookOrderMyOrdersAdapter(context, orderList));
            } else {
                mFilteredOrderList = filteredOrderList;
                tv_filter_count.setVisibility(View.VISIBLE);
                tv_filter_count.setText(String.valueOf(selectedTypeCount));
                rv_orders.setAdapter(new BookOrderMyOrdersAdapter(context, mFilteredOrderList));
            }
        });

        builder.setNegativeButton("Clear Filter", (dialog, which) -> {
            edt_search.setText("");
            for (int i = 0; i < orderStatusList.size(); i++) {
                orderStatusList.get(i).setChecked(false);
            }

            tv_filter_count.setVisibility(View.GONE);
            rv_orders.setAdapter(new BookOrderMyOrdersAdapter(context, orderList));

        });

        builder.create().show();
    }

    private class OrderStatusAdapter extends RecyclerView.Adapter<OrderStatusAdapter.MyViewHolder> {

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

            holder.cb_select.setText(orderStatusList.get(position).getName());

            if (orderStatusList.get(position).isChecked()) {
                holder.cb_select.setChecked(true);
            }

            holder.cb_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    orderStatusList.get(position).setChecked(isChecked);
                }
            });
        }

        @Override
        public int getItemCount() {
            return orderStatusList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private CheckBox cb_select;

            public MyViewHolder(@NonNull View view) {
                super(view);
                cb_select = view.findViewById(R.id.cb_select);
            }
        }
    }

    private class GetAllOrders extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            ll_nopreview.setVisibility(View.GONE);
            rv_orders.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "[]";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "getAllOrders");
            obj.addProperty("user_id", userId);
            res = APICall.JSONAPICall(ApplicationConstants.BOOKORDERAPI, obj.toString());
            return res.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            rv_orders.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            String type = "", message = "";

            tv_filter_count.setVisibility(View.GONE);
            for (int i = 0; i < orderStatusList.size(); i++) {
                orderStatusList.get(i).setChecked(false);
            }
            edt_search.setText("");

            try {
                if (!result.equals("")) {
                    BookOrderGetMyOrdersModel pojoDetails = new Gson().fromJson(result, BookOrderGetMyOrdersModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        orderList = pojoDetails.getResult();

                        if (orderList.size() != 0) {
                            mFilteredOrderList = orderList;
                            rv_orders.setAdapter(new BookOrderMyOrdersAdapter(context, orderList));
                        } else {
                            ll_nopreview.setVisibility(View.VISIBLE);
                            rv_orders.setVisibility(View.GONE);
                        }
                    } else {
                        ll_nopreview.setVisibility(View.VISIBLE);
                        rv_orders.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ll_nopreview.setVisibility(View.VISIBLE);
                rv_orders.setVisibility(View.GONE);
            }
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Utilities.isNetworkAvailable(context)) {
                new GetAllOrders().execute();
            } else {
                Utilities.showMessage(R.string.msgt_nointernetconnection, context, 2);
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }


}
