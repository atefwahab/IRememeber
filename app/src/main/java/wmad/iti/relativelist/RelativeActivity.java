package wmad.iti.relativelist;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;
import wmad.iti.irememeber.R;
import wmad.iti.model.MySingleton;

public class RelativeActivity extends AppCompatActivity {
    GridView gridView;
    int[] iconsImages;
    String[] iconTitles;
    String phone,relativeFirstName,relativeLastName;
    static RelativeActivity relativeActivity;
    Location currentLocation;
    Geocoder geocoder;
    String addreess;
    String city;
    String country;
    String imageUrl,relativeEmail;
    LocationManager locationManager;
    LocationListener bestLocationListener;
    CircularImageView circularImageView;
    Toolbar toolbar;
    public static RelativeActivity instance() {
        return relativeActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        relativeActivity = this;
        setContentView(R.layout.activity_relative);
        circularImageView= (CircularImageView) findViewById(R.id.profile_circularImageView);

        relativeFirstName=getIntent().getStringExtra("relativeFirstName");
        relativeLastName=getIntent().getStringExtra("relativeLastName");
        imageUrl=getIntent().getStringExtra("imageUrl");
        relativeEmail=getIntent().getStringExtra("relativeEmail");
        Log.i("relativeEmail",relativeEmail+"relativeActivity");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.navigation_back);
        getSupportActionBar().setTitle(relativeFirstName + " " + relativeLastName);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        gridView = (GridView) findViewById(R.id.gridView1);
        iconsImages = new int[]{R.drawable.call, R.drawable.sms, R.drawable.add_memory, R.drawable.panic_mode};
        iconTitles = getResources().getStringArray(R.array.relative_activity_arr);
        gridView.setAdapter(new CustomRelativeActivityAdapter(this, iconTitles, iconsImages,relativeFirstName,relativeLastName,relativeEmail));

        phone = getIntent().getStringExtra("phoneNumber");
        getRelativePhoto();
      //  address = getIntent().getStringExtra("Address");
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        geocoder=new Geocoder(getApplicationContext());
      // to ckeck gps is enabled or not
        boolean gpsEnabled= locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
        if (!gpsEnabled){
            Intent settingIntent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

            startActivity(settingIntent);
        }

        //to listen to location
            locationListener();
        //to select criteria
            selectCriteria();


    }//end onCreate


    @Override
    public void onBackPressed() {
      super.onBackPressed();
    }
    /**
     * This method is used to send SMS to Particular relative contain address of his patient
     */
    void sendSMSMessage() {

        if(currentLocation!=null) {
            try {

                List<Address> myAddress = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                if (myAddress.size() > 0) {
                    addreess = myAddress.get(0).getAddressLine(0);
                    city = myAddress.get(0).getAddressLine(1);
                    country = myAddress.get(0).getAddressLine(2);
                    Toast.makeText(getApplicationContext(), "address " + addreess + "city " + city + "country " + country, Toast.LENGTH_LONG).show();
                }

                SmsManager smsManager = SmsManager.getDefault();
                String link = "http://maps.google.com/?q=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude();
                String msg = link + "\n" + addreess + ", " + city + ", " + country;

                smsManager.sendTextMessage(phone, null, msg, null, null);
                Log.i("message",msg);
                Log.i("phone number",phone);
                Toast.makeText(getApplicationContext(), phone+"SMS sent", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getApplicationContext(),"no location",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This method is used to call Particular relative
     */

    void call() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phone));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(callIntent);

    }

    /**
     * This method is used to listen to location of patient
     */
    public void locationListener(){
            bestLocationListener = new LocationListener() {

                public void onLocationChanged(Location location) {
                    currentLocation=location;
                    Toast.makeText(getApplicationContext(), "Latitude " + location.getLatitude() + "\n Longitude" + location.getLongitude(), Toast.LENGTH_LONG).show();
                   runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                   //     Toast.makeText(getApplicationContext(), "Network Provider update", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };


    }
    /**
     *This method is used to select criteria depends on the best way to get location by network or gps
     */
public void selectCriteria(){

    Criteria criteria = new Criteria();
    criteria.setAccuracy(Criteria.ACCURACY_FINE);
    criteria.setAltitudeRequired(false);
    criteria.setBearingRequired(false);
    criteria.setCostAllowed(true);
    criteria.setPowerRequirement(Criteria.POWER_LOW);
    String provider = locationManager.getBestProvider(criteria, true);
    if (provider != null) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //locationManager.NETWORK_PROVIDER
        locationManager.requestLocationUpdates(provider, 2 * 60 * 1000, 10,bestLocationListener);

        Toast.makeText(this, "Best Provider is " + provider, Toast.LENGTH_LONG).show();
    }

           }

    /**
     * This method is used to get photo of relative
     */
    public void getRelativePhoto(){

        ImageRequest request = new ImageRequest(imageUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        circularImageView.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        circularImageView.setImageResource(R.drawable.patient_img);
                    }
                });

        MySingleton.getInstance(this).addToRequestQueue(request);

    }
    }
