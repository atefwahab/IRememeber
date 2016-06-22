package wmad.iti.irememeber;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mikhaellopez.circularimageview.CircularImageView;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import wmad.iti.constants.Urls;
import wmad.iti.dto.Status;
import wmad.iti.dto.User;
import wmad.iti.model.GsonRequest;
import wmad.iti.model.MySingleton;
import wmad.iti.model.SharedPreferenceManager;
import wmad.iti.util.PathValue;
import wmad.iti.util.Validator;

public class RegisterActivity extends AppCompatActivity implements BirthdateInterface, AdapterView.OnItemSelectedListener {

    FloatingActionButton changeCameraFloatingActionButton;
    CircularImageView circularImageView;

    User user;
    Calendar birthdate;
    File photoFile = null;
    String mCurrentPhotoPath;
    String imagePath=null;
    ProgressDialog registerProgressDialog;

    ProgressDialog getlocationProgressDialog;


    IntlPhoneInput phoneInputView;
    BluetoothAdapter mBluetoothAdapter;

    private GoogleApiClient mClient;


    ScrollView scrollView ;

    //EditTexts
    EditText firstNameEditText,
            lastnameEditText, birthdateEditText,homeNumberEditText,emailEditText,passwordEditText,
            passwordConfrimationEditText,
            homeAddressEditText,countryAddressEditText,cityAddressEditText;

    // input Layout
    TextInputLayout firstnameTextInputLayout,lastnameTextInputLayout,homeNumberTextInputLayout,
            birthdateTextInputLayout,emailTextInputLayout,passwordTextInputLayout,passwordConfirmTextInputLayout;

    ImageButton birthdateImageButton;

    // layouts
    LinearLayout firstnameLayout,
            lastnameLayout,
            phonenumLayout,
            birthdateLayout, homeAddressLayout,
            homeNumberLayout,genderLayout,emailLayout,passwordLayout,passwordConfirmationLayout;

    // buttons
    Button personTypeNextButton,
            firstnameNextButton,
            lastnameNextButton,
            phoneNumberNextButton,
            birthdayNextButton,genderNextButton,emailNextButton,passwordNextButton,passwordConfirmationNextButton,
            homeNumberNextButton,
            registerButton,
            getHomeAddressButton,birthdateNextButton;


    // Array Adapters
    ArrayAdapter<CharSequence> genderAdapter;
    ArrayAdapter<CharSequence> personTypeadapter;

    private static final int REQUEST_CAMERA = 209;
    private static final int REQUEST_GALLERY = 151;
    private static final int REQUEST_PLAC_PICKER = 55;


    //longitude and latitude
    double homeLongitude;
    double homeLatitude;

    int gender; // 0 for female 1 for male

    int type; // 0 for relative and 1 for patient


    //Spinners
    Spinner personTypeSpinner;
    Spinner genderSpinner;



    // Request queue Instance ..
    RequestQueue queue;

    // Strings
    String city=null;
    String country =null;
    String address = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



        scrollView= (ScrollView) findViewById(R.id.scrollView);
        // Edit texts
        lastnameEditText = (EditText) findViewById(R.id.lastnameEditText);
        birthdateEditText = (EditText) findViewById(R.id.birthdateEditText);
        homeAddressEditText = (EditText) findViewById(R.id.homeAddressEditText);
        countryAddressEditText=(EditText)findViewById(R.id.countryAddressEditText);
        cityAddressEditText=(EditText) findViewById(R.id.cityAddressEditText);



        // Layouts
        firstnameLayout = (LinearLayout) findViewById(R.id.firstnameLayout);

        // initialize a new object from user
        user = new User();

        // Initialize the client
        mClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();


