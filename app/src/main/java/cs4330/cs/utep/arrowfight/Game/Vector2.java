package cs4330.cs.utep.arrowfight.Game;

public class Vector2 {

    private float x;
    private float y;

    public Vector2(float x, float y){
        this.x = x;
        this.y = y;
    }

    public void add(Vector2 vector){
        this.x += vector.getX();
        this.y += vector.getY();
    }

    public void set(float x, float y){
        this.x = x;
        this.y = y;
    }

    public void set(Vector2 vector){
        this.x = vector.getX();
        this.y = vector.getY();
    }

    public void setX(float x){
        this.x = x;
    }

    public void setY(float y){
        this.y = y;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

}
