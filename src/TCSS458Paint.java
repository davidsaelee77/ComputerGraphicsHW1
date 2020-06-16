/**
 * David Saelee
 * TCSS 458
 * HOMEWORK #3
 */

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

/**
 * Program utilizes the Bresenham algorithm and a variation of Scanline algorithm
 * to draw lines and fill triangles.
 */
public class TCSS458Paint extends JPanel {
    /**
     * Width of canvas.
     */
    static int width;
    /**
     * Height of canvas.
     */
    static int height;
    /**
     * Stores total amount of pixels in image.
     */
    int imageSize;
    /**
     * Stores array of RGB values.
     */
    int[] pixels;
    /**
     * Stores RGB values.
     */
    int r, g, b;
    /**
     * Stores and array of xMin and xMax points.
     */
    ArrayList<My3DPoint> scanline;
    /**
     * Stores the current transformation matrix.
     */
    Matrix CTM;
    /**
     * Initializes and stores the current rotation matrix.
     */
    Matrix rotation = Matrix.identityMatrix();
    /**
     * Stores z-values for all drawn points.
     */
    double[] zBuffer;
    /**
     * Stores shading factor value.
     */
    double shadingFactor;
    /**
     * Stores normal triangle values.
     */
    Vector triangleNormal;
    /**
     * Stores light vector values.
     */
    Vector lightVector;
    /**
     * Stores projection matrix values.
     */
    Matrix projection;
    /**
     * Stores look-at matrix values.
     */
    Matrix lookat;

    /**
     * **NEW LOGIC**
     * <p>
     * Added clipping cube logic and reversed direction of z-buffer.
     * <p>
     * Draws the pixel with the provided parameters.  Will only draw
     * pixel if the incoming z-value parameter is larger than the z-value
     * within the z-buffer.
     *
     * @param x x point value
     * @param y y point value
     * @param r r color value
     * @param g g color value
     * @param b b color value
     */
    void drawPixel(double x, double y, double z, int r, int g, int b) {

        int xInteger = (int) Math.round(x);
        int yInteger = (int) Math.round(y);


        if (xInteger >= 0 && xInteger <= width - 1 && yInteger >= 0 && yInteger <= height - 1) {

            //Computes the location/index of the corresponding
            //z-value in the zBuffer.
            int zIndex = yInteger * width + xInteger;

            if (z < zBuffer[zIndex]) {

                zBuffer[zIndex] = z;

                pixels[(int) ((height - y - 1) * width * 3 + x * 3)] = (int) (.5 * r + .5 * shadingFactor * r);
                pixels[(int) ((height - y - 1) * width * 3 + x * 3 + 1)] = (int) (.5 * g + (.5 * shadingFactor * g));
                pixels[(int) ((height - y - 1) * width * 3 + x * 3 + 2)] = (int) (.5 * b + (.5 * shadingFactor * b));
            }
        }

    }

