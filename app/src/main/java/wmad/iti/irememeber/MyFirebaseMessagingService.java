package wmad.iti.irememeber;

/**
 * Created by atef on 5/31/2016.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Handler;

import wmad.iti.model.MySingleton;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final int DISCONNECT_USER = 5050;
    private int REQUEST_CODE = 0;
    private final int LOCATION_REQUEST = 9393;
    private final int UPDATE_LOCATION = 4141;
    final static int STOP_SERVICE = 5789;

    Bitmap relativeImage;

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        Log.i("*****************", "Message Received " + remoteMessage.getData().get("requestId"));

        switch (Integer.parseInt(remoteMessage.getData().get("requestId"))) {

            // to receive location request
            case LOCATION_REQUEST:

                if (checkIsLocationEnabled() && !ShareLocationService.isStarted) {
                    //do something
                    Intent startServiceIntent = new Intent(getApplicationContext(), ShareLocationService.class);
                    String relativeEmail = remoteMessage.getData().get("email");
                    startServiceIntent.putExtra("relativeEmail", relativeEmail);
                    Log.e(TAG, relativeEmail);
                    startService(startServiceIntent);
                } else {

                    // volley request
                    ImageRequest imageRequest = new ImageRequest(remoteMessage.getData().get("imageUrl"), new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {

                            relativeImage = response;
                            sendNotification(remoteMessage.getData());
                        }
                    }, 0, 0, null, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            relativeImage = BitmapFactory.decodeResource(getResources(), R.drawable.default_profle);
                            sendNotification(remoteMessage.getData());
                        }
                    });

                    // Access the RequestQueue through your singleton class.
                    MySingleton.getInstance(this).addToRequestQueue(imageRequest);

                }

                break;

            // to receive location updates
            case UPDATE_LOCATION:

                Log.e(TAG, "Location Received");
                Log.e(TAG, remoteMessage.getData().get("longitude"));

                if (!MapsActivity.isStarted) {

                    Log.i("*****", "Inside IS not started");
                    // Start Activity
                    Intent mapsIntent = new Intent(getApplicationContext(), MapsActivity.class);
                    mapsIntent.putExtra("latitude", remoteMessage.getData().get("latitude"));
                    mapsIntent.putExtra("longitude", remoteMessage.getData().get("longitude"));
                    mapsIntent.putExtra("patientImage", remoteMessage.getData().get("imageUrl"));
                    mapsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mapsIntent);
                }

                // Activity already started
                if (MapsActivity.isActive == true) {

                    Log.e("**************", "Broadcast Receiver ********");
                    LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(this);
                    Intent intent = new Intent(MapsActivity.broadcast_receiver);


                    intent.putExtra("latitude", remoteMessage.getData().get("latitude"));
                    intent.putExtra("longitude", remoteMessage.getData().get("longitude"));

                    broadcaster.sendBroadcast(intent);

                }


                break;

            case DISCONNECT_USER:
                Log.e(TAG, "Disconnect");
                if (MapsActivity.isActive) {

                    LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(this);
                    Intent intent = new Intent(MapsActivity.broadcast_receiver);


                    intent.putExtra("disconnect", 1);


                    broadcaster.sendBroadcast(intent);
                }
                break;

            // to stop the GPS Service
            case STOP_SERVICE:
                if (ShareLocationService.isStarted == true) {

                    Intent stopServiceIntent = new Intent(this, ShareLocationService.class);
                    stopService(stopServiceIntent);
                }
                break;

        }

    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(Map<String, String> data) {

        Intent intent = new Intent(this, ReceiveOpenGpsRequestActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // my mess
        intent.putExtra("firstname", (String) data.get("firstname"));
        intent.putExtra("lastname", (String) data.get("lastname"));
        intent.putExtra("email", (String) data.get("email"));
        intent.putExtra("imageUrl", (String) data.get("imageUrl"));
        // end of my mess
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)

                // Volley Request to get image here


                .setLargeIcon(relativeImage)
                .setSmallIcon(R.mipmap.logo)
                .setContentTitle(data.get("firstname") + "" + data.get("lastname"))
                .setContentText(getResources().getString(R.string.wants_to_know))
                .setWhen(System.currentTimeMillis())
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setColor(getResources().getColor(R.color.colorPrimary));


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        notificationManager.notify(0, notificationBuilder.build());
        // notificationManager.cancel(notificationID);
    }

    /**
     * to check if location is enabled or not (True) if is is enabled.
     *
     * @return
     */
    private boolean checkIsLocationEnabled() {
        LocationManager locationManager = locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    /**
     * this method is used to get bitmap from URL
     *
     * @param src
     * @return
     */
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.i("getBitmapFromURL: ", "in bitmap fun");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
