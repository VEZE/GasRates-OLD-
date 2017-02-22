package wiksinc.currencyrates.Parsers;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class LoadResource {/*extends AsyncTask<String,String,Document> {

    @Override
    protected Document doInBackground(String... params) {
        Document doc = null;

        try {
            Log.d("URL",params[0]);
            doc = getXMLThroughURL(params[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return doc;
    }*/

    public Document getXMLThroughURL(String p_sURL)  throws Exception
    {
        Document doc;
        try{
            URL oURL = new URL(p_sURL);

            HttpURLConnection urlConnection = (HttpURLConnection) oURL.openConnection();

            try {
                urlConnection.setConnectTimeout(250);
                urlConnection.setDoInput(true);
                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());

                DocumentBuilderFactory  objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();

                doc = objDocumentBuilder.parse(stream);
            } finally {
                urlConnection.disconnect();
            }

        } catch (Exception ex) {
            throw ex;
        }

        return doc;
    }

    private static Document parseXML(InputSource stream)throws Exception
    {
        DocumentBuilderFactory objDocumentBuilderFactory;
        DocumentBuilder objDocumentBuilder;
        Document doc;
        try {
            objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
            objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();

            doc = objDocumentBuilder.parse(stream);
        } catch (Exception ex) {
            throw ex;
        }

        return doc;
    }

}
