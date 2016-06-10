package wmad.iti.irememeber;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import wmad.iti.adapter.MyLisnterInt;
import wmad.iti.adapter.RelativeRecyclerAdapter;
import wmad.iti.dto.RelativeHomePage;
import wmad.iti.patentlist.PatientHome;
import wmad.iti.requests.SendRequest;
import wmad.iti.personalprofile.RlativeProfileActivity;

public class RelativeHomeActivity extends AppCompatActivity implements MyLisnterInt{

    RecyclerView recyclerView;
    static RelativeHomeActivity inst;
    static public RelativeHomeActivity instance(){
        return inst;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relative_home);
        inst=this;
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
}
