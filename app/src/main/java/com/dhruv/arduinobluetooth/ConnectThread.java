package com.dhruv.arduinobluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ConnectThread extends Thread{
    private final BluetoothSocket mmSocket;
    //private final BluetoothDevice mmDevice;
    private final BluetoothAdapter mmAdapter;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public ConnectThread(BluetoothDevice device, BluetoothAdapter mmAdapter) throws IOException {
        this.mmAdapter = mmAdapter;
        BluetoothSocket tmp = null;
       // mmDevice = device;

        try {
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) { }
        mmSocket = tmp;
        mmInStream =mmSocket.getInputStream();
        mmOutStream=mmSocket.getOutputStream();
    }
    public void run() {
        mmAdapter.cancelDiscovery();
        try {

            mmSocket.connect();
            Log.d("Bluetoothconnection", "starting to connect");
        } catch (IOException connectException) {
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }
    }

    public void cancel() {
        try {
            if(mmSocket.isConnected()) {
                mmSocket.close();
            }
        } catch (IOException e) { }
    }
    public void write(String data) {
        try {
            Log.d("TAG", "write: " +data);
            mmOutStream.write(data.getBytes());
        } catch (IOException e) { }
    }
}
