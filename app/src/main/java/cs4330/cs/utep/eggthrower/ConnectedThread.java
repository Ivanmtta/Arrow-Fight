package cs4330.cs.utep.eggthrower;

import android.bluetooth.BluetoothSocket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import android.content.Context;

/**
 * This class is used to
 */
public class ConnectedThread extends Thread{

    private PrintWriter output;
    private BufferedReader input;
    private ConnectedListener listener;

    /**
     * Constructor used to initialize a connected thread.
     * @param context Reference to the context of the MainActivity to link listener.
     * @param socket Connected socket
     */
    public ConnectedThread(Context context, BluetoothSocket socket){
        listener = (ConnectedListener) context;
        try {
            /* Get the input and aoutput stream of the socket */
            output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        /* Error getting input/output streams */
        catch(Exception error) {
            error.printStackTrace();
        }
    }

    /**
     * Method that will run in the background thread used to always be listening
     * to messages from the other connected thread.
     */
    public void run() {
        while(true){
            String message;
            try {
                /* Read a message from the input stream */
                message = input.readLine();
            }
            catch(Exception error) {
                error.printStackTrace();
                break;
            }
            /* If the onther connected thread send a message */
            if(message != null){
                /* Invoke dataReicive method from context */
                listener.dataReceived(message);
            }
        }
    }

    /**
     * Method used to send a message to the other connected thread.
     * @param message The message that will be send
     */
    public void send(String message){
        output.println(message);
        output.flush();
    }

    /**
     * Listener used to make callbacks to the context.
     */
    public interface ConnectedListener{
        void dataReceived(String data);
    }
}