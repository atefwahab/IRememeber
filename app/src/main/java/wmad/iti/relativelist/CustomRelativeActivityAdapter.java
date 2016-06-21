package wmad.iti.relativelist;

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

import wmad.iti.MutualMemories.MutualMemoryActivity;
import wmad.iti.dto.Relative;
import wmad.iti.irememeber.R;
import wmad.iti.memories.MemoryActivity;

/**
 * Created by Doaa on 5/29/2016.
 */
public class CustomRelativeActivityAdapter extends BaseAdapter {
    public static final int IS_PATIENT = 90900;
    String [] result;
    Context context;
    int [] imageId;
    String relativeFirstName,relativeLastName,relativeEmail;

    private static LayoutInflater inflater=null;
    public CustomRelativeActivityAdapter(RelativeActivity relativeActivity, String[] iconesNameList, int[] iconesImages
    ,String relativeFirstName,String relativeLastName,String relativeEmail) {
        // TODO Auto-generated constructor stub
        result=iconesNameList;
        context=relativeActivity;
        this.relativeFirstName=relativeFirstName;
        this.relativeLastName=relativeLastName;
        this.relativeEmail=relativeEmail;
        Log.i("relativeEmailIntent",relativeEmail+"CustomRelativeActivityAdapter");
        imageId=iconesImages;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
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
                String[] some_array = v.getResources().getStringArray(R.array.relative_activity_arr);

                if (result[position].equals(some_array[0])) {
                    RelativeActivity.instance().call();
                }
                if (result[position].equals(some_array[1])) {
                    RelativeActivity.instance().sendSMSMessage();
                }
                if (result[position].equals(some_array[2])) {
                    Intent intent=new Intent(context,MutualMemoryActivity.class);
                    intent.putExtra("relativeFirstName",relativeFirstName);
                    Log.e("teee",relativeFirstName);
                    intent.putExtra("relativeLastName",relativeLastName);
                    intent.putExtra("relativeEmail",relativeEmail);
                    intent.putExtra("type",IS_PATIENT);

                    context.startActivity(intent);
                }


            }
        });

        return rowView;
    }

}
