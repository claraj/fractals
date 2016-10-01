import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;

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

    //int[][] pixelValues;

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

        System.out.println("Painting is painting");

        if (pixelsToDraw == null || pixelsToDraw.peek() == null) {
            g.drawString("nothing to draw", 100, 100);
            return;

        }

        int[][] pixelValues = pixelsToDraw.remove();   //remove from end of queue

//        if (pixelValues == null) {
//            g.drawString("nothing to draw", 100, 100);
//            return;
//        }

//        System.out.println("Painting x start " +
//                graphX + " y start " +graphY +
//                "height " + graphHeight + " width " + graphWidth);


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

    //fixme - need iterations, etc.
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

    // if background task running, stop it

    //* this method is called when user double-clicks so want to stop any tasks running.

    LinkedList<Thread> threads = new LinkedList<Thread>();

    public void notifyCoordinatesUpdated() {
        //Calculate pixel values. when done  repaint

        for (Thread thread : threads) {
            if (thread != null) {
                System.out.println("calculations running, interrupting");   //todo is this working?
                thread.interrupt();
            }
        }

        threads = new LinkedList<Thread>();
        pixelsToDraw = new LinkedList<int[][]>();   //clear any pixels to draw


        int i = 50;
        long conv = 1000l;

        //todo more iterations for larger zoom levels?

        for (int x = 0 ; x < 1 ; x++){

            Thread thread = new Thread(new FractalCalcs(i*=5, conv*=50));
            thread.start();
            threads.push(thread);

        }


        System.out.println("notify coord update running here ");
        //repaint();
    }


    //so in theory, the threads will take longer and longer, and
    LinkedList<int[][]> pixelsToDraw = new LinkedList<int[][]>();

    class FractalCalcs implements Runnable {

        int iterations;
        long convergenceTest;
        FractalCalcs(int iterations, long convergenceTest) {
            this.iterations = iterations;
            this.convergenceTest = convergenceTest;
        }
        @Override
        public void run() {

            System.out.println("background thread starts as " + System.nanoTime());
            System.out.println("Iterations = " + iterations + " convergence test = " + convergenceTest);
            int[][] pixelValues = testConvergences(iterations, convergenceTest);
            pixelsToDraw.add(0, pixelValues);     //todo what are the queue methods called?
            System.out.println("Thread with Iterations = " + iterations + " convergence test = " + convergenceTest + " requests paint");


            repaint();   // <= but with pixelvalues


            System.out.println("background thread done at " + System.nanoTime());

        }
    }

    //This is the slow part. Test each thing for convergence and fill 2d array with pixels.
    private int[][] testConvergences(int iterations, long convergenceLimit) {

        int[][] pixelValues = new int[Fractal.frameHeight][Fractal.frameWidth];

        int pixelX = 0, pixelY = 0;

        double xIncrement = graphWidth / frameWidth;
        double yIncrement = graphHeight / frameHeight;

//
//        System.out.println("xIncrement = " + xIncrement);
//        System.out.println("yIncrement = " + yIncrement);
//        System.out.println("graphHeight = " + graphHeight);
//        System.out.println("graphWidth = " + graphWidth);

        int aieCount = 0;

        for (double x = graphX ; x < graphWidth + graphX ; x += xIncrement) {


            for (double y = graphY ; y < graphHeight + graphY ; y += yIncrement) {

                int color = mandlebrotConverge(x, y, iterations, convergenceLimit);       // This is slow. To Async task!
                //int color = burningShipConverge(x, y);


                //fixme(?) arrayindexoutofbounds, Y coord.  Same number of AIOOB as width. Possibly consequence of non-exact math?
                try {
                    pixelValues[pixelX][pixelY] = color;
                } catch (ArrayIndexOutOfBoundsException e) {
                    //System.out.println(e);

                    aieCount++;
                    //System.out.println("pixelx " + pixelX + " x = " + x  + "graph x " + graphX + " graphwidth " + graphWidth + " framewid " + frameWidth + "  xinr " + xIncrement);
                    //System.out.println("pixely = " + pixelY +  " y = " + y + "graph y " + graphY + " graphhe " + graphHeight + " framehe " + frameHeight + "  yinr " + yIncrement);

                 //   System.out.println("x = " + x + " y " + y + " xmax " + "");
                }
                pixelY++;

            }


            pixelY = 0;

            pixelX++;
        }


        System.out.println("*************************** aie count " + aieCount);


        return pixelValues;

    }




}
