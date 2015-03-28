package com.vaslabs.sdc.utils;

public class EventBasedDynamicQueue<T> extends DynamicQueue<T> {
    
    private OnEventAddedListener eventListener;
    
    public EventBasedDynamicQueue(OnEventAddedListener listener) {
        this.eventListener = listener;
    }
    
    @Override
    public void append(T obj) {
        super.append(obj);
        eventListener.onEventAdded(this);
    }
    
    @Override
    public void appendInFront(T obj) {
        super.appendInFront( obj );
        eventListener.onEventAdded( this );
    }
}
