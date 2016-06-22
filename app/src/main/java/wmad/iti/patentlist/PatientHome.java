package wmad.iti.patentlist;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import wmad.iti.constants.Urls;
import wmad.iti.dto.User;
import wmad.iti.irememeber.R;
import wmad.iti.model.ConnectionDetector;
import wmad.iti.model.GsonRequest;
import wmad.iti.model.MySingleton;
import wmad.iti.model.SharedPreferenceManager;


public class PatientHome extends AppCompatActivity {
    //Creating a List of user
    private List<User> listUsers;
    //Creating Views
    private RecyclerView recyclerView;
    private PatientAdapter adapter;
    Toolbar toolbar;
    GsonRequest gsonRequest;
    RequestQueue requestQueue;
    ConnectionDetector connectionDetector;
    static PatientHome inst;

    public static PatientHome instance(){return inst;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //to initialize fresco used to load images
        Fresco.initialize(this);
        setContentView(R.layout.patient_home);
        inst=this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.navigation_back);
        getSupportActionBar().setTitle("Patients");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Initializing our users list
        listUsers = new ArrayList<>();
        //Calling method to get data of patients
        getData(findViewById(android.R.id.content));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    /**
     * This method used to get data of patients
     *
     */
    private void getData( View view) {
        //to test connectivity of internet
        connectionDetector = new ConnectionDetector(getApplicationContext());
        Boolean isInternetPresent = connectionDetector.isConnectingToInternet();
        //if there is internet connection
        if (isInternetPresent==true) {

            //to pass parameters to url
            HashMap<String, String> header = new HashMap<>();
            header.put("relativeEmail", SharedPreferenceManager.getEmail(getApplicationContext()));
            requestQueue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
            //Creating a json request to get patients
            gsonRequest = new GsonRequest(Urls.WEB_SERVICE_GET_PATIENT_URL, Request.Method.POST, User[].class, header, new Response.Listener<User[]>() {

                @Override
                public void onResponse(User[] users) {
                    ArrayList<User> patients = new ArrayList<>();
                    ;
                    for (int i = 0; i < users.length; i++) {

                        User user = new User();
                        user.setFirstName(users[i].getFirstName());
                        user.setLastName(users[i].getLastName());
                        user.setImageUrl(users[i].getImageUrl());
                        user.setEmail(users[i].getEmail());

                        patients.add(user);
                    }
                    addPatientsToList(patients);

                    boolean result = SharedPreferenceManager.savePatients(getApplicationContext(), users);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.i("onErrorResponse: ", volleyError.toString());
//                    ArrayList<User>arrPatients=SharedPreferenceManager.getPatients(getApplicationContext());
//                    addPatientsToList(arrPatients);

                }
            });


            //Adding request to the queue
            requestQueue.add(gsonRequest);
        }//end if
        if(isInternetPresent==false) {

            //Toast.makeText(getApplicationContext(), "There is no internet connection ", Toast.LENGTH_LONG).show();
            Snackbar snackbar=Snackbar.make(view,"No Connection",Snackbar.LENGTH_LONG);
            snackbar.show();
            ArrayList<User> patients = SharedPreferenceManager.getPatients(getApplicationContext());
            addPatientsToList(patients);
        }
    }//end getData

    /**
     * This method is used to initialize recycler view
     */

    public void initializeRecyclerView( ){
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new PatientAdapter(listUsers, this);

        //Adding adapter to recyclerview
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
    }


    private void addPatientsToList(ArrayList<User> users) {
         for (int i=0;i<users.size();i++){

               User user  =users.get(i);
             listUsers.add(user);
          }

        //This method used to initialize recycler view
        initializeRecyclerView();

    }

}



