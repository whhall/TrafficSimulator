package traffic;

import java.util.*;

/**
 *  {@code Connection} is a functional interface for one of
 *  two connected objects to take {@code Vehicle}s from the other.
 *  @version 2018100500
 *  @author Richard Barton
 */
interface Connection {
    /**
     *  The {@code takeVehicle} method removes a {@code Vehicle} from
     *  this object.
     *  Since no {@code Vehicle} should be in more than one place at
     *  a time, the {@code Vehicle} being returned must be completely
     *  removed from this object.
     *  @return The next {@code Vehicle} to exit this {@code Connection}
     *  object.
     */
    Vehicle     takeVehicle();

    abstract void       tick();
}
