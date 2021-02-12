package com.github.warren_bank.nodejs_frontend.helpers;

import com.github.warren_bank.nodejs_frontend.data_model.NodeJsApp;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;

public final class NodeJsAppRunner {

  public static void exec(NodeJsApp listItem) {
  }

  public static File getStandardOutputFile(Context context, String id, boolean mustExist) {
    if (id == null)
      id = "exec";

    File dir = context.getExternalFilesDir(null);

    if (dir == null)
      dir = context.getFilesDir();

    File file = (dir == null)
      ? null
      : new File(dir, "stdout_" + id + ".txt");

    return ((file == null) || (mustExist && !file.exists()))
      ? null
      : file;
  }
}
