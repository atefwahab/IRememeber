package wmad.iti.relativelist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import wmad.iti.patentlist.PatientHome;

/**
 * Created by Doaa on 5/25/2016.
 */
public class RelativeAdapter extends RecyclerView.Adapter<RelativeAdapter.ViewHolder> {

    private Context context;
    List<User> users;
    static int count=0;
    GsonRequest gsonRequest;
    RequestQueue requestQueue;
    ConnectionDetector connectionDetector;
    Boolean isInternetPresent;
    User user;

    public RelativeAdapter(List<User> users, Context context){
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
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Activity activity = (Activity) context;
         user = users.get(position);


        //to set text of user name
        holder.relativeName.setText(user.getFirstName() + " " + user.getLastName());
        Log.i("onBindViewHolder: ",user.getEmail());
        boolean checked=checkTrusted(user.getEmail());


        holder.starImage.setChecked(checked);

        holder.starImage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton v, boolean isChecked) {
                // to check intenet connection to delete patient
                if (isInternetPresent) {
                    if (isChecked) {
                        if(count==0) {
                            count++;
                            holder.starImage.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.yellowstar));
                            setTrusted(SharedPreferenceManager.getEmail(RelativesListHome.instance().getApplicationContext()),users.get(position).getEmail());
                            Toast.makeText(context, "add to favorite", Toast.LENGTH_LONG).show();
                        }
                    } else{
                        if(count>0){
                            holder.starImage.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.star));
                            count--;
                            removeTrusted(users.get(position).getEmail());
                            Toast.makeText(context,"remove from favorite",Toast.LENGTH_LONG).show();
                        }
//                        holder.starImage.setBackground(ContextCompat.getDrawable(context,R.drawable.star));
                    }

                } if(isInternetPresent==false){

                    Snackbar snackbar = Snackbar.make(v, context.getResources().getString(R.string.NoConnection), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });

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
        public ToggleButton starImage;

        //equal array list of user with array list contain users
        ArrayList <User> user= (ArrayList<User>) users;
        int position;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            relativeName = (TextView) itemView.findViewById(R.id.relative_name);
            relativeImage= (SimpleDraweeView) itemView.findViewById(R.id.relative_image);
            starImage=(ToggleButton)itemView.findViewById(R.id.star_image);

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


    public boolean checkTrusted(String relativeEmail){
        Log.i("checkTrusted: ",relativeEmail);
        final boolean[] checked = {false};
        RequestQueue queue= MySingleton.getInstance(RelativesListHome.instance().getApplicationContext()).getRequestQueue();

        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("patientemail", SharedPreferenceManager.getEmail(RelativesListHome.instance().getApplicationContext()));
        hashMap.put("relativeemail",relativeEmail);
        GsonRequest jsonRequest= new GsonRequest(Urls.WEB_SERVICE_CHECK_TRUSTED_URL, Request.Method.POST, Status.class,hashMap, new Response.Listener<Status>() {
            @Override
            public void onResponse(Status response) {
                checked[0] =false;
                if(response.getStatus()==1){
                    checked[0] =true;
                }else{
                    checked[0] =false;
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("Response error", error.toString());
            }
        });
        queue.add(jsonRequest);


        return checked[0];
    }


    public void setTrusted(String patientEmail,String relativeEmail){
        Log.i("setTrusted: ","patient email > "+patientEmail+" relative email >  "+relativeEmail);
        RequestQueue queue= MySingleton.getInstance(RelativesListHome.instance().getApplicationContext()).getRequestQueue();

        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("patientemail", patientEmail);
        hashMap.put("relativeemail",relativeEmail);
        GsonRequest jsonRequest= new GsonRequest(Urls.WEB_SERVICE_SET_TRUSTED_URL, Request.Method.POST, Status.class,hashMap, new Response.Listener<Status>() {
            @Override
            public void onResponse(Status response) {
                Log.i("set trusted: ","done");
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("Response error", error.toString());
            }
        });
        queue.add(jsonRequest);



    }


    public void removeTrusted(String relativeEmail){
        Log.i("removeTrusted: ","relative email > "+relativeEmail);
        RequestQueue queue= MySingleton.getInstance(RelativesListHome.instance().getApplicationContext()).getRequestQueue();
        String patientEmail=SharedPreferenceManager.getEmail(RelativesListHome.instance().getApplicationContext());
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("patientemail", patientEmail);
        hashMap.put("relativeemail",relativeEmail);
        GsonRequest jsonRequest= new GsonRequest(Urls.WEB_SERVICE_REMOVE_TRUSTED_URL, Request.Method.POST, Status.class,hashMap, new Response.Listener<Status>() {
            @Override
            public void onResponse(Status response) {
                Log.i("remove trusted: ", "done");

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("Response error", error.toString());
            }
        });
        queue.add(jsonRequest);

    }


}
