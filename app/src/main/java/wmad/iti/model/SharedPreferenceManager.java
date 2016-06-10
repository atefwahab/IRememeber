package wmad.iti.model;

import android.content.Context;
import android.content.SharedPreferences;

import java.sql.Date;
import java.util.ArrayList;

import wmad.iti.dto.Relative;
import wmad.iti.dto.User;

/**
 * Created by atef on 5/19/2016.
 */
public abstract class SharedPreferenceManager {

    public static final String LOGIN_SHARED_PREFERENCE = "loginSharedPreference";
    public static final Date user_date = new Date(1993,20,9);
    public static final String GET_PATIENTS_SHARED_PREFRENCE = "getPatientsSharedPrefrence";
    public static final String GET_RELATIVES_SHARED_PREFRENCE = "getRelativesSharedPrefrence";

    /**
     * Author Nihal
     * this method is used to save data of user
     * @param context
     * @param user
     */
    public static void saveUser(Context context , User user){

        // to get thr shared preference
        SharedPreferences sharedPreferences= context.getSharedPreferences(LOGIN_SHARED_PREFERENCE, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //save user's first name
        editor.putString("firstName", user.getFirstName());
        //save user's last name
        editor.putString("lastName", user.getLastName());
        //save user's email
        editor.putString("email", user.getEmail());
        //save user's address
        editor.putString("address", user.getAddress());
        // save user's city
        editor.putString("city", user.getCity());
        //save user's country
        editor.putString("country", user.getCountry());
        //save user's image url
        editor.putString("image", user.getImageUrl());
        //save user's home number
        editor.putString("homeNumber", user.getHomeNumber());
        //save user's phone number
        editor.putString("phoneNumber", user.getPhoneNumber());
        //save user's gender
        editor.putInt("gender", user.getGender());
        //save user's id
        editor.putInt("id", user.getUserId());
        //save user's type
        editor.putInt("type", user.getType());
        //save user's password
        editor.putString("password", user.getPassword());
        //save user's longitude
        editor.putString("longitude", String.valueOf(user.getLongitude()));
        //save user's latitude
        editor.putString("latitude", String.valueOf(user.getLatitude()));
        // save user's birthday
        editor.putString("birthday", String.valueOf(user.getBirthday()));

        //commit
        editor.commit();
    }

    /**
     * Author Nihal
     * this method is used to get the data that is cached
     * @param context
     * @return
     */
    public static User getUser(Context context){

        User user = new User();
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_SHARED_PREFERENCE, context.MODE_PRIVATE);
        sharedPreferences.contains(LOGIN_SHARED_PREFERENCE);
        //get user's first name
        user.setFirstName(sharedPreferences.getString("firstName", ""));
        //get user's last name
        user.setLastName(sharedPreferences.getString("lastName", ""));
        //get user's email
        user.setEmail(sharedPreferences.getString("email", ""));
        //get user's address
        user.setAddress(sharedPreferences.getString("address", ""));
        //get user's city
        user.setCity(sharedPreferences.getString("city", ""));
        //get user's country
        user.setCountry(sharedPreferences.getString("country", ""));
        //get user's home number
        user.setHomeNumber(sharedPreferences.getString("homeNumber", ""));
        //get user's phone number
        user.setPhoneNumber(sharedPreferences.getString("phoneNumber", ""));
        //get user's image
        user.setImageUrl(sharedPreferences.getString("image", ""));
        //get user's password
        user.setPassword(sharedPreferences.getString("password", ""));
        //get user's longitude
        user.setLongitude(Double.parseDouble(sharedPreferences.getString("longitude", "0.0")));
        //get user's latitude
        user.setLatitude(Double.parseDouble(sharedPreferences.getString("latitude", "0.0")));
        //get user's id
        user.setUserId(sharedPreferences.getInt("id", 0));
        //get user's type
        user.setType(sharedPreferences.getInt("type", 2));
        //get user's gender
        user.setGender(sharedPreferences.getInt("gender", 0));
        //get user's birthday
        user.setBirthday(java.sql.Date.valueOf(sharedPreferences.getString("birthday", user_date.toString())));
        return user;
    }

    /**
     * Author Nihal
     * this method is used to check if the caching is happened or not
     * @param context
     * @return
     */
    public static boolean isCached(Context context){

        boolean flag = false;
        SharedPreferences sharedPreferences= context.getSharedPreferences(LOGIN_SHARED_PREFERENCE,Context.MODE_PRIVATE );
        if(sharedPreferences.contains("type")){
            flag = true;
        }
        return flag;
    }

