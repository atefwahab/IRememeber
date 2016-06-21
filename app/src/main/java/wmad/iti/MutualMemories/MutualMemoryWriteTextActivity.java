package wmad.iti.MutualMemories;

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
import android.support.design.widget.TextInputLayout;
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
import wmad.iti.irememeber.PatientHomeActivity;
import wmad.iti.irememeber.R;
import wmad.iti.irememeber.RelativeHomeActivity;
import wmad.iti.memories.MemoryActivity;
import wmad.iti.model.ConnectionDetector;
import wmad.iti.model.GsonRequest;
import wmad.iti.model.MySingleton;
import wmad.iti.model.SharedPreferenceManager;
import wmad.iti.patentlist.CustomPatientActivityAdapter;
import wmad.iti.relativelist.CustomRelativeActivityAdapter;
import wmad.iti.util.Validator;

public class MutualMemoryWriteTextActivity extends AppCompatActivity {
    private Context contextForDialog = null;
    RequestQueue requestQueue;
    GsonRequest gsonRequest;
    // Button btnSave;
    EditText textEditMemory;
    Memory memory;
    ImageView takenImg;
    Button postButton;
    //Camera
    File photoFile = null;
    String mCurrentPhotoPath;
    String imagePath = null;
    // input Layout
   // TextInputLayout textInputLayout;

    private static final int REQUEST_CAMERA = 209;
    private static final int REQUEST_GALLERY = 151;

    int positionIntent;
    Memory memoryIntent;
    ImageLoader mImageLoader;
    String patientEmailIntent, relativeEmailIntent;
    ConnectionDetector connectionDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        contextForDialog = this;
        setContentView(R.layout.activity_write_text);


        textEditMemory = (EditText) findViewById(R.id.writeMemoryText);
      //  textInputLayout = (TextInputLayout) findViewById(R.id.inputLayoutWriteMemory);
        takenImg = (ImageView) findViewById(R.id.imagetaken);
        Toolbar toolbar = (Toolbar) findViewById(R.id.memory);
        setSupportActionBar(toolbar);
        postButton = (Button) toolbar.findViewById(R.id.postbutton);

