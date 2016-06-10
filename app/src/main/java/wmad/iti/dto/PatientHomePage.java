package wmad.iti.dto;

import android.content.Context;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;

import wmad.iti.irememeber.PatientHomeActivity;
import wmad.iti.irememeber.R;

/**
 * Created by Nihal on 15/05/2016.
 */
public class PatientHomePage{

    private String Title;
    private int imageID;

    public void setTitle(String title) {
        Title = title;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

    public String getTitle() {
        return Title;
    }

    public int getImageID() {
        return imageID;
    }

    public static List<PatientHomePage> getData(){

        List<PatientHomePage> data = new ArrayList<>();

        int [] images = {
                R.drawable.personal_info,
                R.drawable.recieve_request,
                R.drawable.relatives,
                R.drawable.home,
                R.drawable.add_memory,
                R.drawable.settings,
                R.drawable.panic_mode
        };

      //  String [] iconsTitle ={"Profile","Requests","Relatives","Home","Memories","Settings","Panic"};
        String[] iconsTitle=  PatientHomeActivity.instance().getResources().getStringArray(R.array.patient_home_arr);

                for(int i = 0 ; i< images.length;i++)
                {
                    PatientHomePage patientHomePage = new PatientHomePage();
                    patientHomePage.setTitle(iconsTitle[i]);
                    patientHomePage.setImageID(images[i]);
                    data.add(patientHomePage);

                }

        return data;
    }
}
