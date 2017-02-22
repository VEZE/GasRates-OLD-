package wiksinc.currencyrates;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import wiksinc.currencyrates.ListsUtils.DetailInfo;
import wiksinc.currencyrates.ListsUtils.ExpendableSummaryListAdapter;
import wiksinc.currencyrates.ListsUtils.HeaderInfo;
import wiksinc.currencyrates.ListsUtils.ListHelper;
import wiksinc.currencyrates.Parsers.Summary;


public class SummaryListFragment extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener{

    View rootview;
    CheckBox checkBox;

    private LinkedHashMap<String, HeaderInfo> myDepartments = new LinkedHashMap<String, HeaderInfo>();
    private ArrayList<HeaderInfo> deptList = new ArrayList<HeaderInfo>();

    private Map<String, Integer >bankImageDict = new HashMap<>();
    {
        bankImageDict.put("А-100",R.drawable.a100);
        bankImageDict.put("Белорусьнефть",R.drawable.belorus);
        bankImageDict.put("Газпромнефть",R.drawable.gazprom);
        bankImageDict.put("Лукоил",R.drawable.lukoil);
        bankImageDict.put("Юнайтед компани",R.drawable.uc);
        bankImageDict.put("Славнефть",R.drawable.slavneft);
        bankImageDict.put("Трайпл",R.drawable.trajpl);
    }
    private Map<String, currencyHelperEntry > currencyInformed = new HashMap<>();
    {
        currencyInformed.put("Бензин АИ-92", new currencyHelperEntry("Бензин АИ-92","nine2"));
        currencyInformed.put("Бензин АИ-95", new currencyHelperEntry("Бензин АИ-95","nine5"));
        currencyInformed.put("Дизтопливо Евро", new currencyHelperEntry("Дизтопливо Евро","dt"));
        currencyInformed.put("ДТ-З-К5 класс 1", new currencyHelperEntry("ДТ-З-К5 класс 1","dt3"));
    }

    private Comparator<ListHelper> bestBuyCmp = new Comparator<ListHelper>() {
        @Override
        public int compare(ListHelper o1, ListHelper o2) {
            return o1.getSummary().getBuy().compareTo(o2.getSummary().getBuy());
        }
    };

    private Comparator<ListHelper> bestSellCmp = new Comparator<ListHelper>() {
        @Override
        public int compare(ListHelper o1, ListHelper o2) {
            return o1.getSummary().getSell().compareTo(o2.getSummary().getSell());
        }
    };

    private ExpendableSummaryListAdapter listAdapter;
    private ExpandableListView myList;
    private SwipeRefreshLayout swipeLayout;

    SQLiteDatabase db;
    SQLite MySQLite;

    final String LOG_TAG = "myLogs";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootview = inflater.inflate(R.layout.currency_list, container, false);
        MySQLite = new SQLite(getActivity());

        checkBox = (CheckBox) rootview.findViewById(R.id.ShowOnlyFavorite);

