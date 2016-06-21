package wmad.iti.irememeber;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.koushikdutta.async.http.Headers;

import java.util.HashMap;

import wmad.iti.constants.Urls;
import wmad.iti.dto.Status;
import wmad.iti.model.GsonRequest;
import wmad.iti.model.MySingleton;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MAPS ACTIVITY";
    private GoogleMap mMap;
    private Marker marker;
    public static boolean isActive = false;
    public static boolean isStarted = false;
    public static final String broadcast_receiver = "LOCATION_BROADCAST_RECEIVER";
    LocalBroadcastManager localBroadcastManager;
    Bitmap patientImageMarker;
    String patientImage;
    String patientEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        isStarted = true;
        patientEmail = getIntent().getStringExtra("patientEmail");
        Log.e("Patient Email2", patientEmail);
    }


    @Override
    protected void onResume() {
        super.onResume();
        localBroadcastManager.registerReceiver(broadcastReceiver, new IntentFilter(broadcast_receiver));
        isActive = true;

    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
        isActive = false;
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        double latitude = Double.parseDouble(getIntent().getStringExtra("latitude"));
        double longitude = Double.parseDouble(getIntent().getStringExtra("longitude"));
        patientImage = getIntent().getStringExtra("patientImage");
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(latitude, longitude);
        marker = mMap.addMarker(new MarkerOptions().position(sydney));
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        //marker.setIcon(BitmapDescriptorFactory.fromBitmap(patientImageMarker));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getIntExtra("disconnect", 0) != 0) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.user_diconnected), Toast.LENGTH_LONG).show();
            } else {
                Log.i("onReceive", "lat" + intent.getStringExtra("latitude"));
                Log.i("lon", "lon" + intent.getStringExtra("longitude"));
                // Toast.makeText(getApplicationContext(),"latitude-->"+intent.getStringExtra("latitude")+" longitude-->"+intent.getStringExtra("longitude"),Toast.LENGTH_SHORT).show();

                double latitude = Double.parseDouble(intent.getStringExtra("latitude"));
                double longitude = Double.parseDouble(intent.getStringExtra("longitude"));
                patientImage = intent.getStringExtra("patientImage");

                // volley request
//                ImageRequest imageRequest = new ImageRequest(patientImage, new Response.Listener<Bitmap>() {
//                    @Override
//                    public void onResponse(Bitmap response) {
//
//                        patientImageMarker = response;
//                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(patientImageMarker));
//
//
//                    }
//                },0 ,0 , null, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
//
//                    }
//                });
//
//                // Access the RequestQueue through your singleton class.
//                MySingleton.getInstance(context).addToRequestQueue(imageRequest);
                // Add a marker in Sydney and move the camera
                //LatLng sydney = new LatLng(-34, 151);
                LatLng latLng = new LatLng(latitude, longitude);
                if (marker != null) {
                    marker.remove();
                }
                marker = mMap.addMarker(new MarkerOptions().position(latLng));
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                // mMap.addMarker(new MarkerOptions().position(nihal).title("Marker in nihal"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

            }
        }
    };

    /**
     * Dispatch onStop() to all fragments.  Ensure all loaders are stopped.
     */
    @Override
    protected void onStop() {
        super.onStop();
        isStarted = false;
        // Stop service from patient side ..

        HashMap<String, String> headers = new HashMap<>();
        headers.put("patientEmail", patientEmail);
        GsonRequest gsonRequest = new GsonRequest(Urls.WEB_SERVICE_STOP_SERVICE, Request.Method.POST, Status.class, headers, new Response.Listener<Status>() {
            @Override
            public void onResponse(Status response) {

                Log.i(TAG, response.getMessage());
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.i(TAG, error.toString());

                    }
                });
        MySingleton.getInstance(this).addToRequestQueue(gsonRequest);
    }


    @Override
    protected void onDestroy() {


        Log.e("Patient Email2", patientEmail);
        // Stop service from patient side ..

        Log.e(TAG, "MAPS DESTROYED");
        HashMap<String, String> headers = new HashMap<>();
        headers.put("patientEmail", patientEmail);
        GsonRequest gsonRequest = new GsonRequest(Urls.WEB_SERVICE_STOP_SERVICE, Request.Method.POST, Status.class, headers, new Response.Listener<Status>() {
            @Override
            public void onResponse(Status response) {

                Log.i(TAG, response.getMessage());
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.i(TAG, error.toString());

                    }
                });
        MySingleton.getInstance(this).addToRequestQueue(gsonRequest);
        super.onDestroy();
    }
}
