package com.github.warren_bank.nodejs_frontend.ui.activity_main.tabs;

import com.github.warren_bank.nodejs_frontend.R;
import com.github.warren_bank.nodejs_frontend.data_model.NodeJsApp;
import com.github.warren_bank.nodejs_frontend.helpers.ProcessMgr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class ForkTabFragment extends AbstractTabFragment {

  // ---------------------------------------------------------------------------------------------
  // Constructor:
  // ---------------------------------------------------------------------------------------------

  public ForkTabFragment() {
    super(true);
  }

  // ---------------------------------------------------------------------------------------------
  // Lifecycle Events:
  // ---------------------------------------------------------------------------------------------

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    getRunningServiceIds();
  }

  // ---------------------------------------------------------------------------------------------
  // Manage list of running daemons:
  // ---------------------------------------------------------------------------------------------

  private ArrayList<String> runningServiceIds;

  private void getRunningServiceIds() {
    runningServiceIds = ProcessMgr.getRunningServiceIds(getContext());
  }

  private void addRunningServiceId(String id) {
    if (!isServiceRunning(id))
      runningServiceIds.add(id);
  }

  private void addRunningServiceId(NodeJsApp listItem) {
    addRunningServiceId(listItem.getId());
  }

  private void removeRunningServiceId(String id) {
    int index;
    while ((index = runningServiceIds.indexOf(id)) >= 0) {
      runningServiceIds.remove(index);
    }
  }

  private void removeRunningServiceId(NodeJsApp listItem) {
    removeRunningServiceId(listItem.getId());
  }

  private boolean isServiceRunning(String id) {
    return runningServiceIds.contains(id);
  }

  private boolean isServiceRunning(NodeJsApp listItem) {
    return isServiceRunning(listItem.getId());
  }

  // ---------------------------------------------------------------------------------------------
  // Row Colors:
  // ---------------------------------------------------------------------------------------------

  @Override
  public int getColorOnRowClear(NodeJsApp listItem) {
    int resId = isServiceRunning(listItem)
      ? R.color.fragmentListItemOnDaemonRunning
      : R.color.fragmentListItemOnRowClear
    ;

    return getResources().getColor(resId);
  }

  // ---------------------------------------------------------------------------------------------
  // Abstract Methods:
  // ---------------------------------------------------------------------------------------------

  protected void startService(NodeJsApp listItem) {
    Intent intent = getServiceIntent(listItem);

    if (intent == null)
      return;

    getContext().startService(intent);

    addRunningServiceId(listItem);
    updateRowBackgroundColorClear(listItem);
  }

  protected void stopService(NodeJsApp listItem) {
    Intent intent = getServiceIntent(listItem);

    if (intent == null)
      return;

    getContext().stopService(intent);

    removeRunningServiceId(listItem);
    updateRowBackgroundColorClear(listItem);
  }

  protected void runApplication(NodeJsApp listItem) {
  }

  private Class getServiceClass(NodeJsApp listItem) {
    try {
      String id        = listItem.getId();
      String className = getResources().getString(R.string.service_package_prefix) + id;

      return Class.forName(className);
    }
    catch(Exception e) {
      return null;
    }
  }

  private Intent getServiceIntent(NodeJsApp listItem) {
    Class serviceClass = getServiceClass(listItem);

    return (serviceClass == null)
      ? null
      : new Intent(getContext(), serviceClass);
  }
}
