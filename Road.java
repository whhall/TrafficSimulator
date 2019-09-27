package traffic;

import java.util.*;

/**
 *  {@code Road} is class that allows vehicular traffic.
 *  @version 2018100500
 *  @author Richard Barton
 */
public class Road implements Connectable {
    private final int           identifier;
    private static int          nextIdentifier;
    private final String        name;
    private final int           carCount;
    private final int           SWlanes;
    private final int           NElanes;
    private final int           hashCode;

    private Lane        SWlane[];
    private Lane        NElane[];

    /*
     *  This class manages individual lanes of a road.
     */
    private class       Lane implements Connection {
        private final boolean   reverseFlow;
        private final Vehicle   lane[];
        private Connection      incoming;

        /*
         *  flow will determine the order in which Vehicles are
         *  accessed in accessor methods.
         */
        public Lane(boolean reverseFlow) {
            this.reverseFlow = reverseFlow;
            lane = new Vehicle[carCount];
        }

        /*
         *  incoming is the place from which we receive
         *  incoming vehicles.
         */
        public void setIncoming(Connection incoming)
        {
            TickManager.registerTick(this, incoming);
            this.incoming = incoming;
        }

        /*
         *  This is called by some object which will take our outgoing
         *  vehicle.
         */
        public Vehicle  takeVehicle()
        {
            Vehicle     returnValue;

            if (carCount <= 0) {
                return(null);
            }

            /*
             *  We're going to return our outgoing vehicle.  Make sure
             *  we replace it with null so the next vehicle in the
             *  lane may move.
             */
            returnValue = lane[carCount - 1];
            lane[carCount - 1] = null;

            return(returnValue);
        }

        /*
         *  Given the index of a position in the lane, return the
         *  vehicle at that index.
         */
        public Vehicle vehicleAt(int which)
        {
            if ((which < 0) || (which >= carCount)) {
                return(null);
            }

            if (reverseFlow == true) {
                /*
                 *  Reverse the order of Vehicles.
                 */
                which = carCount - which - 1;
            }

            return(lane[which]);
        }

        /*
         *  Cause the instance to adjust the position of its vehicles.
         */
        public void tick()
        {
            int             i;
            int             j;

            /*
             *  We'll use the high indices as the outgoing end
             *  of the road and index 0 as the incoming end of
             *  the road.  For a vehicle to advance, the "slot" in
             *  front of it must be empty.  So, we'll start at the
             *  outgoing end.
             */
            for (i = carCount - 1, j = i - 1; (i > 0); --i, --j) {
                if (lane[i] != null) {
                    /*
                     *  This "slot" in the road is not empty.  So, we
                     *  can't advance the vehicle behind us into this
                     *  slot.
                     */
                    continue;
                }

                /*
                 *  This slot is empty so advance the next Vehicle.
                 *  Advance the next vehicle to this slot and empty
                 *  its former slot.
                 */
                lane[i] = lane[j];
                lane[j] = null;
            }
            if ((carCount > 0) && (lane[0] == null) &&
                (incoming != null)) {
                /*
                 *  The incoming slot is empty so get a Vehicle for it.
                 */
                lane[0] = incoming.takeVehicle();
            }
        }
    }

    /**
     *  @param name Required name of road
     *  @param carCount Required number of cars in one direction
     *  @param NElanes Required number of lanes in the
     *          north or east bound direction
     *  @param SWlanes Required number of lanes in the
     *          south or west bound direction
     */
    public Road(String name, int carCount, int NElanes, int SWlanes)
    {
        int     i;

        identifier = nextIdentifier++;
        this.name = name;
        if (carCount < 0) {
            carCount = 0;
        }
        this.carCount = carCount;
        if (SWlanes < 0) {
            SWlanes = 0;
        }
        this.SWlanes = SWlanes;
        if (NElanes < 0) {
            NElanes = 0;
        }
        this.NElanes = NElanes;
        hashCode = Objects.hash(identifier, name, carCount,
                                NElanes, SWlanes);

        /*
         *  Allocate an array to hold each of the south and west
         *  bound lanes and then each of the lanes themselves.
         */
        SWlane = new Lane[SWlanes];
        for (i = SWlanes - 1; (i >= 0); --i) {
            SWlane[i] = new Lane(true);
        }
        /*
         *  Allocate an array to hold each of the north and east
         *  bound lanes and then each of the lanes themselves.
         */
        NElane = new Lane[NElanes];
        for (i = NElanes - 1; (i>= 0); --i) {
            NElane[i] = new Lane(false);
        }
    }

