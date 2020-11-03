package com.example.jsonparsing;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    String jsonResult;
    TextView txtResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtResult = findViewById(R.id.txt_result);
    }

    private class fetchJsonTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... parameters) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                // Setting up the stream to read the JSON from the URL
                URL url = new URL(parameters[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                // Read lines from the input stream
                reader = new BufferedReader(new InputStreamReader(stream));

                // Same as StringBuilder but can be used in a thread like here
                StringBuffer buffer = new StringBuffer();
                String line = "";

                // Actually reading the JSON data and add it to string buffer
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response", " > " + line);
                }
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Close the connections
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                        e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            jsonResult = s;

            // Parse the JSON data
            try {
                JSONObject jsonObject = new JSONObject(jsonResult);
                Iterator<String> keyIterator = jsonObject.keys();

                while (keyIterator.hasNext()) {
                    String key = keyIterator.next();

                    try {
                        JSONObject villager = (JSONObject) jsonObject.get(key);

                        // Nested object, names are in different languages
                        JSONObject nameObj = (JSONObject) villager.get("name");
                        Object name = nameObj.get("name-USen");

                        Object phrase = villager.get("catch-phrase");

                        Object species = villager.get("species");

                        // Print the results to the text view
                        txtResult.append(name.toString() + " the " + species + " says \'" + phrase + "\'\n");
                    } catch (JSONException e) {
                        Log.d("JSON", " > " + e.toString());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void fetchBugs(View view){
        new fetchJsonTask().execute("https://acnhapi.com/v1/villagers");
    }
}

// some resources to look into:
// https://www.journaldev.com/538/string-vs-stringbuffer-vs-stringbuilder
// https://stackoverflow.com/questions/33229869/get-json-data-from-url-using-android
// https://abhiandroid.com/programming/json