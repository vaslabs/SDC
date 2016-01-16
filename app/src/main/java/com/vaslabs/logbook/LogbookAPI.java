package com.vaslabs.logbook;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

/**
 * Created by vnicolaou on 15/08/15.
 */
public enum LogbookAPI implements ILogbookAPI {

    INSTANCE {

        @Override
        public List<Logbook> getLogbookEntries() throws Exception {
            return null;
        }

        @Override
        public void fetchSessions() {
            
        }
    };

    SkydivingSessionData[] sessionData;
}
