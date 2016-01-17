package com.vaslabs.sdc.cache;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.vaslabs.accounts.Account;
import com.vaslabs.accounts.AccountManager;
import com.vaslabs.logbook.SkydivingSessionData;
import com.vaslabs.sdc.connectivity.SdcService;
import com.vaslabs.sdc.ui.Main2Activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by vnicolaou on 17/01/16.
 */
public class CacheManager {

    private static CacheManager cacheManager = new CacheManager();
    private Context context;
    private SkydivingSessionData sessionData;

    public static CacheManager getInstance(Context context) {
        cacheManager.context = context;
        return cacheManager;
    }

    public static SkydivingSessionData getLastSession() {
        if (Main2Activity.sessions == null || Main2Activity.sessions.length == 0)
            return null;
        SkydivingSessionData latestSessionData = Main2Activity.sessions[Main2Activity.sessions.length - 1];
        for (SkydivingSessionData sessionData : Main2Activity.sessions) {
            if (sessionData.getBarometerEntries().size() > 200) {
                latestSessionData = sessionData;
            }
        }
        return latestSessionData;
    }


    public SkydivingSessionData[] getSessionData() {
        AccountManager accountManager = new AccountManager(context);
        Account account = null;
        SkydivingSessionData[] skydivingSessionData = null;
        try {
            account = accountManager.getAccount();
        } catch (Exception e) {
            return null;
        }
        String cacheKey = String.valueOf(account.getKey().hashCode());
        try {
            File file = new File(context.getCacheDir(), cacheKey);
            skydivingSessionData = readFromFile(file);
            if (skydivingSessionData.length == 0)
                return null;
            return skydivingSessionData;
        } catch (IOException e) {
            Log.e("CacheManager", e.getMessage());
        }
        return skydivingSessionData;
    }

    private SkydivingSessionData[] readFromFile(File file) throws FileNotFoundException {
        Gson gson = new Gson();
        JsonReader jr = new JsonReader(new FileReader(file));
        return gson.fromJson(jr, SkydivingSessionData[].class);
    }

    public void cache(String key, String response) {
        File file = new File(context.getCacheDir(), String.valueOf(key.hashCode()));
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file);
            fileWriter.write(response);

        } catch (IOException e) {
            return;
        } finally {
            try {
                fileWriter.close();
            } catch (IOException e) {
                Log.e("CacheManager", e.getMessage());
            }
        }


    }

    public static void clearCache(Context context, String apiToken) {
        try {
            File file = new File(context.getCacheDir(), String.valueOf(apiToken.hashCode()));
            file.delete();
        } catch (Exception e) {
            Log.e("CacheManager", e.getMessage());
        }

    }
}
