package in.oriange.joinstagharse.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import in.oriange.joinstagharse.R;

import static in.oriange.joinstagharse.utilities.RuntimePermissions.CALL_PHONE_PERMISSION_REQUEST;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.CAMERA_AND_STORAGE_PERMISSION_REQUEST;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.LOCATION_PERMISSION_REQUEST;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.READ_CONTACTS_PERMISSION_REQUEST;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.STORAGE_PERMISSION_REQUEST;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.callPermissionMsg;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.cameraStoragePermissionMsg;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.locationPermissionMsg;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.manualPermission;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.readContactsPermissionMsg;
import static in.oriange.joinstagharse.utilities.RuntimePermissions.storagePermissionMsg;
import static in.oriange.joinstagharse.utilities.Utilities.changeStatusBar;
import static in.oriange.joinstagharse.utilities.Utilities.showCallDialog;

public class ContactUsActivity extends AppCompatActivity {

    private Context context;
    private LinearLayout ll_mobile, ll_whatsapp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        init();
        getSessionDetails();
        setDefault();
        setEventListner();
        setUpToolbar();
    }

    private void init() {
        context = ContactUsActivity.this;
        changeStatusBar(context, getWindow());
        ll_mobile = findViewById(R.id.ll_mobile);
        ll_whatsapp = findViewById(R.id.ll_whatsapp);

    }

    private void getSessionDetails() {

    }

    private void setDefault() {

    }

    private void setEventListner() {
        ll_mobile.setOnClickListener(v -> showCallDialog(context, "+919175326801"));

        ll_whatsapp.setOnClickListener(v -> {
            String phoneno = "917020009889";
            String URL = "https://wa.me/" + phoneno;
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL)));
        });
    }

    private void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.icon_backarrow_black);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_AND_STORAGE_PERMISSION_REQUEST: {
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED)) {
                    manualPermission(context, cameraStoragePermissionMsg, permissions, requestCode);
                }
            }
            break;
            case STORAGE_PERMISSION_REQUEST: {
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    manualPermission(context, storagePermissionMsg, permissions, requestCode);
                }
            }
            break;
            case CALL_PHONE_PERMISSION_REQUEST: {
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    manualPermission(context, callPermissionMsg, permissions, requestCode);
                }
            }
            break;
            case LOCATION_PERMISSION_REQUEST: {
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    manualPermission(context, locationPermissionMsg, permissions, requestCode);
                }
            }
            break;
            case READ_CONTACTS_PERMISSION_REQUEST: {
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    manualPermission(context, readContactsPermissionMsg, permissions, requestCode);
                }
            }
            break;
        }
    }

}
