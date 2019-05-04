package cs4330.cs.utep.eggthrower.Game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import cs4330.cs.utep.eggthrower.MainActivity;

public class Slingshot {

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

    public Slingshot(){
        width = (int)(133f / GameView.SCALE_RATIO);
        height = (int)(256f / GameView.SCALE_RATIO);
        if(MainActivity.CONNECTION.equals("SERVER")){
            position = new Vector2(620f / GameView.SCALE_RATIO, 420f / GameView.SCALE_RATIO);
        }
        else{
            position = new Vector2(1300f / GameView.SCALE_RATIO, 420f / GameView.SCALE_RATIO);
        }
        spriteBack = Bitmap.createScaledBitmap(AssetManager.slingshotBack, width, height, true);
        spriteFront = Bitmap.createScaledBitmap(AssetManager.slingshotFront, width, height, true);
        touch = new Vector2(0, 0);
        egg = new Egg((int)(position.getX() + (30 / GameView.SCALE_RATIO)),
                      (int)(position.getY() + (30 / GameView.SCALE_RATIO)));
        pivot = (position.getY() + (30f / GameView.SCALE_RATIO));
    }

    public void update(){
        if(pressed){
            angle = Math.atan2((pivot - touch.getY()), (position.getX() + (width / 2f)) - touch.getX());
            if(!AssetManager.slingshotSound.isPlaying()){
                AssetManager.slingshotSound.start();
            }
        }
        if(egg.position.getX() > GameView.WIDTH || egg.position.getX() < -egg.width || egg.position.getY() > GameView.HEIGHT || egg.position.getY() < -GameView.WIDTH / 2){
            egg = new Egg((int)(position.getX() + (30 / GameView.SCALE_RATIO)),
                    (int)(position.getY() + (30 / GameView.SCALE_RATIO)));
        }
        egg.update();
    }

    public void render(Canvas canvas, Paint paint){
        if(pressed){
            paint.setColor(Color.GRAY);
            paint.setStrokeWidth(width / 30f);
            canvas.drawLine(position.getX(), pivot, touch.getX(), touch.getY(), paint);
            canvas.drawLine(position.getX() + width, pivot, touch.getX(), touch.getY(), paint);
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
            AssetManager.launchSound.start();
        }
    }
}
