package cs4330.cs.utep.eggthrower.Game;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Bonus{

    public Vector2 position;
    public Vector2 velocity;
    public float width;
    public float height;
    public Bitmap sprite;
    private boolean goingRight;
    private float maxXVelocity;

    public Bonus(float x, float y){
        position = new Vector2(x, y);
        velocity = new Vector2(0, -6f/ GameView.SCALE_RATIO);
        maxXVelocity = 10f / GameView.SCALE_RATIO;
        width = 300f / GameView.SCALE_RATIO;
        height = 174f / GameView.SCALE_RATIO;
        sprite = Bitmap.createScaledBitmap(AssetManager.bonus, (int)width, (int)height, true);
    }

    public void update(){
        position.add(velocity);
        if(velocity.getX() > maxXVelocity || velocity.getX() < -maxXVelocity){
            goingRight = !goingRight;
        }
        if(goingRight){
            velocity.setX(velocity.getX() + 1);
        }
        else{
            velocity.setX(velocity.getX() - 1);
        }
    }

    public void render(Canvas canvas){
        canvas.drawBitmap(sprite, position.getX(), position.getY(), null);
    }
}
