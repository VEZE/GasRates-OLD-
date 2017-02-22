package wiksinc.currencyrates.MapModule;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.collect.Maps;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.concurrent.ExecutionException;

import wiksinc.currencyrates.CustomActivity;
import wiksinc.currencyrates.MainActivity;
import wiksinc.currencyrates.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, View.OnClickListener,
        ClusterManager.OnClusterClickListener<GooglePlace>,
        ClusterManager.OnClusterInfoWindowClickListener<GooglePlace>,
        ClusterManager.OnClusterItemClickListener<GooglePlace>,
        ClusterManager.OnClusterItemInfoWindowClickListener<GooglePlace>{

    private GoogleMap mMap;

    Bitmap atmIcon,exchangeIcon;

    Context context ;

    Button TerrainB, NormalB;

    ArrayList<GooglePlace> atms = new ArrayList<>(); //258

    String atmFileName = "atm";

    String temp;
    String GetTitleMap;

    String atmQuery = "заправка";

    private ArrayList<String> lantlong = new ArrayList<>();
    {
        lantlong.add("53.92455961120848,27.455005645751953");
        lantlong.add("53.89563991984441,27.441444396972656");
        lantlong.add("53.86791485688164,27.461013793945312");
        lantlong.add("53.85657669031666,27.573280334472656");
        lantlong.add("53.85819661651936,27.62958526611328");
        lantlong.add("53.872570713760915,27.665634155273438");
        lantlong.add("53.90130409342004,27.65155792236328");
        lantlong.add("53.93385819251255,27.63988494873047");
        lantlong.add("53.94921652742343,27.58563995361328");
        lantlong.add("53.949014480777706,27.535858154296875");
        lantlong.add("53.90110181472825,27.528305053710938");
        lantlong.add("53.90332682647337,27.582550048828125");
        lantlong.add("53.85313413891649,27.513198852539062");
        lantlong.add("53.94598366363026,27.484703063964844");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        context = this;
        TerrainB = (Button) findViewById(R.id.hydrid);
        NormalB = (Button) findViewById(R.id.normal);

        TerrainB.setOnClickListener(this);
        NormalB.setOnClickListener(this);

        atmIcon = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_atm),100,100,false);

       // exchangeIcon = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_exchange),100,100,false);

        // /data/data/wiksinc.currencyrates/files : this.getFilesDir().getAbsolutePath();

        File atmJson = new File(this.getFilesDir().getAbsolutePath(), atmFileName + ".json");

        if (atmJson.exists()) {
            try {
                atms.addAll(load(atmJson));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (atms.size() == 0) {
            try {
                for (String latlng : lantlong) {

                    Log.d("latlong --", latlng);

                    int pervAtmsSize = atms.size();
                    atms.addAll(parseGoogleParse(new GooglePlaces().execute(atmQuery, latlng).get()));
                    Log.d("gas --", String.valueOf(atms.size() - pervAtmsSize));

                }
                Log.d("atms --", String.valueOf(atms.size()));

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            try {
                cache(atms, atmFileName);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        final ClusterManager<GooglePlace>  mClusterExchengeManager = new ClusterManager<>(this, mMap);
        final ClusterManager<GooglePlace>  mClusterAtmManager = new ClusterManager<>(this, mMap);
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                mClusterExchengeManager.onCameraChange(cameraPosition);
                mClusterAtmManager.onCameraChange(cameraPosition);
            }
        });

        mMap.setOnInfoWindowClickListener(mClusterAtmManager); //added
        mClusterAtmManager.setOnClusterItemInfoWindowClickListener(this);


        mClusterExchengeManager.setRenderer(new MyClusterExchangeItemRenderer(this, mMap,
                mClusterExchengeManager));

        mClusterAtmManager.setRenderer(new MyClusterAtmItemRenderer(this, mMap,
                mClusterAtmManager));

        mClusterExchengeManager.getMarkerCollection().setOnInfoWindowAdapter(
                new MarkerInfoWindowAdapter());
        mClusterAtmManager.getMarkerCollection().setOnInfoWindowAdapter(
                new MarkerInfoWindowAdapter());

        mClusterAtmManager.getMarkerCollection().setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                 GetTitleMap = marker.getTitle();
                Intent intent = new Intent(MapsActivity.this, CustomActivity.class);
                startActivityForResult(intent,1);

            }



                                                                              }

        );


        mClusterAtmManager.cluster();
        mClusterExchengeManager.cluster();

        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setMyLocationEnabled(true);

        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        mMap.setOnMarkerClickListener(this);
        mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter());

        CameraUpdate minsk = CameraUpdateFactory.newLatLng(new LatLng(53.9, 27.56));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);

        mMap.moveCamera(minsk);
        mMap.animateCamera(zoom);

        mClusterAtmManager.addItems(atms);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.normal:
                if (mMap.getMapType() != GoogleMap.MAP_TYPE_NORMAL)
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                break;
            case R.id.hydrid:
                if (mMap.getMapType() != GoogleMap.MAP_TYPE_HYBRID)
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
        }
    }
    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data == null) {return;}
        String name = data.getStringExtra("name");
        temp=(name);
        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setTitle("Важное сообщение!")
                .setMessage(temp+GetTitleMap);
        AlertDialog alert = builder.create();
        alert.show();
        //Toast.makeText(MapsActivity.this, temp+GetTitleMap, Toast.LENGTH_SHORT).show();
    }

    private LinkedHashSet<GooglePlace> load(File file) throws IOException, JSONException {
        LinkedHashSet<GooglePlace> temp = new LinkedHashSet<>();

        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();

        String json = new String(data, "UTF-8");

        JSONObject jsonObject = new JSONObject(json);

        if (jsonObject.has("GooglePlaces") && jsonObject.getJSONArray("GooglePlaces").length() > 0) {

            JSONArray jsonArray = jsonObject.getJSONArray("GooglePlaces");

            for (int i = 0; i < jsonArray.length(); i++) {

                GooglePlace poi = new GooglePlace();

                JSONObject father = jsonArray.getJSONObject(i);

                if (father.has("Name")) {

                    poi.setName(father.optString("Name"));
                }

                if (father.has("Address")) {

                    poi.setAddress(father.optString("Address"));
                }

                if (father.has("Category")) {

                    poi.setCategory(father.optString("Category"));
                }

                if (father.has("Lat")) {

                    poi.setLat(father.optDouble("Lat"));
                }

                if (father.has("Lng")) {

                    poi.setLng(father.optDouble("Lng"));
                }

                if (father.has("OpenNow")) {

                    poi.setOpenNow(father.optString("OpenNow"));
                }

                temp.add(poi);
            }
        }

        return temp;
    }

    private void cache(Collection<GooglePlace> array, String fileName) throws JSONException ,IOException{

        JSONObject mainObj = new JSONObject();
        JSONArray ja = new JSONArray();

        for (GooglePlace place: array) {

            JSONObject jo = new JSONObject();

            jo.put("Name", place.getName());
            jo.put("Address", place.getAddress());
            jo.put("Category", place.getCategory());
            jo.put("Lat", place.getLat());
            jo.put("Lng", place.getLng());
            jo.put("OpenNow",place.getOpenNow());

            ja.put(jo);
        }

        mainObj.put("GooglePlaces", ja);

        FileOutputStream fos = openFileOutput(fileName + ".json", Context.MODE_PRIVATE);
        fos.write(mainObj.toString().getBytes());
        fos.close();

    }

    private ArrayList<GooglePlace> parseGoogleParse(final String response) {
        ArrayList<GooglePlace> temp = new ArrayList<>();

        try {

            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.has("results")) {

                JSONArray jsonArray = jsonObject.getJSONArray("results");

                for (int i = 0; i < jsonArray.length(); i++) {

                    GooglePlace poi = new GooglePlace();

                    JSONObject father = jsonArray.getJSONObject(i);

                    if (father.has("name")) {

                        poi.setName(father.optString("name"));
                    }

                    if(father.has("geometry"))
                    {
                        JSONObject child = father.getJSONObject("geometry");

                        if(child.has("location"))
                        {
                            JSONObject baby = child.getJSONObject("location");

                            if(baby.has("lat") && baby.has("lng")) {

                                poi.setLat(baby.getDouble("lat"));
                                poi.setLng(baby.getDouble("lng"));
                            }
                        }
                    }

                    if(father.has("formatted_address"))
                    {
                        poi.setAddress(father.optString("formatted_address"));
                    }

                    if (father.has("opening_hours")) {

                        if (father.getJSONObject("opening_hours").has("open_now")) {

                            if (father.getJSONObject("opening_hours").getString("open_now").equals("true")) {

                                poi.setOpenNow("YES");

                            } else {

                                poi.setOpenNow("NO");

                            }
                        }
                    } else {

                        poi.setOpenNow("Not Known");

                    }

                    if (father.has("types")) {

                        JSONArray typesArray = father.getJSONArray("types");

                        for (int j = 0; j < typesArray.length(); j++) {

                            poi.setCategory(typesArray.getString(j) + ", " + poi.getCategory());
                        }
                    }
                    temp.add(poi);
                }
                if(jsonObject.has("next_page_token"))
                {
                    String page_token = jsonObject.get("next_page_token").toString();
                    temp.addAll(parseGoogleParse(new GooglePlaces().execute(atmQuery,lantlong.get(0),page_token).get()));
                }

            }
        } catch (Exception e) {

            e.printStackTrace();

            return new ArrayList<>();
        }
        return temp;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public boolean onClusterClick(Cluster<GooglePlace> cluster) {
        return false;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<GooglePlace> cluster) {
        Toast.makeText(context, "VLAD LOH",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onClusterItemClick(GooglePlace googlePlace) {
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(GooglePlace googlePlace) {
        Toast.makeText(context, "VLAD LOH",
                Toast.LENGTH_LONG).show();
    }

    class MyClusterAtmItemRenderer extends DefaultClusterRenderer<GooglePlace> {

        public MyClusterAtmItemRenderer(Context context, GoogleMap map,
                                             ClusterManager<GooglePlace> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(GooglePlace item,
                                                   MarkerOptions markerOptions) {
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(atmIcon));
            markerOptions.title(item.getName());
            markerOptions.snippet(item.getAddress());
        }

        @Override
        protected void onClusterItemRendered(GooglePlace clusterItem, Marker marker) {
            super.onClusterItemRendered(clusterItem, marker);
        }
    }

    class MyClusterExchangeItemRenderer extends DefaultClusterRenderer<GooglePlace> {

        public MyClusterExchangeItemRenderer(Context context, GoogleMap map,
                                 ClusterManager<GooglePlace> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(GooglePlace item,
                                                   MarkerOptions markerOptions) {
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(exchangeIcon));
            markerOptions.title(item.getName());
            markerOptions.snippet(item.getAddress());
        }

        @Override
        protected void onClusterItemRendered(GooglePlace clusterItem, Marker marker) {
            super.onClusterItemRendered(clusterItem, marker);
        }
    }

    class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        MarkerInfoWindowAdapter(){
            myContentsView = getLayoutInflater().inflate(R.layout.marker_info, null);
        }
        @Override
        public View getInfoContents(Marker marker) {

            TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.marker_titile));
            tvTitle.setText(marker.getTitle());

            TextView tvAddress = ((TextView)myContentsView.findViewById(R.id.marker_address));
            tvAddress.setText(marker.getSnippet());

            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            return null;
        }

    }
}

