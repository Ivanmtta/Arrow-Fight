package cs4330.cs.utep.arrowfight.Game;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Egg {

    public Vector2 position;
    public Vector2 velocity;
    public float width;
    public float height;
    private Bitmap sprite;
    public boolean moving;
    private boolean inAir;

    public Egg(int x, int y, int width, int height, Bitmap sprite){
        position = new Vector2(x, y);
        velocity = new Vector2(0f, 0f);
        this.width = width;
        this.height = height;
        this.sprite = Bitmap.createScaledBitmap(sprite, width, height, true);
    }

    public void update(){
        if (inAir){
            position.add(velocity);
            velocity.setY(velocity.getY() + 0.35f);
            velocity.setX(velocity.getX() * 0.99f);
            velocity.setY(velocity.getY() * 0.99f);
        }
    }

    public void render(Canvas canvas){
        canvas.drawBitmap(sprite, position.getX(), position.getY(), null);
    }

    public void launch(double angle, double force){
        velocity.set((float) (force * Math.cos(angle)), (float) (force * Math.sin(angle)));
        inAir = true;
    }

    public boolean contains(Vector2 vector){
        return (position.getX() <= vector.getX()) && (vector.getX() < position.getX() + width) &&
                ((position.getY() <= vector.getY()) && (position.getY() < position.getY() + height));
    }
}
