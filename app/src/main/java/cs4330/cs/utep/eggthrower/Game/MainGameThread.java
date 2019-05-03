package cs4330.cs.utep.eggthrower.Game;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class MainGameThread extends Thread{

    private final int FPS = 60;
    private double averageFPS;
    private SurfaceHolder surfaceHolder;
    private GameView gameView;
    private boolean running;
    private static Canvas canvas;

    public MainGameThread(SurfaceHolder surfaceHolder, GameView gameView){
        super();
        this.surfaceHolder = surfaceHolder;
        this.gameView = gameView;
    }

    public void run(){
        long startTime;
        long currentTime;
        long waitTime;
        long totalTime = 0;
        int frameCount = 0;
        long targetTime = 1000 / FPS;

        while(running){
            startTime = System.nanoTime();
            canvas = null;
            /* Lock the canvas in order to enable pixel editing */
            try{
               canvas = surfaceHolder.lockCanvas();
               synchronized(surfaceHolder){
                   gameView.update();
                   gameView.render(canvas);
               }
            }
            catch(Exception error){
                error.printStackTrace();
            }
            finally {
                if(canvas != null){
                    try{
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                    catch(Exception error){
                        error.printStackTrace();
                    }
                }
            }
            currentTime = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - currentTime;

            try{
                if(waitTime > 0){
                    sleep(waitTime);
                }
            }
            catch(Exception error){
                error.printStackTrace();
            }

            totalTime += System.nanoTime() - startTime;
            frameCount ++;
            if(frameCount == FPS){
                averageFPS = 1000 / ((totalTime / (float)frameCount) / 1000000);
                frameCount = 0;
                totalTime = 0;
                Log.i("TEST", "FPS: " + averageFPS);
            }
        }
    }

    public void setRunning(boolean running){
        this.running = running;
    }
}
