package wmad.iti.irememeber;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import wmad.iti.adapter.MyLisnterInt;
import wmad.iti.adapter.RecyclerAdapter;
import wmad.iti.dto.PatientHomePage;

public class PatientHomeActivity extends AppCompatActivity implements MyLisnterInt{
    RecyclerView recyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_home);
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
            case 0:
                Intent profileIntent = new Intent(this, OptionsActivity.class);
                startActivity(profileIntent);
                break;
            case 1:
                Intent RequestsIntent = new Intent(this, SplashScreen.class);
                startActivity(RequestsIntent);
                break;
            case 2:
                Intent RelativesIntent = new Intent(this, SplashScreen.class);
                startActivity(RelativesIntent);
                break;
            case 3:
                Intent homeIntent = new Intent(this, SplashScreen.class);
                startActivity(homeIntent);
                break;
            case 4:
                Intent MemoriesIntent = new Intent(this, SplashScreen.class);
                startActivity(MemoriesIntent);
                break;
            case 5:
                Intent SettingsIntent = new Intent(this, SplashScreen.class);
                startActivity(SettingsIntent);
                break;
            case 6:
                Intent PanicIntent = new Intent(this, SplashScreen.class);
                startActivity(PanicIntent);
                break;

        }

    }

    @Override
    public void goToRelativeActivities(View v, int position) {

    }
}
