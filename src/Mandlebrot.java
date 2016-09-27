import javax.swing.*;

/**
 * Created by admin on 9/26/16.
 */
public class Mandlebrot {

    static int frameWidth = 400;
    static int frameHeight = 400;

    public static void main(String[] args) {


        JFrame frame = new JFrame();
        frame.setUndecorated(true);
        frame.setSize(frameWidth, frameHeight);
        FractalPanel panel = new FractalPanel();
        frame.add(panel);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
