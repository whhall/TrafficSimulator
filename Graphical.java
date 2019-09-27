package traffic;

import java.util.*;
import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *  {@code Graphical} is class that displays a
 *  snapshot graphically.
 *  @version 20181130
 *  @author William Hall
 */

public class Graphical {
    public static void main(String arg[])
    {
        EventQueue.invokeLater(() -> {
                MapFrame     frame   = new MapFrame();

                /*
                 *  Section 10.2, pages 543-546.
                 */
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setTitle("Traffic Simulator");
                frame.setVisible(true);
            });
    }
}

/**
 *  {@code MapFrame} is the class that contains the {@code JFrame}
 *  of the map that displays our snapshot.
 *  @version 2018102700
 *  @author William Hall
 */
class MapFrame extends JFrame {
    private static final int    defaultFrameWidth       = 1024;
    private static final int    defaultFrameHeight      = 768;
    private JButton     		startStopButton;
    private SettingsDialog		settingsDialog;

    public MapFrame() {
        int             frameWidth;
        int             frameHeight;
        Toolkit         toolkit;
        Dimension       screenSize;
        JPanel          panel;
        JMenuBar		menuBar;
        JMenuItem		settings;

        /*
         *  Section 10.3, pages 546-554.
         */
        toolkit = Toolkit.getDefaultToolkit();
        screenSize = toolkit.getScreenSize();

        /*
         *  Make sure the frame isn't bigger than the screen.
         */
        frameWidth = screenSize.width;
        if (frameWidth > defaultFrameWidth) {
            frameWidth = defaultFrameWidth;
        }
        frameHeight = screenSize.height;
        if (frameHeight > defaultFrameHeight) {
            frameHeight = defaultFrameHeight;
        }

        /*
         *  Set the size of the frame and center it in
         *  the screen.
         */
        setSize(frameWidth, frameHeight);
//        setLocationByPlatform(true);
        setLocation((screenSize.width - frameWidth) / 2,
                    (screenSize.height - frameHeight) / 2);

        /*
         *  Create a JPanel to hold our map snapshot and our
         *  start/stop button.
         */
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        startStopButton = new JButton("Start");

        /*
         *  Section 10.4, pages 554-560.
         *
         *  Add the JComponent that displays our snapshot.
         */
        panel.add(new MapComponent(frameWidth,
                                   (frameHeight - 72),
                                   this, panel, startStopButton));

        /*
         *  Center the button and put it after our map.
         */
        startStopButton.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(startStopButton);

        add(panel);

        /*
         *  Section 12.5.1, pages 679-692
         *
         *  Add a menu with the settings dialog to our frame.
         */
         menuBar = new JMenuBar();
         setJMenuBar(menuBar);
         settings = new JMenuItem("Settings");
         settings.addActionListener(event -> {
			 	/*
			 	 *  Section 12.7, pages 743.
			 	 *
			 	 *  If there isn't a seetings dialog already we will
			 	 *  instaintiate one
			 	 */
			 	if (settingsDialog == null){
					settingsDialog = new SettingsDialog(this);
				/*
				 *  Otherwise set the values to the current percentages
				 */
				} else{
					settingsDialog.setValues();
				}
			    /*
			     * Make our dialog box visible
			     */
			     settingsDialog.setVisible(true);
			 });

		/*
		 * Add our menu item to the menu bar
		 */
		 menuBar.add(settings);
    }
}

/**
 *  {@code MapComponent} is the class that contains the
 *  {@code JComponent} of the map that displays our snapshot.
 *  @version 20181130
 *  @author William Hall
 */
class MapComponent extends JComponent {
    private final int   carCount        = 20;
    private final int   NElanes         = 2;
    private final int   SWlanes         = 2;
    private Road        road[];
    private EdgeOfMap   edgeOfMap;
    private Font        snapshotFont;
    private int         mapWidth;
    private int         mapHeight;
    private java.util.Timer     tickTimer;
    private int         tickDelay       = 500;
    private JButton     startStopButton;
    private boolean     stopped         = true;
    private Rectangle   mapLocation;

    /*
     *  Advance all the traffic objects and repaint the map.
     */
    private void tick()
    {
        if (stopped == true) {
            /*
             *  No need to tick if we're stopped.
             */
            return;
        }

        /*
         *  Execute everybody's tick().
         */
        TickManager.tick();

        /*
         *  Cause our component to be redrawn and
         *  make sure we get called again.
         */
        repaint();
        tickTimer.schedule(new Tick(), tickDelay);
    }

