package com.example.acampeon.indicadorclima;

import android.os.AsyncTask;
import android.os.Bundle;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
                weatherTask.execute();
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

        public class FetchWeatherTask extends AsyncTask<Void, Void, Void> {

            private  final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

            @Override
            protected Void doInBackground(Void... param) {
                HttpURLConnection urlConnection = null;
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

                }
                return null;
            }
        }

    }