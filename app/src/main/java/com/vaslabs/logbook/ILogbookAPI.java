package com.vaslabs.logbook;

import android.content.Context;

import com.vaslabs.pwa.CommunicationManager;

/**
 * Created by vnicolaou on 15/08/15.
 */
public interface ILogbookAPI {

    LogbookSummary getLogbookSummary(CommunicationManager communicationManager, Context mContext);

}
