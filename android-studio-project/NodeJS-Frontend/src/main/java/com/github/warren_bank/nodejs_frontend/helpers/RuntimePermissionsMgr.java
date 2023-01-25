package com.github.warren_bank.nodejs_frontend.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class RuntimePermissionsMgr {

  public interface OnRuntimePermissionsListener {
    String[] getMandatoryPermissions();
    void onRequestCancelled();
    void onPermissionsGranted();
    void onPermissionsDenied(String[] permissions);
    void onPermissionsDeniedWithoutPrompt(String[] permissions);
  }

  private static final int REQUEST_CODE_PERMISSIONS = 1;

  private static long TIMESTAMP_PREVIOUS_DENIAL = 0;

  private static ArrayList<String> getMandatoryPermissions(OnRuntimePermissionsListener listener) {
    String[] permissions = listener.getMandatoryPermissions();

    return ((permissions == null) || (permissions.length == 0))
      ? null
      : convertStringArrayToArrayList(permissions)
    ;
  }

  public static boolean isEnabled(Activity activity) {
    boolean result = true;

    if (Build.VERSION.SDK_INT < 23)
      return result;

    final String[] dangerousPermissions = getAllDangerousRequestedPermissions(activity);
    if ((dangerousPermissions != null) && (dangerousPermissions.length > 0)) {
      result = false;
      activity.requestPermissions(dangerousPermissions, REQUEST_CODE_PERMISSIONS);
    }

    if ((Build.VERSION.SDK_INT >= 30) && !Environment.isExternalStorageManager()) {
      result = false;
      Uri uri = Uri.parse("package:" + activity.getPackageName());
      Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
      activity.startActivityForResult(intent, REQUEST_CODE_PERMISSIONS);
    }

    return result;
  }

  public static void onRequestPermissionsResult(OnRuntimePermissionsListener listener, int requestCode, String[] permissions, int[] grantResults) {
    if (requestCode != REQUEST_CODE_PERMISSIONS)
      return;

    if (grantResults.length == 0) {
      if (permissions.length == 0) {
        // no "dangerous" permissions are needed
        listener.onPermissionsGranted();
      }
      else {
        // request was cancelled
        listener.onRequestCancelled();
      }
    }
    else {
      ArrayList<String> mandatoryPermissions = getMandatoryPermissions(listener);
      ArrayList<String> deniedPermissions    = new ArrayList<>();

      for (int i=0; i < grantResults.length; i++) {
        if (
          (grantResults[i] != PackageManager.PERMISSION_GRANTED) &&
          mandatoryPermissions.contains(permissions[i])
        ) {
          // a mandatory permission is not granted
          deniedPermissions.add(permissions[i]);
        }
      }

      if (deniedPermissions.isEmpty()) {
        listener.onPermissionsGranted();
      }
      else {
        long prev_timestamp = TIMESTAMP_PREVIOUS_DENIAL;
        TIMESTAMP_PREVIOUS_DENIAL = (new Date()).getTime();

        // use 250 ms threshold to distinguish whether the denial is automatic;
        // as the result of the user having chosen to "Deny" and "Never ask again"
        if ((TIMESTAMP_PREVIOUS_DENIAL - prev_timestamp) < 250) {
          listener.onPermissionsDeniedWithoutPrompt(
            deniedPermissions.toArray(new String[deniedPermissions.size()])
          );
        }
        else {
          listener.onPermissionsDenied(
            deniedPermissions.toArray(new String[deniedPermissions.size()])
          );
        }
      }
    }
  }

  public static void onActivityResult(OnRuntimePermissionsListener listener, int requestCode, int resultCode, Intent data) {
    if (requestCode != REQUEST_CODE_PERMISSIONS)
      return;

    if (resultCode == Activity.RESULT_OK) {
      listener.onPermissionsGranted();
    }
    else {
      listener.onPermissionsDenied(/* permissions= */ null);
    }
  }

  // =============================================================================================
  // required helpers

  public static ArrayList<String> convertStringArrayToArrayList(String[] in) {
    ArrayList<String> out = new ArrayList<String>();
    Collections.addAll(out, in);
    return out;
  }

  public static String[] getAllRequestedPermissions(Context context) {
    try {
      PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
      return (info.requestedPermissions.length == 0) ? null : info.requestedPermissions;
    }
    catch(Exception e) {
      return null;
    }
  }

  public static String[] getAllDangerousRequestedPermissions(Context context) {
    String[] allPermissions = getAllRequestedPermissions(context);

    if (allPermissions == null)
      return null;

    ArrayList<String> dangerousPermissions = new ArrayList<String>();
    PackageManager pm = context.getPackageManager();
    PermissionInfo info;

    for (String permission : allPermissions) {
      try {
        info = pm.getPermissionInfo(permission, 0);
        switch (info.protectionLevel & PermissionInfo.PROTECTION_MASK_BASE) {
          case PermissionInfo.PROTECTION_DANGEROUS:
            dangerousPermissions.add(permission);
            break;
        }
      }
      catch (PackageManager.NameNotFoundException e) {}
      catch (Exception e) {}
    }

    return dangerousPermissions.isEmpty()
      ? null
      : dangerousPermissions.toArray(new String[dangerousPermissions.size()])
    ;
  }

  // =============================================================================================
  // misc helpers

  public static boolean hasMandatoryPermissions(Context context, OnRuntimePermissionsListener listener) {
    ArrayList<String> mandatoryPermissions = getMandatoryPermissions(listener);

    if (mandatoryPermissions == null)
      return true;

    for (String permission : mandatoryPermissions) {
      if (!hasPermission(context, permission))
        return false;
    }
    return true;
  }

  public static boolean hasPermission(Context context, String permission) {
    return (context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
  }

}
