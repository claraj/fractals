import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;

/**
 * Created by admin on 10/2/16.
 **
 *
 * TODO Mandlebrot set - use this to do the complex calculations
 * todo works for initial drawing, breaks for zoom
 */


class MandlebrotTask extends RecursiveAction {

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


    static double baseGraphXstart;


    //todo that's a lot of arguments.... anything else static?
    public MandlebrotTask(int[][] pixels,
                          int pixelXstart, int pixelXend,
                          double graphXstart, double graphYstart,
                          double sliceXstart, double sliceXwidth,
                          double graphHeight, double graphWidth ) {

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


    //A small number of lines for xstart->xend.

    //todo test on other breakdowns of task, larger areas


    protected void aFewLines() {

        //Figure out and populate array for this line(s)

        int pixelX = pixelXstart, pixelY = 0;    //Working with rows so y always 0 -> height

        double xIncrement = graphWidth / frameWidth;
        double yIncrement = graphHeight / frameHeight;


//        System.out.println("pixelX = " + pixelX);
//        System.out.println("graphXstart = " + graphXstart);
//        System.out.println("graphWidth = " + graphWidth);
//        System.out.println("yIncrement = " + yIncrement);
//        System.out.println("xIncrement = " + xIncrement);

        int aieCount = 0;

        int iterations = 0;

        for (double x = sliceXstart ; x < sliceXstart + sliceXwidth ; x += xIncrement) {
            for (double y = graphYstart ; y < graphHeight + graphYstart ; y += yIncrement) {

                long color = mandlebrotConverge(x, y);

                try { pixels[pixelX][pixelY] = (int)(color % Integer.MAX_VALUE);
                } catch (ArrayIndexOutOfBoundsException e) {
                    aieCount++;
                }
                pixelY++;

                iterations++;

            }

            pixelY = 0;

            pixelX++;
        }

       System.out.println("Slice complete...");

        if (aieCount > 0) {System.out.println("*************************** aie count " + aieCount);}

    }


    @Override
    protected void compute() {

        int arbitraryLimit = frameWidth / 10;   // make a sensible number based on framesize. If frame 500px then slices may be 50px
        //todo base on threads available to this system?


        int length = pixelXend - pixelXstart;

        if (length <= arbitraryLimit) {     //if length is 30 or less, don't divide
            aFewLines();
        }

        else {

            //divide into blocks of arbritraryLimit.. Is this better, or is it better to divide and divide and ....

            ArrayList<MandlebrotTask> tasks = new ArrayList<MandlebrotTask>();

            // 0 to 300 (or however many pixels there are in the frame)
//            for (int counter = 0 ; counter < pixelXend ; counter += arbitraryLimit) {
            for (int counter = 0 ; (counter*arbitraryLimit) < pixelXend ; counter++) {


                /**     public MandlebrotTask(int[][] pixels,
                 int pixelXstart, int pixelXend,
                 double graphXstart, double graphYstart,
                 double graphHeight, double graphWidth ) {*/

                //Chunks of X coords. Need to calculate graphXstart for this slice

                //Example: graph is 300px wide
                //current drawn area is -2 -> +2
                // graphXstart = -2   graphWidth = 4
                // width = width / (totalpixels/pixelsPerSlice)  = pixel = total / number f slices
                // graphXstart for this slice is graphWidth / number of slices + (counter * slice size)

                double slices = frameWidth / arbitraryLimit;
                double sliceWidth = graphWidth/slices;   // 4 divided by (300/30)

                double graphXsliceStart = baseGraphXstart + (sliceWidth * counter);

                int sliceXpixelStart = counter * arbitraryLimit;

               // System.out.println("In pixels X start = " + xstart + " end " + (xstart + arbitraryLimit));

                MandlebrotTask task = new MandlebrotTask(pixels,
                        sliceXpixelStart, sliceXpixelStart + arbitraryLimit,          // e.g. x = 30 to 60
                        graphXstart, graphYstart,                 //Calculate graphXstart.   graphYstart always the same.
                        graphXsliceStart, sliceWidth,
                        graphHeight, graphWidth);                 //measurements of full graph, of which this is a segment

                tasks.add(task);

            }

            invokeAll(tasks);


            /*
            //From documentation....
            int split = length / 2;

            MandlebrotTask task = new MandlebrotTask(src, start, split, dst);
            MandlebrotTask task2 = new MandlebrotTask(src, start + split, length - split, dst);

            invokeAll(task, task2);
             */
        }

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



}

