package  wmad.iti.patentlist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import wmad.iti.irememeber.R;

public class PatientActivity extends AppCompatActivity {
    TextView locationTextView;
    String patientFirstName,patientLastName;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
        locationTextView=(TextView)findViewById(R.id.loctionOfPtientTextView);

        patientFirstName=getIntent().getStringExtra("patientFirstName");
        patientLastName=getIntent().getStringExtra("patientLastName");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.navigation_back);
        getSupportActionBar().setTitle(patientFirstName + " " + patientLastName);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }});

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
