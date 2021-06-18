package com.dhruv.arduinobluetooth;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.Set;

public class Scanner_BTLE {
    private BluetoothHome bluetoothHome;
    private BluetoothAdapter mbluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private long scanPeriod;

    public Scanner_BTLE(BluetoothHome bluetoothHome, long scanPeriod){
        this.bluetoothHome = bluetoothHome;
        mHandler = new Handler();
        this.scanPeriod=scanPeriod;
        final BluetoothManager bluetoothManager = (BluetoothManager) bluetoothHome.getSystemService(Context.BLUETOOTH_SERVICE);
        mbluetoothAdapter = bluetoothManager.getAdapter();

    }


    public boolean isScanning(){
        return mScanning;
    }

    public void start(){
        if(!BluetoothUtils.checkBluetooth(mbluetoothAdapter)){
            BluetoothUtils.requestUserBluetooth(bluetoothHome);
        }
        else{
          scanLeDevice(true);
        }
    }
    public void stop(){
        scanLeDevice(false);
    }
    // If you want to scan for only specific types of peripherals,
    // you can instead call startLeScan(UUID[], BluetoothAdapter.LeScanCallback),
    // providing an array of UUID objects that specify the GATT services your app supports.

    private void scanLeDevice(final boolean enable){
        if(enable && !mScanning) {
            BluetoothUtils.toast(bluetoothHome.getApplication(), "Scanning for bluetooth");

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    BluetoothUtils.toast(bluetoothHome.getApplication(),"STOPPING SCAN");
                    mScanning=false;
                    final boolean b = mbluetoothAdapter.cancelDiscovery();
                    Log.d("STOPSCAN", "bluetooth cancel discovery is " + b);
                    bluetoothHome.stopScan();

                }
            },scanPeriod);
            mScanning=true;
            final boolean b = mbluetoothAdapter.startDiscovery();
            Log.d("STARTSCAN", "bluetooth discovery is " + b);
        }else{
            if(mbluetoothAdapter.isDiscovering()){
                mScanning = false;
                final boolean b = mbluetoothAdapter.cancelDiscovery();
                Log.d("STOPSCAN", "bluetooth cancel discovery is " + b);
            }
        }
    }

    public void findPairedDevices() {
        Set<BluetoothDevice> pairedDevices = mbluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                bluetoothHome.addDevice(device,BluetoothHome.PAIRED_DEVICE_LIST);
            }
        }
    }
}
