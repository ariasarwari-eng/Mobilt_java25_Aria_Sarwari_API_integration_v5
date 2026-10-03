package com.mobilutveckling.myweatherapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class WeatherFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private FirebaseFirestore db;

    public WeatherFragment() {
        // Required empty public constructor
    }
    public static WeatherFragment newInstance(String param1, String param2) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        db = FirebaseFirestore.getInstance();

        /*-----------------------HÄMA KOMPONENTER FRÅN XML--------------------------------------*/

        EditText cityInput = view.findViewById(R.id.cityInput);
        Button getWeatherButton = view.findViewById(R.id.getWeatherButton);
        TextView temperatureText = view.findViewById(R.id.temperatureText);
        TextView weatherText = view.findViewById(R.id.weatherText);
        TextView humidityText = view.findViewById(R.id.humidityText);

        Button historyButton = view.findViewById(R.id.historyButton);
        /*---------------------------------------------------------------------------------------*/

        /*----------------------------SKAPA KLICK EVENT------------------------------------------*/

        getWeatherButton.setOnClickListener(v ->{

            // toString används för att en EditText returnerar ett objekt av typen Editable och ej en String.
            String city = cityInput.getText().toString();

            new Thread(() ->{

                /*----------API ANROP, BYGG IHOP URL -------------*/
                String apiKey = BuildConfig.OPENWEATHER_API_KEY;
                String urlString = "https://api.openweathermap.org/data/2.5/weather?q="
                        +city
                        +"&units=metric&appid="
                        +apiKey;
                /*------------------------------------------------*/

                /*--------------- SKAPA ANSLUTNING--------------- */
                try {

                    /*--------------SKICKA GET FÖRFRÅGAN-----------------*/
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    /*---------------------------------------------------*/

                    /*--------------- LÄS AV HTTP-SVARET FRÅN OPENWEATHER API:ET---------*/

                    // hämtar status kod vi får tillbaka.
                    int responseCode = connection.getResponseCode();

                    if (responseCode == 200){
                        // skapat läsare som läser datan rad för rad.
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(connection.getInputStream())
                        );

                        // StringBuilder för att vi vill samla svaret rad för rad.
                        StringBuilder response = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null){
                            response.append(line);
                        }

                        reader.close();
                        String jsonResponse = response.toString();

                        // från json formaterad string String till JSONObject.
                        JSONObject jsonObject = new JSONObject(jsonResponse);

                        JSONObject main = jsonObject.getJSONObject("main");
                        double temperature = main.getDouble("temp");
                        int humidity = main.getInt("humidity");

                        String weatherDescription = jsonObject.getJSONArray("weather")
                                .getJSONObject(0)
                                .getString("description");

                        /*--------------------------SPARA DATA I FIREBASE-----------------------*/
                        Map<String, Object> weatherData = new HashMap<>();
                        weatherData.put("city", city);
                        weatherData.put("temperature", temperature);
                        weatherData.put("humidity", humidity);
                        weatherData.put("weather", weatherDescription);


                        db.collection("weatherHistory")
                                .add(weatherData)
                                .addOnSuccessListener(documentReference -> {
                                    System.out.println(documentReference.getId());
                                })
                                .addOnFailureListener(e -> {
                                    e.printStackTrace();
                                });

                        requireActivity().runOnUiThread(() -> {
                            temperatureText.setText("Temperature: "+ temperature + " °C");
                            weatherText.setText("Weather: "+ weatherDescription);
                            humidityText.setText("Humidity: "+ humidity + "%");
                        });
                    }

                    /*------------------------------------------------------------------*/

                } catch (Exception e){
                    e.printStackTrace();
                }

            }).start();
        });
        /*----------------ANVÄNDAREN TYCKER PÅ WEATHER HISTORY KNAPP------------------------------*/
        /*Byt Fragmentet  containern på activity till HistoryFragment.*/
        historyButton.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new HistoryFragment())
                    .addToBackStack(null)
                    .commit();
        });
        // returnerar vyn som fragmentet ska visa.
        return view;

    }
}