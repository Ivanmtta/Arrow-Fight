package cs4330.cs.utep.eggthrower.Game;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * This class is used to initialize the game loop in a separate
 * thread.
 */
public class MainGameThread extends Thread {

    /* GameThread attributes */
    private final int FPS = 60;
    private SurfaceHolder surfaceHolder;
    private GameView gameView;
    private boolean running;
    private static Canvas canvas;

    /**
     * Constructor to initialize the game thread.
     *
     * @param surfaceHolder object used to control display surface
     * @param gameView      the game class that will display the game
     */
    public MainGameThread(SurfaceHolder surfaceHolder, GameView gameView) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gameView = gameView;
    }

    /**
     * This method will be called when the method starts and will be
     * called in a separated thread.
     */
    public void run() {
        long startTime;
        long currentTime;
        long waitTime;
        long targetTime = 1000 / FPS;

        /* Run this while the game is running */
        while (running) {
            startTime = System.nanoTime();
            canvas = null;
            /* Lock the canvas in order to enable pixel editing */
            try {
                canvas = surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    gameView.update();
                    gameView.render(canvas);
                }
            } catch (Exception error) {
                error.printStackTrace();
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception error) {
                        error.printStackTrace();
                    }
                }
            }
            /* Calculate how much time it has passed compared with how much
               time it should take for one frame */
            currentTime = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - currentTime;
            /* Sleep the thread to make sure the game runs at 60 FPS */
            try {
                if (waitTime > 0) {
                    sleep(waitTime);
                }
            } catch (Exception error) {
                error.printStackTrace();
            }
        }
    }

    /**
     * This method sets the thread's running state.
     * @param running whether the thread should run or not
     */
    public void setRunning(boolean running) {
        this.running = running;
    }
}
