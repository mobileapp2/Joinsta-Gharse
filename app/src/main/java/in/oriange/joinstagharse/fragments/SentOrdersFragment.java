package in.oriange.joinstagharse.fragments;

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

import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.adapters.BookOrderMyOrdersAdapter;
import in.oriange.joinstagharse.models.BookOrderGetMyOrdersModel;
import in.oriange.joinstagharse.models.MasterModel;
import in.oriange.joinstagharse.utilities.APICall;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.UserSessionManager;
import in.oriange.joinstagharse.utilities.Utilities;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class SentOrdersFragment extends Fragment {

    private Context context;
    private UserSessionManager session;

    private EditText edt_search;
    private ImageButton imb_filter;
    private TextView tv_filter_count;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rv_orders;
    private SpinKitView progressBar;
    private LinearLayout ll_nopreview;

    private List<MasterModel> orderStatusList;
    private List<BookOrderGetMyOrdersModel.ResultBean> orderList;
    private BookOrderMyOrdersAdapter bookOrderMyOrdersAdapter;
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

        bookOrderMyOrdersAdapter = new BookOrderMyOrdersAdapter(context, orderList);
        rv_orders.setAdapter(bookOrderMyOrdersAdapter);
        // status = 'IN CART' - 1,'PLACED'-2,'ACCEPTED'-3,'Ready to Deliver'-4,'DELIVERED'-5,'BILLED'-6,'CANCEL'-7

        orderStatusList.add(new MasterModel("In Cart", "1", false));
        orderStatusList.add(new MasterModel("Placed", "2", false));
        orderStatusList.add(new MasterModel("Accepted", "3", false));
        orderStatusList.add(new MasterModel("Ready to Deliver", "4", false));
        orderStatusList.add(new MasterModel("Delivered", "5", false));
        orderStatusList.add(new MasterModel("Billed", "6", false));
        orderStatusList.add(new MasterModel("Cancelled/Rejected", "7", false));
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
                setUpRecyclerView(orderList, query.toString(), orderStatusList);
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
            int selectedTypeCount = 0;

            for (MasterModel orderStatus : orderStatusList)
                if (orderStatus.isChecked()) {
                    selectedTypeCount = selectedTypeCount + 1;
                }

            if (selectedTypeCount == 0) {
                tv_filter_count.setVisibility(View.GONE);
            } else {
                tv_filter_count.setVisibility(View.VISIBLE);
                tv_filter_count.setText(String.valueOf(selectedTypeCount));
            }

            setUpRecyclerView(orderList, edt_search.getText().toString().trim(), orderStatusList);
        });

        builder.setNegativeButton("Clear Filter", (dialog, which) -> {
            for (int i = 0; i < orderStatusList.size(); i++) {
                orderStatusList.get(i).setChecked(false);
            }

            tv_filter_count.setVisibility(View.GONE);
            setUpRecyclerView(orderList, edt_search.getText().toString().trim(), orderStatusList);
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
            String type = "";
            try {
                if (!result.equals("")) {
                    BookOrderGetMyOrdersModel pojoDetails = new Gson().fromJson(result, BookOrderGetMyOrdersModel.class);
                    type = pojoDetails.getType();

                    if (type.equalsIgnoreCase("success")) {
                        orderList = pojoDetails.getResult();

                        if (orderList.size() != 0) {
                            setUpRecyclerView(orderList, edt_search.getText().toString().trim(), orderStatusList);
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

    private void setUpRecyclerView(List<BookOrderGetMyOrdersModel.ResultBean> orderList, String searchTerm,
                                   List<MasterModel> orderStatusList) {
        if (orderList.size() == 0)
            return;

        List<BookOrderGetMyOrdersModel.ResultBean> filteredOrderList = new ArrayList<>(orderList);

        boolean isOrderFilterApplied = false;

        for (MasterModel orderStatus : orderStatusList)
            if (orderStatus.isChecked()) {
                isOrderFilterApplied = true;
                break;
            }

        if (isOrderFilterApplied) {
            List<BookOrderGetMyOrdersModel.ResultBean> orderOfSelectedOrderStatusList = new ArrayList<>();
            for (MasterModel orderStatus : orderStatusList) {
                if (orderStatus.isChecked()) {
                    for (BookOrderGetMyOrdersModel.ResultBean orderDetails : filteredOrderList)
                        if (orderStatus.getId().equals(orderDetails.getStatus_details().get(orderDetails.getStatus_details().size() - 1).getStatus()))
                            orderOfSelectedOrderStatusList.add(orderDetails);
                }
            }
            filteredOrderList.clear();
            filteredOrderList.addAll(orderOfSelectedOrderStatusList);
        }

        if (!searchTerm.equals("")) {
            ArrayList<BookOrderGetMyOrdersModel.ResultBean> ordersSearchedList = new ArrayList<>();
            for (BookOrderGetMyOrdersModel.ResultBean orderDetails : filteredOrderList) {

                String orderToBeSearched = orderDetails.getOrder_id().toLowerCase() +
                        orderDetails.getOwner_business_code().toLowerCase() +
                        orderDetails.getOwner_business_name().toLowerCase() +
                        orderDetails.getCustomer_name().toLowerCase();

                if (orderToBeSearched.contains(searchTerm.toLowerCase())) {
                    ordersSearchedList.add(orderDetails);
                }
            }

            filteredOrderList.clear();
            filteredOrderList.addAll(ordersSearchedList);
        }

        if (!bookOrderMyOrdersAdapter.selectedOrderId.equals("0")) {
            BookOrderGetMyOrdersModel.ResultBean orderDetails = null;
            for (BookOrderGetMyOrdersModel.ResultBean resultBean : orderList)
                if (resultBean.getId().equals(bookOrderMyOrdersAdapter.selectedOrderId)) {
                    orderDetails = resultBean;
                    break;
                }

            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("ViewBookOrderMyOrderActivity").putExtra("orderDetails", orderDetails));
        }

        if (filteredOrderList.size() > 0) {
            bookOrderMyOrdersAdapter.refreshList(filteredOrderList);
            ll_nopreview.setVisibility(View.GONE);
            rv_orders.setVisibility(View.VISIBLE);
        } else {
            rv_orders.setVisibility(View.GONE);
            ll_nopreview.setVisibility(View.VISIBLE);
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
