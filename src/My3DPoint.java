/**
 * David Saelee
 * TCSS 458
 * HOMEWORK #2
 */

/**
 * Class was built to consolidate numerical data into coordinate point.
 */
public class My3DPoint {

    /**
     * Value to store x, y and z coordinate values.
     */
    double x, y, z;

    /**
     * Constructor to build 3D point.
     *
     * @param x point value.
     * @param y point value.
     * @param z point value.
     */
    public My3DPoint(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Helper method to display coordinate point
     * as string representation.
     *
     * @return string of coordinate point.
     */
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

}
