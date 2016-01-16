package com.vaslabs.logbook;


import java.util.List;

/**
 * Created by vnicolaou on 15/08/15.
 */
public interface ILogbookAPI {

    List<Logbook> getLogbookEntries() throws Exception;
    void fetchSessions();
}
