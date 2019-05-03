package cs4330.cs.utep.eggthrower.Game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cs4330.cs.utep.eggthrower.ConnectedThread;
import cs4330.cs.utep.eggthrower.MainActivity;
import cs4330.cs.utep.eggthrower.R;

public class GameView extends SurfaceView implements SurfaceHolder.Callback{

    private MainGameThread gameThread;
    public static float WIDTH;
    public static float HEIGHT;
    public static float SCALE_RATIO;

    private Paint paint;
    private final int WHITE = Color.rgb(240, 240, 242);
    private final int RED = Color.rgb(255, 161, 143);
    private final int BLUE = Color.rgb(153, 182, 255);
    private final int PLAYING = 0;
    private final int VICTORY = 1;
    private final int DEFEAT = 2;
    private int gameState = 0;
    private int tics = 0;
    private int bonusTic = 0;

    private Slingshot slingshot;
    private Basket basket;
    private int score;
    public static List<Egg> opponentEggs;
    public static List<Bonus> bonuses;

    private ConnectedThread connectedThread;

    public GameView(Context context){
        super(context);
        /* Set callback to the SurfaceHolder to track events */
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
        SCALE_RATIO = 1920f / WIDTH;
        AssetManager.load(getResources());
        opponentEggs = new ArrayList<>();
        bonuses = new ArrayList<>();
        slingshot = new Slingshot();
        basket = new Basket();
        gameThread.setRunning(true);
        gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){

    }

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
        String[] dataList = data.split("/");
        switch(dataList[0]){
            case "END":
                gameState = DEFEAT;
                break;
            case "EGG":
                float y = Float.parseFloat(dataList[1]);
                float velX = Float.parseFloat(dataList[2]) * Float.parseFloat(dataList[4]);
                float velY = Float.parseFloat(dataList[3]) * Float.parseFloat(dataList[4]);
                int x;
                if (MainActivity.CONNECTION.equals("SERVER")) {
                    x = (int) (WIDTH - slingshot.egg.width);
                } else {
                    x = (int) (-64f / SCALE_RATIO);
                }
                Egg tempEgg = new Egg(x, (int) y);
                tempEgg.velocity.set(velX, velY);
                tempEgg.inAir = true;
                opponentEggs.add(tempEgg);
                break;
            case "POINT":
                score++;
                if (score >= 3) {
                    connectedThread.send("END");
                    gameState = VICTORY;
                }
                break;
        }
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
        if(gameState == PLAYING){
            checkEggEdge(slingshot.egg);
            for(Egg currentEgg : opponentEggs){
                currentEgg.update();
                if(currentEgg.position.getY() > HEIGHT){
                    opponentEggs.remove(currentEgg);
                }
                if(basket.contains(new Rect(
                        (int)currentEgg.position.getX(),
                        (int)currentEgg.position.getY(),
                        (int)(currentEgg.position.getX() + currentEgg.width),
                        (int)(currentEgg.position.getY() + currentEgg.height)))){
                    connectedThread.send("POINT");
                    opponentEggs.remove(currentEgg);
                    basket.displayAnimation = true;
                }
            }
            for(Bonus bonus: bonuses){
                bonus.update();
            }
            slingshot.update();
            basket.update();
            generateBonuses();
        }
        else{
            if(tics >= 60){
                gameState = PLAYING;
                score = 0;
                tics = 0;
                basket.displayAnimation = false;
                bonuses.clear();
            }
            tics ++;
        }
    }

    public void render(Canvas canvas){
        if(canvas != null){
            drawBackground(canvas);
            if(gameState == PLAYING){
                slingshot.render(canvas, paint);
                for(Egg currentEgg : opponentEggs){
                    currentEgg.render(canvas);
                }
                basket.render(canvas);
                paint.setColor(Color.BLACK);
                paint.setTextSize(64f / SCALE_RATIO);
                canvas.drawText(String.valueOf(score), WIDTH / 2, 100f / SCALE_RATIO, paint);
                for(Bonus bonus: bonuses){
                    bonus.render(canvas);
                }
            }
            else if(gameState == VICTORY){
                canvas.drawBitmap(AssetManager.victory, 700f / SCALE_RATIO, 420 / SCALE_RATIO, null);
            }
            else if(gameState == DEFEAT){
                canvas.drawBitmap(AssetManager.defeat, 700f / SCALE_RATIO, 420 / SCALE_RATIO, null);
            }
        }
    }

    public void generateBonuses(){
        if(bonusTic >= 180){
            bonuses.add(new Bonus(generateRandomX(), HEIGHT));
            bonusTic = 0;
        }
        bonusTic ++;
    }

    public float generateRandomX(){
        Random rng = new Random();
        int maxClient = (int)(950 / SCALE_RATIO);
        int maxServer = (int)(1620 / SCALE_RATIO);
        int minClient = (int)(0 / SCALE_RATIO);
        int minServer = (int)(850 / SCALE_RATIO);
        if(MainActivity.CONNECTION.equals("SERVER")){
            return rng.nextInt((maxServer - minServer) + 1) + minServer;
        }
        else{
            return rng.nextInt((maxClient - minClient) + 1) + minClient;
        }
    }

    public void checkEggEdge(Egg egg){
        if(MainActivity.CONNECTION.equals("SERVER")){
            if(egg.position.getX() > WIDTH){
                sendEggInformation(egg);
            }
        }
        else{
            if(egg.position.getX() < -egg.width){
                sendEggInformation(egg);
            }
        }
    }

    public void sendEggInformation(Egg egg){
        connectedThread.send("EGG/" + egg.position.getY() + "/" + egg.velocity.getX() + "/" + egg.velocity.getY() + "/" + GameView.SCALE_RATIO);
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