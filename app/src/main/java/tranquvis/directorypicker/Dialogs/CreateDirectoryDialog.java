package tranquvis.directorypicker.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;

import tranquvis.directorypicker.Interfaces.CreateDirectoryDialogListener;
import tranquvis.directorypicker.R;

/**
 * Created by Andi on 04.05.2015.
 */
public class CreateDirectoryDialog extends Dialog implements View.OnClickListener {
    private EditText editTextTitle;
    private Button buttonCreate;

    private List<String> blackList;
    private CreateDirectoryDialogListener listener;

    public CreateDirectoryDialog(Activity activity) {
        super(activity);
        setTitle(R.string.new_folder);
        setContentView(R.layout.dialog_create_dir);

        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        editTextTitle = (EditText)findViewById(R.id.editText_title);
        buttonCreate = (Button) findViewById(R.id.button_create);
        buttonCreate.setOnClickListener(this);
    }

    public void setListener(CreateDirectoryDialogListener listener)
    {
        this.listener = listener;
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
            listener.OnDirCreationRequested(text);
            dismiss();
        }
    }
}
