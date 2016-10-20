package tranquvis.directorypicker.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import tranquvis.directorypicker.Interfaces.RenameDirectoryDialogListener;
import tranquvis.directorypicker.R;

/**
 * Created by Andi on 04.05.2015.
 */
public class RenameDirectoryDialog extends Dialog implements View.OnClickListener
{
    private RenameDirectoryDialogListener listener;
    private File file;
    private EditText editTextTitle;
    private Button buttonRename;

    private List<String> blackList;
    private String title;

    public RenameDirectoryDialog(Activity activity, File file) {
        super(activity);
        this.setTitle(R.string.rename_folder);
        setContentView(R.layout.dialog_rename_dir);

        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        editTextTitle = (EditText)findViewById(R.id.editText_title);
        title = "";
        editTextTitle.setText(title);
        buttonRename = (Button) findViewById(R.id.button_rename);
        buttonRename.setOnClickListener(this);

        this.file = file;
    }

    @Override
    public void onClick(View v)
    {
        String text = editTextTitle.getText().toString();

        if (text.equals(title))
            dismiss();
        else if (text.equals(""))
            Toast.makeText(getContext(), R.string.emptyFolderName, Toast.LENGTH_SHORT).show();
        else if (blackList != null && blackList.contains(text))
            Toast.makeText(getContext(), R.string.folderAlreadyAvailable, Toast.LENGTH_SHORT).show();
        else
        {
            onRenameTo(text);
            dismiss();
        }
    }

    public void setBlackList(List<String> titles)
    {
        this.blackList = titles;
    }

    public void setListener(RenameDirectoryDialogListener listener)
    {
        this.listener = listener;
    }

    protected void onRenameTo(String title) {
        listener.OnRenameRequested(file, title);
    }
}
