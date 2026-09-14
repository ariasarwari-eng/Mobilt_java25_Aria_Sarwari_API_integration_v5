package com.mobilutveckling.myweatherapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

        // hämta knappen på weather sidan som ska ta oss till history
        Button historyButton = view.findViewById(R.id.historyButton);

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