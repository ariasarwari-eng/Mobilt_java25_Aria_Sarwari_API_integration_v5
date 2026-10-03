package com.mobilutveckling.myweatherapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* ----------------------- GENERERAD AV IDE ----------------------------------------------*/

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        /*----------------------------------BAKÅT NAVIGATION--------------------------------------*/

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                if (getSupportFragmentManager().getBackStackEntryCount() > 1){
                    getSupportFragmentManager().popBackStack();

                } else if (getSupportFragmentManager().getBackStackEntryCount()==1) {
                    getSupportFragmentManager().popBackStack();

                    /*---------- ÄNDRAR SYNLIGHET PÅ MINA VIEWS --------------------*/

                    findViewById(R.id.startLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.fragmentContainer).setVisibility(View.GONE);

                    /*--------------------------------------------------------------*/
                }
            }
        };
        /*----------------------------------------------------------------------------------------*/

        getOnBackPressedDispatcher().addCallback(this, callback);

        /*------------------NAVIGERING FRÅN START TILL WEATHER SIDAN (FRAMÅT)---------------------*/

        Button weatherButton = findViewById(R.id.weatherButton);
        weatherButton.setOnClickListener(v -> {

            findViewById(R.id.startLayout).setVisibility(View.GONE);
            findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new WeatherFragment())
                    .addToBackStack(null)
                    .commit();
        });
        /*----------------------------------------------------------------------------------------*/


        /*----------------------Denna sista kod bit har genererats av IDE ------------------------*/
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}