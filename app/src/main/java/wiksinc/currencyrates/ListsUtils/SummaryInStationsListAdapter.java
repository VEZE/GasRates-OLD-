package wiksinc.currencyrates.ListsUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import wiksinc.currencyrates.Parsers.Summary;
import wiksinc.currencyrates.R;

/**
 * Created by Ilya on 08.12.2016.
 */

public class SummaryInStationsListAdapter extends BaseAdapter {

    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Summary> summaries;

    public SummaryInStationsListAdapter(Context context, ArrayList<Summary> summaries) {

        ctx = context;
        this.summaries = summaries;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return summaries.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return summaries.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.list_item, parent, false);
        }

        Summary summary = getCurrency(position);

        // заполняем View в пункте списка данными из товаров: наименование, цена
        // и картинка
        ((TextView) view.findViewById(R.id.currencyTitle)).setText(summary.getCurrency());
        ((TextView) view.findViewById(R.id.BuyAmount)).setText(String.valueOf(summary.getBuy()));
        ((TextView) view.findViewById(R.id.SellAmount)).setText(String.valueOf(summary.getSell()));

        return view;
    }

    // товар по позиции
    Summary getCurrency(int position) {
        return ((Summary) getItem(position));
    }}
