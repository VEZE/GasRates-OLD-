package wiksinc.currencyrates.Parsers;

import org.w3c.dom.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public abstract class XmlParser {

    protected List<Summary> summaries = new ArrayList<Summary>();

    public List<Summary> getCourses() {
        return summaries;
    }

    public abstract void Parse(Document doc , String id) throws IOException;

}
