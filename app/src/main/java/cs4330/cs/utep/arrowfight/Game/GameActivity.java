package cs4330.cs.utep.arrowfight.Game;

import android.app.Activity;
import android.os.Bundle;

import cs4330.cs.utep.arrowfight.ConnectedThread;

public class GameActivity extends Activity implements ConnectedThread.ConnectedListener {

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameView = new GameView(this);
        setContentView(gameView);
    }

    @Override
    public void dataReceived(String data) {
        gameView.dataReceived(data);
    }
}
