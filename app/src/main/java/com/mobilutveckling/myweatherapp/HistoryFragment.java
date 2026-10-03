package com.mobilutveckling.myweatherapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
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
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        Button statisticsButton = view.findViewById(R.id.statisticsButton);
        TextView historyText = view.findViewById(R.id.historyText);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("weatherHistory")
                .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            StringBuilder history = new StringBuilder();

                            // går igenom varje dokument och plockar ut samma information som vi tidigare sparade
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                                String city = document.getString("city");
                                Double temperature = document.getDouble("temperature");
                                Long humidity = document.getLong("humidity");
                                String weather = document.getString("weather");

                                // bygger ihop en text:
                                history.append("City: ")
                                        .append(city)
                                        .append("\nTemperature: ")
                                        .append(temperature)
                                        .append(" °C\nWeather: ")
                                        .append(weather)
                                        .append("\nHumidity: ")
                                        .append(humidity)
                                        .append("%\n\n");
                            }
                            // till slut visas den i HistoryFragment.
                            historyText.setText(history.toString());
                        })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });

        /*----------------------ANVÄNDAREN TYCKER PÅ STATISTIC KNAPP------------------------------*/
        statisticsButton.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new StatisticsFragment())
                    .addToBackStack(null)
                    .commit();
        });
        return view;
    }
}