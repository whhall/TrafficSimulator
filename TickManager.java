package traffic;

import java.util.*;

/**
 *  This object implements the management of various objects that
 *  need to tick in the proper order.
 *  @version 2018110800
 *  @author Richard Barton
 */
public class TickManager {
    private static LinkedList<Connection>       tickList        =
                                            new LinkedList<>();

    /**
     *  Run all the registered tick() methods.
     */
    public static void tick()
    {
        /*
         *  for (Connection tock : tickList) {
         *      tock.tick();
         *  }
         *
         *  The following lambda expression replaces the above loop.
         */
         tickList.forEach(tock -> tock.tick());
    }

    /**
     */
    public static void  registerTick(Connection toAdd,
                                     Connection before)
    {
        int     indexOfBefore;

        indexOfBefore = tickList.indexOf(before);
        if (indexOfBefore < 0) {
            indexOfBefore = 0;
        }
        tickList.remove(toAdd);
        tickList.add(indexOfBefore, toAdd);
    }
}
