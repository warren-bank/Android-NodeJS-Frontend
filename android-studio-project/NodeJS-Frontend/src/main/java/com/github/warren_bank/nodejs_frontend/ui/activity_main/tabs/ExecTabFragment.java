package com.github.warren_bank.nodejs_frontend.ui.activity_main.tabs;

import com.github.warren_bank.nodejs_frontend.data_model.NodeJsApp;
import com.github.warren_bank.nodejs_frontend.helpers.NodeJsAppRunner;

public class ExecTabFragment extends AbstractTabFragment {

  // ---------------------------------------------------------------------------------------------
  // Constructor:
  // ---------------------------------------------------------------------------------------------

  public ExecTabFragment() {
    super(false);
  }

  // ---------------------------------------------------------------------------------------------
  // Abstract Methods:
  // ---------------------------------------------------------------------------------------------

  protected void startService(NodeJsApp listItem) {
  }

  protected void stopService(NodeJsApp listItem) {
  }

  protected void runApplication(NodeJsApp listItem) {
    NodeJsAppRunner.exec(listItem);
  }
}
