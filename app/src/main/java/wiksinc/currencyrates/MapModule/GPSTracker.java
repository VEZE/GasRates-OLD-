package wiksinc.currencyrates;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;

public class GPSTracker extends Service implements LocationListener {

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    static boolean canGetLocation = false;
    float[] distance = new float[2];

    Location location;
    Marker marker;

    SQLite MySQLite = new SQLite(this);
    SQLiteDatabase db;

    double latitude, lat;
    double longitude, lng;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 2; //1000 * 60 * 2 // 2 minute

    protected LocationManager locationManager;

    public void onCreate() {

        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        getLocation();
        Log.d("myTag", "--- Tracker started ---");
        //Toast.makeText(this, "--- Tracker started ---", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    public void onDestroy(Intent intent) {

        Log.d("myTag", "--- Tracker destroyed ---");

    }

    public Location getLocation() {
        Log.d("myTag", "--- getLocation ---");
        try {
            locationManager = (LocationManager) this
                    .getSystemService(LOCATION_SERVICE);

            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                Toast.makeText(this, "no network provider is enabled", Toast.LENGTH_LONG).show();
            } else {
                canGetLocation = true;
                if (isNetworkEnabled) {
                    try {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    } catch (SecurityException e) {
                        Log.e("PERMISSION_EXCEPTION", "PERMISSION_NOT_GRANTED");
                    }
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        try {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        } catch (SecurityException e) {
                            Log.e("PERMISSION_EXCEPTION", "PERMISSION_NOT_GRANTED");
                        }
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            //Toast.makeText(this, "--- Coords " + latitude +":"+ longitude + " On Ethernet  ---", Toast.LENGTH_LONG).show();
                            Log.d("Gps", "--- Coords " + latitude + ":" + longitude + " On Ethernet  ---");
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        try {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        } catch (SecurityException e) {
                            Log.e("PERMISSION_EXCEPTION", "PERMISSION_NOT_GRANTED");
                        }
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            try {
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            } catch (SecurityException e) {
                                Log.e("PERMISSION_EXCEPTION", "PERMISSION_NOT_GRANTED");
                            }
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                Toast.makeText(this, "--- Coords " + latitude + ":" + longitude + " On Gps  ---", Toast.LENGTH_LONG).show();
                                Log.d("Gps", "--- Coords " + latitude + ":" + longitude + " On Gps  ---");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    public void stopUsingGPS() {
        if (locationManager != null) {
            try {
                locationManager.removeUpdates(GPSTracker.this);
            } catch (SecurityException e) {
                Log.e("PERMISSION_EXCEPTION", "PERMISSION_NOT_GRANTED");
            }
        }
    }

    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }


    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        //Toast.makeText(this, "--- Coords " + latitude + ":" + longitude + " On Change ---", Toast.LENGTH_LONG).show();
        Log.i("Gps", "--- Coords " + latitude + ":" + longitude + " On Change  ---");
        db = MySQLite.getWritableDatabase();

        Cursor c = db.query("mytable", null, null, null, null, null, null);

        if (c.moveToFirst()) {
            do {
                String title = c.getString(c.getColumnIndex("title"));
                String gps = c.getString(c.getColumnIndex("gps"));

                lat = 0;
                lng = 0;

                if (gps != null) {
                    for (String item : gps.split(",")) {
                        if (lat == 0)
                            lat = Double.parseDouble(item);
                        else
                            lng = Double.parseDouble(item);
                    }
                    
                    Location.distanceBetween(latitude, longitude,
                            lat, lng, distance);
                    // Toast.makeText(this, "--- For Location ---", Toast.LENGTH_LONG).show();
                    Log.i("Gps", " For Location");
                    if (distance[0] < 80) {
                        Toast.makeText(this, "--- Inside " + distance[0] + "  ---", Toast.LENGTH_LONG).show();
                        Log.i("Gps", "--- Notification  ---");
                        /*Intent notification = new Intent(GPSTracker.this, Notifications_Service.class);
                        notification.putExtra(ReminderMenu_additionally.TITLE, title);
                        notification.putExtra(ReminderMenu_additionally.DATE_TIME, gps);
                        GPSTracker.this.startService(notification);
                        GPSTracker.this.stopService(notification);*/
                    } else {
                        // Toast.makeText(this, "--- Outside " + distance[0] + " ---", Toast.LENGTH_LONG).show();
                        Log.i("Gps", "Outside " + distance[0]);
                    }
                }
                Log.i("Gps", " For end");
            } while (c.moveToNext());
            c.close();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}