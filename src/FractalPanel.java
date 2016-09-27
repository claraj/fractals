import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;

/**
 * Created by clara on 9/26/16.
 */
public class FractalPanel extends JPanel implements MouseMotionListener, MouseListener{

    double graphX, graphY, graphWidth, graphHeight;
    double frameX, frameY, frameWidth, frameHeight;

    double zoomFactor = 10;  //Clicking on an area of the image zooms in 10x

    static HashMap<Integer, Color> colors;

    FractalPanel() {

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

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    void setInitialWindow() {
        //Pixels in Frame (window)
        frameX = 0;
        frameY = 0;
        frameHeight = Mandlebrot.frameHeight;
        frameWidth = Mandlebrot.frameWidth;

        //The area of the graph being drawn
        graphX = -2;
        graphY = -2;
        graphHeight = 4;
        graphWidth = 4;    // -2 to +2
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        System.out.println("Painting x start " +
                graphX + " y start " +graphY +
                "height " + graphHeight + " width " + graphWidth);

        int pixelX = 0, pixelY = 0;

        double xIncrement = graphWidth / frameWidth;
        double yIncrement = graphHeight / frameHeight;

        //System.out.println("graph x " + graphX + " graphwidth " + graphWidth + " framewid " + frameWidth + "  xinr " + xIncrement);

        for (double x = graphX ; x <= graphWidth + graphX ; x += xIncrement) {

            for (double y = graphY ; y <= graphHeight + graphY ; y += yIncrement) {

                int color = converge(x, y);
                if (color == 0) { g.setColor(Color.black);}
                else { g.setColor(colors.get(color % 8));}
                g.drawRect(pixelX, pixelY, 1, 1);

                //System.out.println("pixel x " + pixelX + " y " + pixelY + " y " + y + " graphY " + graphY + " graphHeight " + graphHeight + " yincr " + yIncrement);

                pixelY++;

            }

            pixelY = 0;
            //System.out.println("pixel x " + pixelX + " y " + pixelY);

            pixelX++;
        }

        System.out.println("Total pixels " + pixelX + " " + pixelY);

    }

    //x is real, y is imaginary
    private int converge(double x, double y) {

        //Does zz + c converge or not?

        Complex z = new Complex(0.0, 0.0);
        Complex c = new Complex(x, y);

        for (int n = 0 ; n < 100 ; n++) {
            z = Complex.square(z).add(c);
            if (z.greaterThan(10000)) {
                return n;
            }
        }

        //If no convergence after 100 iterations, assume does not converge.
        return 0;   //This is a weird scale.

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //TODO zoom

        switch (e.getButton()) {

            case 1: {

                System.out.println("Mouse click " + e);
                int xClick = e.getX();
                int yClick = e.getY();

                //Initially frame is -2 -> +2 = 4 wide
                //Click to zoom in to 0.4 wide
                //if xClick at 450, new x graph = 1.0 -> 1.4
                //x and y are based on x, y click location

                // graphX is the where the graph plot starts, initially at -2

                graphX =  graphX + ( ( xClick / frameWidth ) * graphWidth );
                graphY =  graphY + ( ( yClick / frameHeight ) * graphHeight );

                graphHeight = graphHeight / zoomFactor;
                graphWidth = graphWidth / zoomFactor;

                graphX = graphX - (graphWidth / 2);
                graphY = graphY - (graphHeight / 2);

                System.out.println("X " + graphX + " y " + graphY + " width " + graphWidth + " height " + graphHeight);

                repaint();  //redraw.

                break;
            }

            case 2: { // fall through to case 3
             }

            case 3: {
                //zoom out to start
                setInitialWindow();
                repaint();
            }
        }

    }


    boolean dragging = false;
    int clickX, clickY;

    @Override
    public void mousePressed(MouseEvent e) {
        System.out.println("Mouse pressed " + e);
        dragging = true;
        clickX = e.getX();
        clickY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        System.out.println("Mouse released " + e);

        //TODO - this doesn't work as intended :)

        dragging = false;
        if (e.getX() == clickX && e.getY() == clickY) {
            //A click - ignore
        } else {
            //todo this isn't the right thing to do
            graphX =  graphX + ( ( e.getX() / frameWidth ) * graphWidth );
            graphY =  graphY + ( ( e.getX() / frameHeight ) * graphHeight );

            graphX = graphX - (graphWidth / 2);
            graphY = graphY - (graphHeight / 2);

            System.out.println("X " + graphX + " y " + graphY + " width " + graphWidth + " height " + graphHeight);

            repaint();  //redraw.

        }
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
        System.out.println("Mouse drag " + e);

        //Keep size same, move center to mouse

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
