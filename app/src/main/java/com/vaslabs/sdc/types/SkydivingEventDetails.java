package com.vaslabs.sdc.types;

/**
 * Created by vnicolaou on 01/11/15.
 */
public class SkydivingEventDetails {
    public final SkydivingEvent eventType;
    public final long timestamp;

    public SkydivingEventDetails(SkydivingEvent eventType, long timestamp) {
        this.eventType = eventType;
        this.timestamp = timestamp;
    }
}
