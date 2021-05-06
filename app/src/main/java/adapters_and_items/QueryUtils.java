package adapters_and_items;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils {

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() { }

    private static ArrayList<ShipmentItem> extractJson(String responsJSON)
    {
        if (TextUtils.isEmpty(responsJSON))
        {
            Log.i("a", "Json respons is null---------------------: ");
            return null;
        }

        ArrayList<ShipmentItem> ShipmentItemsListAPI = new ArrayList<>();

        try {
            JSONArray itemsArray = new JSONArray(responsJSON);
            for (int i = 0; i < itemsArray.length(); i++)
            {
                JSONObject currentItem = itemsArray.getJSONObject(i);
                 String prodImageURL=currentItem.getString("image");
                 String profileImageURL=currentItem.getString("url");
                 String prodName="not_yet";
                 String counteryFrom=currentItem.getString("from_country");
                 String counteryTo=currentItem.getString("to_country");
                 String lastDate=currentItem.getString("deadline");
                 String profileName="not_yet";
                 double reward=currentItem.getDouble("price");
                 double weight=currentItem.getDouble("weight");
                 double itemsNumber=-1;
                 double rate=0;

                ShipmentItem item = new ShipmentItem( prodImageURL , weight , itemsNumber, prodName, counteryFrom,counteryTo,lastDate,
                                                     reward, profileImageURL, profileName,  rate);
                ShipmentItemsListAPI.add(item);
            }
        }
        catch (JSONException e) {
            Log.e("QueryUtils", "-------Problem parsing the Shipment JSON results", e);
        }

        return ShipmentItemsListAPI;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e("QueryUtils", "Problem building the URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("QueryUtils", "--------Error response code: " + urlConnection.getResponseCode());
            }
        }
        catch (IOException e) {
            Log.e("QueryUtils", "----------Problem retrieving the Shipment JSON results.", e);
        }
        finally {
            if (urlConnection != null) { urlConnection.disconnect();
            }
            if (inputStream != null)   { inputStream.close();
            }
        }
        return jsonResponse;
    }

    // Convert the {@link InputStream} into a String which contains the whole JSON response from the server.
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    //TODO start
    // use makeHttpRequest & readFromStream
    public static ArrayList<ShipmentItem>getApiShipmentItemsData(String requestUrl) {

        URL url = createUrl(requestUrl);
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e("QueryUtils", "-------Problem making the HTTP request.", e);
        }

        ArrayList<ShipmentItem> Data= extractJson(jsonResponse);

        return Data;
    }
}