    /**
     * **NEW LOGIC**
     * <p>
     * Added project matrix computation and light direction computation.  Changed
     * the order of the vertices in solid cube so each triangle is drawn
     * counter-clockwise.  <-- This did not work for me.  I believe there is an error
     * in my cross product computation with the set of vertices.  When I flip the order of
     * the vertices it draws some images correctly and vice versa.  Could not figure out this bug.
     * <p>
     * Maps input file coordinates to canvas.  Calls Bresenham's algorithm
     * to draw best fit lines between given endpoints and calls Scan line algorithm
     * to fill triangles.
     */
    void createImage() {

        Scanner input = getFile();

        CTM = Matrix.identityMatrix();

        //I initially created many lines of code that created triangles
        //for my solid/wireframe cube but then in order to clean up my code
        //I wrote this loop to cut down the lines of code from 36 to 12.
        My3DPoint[] points = new My3DPoint[8];
        int count = 0;
        for (double z : new double[]{-0.5, 0.5}) {
            for (double y : new double[]{-0.5, 0.5}) {
                for (double x : new double[]{-0.5, 0.5}) {
                    points[count++] = new My3DPoint(x, y, z);
                }
            }
        }

        while (input.hasNext()) {
            String command = input.next();

            scanline = new ArrayList<My3DPoint>();

            if (command.equals("DIM")) {
                width = input.nextInt() * 2;
                height = input.nextInt() * 2;
                imageSize = width * height;
                pixels = new int[imageSize * 3];
                //Initializes canvas to a white background.
                Arrays.fill(pixels, 255);
                //Initializes buffer to store z-values for all points.
                zBuffer = new double[imageSize];

                Arrays.fill(zBuffer, Double.POSITIVE_INFINITY);


            } else if (command.equals("LIGHT_DIRECTION")) {

                double x = input.nextDouble();
                double y = input.nextDouble();
                double z = input.nextDouble();

                lightVector = new Vector(x, y, z, 0);

                lightVector = lightVector.normalizeVector();


            } else if (command.equals("FRUSTUM")) {


                double left = input.nextDouble();
                double right = input.nextDouble();
                double top = input.nextDouble();
                double bottom = input.nextDouble();
                double near = input.nextDouble();
                double far = input.nextDouble();


                projection = Matrix.createPrespectiveProjection(left, right, top, bottom, near, far);


            } else if (command.equals("ORTHO")) {

                double left = input.nextDouble();
                double right = input.nextDouble();
                double top = input.nextDouble();
                double bottom = input.nextDouble();
                double near = input.nextDouble();
                double far = input.nextDouble();

                projection = Matrix.createOrthoProjection(left, right, top, bottom, near, far);

            } else if (command.equals("LOOKAT")) {


                double eye_x = input.nextDouble();
                double eye_y = input.nextDouble();
                double eye_z = input.nextDouble();
                double center_x = input.nextDouble();
                double center_y = input.nextDouble();
                double center_z = input.nextDouble();
                double up_x = input.nextDouble();
                double up_y = input.nextDouble();
                double up_z = input.nextDouble();


                lookat = Matrix.lookAT(eye_x, eye_y, eye_z, center_x, center_y, center_z, up_x, up_y, up_z);

            } else if (command.equals("LINE")) {

                My3DPoint p = getPoint(input);
                My3DPoint p1 = getPoint(input);

                drawLine(p, p1);

            } else if (command.equals("TRI")) {

                My3DPoint p = getPoint(input);
                My3DPoint p1 = getPoint(input);
                My3DPoint p2 = getPoint(input);

                drawTriangle(p, p1, p2);


            } else if (command.equals("TRANSLATE")) {

                double x = input.nextDouble();
                double y = input.nextDouble();
                double z = input.nextDouble();

                CTM = CTM.multiplyMatrix(Matrix.createTranslatationMatrix(x, y, z));

            } else if (command.equals("ROTATEX")) {

                double rotationX = input.nextDouble();

                rotationX = Math.toRadians(rotationX);

                CTM = CTM.multiplyMatrix(Matrix.createRotationX(rotationX));


            } else if (command.equals("ROTATEY")) {

                double rotationY = input.nextDouble();

                rotationY = Math.toRadians(rotationY);

                CTM = CTM.multiplyMatrix(Matrix.createRotationY(rotationY));


            } else if (command.equals("ROTATEZ")) {

                double rotationZ = input.nextDouble();

                rotationZ = Math.toRadians(rotationZ);
                CTM = CTM.multiplyMatrix(Matrix.createRotationZ(rotationZ));


            } else if (command.equals("SCALE")) {
                double x = input.nextDouble();
                double y = input.nextDouble();
                double z = input.nextDouble();

                CTM = CTM.multiplyMatrix(Matrix.createScalingMatrix(x, y, z));

            } else if (command.equals("WIREFRAME_CUBE")) {


                drawLine(points[0], points[1]);
                drawLine(points[1], points[3]);
                drawLine(points[3], points[2]);
                drawLine(points[2], points[0]);

                drawLine(points[4], points[5]);
                drawLine(points[5], points[7]);
                drawLine(points[7], points[6]);
                drawLine(points[6], points[4]);

                drawLine(points[0], points[4]);
                drawLine(points[1], points[5]);
                drawLine(points[2], points[6]);
                drawLine(points[3], points[7]);


            } else if (command.equals("SOLID_CUBE")) {

                //COUNTER CLOCKWISE (does not work --> maybe due to cross product?)
//                drawCubeSide(points, 0, 1, 3, 2); // front
//                drawCubeSide(points, 5, 4, 6, 7); // back
//                drawCubeSide(points, 2, 3, 7, 6); // top
//                drawCubeSide(points, 4, 5, 1, 0); // bottom
//                drawCubeSide(points, 1, 5, 7, 3); // right
//                drawCubeSide(points, 4, 0, 2, 6); // left

                //CLOCKWISE
                drawCubeSide(points, 0, 2, 3, 1); // front
                drawCubeSide(points, 5, 7, 6, 4); // back
                drawCubeSide(points, 2, 6, 7, 3); // top
                drawCubeSide(points, 4, 5, 1, 0); // bottom
                drawCubeSide(points, 1, 3, 7, 5); // right
                drawCubeSide(points, 4, 6, 2, 0); // left


            } else if (command.equals("LOAD_IDENTITY_MATRIX")) {

                CTM = Matrix.identityMatrix();


            } else if (command.equals("RGB")) {
                r = (int) Math.round(input.nextDouble() * 255);
                g = (int) Math.round(input.nextDouble() * 255);
                b = (int) Math.round(input.nextDouble() * 255);
            }
        }
        antiAliasing();
    }


