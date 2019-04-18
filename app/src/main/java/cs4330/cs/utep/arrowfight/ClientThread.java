package cs4330.cs.utep.arrowfight;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

public class ClientThread extends Thread {

    private BluetoothSocket socket;
    private BluetoothAdapter bluetoothAdapter;
    public ClientListener listener;

    public ClientThread(Context context, BluetoothDevice device, BluetoothAdapter bluetoothAdapter){
        this.bluetoothAdapter = bluetoothAdapter;
        listener = (ClientListener) context;
        try{
            socket = device.createRfcommSocketToServiceRecord(MainActivity.APP_UUID);
        }
        catch(Exception error){
            error.printStackTrace();
        }
    }

    public void run(){
        bluetoothAdapter.cancelDiscovery();
        try{
            socket.connect();
        }
        catch(Exception error){
            error.printStackTrace();
            try {
                socket.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        manageConnectedSocket(socket);
    }

    public void manageConnectedSocket(BluetoothSocket socket){
        listener.initializeConnectedThread(socket);
    }

    public interface ClientListener{
        void initializeConnectedThread(BluetoothSocket socket);
    }
}