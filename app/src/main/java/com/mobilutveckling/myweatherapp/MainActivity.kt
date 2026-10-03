package com.mobilutveckling.myweatherapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* ----------------------- GENERERAD AV IDE ----------------------------------------------*/
        // this.enableEdgeToEdge()
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        /*----------------------------------BAKÅT NAVIGATION--------------------------------------*/
         val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                    getSupportFragmentManager().popBackStack()
                } else if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                    getSupportFragmentManager().popBackStack()

                    /*---------- ÄNDRAR SYNLIGHET PÅ MINA VIEWS --------------------*/
                    findViewById<View?>(R.id.startLayout).setVisibility(View.VISIBLE)
                    findViewById<View?>(R.id.fragmentContainer).setVisibility(View.GONE)

                    /*--------------------------------------------------------------*/
                }
            }
        }

        /*----------------------------------------------------------------------------------------*/
        onBackPressedDispatcher.addCallback(this, callback)

        /*------------------NAVIGERING FRÅN START TILL WEATHER SIDAN (FRAMÅT)---------------------*/
        val weatherButton = findViewById<Button>(R.id.weatherButton)
        weatherButton.setOnClickListener(View.OnClickListener { v: View? ->
            findViewById<View?>(R.id.startLayout).setVisibility(View.GONE)
            findViewById<View?>(R.id.fragmentContainer).setVisibility(View.VISIBLE)
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, WeatherFragment())
                .addToBackStack(null)
                .commit()
        })


        /*----------------------------------------------------------------------------------------*/


        /*----------------------Denna sista kod bit har genererats av IDE ------------------------*/
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById<View?>(R.id.main),
            OnApplyWindowInsetsListener { v: View?, insets: WindowInsetsCompat? ->
                val systemBars = insets!!.getInsets(WindowInsetsCompat.Type.systemBars())
                v!!.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            })
    }
}