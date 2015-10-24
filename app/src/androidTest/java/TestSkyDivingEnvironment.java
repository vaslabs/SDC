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
    
    public void test_that_skydivers_are_sorted_via_connectivity_strength() {
        SkyDiver me = new SkyDiver(UserInformation.getUserInfo( this.mContext ));
        UserPreferences up = new UserPreferences();
        
        SkyDiver joeSD = SkyDiver.serialiseSkyDiverFromString( "Joe:50.00|1014.12|null|null|null" );
        SkyDiver mikeSD = SkyDiver.serialiseSkyDiverFromString( "Mike:60.00|1012.12|null|null|null" );
        SkyDiver nickSD = SkyDiver.serialiseSkyDiverFromString( "Nick:60.00|1012.12|null|null|null" );
        SkyDiver bobSD = SkyDiver.serialiseSkyDiverFromString( "Bob:60.00|1012.12|null|null|null" );
        SkyDiver aliceSD = SkyDiver.serialiseSkyDiverFromString( "Alice:60.00|1012.12|null|null|null" );
        
        SkyDivingEnvironment environment = SkyDivingEnvironment.getInstance( this.mContext );
        
        
        joeSD.setConnectivityStrength( SDConnectivity.MEDIUM );
        mikeSD.setConnectivityStrength( SDConnectivity.CONNECTION_LOST );
        nickSD.setConnectivityStrength( SDConnectivity.WEAK );
        bobSD.setConnectivityStrength( SDConnectivity.STRONG );
        aliceSD.setConnectivityStrength( SDConnectivity.STRONG );
        
        environment.onNewSkydiverInfo( joeSD );
        environment.onNewSkydiverInfo( mikeSD );
        environment.onNewSkydiverInfo( nickSD );
        environment.onNewSkydiverInfo( bobSD );
        environment.onNewSkydiverInfo( aliceSD );
        
        
        for (int i = 1; i < environment.getCount(); i++) {
            assertTrue(environment.getItem( i ).getConnectivityStrengthAsInt() >= 
                        environment.getItem( i ).getConnectivityStrengthAsInt());
        }
        
    }
}
