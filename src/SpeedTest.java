import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;

/**
 * Created by clara on 10/3/16.
 */
public class SpeedTest {


    double frameX = 0, frameY = 0,
            frameWidth = Fractal.frameWidth, frameHeight = Fractal.frameHeight;

    double zoomFactor = 5;  //Clicking on an area of the image zooms in 5x


    //These are the factors to change
    int zoom = 1;   //number of times zoomed in
    double graphX, graphY, graphWidth, graphHeight;

    long iterations = 20000;
    long convergenceLimit = 1000000000l;


    class Settings {
        int szoom = 1;   //number of times zoomed in
        double sgraphX;
        double sgraphY;
        double sgraphWidth;
        double sgraphHeight;

        public Settings(double graphX, double graphY, double graphWidth, double graphHeight, int zoom) {
            this.szoom = zoom;
            this.sgraphX = graphX;
            this.sgraphY = graphY;
            this.sgraphWidth = graphWidth;
            this.sgraphHeight = graphHeight;
        }
    }

    public static void main(String[] args) {

        new SpeedTest().runTests();

    }

    public void runTests() {

        //Set values

        ArrayList<Settings> settings = new ArrayList<Settings>();

            /*

    graphx -2.0 graph y -2.0 width 4.0 zoom 1
 graphx -1.0533333333333332 graph y -0.013333333333333308 width 0.8 zoom 5
 graphx -0.8053333333333331 graph y 0.12000000000000004 width 0.16 zoom 25
 graphx -0.7402666666666664 graph y 0.17226666666666668 width 0.032 zoom 125


 graphx -0.7273599999999997 graph y 0.18186666666666668 width 0.0064 zoom 625
 graphx -0.7273386666666664 graph y 0.186112 width 0.00128 zoom 3125
 graphx -0.7270911999999997 graph y 0.18687146666666668 width 2.5600000000000004E-4 zoom 15625
     */

        Settings settings1 = new Settings(-2.0, -2.0, 4.0, 4.0, 1);
        settings.add(settings1);
        Settings settings5 = new Settings(-1.0533333333333332, -0.013333333333333308, 0.8, 0.8, 5);
        settings.add(settings5);
        Settings settings25 = new Settings(-0.8053333333333331, 0.12000000000000004 ,0.16, 0.16, 25);
        settings.add(settings25);
        Settings settings125 = new Settings(-0.7402666666666664, 0.17226666666666668, 0.032, 0.032, 125);
        settings.add(settings125);


        Settings settings625 = new Settings( -0.7273599999999997, 0.18186666666666668, 0.0064, 0.0064, 625);
        settings.add(settings625);

        //yawn
//        Settings settings3125 = new Settings(-2.0, -2.0, 4.0, 4.0, 1);
//        settings.add(settings3125);
//        Settings settings15625 = new Settings(-2.0, -2.0, 4.0, 4.0, 1);
//        settings.add(settings15625);



        // TODO test fixedThreadPool

        System.out.println("todo test fixedThreadPool");


        long start = System.currentTimeMillis();

        for (Settings s : settings) {

            long timeStart = System.currentTimeMillis();
            System.out.println();

            set(s);

            int[][] pixelValues = new int[Fractal.frameWidth][Fractal.frameHeight];

            ForkJoinPool pool = new ForkJoinPool();

            MandlebrotTask mt = new MandlebrotTask(pixelValues,
                    0, (int)frameWidth,
                    graphX, graphY,        //graph dimens x y start
                    graphX, graphWidth,       //slicw x dimens
                    graphHeight, graphWidth       //graph dimens
            );

            MandlebrotTask.frameHeight = Fractal.frameHeight;
            MandlebrotTask.frameWidth = Fractal.frameWidth;
            MandlebrotTask.baseGraphXstart = graphX;
            MandlebrotTask.iterations = iterations;
            MandlebrotTask.convergence = convergenceLimit;

            pool.invoke(mt);

            long timeEnd = System.currentTimeMillis();
            double duration = timeEnd - timeStart;

            System.out.println("At zoom level " + s.szoom + " time taken = " + duration/1000);

        }

        long end = System.currentTimeMillis();
        double totalDuration = (double)(end - start) / 1000;
        System.out.println("For fixedThreadPool pool, total run time = " + totalDuration + "\n");












        //Test with RecursiveTask, ForkJoin pool

        System.out.println("Testing RecursiveTask Fork Join Pool ");

        start = System.currentTimeMillis();

        for (Settings s : settings) {

            long timeStart = System.currentTimeMillis();
            System.out.println();

            set(s);

            int[][] pixelValues = new int[Fractal.frameWidth][Fractal.frameHeight];

            ForkJoinPool pool = new ForkJoinPool();

            MandlebrotTask mt = new MandlebrotTask(pixelValues,
                    0, (int)frameWidth,
                    graphX, graphY,        //graph dimens x y start
                    graphX, graphWidth,       //slicw x dimens
                    graphHeight, graphWidth       //graph dimens
            );

            MandlebrotTask.frameHeight = Fractal.frameHeight;
            MandlebrotTask.frameWidth = Fractal.frameWidth;
            MandlebrotTask.baseGraphXstart = graphX;
            MandlebrotTask.iterations = iterations;
            MandlebrotTask.convergence = convergenceLimit;

            pool.invoke(mt);

            long timeEnd = System.currentTimeMillis();
            double duration = timeEnd - timeStart;

            System.out.println("At zoom level " + s.szoom + " time taken = " + duration/1000);

        }

        end = System.currentTimeMillis();
        totalDuration = (double)(end - start) / 1000;
        System.out.println("For forkjoin pool, total run time = " + totalDuration + "\n");



        //Test calculation speed with plain doubles

        System.out.println("Testing with doubles");

        start = System.currentTimeMillis();

        for (Settings s : settings) {

            long timeStart = System.currentTimeMillis();
            System.out.println();

            set(s);

            int[][] pixelValues = testConvergences(iterations, convergenceLimit);

            long timeEnd = System.currentTimeMillis();
            double duration = timeEnd - timeStart;

            System.out.println("At zoom level " + s.szoom + " time taken = " + duration/1000);

        }

        end = System.currentTimeMillis();
        totalDuration = (double)(end - start) / 1000;
        System.out.println("For doubles, total run time = " + totalDuration);



        //Test calculation speed with Complex objects (Spoiler: slow)


        System.out.println("Testing with Complex objects");

        start = System.currentTimeMillis();

        for (Settings s : settings) {

            long timeStart = System.currentTimeMillis();
            System.out.println();

            set(s);

            int[][] pixelValues = testComplexConvergences(iterations, convergenceLimit);

            long timeEnd = System.currentTimeMillis();
            double duration = timeEnd - timeStart;

            System.out.println("At zoom level " + s.szoom + " time taken = " + duration/1000);

        }

        end = System.currentTimeMillis();
        totalDuration = (double)(end - start) / 1000;
        System.out.println("For Complex objects, total run time = " + totalDuration + "\n");




    }

