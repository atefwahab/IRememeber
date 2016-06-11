package wmad.iti.requests;

/*
*author donia
* send request from relative to patient
 */




import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import wmad.iti.constants.Urls;
import wmad.iti.dto.Status;
import wmad.iti.dto.User;
import wmad.iti.irememeber.R;
import wmad.iti.irememeber.RelativeHomeActivity;
import wmad.iti.model.ConnectionDetector;
import wmad.iti.model.GsonRequest;
import wmad.iti.model.MySingleton;
import wmad.iti.model.SharedPreferenceManager;


public class SendRequest extends AppCompatActivity {
    private static SendRequest inst;
    private Button add;
    static private Context context;
    private EditText patientEmail;
    private Spinner spinnerRelation;
    private TextView relationTextView;
     private String relativeEmail;
    private ListView list;
    private int relation;
    private ArrayList<String> fnames;
    private ArrayList<String> lnames;
    private ArrayList<String> emails;
    private ArrayList<String> images;
    ConnectionDetector  connectionDetector;
    ProgressDialog progressDialog;
    Toolbar toolbar;

    /*
* return instance from this class
 */
    public static SendRequest instance() {
        return inst;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.send_request);
     //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.navigation_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
       // txtDisplay = (TextView) findViewById(R.id.txtDisplay);
        add=(Button) findViewById(R.id.done);
        spinnerRelation=(Spinner) findViewById(R.id.spinnerRelation);
        patientEmail=(EditText) findViewById(R.id.patientEmail);
        list=(ListView) findViewById(R.id.listPatient);
        relationTextView=(TextView)findViewById(R.id.relationTextView);

        context=getBaseContext();

        patientEmail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showSoftKeyboard(v);
                return false;
            }
        });



        /*
        * the data of spinner relation
         */

        List<String> categories = new ArrayList<String>();
        categories.add(getResources().getString(R.string.Aunt_father_side)); //20
        categories.add(getResources().getString(R.string.Aunt_Mother_side));//21
        categories.add(getResources().getString(R.string.Brother));//7
        categories.add(getResources().getString(R.string.Brother_in_law)); //39
        categories.add(getResources().getString(R.string.Cousin_female_Mother_side));//25
        categories.add(getResources().getString(R.string.Cousin_female_Father_side));//24
        categories.add(getResources().getString(R.string.Cousin_male_Father_side));//22
        categories.add(getResources().getString(R.string.Cousin_male_Mother_side));//23
        categories.add(getResources().getString(R.string.Daughter));//6
        categories.add(getResources().getString(R.string.Daughter_in_law));//38
        categories.add(getResources().getString(R.string.Father));//3
        categories.add(getResources().getString(R.string.Father_in_law));//35
        categories.add(getResources().getString(R.string.Grandchildren));//17
        categories.add(getResources().getString(R.string.Granddaughter));//16
        categories.add(getResources().getString(R.string.Grandfather));//13
        categories.add(getResources().getString(R.string.Grandmother));//14
        categories.add(getResources().getString(R.string.Grandson));//15
        categories.add(getResources().getString(R.string.Half_brother));//47
        categories.add(getResources().getString(R.string.Half_sister));//48
        categories.add(getResources().getString(R.string.Husband));//9
        categories.add(getResources().getString(R.string.Mother));//4
        categories.add(getResources().getString(R.string.Mother_in_law));//36
        categories.add(getResources().getString(R.string.Nephew_brother_son));//31
        categories.add(getResources().getString(R.string.Nephew_sister_son));//32
        categories.add(getResources().getString(R.string.Niece_brother_daughter));//33
        categories.add(getResources().getString(R.string.Niece_sister_daughter));//34
        categories.add(getResources().getString(R.string.Sister));//8
        categories.add(getResources().getString(R.string.Siter_in_law));//40
        categories.add(getResources().getString(R.string.Son));//5
        categories.add(getResources().getString(R.string.Son_in_law));//37
        categories.add(getResources().getString(R.string.Stepbrother));//46
        categories.add(getResources().getString(R.string.Stepdaughter));//44
        categories.add(getResources().getString(R.string.Stepfather));//41
        categories.add(getResources().getString(R.string.Stepmother));//42
        categories.add(getResources().getString(R.string.Stepsister));//45
        categories.add(getResources().getString(R.string.Stepsister));//43
        categories.add(getResources().getString(R.string.Uncle_Father_side));//18
        categories.add(getResources().getString(R.string.Uncle_Mother_side));//19
        categories.add(getResources().getString(R.string.wife));//10

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerRelation.setAdapter(dataAdapter);

        spinnerRelation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
