package traffic;

/**
 *  {@code Connectable} is an interface for connecting two traffic
 *  objects together so one may take {@code Vehicle}s from the other.
 *  @version 2018100500
 *  @author Richard Barton
 */
interface Connectable {
    /**
     *  The {@code vehiclesGoTo} method stitches traffic objects
     *  together.
     *  This method should call the {@code outgoingTo}'s
     *  {@code setConnection} method with the proper arguments so the
     *  {@code outgoingTo} object's {@code Connection} can take
     *  {@code Vehicle}s from the proper lane.
     *  @param outgoingTo The other {@code Connectable} object that
     *  will be taking {@code Vehicle}s from this object.
     *  @param heading The direction in which {@code Vehicle}s are
     *  traveling: 'N', 'S', 'E' or 'W'.
     *  @param outgoingLane The lane of this object from which
     *  {@code Vehicle}s will be taken.
     *  @param incomingLane The lane of {@code outgoingTo} which will
     *  get {@code Vehicle}s.
     */
    void        vehiclesGoTo(Connectable outgoingTo, char heading,
                             int outgoingLane, int incomingLane);

    /**
     *  The {@code setConnection} method completes the connection with
     *  another {@code Connectable} object.
     *  It should be called by the other {@code Connectable} object's
     *  {@code vehiclesGoTo} method to tell this object how to take
     *  {@code Vehicle}s.
     *  @param incoming The {@code Connection} object from which this
     *  object will receive {@code Vehicle}s.
     *  @param heading The direction in which {@code Vehicle}s are
     *  traveling: 'N', 'S', 'E' or 'W'.
     *  @param lane The lane which will receive incoming
     *  {@code Vehicle}s.
     */
    void        setConnection(Connection incoming,
                              char heading, int lane);
}
