package tranquvis.directorypicker.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;

import tranquvis.directorypicker.Interfaces.FolderCreateTitleListener;
import tranquvis.directorypicker.R;

/**
 * Created by Andi on 04.05.2015.
 */
public class CreateFolderDialog extends Dialog implements View.OnClickListener {
    protected EditText editTextTitle;
    protected Button buttonCreate;

    protected List<String> blackList;
    protected FolderCreateTitleListener folderTitleListener;

    public CreateFolderDialog(Activity activity) {
        super(activity);
        setTitle(R.string.new_folder);
        setContentView(R.layout.dialog_create_folder);

        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        editTextTitle = (EditText)findViewById(R.id.editText_title);
        buttonCreate = (Button) findViewById(R.id.button_create);
        buttonCreate.setOnClickListener(this);
    }

    public void setTitleSelectedListener(FolderCreateTitleListener folderTitleListener)
    {
        this.folderTitleListener = folderTitleListener;
    }

    public void setBlackList(List<String> titles)
    {
        this.blackList = titles;
    }

    @Override
    public void onClick(View v) {
        String text = editTextTitle.getText().toString();

        if(text.equals(""))
            Toast.makeText(getContext(), R.string.emptyFolderName, Toast.LENGTH_SHORT).show();
        else if(blackList != null && blackList.contains(text))
            Toast.makeText(getContext(), R.string.folderAlreadyAvailable, Toast.LENGTH_SHORT).show();
        else {
            folderTitleListener.OnFolderCreateTitleSelected(text);
            dismiss();
        }
    }
}
