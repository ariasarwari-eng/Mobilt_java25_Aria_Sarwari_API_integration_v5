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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WeatherFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeatherFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        // Skriv kod här

        // Skapar / växlar fram Weather-layouten
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        /*-----------------------HÄMA KOMPONENTER FRÅN XML--------------------------------------*/

        EditText cityInput = view.findViewById(R.id.cityInput);
        Button getWeatherButton = view.findViewById(R.id.getWeatherButton);
        TextView temperatureText = view.findViewById(R.id.temperatureText);
        TextView weatherText = view.findViewById(R.id.weatherText);
        TextView humidityText = view.findViewById(R.id.humidityText);

        // hämta knappen på weather sidan som ska ta oss till history
        Button historyButton = view.findViewById(R.id.historyButton);
        /*---------------------------------------------------------------------------------------*/

        /*----------------------------SKAPA KLICK EVENT------------------------------------------*/

        getWeatherButton.setOnClickListener(v ->{

            // toString används för att en EditText returnerar ett objekt av typen Editable och ej en String.
            String city = cityInput.getText().toString();

            /* Startar en separat tråd för API-anropet. För att internetanrop kan ta tid. Vi vill inte låsa Androids UI medan appen väntar på OpenWeather.
            * API anropet får inte köras på huvudtråden/ UI tråden. Eftersom Android annars kan låsa appens gränssnitt medan den väntar på internet*/
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
                    // Vi gör vår text-URL till ett Java-objekt som Java kan arbeta med.
                    URL url = new URL(urlString);
                    // Vi ber Java att öppna en anslutning till den adressen.
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    // GET är den vanligaste HTTP-metoden när vi vill läsa/hämta data från ett API. -> "Hämta information från servern."
                    connection.setRequestMethod("GET");
                    /*---------------------------------------------------*/

                    /*--------------- LÄS AV HTTP-SVARET FRÅN OPENWEATHER API:ET---------*/

                    // responseCode -> servern svarar med en HTTP-statuskod ex 200 -> ok / allt gick bra eller 404 -> not found osv
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200){
                        // läser in svaret här? data från api skickas i JSON-format

                        //BufferedReader läser svaret från servern.
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(connection.getInputStream())
                        );

                        StringBuilder response = new StringBuilder();
                        String line;

                        // läser svaret rad för rad.
                        while ((line = reader.readLine()) != null){
                            // samlar ihop allt
                            response.append(line);
                        }

                        reader.close();
                        // hela JSON svaret finns i denna variabel, använd JSONObject för att senare plocka ut temperatur, väderbeskrivning och luftfuktighet.
                        String jsonResponse = response.toString();

                        // gör om texten från API:t till ett JSON-objekt som Java kan läsa.
                        JSONObject jsonObject = new JSONObject(jsonResponse);

                        // I OpenWeather ligger bland annat temperatur och luftfuktighet under "main"-objektet.
                        JSONObject main = jsonObject.getJSONObject("main");
                        double temperature = main.getDouble("temp");
                        int humidity = main.getInt("humidity");

                       //  Sedan har OpenWeather väderbeskrivningen i en array som heter "weather"
                        // i doc kan du se att weather är array pga [] och inuti finns 1 obj {} med flera olika variabler listade här finns bla beskrivningen
                        String weatherDescription = jsonObject.getJSONArray("weather")
                                .getJSONObject(0)
                                .getString("description");

                        /* Nu har vi API datan som appen ska använda. Obs, anropet körs i en gen tråd och våra komponenter på sidan tillhör
                         * Androids UI-tråd. Att använda .setText innui denna hread går ej??--> temperatureText.setText(String.valueOf(temperature)); */

                        //"När du har fått resultatet, gå tillbaka till appens UI-tråd och uppdatera skärmen."
                        // behövde ej omvandla double / int till String ?? pga bygger ihop meningen?
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

        // Användaren klickar på History knappen
        historyButton.setOnClickListener(v -> {
            // Byt från WeatherFragment till HistoryFragment, måste stå parent pga detta är ett fragment
            // vi använder då Fragment-managern som tillhör den Activity som innehåller vårt Fragment.
            getParentFragmentManager()
                    // påbörjar förändring av fragment
                    .beginTransaction()
                    // tar det som visas i fragmentContainer och ersätt de med HistoryFragment
                    .replace(R.id.fragmentContainer, new HistoryFragment())
                    // Android ska komma ihåg navigationen så vi kan gå tillbaka.
                    .addToBackStack(null)
                    // Genomför
                    .commit();
        });
        return view;

    }
}