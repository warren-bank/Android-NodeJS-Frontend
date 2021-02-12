package com.github.warren_bank.nodejs_frontend.ui.activity_main.tabs.app_list;

import com.github.warren_bank.nodejs_frontend.R;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {
  protected View      rowView;
  protected TextView  appTitle;
  protected ImageView dragHandle;

  public RecyclerViewHolder(View listItemView) {
    super(listItemView);

    rowView    = listItemView;
    appTitle   = (TextView)  listItemView.findViewById(R.id.app_title);
    dragHandle = (ImageView) listItemView.findViewById(R.id.drag_handle);
  }

  public void bind(String title) {
    appTitle.setText(title);
  }
}
