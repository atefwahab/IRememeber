package wmad.iti.requests;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import wmad.iti.constants.Urls;
import wmad.iti.dto.Status;
import wmad.iti.irememeber.R;
import wmad.iti.model.ConnectionDetector;
import wmad.iti.model.GsonRequest;
import wmad.iti.model.MySingleton;
import wmad.iti.model.SharedPreferenceManager;

/**
 * Created by Donia
 * Request Adapter
 */
public class RecyclerAdapterListRequest extends RecyclerView.Adapter<RecyclerAdapterListRequest.MyViewHolder> {

    private LayoutInflater inflater;
    ArrayList<String>images;
    ArrayList<String>names;
    ArrayList<Integer> relation;
    ArrayList<String>emails;
    ConnectionDetector connectionDetector;
    Boolean isInternetPresent;

    public RecyclerAdapterListRequest(Context context, ArrayList<String> imagesArr, ArrayList<String> namesArr, ArrayList<Integer> relationsArr, ArrayList<String> emailsArr) {
        inflater = LayoutInflater.from(context);
        images=imagesArr;
        names=namesArr;
        relation=relationsArr;
        emails=emailsArr;
        connectionDetector = new ConnectionDetector(context);
        isInternetPresent = connectionDetector.isConnectingToInternet();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String name=names.get(position);
        String image=images.get(position);
        Integer relationNum=relation.get(position);
        String email=emails.get(position);
        holder.setData(name, image, relationNum, email, position);

        if(isInternetPresent==true) {

            if (image!="null"){
                Uri uri = Uri.parse(image);
                holder.imgThumb.setImageURI(uri);
                Log.i("image: ",uri+"");
            }
            if(image.equals("http://"+Urls.IP_ADDRESS+":"+Urls.PORT_NUMBER+"/WebService/images/")){
                ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.patient_img).build();

                holder.imgThumb.setImageURI(imageRequest.getSourceUri());
            }
        }
        if(isInternetPresent==false){
            ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.patient_img).build();

            holder.imgThumb.setImageURI(imageRequest.getSourceUri());

        }
    }

    /*
    *author donia
    * remove petiant request
    *
     */
    public void removeItem(int position){

        names.remove(position);
        images.remove(position);
        relation.remove(position);
        emails.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, names.size());
        notifyItemRangeChanged(position,images.size());
        notifyItemRangeChanged(position,relation.size());
        notifyItemRangeChanged(position,emails.size());

