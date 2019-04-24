package cs4330.cs.utep.eggthrower.Game;

import android.graphics.Bitmap;

public class Animation {

    private Bitmap[] frames;
    private float delay;
    private int tics;
    private int frameIndex;
    private boolean finished;

    public Animation(Bitmap[] frames, float delay){
        this.frames = frames;
        this.delay = delay * 60f;
        frameIndex = 0;
    }

    public void play(){
        if(!finished) {
            if (tics >= delay) {
                tics = 0;
                if (frameIndex >= frames.length) {
                    finished = true;
                } else {
                    frameIndex++;
                }
            }
            tics++;
        }
    }

    public Bitmap getCurrentFrame(){
        return frames[frameIndex];
    }
}
