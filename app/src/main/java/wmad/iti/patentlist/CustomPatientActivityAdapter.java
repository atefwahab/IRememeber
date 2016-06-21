package wmad.iti.patentlist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;

import wmad.iti.MutualMemories.MutualMemoryActivity;
import wmad.iti.constants.Urls;
import wmad.iti.dto.Status;
import wmad.iti.irememeber.R;
import wmad.iti.model.GsonRequest;
import wmad.iti.model.MySingleton;
import wmad.iti.model.SharedPreferenceManager;

/**
 * Created by Doaa on 5/29/2016.
 */
public class CustomPatientActivityAdapter extends BaseAdapter {
    public final static int IS_RELATIVE = 2345;
    String [] result;
    Context context;
    Activity activity;
    int [] imageId;
    String patientEmail;
    String patientFirstName,patientLastName,relativeMemoriesFromSpecificPatient;
    private static LayoutInflater inflater=null;
    public CustomPatientActivityAdapter(PatientActivity patientActivity, String[] iconesNameList, int[] iconesImages,String patientEmail,
                                        String patientFirstName,String patientLastName,String relativeMemoriesFromSpecificPatient) {
        // TODO Auto-generated constructor stub
        result=iconesNameList;
        context=activity=patientActivity;
        imageId=iconesImages;
        this.patientEmail=patientEmail;
        this.patientFirstName=patientFirstName;
        this.patientLastName=patientLastName;
        this.relativeMemoriesFromSpecificPatient=relativeMemoriesFromSpecificPatient;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView tv;
        ImageView img;
    }
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.custom_grid_view, null);
        holder.tv=(TextView) rowView.findViewById(R.id.textView1);
        holder.img=(ImageView) rowView.findViewById(R.id.imageView1);

        holder.tv.setText(result[position]);
        holder.img.setImageResource(imageId[position]);

        rowView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //        Toast.makeText(context, "You Clicked "+result[position], Toast.LENGTH_LONG).show();
                //Toast.makeText(context, "You Clicked " + imageId[position], Toast.LENGTH_LONG).show();
                String[] some_array = v.getResources().getStringArray(R.array.patient_activity_arr);

                // Location pressed
                if (result[position].equals(some_array[0])) {

                    final ProgressDialog requestLocation = ProgressDialog.show(context,context.getResources().getString(R.string.request_location),context.getResources().getString(R.string.please_wait),false,false);
                    RequestQueue queue = MySingleton.getInstance(v.getContext()).getRequestQueue();

                    HashMap<String,String> headers = new HashMap<String, String>();
                    headers.put("patientEmail",patientEmail);
                    String relativeEmail=SharedPreferenceManager.getEmail(v.getContext());
                    headers.put("relativeEmail", relativeEmail);
                    Log.e("patient Email",patientEmail);
                    Log.e("patient Email",relativeEmail);
                    GsonRequest request = new GsonRequest(Urls.WEB_SERVICE_REQUEST_UPDATE_LOCATION, Request.Method.POST, Status.class, headers,
                            new Response.Listener<Status>() {
                                @Override
                                public void onResponse(Status response) {
                                    Log.e("Location", response.getMessage());
                                    requestLocation.dismiss();

                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("Volley Error",error.toString());
                                    requestLocation.dismiss();
                                    Toast.makeText(context,context.getResources().getString(R.string.an_error_occurred),Toast.LENGTH_LONG).show();

                                }
                            }
                    );

                    queue.add(request);

                }
                // Memories button
                if (result[position].equals(some_array[1])) {

                    Log.e("***********","memories button pressed");
                    Log.e("^^^^^^^", patientEmail);

                    Intent intent=new Intent(context, MutualMemoryActivity.class);
                    intent.putExtra("patientEmail",patientEmail);
                    intent.putExtra("patientFirstName",patientFirstName);
                    intent.putExtra("patientLastName",patientLastName);
                    intent.putExtra("type",IS_RELATIVE);
                    context.startActivity(intent);


                }

            }
        });

        return rowView;
    }

}
