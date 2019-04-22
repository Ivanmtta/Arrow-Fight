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

    public Basket(int width, int height, Bitmap sprite){
        basketStates = new float[]{0, (GameView.HEIGHT / 2f) - (height / 2f), GameView.HEIGHT - height};
        if(MainActivity.CONNECTION.equals("SERVER")){
            position = new Vector2(40f / GameView.SCALE_RATIO, basketStates[stateIndex]);
        }
        else{
            position = new Vector2(1720f / GameView.SCALE_RATIO, basketStates[stateIndex]);
        }
        this.width = width;
        this.height = height;
        this.sprite = Bitmap.createScaledBitmap(sprite, width, height, true);
    }

    public void update(){
        if(tics >= 120){
            stateIndex = (stateIndex + 1) % basketStates.length;
            position.setY(basketStates[stateIndex]);
            tics = 0;
        }
        tics ++;
    }

    public void render(Canvas canvas){
        canvas.drawBitmap(sprite, position.getX(), position.getY(), null);
    }

    public boolean contains(Rect rectangle){
        Rect hitbox = new Rect((int)position.getX(), (int)position.getY(), (int)(position.getX() + width), (int)(position.getY() + height));
        return hitbox.contains(rectangle);
    }
}
