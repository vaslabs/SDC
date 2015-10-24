import com.vaslabs.sdc.UserInformation;
import com.vaslabs.sdc.UserPreferences;
import com.vaslabs.sdc.connectivity.SkyDivingEnvironment;
import com.vaslabs.sdc.ui.SpeechCommunicationManager;
import com.vaslabs.sdc.utils.SDConnectivity;
import com.vaslabs.sdc.utils.SkyDiver;

import android.test.AndroidTestCase;

public class TestSkyDivingEnvironment extends AndroidTestCase {

    public void test_skydiving_environment_engagements() {
        SkyDivingEnvironment sde = SkyDivingEnvironment.getInstance(this.mContext);
        
        UserPreferences up = new UserPreferences();
        up.name = "Android user";
        
        SkyDiver skydiver = new SkyDiver( UserInformation.getUserInformationFromUserPreferences( up ) );
        sde.onNewSkydiverInfo( skydiver );
        
        try {
            Thread.sleep( 2000 );
        } catch ( InterruptedException e1 ) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        while (SpeechCommunicationManager.getInstance().isTalking()) {
            try {
                Thread.sleep( 2000 );
            } catch ( InterruptedException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        
    }

}
