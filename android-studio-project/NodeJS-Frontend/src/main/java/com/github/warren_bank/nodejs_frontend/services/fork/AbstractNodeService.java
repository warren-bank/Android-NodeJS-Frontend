package com.github.warren_bank.nodejs_frontend.services.fork;

import com.github.warren_bank.nodejs_frontend.R;
import com.github.warren_bank.nodejs_frontend.data_model.NodeJsApp;
import com.github.warren_bank.nodejs_frontend.data_model.Preferences;
import com.github.warren_bank.nodejs_frontend.helpers.NodeJsAppRunner;
import com.github.warren_bank.nodejs_frontend.helpers.WakeLockMgr;
import com.github.warren_bank.nodejs_frontend.helpers.WifiLockMgr;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Process;
import android.widget.RemoteViews;

import java.util.ArrayList;

public abstract class AbstractNodeService extends Service {

  protected abstract String getId();

  private static final String ACTION_STOP = "STOP";

  private String nodejs_app_name;

  private NodeJsApp getApp() {
    ArrayList<NodeJsApp> nodeJsApps = Preferences.getNodeJsApps(AbstractNodeService.this, true);
    NodeJsApp app = NodeJsApp.findById(nodeJsApps, getId());
    return app;
  }

  private void killProcess() {
    WakeLockMgr.release();
    WifiLockMgr.release();

    stopSelf();
    Process.killProcess(Process.myPid());
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();

    NodeJsApp app = getApp();

    if (app == null) {
      killProcess();
      return;
    }

    nodejs_app_name = app.toString();
    showNotification();

    WakeLockMgr.acquire(AbstractNodeService.this);
    WifiLockMgr.acquire(AbstractNodeService.this);

    new Thread() {
      public void run() {
        try {
          NodeJsAppRunner.saveStandardOutputToFile(AbstractNodeService.this, app.getId());
          NodeJsAppRunner.exec(app);
        }
        catch(Exception e) {
          System.out.print("Exception caught: ");
          System.out.println(e.getMessage());
        }
        finally {
          // kill the service after Node.js exits
          killProcess();
        }
      }
    }.start();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    killProcess();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    onStart(intent, startId);
    return START_STICKY;
  }

  @Override
  public void onStart(Intent intent, int startId) {
    processIntent(intent);
  }

  // -------------------------------------------------------------------------
  // process inbound intents

  private void processIntent(Intent intent) {
    if (intent == null) return;

    String action = intent.getAction();
    if (action == null) return;

    if (action == ACTION_STOP) {
      killProcess();
      return;
    }
  }

  // -------------------------------------------------------------------------
  // foregrounding..

  private String getNotificationChannelId() {
    return getPackageName() + ":process_" + getId();
  }

  private void createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= 26) {
      String channelId       = getNotificationChannelId();
      NotificationManager NM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
      NotificationChannel NC = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_HIGH);

      NC.setDescription(channelId);
      NC.setSound(null, null);
      NM.createNotificationChannel(NC);
    }
  }

  private int getNotificationId() {
    return Integer.parseInt(getId(), 10);
  }

  private void showNotification() {
    Notification notification = getNotification();
    int NOTIFICATION_ID = getNotificationId();

    if (Build.VERSION.SDK_INT >= 5) {
      createNotificationChannel();
      startForeground(NOTIFICATION_ID, notification);
    }
    else {
      NotificationManager NM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
      NM.notify(NOTIFICATION_ID, notification);
    }
  }

  private void hideNotification() {
    if (Build.VERSION.SDK_INT >= 5) {
      stopForeground(true);
    }
    else {
      NotificationManager NM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
      int NOTIFICATION_ID    = getNotificationId();
      NM.cancel(NOTIFICATION_ID);
    }
  }

  private Notification getNotification() {
    Notification notification  = (Build.VERSION.SDK_INT >= 26)
      ? (new Notification.Builder(/* context= */ AbstractNodeService.this, /* channelId= */ getNotificationChannelId())).build()
      :  new Notification()
    ;

    notification.when          = System.currentTimeMillis();
    notification.flags         = 0;
    notification.flags        |= Notification.FLAG_ONGOING_EVENT;
    notification.flags        |= Notification.FLAG_NO_CLEAR;
    notification.icon          = R.mipmap.launcher;
    notification.tickerText    = getString(R.string.app_name) + ": " + nodejs_app_name;
    notification.contentIntent = getPendingIntent_StopService();
    notification.deleteIntent  = getPendingIntent_StopService();

    if (Build.VERSION.SDK_INT >= 16) {
      notification.priority    = Notification.PRIORITY_HIGH;
    }
    else {
      notification.flags      |= Notification.FLAG_HIGH_PRIORITY;
    }

    if (Build.VERSION.SDK_INT >= 21) {
      notification.visibility  = Notification.VISIBILITY_PUBLIC;
    }

    RemoteViews contentView    = new RemoteViews(getPackageName(), R.layout.service_notification);
    contentView.setImageViewResource(R.id.notification_icon, R.mipmap.launcher);
    contentView.setTextViewText(R.id.notification_text_line1, getString(R.string.notification_service_content_line1));
    contentView.setTextViewText(R.id.notification_text_line2, nodejs_app_name);
    contentView.setTextViewText(R.id.notification_text_line3, getString(R.string.notification_service_content_line3));
    notification.contentView   = contentView;

    return notification;
  }

  private PendingIntent getPendingIntent_StopService() {
    Intent intent = new Intent(AbstractNodeService.this, AbstractNodeService.this.getClass());
    intent.setAction(ACTION_STOP);

    return PendingIntent.getService(AbstractNodeService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
  }

}
