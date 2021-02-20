package lib.folderpicker.support;

import lib.folderpicker.R;
import lib.folderpicker.FilePojo;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class FileViewHolder extends RecyclerView.ViewHolder {
  protected ImageView iconView;
  protected TextView  nameView;

  public FileViewHolder(View fileRowView) {
    super(fileRowView);

    iconView = (ImageView) fileRowView.findViewById(R.id.fp_iv_icon);
    nameView = (TextView)  fileRowView.findViewById(R.id.fp_tv_name);
  }

  public void bind(FilePojo item) {
    int iconRes = item.isFolder() ? R.drawable.fp_folder : R.drawable.fp_file;

    iconView.setImageResource(iconRes);
    nameView.setText(item.getName());
  }
}
