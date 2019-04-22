package cs4330.cs.utep.eggthrower.Game;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Basket {

    public Vector2 position;
    public float width;
    public float height;
    public Bitmap sprite;

    public Basket(int width, int height, Bitmap sprite){
        position = new Vector2(40f / GameView.SCALE_RATIO, 0);
        this.width = width;
        this.height = height;
        this.sprite = Bitmap.createScaledBitmap(sprite, width, height, true);
    }

    public void update(){

    }

    public void render(Canvas canvas){
        canvas.drawBitmap(sprite, position.getX(), position.getY(), null);
    }
}
