/**
 * Option class - Object of all option details
 */
public class Option implements Comparable<Option>
{
    //Instance variables
    private String supplier;
    private String carType;
    private int price;

    /**
     * Constructor for Option
     * @param supplier
     * @param car
     * @param price
     */
    public Option(String supplier, String car, int price)
    {
        this.supplier = supplier;
        this.carType = car;
        this.price = price;
    }

    /**
     * Pretty print the results to String
     * @return
     */
    public String toString()
    {
        return carType + " - " + supplier + " - " + price;
    }


    /**
     * Price comparison using Comparable<>
     * @param comparePrice - price to compare with
     * @return the price difference to order by
     */
    public int compareTo(Option comparePrice)
    {

        int compare = ((Option) comparePrice).getPrice();

        return compare - this.price;

    }

    //Getters
    public String getSupplier()
    {
        return supplier;
    }

    public String getCarType()
    {
        return carType;
    }

    public int getPrice()
    {
        return price;
    }

    //Setters
    public void setSupplier(String supplier)
    {
        this.supplier = supplier;
    }

    public void setCarType(String carType)
    {
        this.carType = carType;
    }

    public void setPrice(int price)
    {
        this.price = price;
    }
}