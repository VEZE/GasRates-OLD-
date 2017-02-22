package wiksinc.currencyrates.ListsUtils;

import wiksinc.currencyrates.Parsers.Summary;

public class ListHelper{

    private String name;
    private String date;
    private Summary summary;


    public ListHelper(String name ,String date , Summary summary) {

        this.name = name;
        this.date = date;
        this.summary = summary;
    }

    public String getName()
    {
        return name;
    }

    public String getDate() { return date; }

    public Summary getSummary()
    {
        return summary;
    }

}
