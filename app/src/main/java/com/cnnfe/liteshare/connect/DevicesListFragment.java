package com.cnnfe.liteshare.connect;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import android.app.ListFragment;

import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cnnfe.liteshare.R;
import java.util.ArrayList;
import java.util.List;

import static com.cnnfe.liteshare.connect.Helper.getDeviceStatus;

public class DevicesListFragment extends ListFragment implements WifiP2pManager.PeerListListener {
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    ProgressDialog progressDialog = null;
    View mContentView;
    private static WifiP2pDevice thisDevice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.setListAdapter(new WiFiPeerListAdapter(getActivity(), R.layout.row_peers, peers));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_devices_list, container, false);

        return mContentView;
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList)
    {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();

        peers.clear();
        peers.addAll(peerList.getDeviceList());

        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
        if(peers.size() == 0)
        {
            Toast.makeText(getActivity(), "No Devices Found!", Toast.LENGTH_SHORT).show();
            Log.d(DevicesActivity.TAG, "No Devices Found!");
            return;
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        WifiP2pDevice device = (WifiP2pDevice) getListAdapter().getItem(position);
        DevicesActivity.deviceActionListener.showDetails(device);
    }

    public void updateThisDevice(WifiP2pDevice device) {
        this.thisDevice = device;
        TextView view = (TextView) mContentView.findViewById(R.id.my_name);
        view.setText(device.deviceName);
        view = (TextView) mContentView.findViewById(R.id.my_status);
        view.setText(getDeviceStatus(device.status));
    }

    public static WifiP2pDevice getDevice() {
        return thisDevice;
    }

    public void clearPeers() {
        peers.clear();
        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
    }

    public void onInitiateDiscovery()
    {
        if (progressDialog != null && progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(getActivity(), "Finding peers...", "Press back to cancel", true,
                true, new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {

                    }
                });
    }
}