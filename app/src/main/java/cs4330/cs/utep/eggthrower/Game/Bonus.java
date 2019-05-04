package cs4330.cs.utep.eggthrower.Game;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * This class represents an in-game point multiplier that
 * doubles the points of the egg that collides with it.
 */
public class Bonus {

    /* Attributes of the bonus cloud */
    public Vector2 position;
    public Vector2 velocity;
    public float width;
    public float height;
    public Bitmap sprite;
    private boolean goingRight;
    private float maxXVelocity;

    /**
     * Constructor used to initialize a Bonus object.
     *
     * @param x the initial x position of the bonus
     * @param y the initial y position of the bonus
     */
    public Bonus(float x, float y) {
        position = new Vector2(x, y);
        velocity = new Vector2(0, -6f / GameView.SCALE_RATIO);
        maxXVelocity = 10f / GameView.SCALE_RATIO;
        width = 300f / GameView.SCALE_RATIO;
        height = 174f / GameView.SCALE_RATIO;
        /* Resize the sprite to support multiple screen sizes */
        sprite = Bitmap.createScaledBitmap(AssetManager.bonus, (int) width, (int) height, true);
    }

    /**
     * This method updates the position on the bonus to the top on the screen
     * while it oscillates left and right.
     */
    public void update() {
        position.add(velocity);
        /* If the velocity is more than the maximum or minimum velocity, change direction */
        if (velocity.getX() > maxXVelocity || velocity.getX() < -maxXVelocity) {
            goingRight = !goingRight;
        }
        /* Increase velocity corresponding to the direction */
        if (goingRight) {
            velocity.setX(velocity.getX() + 1);
        } else {
            velocity.setX(velocity.getX() - 1);
        }
        if(position.getY() > -height){
            GameView.bonuses.remove(this);
        }
    }

    /**
     * Method used to render the bonus' sprite to the screen.
     *
     * @param canvas the canvas that the sprite will be displayed on
     */
    public void render(Canvas canvas) {
        canvas.drawBitmap(sprite, position.getX(), position.getY(), null);
    }
}
