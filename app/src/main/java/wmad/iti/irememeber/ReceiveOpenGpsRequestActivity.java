package wmad.iti.irememeber;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * @Author Atef
 */
public class ReceiveOpenGpsRequestActivity extends AppCompatActivity {

    TextView textView;
    Button acceptButton;
    Button declineButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_open_gps_request);

        textView = (TextView) findViewById(R.id.textView);
        acceptButton = (Button) findViewById(R.id.acceptButton);
        declineButton=(Button) findViewById(R.id.declineButton);

        final Intent intent = getIntent();

        if(intent.getStringExtra("firstname")!=null){

            textView.setText(intent.getStringExtra("firstname")+" "+intent.getStringExtra("lastname")+getResources().getString(R.string.wants_to_know));
        }

        // accept will open GPS Settings
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                LocationManager locationManager =locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){

                    // open setting for him to enable GPS ..
                    Intent settingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    settingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(settingIntent);


                    Intent startServiceIntent = new Intent(getApplicationContext(),ShareLocationService.class);
                    String relativeEmail = getIntent().getStringExtra("email");
                    startServiceIntent.putExtra("relativeEmail",relativeEmail);
                    Log.e("**Receive Activity",relativeEmail);
                    startService(startServiceIntent);

                    // start service ..

                }
                else{

                    // do something ..
                    // start service ..
                    if(!ShareLocationService.isStarted){
                        //do something
                        Intent startServiceIntent = new Intent(getApplicationContext(),ShareLocationService.class);
                        intent.putExtra("relativeEmail",getIntent().getStringExtra("email"));
                        startService(startServiceIntent);
                    }
                }
            }
        });

    }

    /**
     * this method used to start service of gps broadcasting.
     */
    private void startServiceOfGPSBroadcast(){};

    /**
     * to check if location is enabled or not (True) if is is enabled.
     * @return
     */
    private boolean checkIsLocationEnabled(){
        LocationManager locationManager =locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.finish();
    }
}
