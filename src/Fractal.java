import javax.swing.*;

/**
 * Created by clara on 9/26/16.
 */
public class Fractal {

    static int frameWidth = 500;
    static int frameHeight = 500;

    static String MANDLEBROT = "mandlebrot";
    static String BURNING_SHIP = "burning ship";

    public static void main(String[] args) {


        JFrame frame = new JFrame();
        frame.setUndecorated(true);
        frame.setSize(frameWidth, frameHeight);

        FractalPanel panel = new FractalPanel(MANDLEBROT);

        frame.add(panel);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);



    }

}
