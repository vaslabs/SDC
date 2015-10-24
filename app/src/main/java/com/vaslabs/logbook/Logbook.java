package com.vaslabs.logbook;

/**
 * Created by vnicolaou on 31/08/15.
 */

/**
 * [
 {
 "timeInMillis": 1434870982000,
 "notes": "This is a note aaa",
 "longitude": "33.723346380",
 "metrics": {
 "deploymentAltitude": 668.26,
 "freefalltime": 93.67,
 "maxVelocity": -85.18,
 "exitAltitude": 3415.65
 },
 "location": "A3, Cyprus",
 "latitude": "35.016250030",
 "id": 6
 },
 {
 "timeInMillis": 1430640388000,
 "notes": "This is another note",
 "longitude": "33.723612260",
 "metrics": {
 "deploymentAltitude": 0,
 "freefalltime": 0,
 "maxVelocity": 0,
 "exitAltitude": 0
 },
 "location": "E303, Xylotymvou, Cyprus",
 "latitude": "35.016396680",
 "id": 7
 }
 ]
 */
public final class Logbook {
    private long timeInMillis;
    private String note;
    private LogbookMetrics metrics;
    private String location;
    private double latitude;
    private double longitude;
    private int id;

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public String getNote() {
        return note;
    }

    public LogbookMetrics getMetrics() {
        return metrics;
    }

    public String getLocation() {
        return location;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getId() {
        return id;
    }
}
