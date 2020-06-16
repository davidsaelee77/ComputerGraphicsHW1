/**
 * David Saelee
 * TCSS 458
 * HOMEWORK #3
 */

/**
 * Class used to store matrices and methods to perform
 * computation on image manipulation commands.
 */
public class Matrix {

    /**
     * Stores computed matrix.
     */
    double[][] matrix;

    /**
     * Matrix constructor
     *
     * @param m a matrix.
     */
    private Matrix(double[][] m) {

        matrix = m;
    }

    /**
     * Builds a 4 x 4 identity matrix.
     *
     * @return 4 x 4 identity matrix.
     */
    public static Matrix identityMatrix() {
        double[][] matrix = new double[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {

                if (i == j) {

                    matrix[i][j] = 1;

                } else {

                    matrix[i][j] = 0;
                }
            }
        }
        return new Matrix(matrix);
    }

    /**
     * Method pre-mulitplies incoming matrix with the current
     * transformation matrix.
     * <p>
     * Entire method does m * this (premultiply)
     *
     * @param m is an incoming matrix.
     * @return computed matrix.
     */
    public Matrix multiplyMatrix(Matrix m) {

        double[][] computedMatrix = new double[4][4];

        for (int row = 0; row < 4; row++) {

            for (int column = 0; column < 4; column++) {

                double sum = 0;
                for (int i = 0; i < 4; i++) {

                    sum += m.matrix[row][i] * this.matrix[i][column];

                }

                computedMatrix[row][column] = sum;

            }
        }
        return new Matrix(computedMatrix);

    }

    /**
     * Multiples a matrix to a vector and
     * returns the new computed vector.
     *
     * @param p a position vector.
     * @return computed position vector
     */
    public My3DPoint multiplyPoint(My3DPoint p) {

        double[] vector = new double[4];

        double[] computedPoint = new double[4];

        vector[0] = p.x;
        vector[1] = p.y;
        vector[2] = p.z;
        vector[3] = 1;

        for (int row = 0; row < 4; row++) {

            double sum = 0;
            for (int i = 0; i < vector.length; i++) {

                sum += this.matrix[row][i] * vector[i];

            }

            computedPoint[row] = sum;
        }

        double w = computedPoint[3];

        return new My3DPoint(computedPoint[0] / w, computedPoint[1] / w, computedPoint[2] / w);
    }


    /**
     * Creates a translation matrix.
     *
     * @param x point value.
     * @param y point value.
     * @param z point value.
     * @return translation matrix with provided points.
     */
    public static Matrix createTranslatationMatrix(double x, double y, double z) {
        Matrix m = Matrix.identityMatrix();

        m.matrix[0][3] = x;
        m.matrix[1][3] = y;
        m.matrix[2][3] = z;

        return m;

    }

    /**
     * Creates a scaling matrix.
     *
     * @param x point value.
     * @param y point value.
     * @param z point value.
     * @return scaling matrix with provided points.
     */
    public static Matrix createScalingMatrix(double x, double y, double z) {

        Matrix m = Matrix.identityMatrix();
        m.matrix[0][0] = x;
        m.matrix[1][1] = y;
        m.matrix[2][2] = z;

        return m;
    }

    /**
     * Creates rotation X matrix.
     *
     * @param rotation value in degrees.
     * @return rotation matrix with provided values.
     */
    public static Matrix createRotationX(double rotation) {

        Matrix m = Matrix.identityMatrix();

        m.matrix[1][1] = Math.cos(rotation);
        m.matrix[1][2] = -Math.sin(rotation);
        m.matrix[2][1] = Math.sin(rotation);
        m.matrix[2][2] = Math.cos(rotation);

        return m;

    }

    /**
     * Creates rotation Y matrix.
     *
     * @param rotation value in degrees.
     * @return rotation matrix with provided values.
     */
    public static Matrix createRotationY(double rotation) {
        Matrix m = Matrix.identityMatrix();

        m.matrix[0][0] = Math.cos(rotation);
        m.matrix[0][2] = Math.sin(rotation);
        m.matrix[2][0] = -Math.sin(rotation);
        m.matrix[2][2] = Math.cos(rotation);

        return m;
    }

    /**
     * Creates rotation Z matrix.
     *
     * @param rotation value in degrees.
     * @return rotation matrix with provided values.
     */
    public static Matrix createRotationZ(double rotation) {

        Matrix m = Matrix.identityMatrix();

        m.matrix[0][0] = Math.cos(rotation);
        m.matrix[0][1] = -Math.sin(rotation);
        m.matrix[1][0] = Math.sin(rotation);
        m.matrix[1][1] = Math.cos(rotation);

        return m;


    }

