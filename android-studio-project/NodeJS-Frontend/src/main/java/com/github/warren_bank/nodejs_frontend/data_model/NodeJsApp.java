package com.github.warren_bank.nodejs_frontend.data_model;

import com.github.warren_bank.nodejs_frontend.helpers.EnvVarsParser;
import com.github.warren_bank.nodejs_frontend.helpers.CliOptionsParser;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;

public final class NodeJsApp {
  private String  id;
  private boolean isActive;

  private String     title;
  private String     cwd_dirpath;
  private String[][] env_vars;
  private String[]   node_options;
  private String     js_filepath;
  private String[]   js_options;

  public NodeJsApp(String id) {
    this.id = id;
    remove();
  }

  public void remove() {
    update(false, null, null, null, null, null, null);
  }

  // shallow copy
  public void add(NodeJsApp template) {
    update(true, template.title, template.cwd_dirpath, template.env_vars, template.node_options, template.js_filepath, template.js_options);
  }

  public void add(String title, String cwd_dirpath, String[][] env_vars, String[] node_options, String js_filepath, String[] js_options) {
    update(true, title, cwd_dirpath, env_vars, node_options, js_filepath, js_options);
  }

  public void add(String title, String cwd_dirpath, String env_vars_text, String node_options_text, String js_filepath, String js_options_text) {
    String[][] env_vars     = EnvVarsParser.getEnvArray(env_vars_text);
    String[]   node_options = CliOptionsParser.getCmdArray(node_options_text);
    String[]   js_options   = CliOptionsParser.getCmdArray(js_options_text);

    update(true, title, cwd_dirpath, env_vars, node_options, js_filepath, js_options);
  }

  private void update(boolean isActive, String title, String cwd_dirpath, String[][] env_vars, String[] node_options, String js_filepath, String[] js_options) {
    this.isActive     = isActive;
    this.title        = title;
    this.cwd_dirpath  = cwd_dirpath;
    this.env_vars     = env_vars;
    this.node_options = node_options;
    this.js_filepath  = js_filepath;
    this.js_options   = js_options;
  }

  @Override
  public String toString() {
    return title;
  }

  public String[] getStringArray() {
    String[] result = new String[6];

    result[0] = title;
    result[1] = cwd_dirpath;
    result[2] = EnvVarsParser.toString(env_vars);
    result[3] = CliOptionsParser.toString(node_options);
    result[4] = js_filepath;
    result[5] = CliOptionsParser.toString(js_options);

    return result;
  }

  public boolean isActive() {
    return isActive;
  }

  public boolean isDaemon() {
    return (id != null);
  }

  public String getId() {
    return id;
  }

  public String getCurrentWorkingDirectory() {
    return cwd_dirpath;
  }

  public String[][] getEnvironmentVariables() {
    return env_vars;
  }

  public String[] getNodeOptions() {
    return node_options;
  }

  public String getJsApplicationFilepath() {
    return js_filepath;
  }

  public String[] getJsApplicationOptions() {
    return js_options;
  }

  // helpers

  public static String serialize(NodeJsApp app) {
    String json = new Gson().toJson(app);
    return json;
  }

  public static NodeJsApp deserialize(String json) {
    NodeJsApp app;
    Gson gson = new Gson();
    app = gson.fromJson(json, new TypeToken<NodeJsApp>(){}.getType());
    return app;
  }

  public static String toJson(ArrayList<NodeJsApp> arrayList) {
    String json = new Gson().toJson(arrayList);
    return json;
  }

  public static ArrayList<NodeJsApp> fromJson(String json) {
    ArrayList<NodeJsApp> arrayList;
    Gson gson = new Gson();
    arrayList = gson.fromJson(json, new TypeToken<ArrayList<NodeJsApp>>(){}.getType());
    return arrayList;
  }

  public static ArrayList<NodeJsApp> filterActive(ArrayList<NodeJsApp> arrayList) {
    ArrayList<NodeJsApp> filtered = new ArrayList<NodeJsApp>();

    NodeJsApp app;
    for (int i=0; i < arrayList.size(); i++) {
      app = arrayList.get(i);
      if (app.isActive) {
        filtered.add(app);
      }
    }

    return filtered;
  }

  public static NodeJsApp findById(ArrayList<NodeJsApp> arrayList, String id) {
    if ((id == null) || id.isEmpty())
      return null;

    NodeJsApp app;
    for (int i=0; i < arrayList.size(); i++) {
      app = arrayList.get(i);
      if (app.isActive && id.equals(app.getId())) {
        return app;
      }
    }

    return null;
  }

  public static void movePosition(ArrayList<NodeJsApp> arrayList, int fromPosition, int toPosition) {
    if (fromPosition == toPosition)
      return;

    if (fromPosition < toPosition) {
      for (int i = fromPosition; i < toPosition; i++) {
        Collections.swap(arrayList, i, i + 1);
      }
    }
    else {
      for (int i = fromPosition; i > toPosition; i--) {
        Collections.swap(arrayList, i, i - 1);
      }
    }
  }
}
