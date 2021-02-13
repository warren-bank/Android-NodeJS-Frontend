package com.github.warren_bank.nodejs_frontend.ui.activity_main.tabs.app_list;

import com.github.warren_bank.nodejs_frontend.R;
import com.github.warren_bank.nodejs_frontend.data_model.NodeJsApp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> implements ItemMoveCallback.OnRowEventListener {

  public interface ColorPalette {
    int getColorOnRowSelected(NodeJsApp listItem);
    int getColorOnRowClear(NodeJsApp listItem);
  }

  private ArrayList<NodeJsApp>             listItems;
  private View.OnCreateContextMenuListener onCreateContextMenuListener;
  private ItemMoveCallback.OnDragListener  onDragListener;
  private ColorPalette                     colorPalette;

  public RecyclerViewAdapter(ArrayList<NodeJsApp> listItems, View.OnCreateContextMenuListener onCreateContextMenuListener, ItemMoveCallback.OnDragListener onDragListener, ColorPalette colorPalette) {
    this.listItems                   = listItems;
    this.onCreateContextMenuListener = onCreateContextMenuListener;
    this.onDragListener              = onDragListener;
    this.colorPalette                = colorPalette;
  }

  @Override
  public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_list_item, parent, false);
    RecyclerViewHolder holder = new RecyclerViewHolder(listItemView);

    if (onCreateContextMenuListener != null)
      holder.appTitle.setOnCreateContextMenuListener(onCreateContextMenuListener);

    holder.appTitle.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        int position = holder.getAdapterPosition();

        holder.appTitle.setTag(Integer.valueOf(position));
        holder.appTitle.showContextMenu();
      }
    });

    holder.dragHandle.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if ((onDragListener != null) && (event.getAction() == MotionEvent.ACTION_DOWN))
          onDragListener.onDragBegin(holder);

        return false;
      }
    });

    return holder;
  }

  @Override
  public void onBindViewHolder(RecyclerViewHolder holder, int position) {
    NodeJsApp listItem = listItems.get(position);
    String title = listItem.toString();
    holder.bind(title);

    if (colorPalette != null) {
      int color = colorPalette.getColorOnRowClear(listItem);
      updateRowBackgroundColor(holder, color);
    }
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public int getItemCount() {
    return listItems.size();
  }

  // ---------------------------------------------------------------------------------------------
  // Drag/Drop:
  // ---------------------------------------------------------------------------------------------

  @Override
  public void onRowMoved(int fromPosition, int toPosition) {
    if (fromPosition == toPosition)
      return;

    NodeJsApp.movePosition(listItems, fromPosition, toPosition);
    notifyItemMoved(fromPosition, toPosition);

    if (onDragListener != null)
      onDragListener.onDragComplete(fromPosition, toPosition);
  }

  @Override
  public void onRowSelected(RecyclerViewHolder holder) {
    if (colorPalette != null) {
      NodeJsApp listItem = getListItemFromHolder(holder);
      int color = colorPalette.getColorOnRowSelected(listItem);
      updateRowBackgroundColor(holder, color);
    }
  }

  @Override
  public void onRowClear(RecyclerViewHolder holder) {
    if (colorPalette != null) {
      NodeJsApp listItem = getListItemFromHolder(holder);
      int color = colorPalette.getColorOnRowClear(listItem);
      updateRowBackgroundColor(holder, color);
    }
  }

  private NodeJsApp getListItemFromHolder(RecyclerViewHolder holder) {
    int position = holder.getAdapterPosition();
    NodeJsApp listItem = listItems.get(position);
    return listItem;
  }

  // ---------------------------------------------------------------------------------------------
  // Row Colors:
  // ---------------------------------------------------------------------------------------------

  public void updateRowBackgroundColor(RecyclerViewHolder holder, int color) {
    holder.rowView.setBackgroundColor(color);
  }
}
