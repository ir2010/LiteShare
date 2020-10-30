package com.cnnfe.liteshare.Connect_devices;

import android.app.Application;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.cnnfe.liteshare.R;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private DevicesActivity activity;


    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, DevicesActivity activity)
    {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();

        if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action))
        {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

            if(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED)
            {
                //state = 2
                activity.isWifiP2pEnabled = true;
            }
            else {
                //state = 1
                activity.isWifiP2pEnabled = false;
                activity.resetData();
                //activity.resetData();
            }
            Log.d(DevicesActivity.TAG, "P2P state changed - " + state);
        }
        else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action))
        {
            if (manager != null)
            {
                if (ActivityCompat.checkSelfPermission(activity, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    manager.requestPeers(channel, (WifiP2pManager.PeerListListener) activity.getFragmentManager().findFragmentById(R.id.list_devices));
                }
                else
                {
                    ActivityCompat.requestPermissions(activity, new String[]{ACCESS_FINE_LOCATION}, 1);
                }
            }
            Log.d(DevicesActivity.TAG, "P2P peers changed");
        }
        else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action))
        {
            if (manager == null)
            {
                return;
            }

            Log.d(DevicesActivity.TAG, "connection changed");

            if (isNetworkAvailable(activity.getApplication()))
            {
                DeviceDetailsFragment fragment = (DeviceDetailsFragment) activity.getFragmentManager().findFragmentById(R.id.details_device);
                manager.requestConnectionInfo(channel, fragment);
            }
            else {
                // It's a disconnect
                 activity.resetData();
            }
        }
        else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action))
        {
            DevicesListFragment fragment = (DevicesListFragment) activity.getFragmentManager().findFragmentById(R.id.list_devices);
            fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
        }
    }

    private Boolean isNetworkAvailable(Application application)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null)
                return false;

            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
        }
        else {
            NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
            return nwInfo != null && nwInfo.isConnected();
        }
    }
}
