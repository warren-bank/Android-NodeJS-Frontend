package com.github.warren_bank.nodejs_frontend.ui.activity_main.tabs;

import com.github.warren_bank.nodejs_frontend.R;
import com.github.warren_bank.nodejs_frontend.data_model.NodeJsApp;

import android.content.Intent;

public class ForkTabFragment extends AbstractTabFragment {

  // ---------------------------------------------------------------------------------------------
  // Constructor:
  // ---------------------------------------------------------------------------------------------

  public ForkTabFragment() {
    super(true);
  }

  // ---------------------------------------------------------------------------------------------
  // Abstract Methods:
  // ---------------------------------------------------------------------------------------------

  protected void startService(NodeJsApp listItem) {
    Intent intent = getServiceIntent(listItem);

    if (intent == null)
      return;

    getContext().startService(intent);
  }

  protected void stopService(NodeJsApp listItem) {
    Intent intent = getServiceIntent(listItem);

    if (intent == null)
      return;

    getContext().stopService(intent);
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
