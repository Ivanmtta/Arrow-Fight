package cs4330.cs.utep.arrowfight;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

public class ServerThread extends Thread {

    private BluetoothServerSocket serverSocket;
    private ServerListener listener;

    public ServerThread(Context context, BluetoothAdapter bluetoothAdapter) {
        listener = (ServerListener) context;
        try {
            serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(MainActivity.APP_NAME, MainActivity.APP_UUID);
        }
        catch(Exception error) {
            error.printStackTrace();
        }
    }

    public void run() {
        BluetoothSocket socket;
        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (Exception error) {
                error.printStackTrace();
                break;
            }
            if(socket != null){
                try {
                    manageConnectedSocket(socket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void manageConnectedSocket(BluetoothSocket socket){
        listener.initializeConnectedThread(socket);
    }

    public interface ServerListener{
        void initializeConnectedThread(BluetoothSocket socket);
    }
}