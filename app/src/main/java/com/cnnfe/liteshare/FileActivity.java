package com.cnnfe.liteshare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class FileActivity extends AppCompatActivity
{
    ArrayList<String> listOfFiles = new ArrayList<>();
    ArrayList<String> listOfIcons = new ArrayList<>();
    Button docs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        //RecyclerView recyclerView = (RecyclerView) findViewById(R.id.files_list);
        docs = (Button) findViewById(R.id.docs);

        //checking for permissions
        if(getPackageManager().checkPermission(WRITE_EXTERNAL_STORAGE, getPackageName()) != PackageManager.PERMISSION_GRANTED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, 111);
            }
        }

        docs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFile();
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

    private static final int PICK_PDF_FILE = 2;

    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf|image/*");

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        startActivityForResult(intent, PICK_PDF_FILE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == PICK_PDF_FILE
                && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                // Perform operations on the document using its URI.
                //String path;

                //String[] projection = { MediaStore.Images.Media.DATA };
                //Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                //int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                //cursor.moveToFirst();

                //path = cursor.getString(column_index);
                Toast.makeText(this, uri.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int grantResult: grantResults)
        {
            if(grantResult == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show();
        }
    }
}