    /**
     * Draws a side of the cube.  Each triangle is
     * drawn counter clockwise.
     * <p>
     * //points given counter-clockwise from bottom left
     *
     * @param points an array of points.
     * @param a      vertices value.
     * @param b      vertices value.
     * @param c      vertices value.
     * @param d      vertices value.
     */
    private void drawCubeSide(My3DPoint[] points, int a, int b, int c, int d) {
        drawTriangle(points[a], points[b], points[c]);
        drawTriangle(points[c], points[d], points[a]);
    }

    /**
     * Takes a larger image and averages a group of four pixels
     * into one pixel when the image is returned to its
     * normal size.
     */
    public void antiAliasing() {

        int newHeight = height / 2;
        int newWidth = width / 2;

        int[] newPixels = new int[newHeight * newWidth * 3];

        for (int row = 0; row < newHeight; row++) {
            for (int col = 0; col < newWidth; col++) {

                int topLeft = (2 * row) * width * 3 + (2 * col) * 3;
                int topRight = topLeft + 3;
                int bottomLeft = topLeft + width * 3;
                int bottomRight = bottomLeft + 3;

                newPixels[row * newWidth * 3 + col * 3] =
                        (pixels[topLeft] + pixels[topRight] + pixels[bottomLeft] + pixels[bottomRight]) / 4;

                newPixels[row * newWidth * 3 + col * 3 + 1] =
                        (pixels[topLeft + 1] + pixels[topRight + 1] + pixels[bottomLeft + 1] + pixels[bottomRight + 1]) / 4;

                newPixels[row * newWidth * 3 + col * 3 + 2] =
                        (pixels[topLeft + 2] + pixels[topRight + 2] + pixels[bottomLeft + 2] + pixels[bottomRight + 2]) / 4;

            }
        }
        pixels = newPixels;
        height = newHeight;
        width = newWidth;

    }


    /**
     * Normalizes a triangle given three points.
     *
     * @param p1 vertices value.
     * @param p2 vertices value.
     * @param p3 vertices value.
     * @return computed normalized triangle.
     */
    public Vector normalizeTriangle(My3DPoint p1, My3DPoint p2, My3DPoint p3) {

        Vector vectorP1 = new Vector(p1.x, p1.y, p1.z, 0);
        Vector vectorP2 = new Vector(p2.x, p2.y, p2.z, 0);
        Vector vectorP3 = new Vector(p3.x, p3.y, p3.z, 0);

        //NORMAL TRI = (V2 - V1) * (V3 - V1);
        Vector triangleNormal = (vectorP2.subtractVector(vectorP1)).crossProduct(vectorP3.subtractVector(vectorP1));
        //Vector triangleNormal = (vectorP3.subtractVector(vectorP1)).crossProduct(vectorP2.subtractVector(vectorP1));

        triangleNormal = triangleNormal.normalizeVector();

        return triangleNormal;
    }

