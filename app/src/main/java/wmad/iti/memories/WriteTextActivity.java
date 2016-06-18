package wmad.iti.memories;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import wmad.iti.constants.Urls;
import wmad.iti.dto.Memory;
import wmad.iti.dto.Status;
import wmad.iti.irememeber.R;
import wmad.iti.model.GsonRequest;
import wmad.iti.model.MySingleton;
import wmad.iti.model.SharedPreferenceManager;

public class WriteTextActivity extends AppCompatActivity {
    private Context contextForDialog= null;
    RequestQueue requestQueue;
    GsonRequest gsonRequest;
    Button btnSave;
    EditText textEditMemory;
    Memory memory;
    ImageView takenImg;
    //Camera
    File photoFile = null;
    String mCurrentPhotoPath;
    String imagePath=null;

    private static final int REQUEST_CAMERA = 209;
    private static final int REQUEST_GALLERY = 151;

    int positionIntent;
    Memory memoryIntent;
    ImageLoader mImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        contextForDialog = this;
        setContentView(R.layout.activity_write_text);

        btnSave= (Button) findViewById(R.id.saveButton);
        textEditMemory= (EditText) findViewById(R.id.writeMemoryText);
        takenImg=(ImageView)findViewById(R.id.imagetaken);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("write in  memory ");
        toolbar.setNavigationIcon(R.drawable.navigation_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        positionIntent=getIntent().getIntExtra("position", 1000);
        memoryIntent=getIntent().getParcelableExtra("memory");


  final int flag=getIntent().getIntExtra("flag",0);
        Log.i("flag",flag+ "-->flag");

        switch (flag){
        case 1:
            Log.i("photo flag", flag + "");
            selectImage();
        break;

        case 2:
            Log.i("text flag", flag + "");

        break;
            case 3:
               textEditMemory.setText(memoryIntent.getMemoryText());
                mImageLoader = MySingleton.getInstance(getApplicationContext()).getImageLoader();
                Log.i("URL",memoryIntent.getImageUrl());
                mImageLoader.get(memoryIntent.getImageUrl(), ImageLoader.getImageListener(takenImg, 0, 0));
                break;

}

    btnSave.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Case photo
            if(flag==1&&textEditMemory.getText().toString().length()==0) {
                uploadPicture();
                Intent intent = new Intent(getApplicationContext(), MemoryActivity.class);
                startActivity(intent);
            }
           if(flag==1&&textEditMemory.getText().toString().length() < 10&&textEditMemory.getText().toString().length()!=0){
               uploadPicture();
             Intent intent = new Intent(getApplicationContext(), MemoryActivity.class);
             startActivity(intent);
            }
            if(flag==1&&textEditMemory.getText().toString().length() > 10){
           //     Toast.makeText(getApplicationContext(),"You enter > 10",Toast.LENGTH_LONG).show ();
                  showDialog("You enter > 10");
            }

            //Case text
            if(flag==2){
                if(textEditMemory.getText().toString().length()==0){
                    showDialog("you must enter memory");
                }
                if (textEditMemory.getText().toString().length() < 10) {
                    if (textEditMemory.getText().length() != 0) {
                        saveMemoryTextVolleyRequest();
                        Intent intent = new Intent(getApplicationContext(), MemoryActivity.class);
                        startActivity(intent);
                    }


                }
                if(textEditMemory.getText().toString().length()>10){
                  // Toast.makeText(getApplicationContext(),"You  enter >10",Toast.LENGTH_LONG).show ();
                    showDialog("You enter > 10");
                }


            }//end if text

            //case edit
            if(flag==3&&textEditMemory.getText().toString().length()==0){
             // Toast.makeText(getApplicationContext(),"You  must enter memory",Toast.LENGTH_LONG).show ();
                showDialog("You must enter memory");
            }

            if(flag==3&&textEditMemory.getText().toString().length()<10) {
                if (flag == 3 && textEditMemory.getText().toString().length() != 0) {
                    editTextMemory(positionIntent);
                    Intent intent = new Intent(getApplicationContext(), MemoryActivity.class);
                    startActivity(intent);
                }
            }
            else{
                showDialog("You enter > 10");
            }
      }
    });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void saveMemoryTextVolleyRequest(){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String currentDateandTime = simpleDateFormat.format(new Date());

        Log.i("enter save memory", "memory");
        HashMap<String, String> header = new HashMap<>();
        header.put("patientEmail",SharedPreferenceManager.getUser(getApplicationContext()).getEmail());
        header.put("relativeEmail", null);
        header.put("text", textEditMemory.getText().toString());
        header.put("date",currentDateandTime);

        memory=new Memory();
        memory.setMemoryText(textEditMemory.getText().toString());
        memory.setDateTime(currentDateandTime);
        requestQueue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
        //Creating a json request to get patients
        gsonRequest = new GsonRequest(Urls.SAVE_MEMORY_TEXT_URL, Request.Method.POST, Status.class, header, new Response.Listener<Status>() {

            @Override
            public void onResponse(Status status) {

                status.getMessage();
                Log.i("on Response ",status.getMessage());
                Toast.makeText(getApplicationContext(), status.getMessage(), Toast.LENGTH_LONG).show();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i("onErrorResponse: ", volleyError.getMessage());
            }
        });


        //Adding request to the queue
        requestQueue.add(gsonRequest);

    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * this method is used to select image using AlertDialoge
     */
    private void selectImage() {
        final CharSequence[] items = {"Open Camera", "Choose from Gallery"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    // Camera Case
                    case 0:
                        //
                        Log.i("*****", "first choice");
                        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                        if (photoFile.exists()) {

                            Log.i("*****", "file exists");
                            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

                            startActivityForResult(takePhotoIntent, REQUEST_CAMERA);
                        }


                        //

                        break;

                    // Gallery Case
                    case 1:

                        Intent galleryIntent = new Intent();
                        galleryIntent.setType("image/*");
                        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), REQUEST_GALLERY);
                        break;
                }

            }
        });

        builder.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            // Open Camera Case
            case REQUEST_CAMERA:

                if (resultCode == RESULT_OK) {


                    setImage();
                  // uploadPicture();

                }
                break;


            // gallery case
            case REQUEST_GALLERY:
                if (resultCode == RESULT_OK) {
                    onSelectFromGallery(data);
                   // uploadPicture();
                }
                break;
        }
    }

    private File createImageFile() throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setImage() {

        // Get the dimensions of the View
        int targetW = takenImg.getWidth();
        int targetH = takenImg.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFile.getPath(), bmOptions);

        int photoW = takenImg.getWidth();
        int photoH = takenImg.getHeight();

        // Determine how much to scale down the image
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }
        // int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        imagePath = photoFile.getAbsolutePath();
        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getPath(), bmOptions);
        takenImg.setImageBitmap(bitmap);

    }
    private void onSelectFromGallery(Intent data) {

        Bitmap image = null;

        if (data != null) {
            Log.i("******* ", data.getData().toString());
            try {
                // to know path of image
                imagePath = getRealPathFromURI(data.getData());
                image = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                takenImg.setImageBitmap(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * This method used to upload image to server
     */
    private void uploadPicture() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        final String currentDateandTime = simpleDateFormat.format(new Date());
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
        Ion.with(getApplicationContext())
                .load(Urls.MEMORY_IMAGE_UPLOAD_URL).setLogging("UPLOAD LOGS", Log.DEBUG)
                .setMultipartParameter("date",currentDateandTime)
                .setMultipartParameter("text", textEditMemory.getText().toString())
                .setMultipartParameter("patientEmail", SharedPreferenceManager.getUser(getApplicationContext()).getEmail())
                .setMultipartParameter("relativeEmail", "doaa@gmail.com")
                .setMultipartFile("file", "application/zip", new File(imagePath)).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                if (e != null) {
                    e.printStackTrace();
                }
                loading.dismiss();
                Log.i("completed", "completed");

                if (result != null) {

                    Log.i("success", result.get("message").getAsString());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String currentDateandTime = simpleDateFormat.format(new Date());
                    loading.dismiss();

                }
            }
        });
    }
    private void callback(){

        Log.i("CallBack", "i was called");
    }
    /**
     * to get the Real Path of uri
     * @param contentURI
     * @return
     */
    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
    /**
     * This method is used to make patient edit his memories
     * @param position
     */
    private void editTextMemory(int position) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String currentDateandTime = simpleDateFormat.format(new Date());
       // final Memory memory=memories.get(position);
        int memoryId=memoryIntent.getMemoryId();
        Log.i("memoryID",memoryId+"");
        HashMap<String, String> header = new HashMap<>();
        header.put("memoryId",String.valueOf(memoryId));
        header.put("date",currentDateandTime);
        header.put("text", textEditMemory.getText().toString());
        requestQueue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();

        gsonRequest = new GsonRequest(Urls.EDIT_MEMORY_URL, Request.Method.POST,Status.class, header, new Response.Listener<Status>() {

            @Override
            public void onResponse(Status status) {

                Toast.makeText(getApplicationContext(), status.getMessage(),Toast.LENGTH_LONG).show();


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

    /**
     * This method is used to show dialog
     * @param message
     */

    public void showDialog(String message){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(WriteTextActivity.this);

                    alertDialog.setTitle("Warning");
                    alertDialog.setMessage(message);
                    alertDialog.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), MemoryActivity.class);
                            startActivity(intent);
                        }
                    });


                    alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();
                        }
                    });

                    alertDialog.show();

    }

}