package wmad.iti.irememeber;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import wmad.iti.memories.MemoryActivity;
import wmad.iti.relativelist.RelativesListHome;

/**
 * Created by Dono on 6/20/2016.
 */
public class NotificationService extends Service {

//    String[] iconsTitle=  PatientHomeActivity.instance().getResources().getStringArray(R.array.remind_arr);
    String[] iconsTitle=new String[]{"Please check your memory","Are you ok?","Check your relative"};

    int i=0;

    public NotificationService() {

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.i("onHandleIntent: ","on Handle intent");
        Thread threadReminde=new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if(i<iconsTitle.length){
                        try {
                            // 15 minutes
                           // Thread.sleep(15*60*1000);
                            Thread.sleep(60000);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        showNotifcation(iconsTitle[i],i);
                        Log.i("run: ",iconsTitle[i]);
                        i++;
                    }else{
                        i=0;
                    }

                }
            }
        });
        threadReminde.start();

    }
    @Override
    public void onDestroy() {

    }

    public void showNotifcation(String status, int position){

        // define sound URI, the sound to be played when there's a notification

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // intent triggered, you can add other intent for other actions
        Intent myIntent = new Intent(getApplicationContext(),whichClass(i));
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,myIntent,Intent.FLAG_ACTIVITY_NEW_TASK);


        // this is it, we'll build the notification!

        // in the addAction method, if you don't want any icon, just set the first param to 0
        Notification mNotification = new Notification.Builder(this)
                .setContentTitle("IRemember")
                .setContentIntent(pendingIntent)
                .setContentText(status)
                .setSmallIcon(R.drawable.logo)

                .setSound(soundUri)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);





        // If you want to hide the notification after it was selected, do the code below

        //  mNotification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, mNotification);

    }


//    public void cancelNotification(int notificationId){
//
//        if (Context.NOTIFICATION_SERVICE!=null) {
//
//            String ns = Context.NOTIFICATION_SERVICE;
//
//            NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
//
//            nMgr.cancel(notificationId);
//
//        }

    public Class whichClass(int position){
        Class classes = null;
        Log.i("whichClass: ",position+"");
        switch (position){
            case 0:
                classes=MemoryActivity.class;
                break;
            case 1:
                classes=PatientHomeActivity.class;
                break;
            case 2:
                classes=RelativesListHome.class;
                break;
        }
        return classes;
    }
}