//        notifyDataSetChanged();
    }

    class MyViewHolder extends  RecyclerView.ViewHolder{

        TextView title;
        SimpleDraweeView imgThumb;
        Button accept;
        Button decline;
        int position;

        TextView tvDescription;
        public MyViewHolder(View itemView) {
            super(itemView);
            title       = (TextView)  itemView.findViewById(R.id.tvTitle);
            imgThumb    = (SimpleDraweeView) itemView.findViewById(R.id.img_row);
            accept      =(Button) itemView.findViewById(R.id.accept);
            decline     =(Button)itemView.findViewById(R.id.decline);
            tvDescription=(TextView)itemView.findViewById(R.id.tvDescription);

        }

        /*
        * author donia
        * set data in Request list
        * and set el action
        *
         */

        public void setData(String name,String image, final Integer relationId,String email, final int position) {
            this.title.setText(name);
//            Picasso.with(ShowRequestPatient.instance()).load(image).resize(60,60).placeholder(R.drawable.profile).into(this.imgThumb);
           this.tvDescription.setText(getRelation(relationId));
            this.position = position;

//            if(isInternetPresent){
////                Toast.makeText(SendRequest.instance(),image,Toast.LENGTH_LONG).show();
//                Uri uri = Uri.parse(image);
//                this.imgThumb.setImageURI(uri);
//
//            }else{
//                ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.patient_img).build();
//                this.imgThumb.setImageURI(imageRequest.getSourceUri());
//            }

            this.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get email from shared prefrnce
                    final String patientEmail=SharedPreferenceManager.getEmail(ShowRequestPatient.instance());
                    Toast.makeText(ShowRequestPatient.instance(), "email from shared prefrance > " + patientEmail, Toast.LENGTH_LONG).show();

                    RequestQueue queue= MySingleton.getInstance(ShowRequestPatient.instance()).getRequestQueue();

                    HashMap<String,String> header=new HashMap();
                    header.put("patientEmail",patientEmail);
                    header.put("relativeEmail",emails.get(position));
                    header.put("status","true");

                 //   Toast.makeText(ShowRequestPatient.instance(),emails.get(position)+"  "+position, Toast.LENGTH_LONG).show();
                    GsonRequest jsonRequest= new GsonRequest(Urls.WEB_SERVICE_RESPOND_TO_REQUEST_URL, Request.Method.POST, Status.class, header, new Response.Listener<Status>() {
                        @Override
                        public void onResponse(Status response) {


                            int status=response.getStatus();
                            if(status==1){// success
                                removeItem(position);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("onErrorResponse: ",error.getMessage());

                        }
                    });


                    queue.add(jsonRequest);

                }
            });

            this.decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get email from shared prefrance
                    String patientEmail= SharedPreferenceManager.getEmail(ShowRequestPatient.instance());
                    RequestQueue queue= MySingleton.getInstance(ShowRequestPatient.instance()).getRequestQueue();


                    HashMap<String,String> header=new HashMap();
                    header.put("patientEmail",patientEmail);
                    header.put("relativeEmail",emails.get(position));
                    header.put("status","false");
                    GsonRequest jsonRequest= new GsonRequest(Urls.WEB_SERVICE_RESPOND_TO_REQUEST_URL, Request.Method.POST, Status.class, header, new Response.Listener<Status>() {
                        @Override
                        public void onResponse(Status response) {


                                int status=response.getStatus();
                                if(status==1){// success
                                    removeItem(position);
                                }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("onErrorResponse: ",error.getMessage());

                        }
                    });

                    queue.add(jsonRequest);
                }

            });
        }
        /*
        * author donia
        *parameter number of relation
        * return relation name to display in Requests
         */
        public String getRelation(int position){
            String relation = "";
            switch (position) {
                case 20://Aunt (father side)
                    relation="Aunt (father side)";
                    break;
                case 21://Aunt (Mother side)
                    relation="Aunt (Mother side)";
                    break;
                case 7: //Brother
                    relation="Brother";
                    break;
                case 39://Brother-in-law
                    relation="Brother-in-law";
                    break;
                case 25://Cousin (female) - (Mother side)
                    relation="Cousin (female) - (Mother side)";
                    break;
                case 24://Cousin (female)- (Father side)
                    relation="Cousin (female)- (Father side)";
                    break;
                case 22://Cousin (male) -(Father side)
                    relation="Cousin (male) -(Father side)";
                    break;
                case 23://Cousin (male) -(Mother side)
                    relation="Cousin (male) -(Mother side)";
                    break;
                case 6://Daughter
                    relation="Daughter";
                    break;
                case 38://Daughter-in-law
                    relation="Daughter-in-law";
                    break;
                case 3://Father
                    relation="Father";
                    break;
                case 35://Father-in-law
                    relation="Father-in-law";
                    break;
                case 17://Grandchildren
                    relation="Grandchildren";
                    break;
                case 16://Granddaughter
                    relation="Granddaughter";
                    break;
                case 13://Grandfather
                    relation="Grandfather";
                    break;
                case 14://Grandmother
                    relation="Grandmother";
                    break;
                case 15://Grandson
                    relation="Grandson";
                    break;

                case 47://Half-brother
                    relation="Half-brother";
                    break;
                case 48://Half-sister
                    relation="Half-sister";
                    break;
                case 9://Husband
                    relation="Husband";
                    break;
                case 4://mother
                    relation="mother";
                    break;
                case 36://Mother-in-law
                    relation="Mother-in-law";
                    break;
                case 31://Nephew (brother's son)
                    relation="Nephew (brother's son)";
                    break;
                case 32://Nephew (sister's son)
                    relation="Nephew (sister's son)";
                    break;
                case 33://Niece (brother's daughter)
                    relation="Niece (brother's daughter)";
                    break;
                case 34://Niece (brother's daughter)
                    relation="Niece (brother's daughter)";
                    break;
                case 8://Sister
                    relation="Sister";
                    break;
                case 40://Siter-in-law
                    relation="Siter-in-law";
                    break;
                case 5://Son
                    relation="Son";
                    break;
                case 37://Son-in-law
                    relation="Son-in-law";
                    break;
                case 46://Stepbrother
                    relation="Stepbrother";
                    break;
                case 44://Stepdaughter
                    relation="Stepdaughter";
                    break;
                case 41://Stepfather
                    relation="Stepfather";
                    break;
                case 42://Stepmother
                    relation="Stepmother";
                    break;
                case 45://Stepsister
                    relation="Stepsister";
                    break;
                case 43://Stepson
                    relation="Stepson";
                    break;
                case 18://Uncle (Father side)
                    relation="Uncle (Father side)";
                    break;
                case 19://Uncle (Mother side)
                    relation="Uncle (Mother side)";
                    break;
                case 10://wife
                    relation="wife";
                    break;
                default:
                    relation="default";
                    break;
            }
            return relation;
        }


    }

}
