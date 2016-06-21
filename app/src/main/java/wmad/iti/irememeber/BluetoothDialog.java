package wmad.iti.irememeber;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.mikhaellopez.circularimageview.CircularImageView;

import wmad.iti.constants.Urls;
import wmad.iti.dto.Relative;
import wmad.iti.model.ConnectionDetector;
import wmad.iti.model.MySingleton;

public class BluetoothDialog extends AppCompatActivity {

    CircularImageView photoRelative;
    TextView fullName;
    TextView relation;
    Button closeBtn;

    ImageLoader mImageLoader;
    MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bluetooth_dialog);
        photoRelative = (CircularImageView) findViewById(R.id.relativePhoto);
        fullName = (TextView) findViewById(R.id.fullName);
        relation = (TextView) findViewById(R.id.relation);
        closeBtn=(Button)findViewById(R.id.closeBtn);
        //to checck connection to internet to delete patient

        Intent intent = getIntent();

        String full=intent.getStringExtra("fullName");
        String image=intent.getStringExtra("image");
        int rel=intent.getIntExtra("relation",0);
        Log.i( "onCreate: ",full);
        Log.i( "onCreate: ",image);
        Log.i( "onCreate: ",rel+"");



//        Log.i("onCreate: ", relative.getFirstName());
        // if(!relative.getFirstName().equals("")){
        fullName.setText(full);
        relation.setText(getRelation(rel));
        mImageLoader = MySingleton.getInstance(getApplicationContext()).getImageLoader();
        mImageLoader.get(image, ImageLoader.getImageListener(photoRelative, 0, 0));
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer = MediaPlayer.create(this, R.raw.music1);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.stop();
                finish();
            }
        });

//        try {
//            Thread.sleep(60000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

//        PackageManager pm = getPackageManager();
//        pm.setComponentEnabledSetting(new ComponentName(this,BluetoothDialog.class),
//                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

       // finish();

    }

    public String getRelation(int position){
        String relation = "";
        switch (position) {
            case 20://Aunt (father side)
                relation="Aunt (father side)";
                break;
            case 21://Aunt (Mother side)
                relation="Aunt (Mother side)";
                break;
            case 7: //Brother
                relation="Brother";
                break;
            case 39://Brother-in-law
                relation="Brother-in-law";
                break;
            case 25://Cousin (female) - (Mother side)
                relation="Cousin (female) - (Mother side)";
                break;
            case 24://Cousin (female)- (Father side)
                relation="Cousin (female)- (Father side)";
                break;
            case 22://Cousin (male) -(Father side)
                relation="Cousin (male) -(Father side)";
                break;
            case 23://Cousin (male) -(Mother side)
                relation="Cousin (male) -(Mother side)";
                break;
            case 6://Daughter
                relation="Daughter";
                break;
            case 38://Daughter-in-law
                relation="Daughter-in-law";
                break;
            case 3://Father
                relation="Father";
                break;
            case 35://Father-in-law
                relation="Father-in-law";
                break;
            case 17://Grandchildren
                relation="Grandchildren";
                break;
            case 16://Granddaughter
                relation="Granddaughter";
                break;
            case 13://Grandfather
                relation="Grandfather";
                break;
            case 14://Grandmother
                relation="Grandmother";
                break;
            case 15://Grandson
                relation="Grandson";
                break;

            case 47://Half-brother
                relation="Half-brother";
                break;
            case 48://Half-sister
                relation="Half-sister";
                break;
            case 9://Husband
                relation="Husband";
                break;
            case 4://mother
                relation="mother";
                break;
            case 36://Mother-in-law
                relation="Mother-in-law";
                break;
            case 31://Nephew (brother's son)
                relation="Nephew (brother's son)";
                break;
            case 32://Nephew (sister's son)
                relation="Nephew (sister's son)";
                break;
            case 33://Niece (brother's daughter)
                relation="Niece (brother's daughter)";
                break;
            case 34://Niece (brother's daughter)
                relation="Niece (brother's daughter)";
                break;
            case 8://Sister
                relation="Sister";
                break;
            case 40://Siter-in-law
                relation="Siter-in-law";
                break;
            case 5://Son
                relation="Son";
                break;
            case 37://Son-in-law
                relation="Son-in-law";
                break;
            case 46://Stepbrother
                relation="Stepbrother";
                break;
            case 44://Stepdaughter
                relation="Stepdaughter";
                break;
            case 41://Stepfather
                relation="Stepfather";
                break;
            case 42://Stepmother
                relation="Stepmother";
                break;
            case 45://Stepsister
                relation="Stepsister";
                break;
            case 43://Stepson
                relation="Stepson";
                break;
            case 18://Uncle (Father side)
                relation="Uncle (Father side)";
                break;
            case 19://Uncle (Mother side)
                relation="Uncle (Mother side)";
                break;
            case 10://wife
                relation="wife";
                break;
            default:
                relation="default";
                break;
        }
        return relation;
    }


}
