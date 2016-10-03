import com.sun.org.apache.bcel.internal.generic.ARRAYLENGTH;

import java.util.Arrays;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Created by admin on 10/2/16.
 *
 * A very laborious way to multiply the numbers in an array by 10 :)
 *
 *
 * TODO Mandlebrot set - use this to do the complex calculations
 */


public class TryingOutForkJoinPool {

    public static void main(String[]args){

        int[] src = { 7, 9, 1, 3, 23, 4, 10, -1, 5, 23, 3234, 2, 12, 5, 2, 3, 9};
        int[] dst = new int[src.length];

        ForkJoinPool pool = new ForkJoinPool();
        ArrayMultiplyTask mt = new ArrayMultiplyTask(src, 0, src.length, dst);  //send in data
        pool.invoke(mt);
        //pool.execute(mt);  //??? should be async execution??
        System.out.println("Everything done now, result is " + Arrays.toString(dst));
    }

}


class ArrayMultiplyTask extends RecursiveAction {

    int[] src;
    int[] dst;
    int start;
    int length;

    public ArrayMultiplyTask(int[] src, int start, int len, int[] dst) {
        this.dst = dst;
        this.src = src;
        this.start = start;
        this.length = len;
    }

    static int arrayMods = 0;

    protected void smallestTask() {

        System.out.println("Smallest Task");
        System.out.println("length = " + length);
        System.out.println("start = " + start);

        for (int x = start; x < start+length; x++){

            dst[x]=src[x]*10;
            arrayMods++;
        }


        System.out.println("source" + Arrays.toString(src));
        System.out.println("dest  " + Arrays.toString(dst) + "\n");

    }


    int arbitraryLimit = 2;

    @Override
    protected void compute() {

        System.out.println("Hello from forkpool");


        if (length <= arbitraryLimit) {     //if length is 2, don't divide
            smallestTask();
        }

        else {

            int split = length / 2;

            ArrayMultiplyTask task = new ArrayMultiplyTask(src, start, split, dst);
            ArrayMultiplyTask task2 = new ArrayMultiplyTask(src, start + split, length - split, dst);

            invokeAll(task, task2);   //really should be more than one, but ....


            System.out.println("Hello from forkpool");
        }

    }
}



//public class MandlebrotForkTask extends RecursiveAction {
//
//    //split by vertical lines?
//
//    //These are pixels in frame
//    int[][] convergences = new int[Fractal.frameWidth][Fractal.frameHeight];
//
//    int xPixel;
//    long xGraph;
//    long yMin;
//    long yMax;
//
//    //The x coord of this vertical line, start at yMin, end at yMax
//    MandlebrotForkTask(int xVal, long graphX, long graphYmin, long graphYmax) {
//        //set up data here
//        this.xPixel = xVal;
//        this.xGraph = graphX;
//        this.yMin = graphYmin;
//        this.yMax = graphYmax;
//    }
//
//
//    @Override
//    protected void compute() {
//
//        //Calculate directly, or split into tasks
//
//        ArrayList<MandlebrotForkTask> tasks = new ArrayList<MandlebrotForkTask>(Fractal.frameWidth);
//
//        for (int x = 0 ; x < convergences.length ; x ++) {
//            new MandlebrotForkTask(x, 2, 0 , 3);
//        }
//
//        invokeAll(tasks);
//        invokeAll(task1, task2);   //that will then split into two other tasks if too large
//
//    }
//
//
//    protected void computeLine() {
//
//
//
//    }
//
//}