        android.support.design.widget.AppBarLayout appbar = (android.support.design.widget.AppBarLayout) getActivity().findViewById(R.id.appBar);
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)appbar.getLayoutParams();
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        appbar.setLayoutParams(lp);

        //loadData();
        if(myDepartments.size() == 0) {
            loadDataTest();
        }

        String[] title = new String[deptList.size()];
        String[] sort = new String[]{"Name","Time","Date","Priority"};

        for(int i =0; i < deptList.size();i++ ) {
            HeaderInfo headerInfo = deptList.get(i);
            String name = headerInfo.getName();
            title[i] = name;
        }

        //get reference to the ExpandableListView
        myList = (ExpandableListView) rootview.findViewById(R.id.NearestNotiMenu);

        swipeLayout = (SwipeRefreshLayout) rootview.findViewById(R.id.swipeСontainer);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(Color.rgb(0,0,0),Color.rgb(0,0,0),
                Color.rgb(0,0,0),Color.rgb(0,0,0)
        );

        //create the adapter by passing your ArrayList data
        listAdapter = new ExpendableSummaryListAdapter(getActivity(), deptList);
        //attach the adapter to the list
        myList.setAdapter(listAdapter);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

             @Override
             public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                 myDepartments.clear();
                 deptList.clear();
                 loadDataTest();
                 listAdapter.notifyDataSetChanged();
             }
         }
        );
        //listener for child row click
        myList.setOnChildClickListener(myListItemClicked);
        //listener for group heading click
        myList.setOnGroupClickListener(myListGroupClicked);


        return rootview;
    }

    @Override
    public void onRefresh() {

        NetworkServiсe ns = new NetworkServiсe(getContext());

        if(ns.hasConnection())
        {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    swipeLayout.setRefreshing(false);

                    myDepartments.clear();
                    deptList.clear();
                    loadDataTest();
                    listAdapter.notifyDataSetChanged();

                }
            }, 4444);

            new AppStartFragment().new ResourcesService(getContext()).execute(true);

        }
        else
        {
            swipeLayout.setRefreshing(false);
            ns.showSettingsAlert();
        }
    }

    private void loadDataTest() {

        db = MySQLite.getWritableDatabase();

        ArrayList<ListHelper> listObjects = new ArrayList<>();

        for (String curr: currencyInformed.keySet())
        {
            Log.d("LoadProduct", "Summary: "+ curr);

            String addition = "";

            Cursor bestCursor = db.rawQuery("Select *" +
                                            "from Currencys as C " +
                                            "inner join Banks as B " +
                                            "on C.BankID = B.ID " +
                                            "where C.Name = '" + curr + addition + "'" +
                                            "order by B.Name ASC",null);

            if (bestCursor.moveToFirst()) {

                int currencyNameIndex = 1;
                int currencyBuyIndex = bestCursor.getColumnIndex("Buy");
                int currencySellIndex = bestCursor.getColumnIndex("Sell");
                int currencyIsFavoriteIndex = 5;

                int bankIsFavoriteIndex = 9;
                int bankNameIndex = 7;
                int bankUpdateDateIndex = bestCursor.getColumnIndex("DateOfUpdate");

                do {

                    String currencyName = bestCursor.getString(currencyNameIndex);
                    Boolean currencyIsFavorite = (bestCursor.getInt(currencyIsFavoriteIndex) == 1);
                    Double currencyBuy = bestCursor.getDouble(currencyBuyIndex);
                    Double currencySell = bestCursor.getDouble(currencySellIndex);

                    Boolean bankIsFavorite = (bestCursor.getInt(bankIsFavoriteIndex) == 1);
                    String bankName = bestCursor.getString(bankNameIndex);
                    String bankUpdateDate = bestCursor.getString(bankUpdateDateIndex);

                    String name = currencyName;

                    if(checkBox.isChecked())
                    {
                        if(!currencyIsFavorite)
                        {
                            break;
                        }
                        if(!bankIsFavorite)
                        {
                            continue;
                        }
                    }
                    addProduct(name, bankUpdateDate, new Summary(currencyBuy, currencySell, bankName), new AbstractMap.SimpleEntry<>(false, false));
                    //listObjects.add(new ListHelper(name, bankUpdateDate, new Summary(currencyBuy, currencySell, bankName)));

                } while (bestCursor.moveToNext());

                /*if(listObjects.size() > 0) {

                    ListHelper maxBuyItem = Collections.max(listObjects, bestBuyCmp);
                    ListHelper minSellItem = Collections.min(listObjects, bestSellCmp);

                    for (ListHelper item : listObjects) {

                        Boolean isBuy = false, isSell = false;

                        addProduct(item.getName(), item.getDate(), item.getSummary(), new AbstractMap.SimpleEntry<>(isBuy, isSell));
                    }
                    listObjects.clear();
                }*/
            }

            bestCursor.close();
        }

        db.close();
    }

    //our child listener
    private ExpandableListView.OnChildClickListener myListItemClicked = new ExpandableListView.OnChildClickListener() {

        public boolean onChildClick(ExpandableListView parent, View v,
                                    int groupPosition, int childPosition, long id) {


            /*HeaderInfo headerInfo = deptList.get(groupPosition);

            DetailInfo detailInfo = headerInfo.getProductList().get(childPosition);

            Fragment fragment = new StationsViewFragment();

            Bundle bundle = new Bundle();
            bundle.putString("name",detailInfo.getName());
            bundle.putString("date",detailInfo.getDate());
            bundle.putInt("logo",bankImageDict.get(detailInfo.getName()));

            fragment.setArguments(bundle);
            FragmentTransaction ft =  getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFrame, fragment);
            ft.addToBackStack(null);
            ft.commit();
*/
            return true;
        }

    };

    //our group listener
    private ExpandableListView.OnGroupClickListener myListGroupClicked = new ExpandableListView.OnGroupClickListener() {

        public boolean onGroupClick(ExpandableListView parent, View v,
                                    int groupPosition, long id) {
            /*//get the group header
            HeaderInfo headerInfo = deptList.get(groupPosition);
            //display it or do something with it
            Toast.makeText(getContext(), "Child on Header " + headerInfo.getName(),
                    Toast.LENGTH_LONG).show();

*/
            return false;
        }

    };


    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(view.getId() == R.id.CurrencyName){
            view.getParent().requestDisallowInterceptTouchEvent(true);
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    Log.d(LOG_TAG, "Action");
                    view.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
        }
        return false;
    }

    //here we maintain our products in various departments
    private int addProduct(String bank, String date, Summary summary, Map.Entry<Boolean,Boolean> bestDeal) {

        int groupPosition ;

        //check the hash map if the group already exists
        HeaderInfo headerInfo = myDepartments.get(bank);
        //add the group if doesn't exists
        if (headerInfo == null) {
            headerInfo = new HeaderInfo();
            Log.d("AddProduct", "Summary: "+ bank);
            headerInfo.setName(bank);
           // headerInfo.setAmount(currencyInformed.get(bank).getAmount());
            headerInfo.setTranscription(currencyInformed.get(bank).getTranscription());
            headerInfo.setImage(currencyInformed.get(bank).getImage());

            myDepartments.put(bank, headerInfo);
            deptList.add(headerInfo);
        }

        //get the children for the group
        ArrayList<DetailInfo> productList = headerInfo.getProductList();
        //size of the children list
        int listSize = productList.size();
        //add to the counter
        listSize++;

        //create a new child and add that to the group
        DetailInfo detailInfo = new DetailInfo();

        detailInfo.setName(summary.getCurrency());
        detailInfo.setDate(date);
        detailInfo.setBuy(String.valueOf(summary.getBuy()));
        detailInfo.setSell(String.valueOf(summary.getSell()));
        detailInfo.setBestDeal(bestDeal);


        productList.add(detailInfo);
        Collections.sort(productList);
        headerInfo.setProductList(productList);

        //find the group position inside the list
        groupPosition = deptList.indexOf(headerInfo);
        return groupPosition;
    }


    private final class currencyHelperEntry {

        private String transcription;
        private String image;


        currencyHelperEntry(String transcription, String image) {
            this.transcription = transcription;

            this.image = image;
        }

        void setTranscription(String transcription) {
            this.transcription = transcription;
        }

        String getTranscription() {
            return transcription;
        }

        void setImage(String image) {
            this.image = image;
        }

        String getImage() {
            return image;
        }

       // void setAmount (Integer amount) {
      //      this.amount = amount;
      //  }

     //   Integer getAmount() {
       //     return amount;
       // }
    }
}