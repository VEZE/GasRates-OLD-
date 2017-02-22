package wiksinc.currencyrates;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import wiksinc.currencyrates.ListsUtils.FavoriteListAdapter;
import wiksinc.currencyrates.ListsUtils.FavoriteObject;

public class SelectFavoriteFragment extends android.support.v4.app.Fragment {

    View rootview;
    ListView favoritElements;
    FavoriteListAdapter adapter;
    SQLite MySQLite;
    SQLiteDatabase db;
    String adit;
    String tableName = "Currencys";

    ArrayList<FavoriteObject> favoriteObject = new ArrayList<>();
    boolean IsBankSetNow = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootview = inflater.inflate(R.layout.select_favorite, container, false);

        android.support.design.widget.AppBarLayout appbar = (android.support.design.widget.AppBarLayout) getActivity().findViewById(R.id.appBar);
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)appbar.getLayoutParams();
        lp.height = 0;
        appbar.setLayoutParams(lp);

        MySQLite = new SQLite(getContext());

        tableName = "Currencys";
        adit = "Name";

        loadData(R.array.CurrencysList);

        Button confirm = new Button(getContext());
        confirm.setText("Подтвердить");
        confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                db = MySQLite.getWritableDatabase();
                ContentValues cvEntity = new ContentValues();
                String name;

                for (FavoriteObject object: favoriteObject)
                {
                    name = object.getName();

                    Cursor c = db.rawQuery("Select ID from " + tableName + " where " + adit + " = '" + name + "'", null);

                    if (c.moveToFirst()) {
                        int idIndex = c.getColumnIndex("ID");
                        int bool;

                        if(object.IsChecked())
                        {
                            bool = 1;
                        }
                        else
                        {
                            bool = 0;
                        }

                        do {
                            String entityId = c.getString(idIndex);

                            cvEntity.put("isFavorit", bool);

                            db.update(tableName, cvEntity, "ID = ?", new String[]{entityId});

                        }while (c.moveToNext());

                    }

                    c.close();
                }

                db.close();

                if(IsBankSetNow) {
                    tableName = "Banks";
                    adit = "Name";
                    IsBankSetNow = false;
                    loadData(R.array.BanksList);
                    adapter.notifyDataSetChanged();
                }
                else
                {
                    Fragment fragment = new SummaryListFragment();
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.mainFrame, fragment);
                    ft.commit();
                }
            }
        });

        favoritElements = (ListView) rootview.findViewById(R.id.favoriteList);
        favoritElements.addFooterView(confirm);

        adapter = new FavoriteListAdapter(getContext(),favoriteObject);

        favoritElements.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        return rootview;
    }

    private void loadData(int id)
    {
        favoriteObject.clear();

        db = MySQLite.getWritableDatabase();

        for (String name : getContext().getResources().getStringArray(id)) {

            Cursor c = db.rawQuery("Select isFavorit from " + tableName + " where " + adit + " = '" + name + "'", null);

            if (c.moveToFirst()) {
                int favoritIndex = c.getColumnIndex("isFavorit");
                int isFavorit = c.getInt(favoritIndex);

                favoriteObject.add(new FavoriteObject(name, (isFavorit == 1)));
            }

            c.close();
        }
        db.close();
    }
}
