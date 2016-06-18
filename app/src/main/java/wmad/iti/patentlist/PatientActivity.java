package  wmad.iti.patentlist;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.mikhaellopez.circularimageview.CircularImageView;

import wmad.iti.irememeber.R;
import wmad.iti.model.MySingleton;


public class PatientActivity extends AppCompatActivity {
    TextView locationTextView;
    CircularImageView circularImageView;
    String patientFirstName,patientLastName,imageUrl,patientEmail;
    Toolbar toolbar;
    GridView gridView;
    int[] iconsImages;
    String[] iconTitles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
        circularImageView= (CircularImageView) findViewById(R.id.profile_circularImageView);

        patientFirstName=getIntent().getStringExtra("patientFirstName");
        patientLastName=getIntent().getStringExtra("patientLastName");
        imageUrl=getIntent().getStringExtra("imageUrl");
        // Atef added this code ...
        patientEmail= getIntent().getStringExtra("patientEmail");
        Log.e("patient Email",patientEmail);
        // ......................................................

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.navigation_back);
        getSupportActionBar().setTitle(patientFirstName + " " + patientLastName);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getPatientPhoto();
       //GridView
        gridView = (GridView) findViewById(R.id.gridView1);
        iconsImages = new int[]{R.drawable.location, R.drawable.add_memory};
        iconTitles = getResources().getStringArray(R.array.patient_activity_arr);
        gridView.setAdapter(new CustomPatientActivityAdapter(this, iconTitles, iconsImages,patientEmail));
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * This method is used to get photo of patient
     */
    public void getPatientPhoto(){

        ImageRequest request = new ImageRequest(imageUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        circularImageView.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        circularImageView.setImageResource(R.drawable.patient_img);
                    }
                });

        MySingleton.getInstance(this).addToRequestQueue(request);

    }
}
