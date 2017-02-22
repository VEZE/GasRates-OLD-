package wiksinc.currencyrates.ListsUtils;

import java.util.Map;

public class DetailInfo implements Comparable<DetailInfo>{

    private String name = "";
    private String date ="";
    private String buy = "";
    private String sell = "";
    private Map.Entry<Boolean,Boolean> bestDeal = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() { return date; }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBuy() {
        return buy;
    }

    public void setBuy(String buy) {
        this.buy = buy;
    }

    public String getSell() {
        return sell;
    }

    public void setSell(String sell) {
        this.sell = sell;
    }

    public Map.Entry<Boolean,Boolean> getBestDeal() { return bestDeal; }

    public void setBestDeal(Map.Entry<Boolean,Boolean> bestDeal)  {
        this.bestDeal = bestDeal;
    }

    public Boolean isBestBuyDeal() { return bestDeal.getKey(); }

    public Boolean isBestSellDeal() { return bestDeal.getValue(); }

    @Override
    public int compareTo(DetailInfo another) {
        return this.getName().compareTo(another.getName());
    }
}