    private void set(Settings s) {
        this.graphHeight = s.sgraphHeight;
        this.graphWidth = s.sgraphWidth;
        this.zoom = s.szoom;
        this.graphX = s.sgraphX;
        this.graphY = s.sgraphHeight;

    }





    /* Plain double objects, no threading */

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

                long color = mandlebrotDoubleConverge(x, y, iterations, convergenceLimit);       // This is slow. Parallelize?
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

    private long mandlebrotDoubleConverge(double re, double im, long iterations, long convergenceLimit) {

        double zx = 0;
        double zy = 0;

        for (long n = 0 ; n < iterations ; n++) {

            double realSq = (zx * zx) - (zy * zy);    //square
            double imgSq = 2 * zx * zy;

            zx = re + realSq;
            zy = im + imgSq;
            
            if  (Double.isNaN(zx) || Double.isNaN(zy) ) {
                return n;
            }

            if ( ((zx * zx) + (zy * zy)) > (convergenceLimit*convergenceLimit)) {
                return n;
            }
        }

        //If no convergence after a load of iterations, assume does not converge.
        return 0;   //This is a weird scale.  Perhaps return NaN for not converge?

    }



    /* Complex Objects, no threading */
    private int[][] testComplexConvergences(long iterations, long convergenceLimit) {

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

                long color = mandlebrotComplexConverge(x, y, iterations, convergenceLimit);       // This is slow. Parallelize?
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

    private int mandlebrotComplexConverge(double x, double y, long iterations, long convergenceLimit) {

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




}