    /**
     *  @return The unique identifier of the road
     */
    public int  getIdentifier()
    {
        return(identifier);
    }

    /**
     *  @return The name of the road
     */
    public String       getName()
    {
        return(name);
    }

    /**
     *  Provide this {@code Road} with a {@code Connectable} object
     *  that will take our outgoing {@code Vehicle}s.
     *  @param outgoingTo The {@code Connectable} object which will
     *  take our outgoing {@code Vehicle}s.
     *  @param heading The direction in which {@code Vehicle}s are
     *  heading: 'N', 'S', 'E' or 'W'.
     *  @param outgoingLane The index of our lane from which
     *  {@code Vehicle}s are exiting.
     *  @param incomingLane The index of {@code outgoingTo}'s lane
     *  which will take our {@code Vehicle}s.
     */
    public void vehiclesGoTo(Connectable outgoingTo, char heading,
                             int outgoingLane, int incomingLane)
    {
        if (outgoingLane < 0) {
            return;
        }

        if ((heading == 'N') || (heading == 'E')) {
            /*
             *  For vehicles heading north or east we'll use our
             *  NE lanes.
             */
            if (outgoingLane >= NElanes) {
                return;
            }

            outgoingTo.setConnection(NElane[outgoingLane],
                                     heading, incomingLane);
        } else if ((heading == 'S') || (heading == 'W')) {
            /*
             *  For vehicles heading south or west we'll use our
             *  NE lanes.
             */
            if (outgoingLane >= SWlanes) {
                return;
            }

            outgoingTo.setConnection(SWlane[outgoingLane],
                                     heading, incomingLane);
        }
    }

    /**
     *  Provide this {@code Road} with a {@code Connectable} object
     *  from which we will take our incoming {@code Vehicle}s.
     *  @param incoming The {@code Connection} object from which we
     *  will take our incoming {@code Vehicle}s.
     *  @param heading The direction in which {@code Vehicle}s are
     *  heading: 'N', 'S', 'E' or 'W'.
     *  @param lane The index of our lane which will take the
     *  {@code Vehicle}s.
     */
    public void setConnection(Connection incoming,
                              char heading, int lane)
    {
        if (lane < 0) {
            return;
        }

        if ((heading == 'N') || (heading == 'E')) {
            /*
             *  For vehicles heading north or east we'll use our
             *  NE lanes.
             */
            if (lane >= NElanes) {
                return;
            }

            /*
             *  Give the proper lane the object from which to take
             *  vehicles.
             */
            NElane[lane].setIncoming(incoming);
        } else if ((heading == 'S') || (heading == 'W')) {
            /*
             *  For vehicles heading south or west we'll use our
             *  NE lanes.
             */
            if (lane >= SWlanes) {
                return;
            }

            /*
             *  Give the proper lane the object from which to take
             *  vehicles.
             */
            SWlane[lane].setIncoming(incoming);
        }
    }

    /*
     *  Given a Lane and an array of characters, create a snapshot
     *  of that lane.
     */
    private void laneToCharArray(Lane whichLane,
                                 char charsOfVehicles[])
    {
        int     whichVehicle;
        Vehicle thisVehicle;

        for (whichVehicle = 0; (whichVehicle < carCount);
                                            ++whichVehicle) {
            /*
             *  Assume this "slot" in the road is empty.
             */
            charsOfVehicles[whichVehicle] = '_';
            thisVehicle = whichLane.vehicleAt(whichVehicle);
            if (thisVehicle != null) {
                /*
                 *  There's a vehicle in this slot in the road.
                 *  Get the first character of its type to use
                 *  to represent the vehicle on the road.
                 */
                charsOfVehicles[whichVehicle] =
                                thisVehicle.getType().charAt(0);
            }
        }
    }

    /**
     *  @return a {@code String} representation of the
     *  vehicles on this instance of the road.
     */
    public String[] snapshot()
    {
        int     i;
        int     whichLane;
        char    charsOfVehicles[];
        String  returnValue[];

        /*
         *  Number of total lanes.
         */
        i = NElanes + SWlanes;
        if ((NElanes > 0) && (SWlanes > 0)) {
            /*
             *  Need an extra String for the centerline.
             */
            ++i;
        }
        returnValue = new String[i];
        whichLane = 0;

        /*
         *  Need to build a string of characters for each lane.
         */
        charsOfVehicles = new char[carCount];

        /*
         *  Like when you're reading a map with north up, the south
         *  and west bound lanes appear first.
         */
        for (i = SWlanes - 1; (i >= 0); --i, ++whichLane) {
            laneToCharArray(SWlane[i], charsOfVehicles);

            /*
             *  Put this lane in our return value.
             */
            returnValue[whichLane] = new String(charsOfVehicles);
        }

        if ((NElanes > 0) && (SWlanes > 0)) {
            /*
             *  Need a centerline.
             */
            for (i = carCount - 1; (i >= 0); --i) {
                if ((i & 0x1) == 0) {
                    charsOfVehicles[i] = ' ';
                } else {
                    charsOfVehicles[i] = '-';
                }
            }

            /*
             *  Put the centerline in our return value.
             */
            returnValue[whichLane] = new String(charsOfVehicles);
            ++whichLane;
        }

        for (i = 0; (i < NElanes); ++i, ++whichLane) {
            laneToCharArray(NElane[i], charsOfVehicles);

            /*
             *  Put this lane in our return value.
             */
            returnValue[whichLane] = new String(charsOfVehicles);
        }
            
        return(returnValue);
    }

