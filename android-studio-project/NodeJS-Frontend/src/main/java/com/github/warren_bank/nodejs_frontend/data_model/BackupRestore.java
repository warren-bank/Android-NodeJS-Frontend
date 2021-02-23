package com.github.warren_bank.nodejs_frontend.data_model;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;

public final class BackupRestore {

  public static boolean exportBackup(Context context, String json_filepath) {
    try {
      ArrayList<NodeJsApp> exec_apps_list = Preferences.getNodeJsApps(context, false);
      ArrayList<NodeJsApp> fork_apps_list = Preferences.getNodeJsApps(context, true);

      // exclude inactive apps
      exec_apps_list = NodeJsApp.filterActive(exec_apps_list);
      fork_apps_list = NodeJsApp.filterActive(fork_apps_list);

      ArrayList<NodeJsApp> combined_list = new ArrayList<NodeJsApp>();
      combined_list.addAll(exec_apps_list);
      combined_list.addAll(fork_apps_list);

      String json = NodeJsApp.toJson(combined_list);
      exec_apps_list = null;
      fork_apps_list = null;
      combined_list  = null;

      PrintStream out = new PrintStream(new FileOutputStream(json_filepath));
      out.print(json);
      out.flush();
      out.close();

      return true;
    }
    catch(Exception e) {
      return false;
    }
  }

  public static boolean importBackup(Context context, String json_filepath) {
    try {
      BufferedReader in  = new BufferedReader(new FileReader(json_filepath));
      StringBuilder json = new StringBuilder();
      String line;

      while ((line = in.readLine()) != null)
        json.append(line).append("\n");

      in.close();
      ArrayList<NodeJsApp> combined_list = NodeJsApp.fromJson(json.toString());
      json = null;

      if ((combined_list == null) || combined_list.isEmpty())
        return false;

      ArrayList<NodeJsApp> exec_apps_list = new ArrayList<NodeJsApp>();
      ArrayList<NodeJsApp> fork_apps_list = new ArrayList<NodeJsApp>();

      NodeJsApp app;
      for (int i=0; i < combined_list.size(); i++) {
        app = combined_list.get(i);

        if (!app.isActive())
          continue;

        if (app.isDaemon())
          fork_apps_list.add(app);
        else
          exec_apps_list.add(app);
      }

      if (exec_apps_list.size() > 0) {
        ArrayList<NodeJsApp> current_exec_apps_list = Preferences.getNodeJsApps(context, false);
        current_exec_apps_list.addAll(exec_apps_list);
        Preferences.setNodeJsApps(context, false, current_exec_apps_list);
      }

      if (fork_apps_list.size() > 0) {
        ArrayList<NodeJsApp> current_fork_apps_list = Preferences.getNodeJsApps(context, true);

        // finite size list; can only update inactive items
        for (int i=0; (i < current_fork_apps_list.size()) && (fork_apps_list.size() > 0); i++) {
          app = current_fork_apps_list.get(i);

          if (app.isActive())
            continue;

          app.add(
            fork_apps_list.remove(0)
          );
        }

        Preferences.setNodeJsApps(context, true, current_fork_apps_list);

        // silently ignore:
        // if (fork_apps_list.size() > 0) {}
      }

      exec_apps_list = null;
      fork_apps_list = null;
      combined_list  = null;

      return true;
    }
    catch(Exception e) {
      return false;
    }
  }

}
