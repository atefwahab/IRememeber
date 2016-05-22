package wmad.iti.model;

import android.content.Context;
import android.content.SharedPreferences;

import java.sql.Date;

import wmad.iti.dto.User;

/**
 * Created by atef on 5/19/2016.
 */
public abstract class SharedPreferenceManager {

    public static final String LOGIN_SHARED_PREFERENCE = "loginSharedPreference";
    public static final Date user_date = new Date(1993,20,9);

    /**
     * this method is used to save data of user
     * @param context
     * @param user
     */
    public static void saveUser(Context context , User user){

        // to get thr shared preference
        SharedPreferences sharedPreferences= context.getSharedPreferences(LOGIN_SHARED_PREFERENCE, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //save user's first name
        editor.putString("firstName",user.getFirstName());
        //save user's last name
        editor.putString("lastName", user.getLastName());
        //save user's email
        editor.putString("email",user.getEmail());
        //save user's address
        editor.putString("address",user.getAddress());
        // save user's city
        editor.putString("city",user.getCity());
        //save user's country
        editor.putString("country",user.getCountry());
        //save user's image url
        editor.putString("image",user.getImageUrl());
        //save user's home number
        editor.putString("homeNumber", user.getHomeNumber());
        //save user's phone number
        editor.putString("phoneNumber", user.getPhoneNumber());
        //save user's gender
        editor.putInt("gender", user.getGender());
        //save user's id
        editor.putInt("id",user.getUserId());
        //save user's type
        editor.putInt("type", user.getType());
        //save user's password
        editor.putString("password", user.getPassword());
        //save user's longitude
        editor.putString("longitude",String.valueOf(user.getLongitude()));
        //save user's latitude
        editor.putString("latitude", String.valueOf(user.getLatitude()));
        // save user's birthday
        editor.putString("birthday", String.valueOf(user.getBirthday()));

        //commit
        editor.commit();
    }

    /**
     * this method is used to get the data that is cached
     * @param context
     * @return
     */
    public static User getUser(Context context){

        User user = new User();
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_SHARED_PREFERENCE, 0);
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
}
