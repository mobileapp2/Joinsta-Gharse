package in.oriange.joinstagharse.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonArray;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.joinstagharse.R;

import static in.oriange.joinstagharse.utilities.Utilities.changeStatusBar;

public class BookOrderOrderTypeTextActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edt_order_text)
    EditText edtOrderText;
    @BindView(R.id.btn_save)
    MaterialButton btnSave;

    private Context context;
    private String businessOwnerId, businessOwnerAddress;

    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_order_type_text);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = BookOrderOrderTypeTextActivity.this;
        changeStatusBar(context, getWindow());
    }

    private void getSessionDetails() {

    }

    private void setDefault() {
        businessOwnerId = getIntent().getStringExtra("businessOwnerId");
        businessOwnerAddress = getIntent().getStringExtra("businessOwnerAddress");

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter("BookOrderOrderTypeTextActivity");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void setEventHandler() {
        btnSave.setOnClickListener(v -> {
            if (edtOrderText.getText().toString().trim().isEmpty()) {
                edtOrderText.setError("Please enter order");
                return;
            }

            startActivity(new Intent(context, BookOrderSelectDeliveryTypeActivity.class)
                    .putExtra("businessOwnerId", businessOwnerId)
                    .putExtra("businessOwnerAddress", businessOwnerAddress)
                    .putExtra("isHomeDeliveryAvailable", getIntent().getStringExtra("isHomeDeliveryAvailable"))
                    .putExtra("isPickUpAvailable", getIntent().getStringExtra("isPickUpAvailable"))
                    .putExtra("orderType", "3")
                    .putExtra("orderText", edtOrderText.getText().toString().trim())
                    .putExtra("orderImageArray", new JsonArray().toString()));
        });

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
