package wmad.iti.irememeber;

import android.content.Intent;
import android.net.Uri;
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
import wmad.iti.adapter.RecyclerAdapter;
import wmad.iti.constants.Urls;
import wmad.iti.dto.PatientHomePage;
import wmad.iti.dto.Relative;
import wmad.iti.dto.User;
import wmad.iti.model.GsonRequest;
import wmad.iti.model.MySingleton;
import wmad.iti.model.SharedPreferenceManager;
import wmad.iti.personalprofile.PatientProfileActivity;
import wmad.iti.relativelist.RelativesListHome;
import wmad.iti.requests.BadgeView;
import wmad.iti.requests.ShowRequestPatient;

public class PatientHomeActivity extends AppCompatActivity implements MyLisnterInt{
    RecyclerView recyclerView;
    static PatientHomeActivity inst;
    PatientHomePage patientHomePage;
    User user;
    Double latitude,longitude;
    String mapLabel;


    static public PatientHomeActivity instance(){
      return inst;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_home);
        user=SharedPreferenceManager.getUser(getApplicationContext());
        latitude = user.getLatitude();
        Log.i("latitude", String.valueOf(latitude));
        longitude = user.getLongitude();
        Log.i("longitude", String.valueOf(longitude));
        mapLabel = getResources().getString(R.string.maplLabel);
        Log.i("the location -> ",mapLabel);
        inst=this;
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_patient);
        RecyclerAdapter adapter = new RecyclerAdapter(this, PatientHomePage.getData());
        recyclerView.setAdapter(adapter);


        LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(this);
        mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManagerVertical);

        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mGridLayoutManager);
    }



    @Override
    public void goToPatientActivities(View v, int position) {

        switch (position){

            case 0:  // personal profile activity
                Intent profileIntent = new Intent(this, PatientProfileActivity.class);
                startActivity(profileIntent);
                break;
            case 1: // requests activity
                Intent RequestsIntent = new Intent(this, ShowRequestPatient.class);
                startActivity(RequestsIntent);
                break;
            case 2: //relative activity
                Intent RelativesIntent = new Intent(this, RelativesListHome.class);
                startActivity(RelativesIntent);
                break;
            case 3: //home activity
                // Create a Uri from an intent string. Use the result to create an Intent.
                Uri gmmIntentUri = Uri.parse("geo:0,0?q="+latitude+","+longitude+"(" + mapLabel + ")");
                // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                // Make the Intent explicit by setting the Google Maps package
                mapIntent.setPackage("com.google.android.apps.maps");
                // Attempt to start an activity that can handle the Intent
                startActivity(mapIntent);
                break;
            case 4: //add memory activity
                Intent MemoriesIntent = new Intent(this, SplashScreen.class);
                startActivity(MemoriesIntent);
                break;
            case 5: //setting activity
                Intent SettingsIntent = new Intent(this, SplashScreen.class);
                startActivity(SettingsIntent);
                break;
            case 6: //panic mode activity
                Intent PanicIntent = new Intent(this, SplashScreen.class);
                startActivity(PanicIntent);
                break;

        }

    }

    @Override
    public void goToRelativeActivities(View v, int position) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        inst=this;
    }


    public void updateNotifcationNum(final View icon){


        RequestQueue queue= MySingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("patientemail", SharedPreferenceManager.getEmail(this.getApplicationContext()));

        GsonRequest jsonRequest= new GsonRequest(Urls.WEB_SERVICE_GET_REQUEST_URL, Request.Method.POST, Relative[].class,hashMap, new Response.Listener<Relative[]>() {
            @Override
            public void onResponse(Relative[] response) {
                int sizeOfArray=response.length;

                BadgeView badge = new BadgeView(getApplicationContext(), icon);

             //   Toast.makeText(getApplicationContext(),""+sizeOfArray,Toast.LENGTH_LONG).show();
                badge.setText(""+sizeOfArray);
                badge.show();

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("Response error", error.toString());
            }
        });


        queue.add(jsonRequest);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        setUpRecyclerView();
    }
}
