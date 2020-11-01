package com.cnnfe.liteshare.connect;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.cnnfe.liteshare.MainActivity;
import com.cnnfe.liteshare.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServerAsyncTask extends AsyncTask<Void, Void, String>
{
    private Context context;
    private TextView statusText;

    public FileServerAsyncTask(Context context, TextView statusText)
    {
        this.context = context;
        this.statusText = statusText;
    }

    @Override
    protected String doInBackground(Void... voids)
    {
        try
        {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(8988));
            Log.d(DevicesActivity.TAG, "Server: Socket opened");

            Socket client = serverSocket.accept();
            Log.d(DevicesActivity.TAG, "Server: Connection done");

            //creating the file where downloaded file will be stored
            final File f = new File(context.getString(R.string.download_path) + System.currentTimeMillis() + "." + DevicesActivity.fileExtension);

            File dirs = new File(f.getParent());
            //if the parent directory doesn't exist already, create
            if(!dirs.exists())
                dirs.mkdirs();
            f.createNewFile();

            Log.d(DevicesActivity.TAG, "Server: Copying files "+ f.toString());
            InputStream inputStream = client.getInputStream();

            Helper.copyFile(inputStream, new FileOutputStream(f));
            serverSocket.close();
            return f.getAbsolutePath();
        }
        catch (IOException e)
        {
            Log.e(DevicesActivity.TAG, e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        if (s != null)
        {
            Toast.makeText(context, "File received!", Toast.LENGTH_SHORT).show();
            Intent showFileIntent = new Intent();
            showFileIntent.setAction(Intent.ACTION_VIEW);

            Uri uri = FileProvider.getUriForFile(context, "com.cnnfe.liteshare.provider", new File(s));
            String mimeType = context.getContentResolver().getType(uri);
            showFileIntent.setDataAndType(uri, mimeType);
            showFileIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(showFileIntent);
            context.startActivity(new Intent(context, MainActivity.class));
        }
    }
}
