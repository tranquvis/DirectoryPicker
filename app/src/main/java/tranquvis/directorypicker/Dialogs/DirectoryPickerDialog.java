package tranquvis.directorypicker.Dialogs;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tranquvis.directorypicker.Interfaces.CreateDirectoryDialogListener;
import tranquvis.directorypicker.Adapters.ElementListAdapter;
import tranquvis.directorypicker.Interfaces.DirectoryPickerListener;
import tranquvis.directorypicker.Interfaces.RenameDirectoryDialogListener;
import tranquvis.directorypicker.R;


public class DirectoryPickerDialog extends Dialog
        implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private FileFilter filter = new FileFilter() {
        @Override
        public boolean accept(File file)
        {
            return file.canRead() && file.isDirectory();
        }
    };

    private Activity activity;
    private Button buttonSelect;
    private ImageButton buttonMenu;
    private TextView titleTextView;

    private ListView listBox;
    private ElementListAdapter adapter;
    private ArrayList<File> dirList = new ArrayList<>();

    private DirectoryPickerListener fbdListener;

    private File actDir;

    public DirectoryPickerDialog(Activity activity, File initDir) {
        super(activity);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_dir_picker);

        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        this.activity = activity;

        titleTextView = (TextView) findViewById(R.id.textView_path);
        titleTextView.setOnClickListener(this);

        buttonSelect = (Button) findViewById(R.id.button_select);
        buttonSelect.setOnClickListener(this);
        buttonMenu = (ImageButton) findViewById(R.id.imageButton_menu);
        buttonMenu.setOnClickListener(this);

        listBox = (ListView) findViewById(R.id.listView);

        adapter = new ElementListAdapter(getContext(), dirList);
        listBox.setAdapter(adapter);
        listBox.setOnItemClickListener(this);
        listBox.setOnItemLongClickListener(this);

        if(initDir == null)
        {
            actDir = Environment.getExternalStorageDirectory();
        }
        else actDir = initDir;

        loadDir(actDir);
    }

    public void setDirectoryPickerListener(DirectoryPickerListener listener) {
        this.fbdListener = listener;
    }

    private void loadDir(File dir)
    {
        actDir = dir;
        titleTextView.setText(dir.toString());

        dirList.clear();
        if (dir.getParentFile() != null) {
            dirList.add(new File("..."));
        }

        File[] contents = dir.listFiles(filter);
        if(contents != null)
            Collections.addAll(dirList, contents);

        adapter.notifyDataSetChanged();
    }

    protected void Refresh()
    {
        loadDir(actDir);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_select)
        {
            if (fbdListener != null) fbdListener.onDirPicked(actDir);
            this.dismiss();

        }
        else if (i == R.id.imageButton_menu)
        {
            PopupMenu popupMenu = new PopupMenu(activity, v);
            popupMenu.inflate(R.menu.menu_dir_picker);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
            {
                @Override
                public boolean onMenuItemClick(MenuItem item)
                {
                    return onOptionsItemSelected(item);
                }
            });
            popupMenu.show();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater menuInflater = activity.getMenuInflater();
        menuInflater.inflate(R.menu.menu_dir_picker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.new_folder)
        {
            CreateNewDirectory();

        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        File f = dirList.get(position);
        if(f.getPath().equals("..."))
            loadParentDir();
        else
            loadDir(f);
    }

    private void loadParentDir()
    {
        loadDir(actDir.getParentFile());
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        PopupMenu popupMenu = new PopupMenu(activity, view);
        popupMenu.inflate(R.menu.context_menu_element);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                if (i == R.id.delete)
                    DeleteDirectory(dirList.get(position));
                else if (i == R.id.rename)
                    RenameDirectory(dirList.get(position));
                return true;
            }

        });
        popupMenu.show();
        return true;
    }

    private void CreateNewDirectory() {
        CreateDirectoryDialog dialog = new CreateDirectoryDialog(activity);
        dialog.setListener(new CreateDirectoryDialogListener() {
            @Override
            public void OnDirCreationRequested(String title) {
                File dir = new File(actDir.getPath(), title);
                if(dir.mkdir()) {
                    if (actDir.getParentFile() != null)
                        dirList.add(1, dir);
                    else
                        dirList.add(dir);

                    listBox.setSelectionAfterHeaderView();
                    adapter.notifyDataSetChanged();
                }
                else
                    Toast.makeText(getContext(), R.string.folderCreationFailed, Toast.LENGTH_SHORT)
                        .show();

            }
        });

        List<String> titles = new ArrayList<>();
        for (File f : dirList)
            titles.add(f.getName());
        dialog.setBlackList(titles);

        dialog.show();
    }

    private void DeleteDirectory(final File dir) {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.delete_folder)
                .setMessage(dir.getName())
                .setPositiveButton(R.string.simple_yes, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dir.delete())
                        {
                            dirList.remove(dir);
                            adapter.notifyDataSetChanged();
                        }
                        else
                            Toast.makeText(getContext(), R.string.folderDeletionFailed,
                                    Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.simple_abort, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private void RenameDirectory(File dir) {
        RenameDirectoryDialog dialog = new RenameDirectoryDialog(activity, dir);
        dialog.setListener(new RenameDirectoryDialogListener(){

            @Override
            public void OnRenameRequested(File file, String title) {
                File newFile = new File(file.getPath().substring(0,file.getPath().lastIndexOf('/')),title);

                if(file.renameTo(newFile)) {
                    dirList.set(dirList.indexOf(file), newFile);
                    adapter.notifyDataSetChanged();
                }
                else
                    Toast.makeText(getContext(), R.string.folderRenamingFailed, Toast.LENGTH_SHORT)
                        .show();
            }
        });

        List<String> titles = new ArrayList<>();
        for (File f : dirList) {
            titles.add(f.getName());
        }
        dialog.setBlackList(titles);

        dialog.show();
    }
}