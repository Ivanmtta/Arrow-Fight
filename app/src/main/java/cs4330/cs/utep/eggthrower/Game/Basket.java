package cs4330.cs.utep.eggthrower.Game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import cs4330.cs.utep.eggthrower.MainActivity;

public class Basket {

    public Vector2 position;
    public float width;
    public float height;
    public Bitmap sprite;
    private int tics;
    private float[] basketStates;
    private int stateIndex = 0;
    public boolean displayAnimation;
    private int animationTics;
    private boolean flashSprite;
    private int maxFlashes;

    public Basket(){
        width = 152f / GameView.SCALE_RATIO;
        height = 348f / GameView.SCALE_RATIO;
        basketStates = new float[]{0, (GameView.HEIGHT / 2f) - (height / 2f), GameView.HEIGHT - height};
        if(MainActivity.CONNECTION.equals("SERVER")){
            position = new Vector2(40f / GameView.SCALE_RATIO, basketStates[stateIndex]);
        }
        else{
            position = new Vector2(1720f / GameView.SCALE_RATIO, basketStates[stateIndex]);
        }
        if(MainActivity.CONNECTION.equals("SERVER")){
            sprite = Bitmap.createScaledBitmap(AssetManager.basket, (int)width, (int)height, true);
        }
        else{
            sprite = Bitmap.createScaledBitmap(AssetManager.basketClient, (int)width, (int)height, true);
        }
    }

    public void update(){
        if(tics >= 120){
            stateIndex = (stateIndex + 1) % basketStates.length;
            position.setY(basketStates[stateIndex]);
            tics = 0;
        }
        if(displayAnimation){
            if(animationTics >= 5){
                flashSprite = !flashSprite;
                maxFlashes ++;
                animationTics = 0;
            }
            animationTics ++;
        }
        if(maxFlashes >= 10){
            displayAnimation = false;
            flashSprite = false;
            maxFlashes = 0;
        }
        tics ++;
    }

    public void render(Canvas canvas){
        if(!flashSprite){
            canvas.drawBitmap(sprite, position.getX(), position.getY(), null);
        }
    }

    public boolean contains(Rect rectangle){
        Rect hitBox = new Rect((int)position.getX(), (int)position.getY(), (int)(position.getX() + width), (int)(position.getY() + height));
        return hitBox.contains(rectangle);
    }
}