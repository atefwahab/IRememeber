package wmad.iti.irememeber;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Belal on 5/27/2016.
 */


//Class extending FirebaseInstanceIdService
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.e(TAG, "Refreshed token: " + refreshedToken);

        saveRegistrationToken(refreshedToken);

    }




    /**
     * this method used to save token in general shared preferences with key "Token".
     * @param token
     * @return boolean
     */
    private boolean saveRegistrationToken(String token) {

        //1- get the shared preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //2- get the edior to write using it
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //3- save token to shared preferences
        editor.putString("Token", token);

        //4- commit editor to save it
        return editor.commit();
    }


}