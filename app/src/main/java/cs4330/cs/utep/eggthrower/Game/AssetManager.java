package cs4330.cs.utep.eggthrower.Game;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;

import cs4330.cs.utep.eggthrower.R;

/**
 * Class used to load and manage game assets.
 */
public class AssetManager {

    /* All game's images */
    public static Bitmap basket;
    public static Bitmap basketClient;
    public static Bitmap egg;
    public static Bitmap slingshotBack;
    public static Bitmap slingshotFront;
    public static Bitmap bonus;
    public static Bitmap victory;
    public static Bitmap defeat;

    /* All game's sounds */
    public static MediaPlayer slingshotSound;
    public static MediaPlayer launchSound;
    public static MediaPlayer pointSound;

    /**
     * Method used to load all of the game assets.
     *
     * @param resources reference to the resources class.
     * @param context   reference to the application's context
     */
    public static void load(Resources resources, Context context) {
        basket = BitmapFactory.decodeResource(resources, R.drawable.basket);
        basketClient = BitmapFactory.decodeResource(resources, R.drawable.basketclient);
        egg = BitmapFactory.decodeResource(resources, R.drawable.egg);
        slingshotBack = BitmapFactory.decodeResource(resources, R.drawable.slingshot_back);
        slingshotFront = BitmapFactory.decodeResource(resources, R.drawable.slingshot_front);
        bonus = BitmapFactory.decodeResource(resources, R.drawable.bonus);
        Bitmap tempVictory = BitmapFactory.decodeResource(resources, R.drawable.victory);
        victory = Bitmap.createScaledBitmap(tempVictory, (int) (520 / GameView.SCALE_RATIO),
                (int) (150 / GameView.SCALE_RATIO), true);
        Bitmap tempDefeat = BitmapFactory.decodeResource(resources, R.drawable.defeat);
        defeat = Bitmap.createScaledBitmap(tempDefeat, (int) (520 / GameView.SCALE_RATIO),
                (int) (150 / GameView.SCALE_RATIO), true);

        slingshotSound = MediaPlayer.create(context, R.raw.slingshoot);
        launchSound = MediaPlayer.create(context, R.raw.launch);
        pointSound = MediaPlayer.create(context, R.raw.point);
    }
}