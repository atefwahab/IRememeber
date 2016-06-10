package wmad.iti.requests;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import wmad.iti.constants.Urls;
import wmad.iti.irememeber.R;
import wmad.iti.model.ConnectionDetector;

/**
 * Created by dono on 5/15/2016.
 * custom List
 * search by email
 */
public class CustomAdapterSearchList extends BaseAdapter {
    ArrayList<String> result;
    Context context;
    ArrayList<String> imageId;
    ArrayList<String> emailsList;
    ConnectionDetector connectionDetector;
    Boolean isInternetPresent;
    private static LayoutInflater inflater=null;
    public CustomAdapterSearchList(Context mainActivity, ArrayList<String> prgmNameList, ArrayList<String> emails, ArrayList<String> prgmImages) {
        // TODO Auto-generated constructor stub
        result=prgmNameList;
        context=mainActivity;
        imageId=prgmImages;
        emailsList=emails;
        connectionDetector = new ConnectionDetector(context);
        isInternetPresent = connectionDetector.isConnectingToInternet();

        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return result.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public class Holder
    {
        TextView tv;
        SimpleDraweeView img;
        TextView email;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.patient_search_list, null);
        holder.tv=(TextView) rowView.findViewById(R.id.textView1);
        holder.img=(SimpleDraweeView) rowView.findViewById(R.id.imageView1);
        holder.email=(TextView) rowView.findViewById(R.id.textView2);
        holder.tv.setText(result.get(position));
        holder.email.setText(emailsList.get(position));
        if(isInternetPresent){

            if (imageId.get(position)!="null"){

                Uri uri = Uri.parse(imageId.get(position));
                holder.img.setImageURI(uri);
                Log.i("image: ", uri + "");
            }
            if(imageId.get(position).equals("http://" + Urls.IP_ADDRESS + ":" + Urls.PORT_NUMBER + "/WebService/images/")){
                ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.patient_img).build();

                holder.img.setImageURI(imageRequest.getSourceUri());
            }
        }else{
            ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.profile).build();
            holder.img.setImageURI(imageRequest.getSourceUri());
        }


        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                SendRequest.instance().setEmailInEditText(emailsList.get(position));

            }
        });
        return rowView;
    }



}
