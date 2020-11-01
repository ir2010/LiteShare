package com.cnnfe.liteshare.filepicker;

import android.app.Activity;
import android.app.ListActivity;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.cnnfe.liteshare.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AppShow extends ListActivity
{
    private PackageManager packageManager = null;
    private List<ApplicationInfo> applist = null;
    private AppAdapter listadapter = null;
    public ArrayList<String> uriList = new ArrayList<String>();
    Button selectButton;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_show);

        selectButton = (Button) findViewById(R.id.select_apps);

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent();
                intent.putStringArrayListExtra("apps", uriList);
                Toast.makeText(AppShow.this, "done", Toast.LENGTH_SHORT).show();
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        //uriList = FileActivity.stringUriList;

        packageManager = getPackageManager();
        new LoadApplications().execute();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ApplicationInfo app = applist.get(position);

        try {

            //Intent intent = packageManager.getLaunchIntentForPackage(app.packageName);

            /*if (intent != null) {

                startActivity(intent);

            }*/

            Uri uri;
            //Toast.makeText(this, app.sourceDir, Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.N) {
                //Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                uri = Uri.parse(app.sourceDir);
                uriList.add(uri.toString());
                Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                uri = Uri.fromFile(new File(app.sourceDir));
                uriList.add(uri.toString());
                Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show();
            }


        } catch (ActivityNotFoundException e) {

            Toast.makeText(AppShow.this, e.getMessage(), Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(AppShow.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();

        for (ApplicationInfo info : list) {

            try {

                if (packageManager.getLaunchIntentForPackage(info.packageName) != null) {
                    applist.add(info);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return applist;
    }

    private class LoadApplications extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progress = null;

        @Override
        protected Void doInBackground(Void... params) {

            applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));

            listadapter = new AppAdapter(AppShow.this, R.layout.list_item, applist);
            return null;
        }

        @Override

        protected void onPostExecute(Void result) {
            setListAdapter(listadapter);
            progress.dismiss();
            super.onPostExecute(result);

        }

        @Override
        protected void onPreExecute() {

            progress = ProgressDialog.show(AppShow.this, null, "Loading apps info...");
            super.onPreExecute();
        }
    }

}