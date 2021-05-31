package com.example.covs

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import app.futured.donut.DonutSection
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.util.*



class HomeFragment : BaseFragment() {
    var checkPer = 1
    lateinit var handler:Handler
    private var LocationPermissionID =1000
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private val updateInterval:Long = 30*60*1000
    private val fastInterval:Long = 5*60*1000
    private val displacement = 100f
    private lateinit var totalCase:String
    private lateinit var activeCase:String
    private lateinit var recovered:String
    private lateinit var death:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return getPersistentView(inflater, container, savedInstanceState, R.layout.fragment_home)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!hasInitializedRootView) {
            hasInitializedRootView = true
            fusedLocationProviderClient =LocationServices.getFusedLocationProviderClient(requireContext())
            getLastLocation()
            view.materialCardView?.setOnClickListener{
                val action = HomeFragmentDirections.actionHomeFragmentToTotalCases()
                val extras = FragmentNavigatorExtras(materialCardView to "totalCaseCard")
                findNavController().navigate(action, extras)
                // Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_totalCases, null)
            }
            view.materialCardView3?.setOnClickListener{
                val action = HomeFragmentDirections.actionHomeFragmentToActiveCases()
                val extras = FragmentNavigatorExtras(materialCardView3 to "activeCaseCard")
                findNavController().navigate(action, extras)
            }
            view.materialCardView2?.setOnClickListener{
                val action = HomeFragmentDirections.actionHomeFragmentToRecoveredCases()
                val extras = FragmentNavigatorExtras(materialCardView2 to "recoveredCaseCard")
                findNavController().navigate(action, extras)
            }
            view.materialCardView4?.setOnClickListener{
                val action = HomeFragmentDirections.actionHomeFragmentToDeathCases()
                val extras = FragmentNavigatorExtras(materialCardView4 to "deathCaseCard")
                findNavController().navigate(action, extras)
            }

        }
    }
    private fun getSections(): List<DonutSection>{
        return listOf(
            DonutSection(
                name = "Active",
                color = Color.parseColor("#FCEC52"),
                amount = activeCase.toFloat()
            ),
            DonutSection(
                name = "Death",
                color = Color.parseColor("#FF0000"),
                amount = death.toFloat()
            ),
            DonutSection(
                name = "Recovered",
                color = Color.parseColor("#058C42"),
                amount = recovered.toFloat()
            )
        )

    }
    private fun getLastLocation()
    {
        if(checkpermission())
        {
            if(isLocationEnabled())
            {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task->
                    var location = task.result
                    if(location == null)
                    {
                        getNewLocation()
                    }else
                    {
                        cityName.text = stateName(location.latitude,location.longitude)
                        //Log.d("locationCheck", "Latitude"+location.latitude + " Longitude "
                        // + location.longitude + "\n CityName " + cityName(location.latitude,location.longitude)+" StateName" +stateName(location.latitude,location.longitude))
                        //getCases()
                    }
                }
            }else
            {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Location is not enabled")
                builder.setMessage("Enable location to view cases in your state")
                builder.setIcon(android.R.drawable.ic_dialog_alert)
                builder.setPositiveButton("Yes"){ dialogInterface, which->
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))

                }
                builder.setNegativeButton("No"){ dialogInterface, which->
                    // Log.d("locationCheck","User clicked No")
                }
                val alertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.show()

            }
        }else
        {
            RequestPermission()
        }
    }
    private fun checkpermission():Boolean{
        if(
            ActivityCompat.checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        ){
            return true
        }
        return false
    }

    private fun RequestPermission(){
        if(checkPer <3)
        {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION),LocationPermissionID)
            recall()
            Log.d("locationCheck","Value of check per $checkPer")
            checkPer++

        }


    }
    private  fun recall()
    {
        handler =  Handler(Looper.getMainLooper())
        handler.postDelayed({
            getLastLocation()

        },4000)

    }

    private fun isLocationEnabled():Boolean{
        var locationManager:LocationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == LocationPermissionID)
        {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                //Log.d("locationCheck", "You have permission")

            }
        }
    }
    private fun getNewLocation()
    {
        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            interval= updateInterval
            fastestInterval = fastInterval
            smallestDisplacement = displacement
        }
        fusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest,locationCallback, Looper.getMainLooper()
        )
    }
    private val locationCallback = object : LocationCallback()
    {
        override fun onLocationResult(p0: LocationResult) {
            var lastLocation = p0.lastLocation
            cityName.text = stateName(lastLocation.latitude,lastLocation.longitude)
            //Log.d("locationCheck", "Latitude"+lastLocation.latitude + " Longitude " +
                   // lastLocation.longitude + "\n CityName " + cityName(lastLocation.latitude,
                //lastLocation.longitude)+" StateName" +stateName(lastLocation.latitude,lastLocation.longitude))

        }
    }
    /*private fun cityName(lat:Double,long:Double):String
    {
        var cityname=""
        var geocoder = Geocoder(requireContext(), Locale.getDefault())
        var address = geocoder.getFromLocation(lat,long,1)
        cityname = address[0].locality
        return cityname
    }*/
    private fun stateName(lat: Double, long: Double): String {
        var statename = ""
        var geocoder = Geocoder(requireContext(), Locale.getDefault())
        var address = geocoder.getFromLocation(lat, long, 1)
        statename = address[0].adminArea
        return stateNameChanger(statename)
    }

    private fun stateNameChanger(state:String):String {
        Thread(Runnable {
            val map = mapOf("Andaman and Nicobar Islands" to "AN","Andhra Pradesh" to "AP", "Arunachal Pradesh" to "AR",
                "Assam" to "AS","Bihar" to "BR","Chhattisgarh" to "CT", "Chandigarh" to "CH","Delhi" to "DL",
                "Dadra and Nagar Haveli and Daman and Diu" to "DN", "Goa" to "GA", "Gujarat" to "GJ",
                "Himachal Pradesh" to "HP", "Haryana" to "HR", "Jharkhand" to "JH", "Jammu and Kashmir" to "JK",
                "Karnataka" to "KA", "Kerala" to "KL", "Ladakh" to "LA", "Lakshadweep" to "LD", "Maharashtra" to "MH",
                "Meghalaya" to "ML", "Manipur" to "MN", "Madhya Pradesh" to "MP", "Mizoram" to "MZ", "Nagaland" to "NL",
                "Odisha" to "OR", "Punjab" to "PB","Puducherry" to "PY", "Rajasthan" to "RJ", "Sikkim" to "SK",
                "Telangana" to "TG","Tamil Nadu" to "TN","Tripura" to "TR", "Uttar Pradesh" to "UP",
                "Uttarakhand" to "UT", "West Bengal" to "WB")
            Handler(Looper.getMainLooper()).postDelayed({
                var     stateName:String = map.getValue(state)
                var count = 0
                activity?.runOnUiThread { Log.d("locationCheck", "State new name is $stateName "+ count++)}
                getCases(stateName)

            },1000)


        }).start()

        return state
    }

    private fun getCases(state:String)
    {
        Thread(Runnable {

            val url = "https://api.covid19india.org/v4/min/data.min.json"

            val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    var stateData = response.getJSONObject(state)
                    var totalSection = stateData.getJSONObject("total")
                    activeCase = totalSection.getString("confirmed")
                    recovered = totalSection.getString("recovered")
                    death = totalSection.getString("deceased")
                    totalCase = (activeCase.toInt()+recovered.toInt()+death.toInt()).toString()
                    totalCasesText.text = totalCase
                    activeCasesText.text = activeCase
                    deathText.text = death
                    recoveredText.text = recovered
                    donut_view.cap = totalCase.toFloat()
                    donut_view.submitData(getSections())
                    //checkVal = totalCase.toFloat()
                },
                Response.ErrorListener { error ->
                    // TODO: Handle error
                }
            )

            MySingleton.getInstance(requireContext()).addToRequestQueue(jsonObjectRequest)
            Handler(Looper.getMainLooper()).postDelayed({
                //activity?.runOnUiThread { Log.d("locationCheck", "Confirmed case: $activeCase, Recovered Cases: $recovered, Death: $death, Total Cases: $totalCase") }
            },500)


        }).start()


    }


}