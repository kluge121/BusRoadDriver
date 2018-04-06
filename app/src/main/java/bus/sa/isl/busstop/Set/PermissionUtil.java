package bus.sa.isl.busstop.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.ActivityCompat;


/**
 * Created by alstn on 2017-08-22.
 */

public class PermissionUtil {

    public static boolean checkPermission(Activity activity, String permission) {
        int permissionResult = ActivityCompat.checkSelfPermission(activity, permission);
        return permissionResult == PackageManager.PERMISSION_GRANTED;
    }
    public static boolean verifyPermission(int[] grantresult){
        if(grantresult.length<1){
            return false;
        }
        for (int result : grantresult){
            if(result != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }




}
