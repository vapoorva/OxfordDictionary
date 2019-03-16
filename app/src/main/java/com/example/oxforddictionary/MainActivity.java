package com.example.oxforddictionary;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    private final String app_id = "8add9639";
    private final String app_key = "df0964c84887f7d42e0f989404924534";
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView defi;//text view
    private EditText word1;//word
    private Button ser;//button
    String myurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        word1 = findViewById(R.id.word);
        defi = findViewById(R.id.defination);
        ser = findViewById(R.id.search);

        ser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findMeaningOfEnteredWord();
            }
        });

    }

    private void findMeaningOfEnteredWord() {
        String word = word1.getText().toString();
        if (word.isEmpty()) {
            Toast.makeText(this, "Nothing entered", Toast.LENGTH_SHORT).show();
            return;
        }
        String lowerCaseWord = word.toLowerCase();
        String httpRequestUrl = "https://od-api.oxforddictionaries.com:443/api/v1/entries/en/" + lowerCaseWord;
        new RequestAsyncTask().execute(httpRequestUrl);
    }

    class RequestAsyncTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            myurl = params[0];
            try {
                URL url = new URL(myurl);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("app_id", app_id);
                urlConnection.setRequestProperty("app_key", app_key);

                // read the output from the server
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }

                return stringBuilder.toString();

            }
            catch (Exception e) {
                e.printStackTrace();
                return e.toString();

                                 }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject responseAsJson = new JSONObject(result);
                JSONArray results = responseAsJson.getJSONArray("results");
                if (results.length() > 0) { // valid definitions were found
                    String definition = results.getJSONObject(0).getJSONArray("lexicalEntries").getJSONObject(0)
                            .getJSONArray("entries").getJSONObject(0).getJSONArray("senses")
                            .getJSONObject(0).getJSONArray("definitions").getString(0);
                    defi.setText(definition);

                }

                Log.d(TAG, " " + responseAsJson.toString());
            } catch (Exception ex) {
                Log.d(TAG, "exception during json parsing: " + ex.getMessage());
            }


        }


    }
}
