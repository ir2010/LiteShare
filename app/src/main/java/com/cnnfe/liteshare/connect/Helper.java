package com.cnnfe.liteshare.connect;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

public class Helper
{
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
