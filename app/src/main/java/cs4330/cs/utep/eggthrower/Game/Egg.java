package cs4330.cs.utep.eggthrower.Game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * This class represents an egg that will be launched to the other
 * device using the slingshot to earn points.
 */
public class Egg {

    /* Attributes of the egg */
    public Vector2 position;
    public Vector2 velocity;
    public float width;
    public float height;
    private Bitmap sprite;
    public boolean moving;
    public boolean inAir;
    public int value;

    /**
     * Constructor used to initialize an egg.
     *
     * @param x the initial x position of the bonus
     * @param y the initial y position of the bonus
     */
    public Egg(int x, int y) {
        position = new Vector2(x, y);
        velocity = new Vector2(0f, 0f);
        width = 64f / GameView.SCALE_RATIO;
        height = 82f / GameView.SCALE_RATIO;
        /* Resize sprite to work on multiple devices */
        sprite = Bitmap.createScaledBitmap(AssetManager.egg, (int) width, (int) height, true);
        value = 1;
    }

    /**
     * This method will add velocity, friction and gravity to the egg while
     * it is flying. Also, it will check if it collides with a bonus.
     */
    public void update() {
        if (inAir) {
            position.add(velocity);
            velocity.setY(velocity.getY() + 0.35f);
            velocity.scale(0.99f);
        }
        checkBonusCollision();
    }

    /**
     * Method used to render the egg's sprite to the screen.
     *
     * @param canvas the canvas that the sprite will be displayed on
     */
    public void render(Canvas canvas) {
        canvas.drawBitmap(sprite, position.getX(), position.getY(), null);
    }

    /**
     * This method will launch the egg into the ait.
     *
     * @param angle the angle of launch in radians
     * @param force the force that will be apply to the egg launh
     */
    public void launch(double angle, double force) {
        velocity.set((float) (force * Math.cos(angle)), (float) (force * Math.sin(angle)));
        inAir = true;
    }

    /**
     * This method will check if the egg is colliding with any of
     * the bonuses.
     */
    private void checkBonusCollision() {
        for (Bonus bonus : GameView.bonuses) {
            if (intersects(new Rect(
                    (int) bonus.position.getX(),
                    (int) bonus.position.getY(),
                    (int) (bonus.position.getX() + bonus.width),
                    (int) (bonus.position.getY() + bonus.height)))) {
                /* If its colliding remove the bonus, play a sound and multiply points */
                value *= 2;
                GameView.bonuses.remove(bonus);
                AssetManager.pointSound.start();
            }
        }
    }

    /**
     * This method checks if the egg contains a point inside its hitBox.
     *
     * @param vector the point that will be check
     * @return if the point is inside the egg
     */
    public boolean contains(Vector2 vector) {
        /* Give extra space to make it easier to collide */
        int extra = (int) (50f / GameView.SCALE_RATIO);
        return (position.getX() - extra <= vector.getX()) && (vector.getX() < position.getX() + width + extra) &&
                ((position.getY() - extra <= vector.getY()) && (position.getY() < position.getY() + height + extra));
    }

    /**
     * This method checks if the egg collides with a rectangle.
     *
     * @param rectangle the rectangle that will be ckeck
     * @return if the rectangle intersects the egg
     */
    public boolean intersects(Rect rectangle) {
        Rect hitBox = new Rect(
                (int) position.getX(),
                (int) position.getY(),
                (int) (position.getX() + width),
                (int) (position.getY() + height));
        return hitBox.intersect(rectangle);
    }
}
