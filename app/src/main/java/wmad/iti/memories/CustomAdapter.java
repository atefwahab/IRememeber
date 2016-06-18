package wmad.iti.memories;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import wmad.iti.constants.Urls;
import wmad.iti.dto.Memory;
import wmad.iti.dto.Status;
import wmad.iti.dto.User;
import wmad.iti.irememeber.R;
import wmad.iti.model.GsonRequest;
import wmad.iti.model.MySingleton;
import wmad.iti.model.SharedPreferenceManager;

/**
 * Created by dr-ali on 01/06/2016.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";
    Context context;
    ImageLoader mImageLoader;
    EditText editmemory;
    ArrayList<Memory> memories;
    Memory memory;
    RequestQueue requestQueue;
    GsonRequest gsonRequest;
    int positionofMemory;
    User user;

    public CustomAdapter(ArrayList<Memory> memories, Context context) {

        this.memories=memories;
        this.context=context;


    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }

    public class MemoryViewHolder extends ViewHolder {
        TextView name, date, arrow,postTxt;
        ImageView profileImg, postImg,imageFirstCardProfile;
        private PopupMenu popupMenu;
        int position;
        LinearLayout layoutTextMemory,layoutPhotoMemory;
        public MemoryViewHolder(View v) {
            super(v);

            name=(TextView)v.findViewById(R.id.name);
            date=(TextView)v.findViewById(R.id.date);
            profileImg=(ImageView)v.findViewById(R.id.first);
            postImg=(ImageView)v.findViewById(R.id.imagePost);
            arrow=(TextView)v.findViewById(R.id.draw);
            postTxt=(TextView)v.findViewById(R.id.txt1);
            layoutPhotoMemory= (LinearLayout) v.findViewById(R.id.layoutPhotoMemory);
            layoutTextMemory= (LinearLayout) v.findViewById(R.id.layoutTextMemory);
            imageFirstCardProfile= (ImageView) v.findViewById(R.id.imagep);
          arrow.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                //  Toast.makeText(v.getContext().getApplicationContext(), "arrow click", Toast.LENGTH_SHORT).show();
                  popupMenu = new PopupMenu(v.getContext().getApplicationContext(), v);
                  popupMenu.setOnDismissListener(new OnDismissListener());
                  popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener());
                  popupMenu.inflate(R.menu.popup);
                  popupMenu.show();

              }

               class OnDismissListener implements PopupMenu.OnDismissListener {

                  @Override
                  public void onDismiss(PopupMenu menu) {
                      // TODO Auto-generated method stub

                  }

              }

              /**
               * This method is used to get position of memory
               * @return int
               */

              public int getPositionOfMemory(){

                  position = getAdapterPosition();
                  return position;
              }

              class OnMenuItemClickListener implements
                      PopupMenu.OnMenuItemClickListener {
                  MemoryViewHolder videoViewHolder;

                  @Override
                  public boolean onMenuItemClick(MenuItem item) {
                      // TODO Auto-generated method stub
                    positionofMemory =getPositionOfMemory();
                      switch (item.getItemId()) {
                          case R.id.edit:

                              int flag=3;
                              Memory memoryIntent = memories.get(position);
                              Intent intent=new Intent(context,WriteTextActivity.class);

                              intent.putExtra("memory",memoryIntent);
                              intent.putExtra("flag",flag);
                              intent.putExtra("position",positionofMemory);
                              context.startActivity(intent);
                              return true;


                          case R.id.delete:
                              AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                              alertDialogBuilder.setMessage("Are You sure you want to delete this memory?").setTitle("Delete Memory");

                              alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                  @Override
                                  public void onClick(DialogInterface arg0, int arg1) {
                                      removeItem(positionofMemory);
                                  }
                              });

                              alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                  @Override
                                  public void onClick(DialogInterface dialog, int which) {
                                      dialog.dismiss();
                                  }
                              });

                              AlertDialog alertDialog = alertDialogBuilder.create();
                              alertDialog.show();

                              return true;
                      }
                      return false;
                  }

              }

          });


        }
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.third_card, viewGroup, false);

            return new MemoryViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.i("size", ">> " + memories.size());
        memory = memories.get(position);
        Log.i("dataImage: ",memory.getImageUrl());
        Log.i("dataText: ",memory.getMemoryText());
        final MemoryViewHolder holder = (MemoryViewHolder) viewHolder;
        //text
        if (memory.getImageUrl().equals(Urls.IMAGE_MEMORY_NULL)&&!memory.getMemoryText().equals("")) {
            holder.postTxt.setVisibility(View.VISIBLE);
            holder.postImg.setVisibility(View.GONE);
            holder.layoutPhotoMemory.setVisibility(View.GONE);
            holder.layoutTextMemory.setVisibility(View.VISIBLE);
            Log.i("urlmemory",memory.getImageUrl()+" memory");
            holder.postTxt.setText(memory.getMemoryText());
            holder.date.setText(memory.getDateTime());
        }else
        //text & photo
        if(!memory.getImageUrl().equals(Urls.IMAGE_MEMORY_NULL)&&!memory.getMemoryText().equals("")){
            holder.layoutTextMemory.setVisibility(View.VISIBLE);
            holder.layoutPhotoMemory.setVisibility(View.VISIBLE);
            holder.postTxt.setVisibility(View.VISIBLE);
            holder.postImg.setVisibility(View.VISIBLE);
            holder.date.setText(memory.getDateTime());
            holder.postTxt.setText(memory.getMemoryText());
           // Log.i("urlmemory2", memory.getImageUrl() + " memory");
            mImageLoader = MySingleton.getInstance(context).getImageLoader();
            mImageLoader.get(memory.getImageUrl(), ImageLoader.getImageListener(holder.postImg, 0, 0));
        }else
        //photo
        if(!memory.getImageUrl().equals(Urls.IMAGE_MEMORY_NULL)&&memory.getMemoryText().equals("")){
            holder.layoutPhotoMemory.setVisibility(View.VISIBLE);
            holder.postImg.setVisibility(View.VISIBLE);
            holder.layoutTextMemory.setVisibility(View.GONE);
            holder.postTxt.setVisibility(View.GONE);
            holder.date.setText(memory.getDateTime());
            mImageLoader = MySingleton.getInstance(context).getImageLoader();
            mImageLoader.get(memory.getImageUrl(), ImageLoader.getImageListener(holder.postImg, 0, 0));
            Log.i("urlmemory", memory.getImageUrl() + " memory");
        }
        holder.name.setText(SharedPreferenceManager.getUser(context).getFirstName() + " " + SharedPreferenceManager.getUser(context).getLastName());
        String imageUrl=SharedPreferenceManager.getUser(context).getImageUrl();
        mImageLoader = MySingleton.getInstance(context).getImageLoader();
        mImageLoader.get(imageUrl, ImageLoader.getImageListener(holder.profileImg, 0, 0));
     //   mImageLoader = MySingleton.getInstance(context).getImageLoader();
       // mImageLoader.get(imageUrl, ImageLoader.getImageListener(holder.imageFirstCardProfile, 0, 0));

        //Log.i("imageURLL",imageUrl);
    }

    /**
     * This method is used to remove patient row from patients list
     * @param position
     */
    public void removeItem(int position) {
        Log.i("********************", position + "");
        removeMemory(position);
        memories.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, memories.size());
        Memory[] memoryArr=new Memory[memories.size()];
        for(int i=0;i<memories.size();i++){
            memoryArr[i]=memories.get(i);
        }

    }

    /**
     *This method is used to remove patient from database
     */
    private void removeMemory(int position) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        final String currentDateandTime = simpleDateFormat.format(new Date());
        Memory memory=memories.get(position);
        int memoryId=memory.getMemoryId();

            requestQueue=  MySingleton.getInstance(context).getRequestQueue();
            //Request to remove memory from database
            HashMap<String, String> header = new HashMap<>();
            header.put("memoryId", String.valueOf(memoryId));
            header.put("date",currentDateandTime);
            gsonRequest = new GsonRequest(Urls.DELETE_MEMORY_URL, Request.Method.POST,Status.class,header,new  Response.Listener<Status>() {

                @Override
                public void onResponse(Status status) {
                    if (status.getStatus()==1) {
                        Toast.makeText(context, "REMOVED SUCCESSFULLY", Toast.LENGTH_LONG).show();
                    }
                    if(status.getStatus()==0) {
                        Toast.makeText(context,"FAILED", Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    Toast.makeText(context, "error Response => " + volleyError.getMessage(), Toast.LENGTH_LONG).show();


                }
            });

            requestQueue.add(gsonRequest);
        }

    /**
     * This method is used to make patient edit his memories
     * @param position
     */
    private void editTextMemory(int position) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String currentDateandTime = simpleDateFormat.format(new Date());
        final Memory memory=memories.get(position);
        int memoryId=memory.getMemoryId();
        HashMap<String, String> header = new HashMap<>();
        header.put("memoryId",String.valueOf(memoryId));
        header.put("date",currentDateandTime);
        header.put("text", editmemory.getText().toString());
        requestQueue = MySingleton.getInstance(context).getRequestQueue();

        gsonRequest = new GsonRequest(Urls.EDIT_MEMORY_URL, Request.Method.POST,Status.class, header, new Response.Listener<Status>() {

            @Override
            public void onResponse(Status status) {

                Toast.makeText(context, status.getMessage(),Toast.LENGTH_LONG).show();


            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                // Toast.makeText(RelativesListHome.this, "Error Response of get relatives: " + volleyError.getMessage(), Toast.LENGTH_LONG).show();
                Log.i("onErrorResponse: ", volleyError.getMessage());


            }
        });


        //Adding request to the queue
        requestQueue.add(gsonRequest);

    }


    @Override
    public int getItemCount() {

       return memories.size();
    }


}
