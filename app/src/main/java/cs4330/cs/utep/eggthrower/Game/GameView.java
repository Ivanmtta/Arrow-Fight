package cs4330.cs.utep.eggthrower.Game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cs4330.cs.utep.eggthrower.BluetoothService.ConnectedThread;
import cs4330.cs.utep.eggthrower.BluetoothService.MainActivity;

/**
 * This class is the view that contains all of the graphical
 * elements that are part of the game as well as all of the
 * game logic.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    /* Thread used to control the game loop */
    private MainGameThread gameThread;
    /* Game constants */
    public static float WIDTH;
    public static float HEIGHT;
    public static float SCALE_RATIO;
    /* Variables used to draw shapes */
    private Paint paint;
    private final int WHITE = Color.rgb(240, 240, 242);
    private final int RED = Color.rgb(255, 161, 143);
    private final int BLUE = Color.rgb(153, 182, 255);
    /* Variables used to determine the states of the game */
    private final int PLAYING = 0;
    private final int VICTORY = 1;
    private final int DEFEAT = 2;
    private int gameState = 0;
    private int tics = 0;
    private int bonusTic = 0;
    private int score;
    /* Game objects */
    private Slingshot slingshot;
    private Basket basket;
    public static List<Egg> opponentEggs;
    public static List<Bonus> bonuses;
    /* The connected thread used to receive and send information */
    private ConnectedThread connectedThread;

    /**
     * Constructor used to initialize the game view.
     *
     * @param context Context to the activity that initialized the view
     */
    public GameView(Context context) {
        super(context);
        /* Set callback to the SurfaceHolder to track events */
        getHolder().addCallback(this);
        gameThread = new MainGameThread(getHolder(), this);
        /* Make window focusable so it can handle touch events */
        setFocusable(true);
        /* Initialize and start the connected thread */
        connectedThread = new ConnectedThread(context, MainActivity.connectedSocket);
        connectedThread.start();
    }

    /**
     * This method is called immediately after the surface is first created
     *
     * @param holder The SurfaceHolder whose surface is being created
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        paint = new Paint();
        /* Get the dimensions of the device */
        WIDTH = getWidth();
        HEIGHT = getHeight();
        /* Calculate the ratio of sizes to support multiple screen sizes */
        SCALE_RATIO = 1920f / WIDTH;
        /* Load the all of the game assets */
        AssetManager.load(getResources(), getContext());
        /* Initialize all of the game objects */
        opponentEggs = new ArrayList<>();
        bonuses = new ArrayList<>();
        slingshot = new Slingshot();
        basket = new Basket();
        /* Start game thread to start updating and rendering the game */
        gameThread.setRunning(true);
        gameThread.start();
    }

    /**
     * This method is called immediately after any structural changes (format or size)
     * have been made to the surface.
     *
     * @param holder The SurfaceHolder whose surface has changed
     * @param format The new PixelFormat of the surface
     * @param width  The new width of the surface
     * @param height The new height of the surface
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    /**
     * This method is called immediately before a surface is being destroyed.
     *
     * @param holder The SurfaceHolder whose surface is being destroyed
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                /* Stop the game thread from running */
                gameThread.setRunning(false);
                gameThread.join();
            } catch (Exception error) {
                error.printStackTrace();
            }
            retry = false;
        }
    }

    /**
     * This method will be called when the other connected thread sends a message.
     * Also, it will decode the data and will perform the required operation.
     *
     * @param data The received data
     */
    public void dataReceived(String data) {
        String[] dataList = data.split("/");
        switch (dataList[0]) {
            /* The other player achieved the necessary amount of points to win */
            case "END":
                gameState = DEFEAT;
                break;
            case "EGG":
                /* The other player sent and egg */
                float y = Float.parseFloat(dataList[1]);
                float velX = Float.parseFloat(dataList[2]) * Float.parseFloat(dataList[4]);
                float velY = Float.parseFloat(dataList[3]) * Float.parseFloat(dataList[4]);
                int x;
                /* Check from what part of the screen was the egg sent */
                if (MainActivity.CONNECTION.equals("SERVER")) {
                    x = (int) (WIDTH - slingshot.egg.width);
                } else {
                    x = (int) (-64f / SCALE_RATIO);
                }
                /* Initialize temporary egg, and set all of the received data */
                Egg tempEgg = new Egg(x, (int) y);
                tempEgg.velocity.set(velX, velY);
                tempEgg.inAir = true;
                tempEgg.value = Integer.parseInt(dataList[5]);
                /* Add egg to the list of eggs received */
                opponentEggs.add(tempEgg);
                break;
            case "POINT":
                /* One of the eggs sent from this player collided with the basket */
                score += Integer.parseInt(dataList[1]);
                /* Check if the player achieved all of the required points */
                if (score >= 3) {
                    connectedThread.send("END");
                    gameState = VICTORY;
                }
                break;
        }
    }

    /**
     * This method is used to handle touch screen motion events.
     *
     * @param event The motion event.
     * @return Whether the event was handled.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /* initialize a vector to save the touch position */
        Vector2 touch = new Vector2((int) event.getX(), (int) event.getY());
        switch (event.getAction()) {
            /* The screen was pressed */
            case MotionEvent.ACTION_DOWN:
                slingshot.pressed(touch);
                break;
            /* The screen was released */
            case MotionEvent.ACTION_UP:
                slingshot.released();
                break;
            /* The screen was dragged */
            case MotionEvent.ACTION_MOVE:
                slingshot.moved(touch);
                break;
        }
        return true;
    }

    /**
     * This method updates all of the game objects and if the game ends
     * it waits 2 seconds and restarts the game.
     */
    public void update() {
        /* If the game is not over */
        if (gameState == PLAYING) {
            /* Check if the egg is outside */
            checkEggEdge(slingshot.egg);
            /* Check if the other player scored a point */
            checkForPoints();
            /* Update all game objects */
            for (Bonus bonus : bonuses) {
                bonus.update();
            }
            slingshot.update();
            basket.update();
            /* Generate point multipliers */
            generateBonuses();
        } else {
            /* If the game is over, wait 2 seconds and restart the game */
            if (tics >= 120) {
                gameState = PLAYING;
                score = 0;
                tics = 0;
                basket.displayAnimation = false;
                bonuses.clear();
            }
            tics++;
        }
    }

    /**
     * This method draws all of the game objects into the screen.
     *
     * @param canvas
     */
    public void render(Canvas canvas) {
        if (canvas != null) {
            /* Display the background of the game */
            drawBackground(canvas);
            /* If the game is not over */
            if (gameState == PLAYING) {
                /* Display all of the game objects */
                slingshot.render(canvas, paint);
                for (Egg currentEgg : opponentEggs) {
                    currentEgg.render(canvas);
                }
                for (Bonus bonus : bonuses) {
                    bonus.render(canvas);
                }
                basket.render(canvas);
                /* Display the current score */
                paint.setColor(Color.BLACK);
                paint.setTextSize(64f / SCALE_RATIO);
                canvas.drawText(String.valueOf(score), WIDTH / 2, 100f / SCALE_RATIO, paint);
            } else if (gameState == VICTORY) {
                /* Draw victory game end screen */
                canvas.drawBitmap(AssetManager.victory, 700f / SCALE_RATIO, 420 / SCALE_RATIO, null);
            } else if (gameState == DEFEAT) {
                /* Draw defeat game end screen */
                canvas.drawBitmap(AssetManager.defeat, 700f / SCALE_RATIO, 420 / SCALE_RATIO, null);
            }
        }
    }

    /**
     * This method checks if any of the eggs is inside of the basket, and if it is
     * it sends a message to the other player to register the point.
     */
    public void checkForPoints() {
        for (Egg currentEgg : opponentEggs) {
            /* Update the eggs physics */
            currentEgg.update();
            /* Delete eggs that did not hit the basket */
            if (currentEgg.position.getY() > HEIGHT) {
                opponentEggs.remove(currentEgg);
            }
            /* Check if any of the eggs is colliding with the basket */
            if (basket.contains(new Rect(
                    (int) currentEgg.position.getX(),
                    (int) currentEgg.position.getY(),
                    (int) (currentEgg.position.getX() + currentEgg.width),
                    (int) (currentEgg.position.getY() + currentEgg.height)))) {
                /* Send the point message to the other player using the connected thread */
                connectedThread.send("POINT/" + currentEgg.value);
                /* Remove the egg, display the basket animation and play a sound */
                opponentEggs.remove(currentEgg);
                basket.displayAnimation = true;
                AssetManager.pointSound.start();
            }
        }
    }

    /**
     * This method generates a bonus object in a random position every 3 seconds.
     */
    public void generateBonuses() {
        if (bonusTic >= 180) {
            bonuses.add(new Bonus(generateRandomX(), HEIGHT));
            bonusTic = 0;
        }
        bonusTic++;
    }

    /**
     * This method generates a random x position for a bonus.
     *
     * @return THe random position generated
     */
    public float generateRandomX() {
        Random rng = new Random();
        /* Maximum x positions for the client and server */
        int maxClient = (int) (950 / SCALE_RATIO);
        int maxServer = (int) (1620 / SCALE_RATIO);
        int minClient = (int) (0 / SCALE_RATIO);
        int minServer = (int) (850 / SCALE_RATIO);
        /* Check if the player is a server or a client */
        if (MainActivity.CONNECTION.equals("SERVER")) {
            return rng.nextInt((maxServer - minServer) + 1) + minServer;
        } else {
            return rng.nextInt((maxClient - minClient) + 1) + minClient;
        }
    }

    /**
     * Check if the position of the player's egg was sent to the other
     * player.
     *
     * @param egg The egg of the player
     */
    public void checkEggEdge(Egg egg) {
        /* Check if the player is a server or a client */
        if (MainActivity.CONNECTION.equals("SERVER")) {
            if (egg.position.getX() > WIDTH) {
                sendEggInformation(egg);
            }
        } else {
            if (egg.position.getX() < -egg.width) {
                sendEggInformation(egg);
            }
        }
    }

    /**
     * This method encodes an egg information into a string and sends it into
     * the other player using the connected thread.
     *
     * @param egg The egg that will be send to the other player.
     */
    public void sendEggInformation(Egg egg) {
        connectedThread.send("EGG/" + egg.position.getY() + "/" +
                egg.velocity.getX() + "/" +
                egg.velocity.getY() + "/" +
                GameView.SCALE_RATIO + "/" +
                egg.value);
    }

    /**
     * This method draws the background of the game using different color rectangles
     * to improve performance of the game instead of using an image.
     *
     * @param canvas the canvas that the rectangles will be displayed on
     */
    public void drawBackground(Canvas canvas) {
        paint.setColor(WHITE);
        canvas.drawRect(0, 0, WIDTH, HEIGHT, paint);
        paint.setColor(BLUE);
        for (int i = 1; i < 9; i++) {
            canvas.drawRect(0, i * (WIDTH / 16), WIDTH, (i * (WIDTH / 16)) + (WIDTH / 100), paint);
        }
        paint.setColor(RED);
        canvas.drawRect(WIDTH / 8, 0, (WIDTH / 8) + (WIDTH / 100), HEIGHT, paint);
    }
}