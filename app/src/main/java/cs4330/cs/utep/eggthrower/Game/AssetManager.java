package cs4330.cs.utep.eggthrower.Game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import cs4330.cs.utep.eggthrower.R;

public class AssetManager {

    public static Bitmap basket;
    public static Bitmap basketClient;
    public static Bitmap egg;
    public static Bitmap slingshotBack;
    public static Bitmap slingshotFront;
    public static Bitmap bonus;
    public static Bitmap victory;
    public static Bitmap defeat;

    public static void load(Resources resources) {
        basket = BitmapFactory.decodeResource(resources, R.drawable.basket);
        basketClient = BitmapFactory.decodeResource(resources, R.drawable.basketclient);
        egg = BitmapFactory.decodeResource(resources, R.drawable.egg);
        slingshotBack = BitmapFactory.decodeResource(resources, R.drawable.slingshot_back);
        slingshotFront = BitmapFactory.decodeResource(resources, R.drawable.slingshot_front);
        bonus = BitmapFactory.decodeResource(resources, R.drawable.bonus);
        Bitmap tempVictory = BitmapFactory.decodeResource(resources, R.drawable.victory);
        victory = Bitmap.createScaledBitmap(tempVictory, (int)(520 / GameView.SCALE_RATIO), (int)(150 / GameView.SCALE_RATIO), true);
        Bitmap tempDefeat = BitmapFactory.decodeResource(resources, R.drawable.defeat);
        defeat = Bitmap.createScaledBitmap(tempDefeat, (int)(520 / GameView.SCALE_RATIO), (int)(150 / GameView.SCALE_RATIO), true);
    }
}