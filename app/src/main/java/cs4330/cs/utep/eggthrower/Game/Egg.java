package cs4330.cs.utep.eggthrower.Game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import cs4330.cs.utep.eggthrower.R;

public class Egg {

    public Vector2 position;
    public Vector2 velocity;
    public float width;
    public float height;
    private Bitmap sprite;
    public boolean moving;
    public boolean inAir;
    private boolean exploding;
    private Animation explodeAnimation;

    public Egg(int x, int y, int width, int height, Bitmap sprite){
        position = new Vector2(x, y);
        velocity = new Vector2(0f, 0f);
        this.width = width;
        this.height = height;
        this.sprite = Bitmap.createScaledBitmap(sprite, width, height, true);
        initializeAnimation();
    }

    public void initializeAnimation(){
        Bitmap[] frames = new Bitmap[4];
        frames[0] = BitmapFactory.decodeResource(GameView.resources, R.drawable.basket);
        explodeAnimation = new Animation(frames, .25f);
    }

    public void update(){
        if(inAir){
            position.add(velocity);
            velocity.setY(velocity.getY() + 0.35f);
            velocity.setX(velocity.getX() * 0.99f);
            velocity.setY(velocity.getY() * 0.99f);
            if(!exploding) {
                for (Egg currentEgg : GameView.opponentEggs) {
                    if (this.contains(currentEgg)) {
                        explode();
                    }
                }
            }
            else{
                explodeAnimation.play();
            }
        }
    }

    public void render(Canvas canvas){
        if(exploding){
            canvas.drawBitmap(explodeAnimation.getCurrentFrame(), position.getX(), position.getY(), null);
        }
        else{
            canvas.drawBitmap(sprite, position.getX(), position.getY(), null);
        }
    }

    public void explode(){
        exploding = true;
        velocity.set(0f, 0f);
    }

    public void launch(double angle, double force){
        velocity.set((float) (force * Math.cos(angle)), (float) (force * Math.sin(angle)));
        inAir = true;
    }

    public boolean contains(Vector2 vector){
        int extra = (int)(50f / GameView.SCALE_RATIO);
        return (position.getX() - extra <= vector.getX()) && (vector.getX() < position.getX() + width + extra) &&
                ((position.getY() - extra <= vector.getY()) && (position.getY() < position.getY() + height + extra));
    }

    public boolean contains(Egg egg){
        Rect eggHitBox = new Rect(
                (int) egg.position.getX(),
                (int) egg.position.getY(),
                (int) (egg.position.getX() + egg.width),
                (int) (egg.position.getY() + egg.height));
        Rect hitBox = new Rect((int)position.getX(), (int)position.getY(), (int)(position.getX() + width), (int)(position.getY() + height));
        return hitBox.contains(eggHitBox);
    }
}