//                System.out.println("///////////////////// position"+spinnerRelation.getSelectedItemPosition());

               relation=getRelation(position);
                add.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                connectionDetector = new ConnectionDetector(context);
                if (connectionDetector.isConnectingToInternet()) {

                    String patient = patientEmail.getText().toString();
                    // get email from shared prefrance
                    relativeEmail = SharedPreferenceManager.getEmail(context);
                   // Toast.makeText(getApplicationContext(), "email from shared prefrance > " + relativeEmail, Toast.LENGTH_LONG).show();


                    HashMap<String, String> header = new HashMap<String, String>();
                    header.put("patientEmail", patient);
                    header.put("relativeEmail", relativeEmail);
                    header.put("familyPosition", relation + "");
                    RequestQueue queue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();


                    GsonRequest jsonRequest = new GsonRequest(Urls.WEB_SERVICE_SEND_REQUEST_URL, Request.Method.POST, Status.class, header, new Response.Listener<Status>() {
                        @Override
                        public void onResponse(Status response) {

                          Toast.makeText(SendRequest.instance(), getResources().getText(R.string.requestSend), Toast.LENGTH_LONG).show();
                            Intent intent=new Intent(getApplicationContext(), RelativeHomeActivity.class);
                            startActivity(intent);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(SendRequest.instance(), "Response => " + error.toString(), Toast.LENGTH_LONG).show();
                            Log.i("onErrorResponse: ", error.getMessage());

                        }
                    });


                    queue.add(jsonRequest);
                } else {

                    // here if there is not internet connection the snack bar will be displayed
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.sendRequestActivity),getResources().getString(R.string.no_internt_msg),Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });




    }

    /*
    * send position in spinner then return number of relation based on database
    *
     */
    public int getRelation(int position){
        int relation = 0;
        switch (position) {
            case 0://Aunt (father side)
                relation=20;
                break;
            case 1://Aunt (Mother side)
                relation=21;
                break;
            case 2: //Brother
                relation=7;
                break;
            case 3://Brother-in-law
                relation=39;
                break;
            case 4://Cousin (female) - (Mother side)
                relation=25;
                break;
            case 5://Cousin (female)- (Father side)
                relation=24;
                break;
            case 6://Cousin (male) -(Father side)
                relation=22;
                break;
            case 7://Cousin (male) -(Mother side)
                relation=23;
                break;
            case 8://Daughter
                relation=6;
                break;
            case 9://Daughter-in-law
                relation=38;
                break;
            case 10://Father
                relation=3;
                break;
            case 11://Father-in-law
                relation=35;
                break;
            case 12://Grandchildren
                relation=17;
                break;
            case 13://Granddaughter
                relation=16;
                break;
            case 14://Grandfather
                relation=13;
                break;
            case 15://Grandmother
                relation=14;
                break;
            case 16://Grandson
                relation=15;
                break;

            case 17://Half-brother
                relation=47;
                break;
            case 18://Half-sister
                relation=48;
                break;
            case 19://Husband
                relation=9;
                break;
            case 20://mother
                relation=4;
                break;
            case 21://Mother-in-law
                relation=36;
                break;
            case 22://Nephew (brother's son)
                relation=31;
                break;
            case 23://Nephew (sister's son)
                relation=32;
                break;
            case 24://Niece (brother's daughter)
                relation=33;
                break;
            case 25://Niece (brother's daughter)
                relation=34;
                break;
            case 26://Sister
                relation=8;
                break;
            case 27://Siter-in-law
                relation=40;
                break;
            case 28://Son
                relation=5;
                break;
            case 29://Son-in-law
                relation=37;
                break;
            case 30://Stepbrother
                relation=46;
                break;
            case 31://Stepdaughter
                relation=44;
                break;
            case 32://Stepfather
                relation=41;
                break;
            case 33://Stepmother
                relation=42;
                break;
            case 34://Stepsister
                relation=45;
                break;
            case 35://Stepson
                relation=43;
                break;
            case 36://Uncle (Father side)
                relation=18;
                break;
            case 37://Uncle (Mother side)
                relation=19;
                break;
            case 38://wife
                relation=10;
                break;
        }
        return relation;
    }

    @Override
    protected void onStart() {
        super.onStart();
        inst = this;
        patientEmail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (patientEmail.getRight() - patientEmail.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                         connectionDetector = new ConnectionDetector(context);
                        if (connectionDetector.isConnectingToInternet()) {
                            // your action here
                            progressDialog = ProgressDialog.show(SendRequest.this,"",getResources().getString(R.string.wait));
                            RequestQueue queue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();


                            HashMap<String, String> header = new HashMap<String, String>();
                            header.put("email", patientEmail.getText().toString());
                            GsonRequest jsonRequest = new GsonRequest(Urls.WEB_SERVICE_SEARCH_PATIENT_URL, Request.Method.POST, Status.class, header, new Response.Listener<Status>() {
                                @Override
                                public void onResponse(Status status) {
                                    progressDialog.dismiss();
                                    String fname = null;
                                    String lname = null;
                                    String email = null;
                                    String imageUrl = null;
                                    if(status.getStatus()==1) {
                                        User response = status.getUser();
                                        fname = response.getFirstName();
                                        lname = response.getLastName();
                                        email = response.getEmail();
                                        imageUrl = response.getImageUrl();

                                        if (!fname.equals("null")) {
                                            fnames = new ArrayList<>();
                                            lnames = new ArrayList<>();
                                            emails = new ArrayList<>();
                                            images = new ArrayList<>();
                                            fnames.add(fname);
                                            lnames.add(lname);
                                            emails.add(email);
                                            images.add(imageUrl);
                                            list.setAdapter(new CustomAdapterSearchList(context, fnames, emails, images));
                                            hideSoftKeyboard();
                                        }

                                    }else{
                                        list.setVisibility(View.GONE);
                                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.no_result),Toast.LENGTH_LONG).show();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    progressDialog.dismiss();
                                    Log.i("onErrorResponse: ", error.getMessage());

                                }
                            });

                            queue.add(jsonRequest);

                            list.setVisibility(View.VISIBLE);

                        }else{
                            // here if there is not internet connection the snack bar will be displayed
                            Snackbar snackbar = Snackbar.make(findViewById(R.id.sendRequestActivity), getResources().getString(R.string.no_internt_msg), Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                        return true;
                    }
                }
                return false;

            }
        });
    }


    public  void setEmailInEditText(String patientEmailSelected){
        patientEmail.setText(patientEmailSelected);
        relationTextView.setVisibility(View.VISIBLE);
        spinnerRelation.setVisibility(View.VISIBLE);

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Shows the soft keyboard
     */
    public void showSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }

}
