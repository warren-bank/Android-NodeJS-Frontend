package com.github.warren_bank.nodejs_frontend.ui.activity_main.tabs;

import com.github.warren_bank.nodejs_frontend.R;
import com.github.warren_bank.nodejs_frontend.data_model.NodeJsApp;
import com.github.warren_bank.nodejs_frontend.helpers.NodeJsAppRunner;

import android.app.Activity;
import android.content.Context;

import com.google.android.material.snackbar.Snackbar;

public class ExecTabFragment extends AbstractTabFragment {

  private Activity activity;

  private class applicationRunner implements Runnable {
    private NodeJsApp app;

    public applicationRunner(NodeJsApp app) {
      this.app = app;
    }

    @Override
    public void run() {
      try {
        NodeJsAppRunner.exec(app);

        // show a snackbar after execution of app completes
        if (activity != null) {
          activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
              String message = activity.getString(R.string.app_exec_complete, app.toString());
              Snackbar.make(activity.getWindow().findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
            }
          });
        }
      }
      catch(Exception e) {
        System.out.print("Exception caught: ");
        System.out.println(e.getMessage());
      }
    }
  }

  // ---------------------------------------------------------------------------------------------
  // Constructor:
  // ---------------------------------------------------------------------------------------------

  public ExecTabFragment() {
    super(false);
  }

  // ---------------------------------------------------------------------------------------------
  // Lifecycle Events:
  // ---------------------------------------------------------------------------------------------

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    if (context instanceof Activity)
      this.activity = (Activity) context;
  }

  // ---------------------------------------------------------------------------------------------
  // Abstract Methods:
  // ---------------------------------------------------------------------------------------------

  protected void startService(NodeJsApp listItem) {
  }

  protected void stopService(NodeJsApp listItem) {
  }

  protected void runApplication(NodeJsApp listItem) {
    new Thread(new applicationRunner(listItem)).start();
  }
}
