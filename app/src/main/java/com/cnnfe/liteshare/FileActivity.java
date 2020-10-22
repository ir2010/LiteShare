package com.cnnfe.liteshare;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FileActivity extends AppCompatActivity {

    ArrayList<String> listOfFiles;
    ArrayList<String> listOfIcons;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.files_list);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        FileAdapter adapter = new FileAdapter(listOfIcons , listOfFiles, this);
        recyclerView.setAdapter(adapter);
    }

    ArrayList<String> showAllFiles() {

        File root = new File(Environment.getExternalStorageDirectory().getName());
        ListDir(root, listOfFiles);

        return listOfFiles;
    }

    void ListDir(File root, ArrayList<String> listOfFiles)
    {
        /*File[] files = root.listFiles();
        listOfFiles.clear();

        for(File file: files)
        {
            listOfFiles.add(file.getName());
        }*/

        int i=0;
        File[] files = root.listFiles();
        if (files != null)
        {
            for(File file: files)
            {
                if (file != null)
                {
                    if (file.isDirectory()) {
                        ListDir(file, listOfFiles);
                    } else {
                        listOfFiles.add(file.getName());
                        i++;
                    }
                }
            }
        }
    }
}