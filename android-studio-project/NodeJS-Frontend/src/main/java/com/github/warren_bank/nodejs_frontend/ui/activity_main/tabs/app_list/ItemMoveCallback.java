package com.github.warren_bank.nodejs_frontend.ui.activity_main.tabs.app_list;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ItemMoveCallback extends ItemTouchHelper.Callback {

  public interface OnDragListener {
    void onDragBegin(RecyclerViewHolder holder);
    void onDragComplete(int fromPosition, int toPosition);
  }

  public interface OnRowEventListener {
    void onRowMoved    (int fromPosition, int toPosition);
    void onRowSelected (RecyclerViewHolder holder);
    void onRowClear    (RecyclerViewHolder holder);
  }

  private final OnRowEventListener mListener;

  public ItemMoveCallback(OnRowEventListener listener) {
    mListener = listener;
  }

  @Override
  public boolean isLongPressDragEnabled() {
    return false;
  }

  @Override
  public boolean isItemViewSwipeEnabled() {
    return false;
  }

  @Override
  public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {}

  @Override
  public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
    int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
    return makeMovementFlags(dragFlags, 0);
  }

  @Override
  public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
    mListener.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
    return true;
  }

  @Override
  public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
    super.onSelectedChanged(viewHolder, actionState);

    if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
      if (viewHolder instanceof RecyclerViewHolder) {
        RecyclerViewHolder holder = (RecyclerViewHolder) viewHolder;
        mListener.onRowSelected(holder);
      }
    }
  }

  @Override
  public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
    super.clearView(recyclerView, viewHolder);

    if (viewHolder instanceof RecyclerViewHolder) {
      RecyclerViewHolder holder = (RecyclerViewHolder) viewHolder;
      mListener.onRowClear(holder);
    }
  }
}
