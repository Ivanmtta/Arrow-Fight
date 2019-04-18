package cs4330.cs.utep.arrowfight.Game;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import cs4330.cs.utep.arrowfight.ConnectedThread;
import cs4330.cs.utep.arrowfight.MainActivity;
import cs4330.cs.utep.arrowfight.R;

public class GameActivity extends Activity implements ConnectedThread.ConnectedListener {

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        gameView = new GameView(this);
        setContentView(gameView);
    }

    @Override
    public void dataReceived(String data) {
        gameView.dataReceived(data);
    }
}
