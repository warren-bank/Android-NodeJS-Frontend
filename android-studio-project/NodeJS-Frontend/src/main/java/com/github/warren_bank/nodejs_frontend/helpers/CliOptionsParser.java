package com.github.warren_bank.nodejs_frontend.helpers;

import java.util.ArrayList;

// https://stackoverflow.com/q/18404128

public final class CliOptionsParser {

  // Parses a regular command line and returns it as a string array for use with Runtime.exec()
  public static String[] getCmdArray(String cmd) {
    ArrayList<String> cmdArrayList = getCmdArrayList(cmd);
    return (cmdArrayList == null) ? null : cmdArrayList.toArray( new String[cmdArrayList.size()] );
  }

  public static ArrayList<String> getCmdArrayList(String cmd) {
    if (cmd == null)
      return null;

    ArrayList<String> cmdArrayList = new ArrayList<String>();
    StringBuffer argBuffer = new StringBuffer();
    char[] quotes = {'"', '\''};
    char currentChar = 0, protect = '\\', separate = ' ';
    int cursor = 0;
    cmd = cmd.trim();

    while (cursor < cmd.length()) {
      currentChar = cmd.charAt(cursor);

      // Handle protected characters
      if (currentChar == protect) {
        if (cursor + 1 < cmd.length()) {
          char protectedChar = cmd.charAt(cursor + 1);
          argBuffer.append(protectedChar);
          cursor += 2;
        }
        else
          return null; // Unprotected \ at end of cmd
      }

      // Handle quoted args
      else if (inArray(currentChar, quotes)) {
        int nextQuote = cmd.indexOf(currentChar, cursor + 1);
        if (nextQuote != -1) {
          cmdArrayList.add(cmd.substring(cursor + 1, nextQuote));
          cursor = nextQuote + 1;
        }
        else
          return null; // Unprotected, unclosed quote
      }

      // Handle separator
      else if (currentChar == separate) {
        if (argBuffer.length() != 0)
          cmdArrayList.add(argBuffer.toString());
        argBuffer.setLength(0);
        cursor++;
      }

      else {
        argBuffer.append(currentChar);
        cursor++;
      }
    }

    // Handle the last argument (doesn't have a space after it)
    if (currentChar != 0)
      cmdArrayList.add(argBuffer.toString());

    return cmdArrayList.isEmpty() ? null : cmdArrayList;
  }

  private static boolean inArray(char needle, char[] stack) {
    for (char c : stack)
      if (needle == c)
        return true;
    return false;
  }

  public static String toString(String[] cmdArray) {
    if ((cmdArray == null) || (cmdArray.length == 0))
      return "";

    StringBuffer cmdBuffer = new StringBuffer();
    String separate = " ", quote = "\"", protect = "\\";

    String cmd;
    for (int i=0; i < cmdArray.length; i++) {
      cmd = cmdArray[i];

      if (cmd.contains(separate)) {
        cmd = cmd.replaceAll(quote, protect + quote);
        cmd = quote + cmd + quote;
      }

      cmdBuffer.append(cmd + separate);
    }

    return cmdBuffer.toString().trim();
  }

}
