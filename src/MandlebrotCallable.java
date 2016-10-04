import java.util.concurrent.Callable;

/**
 * Created by admin on 10/2/16.
 */
public class MandlebrotCallable implements Callable<Void> {

    int[][] pixels;
    static long iterations;
    static long convergence;

    //Graph size of original graph, before slicing
    double graphXstart;
    double graphYstart;

    double graphWidth;
    double graphHeight;

    //slice dimens
    double sliceXstart;
    double sliceXwidth;

    //Pixels
    static int frameWidth;
    static int frameHeight;


    int pixelXstart;
    int pixelXend;

    public MandlebrotCallable(int[][] pixels,
                              int pixelXstart, int pixelXend,
                              double graphXstart, double graphYstart,
                              double sliceXstart, double sliceXwidth,
                              double graphHeight, double graphWidth) {

        this.pixels = pixels;
        this.pixelXstart = pixelXstart;
        this.pixelXend = pixelXend;

        this.graphXstart = graphXstart;
        this.graphYstart = graphYstart;

        this.sliceXstart = sliceXstart;
        this.sliceXwidth = sliceXwidth;

        this.graphHeight = graphHeight;
        this.graphWidth = graphWidth;

    }

    @Override
    public Void call() {

        testConvergences();

        return null;

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


    private void testConvergences() {

        int pixelX = pixelXstart, pixelY = 0;

        double xIncrement = graphWidth / frameWidth;
        double yIncrement = graphHeight / frameHeight;


        System.out.println("xIncrement = " + xIncrement);
        System.out.println("yIncrement = " + yIncrement);
        System.out.println("graphHeight = " + graphHeight);
        System.out.println("graphWidth = " + graphWidth);
        System.out.println("pixelX = " + pixelX);
        System.out.println("pixelY = " + pixelY);

        int aieCount = 0;

        int pixelModCount = 0;

        //These are important. The whole of the screen is -2 -> 2 for first draw.
        //Breaking into two, they should be -2 -> 0 and 0 -> 2
        //graphX start should be, like, where this section of the graph starts, graphWidth is the width of this section

        for (double x = sliceXstart ; x < sliceXstart + sliceXwidth ; x += xIncrement) {
            for (double y = graphYstart ; y < graphHeight + graphYstart ; y += yIncrement) {

                long color = mandlebrotConverge(x, y);

                //fixme(?) arrayindexoutofbounds, Y coord.  Same number of AIOOB as width. Possibly consequence of non-exact math for dividing frame into stripes
                try {
                    pixels[pixelX][pixelY] = (int)(color % Integer.MAX_VALUE);
                    pixelModCount++;

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


        System.out.println("Pixel mod: " + pixelModCount);
        if (aieCount > 0) {System.out.println("*************************** aie count " + aieCount);}


        //return pixelValues;

    }


}