    /**
     * Author Nihal
     * this method is used to get email if it is cached
     * @param context
     * @return
     */
    public static String getEmail(Context context){

         String user_email = null;
        SharedPreferences sharedPreferences= context.getSharedPreferences(LOGIN_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        if (sharedPreferences.contains("email")){
            user_email = sharedPreferences.getString("email","");
        }
        return user_email;
    }





    /**
     * Author Doaa
     * This method is used to save data of patient
     * @param context
     * @param user
     */
    public static boolean savePatients(Context context, User[] user) {

        int count=0;

        // to get the shared preference
        SharedPreferences sharedPreferences = context.getSharedPreferences(GET_PATIENTS_SHARED_PREFRENCE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = 0; i < user.length; i++) {

            count++;
            //save patient's first name
            editor.putString("firstName"+count, user[i].getFirstName());
            //save patient's last name
            editor.putString("lastName" + count, user[i].getLastName());
            //save patient's email
            editor.putString("email"+count,user[i].getEmail());

        }
        editor.putInt("count",count);

        return  editor.commit();
    }

    /**
     * Author Doaa
     * This method used to get data of patient
     * @param context
     * @return
     */
    public static ArrayList<User> getPatients(Context context) {
        int count=0;
        int numofKey=0;
        ArrayList<User> users =new ArrayList<>();

        // 1 - check shared has count wla la2
        SharedPreferences sharedPreferences = context.getSharedPreferences(GET_PATIENTS_SHARED_PREFRENCE,Context.MODE_PRIVATE);
        // 2 - get int count ..
        if(sharedPreferences.contains("count")){

            count=sharedPreferences.getInt("count", 0);

            // 4 - loop on the array and get patients
            for(int i=0;i<count;i++){

                numofKey++;

                String firstName=sharedPreferences.getString("firstName"+numofKey,"");
                String lastName =sharedPreferences.getString("lastName"+numofKey,"");
                String email=sharedPreferences.getString("email"+numofKey,"");
                User user = new User();
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setEmail(email);
                users.add(user);


            }
        }

        return users;

    }

    /**
     * Author Doaa
     * This method is used to save data of relatives
     * @param context
     * @param relative
     */
    public static boolean saveRelatives(Context context, Relative[] relative) {

        int count=0;

        // to get the shared preference
        SharedPreferences sharedPreferences = context.getSharedPreferences(GET_RELATIVES_SHARED_PREFRENCE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = 0; i < relative.length; i++) {

            count++;
            //save relative's first name
            editor.putString("firstName" + count, relative[i].getFirstName());
            //save relative's last name
            editor.putString("lastName" + count, relative[i].getLastName());
            //save relative's phone number
            editor.putString("phoneNumber" + count, relative[i].getPhoneNumber());
            //save relative's relationship position
            editor.putInt("familyPosition" + count, relative[i].getRelationshipPosition());
            //save relative's address
            editor.putString("address"+count, relative[i].getAddress());
        }
        editor.putInt("count",count);

        return  editor.commit();
    }

    /**
     * Author Doaa
     * This method used to get data of relatives
     * @param context
     * @return
     */
    public static ArrayList<Relative> getRelatives(Context context) {
        int count=0;
        int numofKey=0;
        ArrayList<Relative> relatives =new ArrayList<>();

        // 1 - check shared has count wla la2
        SharedPreferences sharedPreferences = context.getSharedPreferences(GET_RELATIVES_SHARED_PREFRENCE,Context.MODE_PRIVATE);
        // 2 - get int count ..
        if(sharedPreferences.contains("count")){

            count=sharedPreferences.getInt("count", 0);

            // 4 - loop on the array and get patients
            for(int i=0;i<count;i++){

                numofKey++;

                String firstName=sharedPreferences.getString("firstName"+numofKey,"");
                String lastName =sharedPreferences.getString("lastName"+numofKey,"");
                String phoneNumber=sharedPreferences.getString("phoneNumber"+numofKey,"");
                int familyPosition=sharedPreferences.getInt("familyPosition"+numofKey,0);
                String address=sharedPreferences.getString("address"+numofKey,"");
                Relative relative = new Relative();
                relative.setFirstName(firstName);
                relative.setLastName(lastName);
                relative.setPhoneNumber(phoneNumber);
                relative.setRelationshipPosition(familyPosition);
                relative.setAddress(address);
                relatives.add(relative);


            }
        }

        return relatives;

    }







}
