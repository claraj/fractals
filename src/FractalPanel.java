import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
 * Created by clara on 9/26/16.
 *
 * Mandlebrot set, and Burning Ship fractal
 */
public class FractalPanel extends JPanel{

    double graphX, graphY, graphWidth, graphHeight;
    double frameX, frameY, frameWidth, frameHeight;

    double zoomFactor = 5;  //Clicking on an area of the image zooms in 10x

    int zoom = 1;   //number of times zoomed in

    int[][] pixelValues;

    static HashMap<Integer, Color> colors;


    private Color getDrawingColor(int color) {

        //Make wider color bands - 0-50 is one color, 50-100 is another ...
        //These look good on the ship, but you've got to start zooming on the mandlebrot
        int colorWideBand = (int) (color / 50) ;
        return colors.get(colorWideBand % colors.size());

    }

    FractalPanel(String fractalType) {

        setInitialWindow();

        colors = new HashMap<Integer, Color>();
        colors.put(0, Color.orange);
        colors.put(1, Color.yellow);
        colors.put(2, Color.green);
        colors.put(3, Color.cyan);
        colors.put(4, Color.blue);
        colors.put(5, Color.magenta);
        colors.put(6, Color.pink);
        colors.put(7, Color.red);

        MouseEventsListener listener = new MouseEventsListener(this);

        addMouseListener(listener);

        notifyCoordinatesUpdated();   //initial drawing
    }

    void setInitialWindow() {
        //Pixels in Frame (window)
        frameX = 0;
        frameY = 0;
        frameHeight = Fractal.frameHeight;
        frameWidth = Fractal.frameWidth;

        //The area of the graph being drawn
        graphX = -2;
        graphY = -2;
        graphHeight = 4;
        graphWidth = 4;    // -2 to +2
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (pixelValues == null) {
            g.drawString("hello", 100, 100);
            return;
        }

        System.out.println("Painting x start " +
                graphX + " y start " +graphY +
                "height " + graphHeight + " width " + graphWidth);

//        int pixelX = 0, pixelY = 0;
//
//        double xIncrement = graphWidth / frameWidth;
//        double yIncrement = graphHeight / frameHeight;

        //System.out.println("graph x " + graphX + " graphwidth " + graphWidth + " framewid " + frameWidth + "  xinr " + xIncrement);



        for (int x = 0 ; x < Fractal.frameWidth ; x++) {
            for (int y = 0 ; y < Fractal.frameHeight; y++) {

                int color = pixelValues[x][y];

                if (color == 0) {
                    g.setColor(Color.black);
                } else {
                    g.setColor(getDrawingColor(color));
                }

                g.drawRect(x, y, 1, 1);

            }
        }

//        for (double x = graphX ; x <= graphWidth + graphX ; x += xIncrement) {
//
//            for (double y = graphY ; y <= graphHeight + graphY ; y += yIncrement) {
//
//                int color = mandlebrotConverge(x, y);       // This is slow. To Async task!
//                //int color = burningShipConverge(x, y);
//
//                if (color == 0) {
//                    g.setColor(Color.black);
//                } else {
//                    g.setColor(getDrawingColor(color));
//                }
//                g.drawRect(pixelX, pixelY, 1, 1);
//
//                pixelY++;
//
//            }
//
//            pixelY = 0;
//
//            pixelX++;
//        }

    //    System.out.println("Total pixels " + pixelX + " " + pixelY);

    }




    //x is real, y is imaginary
    private int mandlebrotConverge(double x, double y, int iterations, long convergenceLimit) {

        //int iterations = zoom * 40;              //More detail, the more iterations. This definitely matters
                                            //TODO run this function with a smaller iterations, and then increase it and repaint
                                                ///TODO needs to be in Async so can be interrupted.
                                            //TODO larger bands for drawing colors with higher iterations to get bands rather than noise
        //int decidedNotConverge = zoom * 100;        // todo experiment with this.
        //Does zz + c converge or not?

        Complex z = new Complex(0.0, 0.0);
        Complex c = new Complex(x, y);

        for (int n = 0 ; n < iterations ; n++) {
            z = Complex.square(z).add(c);
            if (z.greaterThan(convergenceLimit)) {
                return n;
            }
        }

        //If no convergence after a load of iterations, assume does not converge.
        return 0;   //This is a weird scale.

    }


    private int burningShipConverge(double x, double y) {

            //function is

        // square of ( abs(real part of zn ) + abs(img part of zn ) ) + c = zn+1     <- z n+1 subscript.


        int iterations = 3000;
        long decidedNotConverge = 100000000000l;        // todo experiment with this.

        Complex z = new Complex(0.0, 0.0);
        Complex c = new Complex(x, y);

        for (int n = 0 ; n < iterations ; n++) {
            z = Complex.absSquare(z).add(c);
            if (z.greaterThan(decidedNotConverge)) {
                return n;
            }
        }

        //If no convergence after a load of iterations, assume does not converge.
        return 0;   //This is a weird scale.

    }


    public void notifyCoordinatesUpdated() {
        //Calculate pixel values and then
        testConvergences(200, 1000000);
        repaint();
    }


    //This is the slow part. Test each thing for convergence and fill 2d array with pixels.
    private void testConvergences(int iterations, long convergenceLimit) {

        pixelValues = new int[Fractal.frameHeight][Fractal.frameWidth];

        int pixelX = 0, pixelY = 0;

        double xIncrement = graphWidth / frameWidth;
        double yIncrement = graphHeight / frameHeight;


        System.out.println("xIncrement = " + xIncrement);
        System.out.println("yIncrement = " + yIncrement);
        System.out.println("graphHeight = " + graphHeight);
        System.out.println("graphWidth = " + graphWidth);

        int aieCount = 0;

        for (double x = graphX ; x < graphWidth + graphX ; x += xIncrement) {


            for (double y = graphY ; y < graphHeight + graphY ; y += yIncrement) {

                int color = mandlebrotConverge(x, y, iterations, convergenceLimit);       // This is slow. To Async task!
                //int color = burningShipConverge(x, y);


                //fixme(?) arrayindexoutofbounds, Y coord.  Same number of AIOOB as width. Possibly consequence of non-exact math?
                try {
                    pixelValues[pixelX][pixelY] = color;
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println(e);

                    aieCount++;
                    System.out.println("pixelx " + pixelX + " x = " + x  + "graph x " + graphX + " graphwidth " + graphWidth + " framewid " + frameWidth + "  xinr " + xIncrement);
                    System.out.println("pixely = " + pixelY +  " y = " + y + "graph y " + graphY + " graphhe " + graphHeight + " framehe " + frameHeight + "  yinr " + yIncrement);

                 //   System.out.println("x = " + x + " y " + y + " xmax " + "");
                }
                pixelY++;

            }


            pixelY = 0;

            pixelX++;
        }


        System.out.println("aie count " + aieCount);

    }




}
