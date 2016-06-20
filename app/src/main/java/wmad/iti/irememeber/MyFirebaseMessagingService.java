package wmad.iti.irememeber;

/**
 * Created by atef on 5/31/2016.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private int REQUEST_CODE = 0;
    private final int LOCATION_REQUEST = 9393;
    private final int UPDATE_LOCATION = 4141;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
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
                    String relativeEmail =remoteMessage.getData().get("email");
                    startServiceIntent.putExtra("relativeEmail",relativeEmail);
                    Log.e(TAG,relativeEmail);
                    startService(startServiceIntent);
                } else {

                    //Calling method to generate notification
                    sendNotification(remoteMessage.getData());
                }

                break;

            // to receive location updates
            case UPDATE_LOCATION:

                Log.e(TAG, "Location Received");
                Log.e(TAG,remoteMessage.getData().get("longitude"));
/*
               if(!MapsActivity.isStarted){

                    Log.i("*****","Inside IS not started");
                    // Start Activity
                    Intent mapsIntent = new Intent(getApplicationContext(),MapsActivity.class);
                    mapsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mapsIntent);
                }

                // Activity already started
                if(MapsActivity.isActive == true){

                    Log.e("**************","Broadcast Receiver ********");
                    LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(this);
                    Intent intent = new Intent("MAPS_BROADCAST_RECEIVER");
                    //intent.setAction("NIHAL_BROADCAST_RECEIVER");

                    intent.putExtra("latitude",remoteMessage.getData().get("latitude"));
                    intent.putExtra("longitude",remoteMessage.getData().get("longitude"));

                    broadcaster.sendBroadcast(intent);
                    //sendBroadcast(intent);
                }*/


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
                .setSmallIcon(R.drawable.location)
                //.setLargeIcon()
                .setContentTitle(data.get("firstname") + " " + data.get("lastname"))
                .setContentText("your relative " + data.get("firstname") + " " + data.get("lastname") + " wants to know your location.")
                .setWhen(System.currentTimeMillis())
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        notificationManager.notify(0, notificationBuilder.build());
        // notificationManager.cancel(notificationID);
    }

    public void cancelNotification(int id, String notificationTAG) {

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
}
