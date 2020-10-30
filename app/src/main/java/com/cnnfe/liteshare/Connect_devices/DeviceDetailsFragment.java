package com.cnnfe.liteshare.Connect_devices;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import android.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cnnfe.liteshare.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

//A fragment that manages a particular peer and allows interaction with device i.e. setting up network connection and transferring data.

public class DeviceDetailsFragment extends Fragment implements WifiP2pManager.ConnectionInfoListener {

    private View mContentView = null;
    private WifiP2pDevice selectedDevice;
    private WifiP2pInfo info;

    ProgressDialog progressDialog = null;


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

        mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = selectedDevice.deviceAddress;
                config.wps.setup = WpsInfo.PBC;  // = 0

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
                Uri uri = Uri.parse(getActivity().getIntent().getExtras().getString("fileUri"));
                sendFile(uri);
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

        if(info.groupFormed && info.isGroupOwner)
        {
            new FileServerAsyncTask(getActivity(), mContentView.findViewById(R.id.status_text)).execute();
        }
        else if(info.groupFormed)
        {
            ((TextView) mContentView.findViewById(R.id.status_text)).setText(getResources().getString(R.string.client_text));
        }
        else
        {
            Toast.makeText(getActivity(), "Not connected", Toast.LENGTH_SHORT).show();
        }

        mContentView.findViewById(R.id.btn_connect).setEnabled(false);
    }

    public void resetViews()
    {
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
        mContentView.findViewById(R.id.btn_connect).setEnabled(true);

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
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS, info.groupOwnerAddress.getHostAddress());

        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);

        FileTransferService.enqueueWork(getActivity(), serviceIntent);
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
}