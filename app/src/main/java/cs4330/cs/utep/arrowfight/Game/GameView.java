package cs4330.cs.utep.arrowfight.Game;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import cs4330.cs.utep.arrowfight.ConnectedThread;
import cs4330.cs.utep.arrowfight.MainActivity;
import cs4330.cs.utep.arrowfight.R;

public class GameView extends SurfaceView implements SurfaceHolder.Callback{

    private MainGameThread gameThread;
    public static float WIDTH;
    public static float HEIGHT;

    private Paint paint;
    private final int WHITE = Color.rgb(240, 240, 242);
    private final int RED = Color.rgb(255, 161, 143);
    private final int BLUE = Color.rgb(153, 182, 255);

    private Slingshot slingshot;

    private ConnectedThread connectedThread;

    public GameView(Context context){
        super(context);
        /* Set callback to the surfaceholder to track events */
        getHolder().addCallback(this);
        gameThread = new MainGameThread(getHolder(), this);
        /* Make window focusable so it can handle touch events */
        setFocusable(true);
        connectedThread = new ConnectedThread(context, MainActivity.connectedSocket);
        connectedThread.start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){
        paint = new Paint();
        WIDTH = getWidth();
        HEIGHT = getHeight();
        slingshot = new Slingshot((int)(WIDTH / 12), (int)(HEIGHT / 3),
                BitmapFactory.decodeResource(getResources(), R.drawable.slingshot_back),
                BitmapFactory.decodeResource(getResources(), R.drawable.slingshot_front),
                BitmapFactory.decodeResource(getResources(), R.drawable.egg));
        gameThread.setRunning(true);
        gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){ }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        boolean retry = true;
        while(retry){
            try{
                gameThread.setRunning(false);
                gameThread.join();
            }
            catch(Exception error){
                error.printStackTrace();
            }
            retry = false;
        }
    }

    public void dataReceived(String data){
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        Vector2 touch = new Vector2((int) event.getX(), (int) event.getY());
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                slingshot.pressed(touch);
                break;
            case MotionEvent.ACTION_UP:
                slingshot.released();
                break;
            case MotionEvent.ACTION_MOVE:
                slingshot.moved(touch);
                break;
        }
        return true;
    }


    public void update(){
        slingshot.update();
    }

    public void render(Canvas canvas){
        if(canvas != null){
            drawBackground(canvas);
            slingshot.render(canvas, paint);
        }
    }

    public void drawBackground(Canvas canvas){
        paint.setColor(WHITE);
        canvas.drawRect(0, 0, WIDTH, HEIGHT, paint);
        paint.setColor(BLUE);
        for(int i = 1; i < 9; i++){
            canvas.drawRect(0, i * (WIDTH / 16), WIDTH, (i * (WIDTH / 16)) + (WIDTH / 100), paint);
        }
        paint.setColor(RED);
        canvas.drawRect(WIDTH / 8, 0, (WIDTH / 8) + (WIDTH / 100), HEIGHT, paint);
    }
}
