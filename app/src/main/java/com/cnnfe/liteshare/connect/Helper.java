package com.cnnfe.liteshare.connect;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.provider.OpenableColumns;
import android.util.Log;

import com.cnnfe.liteshare.R;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Random;

public class Helper
{
    static Context context;

    public static String getDeviceStatus(int deviceStatus)
    {
        Log.d(DevicesActivity.TAG, "Peer status :" + deviceStatus);
        switch (deviceStatus)
        {
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";
        }
    }

    public static boolean copyFile(InputStream inputStream, OutputStream outputStream)
    {
        byte buf[] = new byte[1024];
        int len;

        try
        {
            while((len = inputStream.read(buf)) != -1)
            {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        }
        catch (IOException e)
        {
            Log.d(DevicesActivity.TAG, e.toString());
            return false;
        }
        return true;
    }

    public static void preparePacketForServer(ArrayList<String> uriList, String msg, DataOutputStream outputStream)
    {
        try
        {
            writePassword(outputStream);
            writeMsg(msg, outputStream);
            writeFiles(uriList, outputStream);
            outputStream.close();
        }
        catch (IOException e)
        {
            Log.e(DevicesActivity.TAG, e.toString());
        }
    }

    private static void writePassword(DataOutputStream outputStream) throws IOException
    {
        Random random = new Random();
        int password = random.nextInt(8999) + 1000;

        outputStream.writeInt(password);
        outputStream.flush();
    }

    private static void writeMsg(String msg, DataOutputStream outputStream) throws IOException
    {
        outputStream.writeUTF(msg);
        outputStream.flush();
    }

    private static void writeFiles(ArrayList<String> uriList, DataOutputStream outputStream) throws IOException
    {
        //number of files
        outputStream.writeInt(uriList.size());
        outputStream.flush();

        for(int i=0; i<uriList.size(); i++)
        {
            //name of file
            Uri uri = Uri.parse(uriList.get(i));
            String fileName = getNameFromURI(uri);
            outputStream.writeUTF(fileName);
            outputStream.flush();

            //size of file
            Long fileSize = getFileSize(uri);
            outputStream.writeLong(fileSize);
            outputStream.flush();

            //content of file
            byte buf[] = new byte[1024];
            int len;
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            while (fileSize > 0 && (len = inputStream.read(buf, 0, (int)Math.min(buf.length, fileSize))) != -1)
            {
                outputStream.write(buf,0,len);
                fileSize -= len;
            }
            outputStream.flush();
        }
    }

    public static void processPacketAtServer(DataInputStream inputStream)
    {
        try
        {
            int password = inputStream.readInt();
            String msg = inputStream.readUTF();
            receiveFiles(inputStream);
            inputStream.close();
        }
        catch (IOException e)
        {
            Log.e(DevicesActivity.TAG, e.toString());
        }
    }

    private static void receiveFiles(DataInputStream inputStream)throws IOException
    {
        int noOfFiles = inputStream.readInt();

        for(int i=0; i<noOfFiles; i++)
        {
            File f = new File(context.getString(R.string.download_path) + inputStream.readUTF());

            File dirs = new File(f.getParent());
            //if the parent directory doesn't exist already, create
            if(!dirs.exists())
                dirs.mkdirs();
            f.createNewFile();

            Log.d(DevicesActivity.TAG, "Server: Copying files "+ f.toString());

            Long fileSize = inputStream.readLong();

            byte buf[] = new byte[1024];
            int len;
            FileOutputStream outputStream = new FileOutputStream(f);
            while (fileSize > 0 && (len = inputStream.read(buf, 0, (int)Math.min(buf.length, fileSize))) != -1)
            {
                outputStream.write(buf,0,len);
                fileSize -= len;
            }
            outputStream.close();
        }

    }


    private static String getNameFromURI(Uri uri)
    {
        String name;
        Cursor c = context.getContentResolver().query(uri, null, null, null, null);
        c.moveToFirst();
        name = c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME));
        c.close();
        return name;
    }

    private static long getFileSize(Uri uri) {

        Long size;
        Cursor c = context.getContentResolver().query(uri, null, null, null, null);
        int sizeIndex = c.getColumnIndex(OpenableColumns.SIZE);
        c.moveToFirst();
        size = c.getLong(sizeIndex);
        c.close();
        return size;
    }

    public static String getIPFromMAC(String MAC) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {

                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    // Basic sanity check
                    String device = splitted[5];
                    if (device.matches(".*p2p-p2p0.*")){
                        String mac = splitted[3];
                        if (mac.matches(MAC)) {
                            return splitted[0];
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void deletePersistentGroups(WifiP2pManager manager, WifiP2pManager.Channel channel){
        try {
            Method[] methods = WifiP2pManager.class.getMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().equals("deletePersistentGroup")) {
                    // Delete any persistent group
                    for (int netid = 0; netid < 32; netid++) {
                        methods[i].invoke(manager, channel, netid, null);
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
