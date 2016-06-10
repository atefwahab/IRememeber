package wmad.iti.adapter;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import wmad.iti.dto.PatientHomePage;
import wmad.iti.irememeber.PatientHomeActivity;
import wmad.iti.irememeber.R;

/**
 * Created by Nihal on 13/05/2016.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private List<PatientHomePage> patientData;
    private LayoutInflater mInflater;
    MyLisnterInt myLisnterInt;



    public RecyclerAdapter(Activity activity, List<PatientHomePage> patientData) {
        this.patientData = patientData;
        this.mInflater = LayoutInflater.from(activity);
        myLisnterInt = (MyLisnterInt) activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.custom_view_home_patient, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PatientHomePage currentObj = patientData.get(position);
        holder.setData(currentObj);

    }

    @Override
    public int getItemCount() {
        return patientData.size();

    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView iconImage;
        CardView cardView;

        public MyViewHolder(final View itemView) {
            super(itemView);
            title       = (TextView)  itemView.findViewById(R.id.txv_row_patient);
            iconImage    = (ImageView) itemView.findViewById(R.id.img_row_patient);
            cardView  = (CardView) itemView.findViewById(R.id.card_view_patient);



            itemView.setOnClickListener(new View.OnClickListener(){


                @Override
                public void onClick(View v) {

                    int x = getAdapterPosition();
                    Log.i("number of activity *_* ",Integer.toString(x));
                    myLisnterInt.goToPatientActivities(v,x);
                }
            });
        }


        public void setData(PatientHomePage current) {
            this.title.setText(current.getTitle());
            this.iconImage.setImageResource(current.getImageID());
            if(current.getImageID()==R.drawable.recieve_request){

                PatientHomeActivity.instance().updateNotifcationNum(iconImage);
                }//end of if
        }//end of setDatamethod

}//end of inner class
}//end of class

