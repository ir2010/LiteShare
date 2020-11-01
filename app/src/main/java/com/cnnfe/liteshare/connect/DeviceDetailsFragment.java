package com.cnnfe.liteshare.connect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import android.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cnnfe.liteshare.R;

//A fragment that manages a particular peer and allows interaction with device i.e. setting up network connection and transferring data.

public class DeviceDetailsFragment extends Fragment implements WifiP2pManager.ConnectionInfoListener, WifiP2pManager.GroupInfoListener{

    private View mContentView = null;
    private WifiP2pDevice selectedDevice;
    private WifiP2pInfo info;
    private WifiP2pGroup group;

    static ProgressDialog progressDialog = null;


    public DeviceDetailsFragment()
    {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        mContentView = inflater.inflate(R.layout.fragment_device_details, container, false);

        /*if(!DevicesActivity.isClient)
        {
            mContentView.findViewById(R.id.btn_send).setVisibility(View.GONE);
        }*/

        mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = selectedDevice.deviceAddress;
                config.wps.setup = WpsInfo.PBC;  // = 0

                if(!DevicesActivity.isClient)
                    config.groupOwnerIntent = 15;
                else
                    config.groupOwnerIntent = 0;

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                progressDialog = ProgressDialog.show(getActivity(), "Connecting to "+ selectedDevice.deviceName, "Press back to cancel", true, true);
                DevicesActivity.deviceActionListener.connect(config);
            }
        });

        mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DevicesActivity.deviceActionListener.disconnect();
            }
        });

        mContentView.findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(DevicesActivity.uriString != "")
                {
                    Uri uri = Uri.parse(DevicesActivity.uriString);
                    sendFile(uri);
                }
            }
        });

        return mContentView;
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info)
    {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();

        this.info = info;
        this.getView().setVisibility(View.VISIBLE);

        // The owner IP is now known.
        TextView view = (TextView) mContentView.findViewById(R.id.is_group_owner);
        view.setText(getResources().getString(R.string.group_owner_text) + ((info.isGroupOwner) ? "yes": "no"));

        // InetAddress from WifiP2pInfo struct.
        view = (TextView) mContentView.findViewById(R.id.group_owner_ip);
        view.setText("Group Owner IP - " + ((info.groupOwnerAddress != null) ? info.groupOwnerAddress.getHostAddress(): "NULL"));

        if(info.groupFormed && !DevicesActivity.isClient)
        {
            new FileServerAsyncTask(getActivity(), mContentView.findViewById(R.id.status_text)).execute();
        }
        else if(info.groupFormed)
        {
            mContentView.findViewById(R.id.btn_send).setVisibility(View.VISIBLE);
            ((TextView) mContentView.findViewById(R.id.status_text)).setText(getResources().getString(R.string.client_text));
        }
        else
        {
            mContentView.findViewById(R.id.btn_send).setVisibility(View.GONE);
            //Toast.makeText(getActivity(), "Not connected", Toast.LENGTH_SHORT).show();
        }

       // mContentView.findViewById(R.id.btn_connect).setEnabled(false);
    }

    public void resetViews()
    {
        //mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
        //mContentView.findViewById(R.id.btn_connect).setEnabled(true);
        this.getView().setVisibility(View.GONE);

        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(R.string.empty);

        view = (TextView) mContentView.findViewById(R.id.group_owner_ip);
        view.setText(R.string.empty);

        view = (TextView) mContentView.findViewById(R.id.is_group_owner);
        view.setText(R.string.empty);

        view = (TextView) mContentView.findViewById(R.id.status_text);
        view.setText(R.string.empty);
    }

    public void showDetails(WifiP2pDevice device)
    {
        this.selectedDevice = device;
        this.getView().setVisibility(View.VISIBLE);

        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(device.deviceAddress);

        view = (TextView) mContentView.findViewById(R.id.group_owner_ip);
        view.setText(device.toString());
    }

    private void sendFile(Uri uri)
    {
        TextView statusText = (TextView) mContentView.findViewById(R.id.status_text);
        statusText.setText("Sending: " + uri);
        Log.d(DevicesActivity.TAG, "Intent----------- " + uri);

        Intent serviceIntent = new Intent(getActivity(), FileTransferService.class);

        serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
        serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS, group.groupOwnerAddress.getHostAddress());

        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);

        FileTransferService.enqueueWork(getActivity(), serviceIntent);
    }

    @Override
    public void onGroupInfoAvailable(WifiP2pGroup group) {

        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();

        this.group = group;
        this.getView().setVisibility(View.VISIBLE);

        // The owner IP is now known.
        TextView view = (TextView) mContentView.findViewById(R.id.is_group_owner);
        view.setText(getResources().getString(R.string.group_owner_text) + ((this.group.isGroupOwner()) ? "yes": "no"));

        // InetAddress from WifiP2pInfo struct.
        view = (TextView) mContentView.findViewById(R.id.group_owner_ip);
        view.setText("Group Owner IP - " + ((this.group.getOwner() != null) ? this.group.getOwner(): "NULL"));

        if(this.group.groupFormed && this.group.isGroupOwner)
        {
            new FileServerAsyncTask(getActivity(), mContentView.findViewById(R.id.status_text)).execute();
        }
        else if(this.group.groupFormed)
        {
            mContentView.findViewById(R.id.btn_send).setVisibility(View.VISIBLE);
            ((TextView) mContentView.findViewById(R.id.status_text)).setText(getResources().getString(R.string.client_text));
        }
        else
        {
            mContentView.findViewById(R.id.btn_send).setVisibility(View.GONE);
            //Toast.makeText(getActivity(), "Not connected", Toast.LENGTH_SHORT).show();
        }
    }
}