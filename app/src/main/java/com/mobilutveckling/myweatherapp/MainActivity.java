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

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        /*----------------------------BACK FRÅN WEATHER TILL START------------------------------*/

        // denna kod ställer till det, eftersom när vi är på history sidan så visas Start sidan
        //när vi backar, den ska egentligen backa från history tiill weather och sedan weather till start
        // När användaren trycker på back
        /*OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                // finns det något i backstacken? - om ja
                if (getSupportFragmentManager().getBackStackEntryCount() > 0){
                    // ta bort senaste navigationen från backstack
                    getSupportFragmentManager().popBackStack();

                    findViewById(R.id.startLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.fragmentContainer).setVisibility(View.GONE);
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback); */

        // Försök till att korrigerra ovansttående problem
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                // När vi är på History har vi två navigationer: alltså -> backStackEntryCount = 2
                if (getSupportFragmentManager().getBackStackEntryCount() > 1){

                    // Det finns mer än en navigation i backstacken.
                    // Gå tillbaka ett steg, History → Weather.
                    getSupportFragmentManager().popBackStack();

                    // Här ska backStackEntryCount = 1
                } else if (getSupportFragmentManager().getBackStackEntryCount()==1) {

                    // vi är på weather och går tillbaka till start
                    getSupportFragmentManager().popBackStack();
                    findViewById(R.id.startLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.fragmentContainer).setVisibility(View.GONE);
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        /*--------------------------------------------------------------------------------------------*/

        /*------------------NAVIGERING FRÅN START TILL WEATHER SIDAN------------------------------*/

        // Hämta knappen/komponenten från startsidan
        Button weatherButton = findViewById(R.id.weatherButton);

        // Event för knappen "Show weather"
        weatherButton.setOnClickListener(v -> {

            // komponenterna på startsidan har jag lagt i en egen box asom jag kan gömma.
            findViewById(R.id.startLayout).setVisibility(View.GONE);
            // i xml är visibiliy satt som gone, detta behöver drf ändras
            findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE);

            // manager behövs för att hantera fragments
            getSupportFragmentManager()
                    // börja en fragment transaktion
                    .beginTransaction()
                    //byt innehåll, lägg detta fragment i denna container
                    .replace(R.id.fragmentContainer, new WeatherFragment())
                    // android sparar navigationen, --> Lägg till i backstack, kan gå tbx osv
                    .addToBackStack(null)
                    // genomför
                    .commit();
        });
        /*---------------------------------------------------------------------------------------*/


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}