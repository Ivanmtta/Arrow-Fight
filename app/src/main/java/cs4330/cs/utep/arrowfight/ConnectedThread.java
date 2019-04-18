package cs4330.cs.utep.arrowfight;

import android.bluetooth.BluetoothSocket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import android.content.Context;

public class ConnectedThread extends Thread{

    private PrintWriter output;
    private BufferedReader input;
    private ConnectedListener listener;

    public ConnectedThread(Context context, BluetoothSocket socket){
        listener = (ConnectedListener) context;
        try {
            output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch(Exception error) {
            error.printStackTrace();
        }
    }

    public void run() {
        while(true){
            String message;
            try {
                message = input.readLine();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
            if(message != null){
                listener.dataReceived(message);
            }
        }
    }

    public void send(String message){
        output.println(message);
        output.flush();
    }

    public interface ConnectedListener{
        void dataReceived(String data);
    }
}