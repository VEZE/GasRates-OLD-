package wiksinc.currencyrates.Parsers.HTMLParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import wiksinc.currencyrates.Parsers.Summary;
import wiksinc.currencyrates.Parsers.XmlParser;

public class UnitedCompanyParser extends XmlParser {

    @Override
    public void Parse(org.w3c.dom.Document doc, String id) throws IOException {
        summaries = parse();
    }

    public static List<Summary> parse() throws IOException {

        List<Summary> currencies = new ArrayList<>();

        Document doc = Jsoup.connect("https://www.united-company.by/azs/tseny-na-toplivo/").get();

        Elements div = doc.select("div.table-responsive");
        Elements table = div.select("table");
        Elements tr = table.select("tr");
        Elements tdtitle = tr.select("td");
        Elements td = tr.select("td.text-right");

        addCurrencies(currencies, tdtitle, td, 0, 14, 1);

        return currencies;
    }

    private static void addCurrencies(List<Summary> currencies, Elements tdtitle, Elements td,
                                      int firstTdIndex, int secondTdIndex, int step) {
        for (int i = firstTdIndex; i < secondTdIndex; i = i + step) {
            if( i == 0 || i == 3 || i == 9 || i == 12 ) {
                Summary tempSummary = new Summary();
                tempSummary.setCurrency(tdtitle.get(i).text() + tdtitle.select("br").get(0).text());
                tempSummary.setBuy(cutCurrencyBuySellValue(tdtitle.get(i + 1).text()));
                tempSummary.setSell(cutCurrencyBuySellValue(tdtitle.get(i + 2).text()));
                currencies.add(tempSummary);
            }
        }
    }

    private static double cutCurrencyBuySellValue(String value) {
        return Double.parseDouble(value.substring(0, 4));
    }
}
