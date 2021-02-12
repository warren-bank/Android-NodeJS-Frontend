package com.github.warren_bank.nodejs_frontend.helpers;

import com.github.warren_bank.nodejs_frontend.data_model.NodeJsApp;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public final class NodeJsAppRunner {

  static {
    System.loadLibrary("native-lib");
    System.loadLibrary("node");
  }

  private static native void    saveStandardOutputToFile(String filepath);
  private static native Integer setenv(String name, String value, Integer overwrite);
  private static native Integer startNodeWithArguments(String[] arguments);

  public static void exec(NodeJsApp listItem) {
    String[][] env_vars     = listItem.getEnvironmentVariables();
    String[]   node_options = listItem.getNodeOptions();
    String     js_filepath  = listItem.getJsApplicationFilepath();
    String[]   js_options   = listItem.getJsApplicationOptions();

    ArrayList<String> arguments = new ArrayList<String>();

    if (env_vars != null) {
      for (int i = 0; i < env_vars.length; i++) {
        if (env_vars[i].length == 2) {
          setenv(env_vars[i][0], env_vars[i][1], 1);
        }
      }
    }

    arguments.add("node");

    if (node_options != null) {
      Collections.addAll(arguments, node_options);
    }

    if (!js_filepath.isEmpty()) {
      arguments.add(js_filepath);

      if (js_options != null) {
        Collections.addAll(arguments, js_options);
      }
    }

    if (arguments.size() > 1) {
      startNodeWithArguments(
        arguments.toArray(new String[ arguments.size() ])
      );
    }
  }

  public static File getStandardOutputFile(Context context, String id, boolean mustExist) {
    if (id == null)
      id = "exec";

    File dir = context.getExternalFilesDir(null);

    if (dir == null)
      dir = context.getFilesDir();

    if (!dir.exists())
      dir.mkdir();

    File file = new File(dir, "stdout_" + id + ".txt");

    return (mustExist && !file.exists())
      ? null
      : file;
  }

  public static void saveStandardOutputToFile(File file) {
    if (file == null)
      return;

    String filepath = file.getAbsolutePath();
    saveStandardOutputToFile(filepath);
  }

  public static void saveStandardOutputToFile(Context context, String id) {
    File file = getStandardOutputFile(context, id, false);
    saveStandardOutputToFile(file);
  }
}
