package com.github.warren_bank.nodejs_frontend.helpers;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

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

  public static void setSnackbarHeight(Snackbar SB, int maxLines) {
    View     SBV  = SB.getView();
    TextView SBTV = (TextView) SBV.findViewById(com.google.android.material.R.id.snackbar_text);
    SBTV.setMaxLines(maxLines);
  }

  public static Snackbar getSnackbar(View view, String text) {
    return getSnackbar(view, text, 1);
  }

  public static Snackbar getSnackbar(View view, String text, int maxLines) {
    return getSnackbar(view, text, maxLines, Snackbar.LENGTH_SHORT);
  }

  public static Snackbar getSnackbar(View view, String text, int maxLines, int duration) {
    Snackbar SB = Snackbar.make(view, text, duration);

    if (maxLines > 1)
      setSnackbarHeight(SB, maxLines);

    return SB;
  }

}
