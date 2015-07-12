package com.vaslabs.pwa;

import org.json.JSONArray;
import org.json.JSONException;

public class Response {
    public static final int SKIPPED = -1;
    public static final Response SKIPPED_RESPONSE = new Response(null, SKIPPED);
    private int code;
    private JSONArray body;

    protected Response( JSONArray json, int code ) {
        this.code = code;
        this.body = json;
    }

    public int getCode() {
        return this.code;
    }

    /* workaround, will accept only JSONArray */
    public Object getBody() throws JSONException {
        return this.body.get( 0 );
    }
}
