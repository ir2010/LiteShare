package com.cnnfe.liteshare.connect;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cnnfe.liteshare.R;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
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
    Context context;
    static String msg;
    static int passwordAtClient;
    int passwordAtServer;

    public Helper(Context context) {
        this.context = context;
    }

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

    public void preparePacketForServer(ArrayList<String> uriList, String msg, DataOutputStream outputStream)
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

    private void writePassword(DataOutputStream outputStream) throws IOException
    {
        Random random = new Random();
        passwordAtClient = random.nextInt(8999) + 1000;
        //createPassword();

        outputStream.writeInt(passwordAtClient);
        outputStream.flush();
    }

    private static void writeMsg(String msg, DataOutputStream outputStream) throws IOException
    {
        outputStream.writeUTF(msg);
        outputStream.flush();
    }

    private void writeFiles(ArrayList<String> uriList, DataOutputStream outputStream) throws IOException
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

    public boolean processPacketAtServer(int passwor, String ms, DataInputStream inputStream)
    {
        try
        {
            passwordAtServer = inputStream.readInt();
            //if(checkPassword()){
            msg = inputStream.readUTF();
            Log.d(DevicesActivity.TAG, passwordAtServer + " "+msg);
            boolean res = receiveFiles(inputStream);
            inputStream.close();
            return res;
        //}
        }
        catch (IOException e)
        {
            Log.e(DevicesActivity.TAG, e.toString());
            return false;
        }
    }

    private boolean receiveFiles(DataInputStream inputStream)throws IOException
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
        return noOfFiles != 0;
    }

    public String getNameFromURI(Uri uri)
    {
        try{
        String name;
        Cursor c = context.getContentResolver().query(uri, null, null, null, null);
        c.moveToFirst();
        name = c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME));
        c.close();
        return name;}
        catch (Exception e)
        {
            return "";
        }
    }

    private long getFileSize(Uri uri) {

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

    public boolean checkPassword()
    {
        boolean matched = false;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage("Enter Password");

        final EditText input = new EditText(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setIcon(R.drawable.ic_baseline_vpn_key_24);

        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String password = input.getText().toString();
                        if (password.compareTo("") == 0) {
                            if (passwordAtServer == Integer.valueOf(password)) {
                                input.setTextColor(Color.GREEN);
                                Toast.makeText(context,
                                        "Password Matched", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            } else {
                                input.setTextColor(Color.RED);
                                Toast.makeText(context,
                                        "Wrong Password!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
        String password = input.getText().toString();

        return password.equals("") ? false: passwordAtServer == Integer.valueOf(password);
    }

    public void createPassword()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(passwordAtClient)
                .setTitle("Password is: ");
        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
