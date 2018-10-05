package partmaster;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.*;

/**
 * Adapted from https://github.com/NickIvanter/octopart-java-api
 * @author Andrew Spiteri
 */
public class Octopart
{
    private static final String API_URL = "https://octopart.com/api/v3/parts/search";
    private static String API_KEY = null;
    
    public enum Status {
        OK, CONNECTION_ERROR, INVALID_KEY
    }
    
    public static Status ping()
    {
        Status status = Status.OK;
        HttpURLConnection conn = null;
        int code = 0;
        try
        {
            URL url = new URL(API_URL+"?apikey="+API_KEY);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            code = conn.getResponseCode();
//            System.out.println("HTTP Response Code " + code);
            
        }
        catch (IOException e)
        {
            status = Status.CONNECTION_ERROR;
            Logger.println("Cannot ping server");
        }
        finally
        {
            if (conn != null)
                conn.disconnect();
        }
        
        if (code == 401 || code == 403) status = Status.INVALID_KEY;
        return status;
    }
    
    public static List<Component> searchParts(String query)
    {
        List<Component> foundPartsList = null;
        String paramString = null;
        if (query != null)
        {
            query = query.replaceAll("\\s+","+");   // replace blanks
            paramString = "?q=" + query + "&apikey=" + API_KEY
                                + "&include[]=short_description"
//                                + "&include[]=specs"
//                                + "&include[]=imagesets"
                                + "&limit=20"   // max number of results returned per request
                                ;
        }
        try
        {
            URL url = new URL(API_URL+paramString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            int code = conn.getResponseCode();
            switch (code)
            {
                case 200:
                    break;
                case 400:
                    Logger.println("Invalid search query");
                    break;
                case 401:
                    Logger.println("Invalid API key");
                    break;
                case 429:
                    Logger.println("Too many requests");
                    break;
                case 500:
                    Logger.println("Internal server error");
                    break;
                case 502:
                    Logger.println("Bad gateway");
                    break;
                case 503:
                    Logger.println("Service unavailable");
                    break;
                default:
                    Logger.println("Unsupported HTTP error code encountered");
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder output = new StringBuilder();
            String line;
//            Logger.println("===");
            while ((line = br.readLine()) != null)
            {
//                Logger.println(line);
                output.append(line);
            }
//            Logger.println("===");
            conn.disconnect();
            foundPartsList = getPartsFromJSONData(output.toString());
        }
        catch (MalformedURLException e)
        {
            Logger.println("Invalid URL");
        }
        catch (IOException e)
        {
            Logger.println("Could not open HTTP URL connection");
        }
        catch (JSONException e)
        {
            Logger.println("JSON Exception");
        }
        return foundPartsList;
    }
    
    public static String getApiKey()
    {
        return API_KEY;
    }
    
    public static void setApiKey(String key)
    {
        API_KEY = key;
    }
    
    private static List<Component> getPartsFromJSONData(String jsonResponse) throws JSONException
    {
        List<Component> parts = new ArrayList();
        JSONObject jo = new JSONObject(jsonResponse);
        JSONArray results = (JSONArray) jo.get("results");
        for (int i=0; i<results.length(); i++)
        {
            JSONObject item = (JSONObject) results.getJSONObject(i).get("item");
            if (item != null)
                parts.add(new Component(item));
        }
        return parts;
    }
}