    /**
     * **NEW LOGIC**
     * <p>
     * Added the order of operations that need to occur
     * in order to manipulate image correctly.
     * Modeling --> interactive rotations --> lookAT -->
     * Projections --> Mapping to screen --> Drawing
     * <p>
     * Helper method to reduce lines of code.
     *
     * @param p1 a point used to draw a triangle.
     * @param p2 a point used to draw a triangle.
     * @param p3 a point used to draw a triangle.
     */
    public void drawTriangle(My3DPoint p1, My3DPoint p2, My3DPoint p3) {


        //Stores local matrix to be manipulated.
        Matrix tempRotationHolder = CTM.multiplyMatrix(rotation);

        //LOOKS CLOSER TO TEST IMAGE WHEN PLACED ABOVE NORMALIZING
        //TRIANGLES.
        tempRotationHolder = tempRotationHolder.multiplyMatrix(lookat);

        My3DPoint newP1 = tempRotationHolder.multiplyPoint(p1);
        My3DPoint newP2 = tempRotationHolder.multiplyPoint(p2);
        My3DPoint newP3 = tempRotationHolder.multiplyPoint(p3);

        //normalize triangle here
        triangleNormal = normalizeTriangle(newP1, newP2, newP3);

        shadingFactor = lightVector.dotProduct(triangleNormal);

        if (shadingFactor < 0) {

            shadingFactor = 0;
        }

        //THIS LOOKS LIGHTER AND NOT AS CLOSE TO THE IMAGE WHEN
        //PLACED BELOW NORMALIZING TRIANGLES.
        //tempRotationHolder = tempRotationHolder.multiplyMatrix(lookat);


        if (projection != null) {

            tempRotationHolder = tempRotationHolder.multiplyMatrix(projection);
        }

        p1 = tempRotationHolder.multiplyPoint(p1);
        p2 = tempRotationHolder.multiplyPoint(p2);
        p3 = tempRotationHolder.multiplyPoint(p3);

        p1 = coordinateMapping(p1);
        p2 = coordinateMapping(p2);
        p3 = coordinateMapping(p3);

        bresenhamsAlgorithm(p1, p2);
        bresenhamsAlgorithm(p2, p3);
        bresenhamsAlgorithm(p3, p1);

        scanlineAlgorithm();
    }

    /**
     * Helper method used to retrieve point values and
     * convert the values into a 3D point.
     *
     * @param input value read from text file.
     * @return 3D point.
     */
    private My3DPoint getPoint(Scanner input) {
        return new My3DPoint(input.nextDouble(), input.nextDouble(), input.nextDouble());
    }

    /**
     * Helper method used to map input file coordinates to the canvas.
     *
     * @param input point value to be mapped to screen coordinates.
     * @param dim   the dimension of the canvas.
     * @return computed integer coordinates.
     */
    public int coordinateMap(double input, int dim) {

        return (int) ((dim - 1) * (input + 1) / 2);

    }

    /**
     * Helper method used to map 3D points to canvas.
     *
     * @param p 3D point.
     * @return computed 3D point.
     */
    public My3DPoint coordinateMapping(My3DPoint p) {

        // This gives x and y "whole number" values
        double x = coordinateMap(p.x, width);
        double y = coordinateMap(p.y, height);

        return new My3DPoint(x, y, p.z);

    }


    /**
     * ***Bresenham algorithm.  Borrowed from wikipedia***
     * Plots best fit lines that have a gentler slope.
     */
    public void plotLineLow(My3DPoint p0, My3DPoint p1) {

        double dx = p1.x - p0.x;
        double dy = p1.y - p0.y;
        int yi = 1;

        if (dy < 0) {
            yi = -1;
            dy = -dy;
        }
        double D = 2 * dy - dx;
        double y = p0.y;

        //Computes the z-slope for pixels that form line.
        double zStart = p0.z;
        double zDifference = p1.z - p0.z;
        double zStep = zDifference / (dx);

        for (double i = p0.x; i <= p1.x; i++) {

            drawPixel(i, y, zStart, r, g, b);

            //Captures xMin to be used for filling in triangle.
            scanline.add(new My3DPoint(i, y, zStart));

            //Increments left-most-z-value by computed z-slope until it reaches the right-most-z-value.
            zStart = zStart + zStep;
            if (D > 0) {

                y = y + yi;
                D = D - 2 * dx;
            }
            D = D + 2 * dy;
        }

    }

