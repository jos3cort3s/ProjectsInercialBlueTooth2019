package com.jos3ocrt3s.appbluetoothme.adapters;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.jos3ocrt3s.appbluetoothme.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterList extends BaseAdapter {

    private List<BluetoothDevice> mListBluetoothDevices;
    private Context mContext;

    public AdapterList( Context mContext, List<BluetoothDevice> mListBluetoothDevice) {
        super();
        this.mListBluetoothDevices = mListBluetoothDevice;
        this.mContext = mContext;
    }

   /* public void addDevice(BluetoothDevice device) {
        if(!mListBLE.contains(device)) {
            mListBLE.add(device);
        }
    }

    public BluetoothDevice getDevice(int position) {
        return mListBLE.get(position);
    }*/

    @Override
    public int getCount() {
        return mListBluetoothDevices.size() ;
    }

    @Override
    public Object getItem(int i) {
        return mListBluetoothDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = new ViewHolder();
        // General ListView optimization code.
        if (view == null) {
            view = View.inflate(mContext, R.layout.layout_custom_list, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceName = view.findViewById(R.id.tv_nameDevice);
            viewHolder.deviceAddress = view.findViewById(R.id.tv_addressDevice);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        BluetoothDevice device = mListBluetoothDevices.get(i);
        final String deviceName = device.getName();
        if (deviceName != null && deviceName.length() > 0) {
            viewHolder.deviceName.setText(device.getName());
            viewHolder.deviceAddress.setText(device.getAddress());
        } else {
            viewHolder.deviceName.setText("Uknow name device");
            viewHolder.deviceAddress.setText("Uknow address device");
        }
        return view;
    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
}
