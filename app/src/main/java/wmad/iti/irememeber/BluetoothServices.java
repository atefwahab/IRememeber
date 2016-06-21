package wmad.iti.irememeber;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;

import wmad.iti.constants.Urls;
import wmad.iti.dto.Relative;
import wmad.iti.dto.Status;
import wmad.iti.model.GsonRequest;
import wmad.iti.model.MySingleton;
import wmad.iti.model.SharedPreferenceManager;

/**
 * Created by Dono on 6/20/2016.
 */
public class BluetoothServices extends Service {

    private BluetoothAdapter mBluetoothAdapter;
    IntentFilter filter;

    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        mBluetoothAdapter	= BluetoothAdapter.getDefaultAdapter();
        Log.i("onHandleIntent: ","start service");

        if (mBluetoothAdapter == null) {
            Log.i("onHandleIntent: ","Bluetooth is unsupported by this device");
        }
        filter = new IntentFilter();

        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);


        Thread threadBluetooth=new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    Log.i("run: ","in Thread");
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        PatientHomeActivity.instance().startActivityForResult(intent,1000);
                    }
                    registerReceiver(mReceiver, filter);
                    mBluetoothAdapter.startDiscovery();
                    try {
                        //5 minutes
                      //  Thread.sleep(5*60*1000);
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        threadBluetooth.start();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("onReceive: ","i am in onRecevie");

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                if (state == BluetoothAdapter.STATE_ON) {
                    Log.i("device", "Enabled");
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                mDeviceList = new ArrayList<BluetoothDevice>();
                //  mProgressDlg.show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //  mProgressDlg.dismiss();
                mBluetoothAdapter.cancelDiscovery();
                Log.i(" finish  " ,"finish discover");
                //galy device 3ayza a3redha
                for(int i=0;i<mDeviceList.size();i++){
                    String mac=mDeviceList.get(i).getAddress();
                    checkMacAddress(mac);
                }
                unregisterReceiver(mReceiver);
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDeviceList.add(device);
                Log.i("Found device " , device.getName()+">> "+device.getAddress());
            }
        }
    };

    public void checkMacAddress(String macAddress){
        RequestQueue queue= MySingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("patientEmail", SharedPreferenceManager.getEmail(this.getApplicationContext()));
        hashMap.put("macAddress",macAddress);
        GsonRequest jsonRequest= new GsonRequest(Urls.WEB_SERVICE_BLUETOOTH_URL, Request.Method.POST, Status.class,hashMap, new Response.Listener<Status>() {
            @Override
            public void onResponse(Status response) {
                if(response.getStatus()==1){
                    Relative relative=response.getRelative();
                    // show dialog relative information
                    showDialog(relative);
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("Response error", error.toString());
            }
        });
        queue.add(jsonRequest);
    }

    private void showDialog(Relative relative) {

        Intent intent=new Intent(getApplicationContext(),BluetoothDialog.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("fullName",relative.getFirstName()+" "+relative.getLastName());
        intent.putExtra("image",relative.getImageUrl());
        intent.putExtra("relation",relative.getRelationshipPosition());
        Log.i("showDialog: ",relative.getFirstName());
        Log.i("showDialog: ",relative.getLastName());
        Log.i("showDialog: ",relative.getRelationshipPosition()+"");
        Log.i("showDialog: ",relative.getImageUrl());

        startActivity(intent);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
