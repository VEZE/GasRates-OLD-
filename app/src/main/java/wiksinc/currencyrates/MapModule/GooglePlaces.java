package wiksinc.currencyrates.MapModule;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.InputStream;

public class GooglePlaces extends AsyncTask<String,String,String> {

    String temp;

    @Override
    protected String doInBackground(String... urls) {

        String page_token = "";

        if(urls.length == 3)
            page_token = "&pagetoken=" + urls[2];

        temp = makeCall("https://maps.googleapis.com/maps/api/place/textsearch/json?query="+ urls[0] +"&location="+ urls[1] +"&radius=3000&language=ru&key=AIzaSyB1g6QJ6hH_QvH8WHs1lgbReF_yPUGFMM4" + page_token); //AIzaSyCITtDmYBE1XdoLCrSuKz6MCWcxbdKUnrc

        return temp;
    }

    @NonNull
    private  static String makeCall(String url) {

        StringBuffer buffer_string = new StringBuffer(url);

        String replyString = "";

        HttpClient httpclient = new DefaultHttpClient();

        HttpGet httpget = new HttpGet(buffer_string.toString());

        try {

            HttpResponse response = httpclient.execute(httpget);

            InputStream is = response.getEntity().getContent();

            // buffer input stream the result

            BufferedInputStream bis = new BufferedInputStream(is);

            ByteArrayBuffer baf = new ByteArrayBuffer(20);

            int current = 0;

            while ((current = bis.read()) != -1) {

                baf.append((byte) current);

            }

            replyString = new String(baf.toByteArray());

        } catch (Exception e) {

            e.printStackTrace();

        }

        return replyString.trim();

    }
}
