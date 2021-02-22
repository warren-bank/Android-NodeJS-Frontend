package com.github.warren_bank.nodejs_frontend.services.exec;

import com.github.warren_bank.nodejs_frontend.services.exec.IRemoteServiceCallback;

oneway interface IRemoteService {
  void registerCallback(in IRemoteServiceCallback cb);

//void unregisterCallback(in IRemoteServiceCallback cb);

  void startNodeWithArguments(in String title, in String serializedNodeJsApp);

  void die();
}