    /*
     *  This class is so our timer has something to do.
     */
    private class Tick extends TimerTask
    {
        public void run()
        {
            tick();
        }
    }

    /*
     *  Start ticking.
     */
    private void startTicks()
    {
        if (stopped == false) {
            /*
             *  We're already ticking.
             */
            return;
        }

        startStopButton.setText("Stop");
        stopped = false;
        tick();
    }

    /*
     *  Stop ticking.
     */
    private void stopTicks()
    {
        if (stopped == true) {
            /*
             *  We're not ticking now.
             */
            return;
        }

        startStopButton.setText("Start");
        stopped = true;
    }

    /*
     *  Sections 11.1-11.1.3, pages 587-603.
     *
     *  This is the class to toggle the start/stop button.
     */
    private class StartStopAction implements ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
            if (stopped == true) {
                startTicks();
            } else {
                stopTicks();
            }
        }
    }

    /*
     *  Section 11.1.4, pages 603-607.
     *
     *  This class is to catch window events.
     */
    private class FrameActive extends WindowAdapter {
        private boolean         stoppedWhenDeactivated;

        public void windowActivated(WindowEvent event)
        {
            if (stopped == false) {
                /*
                 *  We're already ticking.
                 */
                return;
            }

            if (stoppedWhenDeactivated == false) {
                /*
                 *  We were running when deactivated.  So, now
                 *  that we activated, restart the ticks.
                 */
                startTicks();
            }
        }

        public void windowDeactivated(WindowEvent event)
        {
            /*
             *  Remember whether we were stopped when deactivated.
             */
            stoppedWhenDeactivated = stopped;

            if (stopped == true) {
                /*
                 *  We're not ticking now.
                 */
                return;
            }

            stopTicks();
        }
    }

    /*
     *  Section 11.2, pages 607-615.
     *
     *  This class is to catch keyboard events to adjust
     *  the frequency of our ticks.
     */
    private class AdjustSpeed extends AbstractAction
    {
        public AdjustSpeed(String which)
        {
            putValue(Action.NAME, which);
            putValue(Action.SHORT_DESCRIPTION,
                     "Update traffic " + which.toLowerCase());
        }

        public void actionPerformed(ActionEvent event)
        {
            String      which;

            which = (String)getValue(Action.NAME);
            which = which.toLowerCase();
            if (which.equals("faster") == true) {
                if (tickDelay > 1) {
                    /*
                     *  Not less than 1.  Halve our time between
                     *  ticks.
                     */
                    tickDelay /= 2;
                }
            } else if (which.equals("slower") == true) {
                if (tickDelay < 10000) {
                    /*
                     *  Not more than 10secs.  Double our time
                     *  between ticks.
                     */
                    tickDelay *= 2;
                }
            }
        }
    }

    /*
     *  Section 11.3, pages 616-624.
     *
     *  This class is to catch mouse events.
     */
    private class MouseMotion implements MouseMotionListener
    {
        public void mouseMoved(MouseEvent event)
        {
            if (mapLocation == null) {
                /*
                 *  We don't know the enclosing rectangle for our
                 *  roads, yet.
                 */
                return;
            }

            if (mapLocation.contains(event.getPoint()) == true) {
                /*
                 *  Use the crosshair mouse cursor over the roads.
                 */
                setCursor(Cursor.
                            getPredefinedCursor(Cursor.
                                                    CROSSHAIR_CURSOR));
            } else {
                /*
                 *  Use the default cursor when not over the roads.
                 */
                setCursor(Cursor.getDefaultCursor());
            }
        }

        public void mouseDragged(MouseEvent event)
        {
            int         newX;
            int         newY;
            int         figureWidth;
            int         figureHeight;

            if ((mapLocation == null) ||
                (mapLocation.contains(event.getPoint()) == false)) {
                /*
                 *  We don't know the enclosing rectangle for our
                 *  roads, yet, or, the mouse isn't dragging the map.
                 */
                return;
            }

            /*
             *  Move the starting location of our roads but avoid
             *  having any part go off the screen.
             */
            figureWidth = (int)mapLocation.getWidth();
            figureHeight = (int)mapLocation.getHeight();

            newX = event.getX() - (figureWidth / 2);
            newY = event.getY() - (figureHeight / 2);

            if (newX < 0) {
                newX = 0;
            } else if (newX > (mapWidth - figureWidth)) {
                newX = mapWidth - figureWidth;
            }

            if (newY < 0) {
                newY = 0;
            } else if (newY > (mapHeight - figureHeight)) {
                newY = mapHeight - figureHeight;
            }

            mapLocation.setLocation(newX, newY);
            repaint();
        }
    }

    public MapComponent(int mapWidth, int mapHeight, MapFrame ourFrame,
                        JPanel ourParent, JButton startStopButton)
    {
        int             lane;
        AdjustSpeed     increaseSpeed;
        AdjustSpeed     decreaseSpeed;
        InputMap        inputMap;
        ActionMap       actionMap;

        /*
         *  Some things to remember.
         */
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.startStopButton = startStopButton;

        /*
         *  Create a pair of roads to display on our map.
         */
        road = new Road[2];

        road[0] = new Road("West Road", carCount, NElanes, SWlanes);
        road[1] = new Road("East Road", carCount, NElanes, SWlanes);
        edgeOfMap = new EdgeOfMap();

        /*
         *  Stitch the roads together including the edge of the map.
         */
        for (lane = NElanes - 1; (lane >= 0); --lane) {
            edgeOfMap.vehiclesGoTo(road[0], 'E', lane, lane);
            road[0].vehiclesGoTo(road[1], 'E', lane, lane);
            road[1].vehiclesGoTo(edgeOfMap, 'E', lane, lane);
        }
        for (lane = SWlanes - 1; (lane >= 0); --lane) {
            edgeOfMap.vehiclesGoTo(road[1], 'W', lane, lane);
            road[1].vehiclesGoTo(road[0], 'W', lane, lane);
            road[0].vehiclesGoTo(edgeOfMap, 'W', lane, lane);
        }

        /*
         *  Section 10.7, pages 573-582.
         *
         *  Create a font we'll use for our snapshots.
         */
        snapshotFont = new Font("Monospaced", Font.BOLD, 18);

        /*
         *  Get a timer to advance our traffic.
         */
        tickTimer = new java.util.Timer();
//        tickTimer.schedule(new tick(), tickDelay);

        /*
         *  Sections 11.1-11.1.3, pages 587-603.
         *
         *  Create an action for our start/stop button and
         *  register it.
         */
        startStopButton.addActionListener(new StartStopAction());

        /*
         *  Center our map inside the containing panel.
         */
        setAlignmentX(CENTER_ALIGNMENT);
        setAlignmentY(CENTER_ALIGNMENT);

        /*
         *  Section 11.1.4, pages 603-607.
         *
         *  Listen for window events.
         */
        ourFrame.addWindowListener(new FrameActive());

        /*
         *  Section 11.2, pages 607-615.
         *
         *  Create our event handler for keyboard events.
         */
        increaseSpeed = new AdjustSpeed("Faster");
        decreaseSpeed = new AdjustSpeed("Slower");

        /*
         *  Map the keystroke to a string.
         */
        inputMap =
            ourParent.getInputMap(JComponent.
                                    WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke('+'), "goFaster");
        inputMap.put(KeyStroke.getKeyStroke('-'), "goSlower");

        /*
         *  Apply an action to the string.
         */
        actionMap = ourParent.getActionMap();
        actionMap.put("goFaster", increaseSpeed);
        actionMap.put("goSlower", decreaseSpeed);

        /*
         *  Section 11.3, pages 616-624.
         *
         *  Listen for mouse events.
         */
        addMouseMotionListener(new MouseMotion());
    }

    /*
     *  Section 10.4, pages 557-559.
     *
     *  The components that contain us will want to know how much
     *  space we need.
     */
    public Dimension    getPreferredSize()
    {
        return(new Dimension(mapWidth, mapHeight));
    }

    /*
     *  Get a snapshot of our traffic and draw it.
     */
    public void paintComponent(Graphics graphics)
    {
        int     i;
        int     roadCount;
        int     whichRoad;
        double  beginningX;
        double  x;
        double  y;
        double  laneAscent;
        double  laneWidth;
        double  laneHeight;
        String  snapshot[][];
        Font                    originalFont;
        Graphics2D              graphics2D;
        FontRenderContext       fontContext;
        Rectangle2D             laneBounds;

        /*
         *  Save the original font to restore before we return
         *  and to use to display the road names.
         */
        originalFont = graphics.getFont();
        /*
         *  Section 10.7, pages 573-582.
         *
         *  Install the font we'll use for the vehicles.
         */
        graphics.setFont(snapshotFont);

        /*
         *  Get a snapshot of our roads to display on our map.
         */
        roadCount = road.length;
        snapshot = new String[roadCount][];

        for (whichRoad = roadCount - 1; (whichRoad >= 0); --whichRoad) {
            snapshot[whichRoad] = road[whichRoad].snapshot();
        }


        /*
         *  Section 10.7, pages 573-582.
         *
         *  We want to center the connection between roads horizontally
         *  and the centerline vertically.  All the strings in
         *  the snapshot are the same length.  So, we can perform the
         *  calculations using just the first string of the first road.
         *  Get some variables we need.
         */
        graphics2D = (Graphics2D)graphics;
        fontContext = graphics2D.getFontRenderContext();
        laneBounds = snapshotFont.getStringBounds(snapshot[0][0],
                                                  fontContext);
        laneWidth = laneBounds.getWidth();
        laneHeight = laneBounds.getHeight();
        laneAscent = laneBounds.getY();

        if (mapLocation == null) {
            /*
             *  This is the first time in here.  So, calculate
             *  the beginning coordinates.
             *
             *  Let's figure out the x coordinate of the first road.
             *  It's the center of the component minus the length
             *  of the road.
             */
            beginningX = (mapWidth / 2) - laneWidth;

            /*
             *  Let's figure out the y coordinate of the string
             *  in the middle.  Then adjust it up for half of the
             *  number of strings we have in the snapshot.
             */
            y = (mapHeight / 2) + (laneAscent / 2);
            y -= (laneHeight * snapshot[0].length) / 2;

            /*
             *  Set up a bounding rectangle that our mouse events
             *  can change to move our roads around.
             */
            mapLocation = new Rectangle((int)beginningX,
                                        (int)(y - laneHeight +
                                                laneAscent),
                                        (int)(laneWidth * roadCount),
                                        (int)(laneHeight *
                                              (snapshot[0].length + 1)));
        } else {
            /*
             *  Use the last coordinates to start our painting.
             */
            beginningX = mapLocation.getX();
            y = mapLocation.getY();
            y += laneHeight - laneAscent;
        }

        /*
         *  Draw a line for the upper edge of the road.
         */
        graphics2D.draw(new Line2D.Double(beginningX,
                                           (y + laneAscent),
                                           (beginningX +
                                            (laneWidth * roadCount)),
                                           (y + laneAscent)));

        /*
         *  Go through each string in the snapshot -- each lane of
         *  both roads -- to display the lane...
         */
        for (i = 0; (i < snapshot[0].length); ++i, y += laneHeight) {
            /*
             *  For each road...
             */
            x = beginningX;
            for (whichRoad = 0; (whichRoad < roadCount);
                                    ++whichRoad, x += laneWidth) {
                if (i == 0) {
                    double      roadNameX;
                    String      roadName;
                    Rectangle2D roadNameBounds;

                    /*
                     *  Before we draw the first lane of this road,
                     *  let's display the road name centered above
                     *  the corresponding road.  Display the road
                     *  name in the original font.
                     */
                    roadName = road[whichRoad].getName();
                    roadNameBounds = originalFont.
                                    getStringBounds(roadName,
                                                    fontContext);
                    roadNameX = x + (laneWidth / 2);
                    roadNameX -= roadNameBounds.getWidth() / 2;
                    graphics.setFont(originalFont);
                    graphics.drawString(roadName,
                                        (int)roadNameX,
                                        (int)(y - laneHeight));
                    graphics.setFont(snapshotFont);
                }

                /*
                 *  Now, draw the lane for this road.
                 */
                graphics.drawString(snapshot[whichRoad][i].
                                                replace('_', ' '),
                                    (int)x, (int)y);
            }
        }

        /*
         *  Draw a line for the lower edge of the road.
         */
        graphics2D.draw(new Line2D.Double(beginningX,
                                           (y + laneAscent),
                                           (beginningX +
                                            (laneWidth * roadCount)),
                                           (y + laneAscent)));

        /*
         *  Restore the original font.
         */
        graphics.setFont(originalFont);
    }
}
