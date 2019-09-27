package traffic;

import java.util.*;

/**
 *  This object implements what happens to lanes at the edge of a map.
 *  Inbound lanes get random vehicles.  Outbound lanes lose their
 *  oldest vehicle.  You may use multiple instances of this class but
 *  you don't have to.  It's designed such that any number of
 *  {@code Connectable} objects may take {@code Vehicle}s from a single
 *  instance and that same single instance will consume
 *  {@code Vehicle}s from all {@code Connection} objects that are
 *  connected to any instance.
 *  @version 2018093000
 *  @author Richard Barton
 */
public class EdgeOfMap implements Connectable, Connection {
    private List<Connection>    incomingList    = new ArrayList<>();

    /**
     *  Give the {@code Connectable} object at the edge of the map a
     *  place from which to get {@code Vehicle}s.
     *  We don't care about outgoing lanes because we just provide
     *  pseudo-randomly created {@code Vehicles}.
     *  @param outgoingTo The inbound {@code Connectable} object at
     *  the edge of the map.
     *  @param heading The direction in which {@code Vehicle}s travel.
     *  @param outgoingLane We ignore this.
     *  @param incomingLane We have to pass this to the
     *  {@code Connectable} object taking {@code Vehicle}s in case it
     *  has multiple lanes.
     */
    public void vehiclesGoTo(Connectable outgoingTo, char heading,
                             int outgoingLane, int incomingLane)
    {
        outgoingTo.setConnection(this, heading, incomingLane);
    }

    /**
     *  Give the {@code Connectable} object at the edge of the map
     *  some place for their outgoing {@code Vehicle}s to go.
     *  We don't care about incoming lanes because we just consume
     *  {@code Vehicle}s.
     *  @param incoming The {@code Connection} object from which we'll
     *  take {@code Vehicle}s.
     *  @param heading We ignore this.
     *  @param lane We ignore this.
     */
    public void setConnection(Connection incoming,
                              char heading, int lane)
    {
        /*
         *  Add the given Connection object to our list of objects
         *  from which to take vehicles on a tick.
         */
        TickManager.registerTick(this, incoming);
        incomingList.add(incoming);
    }

    /**
     *  Take a vehicle from all of the objects connected to this
     *  instance of this class.
     */
    public void tick()
    {
        /*
         *  for (Connection incoming : incomingList) {
         *      incoming.takeVehicle();
         *  }
         *
         *  The following lambda expression replaces the above loop.
         */
         incomingList.forEach(incoming -> incoming.takeVehicle());
    }

    /**
     *  @return A pseudo-randomly chosen subclass of {@code Vehicle}.
     */
    public Vehicle      takeVehicle()
    {
        return(Vehicle.nextRandom());
    }
}
