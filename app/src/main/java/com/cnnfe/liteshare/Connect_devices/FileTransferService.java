package com.cnnfe.liteshare.Connect_devices;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class FileTransferService extends JobIntentService
{
    private static final int SOCKET_TIMEOUT = 5000;
    public static final String ACTION_SEND_FILE = "com.cnnfe.liteshare.SEND_FILE";
    public static final String EXTRAS_FILE_PATH = "file_url";
    public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
    public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";

    public FileTransferService()
    {
        super();
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        Context context = getApplicationContext();
        if(intent.getAction().equals(ACTION_SEND_FILE))
        {
            String fileUri = intent.getExtras().getString(EXTRAS_FILE_PATH);
            String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);

            Socket socket = new Socket();
            int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);

            try {
                Log.d(DevicesActivity.TAG, "Opening client socket - ");
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);

                Log.d(DevicesActivity.TAG, "Client socket - " + socket.isConnected());

                OutputStream outputStream = socket.getOutputStream();
                ContentResolver contentResolver = context.getContentResolver();
                InputStream inputStream = null;

                try {
                    inputStream = contentResolver.openInputStream(Uri.parse(fileUri));
                } catch (FileNotFoundException e) {
                    Log.d(DevicesActivity.TAG, e.toString());
                }

                DeviceDetailsFragment.copyFile(inputStream, outputStream);
                Log.d(DevicesActivity.TAG, "Client: Data written");
            }
            catch (IOException e)
            {
                Log.e(DevicesActivity.TAG, e.getMessage());
            }
            finally {
                if(socket != null)
                {
                    if(socket.isConnected())
                    {
                        try
                        {
                            socket.close();
                        }
                        catch(IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public  static void enqueueWork(Context context, Intent intent)
    {
        enqueueWork(context, FileTransferService.class, 1, intent);
    }
}