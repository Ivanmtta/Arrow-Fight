package cs4330.cs.utep.eggthrower.Game;

/**
 * Class used to storage a vector axis as well
 * as the vector operations.
 */
public class Vector2 {

    /* Axis of the vector */
    private float x;
    private float y;

    /**
     * Constructor to initialize a vector.
     *
     * @param x horizontal axis
     * @param y vertical axis
     */
    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * This method adds the x and y axis of a
     * vector to this vector.
     *
     * @param vector provided vector
     */
    public void add(Vector2 vector) {
        this.x += vector.getX();
        this.y += vector.getY();
    }

    /**
     * This method scales both axis of the vector
     * by a factor.
     *
     * @param factor number used to scale the vector
     */
    public void scale(float factor) {
        this.x *= factor;
        this.y *= factor;
    }

    /**
     * Method used to update the axis of the vector.
     *
     * @param x horizontal axis
     * @param y vertical axis
     */
    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Method used to update the axis of the vector.
     *
     * @param vector vector that will provide its axis
     */
    public void set(Vector2 vector) {
        this.x = vector.getX();
        this.y = vector.getY();
    }

    /**
     * Setter for the horizontal axis
     *
     * @param x provided x
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Setter for the vertical axis
     *
     * @param y provided y
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Getter for the horizontal axis
     *
     * @return x axis
     */
    public float getX() {
        return x;
    }

    /**
     * Getter for the vertical axis
     *
     * @return y axis
     */
    public float getY() {
        return y;
    }
}
