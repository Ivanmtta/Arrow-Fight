package cs4330.cs.utep.eggthrower;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

/**
 * Class used to initialize a client socket that will be later connected
 * to a server socket.
 */
public class ClientThread extends Thread {

    private BluetoothSocket socket;
    private BluetoothAdapter bluetoothAdapter;
    public ClientListener listener;

    /**
     * Constructor used to initialize the client socket.
     * @param context Reference to the context of the MainActivity to link listener
     * @param device Device that we are trying to connect to
     * @param bluetoothAdapter Local device bluetooth adapter
     */
    public ClientThread(Context context, BluetoothDevice device, BluetoothAdapter bluetoothAdapter){
        this.bluetoothAdapter = bluetoothAdapter;
        listener = (ClientListener) context;
        try{
            /* Get a BluetoothSocket to connect with the given BluetoothDevice */
            socket = device.createRfcommSocketToServiceRecord(MainActivity.APP_UUID);
        }
        catch(Exception error){
            error.printStackTrace();
        }
    }

    /**
     * Method that will be waiting until the client is able to
     * connect to the other bluetooth capable device.
     */
    public void run(){
        /* Cancel discovery because it otherwise slows down the connection */
        bluetoothAdapter.cancelDiscovery();
        try{
            /* Connect ti the remote device */
            socket.connect();
        }
        catch(Exception error){
            error.printStackTrace();
            /* Unable to connect to the device */
            try {
                socket.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        /* Initialize a connected thread in the MainActivity */
        listener.initializeConnectedThread(socket);
    }

    /**
     * Listener used to make callbacks to the MainActiviry.
     */
    public interface ClientListener{
        void initializeConnectedThread(BluetoothSocket socket);
    }
}