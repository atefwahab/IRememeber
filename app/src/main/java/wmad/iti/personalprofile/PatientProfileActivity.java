package wmad.iti.personalprofile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import wmad.iti.constants.Urls;
import wmad.iti.dto.Status;
import wmad.iti.dto.User;
import wmad.iti.irememeber.R;
import wmad.iti.model.ConnectionDetector;
import wmad.iti.model.GsonRequest;
import wmad.iti.model.MySingleton;
import wmad.iti.model.SharedPreferenceManager;
import wmad.iti.util.PathValue;

public class PatientProfileActivity extends AppCompatActivity {

    //Dialog appear when we press drawable right edit
    private Context contextForDialog = null;

    public EditText firstInput;
    public EditText secondInput;

    //circular imageView
    CircularImageView relativeImageProfile;
    FloatingActionButton changeCameraFloatingActionButton;

    // two text view to show data in relative profile
   private TextView firstName,phoneNum, countrytText, cityText, addressText ;

    //toolbar which consist of navigationbar and title of page
    private Toolbar toolbar;

    //Camera

    private static final int REQUEST_CAMERA = 209;
    private static final int REQUEST_GALLERY = 151;
    File photoFile = null;
    String mCurrentPhotoPath;

    String imagePath=null;

    ProgressDialog getlocationProgressDialog;

    //webService

    String url;

    ConnectionDetector connectionDetector;

    GsonRequest gsonRequest;

    User user = new User();

    public HashMap<String, String> headers;

    RequestQueue requestQueue;

    String imageURL;

    // final static String UPLOAD_URL="http://10.0.1.25:8084/WebService/service/regist/file";

    Uri filePath;

    //start of onCreate
    @Override

    protected void onCreate(Bundle savedInstanceState) {

        contextForDialog = this;

        View parentLayout = findViewById(R.id.main);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_patient_profile);

        firstName = (TextView) findViewById(R.id.firsttext);
        phoneNum = (TextView) findViewById(R.id.phonetext);

        countrytText= (TextView) findViewById(R.id.countrytext);


        cityText=(TextView)findViewById(R.id.citytext);
        addressText=(TextView)findViewById(R.id.addresstext);


        relativeImageProfile = (CircularImageView) findViewById(R.id.profile_circularImageView);

        // param for jsonRequest

        headers = new HashMap<String,String>();

        //InternetConnection

        connectionDetector = new ConnectionDetector(getApplicationContext());

        // Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        toolbar.setNavigationIcon(R.drawable.navigation_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        showProfile(findViewById(android.R.id.content));
        //method to update name
        nameUpdated();
        //method to update phonenum
        phoneUpdated();
        //method to update country
        countryUpdated();
        //method to update city
        cityUpdated();
        //method to update address
        addressUpdated();

        relativeImageProfile = (CircularImageView) findViewById(R.id.profile_circularImageView);

        changeCameraFloatingActionButton = (FloatingActionButton) findViewById(R.id.changeImgFloatingActionButton);

        changeCameraFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                selectImage();


            }
        });



    }//end oncreate

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    private File createImageFile() throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_"+ timeStamp + "_";
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
    }//


    /**
     * this method is used to select image using AlertDialoge
     */

    private void selectImage() {
        final CharSequence[] items = {"Open Camera", "Choose from Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    }//end of method selectImg

    /**
     * this method used to set image in circular image view
     */
    private void setImage() {

        // Get the dimensions of the View
        int targetW = relativeImageProfile.getWidth();
        int targetH = relativeImageProfile.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFile.getPath(), bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        imagePath = photoFile.getAbsolutePath();
        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getPath(), bmOptions);
        relativeImageProfile.setImageBitmap(bitmap);
    }

    /**
     * this method used to select image from gallery
     * @param data
     */

    private void onSelectFromGallery(Intent data) {

        Bitmap image = null;

        if (data != null) {
            Log.i("******* ", data.getData().toString());
            try {
                // to know path of image
                imagePath = PathValue.getPath(getApplicationContext(),data.getData());
                image = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                relativeImageProfile.setImageBitmap(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }//end of method onSelectFromGallery


    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            // Open Camera Case
            case REQUEST_CAMERA:

                if (resultCode == RESULT_OK) {


                    setImage();
                    uploadPicture();
                }
                break;


            // gallery case
            case REQUEST_GALLERY:
                if (resultCode == RESULT_OK) {
                    filePath = data.getData();
                    onSelectFromGallery(data);
                    uploadPicture();
                }
                break;
        }

    }


    /**
     * this method used to get user information when user login and show this information in personal profile
     */

    private void showProfile(View view){

        //login to show data by make json request

        // check for Internet status

        if (connectionDetector.isConnectingToInternet()) {

            //if there is internet connection we should get data from web service
            // Internet Connection is Present
            // make HTTP requests

            getUser();


        }//end if
        else {

            // Internet connection is not present
            User user= SharedPreferenceManager.getUser(getApplicationContext());
            setFirstNameText(user);
            setPhoneNumText(user);
            setCountryText(user);
            setCityText(user);
            setAddressText(user);
            setImageRequest(user.getImageUrl());


        }//end else

    }//end of method showProfile


    /**
     * this method used to connect to webService by volley and get object from user
     * @return
     */
    private  void getUser(){


        headers.put("email",SharedPreferenceManager.getUser(getApplicationContext()).getEmail());

        headers.put("password",SharedPreferenceManager.getUser(getApplicationContext()).getPassword());

        requestQueue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();

        gsonRequest = new GsonRequest(Urls.WEB_SERVICE_lOGIN_URL, Request.Method.POST, Status.class, headers, new Response.Listener<Status>() {
            @Override
            public void onResponse(Status response) {
                // in case of success login

                if (response.getStatus() == 1) {

                    Log.i("success", response.toString());

                    user = response.getUser();
                    setFirstNameText(user);
                    setPhoneNumText(user);
                    setCountryText(user);
                    setCityText(user);
                    setAddressText(user);

                    //call setImageRequest method

                    setImageRequest(user.getImageUrl());

                    SharedPreferenceManager.saveUser(getApplicationContext(), user);

                }//end if

                //in case of fail login
                else {

                    Log.i("loginfail", response.toString());
                    User user = SharedPreferenceManager.getUser(getApplicationContext());
                    setFirstNameText(user);
                    setPhoneNumText(user);
                    setCountryText(user);
                    setCityText(user);
                    setAddressText(user);
                }//end else

            }
            //on error
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                // Log.i("error volley", error.toString());
                User user = SharedPreferenceManager.getUser(getApplicationContext());
                setFirstNameText(user);
                setPhoneNumText(user);
                setCountryText(user);
                setCityText(user);
                setAddressText(user);
            }

        });

        //add request to Queue
        requestQueue.add(gsonRequest);



    }//end of getUser


    //code of phone num textview


    /**
     * this metho used to set data into phoneNum text
     */

    private void  setPhoneNumText(User user){

        phoneNum.setText(user.getPhoneNumber());

    }//end of method setPhoneNumText


    /**
     * this metho used to set data into phoneNum text
     */

    private void  setCountryText(User user){

        countrytText.setText(user.getCountry());

    }//end of method

    /**
     * this metho used to set data into phoneNum text
     */

    private void  setCityText(User user){

        cityText.setText(user.getCity());

    }//end of method

    /**
     * this metho used to set data into phoneNum text
     */

    private void  setAddressText(User user){

        addressText.setText(user.getAddress());

    }//end of method setPhoneNumText
    /**
     * this method used to get image from webservice
     * @param imageURL
     */
    private void setImageRequest(String imageURL) {

        ImageRequest imageRequest=new ImageRequest(imageURL,

                // On Success
                new Response.Listener<Bitmap>() {

                    @Override
                    // on success
                    public void onResponse(Bitmap bitmap) {

                        relativeImageProfile.setImageBitmap(bitmap);

                    }
                }, 0, 0, null,

                //on error

                new Response.ErrorListener() {

                    public void onErrorResponse(VolleyError error) {

                        relativeImageProfile.setImageResource(R.drawable.default_profle);

                    }
                });
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(imageRequest);

    }//end of setImageRequest method

    /**
     *this method used to update data of user when we press on ok in Dialog
     */

    public void nameUpdated() {

        // listener on drawable_right edit

        firstName.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int DRAWABLE_LEFT = 0;

                final int DRAWABLE_TOP = 1;

                final int DRAWABLE_RIGHT = 2;

                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    if (event.getRawX() >= (firstName.getRight() - firstName.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()))

                    {

                        // my action here
                        // get prompts.xml view
                        if (connectionDetector.isConnectingToInternet()) {

                            LayoutInflater li = LayoutInflater.from(getApplicationContext());

                            View promptsView = li.inflate(R.layout.prompts, null);

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(contextForDialog).setTitle("Enter Your name:");


                            // set prompts.xml to alertdialog builder
                            firstInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
                            firstInput.setText(user.getFirstName());
                            secondInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput2);
                            secondInput.setText(user.getLastName());

                            alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener()

                            {
                                public void onClick(DialogInterface dialog, int id)

                                {

                                    //  requestUpdateName();
                                    headers.put("userEmail", SharedPreferenceManager.getUser(getApplicationContext()).getEmail());

                                    headers.put("userfirstName", firstInput.getText().toString());

                                    headers.put("userlastName", secondInput.getText().toString());

                                    requestUpdate(Urls.WEB_SERVICE_FULL_NAME_URL, headers);

                                    SharedPreferenceManager.saveUser(getApplicationContext(), user);


                                }
                            })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            User user = SharedPreferenceManager.getUser(getApplicationContext());
                                            setFirstNameText(user);

                                        }
                                    });

                            // create alert dialog
                            alertDialogBuilder.setView(promptsView);
                            AlertDialog alertDialog = alertDialogBuilder.create();

                            // show it
                            alertDialog.show();

                            return true;
                        }//end of internetconnection
                        else {

                            Snackbar snackbar = Snackbar.make(findViewById(R.id.main), "No Connection", Snackbar.LENGTH_LONG);
                            snackbar.show();

                        }

                    }
                }

                return false;
            }
        });
    }//end of method

    /////////////////////////////////////////////

    /**
     *this method used to update country
     */

    private void countryUpdated (){

        // listener on drawable_right edit
        countrytText.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int DRAWABLE_LEFT = 0;

                final int DRAWABLE_TOP = 1;

                final int DRAWABLE_RIGHT = 2;

                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    if (event.getRawX() >= (countrytText.getRight() - countrytText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()))

                    {
                        // my action here
                        // get prompts.xml view
                        if (connectionDetector.isConnectingToInternet()) {

                            LayoutInflater li = LayoutInflater.from(getApplicationContext());

                            View promptsView = li.inflate(R.layout.prompts, null);

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(contextForDialog).setTitle("Enter your country");

                            // set prompts.xml to alertdialog builder
                            firstInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
                            firstInput.setText(user.getCountry());
                            secondInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput2);

                            secondInput.setVisibility(View.GONE);
                            alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener()

                            {
                                public void onClick(DialogInterface dialog, int id)

                                {

                                    headers.put("userEmail", SharedPreferenceManager.getUser(getApplicationContext()).getEmail());

                                    headers.put("country", firstInput.getText().toString());



                                    requestUpdate(Urls.WEB_SERVICE_COUNTRY_URL, headers);
                                    SharedPreferenceManager.saveUser(getApplicationContext(), user);


                                }
                            })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            User user = SharedPreferenceManager.getUser(getApplicationContext());
                                           // setPhoneNumText(user);
                                           // countrytText.setText(user.getCountry());

                                             setCountryText(user);

                                        }
                                    });

                            // create alert dialog
                            alertDialogBuilder.setView(promptsView);
                            AlertDialog alertDialog = alertDialogBuilder.create();

                            // show it
                            alertDialog.show();

                            return true;
                        }//end of internetconnection
                        else {

                            Snackbar snackbar = Snackbar.make(findViewById(R.id.main), "No Connection", Snackbar.LENGTH_LONG);
                            snackbar.show();

                        }
                    }
                }

                return false;
            }
        });
    }//end of method phoneUpdated


    /**
     *this method used to update city
     */

    private void cityUpdated (){

        // listener on drawable_right edit

        cityText.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int DRAWABLE_LEFT = 0;

                final int DRAWABLE_TOP = 1;

                final int DRAWABLE_RIGHT = 2;

                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    if (event.getRawX() >= (cityText.getRight() - cityText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()))

                    {
                        // my action here
                        // get prompts.xml view
                        if (connectionDetector.isConnectingToInternet()) {

                            LayoutInflater li = LayoutInflater.from(getApplicationContext());

                            View promptsView = li.inflate(R.layout.prompts, null);

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(contextForDialog).setTitle("Enter your city");

                            // set prompts.xml to alertdialog builder
                            firstInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
                            firstInput.setText(user.getCity());
                            secondInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput2);

                            secondInput.setVisibility(View.GONE);
                            alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener()

                            {
                                public void onClick(DialogInterface dialog, int id)

                                {

                                    headers.put("userEmail", SharedPreferenceManager.getUser(getApplicationContext()).getEmail());

                                    headers.put("city", firstInput.getText().toString());



                                    requestUpdate(Urls.WEB_SERVICE_CITY_URL, headers);
                                    SharedPreferenceManager.saveUser(getApplicationContext(), user);


                                }
                            })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            User user = SharedPreferenceManager.getUser(getApplicationContext());
                                            // setPhoneNumText(user);
                                            setCityText(user);

                                        }
                                    });

                            // create alert dialog
                            alertDialogBuilder.setView(promptsView);
                            AlertDialog alertDialog = alertDialogBuilder.create();

                            // show it
                            alertDialog.show();

                            return true;
                        }//end of internetconnection
                        else {

                            Snackbar snackbar = Snackbar.make(findViewById(R.id.main), "No Connection", Snackbar.LENGTH_LONG);
                            snackbar.show();

                        }
                    }
                }

                return false;
            }
        });
    }//end of method cityUpdated


    /**
     *this method used to update address
     */

    private void addressUpdated (){

        // listener on drawable_right edit

        addressText.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int DRAWABLE_LEFT = 0;

                final int DRAWABLE_TOP = 1;

                final int DRAWABLE_RIGHT = 2;

                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    if (event.getRawX() >= (addressText.getRight() - addressText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()))

                    {
                        // my action here
                        // get prompts.xml view
                        if (connectionDetector.isConnectingToInternet()) {

                            LayoutInflater li = LayoutInflater.from(getApplicationContext());

                            View promptsView = li.inflate(R.layout.prompts, null);

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(contextForDialog).setTitle("Enter your address");

                            // set prompts.xml to alertdialog builder
                            firstInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
                            firstInput.setText(user.getAddress());
                            secondInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput2);

                            secondInput.setVisibility(View.GONE);
                            alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener()

                            {
                                public void onClick(DialogInterface dialog, int id)

                                {

                                    headers.put("userEmail", SharedPreferenceManager.getUser(getApplicationContext()).getEmail());

                                    headers.put("address", firstInput.getText().toString());


                                    requestUpdate(Urls.WEB_SERVICE_ADDRESS_URL, headers);
                                    SharedPreferenceManager.saveUser(getApplicationContext(), user);


                                }
                            })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            User user = SharedPreferenceManager.getUser(getApplicationContext());
                                            // setPhoneNumText(user);
                                            setAddressText(user);

                                        }
                                    });

                            // create alert dialog
                            alertDialogBuilder.setView(promptsView);
                            AlertDialog alertDialog = alertDialogBuilder.create();

                            // show it
                            alertDialog.show();

                            return true;
                        }//end of internetconnection
                        else {

                            Snackbar snackbar = Snackbar.make(findViewById(R.id.main), "No Connection", Snackbar.LENGTH_LONG);
                            snackbar.show();

                        }
                    }
                }

                return false;
            }
        });
    }//end of method phoneUpdated
