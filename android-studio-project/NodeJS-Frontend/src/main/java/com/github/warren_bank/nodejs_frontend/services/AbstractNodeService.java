package com.github.warren_bank.nodejs_frontend.services;

import com.github.warren_bank.nodejs_frontend.data_model.NodeJsApp;
import com.github.warren_bank.nodejs_frontend.data_model.Preferences;
import com.github.warren_bank.nodejs_frontend.helpers.NodeJsAppRunner;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public abstract class AbstractNodeService extends Service {

  protected abstract String getId();

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

}
