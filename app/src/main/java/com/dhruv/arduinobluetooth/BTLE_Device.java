package com.dhruv.arduinobluetooth;

import android.bluetooth.BluetoothDevice;

public class BTLE_Device {

    private BluetoothDevice bluetoothDevice;
    private int rssi;

    public BTLE_Device(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public String getAddress() {
        return bluetoothDevice.getAddress();
    }

    public String getName() {
        return bluetoothDevice.getName();
    }

    public BluetoothDevice getBluetoothDevice() {return bluetoothDevice;}

}
