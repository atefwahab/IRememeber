package wmad.iti.MutualMemories;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import wmad.iti.constants.Urls;
import wmad.iti.dto.Memory;
import wmad.iti.dto.Relative;
import wmad.iti.dto.User;
import wmad.iti.irememeber.R;
import wmad.iti.memories.CustomAdapter;
import wmad.iti.memories.WriteTextActivity;
import wmad.iti.model.ConnectionDetector;
import wmad.iti.model.GsonRequest;
import wmad.iti.model.MySingleton;
import wmad.iti.model.SharedPreferenceManager;
import wmad.iti.patentlist.CustomPatientActivityAdapter;
import wmad.iti.relativelist.CustomRelativeActivityAdapter;

public class MutualMemoryActivity extends AppCompatActivity {
    //toolbar
    private Toolbar toolbar;
    final String TAG = "myTag";
    // private RecyclerView.LayoutManager mLayoutManager;
    GsonRequest gsonRequest;
    RequestQueue requestQueue;
    RecyclerView recyclerView;
    MutualMemoryCustomAdapter adapter;
    private List<Memory> listMemories;
    ArrayList<Memory> patientMemories;
    ImageView image, videoImg, cameraImg, locationImg, takenImg;
    TextView enterMemory, video, camera, location;
    User user;
    int flag,type;
    ConnectionDetector connectionDetector;
    String patientEmailIntent, patientFirstName, patientLastName, relativeEmailIntent, relativeFirstNameIntent,
            relativeLastNameIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);
        // image= (ImageView) findViewById(R.id.imagep);
        //  videoImg= (ImageView) findViewById(R.id.videoimage);
        cameraImg = (ImageView) findViewById(R.id.cameraimg);
        locationImg = (ImageView) findViewById(R.id.locationimg);
        enterMemory = (TextView) findViewById(R.id.entermemory);
        video = (TextView) findViewById(R.id.video);
        camera = (TextView) findViewById(R.id.camera);
        takenImg = (ImageView) findViewById(R.id.imagetaken);

        type=getIntent().getIntExtra("type", 0) ;

        patientEmailIntent = getIntent().getStringExtra("patientEmail");
        patientFirstName = getIntent().getStringExtra("patientFirstName");
        patientLastName = getIntent().getStringExtra("patientLastName");

        relativeEmailIntent = getIntent().getStringExtra("relativeEmail");
        relativeFirstNameIntent = getIntent().getStringExtra("relativeFirstName");
        relativeLastNameIntent = getIntent().getStringExtra("relativeLastName");
        //when you click in camera
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 1;
                Intent intent = new Intent(getApplicationContext(), MutualMemoryWriteTextActivity.class);
                intent.putExtra("patientEmail", patientEmailIntent);
                intent.putExtra("relativeEmail", relativeEmailIntent);
                intent.putExtra("flag", flag);
                intent.putExtra("type",type);
                startActivity(intent);
                //selectImage();

            }
        });
        enterMemory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 2;
                Intent intent = new Intent(getApplicationContext(), MutualMemoryWriteTextActivity.class);
                intent.putExtra("relativeEmail", relativeEmailIntent);
                intent.putExtra("patientEmail", patientEmailIntent);
                intent.putExtra("flag", flag);
                intent.putExtra("type",type);
                startActivity(intent);

            }
        });


        // Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Memory");
        toolbar.setNavigationIcon(R.drawable.navigation_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //Initializing our users list
        listMemories = new ArrayList<>();
        //Calling method to get data of patients
        getMemoriesData(findViewById(android.R.id.content));

    }//end on create


    /**
     * This method is used to get all memories of patient
     */
    public void getMemoriesData(View view) {
        //to test connectivity of internet
        connectionDetector = new ConnectionDetector(getApplicationContext());
        Boolean isInternetPresent = connectionDetector.isConnectingToInternet();
        //if there is internet connection
        if (isInternetPresent == true) {
            HashMap<String, String> header = new HashMap<>();
            //to check if patient enter edit memories at specific relative to get mutual memories
            if (getIntent().getIntExtra("type", 0) == CustomRelativeActivityAdapter.IS_PATIENT) {

                header.put("patientEmail", SharedPreferenceManager.getUser(getApplicationContext()).getEmail());
                header.put("relativeEmail", relativeEmailIntent);
                Log.i("relativeEmailIntent", relativeEmailIntent + "relativeEmailIntent");

            }
          //  to check if relative enter edit memories at specific patient to get mutual memories
            if (getIntent().getIntExtra("type", 0) == CustomPatientActivityAdapter.IS_RELATIVE) {
                header.put("patientEmail", patientEmailIntent);
                header.put("relativeEmail", SharedPreferenceManager.getUser(getApplicationContext()).getEmail());
            }

            requestQueue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
            //Creating a json request to get memoriess
            gsonRequest = new GsonRequest(Urls.GET_MUTUAL_MEMORIES_URL, Request.Method.POST, Memory[].class, header, new Response.Listener<Memory[]>() {

                @Override
                public void onResponse(Memory[] memories) {
                    patientMemories = new ArrayList<>();

                    for (int i = 0; i < memories.length; i++) {
//               Toast.makeText(MemoryActivity.this, "Response of get relatives => " + memories[i].getMemoryText(), Toast.LENGTH_LONG).show();

                        Memory memory = new Memory();
                        memory.setMemoryId(memories[i].getMemoryId());
                        memory.setMemoryText(memories[i].getMemoryText());
                        memory.setDateTime(memories[i].getDateTime());
                        memory.setAddress(memories[i].getAddress());
                        memory.setCity(memories[i].getCity());
                        memory.setCountry(memories[i].getCountry());
                        memory.setImageUrl(memories[i].getImageUrl());
                        memory.setLatitude(memories[i].getLatitude());
                        memory.setLongitude(memories[i].getLongitude());
                        memory.setMemoryId(memories[i].getMemoryId());
                        if (memories[i].getRelative() != null) {
                            Relative relative = memory.setRelative(memories[i].getRelative());
                        }
                        patientMemories.add(memory);
//                        Toast.makeText(getApplicationContext(), relative.getFirstName(), Toast.LENGTH_LONG).show();
                        if(patientMemories.size()!=0){
                            initializeRecyclerView(patientMemories);
                        }
                        else{
                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.NoMutualMemories),Toast.LENGTH_LONG).show();
                        }
                        SharedPreferenceManager.saveMemories(getApplicationContext(), memories);
                    }



                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    // Toast.makeText(RelativesListHome.this, "Error Response of get relatives: " + volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    Log.i("onErrorResponse: ", volleyError.getMessage());
                    ArrayList<Memory> memories = SharedPreferenceManager.getMemories(getApplicationContext());
                    initializeRecyclerView(memories);

                }
            });


            //Adding request to the queue
            requestQueue.add(gsonRequest);
        } else {
            Snackbar snackbar = Snackbar.make(view, "No Connection", Snackbar.LENGTH_LONG);
            snackbar.show();
            ArrayList<Memory> memories = SharedPreferenceManager.getMemories(getApplicationContext());
            initializeRecyclerView(memories);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    public void initializeRecyclerView(ArrayList<Memory> patientMemories) {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        if(getIntent().getIntExtra("type",0)==CustomRelativeActivityAdapter.IS_PATIENT){adapter = new MutualMemoryCustomAdapter(patientMemories, this,  getIntent().getStringExtra("relativeFirstName"), getIntent().getStringExtra("relativeLastName"), getIntent().getIntExtra("type",0));}

        if(getIntent().getIntExtra("type",0)==CustomPatientActivityAdapter.IS_RELATIVE){adapter = new MutualMemoryCustomAdapter(patientMemories, this,  patientFirstName, patientLastName, getIntent().getIntExtra("type",0));}


        //Adding adapter to recyclerview
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

            getMemoriesData(findViewById(android.R.id.content));



    }
    @Override
    protected void onStop() {
        super.onStop();
        if (MySingleton.getInstance(this).getRequestQueue() != null) {
            MySingleton.getInstance(this).getRequestQueue().cancelAll(TAG);
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (MySingleton.getInstance(this).getRequestQueue() != null) {
            MySingleton.getInstance(this).getRequestQueue().cancelAll(TAG);
        }
    }

}