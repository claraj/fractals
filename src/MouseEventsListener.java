import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by clara on 9/30/16.
 *
 */
public class MouseEventsListener extends MouseAdapter {

    FractalPanel panel;

    MouseEventsListener(FractalPanel panel) {
        this.panel = panel;
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

                panel.graphX =  panel.graphX + ( ( xClick / panel.frameWidth ) * panel.graphWidth );
                panel.graphY =  panel.graphY + ( ( yClick / panel.frameHeight ) * panel.graphHeight );

                panel.graphHeight = panel.graphHeight / panel.zoomFactor;
                panel.graphWidth = panel.graphWidth / panel.zoomFactor;

                panel.graphX = panel.graphX - (panel.graphWidth / 2);
                panel.graphY = panel.graphY - (panel.graphHeight / 2);

                System.out.println("X " + panel.graphX + " y " + panel.graphY + " width " + panel.graphWidth + " height " + panel.graphHeight);


                panel.zoom *= panel.zoomFactor;

                panel.notifyCoordinatesUpdated();

                break;
            }

            case 2: { // fall through to case 3
            }

            case 3: {
                //zoom out to start

                //notify panel, which will cancel any pending calculations

                panel.zoom = 1;

                panel.setInitialWindow();

                panel.notifyCoordinatesUpdated();
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

        int xDiff = e.getX() - clickX;
        int yDiff = e.getY() - clickY;

        if (xDiff == 0 && yDiff == 0) {
            //a click, ignore
        } else {

            System.out.println("Dragging, xdiff " + xDiff + " ydiff = " + yDiff);

            System.out.println("Before drag X " + panel.graphX + " y " + panel.graphY + " width " + panel.graphWidth + " height " + panel.graphHeight);
            System.out.println("Frame height = " + panel.frameHeight + " frame width " + panel.frameWidth );
            //translate top left by negative vector of drag, diffX, diffY, on the scale of the current graph drawn

            //  graphX, graphY are the coordinates of the graph drawn,
            // graphWidth, graphHeight are the graph's height and width.

            double  xScale = ( panel.graphWidth / panel.frameWidth ) * xDiff;

            panel.graphX -= xScale;
            double  yScale = ( panel.graphHeight / panel.frameHeight ) * yDiff;

            panel.graphY -= yScale;
        panel.graphY = panel.graphY - (panel.graphHeight / 2);

            System.out.println("After drag X " + panel.graphX + " y " + panel.graphY + " width " + panel.graphWidth + " height " + panel.graphHeight);

            panel.notifyCoordinatesUpdated();   //again got to stop threads


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
