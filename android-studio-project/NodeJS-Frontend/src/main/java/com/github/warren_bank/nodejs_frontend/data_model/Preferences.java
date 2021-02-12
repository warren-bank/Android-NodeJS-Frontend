package com.github.warren_bank.nodejs_frontend.data_model;

import com.github.warren_bank.nodejs_frontend.R;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

public final class Preferences {
  private static final String PREFS_FILENAME = "PREFS";
  private static final String PREF_APPS_EXEC = "APPS_EXEC";
  private static final String PREF_APPS_FORK = "APPS_FORK";

  private static String getAppsPrefKey(boolean isDaemon) {
    return isDaemon ? PREF_APPS_FORK : PREF_APPS_EXEC;
  }

  private static SharedPreferences getSharedPreferences(Context context) {
    return context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE);
  }

  private static ArrayList<NodeJsApp> initializeNodeJsApps(Context context, boolean isDaemon) {
    ArrayList<NodeJsApp> listItems = new ArrayList<NodeJsApp>();

    if (isDaemon) {
      int max_nodejs_daemons = context.getResources().getInteger( R.integer.max_nodejs_daemons );
      String formatter = "%0" + String.format("%d", max_nodejs_daemons).length() + "d";
      String id;
      for (int i=1; i <= max_nodejs_daemons; i++) {
        id = String.format(formatter, i);
        listItems.add( new NodeJsApp(id) );
      }
    }

    setNodeJsApps(context, isDaemon, listItems);
    return listItems;
  }

  public static ArrayList<NodeJsApp> getNodeJsApps(Context context, boolean isDaemon) {
    SharedPreferences sharedPreferences = getSharedPreferences(context);
    String PREF_KEY                     = getAppsPrefKey(isDaemon);
    String JSON                         = sharedPreferences.getString(PREF_KEY, null);

    return (JSON == null)
      ? initializeNodeJsApps(context, isDaemon)
      : NodeJsApp.fromJson(JSON)
    ;
  }

  public static void setNodeJsApps(Context context, boolean isDaemon, ArrayList<NodeJsApp> listItems) {
    SharedPreferences sharedPreferences   = getSharedPreferences(context);
    String PREF_KEY                       = getAppsPrefKey(isDaemon);
    String JSON                           = NodeJsApp.toJson(listItems);
    SharedPreferences.Editor prefs_editor = sharedPreferences.edit();
    prefs_editor.putString(PREF_KEY, JSON);
    prefs_editor.commit();
  }
}
