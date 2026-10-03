package com.mobilutveckling.myweatherapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class WeatherFragment : Fragment() {
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var db: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getArguments() != null) {
            mParam1 = getArguments()!!.getString(ARG_PARAM1)
            mParam2 = getArguments()!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_weather, container, false)
        db = FirebaseFirestore.getInstance()

        /*-----------------------HÄMA KOMPONENTER FRÅN XML--------------------------------------*/
        val cityInput = view.findViewById<EditText>(R.id.cityInput)
        val getWeatherButton = view.findViewById<Button>(R.id.getWeatherButton)
        val temperatureText = view.findViewById<TextView>(R.id.temperatureText)
        val weatherText = view.findViewById<TextView>(R.id.weatherText)
        val humidityText = view.findViewById<TextView>(R.id.humidityText)

        val historyButton = view.findViewById<Button>(R.id.historyButton)

        /*---------------------------------------------------------------------------------------*/

        /*----------------------------SKAPA KLICK EVENT------------------------------------------*/
        getWeatherButton.setOnClickListener(View.OnClickListener { v: View? ->

            // toString används för att en EditText returnerar ett objekt av typen Editable och ej en String.
            val city = cityInput.getText().toString()
            Thread(Runnable {
                /*----------API ANROP, BYGG IHOP URL -------------*/
                val apiKey = BuildConfig.OPENWEATHER_API_KEY
                val urlString = ("https://api.openweathermap.org/data/2.5/weather?q="
                        + city
                        + "&units=metric&appid="
                        + apiKey)

                /*------------------------------------------------*/

                /*--------------- SKAPA ANSLUTNING--------------- */
                try {
                    /*--------------SKICKA GET FÖRFRÅGAN-----------------*/

                    val url = URL(urlString)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.setRequestMethod("GET")

                    /*---------------------------------------------------*/

                    /*--------------- LÄS AV HTTP-SVARET FRÅN OPENWEATHER API:ET---------*/

                    // hämtar status kod vi får tillbaka.
                    val responseCode = connection.getResponseCode()

                    if (responseCode == 200) {
                        // skapat läsare som läser datan rad för rad.
                        val reader = BufferedReader(
                            InputStreamReader(connection.getInputStream())
                        )

                        // StringBuilder för att vi vill samla svaret rad för rad.
                        val response = StringBuilder()
                        var line: String?

                        while ((reader.readLine().also { line = it }) != null) {
                            response.append(line)
                        }

                        reader.close()
                        val jsonResponse = response.toString()

                        // från json formaterad string String till JSONObject.
                        val jsonObject = JSONObject(jsonResponse)

                        val main = jsonObject.getJSONObject("main")
                        val temperature = main.getDouble("temp")
                        val humidity = main.getInt("humidity")

                        val weatherDescription = jsonObject.getJSONArray("weather")
                            .getJSONObject(0)
                            .getString("description")

                        /*--------------------------SPARA DATA I FIREBASE-----------------------*/
                        val weatherData: MutableMap<String?, Any?> = HashMap<String?, Any?>()
                        weatherData.put("city", city)
                        weatherData.put("temperature", temperature)
                        weatherData.put("humidity", humidity)
                        weatherData.put("weather", weatherDescription)


                        db!!.collection("weatherHistory")
                            .add(weatherData)
                            .addOnSuccessListener(OnSuccessListener { documentReference: DocumentReference? ->
                                println(documentReference!!.getId())
                            })
                            .addOnFailureListener(OnFailureListener { e: Exception? ->
                                e!!.printStackTrace()
                            })

                        requireActivity().runOnUiThread(Runnable {
                            temperatureText.setText("Temperature: " + temperature + " °C")
                            weatherText.setText("Weather: " + weatherDescription)
                            humidityText.setText("Humidity: " + humidity + "%")
                        })
                    }

                    /*------------------------------------------------------------------*/
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }).start()
        })
        /*----------------ANVÄNDAREN TYCKER PÅ WEATHER HISTORY KNAPP------------------------------*/
        /*Byt Fragmentet  containern på activity till HistoryFragment.*/
        historyButton.setOnClickListener(View.OnClickListener { v: View? ->
            getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, HistoryFragment())
                .addToBackStack(null)
                .commit()
        })
        // returnerar vyn som fragmentet ska visa.
        return view
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        fun newInstance(param1: String?, param2: String?): WeatherFragment {
            val fragment = WeatherFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.setArguments(args)
            return fragment
        }
    }
}