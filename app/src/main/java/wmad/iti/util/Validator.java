package wmad.iti.util;

import android.util.Log;
import android.widget.EditText;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by atef on 5/16/2016.
 */
public class Validator {


    /***
     * this method used to validate edit text and check if it is not empty
     *
     * @param editText
     * @return
     */
    public static boolean isNotEmpty(EditText editText) {

        boolean flag = true;

        if (editText.getText().toString().equals("")) {
            flag = false;
        }

        return flag;
    }

    /**
     * this method used to make sure an EditText is empty
     * @param editText
     * @return
     */
    public static boolean isEmpty(EditText editText) {

        boolean flag = false;

        if (editText.getText().toString().equals("")) {
            flag = true;
        }

        return flag;
    }

    public static boolean isNotEmptyString(String str) {
        boolean flag = true;

        if (str.equals("")||str==null) {
            flag = false;
        }

        return flag;
    }

    /**
     * this method used to validate email edit text and returns true if edittext of email is not empty
     * @param emailEditText
     * @return
     */
    public static boolean isEmail(EditText emailEditText) {

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        boolean flag = true;

        if (emailEditText.getText().toString().isEmpty()) {

            flag = false;


        } else {


            if (!emailEditText.getText().toString().matches(emailPattern)) {


                flag = false;

            }

        }

        return flag;
    }

    /**
     * this method is used to make sure it is a phone number
     * @param phoneNumberEditText
     * @return
     */

    public static boolean isPhoneNumber(EditText phoneNumberEditText){

        boolean flag = false;

        String pattern = "^\\+[0-9]{10,13}$";
        if(phoneNumberEditText.getText().toString().matches(pattern)){
            flag=true;
        }
        return flag;


    }


    /**
     * this method used to make sure it is password which is [6:20] characters
     * @param editText
     * @return
     */
    public  static boolean isPassword(EditText editText){

        boolean flag = false;

        String pattern = "[a-zA-Z0-9._-]{6,20}$";

        if (editText.getText().toString().matches(pattern)){

            flag = true;
        }

        return flag;
    }

    /**
     * this method used to make sure two of EditTexts that are used for password is matched
     * @param passwordEditText
     * @param passwordConfirmEditText
     * @return
     */
    public static boolean isMatchPassword(EditText passwordEditText,EditText passwordConfirmEditText){

        boolean flag = false;

        if (passwordEditText.getText().toString().equals(passwordConfirmEditText.getText().toString())){
            flag = true;
        }

        return flag;

    }


    /**
     * this method used to make sure date is before today
     * @param birthdate
     * @return
     */
    public static boolean isBirthdate(Calendar birthdate){

        boolean flag = true;



        Calendar today =  Calendar.getInstance();


        //CHECK IF DIFFERENCE IS 6 YEARS
        if(today.get(Calendar.YEAR)-birthdate.get(Calendar.YEAR)<6){

            flag=false;
        }

        return flag;

    }

}