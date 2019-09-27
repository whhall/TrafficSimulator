package traffic;

/**
 *  This object implements a {@code Vehicle} of type {@code Motorcycle}.
 *  @version 20181130
 *  @author William Hall
 */
public class Motorcycle extends Vehicle {

	private static int	randomPercentage = 5;

    /**
     *  Performs a unit test on the {@code Motorcycle} class
     *  @param args arguments to the unit test
     */
    public static void main(String[] args)
    {
        Vehicle[]       motorcycle;

        motorcycle = new Motorcycle[2];

        motorcycle[0] = new Motorcycle();
        motorcycle[1] = new Motorcycle();

        for (Vehicle which : motorcycle) {
            System.out.println(which.getType() +
                               ": " + which.getIdentifier());
        }
    }

    public Motorcycle()
    {
        super("motorcycle");
    }
	/**
	*  Return a label for this subclass.
	*  @return String Label for this subclass.
	*/
	public static String getLabel()
	{
	return("Motorcycle");
	}

	/**
	*  Return the percentage of occurrences for this.
	*  {@code Vehicle} subclass.
	*  @return in Percentage of random, {@code Motorcycle}s.
	*/
	public static int getRandomPercentage()
	{
	  return(randomPercentage);
	}
	/**
	*  set the percentage of occurrences for this
	*  {@code Vehicle} type.
	*  @param int Percentage of random {@code Motorcycle}s
	*/
	public static void setRandomPercentage(int newRandomPercentage)
	{
	   randomPercentage = newRandomPercentage;
	}
}
