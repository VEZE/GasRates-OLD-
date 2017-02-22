package wiksinc.currencyrates;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wiksinc.currencyrates.Parsers.Summary;
import wiksinc.currencyrates.Parsers.HTMLParser.UnitedCompanyParser;
//import wiksinc.currencyrates.Parsers.HTMLParser.BtaParser;
//import wiksinc.currencyrates.Parsers.HTMLParser.BwebParser;
//import wiksinc.currencyrates.Parsers.HTMLParser.MMParser;
//import wiksinc.currencyrates.Parsers.HTMLParser.ParitetParser;
import wiksinc.currencyrates.Parsers.LoadResource;
import wiksinc.currencyrates.Parsers.XmlParser;


public class AppStartFragment extends android.support.v4.app.Fragment {

    final String LOG_TAG = "myLogs";

    View rootview;
    Fragment fragment;
    ProgressBar progressBar;
    TextView currentBankName;
    Bundle bundle;

    final String SAVED_BOOl = "first_start";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootview = inflater.inflate(R.layout.app_start_greeting, container, false);

        progressBar = (ProgressBar) rootview.findViewById(R.id.ProgressBar);

        currentBankName = (TextView) rootview.findViewById(R.id.CurrentBankLoad) ;

        android.support.design.widget.AppBarLayout appbar = (android.support.design.widget.AppBarLayout) getActivity().findViewById(R.id.appBar);
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)appbar.getLayoutParams();
        lp.height = 0;
        appbar.setLayoutParams(lp);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;

        bundle = this.getArguments();

        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.loading,options);
        bitmap.compress(Bitmap.CompressFormat.JPEG , 100, stream);

        ImageView rl = (ImageView) rootview.findViewById(R.id.background);
        rl.setBackground(new BitmapDrawable(bitmap));

        new ResourcesService(getContext()).execute(false);

        return rootview;
    }

    public class ResourcesService extends AsyncTask<Boolean, String, String> {

        SQLite MySQLite;
        SQLiteDatabase db;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df;
        Context context;
        int iterCounter = 1;

        private Map<String, String> banks = new HashMap<>();

        @Override
        protected String doInBackground(Boolean... isForceUpdate) {

            StartUpdate(isForceUpdate[0]);
            showDB();

            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {

            if(bundle != null) {
                if (bundle.getBoolean(SAVED_BOOl)) {
                    fragment = new SelectFavoriteFragment();
                } else {
                    fragment = new SummaryListFragment();
                }

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainFrame, fragment);
                ft.commit();
            }
            else
            {

            }

        }

        protected void onProgressUpdate(String... progress) {
            currentBankName.setText(progress[0]);
            progressBar.setProgress(Integer.valueOf(progress[1]));

            System.out.println(progress[0] + "/-/" + progress[1]);
        }

        ResourcesService(Context context) {

            this.context = context;
            MySQLite = new SQLite(context);

            banks.put("А-100", "");
            banks.put("Беларусьнефть", "");
            banks.put("Газпромнефть", "");
            banks.put("Лукоил", "");
            banks.put("Юнайтед компани", "");
            banks.put("Славнефть", "");
            banks.put("Трайпл", "");
        }

        void StartUpdate(Boolean isForceUpdate) {
            for (Map.Entry<String, String> bank_url : banks.entrySet()) {
                try {
                    LoadResource(bank_url, isForceUpdate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void LoadResource(Map.Entry<String, String> bank_url, Boolean isForceUpdate) throws Exception {

            db = MySQLite.getWritableDatabase();
            ContentValues cvCurrencys = new ContentValues();
            ContentValues cvBanks = new ContentValues();

            Document doc = null;

            XmlParser parser = null;
            String id = "";

            String bankName = bank_url.getKey();

            //region switch
            switch (bankName) {
                case "А-100":
                    parser = new UnitedCompanyParser();
                    break;

                case "Беларусьнефть":
                    parser = new UnitedCompanyParser();
                    break;

                case "Газпромнефть":
                    parser = new UnitedCompanyParser();
                    break;

                case "Лукоил":
                    parser = new UnitedCompanyParser();
                    break;

                case "Юнайтед компани":
                    parser = new UnitedCompanyParser();
                    break;

                case "Славнефть":
                    parser = new UnitedCompanyParser();
                    break;

                case "Трайпл":
                    parser = new UnitedCompanyParser();
                    break;
            }
            //endregion

            // DateOfUpdate format in Banks '2016-12-01 18:31:13' --------------!

            df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            int day = 33;

            String time = df.format(calendar.getTime());

            if (!isForceUpdate) {
                publishProgress(bankName, String.valueOf((int) ((iterCounter / (float) banks.size()) * 100)));
                day = calendar.getTime().getDate();
            }

            Calendar cal1 = Calendar.getInstance();

            List<Summary> employees = null;

            Cursor c = db.rawQuery("Select * from Banks where Name = '" + bankName + "'", null);

            if (!c.moveToFirst()) {
                cvBanks.put("Name", bankName);
                int bankId = (int) db.insert("Banks", null, cvBanks);

                if (!bank_url.getValue().equals("")) {
                    doc = new LoadResource().getXMLThroughURL(bank_url.getValue());
                }

                if (parser != null) {
                    parser.Parse(doc, id);
                    employees = parser.getCourses();
                }

                for (Summary item : employees) {

                    cvCurrencys.put("Name", item.getCurrency());
                    cvCurrencys.put("Buy", item.getBuy());
                    cvCurrencys.put("Sell", item.getSell());
                    cvCurrencys.put("BankID", bankId);

                    db.insert("Currencys", null, cvCurrencys);

                }

                day = calendar.getTime().getDate();
            } else {
                cal1.setTime(df.parse(c.getString(c.getColumnIndex("DateOfUpdate"))));
            }

            if (cal1.get(Calendar.DAY_OF_MONTH) < day) {

                if (!bank_url.getValue().equals("")) {
                    doc = new LoadResource().getXMLThroughURL(bank_url.getValue());
                }

                if (parser != null) {
                    parser.Parse(doc, id);
                    employees = parser.getCourses();
                }

                String name = c.getString(c.getColumnIndex("Name"));
                int bankId = c.getInt(c.getColumnIndex("ID"));

                cvBanks.put("DateOfUpdate", time);
                db.update("Banks", cvBanks, "ID = ?", new String[]{String.valueOf(bankId)});

                for (Summary item : employees) {

                    Cursor currencysCursor = db.rawQuery("Select * from Currencys where Name = '" + item.getCurrency() + "' and BankID = '" + bankId + "'", null);
                    if (!currencysCursor.moveToFirst()) {
                        cvCurrencys.put("Name", item.getCurrency());
                        cvCurrencys.put("Buy", item.getBuy());
                        cvCurrencys.put("Sell", item.getSell());
                        cvCurrencys.put("BankID", bankId);

                        db.insert("Currencys", null, cvCurrencys);
                    } else {

                        String currencyId = currencysCursor.getString(currencysCursor.getColumnIndex("ID"));

                        cvCurrencys.put("Buy", item.getBuy());
                        cvCurrencys.put("Sell", item.getSell());
                        //cvCurrencys.put("BankID", id);

                        db.update("Currencys", cvCurrencys, "ID = ?", new String[]{currencyId});

                    }
                    currencysCursor.close();

                }

                c.close();
                db.close();
            }
            iterCounter++;
        }

        void showDB() {

            db = MySQLite.getWritableDatabase();

            Cursor c = db.rawQuery("Select * from Banks ", null);
            Cursor c1 = db.rawQuery("Select * from Currencys ", null);

            if (c.moveToFirst()) {

                int bankId = c.getColumnIndex("ID");
                int bankName = c.getColumnIndex("Name");
                int bankUpdateDate = c.getColumnIndex("DateOfUpdate");
                int bankFavorite = c.getColumnIndex("isFavorit");

                do {

                    Log.d("Bank ----- ", "ID = " + c.getInt(bankId) +
                            ",Name = " + c.getString(bankName) +
                            ",DateOfUpdate: " + c.getString(bankUpdateDate) +
                            ",isFavorit: " + (c.getInt(bankFavorite) == 1));

                } while (c.moveToNext());
            }
            c.close();

            if (c1.moveToFirst()) {

                int curId = c1.getColumnIndex("ID");
                int curName = c1.getColumnIndex("Name");
                int curBuy = c1.getColumnIndex("Buy");
                int curSell = c1.getColumnIndex("Sell");
                int curBankIdFK = c1.getColumnIndex("BankID");
                int curFavorite = c1.getColumnIndex("isFavorit");

                do {

                    Log.d("Cur ----- ", "ID = " + c1.getInt(curId) +
                            ",Name = " + c1.getString(curName) +
                            ",Buy: " + c1.getDouble(curBuy) +
                            ",Sell: " + c1.getDouble(curSell) +
                            ",BankID: " + c1.getInt(curBankIdFK) +
                            ",isFavorit: " + (c1.getInt(curFavorite) == 1));

                } while (c1.moveToNext());
            }
            c1.close();
            db.close();
        }
    }
}