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

                //TODO cancel calculations

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
        if (e.getX() == clickX && e.getY() == clickY) {
            //A click - ignore
        } else {
            //todo this isn't the right thing to do
            panel.graphX =  panel.graphX + ( ( e.getX() / panel.frameWidth ) * panel.graphWidth );
            panel.graphY =  panel.graphY + ( ( e.getX() / panel.frameHeight ) * panel.graphHeight );

            panel.graphX = panel.graphX - (panel.graphWidth / 2);
            panel.graphY = panel.graphY - (panel.graphHeight / 2);

            System.out.println("X " + panel.graphX + " y " + panel.graphY + " width " + panel.graphWidth + " height " + panel.graphHeight);

//            panel.repaint();  //redraw.
            panel.notifyCoordinatesUpdated();


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
