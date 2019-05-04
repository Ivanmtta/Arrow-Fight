package cs4330.cs.utep.eggthrower.Game;

import android.app.Activity;
import android.os.Bundle;

import cs4330.cs.utep.eggthrower.BluetoothService.ConnectedThread;

/**
 * Second screen of the application that contains the game screen.
 */
public class GameActivity extends Activity implements ConnectedThread.ConnectedListener {

    /* Surface view that contains the came */
    private GameView gameView;

    /**
     * Method that will be invoked when the activity is created.
     *
     * @param savedInstanceState last saved instance.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Initialize surfaceView and set it as the view */
        gameView = new GameView(this);
        setContentView(gameView);
    }

    /**
     * This method will be called when the linked socket sends a message.
     *
     * @param data Data send from the other socket
     */
    @Override
    public void dataReceived(String data) {
        gameView.dataReceived(data);
    }
}