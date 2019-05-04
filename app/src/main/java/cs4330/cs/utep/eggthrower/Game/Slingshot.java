package cs4330.cs.utep.eggthrower.Game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import cs4330.cs.utep.eggthrower.BluetoothService.MainActivity;

/**
 * This class represents the slingshot that the player will use to
 * launch the eggs to the opponents' basket.
 */
public class Slingshot {

    /* Slingshot attributes */
    private Vector2 position;
    public int width;
    public int height;
    private Bitmap spriteBack;
    private Bitmap spriteFront;
    private Vector2 touch;
    private boolean pressed;
    private double angle;
    public Egg egg;
    private float pivot;

    /**
     * Constructor used to initialize the slingshot object.
     */
    public Slingshot() {
        width = (int) (133f / GameView.SCALE_RATIO);
        height = (int) (256f / GameView.SCALE_RATIO);
        /* Check whether the player is a server or a client to set corresponding slingshot position */
        if (MainActivity.CONNECTION.equals("SERVER")) {
            position = new Vector2(620f / GameView.SCALE_RATIO, 420f / GameView.SCALE_RATIO);
        } else {
            position = new Vector2(1300f / GameView.SCALE_RATIO, 420f / GameView.SCALE_RATIO);
        }
        /* Resize slingshot's sprite to support multiple screen sizes */
        spriteBack = Bitmap.createScaledBitmap(AssetManager.slingshotBack, width, height, true);
        spriteFront = Bitmap.createScaledBitmap(AssetManager.slingshotFront, width, height, true);
        touch = new Vector2(0, 0);
        /* Initialize egg inside the slingshot sprite */
        egg = new Egg((int) (position.getX() + (30 / GameView.SCALE_RATIO)),
                (int) (position.getY() + (30 / GameView.SCALE_RATIO)));
        pivot = (position.getY() + (30f / GameView.SCALE_RATIO));
    }

    /**
     * This method will calculate the launch angle as well as check if the
     * egg is outside of the screen.
     */
    public void update() {
        if (pressed) {
            /* Calculate launch angle */
            angle = Math.atan2((pivot - touch.getY()), (position.getX() + (width / 2f)) - touch.getX());
            /* Play slingshot sound */
            if (!AssetManager.slingshotSound.isPlaying()) {
                AssetManager.slingshotSound.start();
            }
        }
        /* If the egg is outside of the screen, restore its position */
        if (egg.position.getX() > GameView.WIDTH ||
                egg.position.getX() < -egg.width ||
                egg.position.getY() > GameView.HEIGHT ||
                egg.position.getY() < -GameView.WIDTH / 2) {
            egg = new Egg((int) (position.getX() + (30 / GameView.SCALE_RATIO)),
                    (int) (position.getY() + (30 / GameView.SCALE_RATIO)));
        }
        egg.update();
    }

    /**
     * This method will render both parts of the slingshot sprite, the rubber band and
     * the egg in between.
     *
     * @param canvas the canvas that the sprite will be displayed on
     * @param paint  paint that will be used to draw a shape
     */
    public void render(Canvas canvas, Paint paint) {
        /* if the user is holding the egg */
        if (pressed) {
            paint.setColor(Color.GRAY);
            paint.setStrokeWidth(width / 30f);
            canvas.drawLine(position.getX(), pivot, touch.getX(), touch.getY(), paint);
            canvas.drawLine(position.getX() + width, pivot, touch.getX(), touch.getY(), paint);
        }
        /* Draw both slingshot's sprites */
        canvas.drawBitmap(spriteBack, position.getX(), position.getY(), null);
        egg.render(canvas);
        canvas.drawBitmap(spriteFront, position.getX(), position.getY(), null);
    }

    /**
     * This method will be called when the user presses the screen.
     *
     * @param touch position of the player's finger
     */
    public void pressed(Vector2 touch) {
        /* If the player's finger is inside of the egg */
        if (egg.contains(touch)) {
            pressed = true;
            /* Make the egg follow the finger */
            egg.position.set(touch.getX() - egg.width / 2, touch.getY() - egg.height / 2);
            this.touch.set(touch);
            egg.moving = true;
        }
    }

    /**
     * This method will be called when the user is dragging the egg.
     *
     * @param touch position of the player's finger
     */
    public void moved(Vector2 touch) {
        this.touch.set(touch);
        /* If egg was touched, move it */
        if (egg.moving) {
            egg.position.set(touch.getX() - egg.width / 2, touch.getY() - egg.height / 2);
        }
    }

    /**
     * This method will be called then the user stops touching the screen.
     */
    public void released() {
        pressed = false;
        /* If the egg was being dragged by the user */
        if (egg.moving) {
            /* Calculate the force from the pivot of the egg to the last player's position */
            double force = (float) Math.sqrt(Math.pow((position.getX() - touch.getX()), 2) +
                    Math.pow((pivot - touch.getY()), 2));
            /* Launch the egg and play a sound */
            egg.launch(angle, force / 8);
            egg.moving = false;
            AssetManager.launchSound.start();
        }
    }
}
