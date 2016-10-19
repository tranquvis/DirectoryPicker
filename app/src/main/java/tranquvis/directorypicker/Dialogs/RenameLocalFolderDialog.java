package tranquvis.directorypicker.Dialogs;

import android.app.Activity;

import java.io.File;

import tranquvis.directorypicker.Interfaces.LocalFolderRenameTitleListener;

/**
 * Created by Andi on 04.05.2015.
 */
public class RenameLocalFolderDialog extends RenameFolderDialog
{
    protected LocalFolderRenameTitleListener folderTitleListener;
    File file;

    public RenameLocalFolderDialog(Activity activity, File file) {
        super(activity, file.getName());

        this.file = file;
    }

    public void setTitleSelectedListener(LocalFolderRenameTitleListener folderTitleListener)
    {
        this.folderTitleListener = folderTitleListener;
    }

    @Override
    protected void onRenameTo(String title) {
        folderTitleListener.OnFolderRenameTitleSelected(file, title);
    }
}
