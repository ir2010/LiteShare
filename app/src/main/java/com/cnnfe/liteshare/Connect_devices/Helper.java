package com.cnnfe.liteshare.Connect_devices;

import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;

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
}
