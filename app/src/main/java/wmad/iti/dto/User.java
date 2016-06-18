package wmad.iti.dto;

/**
 * Created by atef on 5/16/2016.
 */
import java.sql.Date;

/**
 *
 * @author atef
 * this Class represents the User table DTO .
 */
public class User {



    // Type constants
    public static final int USER_RELATIVE =0;
    public static final int USER_PATIENT =1;

    // Gender Constants
    public static final int USER_MALE =1;
    public static final int USER_FEMALE =0;




    int userId;
    String firstName;
    String lastName;
    Date birthday;
    int gender; // 0 for female 1 for male
    String phoneNumber;
    String email;
    String homeNumber;
    String country;
    String city;
    String address;
    int type; // 0 for relative and 1 for patient
    String password;
    double longitude;
    double latitude;
    String imageUrl;
    String macAddress;

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    /**
     * this method is used to get the gender of user, return constants USER_MALE or USER_FEMALE
     * @return gender
     */
    public int getGender() {
        return gender;
    }

    /**
     * this method is used to set the gender of user, use constants USER_MALE or USER_FEMALE
     * @param gender
     */
    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHomeNumber() {
        return homeNumber;
    }

    public void setHomeNumber(String homeNumber) {
        this.homeNumber = homeNumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * this method is used to know if the user is patient or relative
     * @return USER_RELATIVE for relative;
     * @return USER_PATIENT for patient
     */
    public int getType() {
        return type;
    }

    /**
     * this method is used to set type of user
     * @param type
     *
     */
    public void setType(int type) {
        this.type = type;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }




}