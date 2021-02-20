package lib.folderpicker.support;

import lib.folderpicker.R;
import lib.folderpicker.FilePojo;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FolderAdapter extends RecyclerView.Adapter<FileViewHolder> {
  private FolderPicker        mFolderPicker;
  private ArrayList<FilePojo> mFolderAndFileList;

  public FolderAdapter(FolderPicker mFolderPicker, ArrayList<FilePojo> mFolderAndFileList) {
    this.mFolderPicker      = mFolderPicker;
    this.mFolderAndFileList = mFolderAndFileList;
  }

  @Override
  public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View fileRowView = mFolderPicker.getLayoutInflater().inflate(R.layout.fp_filerow, parent, false);
    FileViewHolder holder = new FileViewHolder(fileRowView);

    fileRowView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        int position = holder.getAdapterPosition();
        mFolderPicker.listClick(position);
      }
    });

    return holder;
  }

  @Override
  public void onBindViewHolder(FileViewHolder holder, int position) {
    FilePojo item = mFolderAndFileList.get(position);
    holder.bind(item);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public int getItemCount() {
    return mFolderAndFileList.size();
  }
}
