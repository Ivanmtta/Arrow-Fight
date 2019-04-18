package cs4330.cs.utep.arrowfight.Game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Slingshot {

    private Vector2 position;
    private float width;
    private float height;
    private Bitmap spriteBack;
    private Bitmap spriteFront;
    private Vector2 touch;
    private boolean pressed;
    private double angle;
    public Egg egg;
    private float pivot;
    private Bitmap eggSprite;

    public Slingshot(int width, int height, Bitmap spriteBack, Bitmap spriteFront, Bitmap eggSprite){
        position = new Vector2(GameView.WIDTH / 4, GameView.HEIGHT / 3);
        this.width = width;
        this.height = height;
        this.spriteBack = Bitmap.createScaledBitmap(spriteBack, width, height, true);
        this.spriteFront = Bitmap.createScaledBitmap(spriteFront, width, height, true);
        touch = new Vector2(0, 0);
        egg = new Egg((int) (position.getX() + width / 2) - (width / 4),
                (int) (position.getY()+ height / 2) - (height / 3) - (height / 16),
                width / 2, height / 3, eggSprite);
        this.eggSprite = eggSprite;
        pivot = (position.getY()+ height / 2f) - (height / 3f) - (height / 16f);
    }

    public void update(){
        if(pressed){
            angle = Math.atan2((pivot - touch.getY()), (position.getX() + (width / 2)) - touch.getX());
        }
        if(egg.position.getX() > GameView.WIDTH || egg.position.getX() < -egg.width || egg.position.getY() > GameView.HEIGHT || egg.position.getY() < -egg.height){
            egg = new Egg((int) ((position.getX() + width / 2) - (width / 4)),
                    (int) ((position.getY()+ height / 2) - (height / 3) - (height / 16)),
                    (int) (width / 2), (int) (height / 3), eggSprite);
        }
        egg.update();
    }

    public void render(Canvas canvas, Paint paint){
        if(pressed){
            paint.setColor(Color.GRAY);
            paint.setStrokeWidth(width / 30);
            canvas.drawLine(position.getX(), position.getY() + width / 4, touch.getX(), touch.getY(), paint);
            canvas.drawLine(position.getX() + width, position.getY() + width / 4, touch.getX(), touch.getY(), paint);
        }
        canvas.drawBitmap(spriteBack, position.getX(), position.getY(), null);
        egg.render(canvas);
        canvas.drawBitmap(spriteFront, position.getX(), position.getY(), null);
    }

    public void pressed(Vector2 touch){
        if(egg.contains(touch)){
            pressed = true;
            egg.position.set(touch.getX() - egg.width / 2, touch.getY() - egg.height / 2);
            this.touch.set(touch);
            egg.moving = true;
        }
    }

    public void moved(Vector2 touch){
        this.touch.set(touch);
        if(egg.moving){
            egg.position.set(touch.getX() - egg.width / 2, touch.getY() - egg.height / 2);
        }
    }

    public void released(){
        pressed = false;
        if(egg.moving){
            double force = (float) Math.sqrt(Math.pow((position.getX() - touch.getX()), 2) + Math.pow((pivot - touch.getY()), 2));
            egg.launch(angle, force / 8);
            egg.moving = false;
        }
    }
}
