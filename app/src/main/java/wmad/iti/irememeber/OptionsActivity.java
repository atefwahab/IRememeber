package wmad.iti.irememeber;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import wmad.iti.constants.Urls;
import wmad.iti.dto.Status;
import wmad.iti.dto.User;
import wmad.iti.model.ConnectionDetector;
import wmad.iti.model.GsonRequest;
import wmad.iti.model.MySingleton;
import wmad.iti.model.SharedPreferenceManager;
import wmad.iti.util.Validator;

public class OptionsActivity extends AppCompatActivity {

    EditText emailEditText,passwordEditText;
    TextInputLayout inputLayoutEmail,inputLayoutPassword;
    Button loginButton;

    String emailEditTextValue,passwordEditTextValue;
    GsonRequest gsonRequest;
    public Map<String, String> headers;
    RequestQueue requestQueue;
    Intent registerIntent;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        emailEditText = (EditText)findViewById(R.id.emailEditText);
        passwordEditText = (EditText)findViewById(R.id.passwordEditText);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.inputLayoutEmail);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.inputLayoutPassword);
        loginButton = (Button) findViewById(R.id.loginButton);

        headers = new HashMap<String,String>();
        //when click the login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkInternet(getApplicationContext());

            }
        });

    }

    /**
     * this method is used to check internet connection
     * @param context
     */
    public void checkInternet(Context context){
        ConnectionDetector connectionDetector = new ConnectionDetector(context);
        if (connectionDetector.isConnectingToInternet()){
            validateAndLogin();
        }
        else{
            // here if there is not internet connection the snack bar will be displayed
            Snackbar snackbar = Snackbar.make(findViewById(R.id.optionActivity),getResources().getString(R.string.no_internt_msg),Snackbar.LENGTH_LONG);
            snackbar.show();
        }


    }
    /**
     * this method used to validate the Edittexts and perform login process PS. it is called on click of login button through XML
     *
     */
    public void validateAndLogin(){

        Log.i("validation method ", "I am in the validation method :D ");


        // check if email is valid
        if(Validator.isEmail(emailEditText) ){

            inputLayoutEmail.setErrorEnabled(false);

            // check password is correct or not
            if(Validator.isPassword(passwordEditText)){

                progressDialog = ProgressDialog.show(OptionsActivity.this,"","Loading..",false,false);
                inputLayoutPassword.setErrorEnabled(false);
                emailEditTextValue=emailEditText.getText().toString();
                passwordEditTextValue=passwordEditText.getText().toString();
                headers.put("email",emailEditTextValue);
                headers.put("password",passwordEditTextValue);
               // headers.put("macAddress", getMacAddress());

                requestQueue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();

                gsonRequest = new GsonRequest(Urls.WEB_SERVICE_lOGIN_URL, Request.Method.POST, Status.class, headers, new Response.Listener<Status>() {
                    @Override
                    public void onResponse(Status response) {
                        progressDialog.dismiss();
                        // in case of success login
                        if (response.getStatus() == 1){
                            User user = response.getUser();
                            Log.i("user first name ", user.getFirstName());

                            SharedPreferenceManager.saveUser(getApplicationContext(),user);

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
                        progressDialog.dismiss();
                        Log.i("***error***",error.toString());
                        VolleyLog.d("my error*******", "Error: " + error.getMessage());
                    }
                });


                requestQueue.add(gsonRequest);

            }
            //in case of password invalid
            else{

                inputLayoutPassword.setError(getResources().getString(R.string.invalid_password));
            }

        }//end of first if which checks on validation of email
        else{

            inputLayoutEmail.setError(getResources().getString(R.string.invalid_email));
        }

    }// end of ValidateAndLogin method

    @Override
    protected void onStop() {
        super.onStop();
        this.finish();
    }

    public void goToRegister(View view){


        registerIntent = new Intent(getApplicationContext(),RegisterActivity.class);
        startActivity(registerIntent);
    }


}
