package com.vaslabs.accounts;

/**
 * Created by vnicolaou on 14/01/16.
 */
public enum RequestOutcome {
    OK("{'message':'OK', 'code': 200}"), NOT_OK("");

    public final String responseMessage;

    RequestOutcome(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}
