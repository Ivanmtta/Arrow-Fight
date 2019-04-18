package cs4330.cs.utep.arrowfight;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

/**
 * Class used to initialize a server socket and start listening for client
 * sockets to connect to the server.
 */
public class ServerThread extends Thread {

    /* Server socket used to listen for client */
    private BluetoothServerSocket serverSocket;
    /* Listener to notify MainActivity when two sockets are connected */
    private ServerListener listener;

    /**
     * Constructor used to initialize server thread to start listening
     * to client sockets.
     * @param context Reference to the context of the MainActivity to link listener.
     * @param bluetoothAdapter Local device bluetooth adapter
     */
    public ServerThread(Context context, BluetoothAdapter bluetoothAdapter) {
        listener = (ServerListener) context;
        try {
            /* Initialize server socket by using an universal unique identifier */
            serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(MainActivity.APP_NAME, MainActivity.APP_UUID);
        }
        catch(Exception error) {
            error.printStackTrace();
        }
    }

    /**
     * Method that will be waiting for a client socket to connect to
     * the server socket, then it will initialize a connected thread
     * in the MainActivity.
     */
    public void run() {
        BluetoothSocket socket;
        /* Keep listening until exception occurs or a socket is returned */
        while (true) {
            try {
                /* Accept any sockets connecting to the UUID */
                socket = serverSocket.accept();
            } catch (Exception error) {
                error.printStackTrace();
                break;
            }
            /* If a client socket connected */
            if(socket != null){
                try {
                    /* Initialize a connected thread in the MainActivity */
                    listener.initializeConnectedThread(socket);
                }
                catch(Exception error) {
                    error.printStackTrace();
                }
            }
        }
    }

    /**
     * Listener used to make callbacks to the MainActiviry.
     */
    public interface ServerListener{
        void initializeConnectedThread(BluetoothSocket socket);
    }
}