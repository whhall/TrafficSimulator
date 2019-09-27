package traffic;

/**
 *  This object implements a {@code Vehicle} of type {@code Car}.
 *  @version 20181130
 *  @author William Hall
 */
public class Car extends Vehicle {

	private static int	randomPercentage = 20;

    /**
     *  Performs a unit test on the {@code} class
     *  @param args arguments to the unit test
     */
    public static void main(String[] args)
    {
        Vehicle[]       car;

        car = new Car[2];

        car[0] = new Car();
        car[1] = new Car();

        for (Vehicle which : car) {
            System.out.println(which.getType() +
                               ": " + which.getIdentifier());
        }
    }

    public Car()
    {
        super("car");
    }
	/**
	 *  Return a label for this subclass.
	 *  @return String Label for this subclass.
	 */
	 public static String getLabel()
	 {
		return("Car");
	 }

	 /**
	  *  Return the percentage of occurrences for this.
	  *  {@code Vehicle} subclass.
	  *  @return in Percentage of random, {@code Car}s.
	  */
	  public static int getRandomPercentage()
	  {
		  return(randomPercentage);
	  }
	  /**
	   *  set the percentage of occurrences for this
	   *  {@code Vehicle} type.
	   *  @param int Percentage of random {@code Car}s
	   */
	   public static void setRandomPercentage(int newRandomPercentage)
	   {
		   randomPercentage = newRandomPercentage;
	}
}
