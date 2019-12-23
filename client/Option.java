public class Option
{
    private String supplier;
    private String carType;
    private int price;

    public Option(String supplier, String car, int price)
    {
        this.supplier = supplier;
        this.carType = car;
        this.price = price;
    }

    public String toString()
    {
        return supplier + " " + carType + " " + price;
    }

}