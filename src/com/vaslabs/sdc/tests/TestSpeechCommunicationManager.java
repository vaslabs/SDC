package com.vaslabs.sdc.tests;

import com.vaslabs.sdc.ui.OnSpeechSuccessListener;
import com.vaslabs.sdc.ui.SpeechCommunicationManager;

import android.test.AndroidTestCase;
import android.widget.Toast;

public class TestSpeechCommunicationManager extends AndroidTestCase {
    boolean talked = false;
    public void test_adding_messages_to_speech_queue() {
        final SpeechCommunicationManager scm = SpeechCommunicationManager.getInstance();
        
        OnSpeechSuccessListener listener = new OnSpeechSuccessListener() {
            
            @Override
            public void onSuccess() {
                scm.getProximityWarning( mContext );
                talked = true;
            }
            
            @Override
            public void onFailure() {
                Toast.makeText( mContext, "Failed to initialise text to speech", Toast.LENGTH_LONG ).show();
                talked = true;
            }
        };
        
        while (!talked) {
            scm.initialiseTextToSpeech( this.mContext, listener );
            try {
                Thread.sleep( 1000 );
            } catch ( InterruptedException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep( 1000 );
        } catch ( InterruptedException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        while (scm.isTalking()) {
            try {
                Thread.sleep( 1000 );
            } catch ( InterruptedException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
