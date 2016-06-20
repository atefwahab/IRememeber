package wmad.iti.irememeber;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;

import wmad.iti.adapter.MyLisnterInt;
import wmad.iti.adapter.RelativeRecyclerAdapter;
import wmad.iti.constants.Urls;
import wmad.iti.dto.RelativeHomePage;
import wmad.iti.dto.Status;
import wmad.iti.model.GsonRequest;
import wmad.iti.model.MySingleton;
import wmad.iti.model.SharedPreferenceManager;
import wmad.iti.patentlist.PatientHome;
import wmad.iti.requests.SendRequest;
import wmad.iti.personalprofile.RlativeProfileActivity;

public class RelativeHomeActivity extends AppCompatActivity implements MyLisnterInt{

    RecyclerView recyclerView;
    static RelativeHomeActivity inst;
    BluetoothAdapter mBluetoothAdapter;

    static public RelativeHomeActivity instance(){
        return inst;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relative_home);
        inst=this;
        updateMacAddress();
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_relative);
        RelativeRecyclerAdapter adapter = new RelativeRecyclerAdapter(this, RelativeHomePage.getRelativeData());
        recyclerView.setAdapter(adapter);


        LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(this);
        mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManagerVertical);

        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mGridLayoutManager);
    }

    @Override
    public void goToPatientActivities(View v, int position) {

    }

    @Override
    public void goToRelativeActivities(View v, int position) {
        switch (position) {
            case 0: //relative profile activity
                Intent profileIntent = new Intent(this,RlativeProfileActivity.class);
                startActivity(profileIntent);
                break;
            case 1: //add patient activity
                Intent AddPatientIntent = new Intent(this, SendRequest.class);
                startActivity(AddPatientIntent);
                break;
            case 2: //patients activity
                Intent PatientsIntent = new Intent(this, PatientHome.class);
                startActivity(PatientsIntent);
                break;
        }

    }
    public String getMacAddress() {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        String macAddress = mBluetoothAdapter.getAddress();
     //   Toast.makeText(getApplicationContext(),macAddress,Toast.LENGTH_LONG).show();
        Log.e("getMacAddress: ", macAddress + ">> " + mBluetoothAdapter.getAddress());
        return macAddress;
    }


    public void updateMacAddress(){
        RequestQueue queue= MySingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        final String macAddress=getMacAddress();
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("email", SharedPreferenceManager.getEmail(this.getApplicationContext()));
        hashMap.put("macAddress",macAddress);
        GsonRequest jsonRequest= new GsonRequest(Urls.WEB_SERVICE_MAC_ADDRESS_URL, Request.Method.POST, Status.class,hashMap, new Response.Listener<Status>() {
            @Override
            public void onResponse(Status response) {

                if(response.getStatus()==1){
                    SharedPreferenceManager.saveMacAddress(getApplicationContext(),macAddress);
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
}
