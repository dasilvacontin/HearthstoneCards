package in.dasilvacont.hearthstonecards.app;

/**
 * Created by dasilvacontin on 16/03/15.
 */

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import in.dasilvacont.hearthstonecards.app.CardAdapter;
import in.dasilvacont.hearthstonecards.app.data.CardContract;
import in.dasilvacont.hearthstonecards.app.data.CardProvider;

public class FetchCardTask extends AsyncTask<Void, Void, Void> {

    private final String LOG_TAG = FetchCardTask.class.getSimpleName();

    private final Context mContext;

    public FetchCardTask(Context context) {
        Log.d(LOG_TAG, "FetchCardTask created!");
        mContext = context;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private void getCardDataFromJson(String cardJsonStr)
            throws JSONException {
        Log.d(LOG_TAG, "getCardDataFromJson...");

        Vector<ContentValues> cVVector = new Vector<ContentValues>();

        try {
            JSONObject cardJson = new JSONObject(cardJsonStr);
            Iterator<?> editions = cardJson.keys();

            while ( editions.hasNext() ) {
                String edition = (String)editions.next();
                if (cardJson.get(edition) instanceof JSONArray) {
                    JSONArray cards = cardJson.getJSONArray(edition);

                    for (int i = 0; i < cards.length(); ++i) {
                        JSONObject card = cards.getJSONObject(i);

                        String cardRarity = "Unknown";
                        String cardCost = "0";
                        String cardAttack = "0";
                        String cardHealth = "0";
                        String cardText = "";
                        String playerClass = "None";

                        if (card.has("rarity")) cardRarity = card.getString("rarity");
                        if (card.has("cost")) cardCost = card.getString("cost");
                        if (card.has("attack")) cardAttack = card.getString("attack");
                        if (card.has("health")) cardHealth = card.getString("health");
                        if (card.has("text")) cardText = card.getString("text");
                        if (card.has("playerClass")) playerClass = card.getString("playerClass");

                        ContentValues cardValues = new ContentValues();
                        cardValues.put(CardContract.CardEntry.COLUMN_CARD_ID, card.getString("id"));
                        cardValues.put(CardContract.CardEntry.COLUMN_CARD_NAME, card.getString("name"));
                        cardValues.put(CardContract.CardEntry.COLUMN_TYPE, card.getString("type"));
                        cardValues.put(CardContract.CardEntry.COLUMN_RARITY, cardRarity);
                        cardValues.put(CardContract.CardEntry.COLUMN_COST, cardCost);
                        cardValues.put(CardContract.CardEntry.COLUMN_ATTACK, cardAttack);
                        cardValues.put(CardContract.CardEntry.COLUMN_HEALTH, cardHealth);
                        cardValues.put(CardContract.CardEntry.COLUMN_TEXT, cardText);
                        cardValues.put(CardContract.CardEntry.COLUMN_PLAYER_CLASS, playerClass);
                        cVVector.add(cardValues);
                    }
                }
            }

            // add to database
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                mContext.getContentResolver().bulkInsert(CardContract.CardEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "FetchCardTask Complete. " + cVVector.size() + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.d(LOG_TAG, "doInBackground");

       // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String cardJsonStr = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            final String CARD_BASE_URL =
                    "http://hearthstonejson.com/json/AllSets.json";
            URL url = new URL(CARD_BASE_URL);

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            cardJsonStr = buffer.toString();
            getCardDataFromJson(cardJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return null;
    }
}
