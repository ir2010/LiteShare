package com.cnnfe.liteshare.Connect_devices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.cnnfe.liteshare.R;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class DevicesActivity extends AppCompatActivity implements WifiP2pManager.ChannelListener
{
    public static final String TAG = "liteshare";

    private WifiP2pManager manager;                    //has methods that allow discover, request, and connect to peers
    private WifiP2pManager.Channel channel;            //to connect the application to the Wi-Fi P2P framework
    private BroadcastReceiver receiver;                //notifies of important Wi-Fi p2p events
    public static boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;

    public static DeviceActionListener deviceActionListener;
    private DevicesListFragment fragmentList;
    private DeviceDetailsFragment fragmentDetails;

    private final IntentFilter intentFilter = new IntentFilter();     //intents that notify of specific events detected by the Wi-Fi P2P framework
    String[] permissions = {ACCESS_FINE_LOCATION, WRITE_EXTERNAL_STORAGE, ACCESS_WIFI_STATE};     //permissions required for the app

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        //check and request for permissions
        if (!checkForPermissions(this, permissions))
        {
            ActivityCompat.requestPermissions(this, permissions, 1);
        }

        fragmentList = (DevicesListFragment) getFragmentManager().findFragmentById(R.id.list_devices);
        fragmentDetails = (DeviceDetailsFragment) getFragmentManager().findFragmentById(R.id.details_device);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);

        deviceActionListener = new DeviceActionListener(manager, channel, fragmentDetails, fragmentList, getApplicationContext());

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.turn_on_wifi:
                if (manager != null && channel != null) {
                    //open the wireless settings screen of the device for the user to turn on wifi. We will be notified by the WiFiDeviceBroadcastReceiver instead
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                } else {
                    Log.e(TAG, "channel or manager is null");
                }
                return true;

            case R.id.discover_peers:
                if (!isWifiP2pEnabled) {
                    //wifi not enabled
                    Toast.makeText(DevicesActivity.this, R.string.p2p_off_warning, Toast.LENGTH_SHORT).show();
                    return true;
                }

                ProgressDialog progressDialog =
                        ProgressDialog.show(this, "Press back to cancel", "Finding Peers...", true,
                                true, new DialogInterface.OnCancelListener() {

                                    @Override
                                    public void onCancel(DialogInterface dialog) {

                                    }
                                });

                if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

                        @Override
                        public void onSuccess() {
                            Toast.makeText(DevicesActivity.this, "Discovery Initiated",
                                    Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int reasonCode) {
                            Toast.makeText(DevicesActivity.this, "Discovery Failed : " + reasonCode,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void resetData()
    {
        if (fragmentList != null)
        {
            fragmentList.clearPeers();
        }
        if (fragmentDetails != null)
        {
            fragmentDetails.resetViews();
        }
    }



    //to check for the required permissions
    private boolean checkForPermissions(Context context, String... permissions)
    {
        if (context != null && permissions != null)
        {
            for (String permission : permissions)
            {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                {
                    return false;
                }
            }
        }
        return true;
    }

    //handling the case when permissions denied
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int grantResult: grantResults)
        {
            if(grantResult == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show();
            else
            {
                //disable all the buttons
                MenuItem discoverButton = (MenuItem) findViewById(R.id.discover_peers);
                discoverButton.setEnabled(false);
            }
        }
    }

    @Override
    public void onChannelDisconnected() {

        if (manager != null && !retryChannel)
        {
            Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
            resetData();
            retryChannel = true;
            manager.initialize(this, getMainLooper(), this);
        } else
            {
            Toast.makeText(this, "Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.", Toast.LENGTH_LONG).show();
        }
    }
}