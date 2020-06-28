package in.oriange.joinstagharse.utilities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import in.oriange.joinstagharse.R;

public class RuntimePermissions {

    public static String[] CAMERA_AND_STORAGE_PERMISSION = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    public static String[] STORAGE_PERMISSION = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    public static String[] CALL_PHONE_PERMISSION = {Manifest.permission.CALL_PHONE};
    public static String[] LOCATION_PERMISSION = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    public static String[] READ_CONTACTS_PERMISSION = {Manifest.permission.READ_CONTACTS};

    public static final int CAMERA_AND_STORAGE_PERMISSION_REQUEST = 100;
    public static final int STORAGE_PERMISSION_REQUEST = 200;
    public static final int CALL_PHONE_PERMISSION_REQUEST = 300;
    public static final int LOCATION_PERMISSION_REQUEST = 400;
    public static final int READ_CONTACTS_PERMISSION_REQUEST = 500;

    public static String cameraStoragePermissionMsg = "You have permanently denied permission to access camera or storage, please provide required permissions to explore complete functionality of app";
    public static String storagePermissionMsg = "You have permanently denied permission to access storage, please provide required permissions to explore complete functionality of app";
    public static String callPermissionMsg = "You have permanently denied permission to call, please provide required permissions to explore complete functionality of app";
    public static String locationPermissionMsg = "You have permanently denied permission to access device location, please provide required permissions to explore complete functionality of app";
    public static String readContactsPermissionMsg = "You have permanently denied permission to read contacts, please provide required permissions to explore complete functionality of app";

    public static boolean isCameraStoragePermissionGiven(Context context, String[] permissions) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, permissions, CAMERA_AND_STORAGE_PERMISSION_REQUEST);
            return false;
        } else {
            return true;
        }
    }

    public static boolean isStoragePermissionGiven(Context context, String[] permissions) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, permissions, STORAGE_PERMISSION_REQUEST);
            return false;
        } else {
            return true;
        }
    }

    public static boolean isCallPermissionGiven(Context context, String[] permissions) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, permissions, CALL_PHONE_PERMISSION_REQUEST);
            return false;
        } else {
            return true;
        }
    }

    public static boolean isLocationPermissionGiven(Context context, String[] permissions) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, permissions, LOCATION_PERMISSION_REQUEST);
            return false;
        } else {
            return true;
        }
    }

    public static boolean isReadContactsPermissionGiven(Context context, String[] permissions) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, permissions, READ_CONTACTS_PERMISSION_REQUEST);
            return false;
        } else {
            return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void manualPermission(Context context, String message, String[] permissions, int requestCode) {
        for (String permission : permissions) {
            if (!neverAskAgainSelected((Activity) context, permission)) {
                return;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        builder.setTitle("Permission");
        builder.setMessage(message);
        builder.setPositiveButton("Go to settings", (dialog, id) -> {
            dialog.dismiss();
            context.startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.getPackageName(), null)));
        });
        builder.setNegativeButton("cancel", (dialog, which) -> {

        });
        builder.create();
        AlertDialog alertD = builder.create();
        alertD.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean neverAskAgainSelected(final Activity activity, final String permission) {
        final boolean prevShouldShowStatus = getRatinaleDisplayStatus(activity, permission);
        final boolean currShouldShowStatus = activity.shouldShowRequestPermissionRationale(permission);
        return prevShouldShowStatus == currShouldShowStatus;
    }

    public static boolean getRatinaleDisplayStatus(final Context context, final String permission) {
        SharedPreferences genPrefs = context.getSharedPreferences("GENERIC_PREFERENCES", Context.MODE_PRIVATE);
        return genPrefs.getBoolean(permission, false);
    }
}
