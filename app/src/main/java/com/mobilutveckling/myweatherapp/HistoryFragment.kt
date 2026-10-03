package com.mobilutveckling.myweatherapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.mobilutveckling.myweatherapp.HistoryFragment.Companion.newInstance

/**
 * A simple [Fragment] subclass.
 * Use the [newInstance] factory method to
 * create an instance of this fragment.
 */
class HistoryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

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
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        val statisticsButton = view.findViewById<Button>(R.id.statisticsButton)
        val historyText = view.findViewById<TextView>(R.id.historyText)

        val db = FirebaseFirestore.getInstance()

        db.collection("weatherHistory")
            .get()
            .addOnSuccessListener(OnSuccessListener { queryDocumentSnapshots: QuerySnapshot? ->
                val history = StringBuilder()
                // går igenom varje dokument och plockar ut samma information som vi tidigare sparade
                for (document in queryDocumentSnapshots!!) {
                    val city = document.getString("city")
                    val temperature = document.getDouble("temperature")
                    val humidity = document.getLong("humidity")
                    val weather = document.getString("weather")

                    // bygger ihop en text:
                    history.append("City: ")
                        .append(city)
                        .append("\nTemperature: ")
                        .append(temperature)
                        .append(" °C\nWeather: ")
                        .append(weather)
                        .append("\nHumidity: ")
                        .append(humidity)
                        .append("%\n\n")
                }
                // till slut visas den i HistoryFragment.
                historyText.setText(history.toString())
            })
            .addOnFailureListener(OnFailureListener { e: Exception? ->
                e!!.printStackTrace()
            })

        /*----------------------ANVÄNDAREN TYCKER PÅ STATISTIC KNAPP------------------------------*/
        statisticsButton.setOnClickListener(View.OnClickListener { v: View? ->
            getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, StatisticsFragment())
                .addToBackStack(null)
                .commit()
        })
        return view
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * 
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HistoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): HistoryFragment {
            val fragment = HistoryFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.setArguments(args)
            return fragment
        }
    }
}