    /**
     * ***Bresenham algorithm.  Borrowed from wikipedia***
     * Plots best fit lines that have a steeper slope.
     */
    /**
     * * **NEWLY ADDED LOGIC**
     * <p>
     * Captures and computes the z-values for all points that lie
     *
     * @param p0
     * @param p1
     */
    public void plotLineHigh(My3DPoint p0, My3DPoint p1) {

        double dx = p1.x - p0.x;
        double dy = p1.y - p0.y;
        int xi = 1;
        if (dx < 0) {
            xi = -1;
            dx = -dx;
        }
        double D = 2 * dx - dy;
        double x = p0.x;

        //Computes the z-slope for pixels that form line.
        double zStart = p0.z;
        double zDifference = p1.z - p0.z;
        double zStep = zDifference / (dy);

        for (double i = p0.y; i <= p1.y; i++) {
            drawPixel(x, i, zStart, r, g, b);

            //Captures xMin to be used for filling in triangle.
            scanline.add(new My3DPoint(x, i, zStart));

            //Increments left-most-z-value by computed z-slope until it reaches the right-most-z-value.
            zStart = zStart + zStep;

            if (D > 0) {
                x = x + xi;
                D = D - 2 * dy;
            }
            D = D + 2 * dx;
        }
    }


    /**
     * Draws an outline of an image given two points.
     *
     * @param p0 a point value.
     * @param p1 a point value.
     */
    public void drawLine(My3DPoint p0, My3DPoint p1) {

        Matrix tempRotationHolder = CTM.multiplyMatrix(rotation);

        tempRotationHolder = tempRotationHolder.multiplyMatrix(lookat);

        if (projection != null) {

            tempRotationHolder = tempRotationHolder.multiplyMatrix(projection);
        }

        p0 = tempRotationHolder.multiplyPoint(p0);
        p1 = tempRotationHolder.multiplyPoint(p1);

        p0 = coordinateMapping(p0);
        p1 = coordinateMapping(p1);

        bresenhamsAlgorithm(p0, p1);

    }

    /**
     * ***Bresenham algorithm.  Borrowed from wikipedia***
     * Determines which line to draw with the given parameters.
     * Assures positive slope, flipping values accordingly.
     */
    /**
     * **NEWLY ADDED LOGIC**
     * <p>
     * Handles program interaction element.  After image is rendered, the specifications are
     * stored in the rotation matrix.  If the user decides to rotate the image, the program
     * uses the contents of the local temporary matrix to perform any command manipulations.
     *
     * @param p0 3D point value.
     * @param p1 3D point value.
     */
    public void bresenhamsAlgorithm(My3DPoint p0, My3DPoint p1) {

        if (Math.abs(p1.y - p0.y) < Math.abs(p1.x - p0.x)) {
            if (p0.x > p1.x) {
                plotLineLow(p1, p0);
            } else {
                plotLineLow(p0, p1);
            }
        } else {
            if (p0.y > p1.y) {
                plotLineHigh(p1, p0);
            } else {
                plotLineHigh(p0, p1);
            }
        }

    }

