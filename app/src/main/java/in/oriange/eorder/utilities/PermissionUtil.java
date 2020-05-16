package in.oriange.eorder.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtil {


    public static final int PERMISSION_ALL = 1;

    public static boolean doesAppNeedPermissions() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1;
    }

    public static String[] getPermissions(Context context)
            throws PackageManager.NameNotFoundException {
        PackageInfo info = context.getPackageManager().getPackageInfo(
                context.getPackageName(), PackageManager.GET_PERMISSIONS);

        return info.requestedPermissions;
    }

    public static boolean askPermissions(Activity activity) {
        if (doesAppNeedPermissions()) {
            try {
                String[] permissions = getPermissions(activity);

                if (!checkPermissions(activity, permissions)) {
                    ActivityCompat.requestPermissions(activity, permissions,
                            PERMISSION_ALL);
                    return true;

                } else
                    return false;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else
            return false;
    }

    public static boolean checkPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null &&
                permissions != null) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) !=
                        PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