    /**
     * Creates an othrographic projection matrix.
     * http://learnwebgl.brown37.net/08_projections/projections_ortho.html Equation 5
     *
     * @param left   double
     * @param right  double
     * @param top    double
     * @param bottom double
     * @param near   double
     * @param far    double
     * @return a computed orthograpic matrix.
     */
    public static Matrix createOrthoProjection(double left, double right, double top, double bottom, double near, double far) {

        Matrix m = Matrix.identityMatrix();

        m.matrix[0][0] = 2 / (right - left);
        m.matrix[0][3] = -(right + left) / (right - left);
        m.matrix[1][1] = 2 / (top - bottom);
        m.matrix[1][3] = -(top + bottom) / (top - bottom);
        m.matrix[2][2] = -2 / (far - near);
        m.matrix[2][3] = -(far + near) / (far - near);


        return m;
    }

    /**
     * Creates a prespective projection matrix.
     * http://learnwebgl.brown37.net/08_projections/projections_perspective.html Equation 9
     *
     * @param left   double
     * @param right  double
     * @param top    double
     * @param bottom double
     * @param near   double
     * @param far    double
     * @return a computed prespective projection matrix.
     */
    public static Matrix createPrespectiveProjection(double left, double right, double top, double bottom, double near, double far) {


        Matrix m = identityMatrix();

        m.matrix[0][0] = 2 * near / (right - left);
        m.matrix[0][3] = -near * (right + left) / (right - left);
        m.matrix[1][1] = 2 * near / (top - bottom);
        m.matrix[1][3] = -near * (top + bottom) / (top - bottom);
        m.matrix[2][2] = -(far + near) / (far - near);
        m.matrix[2][3] = 2 * far * near / (near - far);
        m.matrix[3][2] = -1;
        m.matrix[3][3] = 0;

        return m;

    }

    /**
     * Creates a look-at projection matrix.
     * http://learnwebgl.brown37.net/07_cameras/camera_math.html Equation 4
     *
     * @param eyeX    double
     * @param eyeY    double
     * @param eyeZ    double
     * @param centerX double
     * @param centerY double
     * @param centerZ double
     * @param updx    double
     * @param updy    double
     * @param updz    double
     * @return a computed look-at projection matrix.
     */
    public static Matrix lookAT(double eyeX, double eyeY, double eyeZ,
                                double centerX, double centerY, double centerZ,
                                double updx, double updy, double updz) {

        Matrix rotateToAlign = rotateToAlign(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, updx, updy, updz);
        Matrix translateToOrigin = translateToOrigin(eyeX, eyeY, eyeZ);
        return translateToOrigin.multiplyMatrix(rotateToAlign);
    }

    /**
     * Helper method to multiply matrix with translate to origin matrix.
     *
     * @param eyeX    double
     * @param eyeY    double
     * @param eyeZ    double
     * @param centerX double
     * @param centerY double
     * @param centerZ double
     * @param updx    double
     * @param updy    double
     * @param updz    double
     * @return a matrix with computed elements.
     */
    public static Matrix rotateToAlign(double eyeX, double eyeY, double eyeZ,
                                       double centerX, double centerY, double centerZ,
                                       double updx, double updy, double updz) {

        Vector center = new Vector(centerX, centerY, centerZ, 0);
        Vector eye = new Vector(eyeX, eyeY, eyeZ, 0);
        Vector up = new Vector(updx, updy, updz, 0);

        Vector n = eye.subtractVector(center).normalizeVector();


        Vector u = up.crossProduct(n).normalizeVector();
        Vector v = n.crossProduct(u).normalizeVector();


        Matrix m = Matrix.identityMatrix();

        m.matrix[0][0] = u.x;
        m.matrix[0][1] = u.y;
        m.matrix[0][2] = u.z;

        m.matrix[1][0] = v.x;
        m.matrix[1][1] = v.y;
        m.matrix[1][2] = v.z;

        m.matrix[2][0] = n.x;
        m.matrix[2][1] = n.y;
        m.matrix[2][2] = n.z;

        return m;
    }

    /**
     * Helper method to multiply matrix with rotate to align matrix.
     *
     * @param eyeX double
     * @param eyeY double
     * @param eyeZ double
     * @return a matrix with computed elements.
     */
    public static Matrix translateToOrigin(double eyeX, double eyeY, double eyeZ) {
        Matrix m = Matrix.identityMatrix();
        m.matrix[0][3] = -eyeX;
        m.matrix[1][3] = -eyeY;
        m.matrix[2][3] = -eyeZ;
        return m;
    }

    /**
     * Helper method for debugging purposes.
     * Prints the contents of a matrix.
     *
     * @return matrix displayed as a string.
     */
    public String toString() {
        String res = "";
        for (double[] r : matrix) {
            res += "[" + r[0];
            for (int i = 1; i < r.length; i++) {
                res += ", " + r[i];
            }
            res += "]\n";
        }
        return res + "\n";
    }


}