package com.github.warren_bank.nodejs_frontend.helpers;

import com.github.warren_bank.nodejs_frontend.R;

import android.app.ActivityManager;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public final class ProcessManager {

  public static ArrayList<String> getRunningServiceIds(Context context) {
    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    List<ActivityManager.RunningAppProcessInfo> allProcesses = am.getRunningAppProcesses();

    String filterPrefix = context.getResources().getString(R.string.process_filter_prefix);
    int    filterLength = filterPrefix.length();

    ArrayList<String> serviceIds = new ArrayList<String>();
    String processName;
    String serviceId;
    for (int i = 0; i < allProcesses.size(); i++) {
      ActivityManager.RunningAppProcessInfo process = allProcesses.get(i);
      processName = process.processName;
      if (processName.startsWith(filterPrefix)) {
        serviceId = processName.substring(filterLength);
        serviceIds.add(serviceId);
      }
    }

    return serviceIds;
  }

}
