package com.vaslabs.sdc.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.vaslabs.sdc.UserInformation;
import com.vaslabs.sdc.UserPreferences;
import com.vaslabs.sdc.connectivity.SkyDivingEnvironment;
import com.vaslabs.sdc.ui.SpeechCommunicationManager;
import com.vaslabs.sdc.utils.SDConnectivity;
import com.vaslabs.sdc.utils.SkyDiver;
import com.vaslabs.sdc.utils.SkyDiverPositionalComparator;

import android.test.AndroidTestCase;

public class TestSkyDivingEnvironment extends AndroidTestCase {

    public void test_skydiving_environment_engegements() {
        SkyDivingEnvironment sde = SkyDivingEnvironment.getInstance(this.mContext);
        
        UserPreferences up = new UserPreferences();
        up.name = "Android user";
        
        SkyDiver skydiver = new SkyDiver( UserInformation.getUserInformationFromUserPreferences( up ) );
        sde.onNewSkydiverInformation( skydiver );
        
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
        List<SkyDiver> skydivers = new ArrayList<SkyDiver>();
        SkyDiver me = new SkyDiver(UserInformation.getUserInfo( this.mContext ));
        UserPreferences up = new UserPreferences();
        
        SkyDiver joeSD = SkyDiver.serialiseSkyDiverFromString( "Joe:50.00|1014.12|null|null|null" );
        SkyDiver mikeSD = SkyDiver.serialiseSkyDiverFromString( "Mike:60.00|1012.12|null|null|null" );
        SkyDiver nickSD = SkyDiver.serialiseSkyDiverFromString( "Nick:60.00|1012.12|null|null|null" );
        SkyDiver bobSD = SkyDiver.serialiseSkyDiverFromString( "Bob:60.00|1012.12|null|null|null" );
        SkyDiver aliceSD = SkyDiver.serialiseSkyDiverFromString( "Alice:60.00|1012.12|null|null|null" );
        
        skydivers.add( joeSD );
        skydivers.add( mikeSD );
        skydivers.add( nickSD );
        skydivers.add( bobSD );
        skydivers.add( aliceSD );
        
        joeSD.setConnectivityStrength( SDConnectivity.MEDIUM );
        mikeSD.setConnectivityStrength( SDConnectivity.CONNECTION_LOST );
        nickSD.setConnectivityStrength( SDConnectivity.WEAK );
        bobSD.setConnectivityStrength( SDConnectivity.STRONG );
        aliceSD.setConnectivityStrength( SDConnectivity.STRONG );
        
        Collections.sort( skydivers, new SkyDiverPositionalComparator(me) );
        
        for (int i = 1; i < skydivers.size(); i++) {
            assertTrue(skydivers.get( i-1 ).getConnectivityStrengthAsInt() >= 
                        skydivers.get( i ).getConnectivityStrengthAsInt());
        }
        
    }
}
