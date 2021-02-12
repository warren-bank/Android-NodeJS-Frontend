package com.github.warren_bank.nodejs_frontend.helpers;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.view.View;

public final class UI {

  public static void hideKeyboard(Context context, View view) {
    InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
  }

  public static void hideKeyboard(Activity activity) {
    Context context = (Context) activity;
    View view = activity.getWindow().getDecorView().findViewById(android.R.id.content);

    hideKeyboard(context, view);
  }

}
