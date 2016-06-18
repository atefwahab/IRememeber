package wmad.iti.irememeber;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;

import wmad.iti.constants.Urls;
import wmad.iti.dto.Status;
import wmad.iti.model.GsonRequest;
import wmad.iti.model.MySingleton;
import wmad.iti.model.SharedPreferenceManager;

/**
 * @author atef
 * This service used to connnect ot Web service and send my token ..
 */
public class SendTokenToServerIntentService extends IntentService {


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     *  Used to name the worker thread, important only for debugging.
     */
    public SendTokenToServerIntentService() {super("SendTokenToServerIntentService");}

    /**
     * This method is invoked on the worker thread with a request to process.
     * Only one Intent is processed at a time, but the processing happens on a
     * worker thread that runs independently from other application logic.
     * So, if this code takes a long time, it will hold up other requests to
     * the same IntentService, but it will not hold up anything else.
     * When all requests have been handled, the IntentService stops itself,
     * so you should not call {@link #stopSelf}.
     *
     * @param intent The value passed to {@link
     *               Context#startService(Intent)}.
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        Log.e("Register Token ","Service started");
        String token = SharedPreferenceManager.getToken(this);


        if(token!=null){

            Log.e("Register Token ",token);

            RequestQueue queue = MySingleton.getInstance(this).getRequestQueue();
            HashMap<String,String> headers = new HashMap<>();
            headers.put("email", SharedPreferenceManager.getEmail(this));
            headers.put("token",token);
            GsonRequest request = new GsonRequest(Urls.WEB_SERVICE_UPDATE_TOKEN,  Request.Method.POST,Status.class,
                    headers,
                    new Response.Listener<Status>() {
                        @Override
                        public void onResponse(Status response) {
                            Log.i("Token Status", response.getMessage());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Volley Error",error.toString());
                        }
                    }
            );


            queue.add(request);

        }




    }
}
