import javax.swing.*;
import java.awt.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

/**
 * Created by clara on 9/26/16.
 *
 * Mandlebrot set, and Burning Ship fractal
 *
 *
 * todo make sure that when thread is cancelled, any subthreads are also cancelled
 * todo investigate weird drawing after a couple of zooms - probably threading
 * todo fix 27000 aioob excceptions in ForkJoinPool
 * todo write fixedThreadPool code correctly. One thread is overwriting the other. Also use arguments so it works at zoom too.
 */
public class FractalPanel extends JPanel{

    double graphX, graphY, graphWidth, graphHeight;
    int frameX, frameY, frameWidth, frameHeight;

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

    //Objects are slow. MUCH faster to use doubles and do the math
    //Benchmark for initial draw  at 300x300  iterations 50 convergence limit 1000
    //                                        iterations 300 convergence limit 6000
    // With doubles: 5, 15 ms
    // With Complex objects: about 41, 65  --> varied slightly between runs
    private long mandlebrotConverge(double re, double im, long iterations, long convergenceLimit) {

        double zx = 0;
        double zy = 0;

        double cx = re;
        double cy = im;

        for (long n = 0 ; n < iterations ; n++) {

            double realSq = (zx * zx) - (zy * zy);    //square
            double imgSq = 2 * zx * zy;

            zx = cx + realSq;
            zy = cy + imgSq;

            if  (Double.isNaN(zx) || Double.isNaN(zy) ) {
               // System.out.println("Overflow " + n);
                return n;
            }

            if ( ((zx * zx) + (zy * zy)) > (convergenceLimit*convergenceLimit)) {
               // System.out.println("Big number " + n);
                return n;
            }
        }

        //If no convergence after a load of iterations, assume does not converge.
        return 0;   //This is a weird scale.  Perhaps return NaN for not converge?

    }




    //fixme - test - output doesn't quite look right... ?
    private long burningShipConverge(double im, double re, long iterations, long convergenceLimit) {

            //function is

        // square of ( abs(real part of zn ) + abs(img part of zn ) ) + c = zn+1     <- z n+1 subscript.


        double zx = 0;
        double zy = 0;

        double cx = re;
        double cy = im;

        for (long n = 0 ; n < iterations ; n++) {

            zx = Math.abs(zx);
            zy = Math.abs(zy);

            double realSq = (zx * zx) - (zy * zy);    //square
            double imgSq = 2 * zx * zy;

            zx = cx + realSq;
            zy = cy + imgSq;

            if  (Double.isNaN(zx) || Double.isNaN(zy) ) {
                // System.out.println("Overflow " + n);
                return n;
            }

            if ( ((zx * zx) + (zy * zy)) > (convergenceLimit*convergenceLimit)) {
                // System.out.println("Big number " + n);
                return n;
            }
        }

        //If no convergence after a load of iterations, assume does not converge.
        return 0;   //This is a weird scale.  Perhaps return NaN for not converge?


        /*int iterations = 3000;
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
*/
    }

    // if background task running, stop it

    //* this method is called when user double-clicks so want to stop any tasks running.

    LinkedList<Thread> threads = new LinkedList<Thread>();

    class Settings {
        Settings(long it, long con) {
            this.iterations = it;
            this.convergenceTest = con;
        }
        long iterations;
        long convergenceTest;
    }


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

       // for (int x = 0 ; x < 10 ; x+=5){

            Thread thread = new Thread(new FractalCalcs(new Settings(i*(zoom), conv*(zoom))));
            thread.start();
            threads.push(thread);

       // }


        System.out.println("notify coord update running here ");
        //repaint();
    }


    //so in theory, the threads will take longer and longer, and
    LinkedList<int[][]> pixelsToDraw = new LinkedList<int[][]>();

    class FractalCalcs implements Runnable {

        long iterations;
        long convergenceTest;
        FractalCalcs(Settings settings) {
            this.iterations = settings.iterations;
            this.convergenceTest = settings.convergenceTest;
        }
        @Override
        public void run() {


            long timestart = System.nanoTime();
            System.out.println("background thread starts as " + System.nanoTime());
            System.out.println("Iterations = " + iterations + " convergence test = " + convergenceTest);


            //Maths in the program, no threads
            //int[][] pixelValues = testConvergences(iterations, convergenceTest);

            //Thread pool
            //int[][] pixelValues = fixedThreadPool();

            //Divide recursively
            int[][] pixelValues = forkJoinPool();

            pixelsToDraw.add(0, pixelValues);     //todo what are the queue methods called?

            System.out.println("Thread with Iterations = " + iterations + " convergence test = " + convergenceTest + " requests paint");

            repaint();   // <= but with pixelvalues

            System.out.println("background thread done at " + System.nanoTime() + " taking " + (System.nanoTime() - timestart)/1000000);

        }


        private int[][] fixedThreadPool() {

            int[][] pixelValues = new int[Fractal.frameWidth][Fractal.frameHeight];
            ExecutorService ex = Executors.newFixedThreadPool(5);

            MandlebrotRunnable mr = new MandlebrotRunnable(pixelValues,
                    0,
                    graphX,
                    graphY,
                    graphHeight, (graphWidth/2)
            );



            MandlebrotRunnable mr2 = new MandlebrotRunnable(pixelValues,
                    150,
                    graphX + (graphWidth/2) ,
                    graphY,
                    graphHeight, (graphWidth/2)
            );



            MandlebrotRunnable.frameHeight = Fractal.frameHeight;
            MandlebrotRunnable.frameWidth = (Fractal.frameWidth)/2;
            MandlebrotRunnable.iterations = iterations;
            MandlebrotRunnable.convergence = convergenceTest;


            try {
                ex.submit(mr, mr2).get();     /// second is overwriting the first, need sorting out
            }

            catch (Exception e) {
                System.out.println("Exception " + e);
            }

            return pixelValues;

        }

        private int[][] forkJoinPool() {
            // ForkJoinPool
             int[][] pixelValues = new int[Fractal.frameWidth][Fractal.frameHeight];

             ForkJoinPool pool = new ForkJoinPool();

             MandlebrotTask mt = new MandlebrotTask(pixelValues,
             0, frameWidth,
             graphX,
             graphY,
             graphX, graphWidth,
             graphHeight, graphWidth
             );
             MandlebrotTask.frameHeight = Fractal.frameHeight;
             MandlebrotTask.frameWidth = Fractal.frameWidth;
             MandlebrotTask.baseGraphXstart = graphX;
            // MandlebrotTask.pixelYend = Fractal.frameHeight;
             MandlebrotTask.iterations = iterations;
             MandlebrotTask.convergence = convergenceTest;

             pool.invoke(mt);

            return pixelValues;

        }


    }

    //This is the slow part. Test each thing for convergence and fill 2d array with pixels.
    private int[][] testConvergences(long iterations, long convergenceLimit) {

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

                long color = mandlebrotConverge(x, y, iterations, convergenceLimit);       // This is slow. Parallelize?
                //long color = burningShipConverge(x, y, iterations, convergenceLimit);


                //fixme(?) arrayindexoutofbounds, Y coord.  Same number of AIOOB as width. Possibly consequence of non-exact math?
                try {
                    pixelValues[pixelX][pixelY] = (int)(color % Integer.MAX_VALUE);
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


        if (aieCount > 0) {System.out.println("*************************** aie count " + aieCount);}


        return pixelValues;

    }




}
