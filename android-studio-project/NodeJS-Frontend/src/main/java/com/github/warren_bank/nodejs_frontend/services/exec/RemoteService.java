package com.github.warren_bank.nodejs_frontend.services.exec;

import com.github.warren_bank.nodejs_frontend.data_model.NodeJsApp;
import com.github.warren_bank.nodejs_frontend.helpers.NodeJsAppRunner;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

public class RemoteService extends Service {

  protected final RemoteCallbackList<IRemoteServiceCallback> mCallbacks = new RemoteCallbackList<IRemoteServiceCallback>();

  private final IRemoteService.Stub mBinder = new IRemoteService.Stub() {

    private boolean isRunning = false;
    private String  appTitle;

    public void registerCallback(IRemoteServiceCallback cb) {
      if (cb != null) mCallbacks.register(cb);
    }

/* -----------------------------------------------------------------------------
 * not needed:
 * -----------------------------------------------------------------------------
    
    public void unregisterCallback(IRemoteServiceCallback cb) {
      if (cb != null) mCallbacks.unregister(cb);
    }

 * -----------------------------------------------------------------------------
 * not needed, and not oneway:
 * -----------------------------------------------------------------------------

    public boolean isNodeRunning() {
      return isRunning;
    }

    public String getAppTitle() {
      return appTitle;
    }

 * -----------------------------------------------------------------------------
 */

    public void startNodeWithArguments(String title, String serializedNodeJsApp) {
      if (isRunning) return;

      if (serializedNodeJsApp == null) {
        killProcess();
        return;
      }

      isRunning = true;
      appTitle  = title;

      new Thread() {
        public void run() {
          try {
            NodeJsApp app = NodeJsApp.deserialize(serializedNodeJsApp);

            if (app == null)
              throw new Exception("Could not deserialize app JSON:" + "\n" + serializedNodeJsApp);

            NodeJsAppRunner.saveStandardOutputToFile(RemoteService.this, null);
            NodeJsAppRunner.exec(app);
          }
          catch(Exception e) {
            System.out.print("Exception caught: ");
            System.out.println(e.getMessage());
          }
          finally {
            // notify caller that Node.js execution is complete
            notifyNodeComplete(appTitle);

            // kill the service
            killProcess();
          }
        }
      }.start();
    }

    public void die() {
      // kill the service
      killProcess();
    }

  };

  protected void notifyNodeComplete(String title) {
    if (title == null) return;

    // Broadcast to all clients the new value.
    final int N = mCallbacks.beginBroadcast();
    for (int i=0; i<N; i++) {
      try {
        mCallbacks.getBroadcastItem(i).onNodeComplete(title);
      }
      catch (RemoteException e) {
        // RemoteCallbackList will remove the dead object
      }
    }
    mCallbacks.finishBroadcast();
  }

  protected void killProcess() {
    mCallbacks.kill();

    stopSelf();
    Process.killProcess(Process.myPid());
  }

  @Override
  public void onCreate() {
    super.onCreate();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return START_NOT_STICKY;
  }

  @Override
  public IBinder onBind(Intent intent) {
    return mBinder;
  }

  @Override
  public boolean onUnbind(Intent intent) {
    killProcess();
    return false;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    killProcess();
  }

}
