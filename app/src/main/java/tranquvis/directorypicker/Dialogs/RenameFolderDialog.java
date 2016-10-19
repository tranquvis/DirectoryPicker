package tranquvis.directorypicker.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;

import tranquvis.directorypicker.R;

/**
 * Created by Andi on 04.05.2015.
 */
public abstract class RenameFolderDialog extends Dialog implements View.OnClickListener {
    protected EditText editTextTitle;
    protected Button buttonRename;

    protected List<String> blackList;
    String title;

    public RenameFolderDialog(Activity activity, String title) {
        super(activity);
        this.setTitle(R.string.rename_folder);
        setContentView(R.layout.dialog_rename_folder);

        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        editTextTitle = (EditText)findViewById(R.id.editText_title);
        editTextTitle.setText(title);
        buttonRename = (Button) findViewById(R.id.button_rename);
        buttonRename.setOnClickListener(this);

        this.title = title;
    }

    public void setBlackList(List<String> titles)
    {
        this.blackList = titles;
    }

    @Override
    public void onClick(View v) {
        String text = editTextTitle.getText().toString();

        if(text.equals(title))
            dismiss();
        else if(text.equals(""))
            Toast.makeText(getContext(), R.string.emptyFolderName, Toast.LENGTH_SHORT).show();
        else if(blackList != null && blackList.contains(text))
            Toast.makeText(getContext(), R.string.folderAlreadyAvailable, Toast.LENGTH_SHORT).show();
        else {
            onRenameTo(text);
            dismiss();
        }
    }

    protected abstract void onRenameTo(String title);
}