//////////////////////////////////////////////////////
    /**
     * this method used to send request by volley to update information of the user
     * @param url
     * @param headers
     */
    private void requestUpdate(String url,HashMap <String,String> headers ){


        requestQueue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();

        gsonRequest = new GsonRequest(url, Request.Method.POST, Status.class, headers, new Response.Listener<Status>()

        {

            @Override

            public void onResponse(Status response) {

                // in case of success login
                if (response.getStatus() == 1) {

                    getUser();


                    SharedPreferenceManager.saveUser(getApplicationContext(), user);



                }//end if

                //in case of fail login
                else {

                    Log.i("updatefail", response.toString());

                    User user= SharedPreferenceManager.getUser(getApplicationContext());
                    setFirstNameText(user);

                }//end else

            }
            //on error
        }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("error volley", error.toString());

                SharedPreferenceManager.getUser(getApplicationContext());

            }

        });
        requestQueue.add(gsonRequest);

    }//end of method


    /**
     * this method used to set first text with fullName
     * @param user
     */
    private void setFirstNameText(User user){

        firstName.setText(user.getFirstName() + " " + user.getLastName());

    }//end of method setFirstNameText


    /**
     *this method used to update phoneNUm
     */

    private void phoneUpdated() {

        // listener on drawable_right edit

        phoneNum.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int DRAWABLE_LEFT = 0;

                final int DRAWABLE_TOP = 1;

                final int DRAWABLE_RIGHT = 2;

                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    if (event.getRawX() >= (phoneNum.getRight() - phoneNum.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()))

                    {
                        // my action here
                        // get prompts.xml view
                        if (connectionDetector.isConnectingToInternet()) {

                            LayoutInflater li = LayoutInflater.from(getApplicationContext());

                            View promptsView = li.inflate(R.layout.prompts, null);

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(contextForDialog).setTitle("Enter phone number");

                            // set prompts.xml to alertdialog builder
                            firstInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
                            firstInput.setText(user.getPhoneNumber());
                            secondInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput2);

                            secondInput.setVisibility(View.GONE);
                            alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener()

                            {
                                public void onClick(DialogInterface dialog, int id)

                                {

                                    headers.put("userEmail", SharedPreferenceManager.getUser(getApplicationContext()).getEmail());

                                    headers.put("phone", firstInput.getText().toString());


                                    //  requestUpdatePhone();
                                    requestUpdate(Urls.WEB_SERVICE_PHONE_NUMBER_URL,headers);
                                    SharedPreferenceManager.saveUser(getApplicationContext(), user);


                                }
                            })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            User user = SharedPreferenceManager.getUser(getApplicationContext());
                                            setPhoneNumText(user);

                                        }
                                    });

                            // create alert dialog
                            alertDialogBuilder.setView(promptsView);
                            AlertDialog alertDialog = alertDialogBuilder.create();

                            // show it
                            alertDialog.show();

                            return true;
                        }//end of internetconnection
                        else {

                            Snackbar snackbar = Snackbar.make(findViewById(R.id.main), "No Connection", Snackbar.LENGTH_LONG);
                            snackbar.show();

                        }
                    }
                }

                return false;
            }
        });
    }//end of method phoneUpdated


    /**
     * this method used to upload image to server
     */
    private void uploadPicture() {

        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
        Ion.with(getApplicationContext())
                .load(Urls.IMAGE_UPLOAD_URL).setLogging("UPLOAD LOGS", Log.DEBUG)
                .setMultipartParameter("email", user.getEmail())
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

                    loading.dismiss();

                }//end if
            }//end of onCompleted
        });
    }//end of method  uploadPicture

    private void callback(){

        Log.i("CallBack", "i was called");
    }//end of method callback

    /**
     * this method used to get realpath of uri
     * @param contentURI
     * @return
     */




}
