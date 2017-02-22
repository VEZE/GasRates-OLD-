package wiksinc.currencyrates.Parsers;

/**
 * Created by Ilya on 24.11.2016.
 */
public class Summary {

    private String Currency;
    private double Buy;
    private double Sell;


    public Summary()
    {
    }

    public Summary(double buy, double sell, String currency) {
        Buy = buy;
        Sell = sell;
        Currency = currency;
    }

    public void setBuy(double value)
    {
        Buy = value;
    }
    public void setSell(double value)
    {
        Sell = value;
    }

    public void setCurrency(String name)
    {
        Currency = name;
    }

    public Double getBuy()
    {
        return Buy;
    }
    public Double getSell()
    {
        return Sell;
    }

    public String getCurrency()
    {
        return Currency;
    }

    @Override
    public boolean equals (Object o) {

        Summary x = (Summary) o;

        return x.Currency.equals(Currency);
    }
}
