package com.google.app.bakingapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import com.google.app.bakingapp.MainActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    /*
    This function reads a JSON object and builds an URL
     */
    public static URL buildURL(String url, String api_key) {

        String base_url = "", scheme = "";
        JSONArray paths = new JSONArray();
        JSONArray params_name = new JSONArray();
        JSONArray params_value = new JSONArray();
        Uri.Builder builder = new Uri.Builder();
        URL urlMovie = null;

        try {

            JSONObject urlJSONObject = new JSONObject(url);
            scheme = urlJSONObject.getString("scheme");
            base_url = urlJSONObject.getString("base_url");
            paths = urlJSONObject.getJSONArray("paths");
            params_name = urlJSONObject.getJSONArray("params_name");
            params_value = urlJSONObject.getJSONArray("params_value");

            Log.i(MainActivity.APP_NAME, String.format("the url string is %s %s %s %s",
                    params_value.toString(), params_name.toString(), paths, base_url));


            builder = new Uri.Builder();

            //Read paths
            if (paths != null && paths.length() > 0)
                for (int i = 0; i < paths.length(); i++) {

                    builder.appendPath(paths.getString(i));
                }

            //Read query parameters
            // if any one of the arrays is empty, query is not added
            if (params_name != null && params_value != null &&
                    params_name.length() == params_value.length()) {

                for (int i = 0; i < params_name.length(); i++) {

                    if (params_name.getString(i).equalsIgnoreCase("api_key"))
                        builder.appendQueryParameter(params_name.getString(i), api_key);
                    else
                        builder.appendQueryParameter(params_name.getString(i), params_value.getString(i));
                }

            } else {
                Log.e(MainActivity.APP_NAME, String.format("the parameters name and values count don't match %s", url));
            }

            Log.i(MainActivity.APP_NAME, String.format("the builder is %s", builder));


        } catch (Exception e) {
            Log.e(MainActivity.APP_NAME, String.format("Exception occurred in building the URL %s", url));
            e.printStackTrace();
        }

        builder.authority(base_url)
                .scheme(scheme);
        String myurl = builder.build().toString();

        try {
            urlMovie = new URL(myurl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i(MainActivity.APP_NAME, String.format("the UrL formed is %s", myurl));
        return urlMovie;
    }


    public static String getResponseFromHttpUrl(URL url) {

        HttpURLConnection urlConnection = null;
        InputStream inputStream;
        Scanner scanner;
        String response = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            inputStream = urlConnection.getInputStream();
            scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");
            boolean hasNext = scanner.hasNext();

            if (hasNext) {
                response = scanner.next();
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return response;
    }

    public static byte[] getPictureByteOfArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 10, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

      /*
    https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
     */
    public static  boolean isOnline(Context context) {
        boolean status = false;

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        status = netInfo != null && netInfo.isConnectedOrConnecting();

        return status;
    }
}