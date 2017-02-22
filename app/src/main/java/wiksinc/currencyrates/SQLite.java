package wiksinc.currencyrates;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class SQLite extends SQLiteOpenHelper {
    final String LOG_TAG = "myLogs";

    SQLite(Context context) { super(context, "myDB", null, 1); }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d(LOG_TAG, "------------ Create mytable: ------------");

        db.execSQL("create table Banks ("
                + "ID integer primary key autoincrement,"
                + "Name text unique,"
                + "DateOfUpdate datetime default current_timestamp,"
                + "isFavorit integer default 0"
                + ");");

        db.execSQL("create table Currencys ("
                + "ID integer primary key autoincrement,"
                + "Name text,"
                + "Buy real,"
                + "Sell real,"
                + "BankID integer,"
                + "isFavorit integer default 0,"
                + "FOREIGN KEY(BankID) REFERENCES Banks(ID)"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
