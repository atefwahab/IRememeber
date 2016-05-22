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

import wmad.iti.dto.RelativeHomePage;
import wmad.iti.irememeber.R;

/**
 * Created by Nihal on 16/05/2016.
 */
public class RelativeRecyclerAdapter extends RecyclerView.Adapter<RelativeRecyclerAdapter.MyViewHolder> {

    private List<RelativeHomePage> relativeData;
    private LayoutInflater mInflater;
    MyLisnterInt myLisnterInt;

    public RelativeRecyclerAdapter(Activity activity, List<RelativeHomePage> relativeData) {
        this.relativeData = relativeData;
        this.mInflater = LayoutInflater.from(activity);
        myLisnterInt = (MyLisnterInt) activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.custom_view_home_relative, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        RelativeHomePage currentObj = relativeData.get(position);
        holder.setData(currentObj);
    }

    @Override
    public int getItemCount() {
        return relativeData.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView iconImage;
        CardView cardView;

        public MyViewHolder(final View itemView) {
            super(itemView);
            title       = (TextView)  itemView.findViewById(R.id.txv_row_relative);
            iconImage    = (ImageView) itemView.findViewById(R.id.img_row_relative);
            cardView  = (CardView) itemView.findViewById(R.id.card_view_relative);



            itemView.setOnClickListener(new View.OnClickListener(){


                @Override
                public void onClick(View v) {

                    int x = getAdapterPosition();
                    Log.i("number of activity *_* ", Integer.toString(x));
                    myLisnterInt.goToRelativeActivities(v,x);
                }
            });
        }


        public void setData(RelativeHomePage current) {
            this.title.setText(current.getRelativeTitle());
            this.iconImage.setImageResource(current.getRelativeImageID());
        }
    }
}