    /**
     *  Compare two {@code Road} objects
     *  @param otherObject the object to compare to this object
     *  @return equal or not
     */
    public boolean equals(Object otherObject)
    {
        Road    otherMe;

        if (otherObject == null) {
            /*
             *  The caller didn't give us anything.
             */
            return(false);
        }

        if (this == otherObject) {
            /*
             *  They're at the same address!
             */
            return(true);
        }

        if (getClass() != otherObject.getClass()) {
            /*
             *  They're different classes.
             */
            return(false);
        }

        /*
         *  We know the otherObject must be a non-null reference to
         *  this class.
         */
        otherMe = (Road)otherObject;

        /*
         *  We're going to trust identifier to be unique.
         */
        if (identifier != otherMe.identifier) {
            /*
             *  They're different identifiers.
             */
            return(false);
        }

        return(true);
    }

    /**
     *  @return object's hash code
     */
    public int hashCode()
    {
        return(hashCode);
    }

    /**
     *  @return {@code String} representation of object
     */
    public String toString()
    {
        return(getClass().getName() +
               "[identifier=" + identifier +
               ",name=" + name +
               ",carCount=" + carCount +
               ",SWlanes=" + SWlanes +
               ",NElanes=" + NElanes + "]");
    }

    /*
     *  Run some ticks with car configurations and make sure the road
     *  is behaving.
     */
    private static int testRoadTicks(int carCount,
                                     int NElanes, int SWlanes)
    {
        int          exitCode;
        int          i;
        int          maxTicks;
        char         thisSnapshot[][];
        char         lastSnapshot[][];
        String       roadName;
        Road         roadToTest;
        EdgeOfMap    edgeOfMap;

        exitCode = 0;
        roadName = "Road with " + carCount + " cars," +
                                  NElanes + " NElanes," +
                                  SWlanes + " SWlanes";
                                 
        roadToTest = new Road(roadName, carCount, NElanes, SWlanes);
        edgeOfMap = new EdgeOfMap();
        for (i = NElanes - 1; (i >= 0); --i) {
            /*
             *  For our north or east bound lanes...
             *  The edge of the map takes our road's east bound
             *  outgoing vehicles.
             */
            edgeOfMap.vehiclesGoTo(roadToTest, 'E', i, i);
            /*
             *  Our road takes east bound incoming vehicles from the
             *  edge of the map.
             */
            roadToTest.vehiclesGoTo(edgeOfMap, 'E', i, i);
        }
        for (i = SWlanes - 1; (i >= 0); --i) {
            /*
             *  For our south or west bound lanes...
             *  The edge of the map takes our road's west bound
             *  outgoing vehicles.
             */
            edgeOfMap.vehiclesGoTo(roadToTest, 'W', i, i);
            /*
             *  Our road takes west bound incoming vehicles from the
             *  edge of the map.
             */
            roadToTest.vehiclesGoTo(edgeOfMap, 'W', i, i);
        }

        /*
         *  We'll run some number of ticks of the clock on the given
         *  road configuration to output what happens on the road.
         *  Then we'll compare each snapshot with the last to see if
         *  vehicles advanced as they should.
         */
        thisSnapshot = null;
        lastSnapshot = null;
        maxTicks = (3 * carCount) + 2;
        System.out.println("    " + roadToTest.getName());
        for (i = 0; (i < maxTicks); ++i) {
            int         whichLane;
            String      snapshot[];

            TickManager.tick();
            snapshot = roadToTest.snapshot();

            if (thisSnapshot == null) {
                thisSnapshot = new char[snapshot.length][];
            }
            if (lastSnapshot == null) {
                lastSnapshot = new char[snapshot.length][];
            }

            /*
             *  We have to go through each lane of the snapshot.
             */
            for (whichLane = 0; (whichLane < snapshot.length);
                                        ++whichLane) {
                int         j;
                int         k;
                boolean     reverseFlow;

                if (whichLane > 0) {
                    System.out.printf("    %s\n",
                                      snapshot[whichLane]);
                } else {
                    System.out.printf("%2d: %s\n", i,
                                      snapshot[whichLane]);
                }

                if ((snapshot[whichLane].length() > 0) &&
                    ((snapshot[whichLane].charAt(0) == ' ') ||
                     (snapshot[whichLane].charAt(0) == '-'))) {
                    /*
                     *  Skip the centerline.
                     */
                    continue;
                }

                /*
                 *  Remember that, if there are SWlanes, they appear
                 *  first in the snapshot array and are reversed.
                 */
                reverseFlow = false;
                if (whichLane < SWlanes) {
                    reverseFlow = true;
                }

                thisSnapshot[whichLane] =
                                snapshot[whichLane].toCharArray();
                if (reverseFlow == true) {
                    /*
                     *  To avoid having two different loops below that
                     *  simulate the tick using the character array,
                     *  we'll just reverse the array when the flow is
                     *  reversed.
                     */
                    for (j = 0, k = thisSnapshot[whichLane].length - 1;
                         (j < k); ++j, --k) {
                        char    temp;

                        temp = thisSnapshot[whichLane][j];
                        thisSnapshot[whichLane][j] =
                                        thisSnapshot[whichLane][k];
                        thisSnapshot[whichLane][k] = temp;
                    }
                }

                if (lastSnapshot[whichLane] != null) {
                    /*
                     *  Advance the vehicles in the last snapshot to
                     *  see if this snapshot looks correct.
                     *  Since we're using EdgeOfMap, make sure the last
                     *  vehicle leaves the lane.
                     */
                    j = lastSnapshot[whichLane].length;
                    if (j > 0) {
                        lastSnapshot[whichLane][j - 1] = '_';
                    }
                    for (j = lastSnapshot[whichLane].length - 1,
                                    k = j - 1; (k >= 0); --j, --k) {
                        if (lastSnapshot[whichLane][j] != '_') {
                            /*
                             *  This slot isn't empty so we can't
                             *  advance anything to here.
                             */
                            continue;
                        }

                        /*
                         *  This slot must be empty.  Move the following
                         *  vehicle here.
                         */
                        lastSnapshot[whichLane][j] =
                                        lastSnapshot[whichLane][k];
                        lastSnapshot[whichLane][k] = '_';
                    }
                    if (lastSnapshot[whichLane].length > 0) {
                        lastSnapshot[whichLane][0] =
                                        thisSnapshot[whichLane][0];
                    }

                    /*
                     *  Now compare them to make sure tick() did the
                     *  right thing.
                     */
                    for (j = lastSnapshot[whichLane].length - 1;
                                                (j >= 0); --j) {
                        if (thisSnapshot[whichLane][j] ==
                                        lastSnapshot[whichLane][j]) {
                            /*
                             *  This one's correct.
                             */
                            continue;
                        }

                        /*
                         *  Something's wrong.
                         */
                        k = j;
                        if (reverseFlow == true) {
                            /*
                             *  In order to provide the correct position
                             *  when the flow is reversed, we need to
                             *  reverse the position number.
                             */
                            k = carCount - j - 1;
                        }
                        System.out.println("**** ERROR:  Position " +
                                           k + " should be '" +
                                           lastSnapshot[whichLane][j] +
                                           "'");
                        exitCode = 1;
                    }
                }
                lastSnapshot[whichLane] = thisSnapshot[whichLane];
            }
        }

        return(exitCode);
    }

