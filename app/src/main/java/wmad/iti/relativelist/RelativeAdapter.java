package wmad.iti.relativelist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import wmad.iti.constants.Urls;
import wmad.iti.dto.Status;
import wmad.iti.dto.User;
import wmad.iti.irememeber.R;
import wmad.iti.model.ConnectionDetector;
import wmad.iti.model.GsonRequest;
import wmad.iti.model.MySingleton;
import wmad.iti.model.SharedPreferenceManager;
import wmad.iti.patentlist.PatientActivity;

/**
 * Created by Doaa on 5/25/2016.
 */
public class RelativeAdapter extends RecyclerView.Adapter<RelativeAdapter.ViewHolder> {

    private Context context;
    List<User> users;

    GsonRequest gsonRequest;
    RequestQueue requestQueue;
    ConnectionDetector connectionDetector;
    Boolean isInternetPresent;
    User user;

    public RelativeAdapter(List<User> users, Context context) {
        super();
        //Getting all users
        this.users = users;
        this.context = context;
        //to checck connection to internet to delete patient
        connectionDetector = new ConnectionDetector(context);
        isInternetPresent = connectionDetector.isConnectingToInternet();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_relatives_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Activity activity = (Activity) context;
         user = users.get(position);


        //to set text of user name
        holder.relativeName.setText(user.getFirstName() + " " + user.getLastName());

        //to check internet connection to set image of patient
        if(isInternetPresent==true) {

            if (user.getImageUrl()!="null"){
                Uri uri = Uri.parse(user.getImageUrl());

                holder.relativeImage.setImageURI(uri);
                Log.i("image url--->", String.valueOf(uri));
           }
         if(user.getImageUrl().equals("http://"+Urls.IP_ADDRESS+":"+Urls.PORT_NUMBER+"/WebService/images/")){
                ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.patient_img).build();

            holder.relativeImage.setImageURI(imageRequest.getSourceUri());

            }
        }
        if(isInternetPresent==false){
            ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.patient_img).build();

            holder.relativeImage.setImageURI(imageRequest.getSourceUri());

        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * This class used to hold views
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView relativeName;
        public SimpleDraweeView relativeImage;
        //equal array list of user with array list contain users
        ArrayList <User> user= (ArrayList<User>) users;
        int position;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            relativeName = (TextView) itemView.findViewById(R.id.relative_name);
            relativeImage= (SimpleDraweeView) itemView.findViewById(R.id.relative_image);


            //to open relative activity
            relativeName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    position = getAdapterPosition();
                    Log.i("position i", String.valueOf(position));

                    Intent intent = new Intent(context,RelativeActivity.class);
                    intent.putExtra("phoneNumber",user.get(position).getPhoneNumber());
                    intent.putExtra("relativeFirstName",user.get(position).getFirstName());
                    intent.putExtra("relativeLastName",user.get(position).getLastName());
                    intent.putExtra("imageUrl",user.get(position).getImageUrl());
                    context.startActivity(intent);
                }
            });
        }//end of constructor


    }// end ViewHolder class (inner class)



}
