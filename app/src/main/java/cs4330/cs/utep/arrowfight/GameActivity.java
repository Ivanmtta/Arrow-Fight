package cs4330.cs.utep.arrowfight;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends AppCompatActivity implements ConnectedThread.ConnectedListener {

    private ConnectedThread connectedThread;
    private TextView msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        msg = findViewById(R.id.text);
        connectedThread = new ConnectedThread(this, MainActivity.connectedSocket);
        connectedThread.start();
    }

    @Override
    public void dataReceived(String data) {
        runOnUiThread(()->{
            Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
        });
    }

    public void sendMessage(View view){
        connectedThread.send(msg.getText().toString());
    }
}
