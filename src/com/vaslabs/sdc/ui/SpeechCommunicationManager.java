package com.vaslabs.sdc.ui;


import com.vaslabs.sdc.utils.DynamicQueue;
import com.vaslabs.sdc.utils.EventBasedDynamicQueue;
import com.vaslabs.sdc.utils.OnEventAddedListener;

import android.content.Context;
import android.speech.tts.TextToSpeech;

public class SpeechCommunicationManager implements TextToSpeech.OnInitListener, OnEventAddedListener {
    
    private static SpeechCommunicationManager speech = new SpeechCommunicationManager();
    private TextToSpeech textToSpeech;
    private DynamicQueue<String> messagesQueue;
    private OnSpeechSuccessListener successListener;
    
    private SpeechCommunicationManager() {
        messagesQueue = new EventBasedDynamicQueue<String>( this );
    }
    
    public static SpeechCommunicationManager getInstance() {
        return speech;
    }
    
    public void registerSuccessListener(OnSpeechSuccessListener listener) {
        this.successListener = listener;
    }
    
    public void initialiseTextToSpeech(Context c, OnSpeechSuccessListener listener) {
        this.successListener = listener;
        textToSpeech = new TextToSpeech( c, this );
    }
    
    public void getProximityWarning(Context c) {
        if (textToSpeech == null) {
            return;
        }
        String textMessage = c.getString( R.string.proximity_warning );
        messagesQueue.append( textMessage );
    }

    @Override
    public void onInit( int status ) {
        if (status != TextToSpeech.SUCCESS) {
            //warn user TODO
            if (successListener != null)
                successListener.onFailure();
        } else {
            if (successListener != null)
                successListener.onSuccess();
        }
    }

    @Override
    public <T> void onEventAdded( DynamicQueue<T> queue ) {
        if (textToSpeech == null) {
            //warn TODO
            return;
        }
        while (queue.size() > 0) {
            textToSpeech.speak( queue.pop().toString(), TextToSpeech.QUEUE_ADD, null );
        }
        
    }

    public boolean isTalking() {
        return this.textToSpeech.isSpeaking();
    }

    public void getTalkingAvailable(Context c) {
        messagesQueue.append( c.getString( R.string.scm_success) );
    }
    
    protected void finalize() {
        textToSpeech.shutdown();
    }

    public void shutdown() {
        textToSpeech.shutdown();
    }
    
}


