package com.cnnfe.liteshare.Connect_devices;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cnnfe.liteshare.R;

import java.util.List;

public class WiFiPeerListAdapter extends ArrayAdapter<WifiP2pDevice>
{
    private List<WifiP2pDevice> items;
    Context context;

    public WiFiPeerListAdapter(@NonNull Context context, int resource, List<WifiP2pDevice> items)
    {
        super(context, resource, items);
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View v = convertView;
        if(v == null)
        {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.row_peers, null);
        }

        WifiP2pDevice device = items.get(position);
        if (device != null)
        {
            TextView top = (TextView) v.findViewById(R.id.device_name);
            TextView bottom = (TextView) v.findViewById(R.id.device_details);
            if (top != null)
            {
                top.setText(device.deviceName);
            }
            if (bottom != null) {
                bottom.setText(Helper.getDeviceStatus(device.status));
            }
        }

        return v;
    }
}