        // Person type spinner
        personTypeSpinner = (Spinner) findViewById(R.id.peronTypeSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        personTypeadapter = ArrayAdapter.createFromResource(this,
                R.array.type, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        personTypeadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        personTypeSpinner.setAdapter(personTypeadapter);
        personTypeSpinner.setOnItemSelectedListener(this);

        // PersonTypButton next
        personTypeNextButton = (Button) findViewById(R.id.nextPersonTypeButton);
        personTypeNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                user.setType(type);
                personTypeNextButton.setVisibility(View.GONE);
                firstnameLayout.setVisibility(View.VISIBLE);
            }
        });

        circularImageView = (CircularImageView) findViewById(R.id.profile_circularImageView);






        changeCameraFloatingActionButton = (FloatingActionButton) findViewById(R.id.changeImgFloatingActionButton);
        changeCameraFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                selectImage();

            }
        });

        // to show last name layout ..
        firstnameNextButton = (Button) findViewById(R.id.firstnameNextButton);
        firstnameNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showLastNameLayout();




            }
        });


        // show phone number layout
        lastnameNextButton = (Button) findViewById(R.id.lastnamenextButton);
        lastnameNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhoneNumberLayout();
            }
        });



        // show home number layout
        phoneNumberNextButton = (Button) findViewById(R.id.phoneNumberNextButton);
        phoneNumberNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHomeNumberLayout();
            }
        });

        // home num next button
        homeNumberNextButton =(Button) findViewById(R.id.nextHomePhoneNumberButton);
        homeNumberNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBirthdateLayout();
            }
        });

        // show gender
        birthdateNextButton = (Button) findViewById(R.id.nextBirthdateButton);
        birthdateNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGender();
            }
        });

        // show email
        genderNextButton = (Button) findViewById(R.id.nextgenderButton);
        genderNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmail();
            }
        });


        // show password
        emailNextButton = (Button) findViewById(R.id.nextEmailButton);
        emailNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPassword();
            }
        });



        //passwordNextButton
        passwordNextButton = (Button) findViewById(R.id.nextPasswordButton);
        passwordNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordConfirmation();
            }
        });


        // password confirmation next button
        passwordConfirmationNextButton = (Button) findViewById(R.id.nextPasswordConfrimButton);
        passwordConfirmationNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHomeAddress();
            }
        });


        birthdateImageButton = (ImageButton) findViewById(R.id.getBirthdayimageButton);
        birthdateImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment birthdateDialogFragment = new MyDatePickerFragment();
                birthdateDialogFragment.show(getSupportFragmentManager(), "datePicker");
                birthdateEditText.setEnabled(false);
            }
        });


        // get spinner object
        genderSpinner = (Spinner) findViewById(R.id.genderSpinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        genderAdapter = ArrayAdapter.createFromResource(this, R.array.gender, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        genderSpinner.setAdapter(genderAdapter);

        //add listener to genderSpinner
        genderSpinner.setOnItemSelectedListener(this);





        // get the location of home
        getHomeAddressButton = (Button) findViewById(R.id.homeAddressButton);
        getHomeAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                // place picker
                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                try {

                    startActivityForResult(intentBuilder.build(RegisterActivity.this), REQUEST_PLAC_PICKER);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //register of home address
        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                register();
            }
        });
        //




        // end of OnCreate method



    }


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

                         //   Log.i("*****", "file exists");
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

                }
                break;


            // gallery case
            case REQUEST_GALLERY:
                if (resultCode == RESULT_OK) {
                    onSelectFromGallery(data);
                }
                break;

            // Place Picker case
            case REQUEST_PLAC_PICKER:


                if (resultCode == RESULT_OK) {
                    Place place = PlacePicker.getPlace(data, this);

                    homeLatitude=place.getLatLng().latitude;
                    homeLongitude=place.getLatLng().longitude;

                    address = String.format("%s", place.getAddress());
                    Log.e("address",address);
                    Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());

                    try {
                        List<Address> addressList = geocoder.getFromLocation(homeLatitude,homeLongitude, 1);
                        // 0 index in object is containing the whole address

                        // getting address if it didn't come from PlacePicker API
                        if(address==null||address.equals("")){

                            address = addressList.get(0).getAddressLine(0);
                        }


                        // 1 contains the city
                        city = addressList.get(0).getAddressLine(1);

                        // 2 contains country
                        country = addressList.get(0).getCountryName();


                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                    String homeAddress = String.format("%s", place.getAddress());
                    homeAddressEditText.setText(address);
                    cityAddressEditText.setText(city);
                    countryAddressEditText.setText(country);


                    registerButton.setVisibility(View.VISIBLE);



                }
                if(resultCode==PlacePicker.RESULT_ERROR){

                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.gps_not_available),Toast.LENGTH_LONG);
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

    private void setImage() {

        // Get the dimensions of the View
        int targetW = circularImageView.getWidth();
        int targetH = circularImageView.getHeight();

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
        circularImageView.setImageBitmap(bitmap);
    }

    private void onSelectFromGallery(Intent data) {

        Bitmap image = null;

        if (data != null) {
            Log.i("******* ", data.getData().toString());
            try {
                // to know path of image

                imagePath = PathValue.getPath(getApplicationContext(),data.getData());
                image = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                circularImageView.setImageBitmap(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * @param year
     * @param month
     * @param day
     * @author Atef
     * This method used to update the date from datePicker ..
     */
    @Override
    public void changeDate(int year, int month, int day) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        // get date object from Calendar to print date
        Date date = calendar.getTime();
        this.birthdate = calendar;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Log.i("DATE .....", date.toString());
        birthdateEditText.setText(simpleDateFormat.format(date));
    }

    /**
     * <p>Callback method to be invoked when an item in this view has been
     * selected. This callback is invoked only when the newly selected
     * position is different from the previously selected position or if
     * there was no selected item.</p>
     * <p/>
     * Impelmenters can call getItemAtPosition(position) if they need to access the
     * data associated with the selected item.
     *
     * @param parent   The AdapterView where the selection happened
     * @param view     The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id       The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


        Log.i("Spinner","OnItemSelected is called");




        if (parent.getItemAtPosition(position).toString().equals("Male")) {

            Log.i("Spinner","male");
            //male
            gender = 1;


        }
        if(parent.getItemAtPosition(position).toString().equals(getResources().getStringArray(R.array.gender)[1])){

            // female
            gender = 0;
        }



        // patient
        if (parent.getItemAtPosition(position).toString().equals(getResources().getStringArray(R.array.type)[0])) {

            type=1;


        }

        // relative

        if (parent.getItemAtPosition(position).toString().equals(getResources().getStringArray(R.array.type)[1])) {

            type=0;


        }



    }

    /**
     * Callback method to be invoked when the selection disappears from this
     * view. The selection can disappear for instance when touch is activated
     * or when the adapter becomes empty.
     *
     * @param parent The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }





    /**
     * @author Atef
     *         this  class used to show the datePicker dialog when i choose birthday
     */
    public static class MyDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        BirthdateInterface birthdateInterface;


        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {

            birthdateInterface.changeDate(year, month, day);


        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            birthdateInterface = (BirthdateInterface) activity;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);


            // Create a new instance of DatePickerDialog and return it

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }
    }// end of datepicker class




    protected void onStart() {
        super.onStart();
        mClient.connect();
    }
    @Override
    protected void onStop() {
        mClient.disconnect();
        super.onStop();
    }


    /**
     * this method used to show lastname Layout
     */
    private  void showLastNameLayout(){


        firstNameEditText = (EditText) findViewById(R.id.firstnameEditText);
        firstnameTextInputLayout = (TextInputLayout) findViewById(R.id.firstnameInputLayout);

        // validate first name
        if(Validator.isNotEmpty(firstNameEditText)){

            user.setFirstName(firstNameEditText.getText().toString());
            lastnameLayout = (LinearLayout) findViewById(R.id.lastnameLayout);
            firstnameNextButton.setVisibility(View.GONE);
            firstnameTextInputLayout.setErrorEnabled(false);
            firstNameEditText.setEnabled(false);
            lastnameLayout.setVisibility(View.VISIBLE);

            scrollView.post(new Runnable() {

                @Override
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    lastnameEditText.requestFocus();
                }
            });



        }else{


            firstnameTextInputLayout.setError(getResources().getString(R.string.firstname_is_required));

        }
    }


    /**
     * used to show phone number layout
     */
    private void showPhoneNumberLayout(){

        lastnameEditText = (EditText) findViewById(R.id.lastnameEditText);
        lastnameTextInputLayout = (TextInputLayout) findViewById(R.id.lastnameInputLayout);

        if(Validator.isNotEmpty(lastnameEditText)){

            phoneInputView = (IntlPhoneInput) findViewById(R.id.phonenumberEditText);
            user.setLastName(lastnameEditText.getText().toString());
            phonenumLayout = (LinearLayout) findViewById(R.id.phonenumLayout);
            lastnameNextButton.setVisibility(View.GONE);
            lastnameEditText.setEnabled(false);
            lastnameTextInputLayout.setErrorEnabled(false);
            phonenumLayout.setVisibility(View.VISIBLE);

            scrollView.post(new Runnable() {

                @Override
                public void run() {

                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);

                }
            });


        }else{

            lastnameTextInputLayout.setError(getResources().getString(R.string.lastname_is_required));
        }

    }

    private  void showHomeNumberLayout(){




        if (phoneInputView.isValid()){


            homeNumberEditText = (EditText) findViewById(R.id.homePhoneNumberEditText);
            // add it to user
            user.setPhoneNumber(phoneInputView.getNumber());
            homeNumberLayout = (LinearLayout) findViewById(R.id.homePhoneNumberLayout);
            phoneNumberNextButton.setVisibility(View.GONE);
            phoneInputView.setEnabled(false);
            homeNumberLayout.setVisibility(View.VISIBLE);

            scrollView.post(new Runnable() {

                @Override
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    homeNumberEditText.requestFocus();
                }
            });

        }
    }


    // show Birthdate Layout
    private void showBirthdateLayout(){

        homeNumberTextInputLayout = (TextInputLayout) findViewById(R.id.homePhoneNumberTextInputLayout);




        // add to user
        user.setHomeNumber(homeNumberEditText.getText().toString());

        homeNumberTextInputLayout.setErrorEnabled(false);
        birthdateLayout = (LinearLayout) findViewById(R.id.birthdateLayout);



        homeNumberNextButton.setVisibility(View.GONE);
        homeNumberEditText.setEnabled(false);
        birthdateEditText.setEnabled(false);
        birthdateLayout.setVisibility(View.VISIBLE);

        scrollView.post(new Runnable() {

            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                birthdateEditText.requestFocus();
            }
        });



    }

    private void showGender(){


        birthdateTextInputLayout = (TextInputLayout) findViewById(R.id.birthdateTextInputLayout);
        genderLayout = (LinearLayout) findViewById(R.id.genderLayout);

        if(Validator.isNotEmpty(birthdateEditText)&&Validator.isBirthdate(birthdate)){


            user.setBirthday(new java.sql.Date(birthdate.getTimeInMillis()));
            birthdateTextInputLayout.setErrorEnabled(false);
            birthdateNextButton.setVisibility(View.GONE);
            genderLayout.setVisibility(View.VISIBLE);
            birthdateEditText.setEnabled(true);

            scrollView.post(new Runnable() {

                @Override
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    genderSpinner.requestFocus();
                }
            });



        }else{
            birthdateTextInputLayout.setError(getResources().getString(R.string.birthdate_error));
        }
    }


    // show email
    private void showEmail(){


        emailEditText = (EditText) findViewById(R.id.emailEditText);
        user.setGender(gender);

        emailLayout = (LinearLayout) findViewById(R.id.emailLayout);
        genderNextButton.setVisibility(View.GONE);
        emailLayout.setVisibility(View.VISIBLE);



        scrollView.post(new Runnable() {

            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                emailEditText.requestFocus();
            }
        });





    }


    // after email show password
    private void showPassword(){


        emailTextInputLayout = (TextInputLayout) findViewById(R.id.emailTextInputLayout);


        // validate email
        if(Validator.isEmail(emailEditText)){

            emailTextInputLayout.setErrorEnabled(false);
            // add to user
            user.setEmail(emailEditText.getText().toString());

            passwordLayout = (LinearLayout) findViewById(R.id.passwordLayout);
            emailEditText.setEnabled(false);
            emailNextButton.setVisibility(View.GONE);
            passwordLayout.setVisibility(View.VISIBLE);

            passwordEditText = (EditText)findViewById(R.id.passwordEditText);

            scrollView.post(new Runnable() {

                @Override
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    passwordEditText.requestFocus();
                }
            });

        }else{

            emailTextInputLayout.setError(getResources().getString(R.string.invalid_email));
        }

    }

    // show password confirmation
    private void showPasswordConfirmation(){

        passwordTextInputLayout = (TextInputLayout) findViewById(R.id.passwordTextInputLayout);

        // Validate password
        if(Validator.isPassword(passwordEditText)){

            passwordTextInputLayout.setErrorEnabled(false);

            passwordEditText.setEnabled(false);

            passwordNextButton.setVisibility(View.GONE);
            passwordConfirmationLayout = (LinearLayout) findViewById(R.id.passwordConfirmLayout);
            passwordConfirmationLayout.setVisibility(View.VISIBLE);
            passwordConfrimationEditText=(EditText) findViewById(R.id.passwordConfirmEditText);



            scrollView.post(new Runnable() {

                @Override
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    passwordConfrimationEditText.requestFocus();
                }
            });



        }else{
            passwordTextInputLayout.setError(getResources().getString(R.string.invalid_password));
        }
    }

    // this method used to show home address
    private void showHomeAddress(){

        passwordConfirmTextInputLayout = (TextInputLayout) findViewById(R.id.passwordConfirmTextInputLayout);

        if(Validator.isMatchPassword(passwordEditText, passwordConfrimationEditText)){


            user.setPassword(passwordConfrimationEditText.getText().toString());

            passwordConfirmTextInputLayout.setErrorEnabled(false);
            passwordConfrimationEditText.setEnabled(false);
            passwordConfirmationNextButton.setVisibility(View.GONE);
            homeAddressLayout = (LinearLayout) findViewById(R.id.homeAddressLayout);
            homeAddressLayout.setVisibility(View.VISIBLE);





            scrollView.post(new Runnable() {

                @Override
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);

                }
            });



        }else{

            passwordConfirmTextInputLayout.setError(getResources().getString(R.string.passwords_not_matched));
        }
    }

    /**
     * this method used to perform reg
     */
    private void register(){


        registerProgressDialog = ProgressDialog.show(this,getResources().getString(R.string.registering),getResources().getString(R.string.please_wait),false,false);




        //check validated address
        if(Validator.isNotEmptyString(country)&&Validator.isNotEmptyString(city)&&Validator.isNotEmptyString(address)){

            user.setCountry(country);
            user.setCity(city);
            user.setAddress(address);
            user.setLatitude(homeLatitude);
            user.setLongitude(homeLongitude);

            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();


            // JUST TRY THE WEB SERVICE
            queue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();

            HashMap<String,String> headers = new HashMap<>();
            headers.put("data", gson.toJson(user));



            //GsonRequest
            GsonRequest<Status> gsonRequest = new GsonRequest<>(Urls.WEB_SERVICE_REGISTER_URL, Request.Method.POST,
                    Status.class, headers,
                    // On Success Registeration process
                    new Response.Listener<Status>() {
                        @Override
                        public void onResponse(Status response) {




                            // Successful registeration
                            if(response.getStatus()==1){

                                // to upload image
                                if(imagePath!=null){


                                    uploadPicture();

                                }else{

                                    login();
                                }
                            }
                            // failed to register
                            else{
                                registerProgressDialog.dismiss();
                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.email_already_exist),Toast.LENGTH_LONG).show();
                            }




                        }
                    },
                    // On failed Registeration process
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            // Dismiss the progress dialog
                            registerProgressDialog.dismiss();
                            // show the toast of failure
                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.register_failed),Toast.LENGTH_LONG).show();

                        }
                    }
            );
            // end of request




            // add request to the queue ..
            queue.add(gsonRequest);

        }else{

            registerProgressDialog.dismiss();
            Toast.makeText(this,getResources().getString(R.string.location_required),Toast.LENGTH_LONG).show();

        }





    }

    /**
     * This method used to upload image to server
     */
    private void uploadPicture() {

        // final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
        Ion.with(getApplicationContext())
                .load(Urls.IMAGE_UPLOAD_URL).setLogging("UPLOAD LOGS", Log.DEBUG)
                .setMultipartParameter("email", user.getEmail())
                .setMultipartFile("file", "application/zip", new File(imagePath)).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                if (e != null) {
                    e.printStackTrace();
                }

                Log.i("completed", "completed");
                //Login called here
                registerProgressDialog.dismiss();
                // here we are login
                login();

                if (result != null) {

                    Log.i("success", result.get("message").getAsString());



                }
            }
        });
    }



    /**
     * to get the Real Path of uri
     * @param contentURI
     * @return
     */


    /**
     * this method used to perform login
     */
    private void login(){

        Log.i("validation method ", "I am in the validation method :D ");



        HashMap<String,String> headers = new HashMap<>();
        headers.put("email",user.getEmail());
        headers.put("password",user.getPassword());
        headers.put("macAddress", getMacAddress());



        GsonRequest gsonRequest = new GsonRequest(Urls.WEB_SERVICE_lOGIN_URL, Request.Method.POST, Status.class, headers, new Response.Listener<Status>() {
            @Override
            public void onResponse(Status response) {

                // in case of success login
                if (response.getStatus() == 1){
                    user = response.getUser();
                    Log.i("user first name ", user.getFirstName());

                    SharedPreferenceManager.saveUser(getApplicationContext(), user);

                    // go to home Activity .. based on type
                    // 1 indicates to patient
                    if(user.getType() == 1){
                        Log.i("in optionsActivity",String.valueOf(user.getType()));
                        Intent patientIntent = new Intent(getApplicationContext(),PatientHomeActivity.class);
                        startActivity(patientIntent);
                    }
                    else{

                        Intent relativeIntent = new Intent(getApplicationContext(),RelativeHomeActivity.class);
                        startActivity(relativeIntent);
                    }

                }
                //in case of login failed
                else{
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.login_failed),Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("***error***",error.toString());
                VolleyLog.d("my error*******", "Error: " + error.getMessage());
            }
        });


        queue.add(gsonRequest);



    }// end of ValidateAndLogin method

    /**
     * @return
     * @author Donia
     * this method used to get Mac Address
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public String getMacAddress() {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        String macAddress = mBluetoothAdapter.getAddress();
        // Toast.makeText(getApplicationContext(),macAddress,Toast.LENGTH_LONG).show();
        Log.i("getMacAddress: ", macAddress + ">> " + mBluetoothAdapter.getAddress());
        return macAddress;
    }

}
