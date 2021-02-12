package com.github.warren_bank.nodejs_frontend.helpers;

import java.util.ArrayList;

public final class EnvVarsParser {

  public static String[][] getEnvArray(String env) {
    ArrayList<String[]> envArrayList = getEnvArrayList(env);
    return (envArrayList == null) ? null : envArrayList.toArray( new String[envArrayList.size()][] );
  }

  public static ArrayList<String[]> getEnvArrayList(String env) {
    if (env == null)
      return null;

    env = env.trim();

    ArrayList<String[]> envArrayList = new ArrayList<String[]>();

    String[] pairs = env.split("\\s*[\\r\\n]+\\s*");
    String[] parts;
    for (String pair: pairs) {
      parts = pair.split("\\s*[:=]\\s*", 2);

      if ((parts.length == 2) && !parts[0].isEmpty() && !parts[1].isEmpty()) {
        envArrayList.add(parts);
      }
    }

    return envArrayList.isEmpty() ? null : envArrayList;
  }

  public static String toString(String[][] envArray) {
    if ((envArray == null) || (envArray.length == 0))
      return "";

    StringBuffer envBuffer = new StringBuffer();

    String[] parts;
    for (int i=0; i < envArray.length; i++) {
      parts = envArray[i];

      envBuffer.append(parts[0] + "=" + parts[1] + "\n");
    }

    return envBuffer.toString();
  }

}