        getSupportActionBar().setTitle("write in memory ");
        toolbar.setNavigationIcon(R.drawable.navigation_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        positionIntent = getIntent().getIntExtra("position", 1000);
        memoryIntent = getIntent().getParcelableExtra("memory");
        patientEmailIntent = getIntent().getStringExtra("patientEmail");
        relativeEmailIntent = getIntent().getStringExtra("relativeEmail");
        Log.i("relativeinWrite",relativeEmailIntent+" write");

        final int flag = getIntent().getIntExtra("flag", 0);


        Log.i("flag", flag + "-->flag");

        switch (flag) {
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
                Log.i("URL", memoryIntent.getImageUrl());
                mImageLoader.get(memoryIntent.getImageUrl(), ImageLoader.getImageListener(takenImg, 0, 0));
                break;

        }

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Case photo
                if (flag == 1) {
                    if (textEditMemory.getText().length() < 400) {
                        uploadPicture();
                    }
                    if ( textEditMemory.getText().length() > 400) {
                        showDialog(getResources().getString(R.string.maxChar));
                    }
                }

                //Case text
                if(flag==2) {
                    if (textEditMemory.getText().length() != 0 && textEditMemory.getText().length() < 400) {
                        saveMemoryTextVolleyRequest(findViewById(android.R.id.content));

                    }
                    if ( textEditMemory.getText().length() > 400) {
                        showDialog(getResources().getString(R.string.maxChar));
                    }
                    if ( textEditMemory.getText().length() == 0) {

                        showDialog(getResources().getString(R.string.nullText));
                    }

                }
                //case edit
                if (flag == 3) {
                    if (textEditMemory.getText().length() != 0 && textEditMemory.getText().length() < 400) {
                        editTextMemory(positionIntent);

                    }
                    if ( textEditMemory.getText().length() > 400) {
                        showDialog(getResources().getString(R.string.maxChar));
                    }
                    if ( textEditMemory.getText().length() == 0) {

                        showDialog(getResources().getString(R.string.nullText));
                    }


                }


            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void saveMemoryTextVolleyRequest(View view) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String currentDateandTime = simpleDateFormat.format(new Date());

        HashMap<String, String> header = new HashMap<>();
        //to test connectivity of internet
        connectionDetector = new ConnectionDetector(getApplicationContext());
        Boolean isInternetPresent = connectionDetector.isConnectingToInternet();
        //if there is internet connection
        if (isInternetPresent == true) {
            Log.i("type here", String.valueOf(getIntent().getIntExtra("type", 0))+" type");
            //to check if patient enter edit memories at specific relative to get mutual memories
            if (getIntent().getIntExtra("type", 0) == CustomRelativeActivityAdapter.IS_PATIENT) {
                Log.i("type here2", String.valueOf(getIntent().getIntExtra("type", 0)));
                header.put("patientEmail", SharedPreferenceManager.getUser(getApplicationContext()).getEmail());
                header.put("relativeEmail", relativeEmailIntent);

            }
            //  to check if relative enter edit memories at specific patient to get mutual memories
            if (getIntent().getIntExtra("type", 0) == CustomPatientActivityAdapter.IS_RELATIVE) {
                header.put("patientEmail", patientEmailIntent);
                header.put("relativeEmail", SharedPreferenceManager.getUser(getApplicationContext()).getEmail());
            }
            header.put("text", textEditMemory.getText().toString());
            header.put("date", currentDateandTime);

            memory = new Memory();
            memory.setMemoryText(textEditMemory.getText().toString());
            memory.setDateTime(currentDateandTime);
            requestQueue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
            //Creating a json request to get patients
            gsonRequest = new GsonRequest(Urls.SAVE_MEMORY_TEXT_URL, Request.Method.POST, Status.class, header, new Response.Listener<Status>() {

                @Override
                public void onResponse(Status status) {

                    status.getMessage();
                    Log.i("on Response ", status.getMessage());
                //    Toast.makeText(getApplicationContext(), status.getMessage(), Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.postMemory), Toast.LENGTH_LONG).show();

                    if (getIntent().getIntExtra("type", 0) == CustomPatientActivityAdapter.IS_RELATIVE) {
                        goToRelativeHomeActivity();
                    }
                    if (getIntent().getIntExtra("type", 0) == CustomRelativeActivityAdapter.IS_PATIENT) {
                        goToPatientHomeActivity();
                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.i("onErrorResponse: ", volleyError.toString());
                }
            });


            //Adding request to the queue
            requestQueue.add(gsonRequest);


        } else {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.no_internt_msg), Toast.LENGTH_LONG).show();
        }

    }



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
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
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
        //to test connectivity of internet
        connectionDetector = new ConnectionDetector(getApplicationContext());
        Boolean isInternetPresent = connectionDetector.isConnectingToInternet();
        //if there is internet connection
        if (isInternetPresent == true) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            final String currentDateandTime = simpleDateFormat.format(new Date());
            final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
            Log.i("relativeFunc", relativeEmailIntent + " func");
            if (getIntent().getIntExtra("type", 0) == CustomRelativeActivityAdapter.IS_PATIENT) {
                Ion.with(getApplicationContext())
                        .load(Urls.MEMORY_IMAGE_UPLOAD_URL).setLogging("UPLOAD LOGS", Log.DEBUG)
                        .setMultipartParameter("date", currentDateandTime)
                        .setMultipartParameter("text", textEditMemory.getText().toString())
                        .setMultipartParameter("patientEmail", SharedPreferenceManager.getUser(getApplicationContext()).getEmail())
                        .setMultipartParameter("relativeEmail", relativeEmailIntent)
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
                            if (getIntent().getIntExtra("type", 0) == CustomPatientActivityAdapter.IS_RELATIVE) {
                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.imageUploaded),Toast.LENGTH_LONG).show();
                                goToRelativeHomeActivity();
                            }
                            if (getIntent().getIntExtra("type", 0) == CustomRelativeActivityAdapter.IS_PATIENT) {
                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.imageUploaded),Toast.LENGTH_LONG).show();
                                goToPatientHomeActivity();
                            }

                        }
                    }
                });
            }
            if (getIntent().getIntExtra("type", 0) == CustomPatientActivityAdapter.IS_RELATIVE) {
                Ion.with(getApplicationContext())
                        .load(Urls.MEMORY_IMAGE_UPLOAD_URL).setLogging("UPLOAD LOGS", Log.DEBUG)
                        .setMultipartParameter("date", currentDateandTime)
                        .setMultipartParameter("text", textEditMemory.getText().toString())
                        .setMultipartParameter("patientEmail", patientEmailIntent)
                        .setMultipartParameter("relativeEmail", SharedPreferenceManager.getUser(getApplicationContext()).getEmail())
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
                            if (getIntent().getIntExtra("type", 0) == CustomPatientActivityAdapter.IS_RELATIVE) {
                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.imageUploaded),Toast.LENGTH_LONG).show();
                                goToRelativeHomeActivity();
                            }
                            if (getIntent().getIntExtra("type", 0) == CustomRelativeActivityAdapter.IS_PATIENT) {
                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.imageUploaded),Toast.LENGTH_LONG).show();
                                goToPatientHomeActivity();
                            }

                        }
                    }
                });

            }
        }//end if--> there is internet
        else{
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.no_internt_msg),Toast.LENGTH_LONG).show();
        }
    }

    private void callback() {

        Log.i("CallBack", "i was called");
    }

    /**
     * to get the Real Path of uri
     *
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
     *
     * @param position
     */
    private void editTextMemory(int position) {
        //to test connectivity of internet
        connectionDetector = new ConnectionDetector(getApplicationContext());
        Boolean isInternetPresent = connectionDetector.isConnectingToInternet();
        //if there is internet connection
        if (isInternetPresent == true) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String currentDateandTime = simpleDateFormat.format(new Date());
            // final Memory memory=memories.get(position);
            int memoryId = memoryIntent.getMemoryId();
            Log.i("memoryID", memoryId + "");
            HashMap<String, String> header = new HashMap<>();
            header.put("memoryId", String.valueOf(memoryId));
            header.put("date", currentDateandTime);
            header.put("text", textEditMemory.getText().toString());
            requestQueue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();

            gsonRequest = new GsonRequest(Urls.EDIT_MEMORY_URL, Request.Method.POST, Status.class, header, new Response.Listener<Status>() {

                @Override
                public void onResponse(Status status) {

                   // Toast.makeText(getApplicationContext(), status.getMessage(), Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.editMemory), Toast.LENGTH_LONG).show();
                    Log.i("^^^_^^^^",(getIntent().getIntExtra("type", 0)+"^_^"));
                    if (getIntent().getIntExtra("type", 0) == CustomPatientActivityAdapter.IS_RELATIVE) {
                        goToRelativeHomeActivity();

                    }
                    if (getIntent().getIntExtra("type", 0) == CustomRelativeActivityAdapter.IS_PATIENT) {
                        goToPatientHomeActivity();

                    }

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
        }else{
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.no_internt_msg),Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This method is used to show dialog
     *
     * @param message
     */

    public void showDialog(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MutualMemoryWriteTextActivity.this);

        alertDialog.setTitle("Warning");
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                if (getIntent().getIntExtra("type", 0) == CustomPatientActivityAdapter.IS_RELATIVE) {
                    Intent intent = new Intent(getApplicationContext(), RelativeHomeActivity.class);
                    startActivity(intent);
                }
                if(getIntent().getIntExtra("type", 0) == CustomRelativeActivityAdapter.IS_PATIENT){
                    Intent intent = new Intent(getApplicationContext(), PatientHomeActivity.class);
                    startActivity(intent);
                }


            }
        });


        alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        alertDialog.show();

    }

    /**
     * this is used to go to mutual memory Activity
     */
    private void goToMutualMemoryActivity() {
        Intent intent = new Intent(getApplicationContext(), MutualMemoryActivity.class);
        startActivity(intent);
        this.finish();
    }
    /**
     * this is used to go to patient home Activity
     */
    private void goToPatientHomeActivity() {
        Intent intent = new Intent(getApplicationContext(), PatientHomeActivity.class);
        startActivity(intent);
        this.finish();
    }

    /**
     * This method is used to go to relative home activity
     */
    private void goToRelativeHomeActivity() {
        Intent intent=new Intent(this, RelativeHomeActivity.class);
        startActivity(intent);
        this.finish();
    }
}