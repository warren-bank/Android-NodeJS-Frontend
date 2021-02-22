package com.github.warren_bank.nodejs_frontend.ui.activity_main.tabs;

import com.github.warren_bank.nodejs_frontend.R;
import com.github.warren_bank.nodejs_frontend.data_model.NodeJsApp;
import com.github.warren_bank.nodejs_frontend.helpers.NodeJsAppRunner;
import com.github.warren_bank.nodejs_frontend.helpers.UI;
import com.github.warren_bank.nodejs_frontend.services.exec.IRemoteService;
import com.github.warren_bank.nodejs_frontend.services.exec.IRemoteServiceCallback;
import com.github.warren_bank.nodejs_frontend.services.exec.RemoteService;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;

public class ExecTabFragment extends AbstractTabFragment {

  private Activity mActivity;

  private void showSnackbar(String message) {
    if (mActivity != null) {
      View view = mActivity.getWindow().findViewById(android.R.id.content);
      UI.getSnackbar(view, message, /* maxLines= */ 6).show();
    }
  }

  protected IRemoteServiceCallback mCallback = new IRemoteServiceCallback.Stub() {
    public void onNodeComplete(String appTitle) {
      unbindRemoteService();
      showExecutionCompleteNotification(appTitle);
    }
  };

  protected void showExecutionCompleteNotification(String appTitle) {
    // show a snackbar after execution of app completes
    if (mActivity != null) {
      mActivity.runOnUiThread(new Runnable() {
        @Override
        public void run() {
          String message = mActivity.getString(R.string.app_exec_complete, appTitle);
          showSnackbar(message);
        }
      });
    }
  }

  protected IRemoteService mService;
  protected boolean        mIsBound;
  protected NodeJsApp      mPendingNodeJsApp;

  private ServiceConnection mConnection = new ServiceConnection() {

    public void onServiceConnected(ComponentName className, IBinder service) {
      mService = IRemoteService.Stub.asInterface(service);
      mIsBound = true;

      try {
        if (mPendingNodeJsApp == null) {
          mService.die();
        }
        else {
          mService.registerCallback(mCallback);

          String title               = mPendingNodeJsApp.toString();
          String serializedNodeJsApp = NodeJsApp.serialize(mPendingNodeJsApp);

          mService.startNodeWithArguments(title, serializedNodeJsApp);
        }        
      }
      catch(RemoteException e) {
        // the service has crashed; disconnect will occur next..
      }
      catch(Exception e) {}
    }

    public void onServiceDisconnected(ComponentName className) {
      mService = null;
      mIsBound = false;
    }

  };

  private void bindRemoteService(NodeJsApp app) {
    if (app == null) return;

    if (mIsBound) {
      if (mActivity != null) {
        String message = mActivity.getString(R.string.notification_process_busy, app.toString(), mPendingNodeJsApp.toString());
        showSnackbar(message);
      }
      return;
    }

    mIsBound          = true;
    mPendingNodeJsApp = app;

    Context context = getContext();
    Intent  intent  = new Intent(context, RemoteService.class);

    context.bindService(intent, mConnection, (Context.BIND_AUTO_CREATE | Context.BIND_NOT_FOREGROUND));
  }

  protected void unbindRemoteService() {
    if (mPendingNodeJsApp == null) return;

    mIsBound          = false;
    mPendingNodeJsApp = null;

    Context context = getContext();

    context.unbindService(mConnection);
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
      this.mActivity = (Activity) context;
  }

  @Override
  public void onDetach() {
    super.onDetach();

    this.mActivity = null;

    if (mIsBound && (mService != null)) {
      try {
        mService.die();
      }
      catch(RemoteException e) {}
      catch(Exception e) {}
    }
  }

  // ---------------------------------------------------------------------------------------------
  // Abstract Methods:
  // ---------------------------------------------------------------------------------------------

  protected void startService(NodeJsApp listItem) {
  }

  protected void stopService(NodeJsApp listItem) {
  }

  protected void runApplication(NodeJsApp listItem) {
    bindRemoteService(listItem);
  }
}
