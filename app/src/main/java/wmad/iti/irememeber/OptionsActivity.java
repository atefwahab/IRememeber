package wmad.iti.irememeber;

import android.content.Intent;
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
import wmad.iti.model.GsonRequest;
import wmad.iti.model.MySingleton;
import wmad.iti.model.SharedPreferenceManager;
import wmad.iti.util.Validator;

public class OptionsActivity extends AppCompatActivity {

    EditText emailEditText,passwordEditText;
    TextInputLayout inputLayoutEmail , inputLayoutPassword;

    String password,emailEditTextValue,passwordEditTextValue;
    GsonRequest gsonRequest;
    int userType;
    public Map<String, String> headers;
    RequestQueue requestQueue;
    Intent registerIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        emailEditText = (EditText)findViewById(R.id.emailEditText);
        passwordEditText = (EditText)findViewById(R.id.passwordEditText);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.inputLayoutEmail);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.inputLayoutPassword);

        headers = new HashMap<String,String>();

    }

    /**
     * this method used to validate the Edittexts and perform login process PS. it is called on click of login button through XML
     * @param view
     */
    public void validateAndLogin(View view){

        Log.i("validation method ", "I am in the validation method :D ");
        // check if email is valid
        if(Validator.isEmail(emailEditText) ){

            inputLayoutEmail.setErrorEnabled(false);

            // check password is correct or not
            if(Validator.isPassword(passwordEditText)){

                inputLayoutPassword.setErrorEnabled(false);
                emailEditTextValue=emailEditText.getText().toString();
             passwordEditTextValue=passwordEditText.getText().toString();
             headers.put("email",emailEditTextValue);
             headers.put("password",passwordEditTextValue);

           requestQueue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();

            gsonRequest = new GsonRequest(Urls.WEB_SERVICE_lOGIN_URL, Request.Method.POST, Status.class, headers, new Response.Listener<Status>() {
                @Override
                public void onResponse(Status response) {
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
