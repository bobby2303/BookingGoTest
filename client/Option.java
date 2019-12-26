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
        return carType + " - " + supplier + " - " + price;
    }


    public int compareTo(Option comparePrice)
    {

        int compare = ((Option) comparePrice).getPrice();

        return compare - this.price;

    }

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