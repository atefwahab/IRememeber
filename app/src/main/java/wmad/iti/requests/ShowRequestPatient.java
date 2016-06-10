package wmad.iti.requests;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;
import java.util.HashMap;

import wmad.iti.constants.Urls;
import wmad.iti.irememeber.R;
import wmad.iti.model.ConnectionDetector;
import wmad.iti.model.GsonRequest;
import wmad.iti.model.MySingleton;
import wmad.iti.dto.Relative;
import wmad.iti.model.SharedPreferenceManager;

/*
*author donia
* show list of request in patient
 */
public class ShowRequestPatient extends AppCompatActivity {

    static ShowRequestPatient inst;
    ArrayList<String> names;
    ArrayList<String> images;
    ArrayList<Integer> relations;
    ArrayList<String> emails;
    ConnectionDetector connectionDetector;
    Toolbar toolbar;
    public static ShowRequestPatient instance(){
        return inst;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.show_request_patient);
        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.navigation_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // get mail from shared prefrance
        connectionDetector = new ConnectionDetector(getApplicationContext());
        if (connectionDetector.isConnectingToInternet()) {
            String patientEmail = SharedPreferenceManager.getEmail(getApplicationContext());
            HashMap<String, String> header = new HashMap<>();
            header.put("patientemail", patientEmail);
            RequestQueue queue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();

            GsonRequest jsonRequest = new GsonRequest(Urls.WEB_SERVICE_GET_REQUEST_URL, Request.Method.POST, Relative[].class, header, new Response.Listener<Relative[]>() {
                @Override
                public void onResponse(Relative[] response) {

                    names = new ArrayList<>();
                    images = new ArrayList<>();
                    relations = new ArrayList<>();
                    emails = new ArrayList<>();
                    for (int i = 0; i < response.length; i++) {

                        Relative jresponse = response[i];
                        String email = jresponse.getEmail();
                        String image = jresponse.getImageUrl();
                        String name = jresponse.getFirstName() + " " + jresponse.getLastName();
                        Integer relation = jresponse.getRelationshipPosition();

                        names.add(name);
                        emails.add(email);
                        relations.add(relation);
                        images.add(image);

                    }


                    setUpRecyclerView();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });


            queue.add(jsonRequest);
        }else{
            // here if there is not internet connection the snack bar will be displayed
            Snackbar snackbar = Snackbar.make(findViewById(R.id.showRequestActivity),getResources().getString(R.string.no_internt_msg),Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }





    private void setUpRecyclerView() {

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerAdapterListRequest adapter=new RecyclerAdapterListRequest(this,images, names,relations,emails);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(this);
        mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManagerVertical);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void onStart() {
        super.onStart();
        inst = this;

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
