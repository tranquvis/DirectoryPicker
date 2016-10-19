package tranquvis.directorypicker.Dialogs;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Environment;
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

import tranquvis.directorypicker.Interfaces.FolderCreateTitleListener;
import tranquvis.directorypicker.Adapters.LocalFileListAdapter;
import tranquvis.directorypicker.Interfaces.LocalFolderBrowserDialogListener;
import tranquvis.directorypicker.Interfaces.LocalFolderRenameTitleListener;
import tranquvis.directorypicker.R;


public class LocalFolderBrowserDialog extends Dialog
        implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    protected FileFilter filter = new FileFilter() {
        @Override
        public boolean accept(File file)
        {
            if (!file.canRead()) return false;
            return file.isDirectory();
        }
    };

    protected Activity activity;
    protected Button buttonSelect;
    protected ImageButton buttonMenu;
    protected TextView titleTextView;

    protected ListView listBox;
    protected LocalFileListAdapter adapter;
    protected ArrayList<File> folderList = new ArrayList<>();

    protected LocalFolderBrowserDialogListener fbdListener;

    public File actFolder;

    public LocalFolderBrowserDialog(Activity activity, File initFolder) {
        super(activity);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_local_folder_browser);

        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        this.activity = activity;

        titleTextView = (TextView) findViewById(R.id.textView_path);
        titleTextView.setOnClickListener(this);

        buttonSelect = (Button) findViewById(R.id.button_select);
        buttonSelect.setOnClickListener(this);
        buttonMenu = (ImageButton) findViewById(R.id.imageButton_menu);
        buttonMenu.setOnClickListener(this);

        listBox = (ListView) findViewById(R.id.listView);

        adapter = new LocalFileListAdapter(getContext(), R.layout.listview_item_file, folderList);
        listBox.setAdapter(adapter);
        listBox.setOnItemClickListener(this);
        listBox.setOnItemLongClickListener(this);

        if(initFolder == null)
        {
            actFolder = Environment.getExternalStorageDirectory();
        }
        else actFolder = initFolder;

        loadFolder(actFolder);
    }

    public void setFolderBrowserDialogListener(LocalFolderBrowserDialogListener listener) {
        this.fbdListener = listener;
    }

    private void loadFolder(File folder)
    {
        actFolder = folder;
        titleTextView.setText(folder.toString());

        folderList.clear();
        if (folder.getParentFile() != null) {
            folderList.add(new File("..."));
        }

        File[] contents = folder.listFiles(filter);
        if(contents != null)
            Collections.addAll(folderList, contents);

        adapter.notifyDataSetChanged();
    }

    protected void Refresh()
    {
        loadFolder(actFolder);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_select)
        {
            if (fbdListener != null) fbdListener.onFolderSelected(actFolder);
            this.dismiss();

        }
        else if (i == R.id.imageButton_menu)
        {
            PopupMenu popupMenu = new PopupMenu(activity, v);
            popupMenu.inflate(R.menu.menu_folder_browser);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = activity.getMenuInflater();
        menuInflater.inflate(R.menu.menu_folder_browser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.new_folder)
        {
            CreateNewFolder();

        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        File f = folderList.get(position);
        if(f.getPath() == "...")
            loadParentFolder();
        else
            loadFolder(f);
    }

    private void loadParentFolder()
    {
        loadFolder(actFolder.getParentFile());
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        PopupMenu popupMenu = new PopupMenu(activity, view);
        popupMenu.inflate(R.menu.context_menu_folder);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                if (i == R.id.delete)
                {
                    DeleteFolder(folderList.get(position));

                }
                else if (i == R.id.rename)
                {
                    RenameFolder(folderList.get(position));

                }
                return true;
            }

        });

        popupMenu.show();

        return true;
    }

    private void CreateNewFolder() {
        CreateFolderDialog dialog = new CreateFolderDialog(activity);
        dialog.setTitleSelectedListener(new FolderCreateTitleListener() {
            @Override
            public void OnFolderCreateTitleSelected(String title) {
                File folder = new File(actFolder.getPath(), title);

                if(folder.mkdir()) {
                    if (actFolder.getParentFile() != null)
                        folderList.add(1, folder);
                    else
                        folderList.add(folder);

                    listBox.setSelectionAfterHeaderView();
                    adapter.notifyDataSetChanged();
                }
                else
                    Toast.makeText(getContext(), R.string.folderCreationFailed, Toast.LENGTH_SHORT)
                        .show();

            }
        });

        List<String> titles = new ArrayList<>();
        for (File f : folderList)
            titles.add(f.getName());
        dialog.setBlackList(titles);

        dialog.show();
    }

    private void DeleteFolder(final File folder) {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.delete_folder)
                .setMessage(folder.getName())
                .setPositiveButton(R.string.simple_yes, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (folder.delete())
                        {
                            folderList.remove(folder);
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

    private void RenameFolder(File folder) {
        RenameLocalFolderDialog dialog = new RenameLocalFolderDialog(activity, folder);
        dialog.setTitleSelectedListener(new LocalFolderRenameTitleListener(){

            @Override
            public void OnFolderRenameTitleSelected(File file, String title) {
                File newFile = new File(file.getPath().substring(0,file.getPath().lastIndexOf('/')),title);

                if(file.renameTo(newFile)) {
                    folderList.set(folderList.indexOf(file), newFile);
                    adapter.notifyDataSetChanged();
                }
                else
                    Toast.makeText(getContext(), R.string.folderRenamingFailed, Toast.LENGTH_SHORT)
                        .show();
            }
        });

        List<String> titles = new ArrayList<>();
        for (File f : folderList) {
            titles.add(f.getName());
        }
        dialog.setBlackList(titles);

        dialog.show();
    }
}