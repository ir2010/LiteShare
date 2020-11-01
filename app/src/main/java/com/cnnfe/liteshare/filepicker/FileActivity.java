package com.cnnfe.liteshare.filepicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cnnfe.liteshare.connect.DevicesActivity;
import com.cnnfe.liteshare.R;
import com.cnnfe.liteshare.connect.Helper;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class FileActivity extends AppCompatActivity
{
    //ArrayList<String> listOfFiles = new ArrayList<>();
    //ArrayList<String> listOfIcons = new ArrayList<>();
    private static final int CHOOSE_FILE = 1;
    private static final int CHOOSE_APP = 2;

    Button docs, images, audios, videos, send_files, apps;
    EditText message_edittext;
    String message = "", fileExtension= "";
    TextView selected_files;

    public ArrayList<String> stringUriList = new ArrayList<String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        //RecyclerView recyclerView = (RecyclerView) findViewById(R.id.files_list);
        docs = (Button) findViewById(R.id.docs);
        images = (Button) findViewById(R.id.images);
        audios = (Button) findViewById(R.id.audios);
        videos = (Button) findViewById(R.id.videos);
        apps = (Button) findViewById(R.id.apps);
        selected_files = (TextView) findViewById(R.id.selected_files);
        send_files = (Button) findViewById(R.id.send_file);
        message_edittext = (EditText) findViewById(R.id.edit_input);


        send_files.setEnabled(false);
        selected_files.setText("Selected files: ");
        //If android version is greater than marshmallow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            //checking for permissions
            if(getPackageManager().checkPermission(WRITE_EXTERNAL_STORAGE, getPackageName()) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, 111);
            }
        }

        //choose document type files
        docs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile("*/*");
            }
        });

        //choose image type files
        images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile("image/*");
            }
        });

        //choose audio type files
        audios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile("audio/*");
            }
        });

        //choose video type files
        videos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile("video/*");
            }
        });

        //choose apps
        apps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //chooseFile("application/*");
                Intent intent = new Intent(FileActivity.this, AppShow.class);
                startActivityForResult(intent, CHOOSE_APP);
            }
        });

        send_files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FileActivity.this, DevicesActivity.class);
                intent.putExtra("fileUri", stringUriList);
                //intent.putExtra("extension", fileExtension);
                intent.putExtra("msg", message);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        message_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                message = message_edittext.getText().toString();
                if(message == "" || message == " " || message == "  ")
                    send_files.setEnabled(false);
                else
                    send_files.setEnabled(true);
            }
        });

        /*recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        showAllFiles();

        FileAdapter adapter = new FileAdapter(listOfIcons , listOfFiles, this);
        recyclerView.setAdapter(adapter);
    }

    void showAllFiles()
    {
        File root = new File(getExternalFilesDir());
        ListDir(root);
    }

    void ListDir(File root)
    {
        /*File[] files = root.listFiles();
        listOfFiles.clear();

        for(File file: files)
        {
            listOfFiles.add(file.getName());
        }*/
/*
        int i=0;
        File[] files = root.listFiles();
        if (files != null)
        {
            for(File file: files)
            {
                if (file != null)
                {
                    if (file.isDirectory()) {
                        ListDir(file);
                    } else {
                        listOfFiles.add(file.getName());
                        listOfIcons.add(file.getName());
                        i++;
                    }
                }
            }
        }*/

    }

    private void chooseFile(String MIMEType)
    {
        String[] mimeForDocs = {"application/*", "text/*"};
        //opening file picker
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType(MIMEType);

        //for docs
        if(MIMEType == "*/*")
        {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeForDocs);
        }


        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        startActivityForResult(intent, CHOOSE_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData)
    {
        super.onActivityResult(requestCode, resultCode, resultData);

        if (requestCode == CHOOSE_FILE && resultCode == Activity.RESULT_OK)
        {
            // The result data contains a URI for the document or directory that the user selected.
            if (resultData != null)
            {
                send_files.setEnabled(true);
                if(resultData.getClipData() != null) {
                    for (int i = 0; i < resultData.getClipData().getItemCount(); i++) {
                        Uri uri = resultData.getClipData().getItemAt(i).getUri();
                        stringUriList.add(uri.toString());
                        selected_files.setText(selected_files.getText() + "\n" + new Helper(getApplicationContext()).getNameFromURI(uri));
                    }
                }
                else
                {
                    Uri uri = resultData.getData();
                    stringUriList.add(resultData.getData().toString());
                    selected_files.setText(selected_files.getText() + " " + new Helper(getApplicationContext()).getNameFromURI(uri));
                }
                message_edittext.setEnabled(true);

                /*ContentResolver contentResolver = getApplicationContext().getContentResolver();
                MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                fileExtension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
                Toast.makeText(FileActivity.this, fileExtension, Toast.LENGTH_SHORT).show();*/
            }
        }

        if(requestCode == CHOOSE_APP && resultCode == Activity.RESULT_OK)
        {
            Toast.makeText(this, "ho", Toast.LENGTH_SHORT).show();
            stringUriList = resultData.getStringArrayListExtra("apps");
            if(stringUriList.size() != 0)
            {
                //send_files.setEnabled(true);
            }

            for(int i=0; i<stringUriList.size(); i++)
            {
                Uri uri = Uri.parse(stringUriList.get(i));
                selected_files.setText(selected_files.getText() + "\n" + uri.toString());
            }
        }
    }

    //requesting permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int grantResult: grantResults)
        {
            if(grantResult == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show();
            else
            {
                //disable all the buttons
                docs.setEnabled(false);
                images.setEnabled(false);
                apps.setEnabled(false);
                audios.setEnabled(false);
                videos.setEnabled(false);
            }
        }
    }

    private void showSnackbar(View view)
    {
        Snackbar snackbar = Snackbar.make(view, "Permissions not granted!", Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, 111);
            }
        });
        snackbar.show();
    }
}