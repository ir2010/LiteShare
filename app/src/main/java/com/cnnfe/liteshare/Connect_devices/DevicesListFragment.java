package com.cnnfe.liteshare.Connect_devices;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import android.app.Fragment;
import android.app.ListFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cnnfe.liteshare.R;
import com.cnnfe.liteshare.dummy.DummyContent;

import java.util.ArrayList;
import java.util.List;
import com.cnnfe.liteshare.Connect_devices.Helper;

import static com.cnnfe.liteshare.Connect_devices.Helper.getDeviceStatus;

public class DevicesListFragment extends ListFragment implements WifiP2pManager.PeerListListener
{
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    ProgressDialog progressDialog;
    View mContentView;
    private  WifiP2pDevice thisDevice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        this.setListAdapter(new WiFiPeerListAdapter(getActivity(), R.layout.row_peers, peers));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mContentView = inflater.inflate(R.layout.fragment_devices_list, container, false);

        return mContentView;
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        if(progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();

        peers.clear();
        peers.addAll(peerList.getDeviceList());
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        WifiP2pDevice device = (WifiP2pDevice) getListAdapter().getItem(position);
        new DeviceActionListener().showDetails(device);
    }

    public void updateThisDevice(WifiP2pDevice device) {
        this.thisDevice = device;
        TextView view = (TextView) mContentView.findViewById(R.id.my_name);
        view.setText(device.deviceName);
        view = (TextView) mContentView.findViewById(R.id.my_status);
        view.setText(getDeviceStatus(device.status));
    }

    public WifiP2pDevice getDevice() {
        return thisDevice;
    }

    public void clearPeers()
    {
        peers.clear();
        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
    }



}