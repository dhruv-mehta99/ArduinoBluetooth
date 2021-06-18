package com.dhruv.arduinobluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class BroadcastReceiver_Bluetooth extends BroadcastReceiver {
    Context activityContext;
    BluetoothHome bluetoothHome;
    BluetoothController bluetoothController;
    public BroadcastReceiver_Bluetooth(Context activityContext, BluetoothHome mbluetoothHome) {
        this.activityContext = activityContext;
        bluetoothHome=mbluetoothHome;
    }
    public BroadcastReceiver_Bluetooth(Context activityContext, BluetoothController bluetoothController) {
        this.activityContext = activityContext;
        this.bluetoothController=bluetoothController;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if(BluetoothDevice.ACTION_FOUND.equals(action)){
            BluetoothDevice device ;
            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            // Create a new device item
            BTLE_Device newDevice = new BTLE_Device(device);
            bluetoothHome.addDevice(device,BluetoothHome.AVAILABLE_DEVICE_LIST);
            // Add it to our adapter
            Log.d("DEVICELIST", "Bluetooth device found " + device.getName());
        }
        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            //Do something if connected
            Toast.makeText(bluetoothController.getApplicationContext(), "BT Connected", Toast.LENGTH_SHORT).show();
            bluetoothController.findViewById(R.id.mainlayout).setVisibility(View.VISIBLE);
            bluetoothController.findViewById(R.id.progress_circular).setVisibility(View.GONE);
            bluetoothController.findViewById(R.id.progress_circular).setActivated(false);

        }
        else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            //Do something if disconnected
            Toast.makeText(activityContext, "BT Disconnected", Toast.LENGTH_SHORT).show();

        }
        else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    BluetoothUtils.toast(activityContext, "Bluetooth is off");
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    BluetoothUtils.toast(activityContext, "Bluetooth is turning off...");
                    break;
                case BluetoothAdapter.STATE_ON:
                    BluetoothUtils.toast(activityContext, "Bluetooth is on");
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    BluetoothUtils.toast(activityContext, "Bluetooth is turning on...");
                    break;
            }
        }
    }
}
