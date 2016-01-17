package com.vaslabs.sdc_dashboard.API;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by vnicolao on 11/07/15.
 */
public class API {
    private static final String API_TOKEN_FILE = "apitoken.raw";

    public static String getApiToken(Context context) throws IOException {
        FileInputStream fis = context.openFileInput(API.API_TOKEN_FILE);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
        String token;
        try {
            token = reader.readLine();
        } finally {
            reader.close();
        }
        return token;
    }

    public static void saveApiToken(Context context, String token) throws IOException {
        FileOutputStream fos = context.openFileOutput(API.API_TOKEN_FILE, Context.MODE_PRIVATE);
        try {
            fos.write(token.getBytes());
        } finally {
            fos.close();
        }
    }

    public static void deleteToken(Context context) {
        File file = new File(context.getFilesDir(), API.API_TOKEN_FILE);
        file.delete();
    }
}