    /**
     *  Performs a unit test on the {@code Road} class
     *  by instantiating several different roads
     *  and testing the methods.
     *  @param args arguments to the unit test
     */
    public static void main(String[] args)
    {
        int     exitCode;
        Road[]  road  = {new Road("Two Lane Road", 10, 1, 1),
                         new Road("Two Lane Road", 10, 1, 1),
                         new Road("One Lane Road SW", 10, 0, 1),
                         new Road("One Lane Road NE", 10, 1, 0),
                         new Road("Smaller Two Lane Road", 9, 1, 1),
                         new Road("Smaller Two Lane Road SW", 9, 0, 1),
                         new Road("Smaller Two Lane Road NE", 9, 1, 0),
                         new Road("Two Lane Roae", 10, 1, 1),
                         new Road("", 10, 1, 1),
                         new Road("", 0, 1, 1),
                         new Road("", 0, 0, 0),
                         new Road("Two Lane Road to nowhere", 0, 1, 1)};
        int     i;
        BitSet  usedIdentifiers;
        int     fakeCarCount;
        String  fakeRoadName;
        int     carCountToTest;
        int     NElanesToTest;
        int     SWlanesToTest;

        exitCode = 0;
        for (Road which : road) {
            /*
             *  For each road, output its toString() and its
             *  name and identifier.
             */
            System.out.println("" + which);
            System.out.println(which.getName() + ": " +
                               which.getIdentifier());
            
            for (Road other : road) {
                int     errors;
                boolean compareIdentifiers;
                boolean compareEquals;

                /*
                 *  For every other road int he array, including
                 *  this one, make sure the equals() and
                 *  hashCode() methods do what they're supposed
                 *  to do.
                 *
                 *  We're counting on identifier to be unique.
                 */
                compareIdentifiers = which.getIdentifier() ==
                                            other.getIdentifier();
                compareEquals = which.equals(other);
                errors = 0;

                /*
                 *  Be verbose.
                 */
                System.out.print(which + ".equals(" + other +
                                 ") : ");
                if (compareEquals == true) {
                    /*
                     *  So, equals() says they're equal.
                     */
                    System.out.println("true");
                    if (compareIdentifiers != true) {
                        /*
                         *  But their identifiers are different.
                         */
                        ++errors;
                    }
                } else {
                    /*
                     *  So, equals() says they aren't.
                     */
                    System.out.println("false");
                    if (compareIdentifiers != false) {
                        /*
                         *  But their identifiers are the same.
                         */
                        ++errors;
                    }
                }

                System.out.print(which + ".hashCode() == " + other +
                                 ".hashCode() : ");
                if (which.hashCode() == other.hashCode()) {
                    /*
                     *  They have the same hash code.
                     */
                    System.out.println("true");
                } else {
                    /*
                     *  They have different hash codes.
                     */
                    System.out.println("false");
                    if (compareIdentifiers != false) {
                        /*
                         *  But equals() says they're the same.
                         */
                        ++errors;
                    }
                }

                if (errors > 0) {
                    /*
                     *  We had some errors.
                     */
                    System.out.println("*****  ERROR  *****");
                    exitCode = 1;
                }
            }
        }

        /*
         *  We're going to instantiate a lot of instantances
         *  and make sure the identifiers don't get duplicated.
         */
        i = 1024 * 1024;
        usedIdentifiers = new BitSet(i);
        for (Road which : road) {
            /*
             *  Add the instantances we've already created to our
             *  database.
             */
            usedIdentifiers.set(which.getIdentifier());
        }
        fakeRoadName = "Fake Road 0";
        fakeCarCount = 1;
        for (; (i > 0); --i) {
            int      identifier;
            Road     which;

            if ((i % 100) == 0) {
                /*
                 *  Every hundred roads, change the name.
                 */
                fakeRoadName = "Fake Road " + (i / 100);
                fakeCarCount = 1;
            } else if ((i & 0x1) == 0) {
                /*
                 *  Every other road, increment the number of cars.
                 */
                ++fakeCarCount;
            }
            which = new Road(fakeRoadName, fakeCarCount,
                             (i & 0x1), ((i >> 1) & 0x1));

            /*
             *  Get this instance's identifier and see if it's
             *  already in our database.
             */
            identifier = which.getIdentifier();
            if (usedIdentifiers.get(identifier) == true) {
                /*
                 *  We've seen this identifier before.
                 */
                System.out.println(which + ": duplicate identifier(" +
                                   identifier + ")");
                exitCode = 1;
                continue;
            }

            /*
             *  Add this identifier to our database.
             */
            usedIdentifiers.set(identifier);
        }

        /*
         *  Make sure wild combinations of cars and lanes works.
         */
        for (carCountToTest = -1; (carCountToTest <= 2);
                                                ++carCountToTest) {
            for (NElanesToTest = -1; (NElanesToTest <= 2);
                                                    ++NElanesToTest) {
                for (SWlanesToTest = -1; (SWlanesToTest <= 2);
                                                    ++SWlanesToTest) {
                    exitCode += testRoadTicks(carCountToTest,
                                              NElanesToTest,
                                              SWlanesToTest);
                }
            }
        }
        /*
         *  Do a more reasonable one to look at.
         */
        exitCode += testRoadTicks(10, 1, 1);

        if (exitCode != 0) {
            /*
             *  We found a problem during unit test.
             */
            System.out.println("\n UNIT TEST FAILED!");
            System.exit(exitCode);
        }
    }
}
