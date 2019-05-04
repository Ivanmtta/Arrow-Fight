package cs4330.cs.utep.eggthrower.Game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import cs4330.cs.utep.eggthrower.BluetoothService.MainActivity;

/**
 * This class represents a basket inside the game, and the player will try
 * to hit it using its eggs to earn points.
 */
public class Basket {

    /* Attributes of the basket */
    public Vector2 position;
    public float width;
    public float height;
    public Bitmap sprite;
    /* Variables used to animate the basket */
    private int tics;
    private float[] basketStates;
    private int stateIndex = 0;
    public boolean displayAnimation;
    private int animationTics;
    private boolean flashSprite;
    private int maxFlashes;

    /**
     * Constructor used to initialize a basket object.
     */
    public Basket() {
        width = 152f / GameView.SCALE_RATIO;
        height = 348f / GameView.SCALE_RATIO;
        /* Initialize the 3 possible positions of the basket */
        basketStates = new float[]{0, (GameView.HEIGHT / 2f) - (height / 2f), GameView.HEIGHT - height};
        /* If the player is the server set the basket's position in the left side */
        if (MainActivity.CONNECTION.equals("SERVER")) {
            position = new Vector2(40f / GameView.SCALE_RATIO, basketStates[stateIndex]);
        }
        /* If the player is the server set the basket's position in the right side */
        else {
            position = new Vector2(1720f / GameView.SCALE_RATIO, basketStates[stateIndex]);
        }
        /* Change the basket's sprite depending if the player is server or client */
        if (MainActivity.CONNECTION.equals("SERVER")) {
            sprite = Bitmap.createScaledBitmap(AssetManager.basket, (int) width, (int) height, true);
        } else {
            sprite = Bitmap.createScaledBitmap(AssetManager.basketClient, (int) width, (int) height, true);
        }
    }

    /**
     * This method will change the baskets position every 2 seconds and will
     * update the flashing animation if the basket has been hit by an egg.
     */
    public void update() {
        /* every 2 seconds, change the position of the basket */
        if (tics >= 120) {
            stateIndex = (stateIndex + 1) % basketStates.length;
            position.setY(basketStates[stateIndex]);
            tics = 0;
        }
        /* if the basket got hit by an egg */
        if (displayAnimation) {
            if (animationTics >= 5) {
                flashSprite = !flashSprite;
                maxFlashes++;
                animationTics = 0;
            }
            animationTics++;
        }
        if (maxFlashes >= 10) {
            displayAnimation = false;
            flashSprite = false;
            maxFlashes = 0;
        }
        tics++;
    }

    /**
     * Method used to render the basket's sprite to the screen.
     *
     * @param canvas the canvas that the sprite will be displayed on
     */
    public void render(Canvas canvas) {
        if (!flashSprite) {
            canvas.drawBitmap(sprite, position.getX(), position.getY(), null);
        }
    }

    /**
     * This method checks the basket contains a rectangle inside.
     *
     * @param rectangle the rectangle that will be checked
     * @return if the rectangle is inside the basket
     */
    public boolean contains(Rect rectangle) {
        Rect hitBox = new Rect(
                (int) position.getX(),
                (int) position.getY(),
                (int) (position.getX() + width),
                (int) (position.getY() + height));
        return hitBox.contains(rectangle);
    }
}