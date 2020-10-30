package com.cnnfe.liteshare.Connect_devices;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class DeviceActionListener
{
    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    DeviceDetailsFragment detailsFragment;
    DevicesListFragment listFragment;
    Context context;

    public DeviceActionListener() {
    }

    public DeviceActionListener(WifiP2pManager manager, WifiP2pManager.Channel channel, DeviceDetailsFragment detailsFragment, DevicesListFragment listFragment, Context context) {
        this.manager = manager;
        this.channel = channel;
        this.detailsFragment = detailsFragment;
        this.listFragment = listFragment;
        this.context = context;
    }

    public void showDetails(WifiP2pDevice device)
    {
        detailsFragment.showDetails(device);
    }

    public void connect(WifiP2pConfig config)
    {
        if (ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            manager.connect(channel, config, new WifiP2pManager.ActionListener()
            {
                @Override
                public void onSuccess()
                {
                    Toast.makeText(context, "Connection started", Toast.LENGTH_SHORT).show();
                    // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
                }

                @Override
                public void onFailure(int reason)
                {
                    Toast.makeText(context, "Connect failed. Retry.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void disconnect()
    {
        detailsFragment.resetViews();
        manager.removeGroup(channel, new WifiP2pManager.ActionListener()
        {
            @Override
            public void onFailure(int reasonCode) {
                Log.d(DevicesActivity.TAG, "Disconnect failed. Reason :" + reasonCode);
            }

            @Override
            public void onSuccess() {
                detailsFragment.getView().setVisibility(View.GONE);
            }
        });
    }

    public void cancelDisconnect()
    {
        /*
         * A cancel abort request by user. Disconnect i.e. removeGroup if
         * already connected. Else, request WifiP2pManager to abort the ongoing
         * request
         */
        if (manager != null)
        {
            if (listFragment.getDevice() == null || listFragment.getDevice().status == WifiP2pDevice.CONNECTED)
            {
                disconnect();
            } else if (listFragment.getDevice().status == WifiP2pDevice.AVAILABLE || listFragment.getDevice().status == WifiP2pDevice.INVITED)
            {
                manager.cancelConnect(channel, new WifiP2pManager.ActionListener()
                {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(context, "Aborting connection",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(context, "Connect abort request failed. Reason Code: " + reasonCode, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
