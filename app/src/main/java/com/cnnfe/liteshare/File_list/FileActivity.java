package com.cnnfe.liteshare.File_list;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cnnfe.liteshare.Connect_devices.DevicesActivity;
import com.cnnfe.liteshare.MainActivity;
import com.cnnfe.liteshare.R;
import com.google.android.material.snackbar.Snackbar;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class FileActivity extends AppCompatActivity
{
    //ArrayList<String> listOfFiles = new ArrayList<>();
    //ArrayList<String> listOfIcons = new ArrayList<>();
    private static final int CHOOSE_FILE = 1;

    Button docs, images, audios, videos, apps, send_files;
    TextView selected_files;
    Uri uri = null;

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
                startActivity(intent);
            }
        });

        send_files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FileActivity.this, DevicesActivity.class);
                intent.putExtra("fileUri", uri);
                startActivity(intent);
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

   // @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData)
    {
        super.onActivityResult(requestCode, resultCode, resultData);

        if (requestCode == CHOOSE_FILE && resultCode == Activity.RESULT_OK)
        {
            // The result data contains a URI for the document or directory that the user selected.
            if (resultData != null)
            {
                uri = resultData.getData();
                // Perform operations on the document using its URI.
                //String path;

                //String[] projection = { MediaStore.Images.Media.DATA };
                //Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                //int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                //cursor.moveToFirst();

                //path = cursor.getString(column_index);
                Toast.makeText(this, uri.toString(), Toast.LENGTH_LONG).show();
                selected_files.setText(uri.toString());
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