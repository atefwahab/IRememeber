package wmad.iti.dto;

import java.util.ArrayList;
import java.util.List;

import wmad.iti.irememeber.R;

/**
 * Created by Nihal on 16/05/2016.
 */
public class RelativeHomePage {

    private String relativeTitle;
    private int relativeImageID;

    public String getRelativeTitle() {
        return relativeTitle;
    }

    public int getRelativeImageID() {
        return relativeImageID;
    }

    public void setRelativeTitle(String relativeTitle) {
        this.relativeTitle = relativeTitle;
    }

    public void setRelativeImageID(int relativeImageID) {
        this.relativeImageID = relativeImageID;
    }



    public static List<RelativeHomePage> getRelativeData(){

        List<RelativeHomePage> data = new ArrayList<>();

        int [] relativeImages = {
                R.drawable.personal_info,
                R.drawable.add_patient,
                R.drawable.patients

        };

        String [] relativeIconsTitle ={"Profile","Find Patients","Patients"};

        for(int i = 0 ; i< relativeImages.length;i++)
        {
            RelativeHomePage relativeHomePage = new RelativeHomePage();
            relativeHomePage.setRelativeTitle(relativeIconsTitle[i]);
            relativeHomePage.setRelativeImageID(relativeImages[i]);
            data.add(relativeHomePage);
        }

        return data;
    }
}
