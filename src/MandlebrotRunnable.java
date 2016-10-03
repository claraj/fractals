/**
 * Created by admin on 10/2/16.
 */
public class MandlebrotRunnable implements Runnable {

    int[][] pixels;

    static long iterations;
    static long convergence;

    int pixelXstart;
//    int pixelXend;
//

    double graphXstart;

    double graphXend;   //not needed if have graphHeight

    double graphYstart;
    double graphYend;     //same for graphWidth


    double graphWidth;
    double graphHeight;

    static int frameWidth;
    static int frameHeight;

    public MandlebrotRunnable(int[][] pixels,
                              int xStart,
                        //  int pixelXstart, int pixelXend,
                          double graphXstart, double graphYstart,
                          double graphHeight, double graphWidth ) {

        this.pixels = pixels;
        this.pixelXstart = xStart;
//        this.pixelXend = pixelXend;

        this.graphXstart = graphXstart;
        this.graphXend = graphXend;
        this.graphYstart = graphYstart;
        this.graphYend = graphYend;

        this.graphHeight = graphHeight;
        this.graphWidth = graphWidth;

    }

    @Override
    public void run() {

        testConvergences();

    }

    private long mandlebrotConverge(double re, double im) {

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

            if ( ((zx * zx) + (zy * zy)) > (convergence*convergence)) {
                // System.out.println("Big number " + n);
                return n;
            }
        }

        //If no convergence after a load of iterations, assume does not converge.
        return 0;   //This is a weird scale.  Perhaps return NaN for not converge?

    }



    //This is the slow part. Test each thing for convergence and fill 2d array with pixels.
    private void testConvergences() {

     //   int[][] pixelValues = new int[Fractal.frameHeight][Fractal.frameWidth];

        int pixelX = pixelXstart, pixelY = 0;

        double xIncrement = graphWidth / frameWidth;
        double yIncrement = graphHeight / frameHeight;


        System.out.println("xIncrement = " + xIncrement);
        System.out.println("yIncrement = " + yIncrement);
        System.out.println("graphHeight = " + graphHeight);
        System.out.println("graphWidth = " + graphWidth);

        int aieCount = 0;

        int pixelModCount = 0;

        //These are important. The whole of the screen is -2 -> 2 for first draw.
        //Breaking into two, they should be -2 -> 0 and 0 -> 2
        //graphX start should be, like, where this section of the graph starts, graphWidth is the width of this section

        for (double x = graphXstart ; x < graphWidth + graphXstart ; x += xIncrement) {
            for (double y = graphYstart ; y < graphHeight + graphYstart ; y += yIncrement) {

                long color = mandlebrotConverge(x, y);
                //long color = burningShipConverge(x, y, iterations, convergenceLimit);


                //fixme(?) arrayindexoutofbounds, Y coord.  Same number of AIOOB as width. Possibly consequence of non-exact math?
                try {
                    pixels[pixelX][pixelY] = (int)(color % Integer.MAX_VALUE);
                } catch (ArrayIndexOutOfBoundsException e) {
                    //System.out.println(e);

                    aieCount++;
                    //System.out.println("pixelx " + pixelX + " x = " + x  + "graph x " + graphX + " graphwidth " + graphWidth + " framewid " + frameWidth + "  xinr " + xIncrement);
                    //System.out.println("pixely = " + pixelY +  " y = " + y + "graph y " + graphY + " graphhe " + graphHeight + " framehe " + frameHeight + "  yinr " + yIncrement);

                    //   System.out.println("x = " + x + " y " + y + " xmax " + "");
                }
                pixelY++;

                pixelModCount++;

            }

            pixelY = 0;

            pixelX++;


        }


        System.out.println("Pixel mod: " + pixelModCount);
        if (aieCount > 0) {System.out.println("*************************** aie count " + aieCount);}


        //return pixelValues;

    }


}
