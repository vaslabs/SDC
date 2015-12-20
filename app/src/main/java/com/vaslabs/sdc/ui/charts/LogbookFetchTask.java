package com.vaslabs.sdc.ui.charts;


import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.vaslabs.logbook.Logbook;
import com.vaslabs.logbook.LogbookAPI;
import com.vaslabs.logbook.LogbookSummary;
import com.vaslabs.sdc.types.LogbookSummaryEntry;
import com.vaslabs.sdc.ui.R;
import com.vaslabs.sdc.ui.fragments.CardViewAdapter;

import java.io.FileNotFoundException;
import java.util.List;

public class LogbookFetchTask extends AsyncTask<Void, Void, List<Logbook>> {

    private final RecyclerView recyclerView;
    private Exception exception = null;
    private final Context context;

    public LogbookFetchTask(RecyclerView recyclerView) {
        this.context = recyclerView.getContext();
        this.recyclerView = recyclerView;
    }

    @Override
    protected void onPreExecute() {
        Toast.makeText(context, "Loading data...", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected List<Logbook> doInBackground(Void... params) {
        List<Logbook> logbookEntries = null;
        LogbookSummary logbookSummary = null;
        try {
            logbookEntries = LogbookAPI.INSTANCE.getLogbookEntries();
            return logbookEntries;
        } catch (Exception e) {
            this.exception = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Logbook> logbookEntries) {
        if (exception != null) {
            if (exception instanceof FileNotFoundException) {
                Toast.makeText(context, R.string.api_token_problem, Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(context, exception.toString(), Toast.LENGTH_LONG).show();
            return;
        }
        if (logbookEntries == null) {
            Toast.makeText(context, "Failed to fetch logbook data", Toast.LENGTH_SHORT).show();
        }
        LogbookSummary logbookSummary = LogbookSummary.fromLogbookEntries(logbookEntries);

        LogbookSummaryEntry[] logbookSummaryEntries = LogbookSummaryEntry.fromLogbookSummary(logbookSummary, context);
        recyclerView.setAdapter(new CardViewAdapter(logbookSummaryEntries));

    }
}