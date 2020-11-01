package com.cnnfe.liteshare.Connect_devices;

import android.app.ProgressDialog;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cnnfe.liteshare.R;

//A fragment that manages a particular peer and allows interaction with device i.e. setting up network connection and transferring data.

public class DeviceDetailsFragment extends Fragment implements WifiP2pManager.ConnectionInfoListener {

    private View mContentView = null;
    private WifiP2pDevice selectedDevice;
    private WifiP2pInfo info;


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

                ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Connecting to "+ selectedDevice.deviceName, "Press back to cancel", true, true);
                new DeviceActionListener().connect(config);
            }
        });

        mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DeviceActionListener().disconnect();
            }
        });

        return mContentView;
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info)
    {

    }

    public void resetViews()
    {
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);

        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(R.string.empty);

        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(R.string.empty);

        view = (TextView) mContentView.findViewById(R.id.group_owner);
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

        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(device.toString());
    }
}