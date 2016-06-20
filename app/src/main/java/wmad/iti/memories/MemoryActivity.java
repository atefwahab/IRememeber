package wmad.iti.memories;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import wmad.iti.constants.Urls;
import wmad.iti.dto.Memory;
import wmad.iti.dto.User;
import wmad.iti.irememeber.R;
import wmad.iti.model.GsonRequest;
import wmad.iti.model.MySingleton;
import wmad.iti.model.SharedPreferenceManager;

public class MemoryActivity extends AppCompatActivity {
    //toolbar
    private Toolbar toolbar;

   // private RecyclerView.LayoutManager mLayoutManager;
    GsonRequest gsonRequest;
    RequestQueue requestQueue;
    RecyclerView recyclerView;
    CustomAdapter adapter;
    private List<Memory> listMemories;
    ArrayList<Memory> patientMemories;
    ImageView image,videoImg,cameraImg,locationImg, takenImg;
    TextView enterMemory,video,camera,location;
    User user;
    int flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);
       // image= (ImageView) findViewById(R.id.imagep);
      //  videoImg= (ImageView) findViewById(R.id.videoimage);
        cameraImg= (ImageView) findViewById(R.id.cameraimg);
        locationImg= (ImageView) findViewById(R.id.locationimg);
        enterMemory= (TextView) findViewById(R.id.entermemory);
        video= (TextView) findViewById(R.id.video);
        camera= (TextView) findViewById(R.id.camera);
        takenImg=(ImageView)findViewById(R.id.imagetaken);

        //when you click in camera
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag=1;
                Intent intent = new Intent(getApplicationContext(),WriteTextActivity.class);
                intent.putExtra("flag",flag);
                startActivity(intent);
                //selectImage();

            }
        });
        enterMemory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag=2;
          Intent intent=new Intent(getApplicationContext(),WriteTextActivity.class);
                intent.putExtra("flag",flag);
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
        getMemoriesData();

    }//end on create





    /**
     * This method is used to get all memories of patient
     */
    public void getMemoriesData() {

        HashMap<String, String> header = new HashMap<>();
        header.put("patientEmail", SharedPreferenceManager.getUser(getApplicationContext()).getEmail());
        requestQueue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
        //Creating a json request to get memoriess
        gsonRequest = new GsonRequest(Urls.GET_MEMORIES_URL, Request.Method.POST, Memory[].class, header, new Response.Listener<Memory[]>() {

            @Override
            public void onResponse(Memory[] memories) {
                patientMemories = new ArrayList<>();

                for (int i = 0; i < memories.length; i++) {
//               Toast.makeText(MemoryActivity.this, "Response of get relatives => " + memories[i].getMemoryText(), Toast.LENGTH_LONG).show();

                    Memory memory = new Memory();

                    memory.setMemoryText(memories[i].getMemoryText());
                    memory.setDateTime(memories[i].getDateTime());
                    memory.setAddress(memories[i].getAddress());
                    memory.setCity(memories[i].getCity());
                    memory.setCountry(memories[i].getCountry());
                    memory.setImageUrl(memories[i].getImageUrl());
                    memory.setLatitude(memories[i].getLatitude());
                    memory.setLongitude(memories[i].getLongitude());
                    memory.setMemoryId(memories[i].getMemoryId());
                    user=memory.setUser(memories[i].getUser());

                    patientMemories.add(memory);
//                    Toast.makeText(getApplicationContext(),patientMemories.size()+""+memories[i].getMemoryId(),Toast.LENGTH_LONG).show();
                }

                //addTextMemoriesToList(patientMemories);
                initializeRecyclerView();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                // Toast.makeText(RelativesListHome.this, "Error Response of get relatives: " + volleyError.getMessage(), Toast.LENGTH_LONG).show();
                Log.i("onErrorResponse: ", volleyError.getMessage());


            }
        });


        //Adding request to the queue
        requestQueue.add(gsonRequest);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    public void initializeRecyclerView( ){
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new CustomAdapter(patientMemories, this);

        //Adding adapter to recyclerview
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
    }
    @Override
    protected void onResume() {
        super.onResume();
        getMemoriesData();

    }

}