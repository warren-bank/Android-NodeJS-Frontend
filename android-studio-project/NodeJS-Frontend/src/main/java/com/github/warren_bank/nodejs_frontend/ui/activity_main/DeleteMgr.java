package com.github.warren_bank.nodejs_frontend.ui.activity_main;

import com.github.warren_bank.nodejs_frontend.R;
import com.github.warren_bank.nodejs_frontend.data_model.Preferences;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public final class DeleteMgr {

  public static boolean onOptionsItemSelected(Activity activity, int menuItemId) {
    switch(menuItemId) {
      case R.id.menu_delete: {
        initiateDataDelete(activity);
        return true;
      }
    }
    return false;
  }

  private static void initiateDataDelete(Activity activity) {
    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    builder.setTitle(activity.getString(R.string.activity_main_menu_delete));
    builder.setMessage(activity.getString(R.string.warning_delete));
    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        Preferences.resetNodeJsApps(activity);
        activity.recreate();
      }
    });
    builder.show();
  }

}
