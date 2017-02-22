package wiksinc.currencyrates.ListsUtils;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import wiksinc.currencyrates.R;

public class ExpendableSummaryListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<HeaderInfo> deptList;

    public ExpendableSummaryListAdapter(Context context, ArrayList<HeaderInfo> deptList) {
        this.context = context;
        this.deptList = deptList;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<DetailInfo> productList = deptList.get(groupPosition).getProductList();
        return productList.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View view, ViewGroup parent) {

        DetailInfo detailInfo = (DetailInfo) getChild(groupPosition, childPosition);
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.child_row, null);
        }

        TextView bankName = (TextView) view.findViewById(R.id.bankName);
        bankName.setText(detailInfo.getName().trim());

        TextView dateOfUpdate = (TextView) view.findViewById(R.id.DateofUpdate);
        dateOfUpdate.setText(detailInfo.getDate().trim());

        TextView currencySell = (TextView) view.findViewById(R.id.CurrencySell);
        currencySell.setTag(childPosition);
        currencySell.setText(detailInfo.getSell().trim());

        TextView currencyBuy = (TextView) view.findViewById(R.id.CurrencyBuy);
        currencyBuy.setTag(childPosition);
        currencyBuy.setText(detailInfo.getBuy().trim());

        if(detailInfo.isBestBuyDeal())
        {
            currencyBuy.setTextColor(Color.rgb(0,150,0));
        }
        else
        {
            currencyBuy.setTextColor(Color.GRAY);
        }
        if(detailInfo.isBestSellDeal())
        {
            currencySell.setTextColor(Color.rgb(0,150,0));
        }
        else
        {
            currencySell.setTextColor(Color.GRAY);
        }
        return view;
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        ArrayList<DetailInfo> productList = deptList.get(groupPosition).getProductList();
        return productList.size();

    }

    @Override
    public Object getGroup(int groupPosition) {
        return deptList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return deptList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isLastChild, View view,
                             ViewGroup parent) {

        HeaderInfo headerInfo = (HeaderInfo) getGroup(groupPosition);
        if (view == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inf.inflate(R.layout.group_heading, null);
        }

        ImageView Image = (ImageView) view.findViewById(R.id.bankImage);
        TextView transcription = (TextView) view.findViewById(R.id.CurrencyTranscription);
       // TextView amount = (TextView) view.findViewById(R.id.CurrencyAmount);
        TextView currencyName = (TextView) view.findViewById(R.id.CurrencyName);

        Image.setImageResource(context.getResources().getIdentifier(headerInfo.getImage().trim(), "drawable", context.getPackageName()));
        //amount.setText(String.format("%d : 1",headerInfo.getAmount()));
        currencyName.setText(headerInfo.getName().trim());
        transcription.setText(headerInfo.getTranscription());

        return view;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }



}