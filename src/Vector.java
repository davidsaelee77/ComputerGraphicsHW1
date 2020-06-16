/**
 * David Saelee
 * TCSS 458
 * HOMEWORK #3
 */

/**
 * Class used to perform vector calculations.
 */
public class Vector {

    double x, y, z, w; //values for the vector

    /**
     * Creates a new vector with the given values
     *
     * @param x x value of vector
     * @param y y value of vector
     * @param z z value of vector
     * @param w w value of vector
     */
    public Vector(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    /**
     * No arg constructor that makes a vector with 0s for each value
     */
    public Vector() {
        this(0, 0, 0, 0);
    }


    /**
     * Computes the dot product of two vectors. The
     * dot product is commutative.
     *
     * @param other the vector to be multiplied
     * @return the value of the dot product
     */
    public double dotProduct(Vector other) {

        return x * other.x + y * other.y + z * other.z + w * other.w;
    }

    /**
     * Computes the cross product of two vectors.
     *
     * @param other vector to be multiplied.
     * @return a vector of the cross product.
     */
    public Vector crossProduct(Vector other) {


        return new Vector(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x, 0);

    }

    /**
     * Subtracts two vectors.
     *
     * @param other vector to be subtracted.
     * @return the value of the subtraction.
     */
    public Vector subtractVector(Vector other) {


        return new Vector(this.x - other.x, this.y - other.y, this.z - other.z, 0);
    }

    /**
     * Normalizes the vector.
     *
     * @return a normalized vector.
     */
    public Vector normalizeVector() {


        double magnitude = returnMagnitude();

        return new Vector(this.x / magnitude, this.y / magnitude, this.z / magnitude, this.w);

    }

    /**
     * Returns the magnitude of a given vector.
     *
     * @return magnitude of a vector.
     */
    public double returnMagnitude() {

        return Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2) + Math.pow(this.z, 2));
    }

    /**
     * The string output of a vector.
     *
     * @return string representing a vector.
     */
    public String toString() {

        return "[" + x + " " + y + " " + z + " " + w + "]";
    }
}
