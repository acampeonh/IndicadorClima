package com.example.acampeon.indicadorclima;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by acampeon on 26/04/16.
 */

    public class ForecastFragment extends Fragment {

        public ForecastFragment (){
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.forecastfragment, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_refresh) {
                FetchWeatherTask weatherTask = new FetchWeatherTask();
                String zip_code = "94043";
                weatherTask.execute(zip_code);
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

            ArrayAdapter<String> mForecastAdapter;
            // Create some ...
            String[] data = {
                    "Mon 25/04 - Sunny - 31/7",
                    "Tue 26/04 - Foggy - 31/7",
                    "Wed 27/04 - Cloudy - 31/7",
                    "Thu 28/04 - Rainy - 31/7",
                    "Fri 29/04 - Cloudy - 31/7",
                    "Sat 30/04 - Sunny - 31/7",
                    "Sun 01/05 - Sunny - 31/7",
            };

            List<String> weekForecast = new ArrayList<String>(Arrays.asList(data));

            mForecastAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    R.layout.list_item_forecast,
                    R.id.list_item_forecast_textview,
                    weekForecast);

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
            listView.setAdapter(mForecastAdapter);

/*            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            //Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
            try{
                //
                String base = "http://api.openweathermap.org/data/2.5/forecast/daily?q=9404&mode=json&units=metric&cnt=7";
                String key  = "&APPID=" + BuildConfig.OPEN_WEATHER_MAP_API_KEY;
                URL url     = new URL(base.concat(key));

                //Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null){
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null){
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    urlConnection.disconnect();
                }
                forecastJsonStr = buffer.toString();

            }catch (IOException e){
                Log.e("PlaceholderFragment", "Error", e);
                return null;
            }finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }

            }*/
            return rootView;
        }

        public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

            private  final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

            private String formatHighLows(double high, double low){
                long roundeHigh = Math.round(high);
                long roundeLow = Math.round(low);

                String highLowStr = roundeHigh + "/" + roundeLow;
                return highLowStr;
            }

            private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays) throws JSONException {
                //these are the names of the json objects that need to be extracted.
                final String OWM_LIST           = "list";
                final String OWM_WEATHER        = "weather";
                final String OWM_TEMPERAURE     = "temp";
                final String OWM_MAX            = "max";
                final String OWM_MIN            = "min";
                final String OWM_DESCRIPTION    = "main";

                JSONObject forecastJson = new JSONObject(forecastJsonStr);
                JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

                Calendar gc = new GregorianCalendar();

                String[] resultStrs = new String[numDays];

                for(int i=0; i < weatherArray.length(); i++){
                    String day;
                    String description;
                    String highAndLow;

                    JSONObject dayForecast = weatherArray.getJSONObject(i);
                    long dateTime;

                    day = gc.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH);
                    gc.add(Calendar.DAY_OF_WEEK, 1);

                    JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                    description = weatherObject.getString(OWM_DESCRIPTION);

                    JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERAURE);

                    double high = temperatureObject.getDouble(OWM_MAX);
                    double low = temperatureObject.getDouble(OWM_MIN);

                    highAndLow = formatHighLows(high, low);
                    resultStrs[i] = day + " - " + description + " - " + highAndLow;
                    Log.v("JsonObject", resultStrs[i]);
                }
                return resultStrs;
            }

            @Override
            protected String[]  doInBackground(String... params) {

                //If there's no zip code, there's nothing to look up. Vrify size od params.
                if (params.length == 0){
                    return null;
                }

                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String format = "json";
                String units = "metric";
                int numDays = 7;

                //Will contain the raw JSON response as a string.
                String forecastJsonStr = null;

                try{
                    //
                    final String FORECAST_BASE_URL  = "http://api.openweathermap.org/data/2.5/forecast/daily";
                    final String QUERY_PARAM        = "q";
                    final String FORMAT_PARAM       = "mode";
                    final String UNITS_PARAM        = "units";
                    final String DAYS_PARAM         = "cnt";
                    final String APPID_PARAM        = "APPID";

                    Uri builtUri =  Uri.parse(FORECAST_BASE_URL).buildUpon()
                                    .appendQueryParameter(QUERY_PARAM, params[0])
                                    .appendQueryParameter(FORMAT_PARAM, format)
                                    .appendQueryParameter(UNITS_PARAM, units)
                                    .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                                    .appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
                                    .build();

                    URL url = new URL(builtUri.toString());

                    Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                    //Create the request to OpenWeatherMap, and open the connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null){
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null){
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        urlConnection.disconnect();
                    }
                    forecastJsonStr = buffer.toString();

                }catch (IOException e){
                    Log.e("PlaceholderFragment", "Error", e);
                    return null;
                }finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e("PlaceholderFragment", "Error closing stream", e);
                        }
                    }
                }

                try{
                    return getWeatherDataFromJson(forecastJsonStr,numDays);
                }catch (JSONException e){
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }

                return null;
            }
        }

    }