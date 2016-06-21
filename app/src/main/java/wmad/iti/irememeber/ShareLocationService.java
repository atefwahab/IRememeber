package wmad.iti.irememeber;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import wmad.iti.constants.Urls;
import wmad.iti.dto.Status;
import wmad.iti.model.GsonRequest;
import wmad.iti.model.MySingleton;
import wmad.iti.model.SharedPreferenceManager;

public class ShareLocationService extends Service {


    public static boolean isStarted = false;
    final String TAG = "**ShareLocation_Service";
    LocationManager locationManager;
    LocationListener locationListener;
    String relativeEmail;

    public ShareLocationService() {
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {


                Log.i(TAG, "Location Latitude>>" + location.getLatitude());

                RequestQueue queue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
                HashMap<String, String> headers = new HashMap<>();
                headers.put("relativeEmail", relativeEmail);
                headers.put("longitude", String.valueOf(location.getLongitude()));
                headers.put("latitude", String.valueOf(location.getLatitude()));
                headers.put("patientEmail", SharedPreferenceManager.getEmail(getApplicationContext()));

                GsonRequest request = new GsonRequest(Urls.WEB_SERVICE_BROADCAST_LOCATION, Request.Method.POST, Status.class, headers,
                        new Response.Listener<Status>() {
                            @Override
                            public void onResponse(Status response) {
                                Log.i(TAG, response.getMessage());
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(TAG, error.toString());
                            }
                        }
                );
                queue.add(request);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

                //Disconnect user
                RequestQueue queue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
                HashMap<String, String> headers = new HashMap<>();
                headers.put("relativeEmail", relativeEmail);


                GsonRequest request = new GsonRequest(Urls.WEB_SERVICE_DISCONNECT_USER, Request.Method.POST, Status.class, headers,
                        new Response.Listener<Status>() {
                            @Override
                            public void onResponse(Status response) {
                                Log.i(TAG, response.getMessage());
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(TAG, error.toString());
                            }
                        }
                );
                queue.add(request);
                ShareLocationService.this.stopSelf();
            }
        };
    }


    /**
     *
     * @param intent  The Intent supplied to {@link Context#startService},
     *                as given.  This may be null if the service is being restarted after
     *                its process has gone away, and it had previously returned anything
     *                except {@link #START_STICKY_COMPATIBILITY}.
     * @param flags   Additional data about this start request.  Currently either
     *                0, {@link #START_FLAG_REDELIVERY}, or {@link #START_FLAG_RETRY}.
     * @param startId A unique integer representing this specific request to
     *                start.  Use with {@link #stopSelfResult(int)}.
     * @return The return value indicates what semantics the system should
     * use for the service's current started state.  It may be one of the
     * constants associated with the {@link #START_CONTINUATION_MASK} bits.
     * @see #stopSelfResult(int)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // log
        Log.i(TAG, "Service Started ..");
        relativeEmail = intent.getStringExtra("relativeEmail");
        Log.e(TAG, relativeEmail);
        isStarted = true;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Runtime Permission ..
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return 1;
        }

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                    locationListener);

        } else {

            Intent settingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            settingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(settingIntent);

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                    locationListener);
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service destroyed ..");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(locationListener);
        isStarted=false;
    }
}
