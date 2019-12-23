public class Option implements Comparable<Option>
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

    public int getPrice()
    {
        return price;
    }

    public int compareTo(Option comparePrice) {

        int compare = ((Option) comparePrice).getPrice();

        return compare - this.price;

    }

}