package wiksinc.currencyrates.ListsUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import java.util.ArrayList;

import wiksinc.currencyrates.R;

public class FavoriteListAdapter  extends BaseAdapter {

    Context ctx;
    LayoutInflater lInflater;
    ArrayList<FavoriteObject> favoritreList;

    public FavoriteListAdapter(Context context, ArrayList<FavoriteObject> favoritreList) {

        ctx = context;
        this.favoritreList = favoritreList;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return favoritreList.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return favoritreList.get(position);
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
            view = lInflater.inflate(R.layout.favorite_list_item, parent, false);
        }

        FavoriteObject favoriteObject = getFavoriteObject(position);

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.itemCheckBox);
        checkBox.setText(favoriteObject.getName());

        checkBox.setOnCheckedChangeListener(myCheckChangeList);
        checkBox.setTag(position);
        checkBox.setChecked(favoriteObject.isChecked);

        return view;
    }

    // товар по позиции
    FavoriteObject getFavoriteObject(int position) {
        return ((FavoriteObject) getItem(position));
    }

    OnCheckedChangeListener myCheckChangeList = new OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {

            getFavoriteObject((Integer) buttonView.getTag()).isChecked = isChecked;
        }
    };
}