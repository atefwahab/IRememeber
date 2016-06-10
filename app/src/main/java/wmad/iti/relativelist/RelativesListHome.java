package wmad.iti.relativelist;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import wmad.iti.dto.Relative;
import wmad.iti.dto.User;
import wmad.iti.irememeber.R;
import wmad.iti.model.ConnectionDetector;
import wmad.iti.model.GsonRequest;
import wmad.iti.model.MySingleton;
import wmad.iti.model.SharedPreferenceManager;



/**
 * This class used to view relative's list
 * @Author Doaa
 */
public class RelativesListHome extends AppCompatActivity {
    //Creating a List of user
    private List<User> listUsers;
    //Creating Views
    private RecyclerView recyclerView;
    private RelativeAdapter adapter;

    GsonRequest gsonRequest;
    RequestQueue requestQueue;
    ConnectionDetector connectionDetector;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //to initialize fresco used to load images
        Fresco.initialize(this);
        setContentView(R.layout.relatives_list_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.navigation_back);
        getSupportActionBar().setTitle("Relatives");

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
     * This method used to get data of Relatives
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
            header.put("patientEmail", SharedPreferenceManager.getEmail(getApplicationContext()));
            requestQueue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
            //Creating a json request to get patients
            gsonRequest = new GsonRequest(Urls.WEB_SERVICE_GET_RELATIVES_URL, Request.Method.POST, Relative[].class, header, new Response.Listener<Relative[]>() {

                @Override
                public void onResponse(Relative[] users) {
                    ArrayList<Relative> relatives = new ArrayList<>();
                    ;
                    for (int i = 0; i < users.length; i++) {
                    //    Toast.makeText(RelativesListHome.this, "Response of get relatives => " + users[i].getFirstName(), Toast.LENGTH_LONG).show();

                        Relative relative=new Relative();
                        relative.setFirstName(users[i].getFirstName());
                        relative.setLastName(users[i].getLastName());
                        relative.setImageUrl(users[i].getImageUrl());
                        relative.setPhoneNumber(users[i].getPhoneNumber());
                        relative.setAddress(users[i].getAddress());
                        relatives.add(relative);
                    }

                    addRelativesToList(relatives);

                  SharedPreferenceManager.saveRelatives(getApplicationContext(), users);
                 //   Toast.makeText(getApplicationContext(), "result of cache relative= " + result, Toast.LENGTH_LONG).show();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                   // Toast.makeText(RelativesListHome.this, "Error Response of get relatives: " + volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    Log.i("onErrorResponse: ", volleyError.getMessage());
                    ArrayList<Relative> arrRelatives =SharedPreferenceManager.getRelatives(getApplicationContext());
                    addRelativesToList(arrRelatives);

                }
            });


            //Adding request to the queue
            requestQueue.add(gsonRequest);
        }//end if
        if(isInternetPresent==false) {


            Snackbar snackbar=Snackbar.make(view,getApplicationContext().getResources().getString(R.string.NoConnection),Snackbar.LENGTH_LONG);
            snackbar.show();
         ArrayList<Relative> relatives = SharedPreferenceManager.getRelatives(getApplicationContext());
         addRelativesToList(relatives);
        }
    }//end getData

    /**
     * This method is used to initialize recycler view
     */

    public void initializeRecyclerView( ){
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new RelativeAdapter(listUsers, this);

        //Adding adapter to recyclerview
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
    }


    private void addRelativesToList(ArrayList<Relative> users) {
        for (int i=0;i<users.size();i++){

            User user  =users.get(i);
            listUsers.add(user);
        }

        //This method used to initialize recycler view
        initializeRecyclerView();

    }


}
