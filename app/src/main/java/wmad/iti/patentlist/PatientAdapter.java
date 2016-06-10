package  wmad.iti.patentlist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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

/**
 * Created by Doaa on 5/19/2016.
 */
public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.ViewHolder> {
    private Context context;
    List<User> users;

    GsonRequest gsonRequest;
    RequestQueue requestQueue;
    ConnectionDetector connectionDetector;
    Boolean isInternetPresent;
    User user;

    public PatientAdapter(List<User> users, Context context){
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
                .inflate(R.layout.patient_list_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Activity activity = (Activity) context;
        user = users.get(position);
        holder.setData(user, position);

        //Listener of delete button
        holder.deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // to check intenet connection to delete patient
                if (isInternetPresent) {

                    ///////Dialog to make sure he want to delete patient
                    ////////

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setMessage(context.getResources().getString(R.string.deleteMessage));

                    alertDialogBuilder.setPositiveButton(context.getResources().getString(R.string.Ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                            removeItem(position);
                            Log.i("****************", String.valueOf(position));
                        }
                    });

                    alertDialogBuilder.setNegativeButton(context.getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    /////////////////////////////////////

                } if(isInternetPresent==false){

                    Snackbar snackbar = Snackbar.make(v, context.getResources().getString(R.string.NoConnection), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
        //to set text of user name
        holder.patientName.setText(user.getFirstName() + " " + user.getLastName());

        //to check internet connection to set image of patient
        if(isInternetPresent==true) {

           if (user.getImageUrl()!="null"){
                Uri uri = Uri.parse(user.getImageUrl());

                holder.patientImage.setImageURI(uri);
                Log.i("patient url **********",user.getImageUrl());
            }
            if(user.getImageUrl().equals("http://"+Urls.IP_ADDRESS+":"+Urls.PORT_NUMBER+"/WebService/images/")){
                ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.patient_img).build();

                holder.patientImage.setImageURI(imageRequest.getSourceUri());
            }
        }
        if(isInternetPresent==false){
            ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.patient_img).build();

            holder.patientImage.setImageURI(imageRequest.getSourceUri());

        }

    }

    public String getPatientFirstName(){
        return user.getFirstName();
    }
    public String getPatientLastName(){
        return user.getLastName();
    }


    /**
     * This method is used to remove patient row from patients list
     * @param position
     */
    public void removeItem(int position) {
        Log.i("********************", position + "");
        removePatient(position);
        users.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, users.size());
        User[] userArr=new User[users.size()];
        for(int i=0;i<users.size();i++){
            userArr[i]=users.get(i);
        }
        SharedPreferenceManager.savePatients(context, userArr);
    }

    /**
     *This method is used to remove patient from database
     */
    private void removePatient(int position) {

        String patientEmail=null;
        //to get patient email that saved on shared prefrence to pass it as a parameter to delete patient
        ArrayList<User> patients=SharedPreferenceManager.getPatients(context);

        User user=patients.get(position);
        patientEmail=user.getEmail();

        //to check internet connection to make request to webservice to delete patient
        if(isInternetPresent==true){
            requestQueue=  MySingleton.getInstance(context).getRequestQueue();
            //Request to remove patient from database
            HashMap<String, String> header = new HashMap<>();
            header.put("patientEmail",patientEmail);
            header.put("relativeEmail", SharedPreferenceManager.getEmail(context));
            gsonRequest = new GsonRequest(Urls.WEB_SERVICE_REMOVE_PATIENT_URL, Request.Method.POST,Status.class,header,new  Response.Listener<Status>() {

                @Override
                public void onResponse(Status status) {
                    if (status.getStatus()==1) {
                        Toast.makeText(context, context.getResources().getString(R.string.RemovedSuccessfully), Toast.LENGTH_LONG).show();
                    }
                    if(status.getStatus()==0) {
                        Toast.makeText(context, context.getResources().getString(R.string.FailedRemoved), Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    //        Toast.makeText(context, "error Response => " + volleyError.getMessage(), Toast.LENGTH_LONG).show();


                }
            });

            requestQueue.add(gsonRequest);
        }

    }

    @Override
    public int getItemCount() {
        return users.size();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView patientName;
        public ImageView deleteImage;
        public SimpleDraweeView patientImage;
        int position;
        User user;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            patientName = (TextView) itemView.findViewById(R.id.patien_name);
            patientImage= (SimpleDraweeView) itemView.findViewById(R.id.patien_image);
            deleteImage= (ImageView)itemView.findViewById(R.id.delete_image);

            //to open patient activity
            patientName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, PatientActivity.class);
                    String patientFirstName=getPatientFirstName();
                    String patientLastName=getPatientLastName();

                    intent.putExtra("patientFirstName",patientFirstName);
                    intent.putExtra("patientLastName",patientLastName);
                    context.startActivity(intent);

                }
            });
        }

        /**
         * This method is used to initialize current obj and position from onBindViewHolder method
         * @param currentObj
         * @param position
         */
        public void setData(User currentObj, int position) {

            this.position = position;
            this.user = currentObj;

        }

        public int getPositionOfPatient(){

            position = getAdapterPosition();
            return position;
        }

    }
}
