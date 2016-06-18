package wmad.iti.dto;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Doaa on 6/13/2016.
 */
public class Memory implements Parcelable{
    int memoryId;
    String memoryText;
    String imageUrl;
    String video_url;
    String dateTime;
    Double longitude;
    Double latitude;
    String address;
    User user;
    public Memory(){}

    protected Memory(Parcel in) {
        memoryId = in.readInt();
        memoryText = in.readString();
        imageUrl = in.readString();
        video_url = in.readString();
        dateTime = in.readString();
        address = in.readString();
        city = in.readString();
        country = in.readString();
    }

    public static final Creator<Memory> CREATOR = new Creator<Memory>() {
        @Override
        public Memory createFromParcel(Parcel in) {
            return new Memory(in);
        }

        @Override
        public Memory[] newArray(int size) {
            return new Memory[size];
        }
    };

    public User getUser() {
        return user;
    }

    public User setUser(User user) {
        this.user = user;
        return user;
    }



    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getMemoryId() {
        return memoryId;
    }

    public void setMemoryId(int memoryId) {
        this.memoryId = memoryId;
    }

    public String getMemoryText() {
        return memoryText;
    }

    public void setMemoryText(String memoryText) {
        this.memoryText = memoryText;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    String city;
    String country;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(memoryId);
        dest.writeString(memoryText);
        dest.writeString(imageUrl);
        dest.writeString(video_url);
        dest.writeString(dateTime);
        dest.writeString(address);
        dest.writeString(city);
        dest.writeString(country);
    }
}
