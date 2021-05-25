package com.example.covs

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import app.futured.donut.DonutSection
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*


class HomeFragment : Fragment() {
    var LocationPermissionID =1000
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view:View = inflater.inflate(R.layout.fragment_home, container, false)
        view.donut_view.cap = 1f
        view.donut_view.submitData(getSections())
        fusedLocationProviderClient =LocationServices.getFusedLocationProviderClient(requireContext())
        getlastlocation()

        return view
    }

    private fun getSections(): List<DonutSection>{
        return listOf(
            DonutSection(
                name = "Active",
                color = Color.parseColor("#FCEC52"),
                amount = 1f
            ),
            DonutSection(
                name = "Recovered",
                color = Color.parseColor("#058C42"),
                amount = 2f
            ),
            DonutSection(
                name = "Death",
                color = Color.parseColor("#FF0000"),
                amount = 1f
            )
        )

    }
    private fun getlastlocation()
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
                        Log.d("locationCheck", "Latitude"+location.latitude + " Longitude "
                                + location.longitude + "\n CityName " + cityName(location.latitude,location.longitude)+" StateName" +stateName(location.latitude,location.longitude))
                    }
                }
            }else
            {
               val builder = AlertDialog.Builder(requireContext())
               builder.setTitle("Location is not enabled")
               builder.setMessage("Enable location to view cases in your state")
                builder.setIcon(android.R.drawable.ic_dialog_alert)
                builder.setPositiveButton("Yes"){ dialoginterface, which->
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))

                }
                builder.setNegativeButton("No"){ dialoginterface, which->
                    Log.d("locationCheck","User clicked No")
                }
                val alertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.show()

            }
        }
        else
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
        runBlocking {
            launch {
                delay(10000)
                getlastlocation()
            }
        }
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION),LocationPermissionID)

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
                Log.d("locationCheck", "You have permission")

            }
        }
    }
    private fun getNewLocation()
    {
        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
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
            Log.d("locationCheck", "Latitude"+lastLocation.latitude + " Longitude " +
                    lastLocation.longitude + "\n CityName " + cityName(lastLocation.latitude,
                lastLocation.longitude)+" StateName" +stateName(lastLocation.latitude,lastLocation.longitude))

        }
    }
    private fun cityName(lat:Double,long:Double):String
    {
        var cityname=""
        var geocoder = Geocoder(requireContext(), Locale.getDefault())
        var address = geocoder.getFromLocation(lat,long,1)
        cityname = address[0].locality
        return cityname
    }
    private fun stateName(lat:Double,long:Double):String
    {
        var statename=""
        var geocoder = Geocoder(requireContext(), Locale.getDefault())
        var address = geocoder.getFromLocation(lat,long,1)
        statename = address[0].adminArea
        return statename
    }

}