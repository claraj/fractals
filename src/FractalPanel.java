import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;

/**
 * Created by admin on 9/26/16.
 */
public class FractalPanel extends JPanel implements MouseMotionListener, MouseListener{

    double graphX, graphY, graphWidth, graphHeight;



    static HashMap<Integer, Color> colors;

    public FractalPanel() {
        colors = new HashMap<Integer, Color>();
        colors.put(0, Color.orange);
        colors.put(1, Color.yellow);
        colors.put(2, Color.green);
        colors.put(3, Color.cyan);
        colors.put(4, Color.blue);
        colors.put(5, Color.magenta);
        colors.put(6, Color.pink);
        colors.put(7, Color.red);

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        //g.setColor(Color.blue);
        //g.fillRect(10, 10, 500, 500);

        double height = Mandlebrot.frameHeight;
        double width = Mandlebrot.frameWidth;

        double rint, iint;

        for (double drawX = 0 ; drawX < width ; drawX++) {

            for (double drawY = 0 ; drawY < height ; drawY++) {

                //Shift 0, 0 to center and scale from 0 - 200 to -100 to +100
                //Actually to -2 to +2

                rint = drawX - width/2;
                iint = drawY - height/2;

                double r = rint/50;
                double i = iint/50;

                System.out.println(r + " " + i);

                int color = converge(r, i);

                if (color == 0) {
                    g.setColor(Color.black);
                } else {
                    g.setColor(colors.get(color % 8));
                }
                g.drawRect((int)drawX, (int)drawY, 1, 1);

            }
        }

    }

    //x is real, y is imaginary
    private int converge(double x, double y) {


        //Does zz + c converge or not?

        Complex z = new Complex(0.0, 0.0);
        Complex c = new Complex(x, y);

        for (int n = 0 ; n < 100 ; n++) {

            z = Complex.square(z).add(c);
            if (z.greaterThan(10000)) {
                System.out.println(n);
                return n;

            }

        }



        return 0;

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //TODO zoom
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

        //TODO scroll

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
