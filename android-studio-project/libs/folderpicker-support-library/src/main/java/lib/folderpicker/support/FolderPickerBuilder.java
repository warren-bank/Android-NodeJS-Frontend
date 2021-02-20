package lib.folderpicker.support;

public class FolderPickerBuilder extends lib.folderpicker.FolderPickerBuilder {
    @Override
    protected Class<? extends lib.folderpicker.FolderPicker> getFolderPickerClass() {
        return FolderPicker.class;
    }
}
