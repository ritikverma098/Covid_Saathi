package com.example.covs

import android.content.Intent
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
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.color.MaterialColors
import com.google.android.material.transition.MaterialContainerTransform
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ActiveCases : Fragment() {
    val prefFile = "stateCode"
    lateinit var state: String
    lateinit var chart:LineChart

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
        val view : View = inflater.inflate(R.layout.fragment_active_cases, container, false)
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences(prefFile,0)
        chart = view.findViewById(R.id.activeLineGraph)
        //  chart.setViewPortOffsets(0f,0f,0f,0f)

        chart.description.isEnabled = false
        chart.setTouchEnabled(true)
        chart.isDragEnabled = false
        chart.setScaleEnabled(false)
        chart.setPinchZoom(false)
        chart.setDrawGridBackground(false)
        chart.maxHighlightDistance = 300f

        val x : XAxis = chart.xAxis
        x.isEnabled = true
        x.setDrawGridLines(false)
        x.position = XAxis.XAxisPosition.BOTTOM
        x.textColor = MaterialColors.getColor(requireContext(), R.attr.appText, Color.BLACK)
        val y : YAxis = chart.axisLeft
        y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        y.setDrawGridLines(true)
        y.textColor = MaterialColors.getColor(requireContext(), R.attr.appText, Color.BLACK)
        chart.axisRight.isEnabled = false;

        chart.legend.isEnabled = false;
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
                    var dayToDisplay = SimpleDateFormat("dd-MMM")
                    //var currentDate = dateFormat.format(Date())
                    //activity?.runOnUiThread{ Log.d("locationCheck","Current date : " + currentDate[8]+currentDate[9])}
                    //var date = LocalDate.now()
                    var dateStore = ArrayList<String>()
                    var dateDisplay = ArrayList<String>()
                    for (i in 1..7)
                    {
                        cal.add(Calendar.DAY_OF_MONTH, -1)
                        date = cal.time
                        dateStore.add(dateFormat.format(date))
                        dateDisplay.add(dayToDisplay.format(date))

                        //var lastSeven = date.minusDays(i.toLong())
                        //dateStore.add(lastSeven.format(DateTimeFormatter.ofPattern("YYYY-MM-dd")))

                    }
                    dateDisplay.reverse()
                    var dates = state.getJSONObject("dates")
                    var dateSelect:JSONObject
                    var activeCasesList = ArrayList<String>()
                    var delta:JSONObject
                    for (i in dateStore)
                    {
                        dateSelect = dates.getJSONObject(i)
                        delta = dateSelect.getJSONObject("delta7")
                        activeCasesList.add(delta.getString("confirmed"))


                    }
                    var floatData = ArrayList<Int>()
                    floatData.add(0)
                    floatData.add(1)
                    floatData.add(2)
                    floatData.add(3)
                    floatData.add(4)
                    floatData.add(5)
                    floatData.add(6)
                    activeCasesList.reverse()
                    var entries = ArrayList<Entry>()
                    for(i in 0..6)
                    {

                        entries.add(Entry(floatData[i].toFloat(),activeCasesList[i].toFloat()))
                    }
                    var lineDataSet = LineDataSet(entries,"Active cases")
                    lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
                    lineDataSet.cubicIntensity = 0.2f
                    lineDataSet.setDrawFilled(true)
                    lineDataSet.setDrawCircles(false)
                    lineDataSet.lineWidth = 1.8f
                    lineDataSet.circleRadius = 4f
                    lineDataSet.setCircleColor(Color.WHITE)
                    lineDataSet.setColor(resources.getColor(R.color.middleYellow))
                    lineDataSet.fillColor = resources.getColor(R.color.middleYellow)
                    lineDataSet.fillAlpha = 100
                    lineDataSet.setDrawHorizontalHighlightIndicator(false)
                    val ifill = object : IFillFormatter {
                        override fun getFillLinePosition(
                            dataSet: ILineDataSet?,
                            dataProvider: LineDataProvider?
                        ): Float {
                            return chart.axisLeft.axisMinimum
                        }
                    }
                    lineDataSet.setFillFormatter(ifill)
                    var lineData = LineData(lineDataSet)
                    lineData.setDrawValues(false)
                    var xAxis = chart.xAxis
                    xAxis.valueFormatter = com.github.mikephil.charting.formatter.IndexAxisValueFormatter(dateDisplay)
                    chart.data = lineData
                    chart.invalidate()
                    //activity?.runOnUiThread{ Log.d("locationCheck","Current date : $entries")}
                },
                Response.ErrorListener { error ->
                    var intent = Intent(requireContext(),InternetError::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
            )

            MySingleton.getInstance(requireContext()).addToRequestQueue(jsonObjectRequest)
        }).start()

    }


}