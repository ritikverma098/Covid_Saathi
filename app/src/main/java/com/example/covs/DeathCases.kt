package com.example.covs

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.color.MaterialColors
import com.google.android.material.transition.MaterialContainerTransform
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DeathCases : Fragment() {
    val prefFile = "stateCode"
    lateinit var state: String
    lateinit var chart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.fragmentFrame
            duration = 1000.toLong()
            scrimColor  = Color.TRANSPARENT
            setAllContainerColors(MaterialColors.getColor(requireContext(), R.attr.appPrimaryColor, Color.WHITE))
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_death_cases, container, false)
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences(prefFile,0)
        chart = view.findViewById(R.id.deathLineGraph)
        state = sharedPreferences.getString("stateCode","").toString()
        getData(state)
        return view
    }
    private fun getData(state:String)
    {
        Thread(Runnable {
            var url = "https://api.covid19india.org/v4/min/timeseries.min.json"
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener { response ->
                    var state = response.getJSONObject(state)
                    var dateFormat = SimpleDateFormat("YYYY-MM-dd")
                    var cal = Calendar.getInstance()
                    var date = cal.time
                    var dateFormatDay = SimpleDateFormat("dd")
                    //var currentDate = dateFormat.format(Date())
                    //activity?.runOnUiThread{ Log.d("locationCheck","Current date : " + currentDate[8]+currentDate[9])}
                    //var date = LocalDate.now()
                    var dateStore = ArrayList<String>()
                    var dateDayStore = ArrayList<Int>()
                    for (i in 1..7)
                    {
                        cal.add(Calendar.DAY_OF_MONTH, -1)
                        date = cal.time
                        dateStore.add(dateFormat.format(date))
                        dateDayStore.add(dateFormatDay.format(date).toInt())

                        //var lastSeven = date.minusDays(i.toLong())
                        //dateStore.add(lastSeven.format(DateTimeFormatter.ofPattern("YYYY-MM-dd")))

                    }

                    var dates = state.getJSONObject("dates")
                    var dateSelect: JSONObject
                    var activeCasesList = ArrayList<String>()
                    var delta:JSONObject
                    for (i in dateStore)
                    {
                        dateSelect = dates.getJSONObject(i)
                        delta = dateSelect.getJSONObject("delta7")
                        activeCasesList.add(delta.getString("deceased"))


                    }

                    var entries = ArrayList<Entry>()
                    for(i in 6 downTo 0)
                    {

                        entries.add(Entry(dateDayStore[i].toFloat(),activeCasesList[i].toFloat()))
                    }
                    var lineDataSet = LineDataSet(entries,"Deaths")
                    var lineData = LineData(lineDataSet)
                    chart.data = lineData
                    chart.invalidate()
                    //activity?.runOnUiThread{ Log.d("locationCheck","Current date : $entries")}
                },
                Response.ErrorListener { error ->
                    // TODO: Handle error
                }
            )

            MySingleton.getInstance(requireContext()).addToRequestQueue(jsonObjectRequest)
        }).start()

    }

}