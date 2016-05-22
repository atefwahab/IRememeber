package wmad.iti.irememeber;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import wmad.iti.model.SharedPreferenceManager;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // intialize a thread
        Thread loadThread = new Thread(){

            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    // check email on shared preferences if it there..
                    if(SharedPreferenceManager.isCached(getApplicationContext())){
                        //checks on user's type
                        // 1 --> patient
                        if(SharedPreferenceManager.getUser(getApplicationContext()).getType() == 1){
                            Log.i("in SplashScreen patient", String.valueOf(SharedPreferenceManager.getUser(getApplicationContext()).getType()));
                            Intent patientIntent = new Intent(getApplicationContext(),PatientHomeActivity.class);
                            startActivity(patientIntent);
                        }
                        // 0 --> relative
                        else{
                            Log.i("in SplashScreen rel", String.valueOf(SharedPreferenceManager.getUser(getApplicationContext()).getType()));
                            Intent relativeIntent = new Intent(getApplicationContext(),RelativeHomeActivity.class);
                            startActivity(relativeIntent);
                        }
                    }
                    // if the user's data does not cached before
                    else{
                        Log.i("in splash screen","the cache does not happen");
                        Intent optionsIntent = new Intent(getApplicationContext(),OptionsActivity.class);
                        startActivity(optionsIntent);

                    }

                }
            }
        };

        loadThread.start();


    }

    @Override
    protected void onPause() {
        super.onPause();
        this.finish();
    }
}