    /**
     * Algorithm sorts all points captured when drawing the outline of triangle.
     * Sorts all points so that the Y-values are in ascending order.
     * Loops through the points and calls drawPixel from xMin to xMax.
     * If triangle has a flat base then we have to deal with
     * the edge case of having a horizontal line.
     */
    public void scanlineAlgorithm() {

        Collections.sort(scanline, new Comparator<My3DPoint>() {
            public int compare(My3DPoint p, My3DPoint p2) {
                return Double.compare(p.y, p2.y);
            }
        });

        Iterator<My3DPoint> points = scanline.iterator();
        My3DPoint p = points.next();

        while (points.hasNext()) {

            double xMin = p.x;
            double xMax = p.x;

            double zLeft = p.z;
            double zRight = p.z;

            double y = p.y;

            p = points.next();

            //Captures the zvalues for the left and right most z's.
            while (p.y == y) {

                if (p.x < xMin) {

                    xMin = p.x;
                    zLeft = p.z;

                }
                if (p.x > xMax) {

                    xMax = p.x;
                    zRight = p.z;
                }

                if (points.hasNext()) {
                    p = points.next();
                } else {
                    break;
                }
            }

            //Performs the same z-slope calculation.
            double zDifference = (zRight - zLeft);
            double zStep = zDifference / (xMax - xMin);
            zLeft = zLeft + zStep;

            for (double i = xMin + 1; i < xMax; i++) {
                drawPixel(i, y, zLeft, r, g, b);

                zLeft = zLeft + zStep;

            }
        }
        scanline.clear();
    }


    /**
     * ALL CODE BELOW THIS POINT WAS PROVIDED AS STARTER CODE.
     * NOTHING WAS MODIFIED BELOW THIS POINT.
     */

    public void paintComponent(Graphics g) {
        createImage();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        WritableRaster wr_raster = image.getRaster();
        wr_raster.setPixels(0, 0, width, height, pixels);
        g.drawImage(image, 0, 0, null);
    }

    public static void main(String args[]) {
        JFrame frame = new JFrame("HOMEWORK 3");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        selectFile();

        TCSS458Paint rootPane = new TCSS458Paint();
        getDim(rootPane);
        rootPane.setPreferredSize(new Dimension(width, height));

        frame.getContentPane().add(rootPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);


        /**
         * Java swing key listener to manipulate image
         * when user presses left, right, up or down arrow keys.
         */
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            /**
             * Switch statement to perform specified action
             * as the user presses the arrow keys. (Moves the image
             * by 3 degrees depending on the key pressed).
             *
             * @param e key event value.
             */
            @Override
            public void keyPressed(KeyEvent e) {

                int keyCode = e.getKeyCode();

                double negative3 = Math.toRadians(-3.0);
                double positive3 = Math.toRadians(3.0);

                switch (keyCode) {

                    case KeyEvent.VK_UP:

                        rootPane.rotation = rootPane.rotation.multiplyMatrix(Matrix.createRotationX(positive3));
                        rootPane.repaint();

                        break;
                    case KeyEvent.VK_DOWN:

                        rootPane.rotation = rootPane.rotation.multiplyMatrix(Matrix.createRotationX(negative3));
                        rootPane.repaint();


                        break;

                    case KeyEvent.VK_LEFT:


                        rootPane.rotation = rootPane.rotation.multiplyMatrix(Matrix.createRotationY(negative3));
                        rootPane.repaint();

                        break;

                    case KeyEvent.VK_RIGHT:

                        rootPane.rotation = rootPane.rotation.multiplyMatrix(Matrix.createRotationY(positive3));
                        rootPane.repaint();

                        break;

                }
            }

            /**
             * NO FUNCTION.
             *
             * @param e key event value.
             */
            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

    }

    static File selectedFile = null;

    static private void selectFile() {
        int approve; //return value from JFileChooser indicates if the user hit cancel

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));

        approve = chooser.showOpenDialog(null);
        if (approve != JFileChooser.APPROVE_OPTION) {
            System.exit(0);
        } else {
            selectedFile = chooser.getSelectedFile();
        }
    }

    static private Scanner getFile() {
        Scanner input = null;
        try {
            input = new Scanner(selectedFile);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "There was an error with the file you chose.",
                    "File Error", JOptionPane.ERROR_MESSAGE);
        }
        return input;
    }

    static void getDim(JPanel rootPane) {
        Scanner input = getFile();

        String command = input.next();
        if (command.equals("DIM")) {
            width = input.nextInt();
            height = input.nextInt();
            rootPane.setPreferredSize(new Dimension(width, height));
        }
    }

}

