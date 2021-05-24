package com.example.covs

import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import java.util.*


class HomeFragment : Fragment() {
    var LocationPermissionID =1000
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest

    /*private fun showPermissionRequestExplanation(
        permission: String,
        message: String,
        retry: (() -> Unit)? = null
    ) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("$permission Required")
            setMessage(message)
            setPositiveButton("Ok") { _, _ -> retry?.invoke() }
        }.show()
    }
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>*/
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
      var view:View =  inflater.inflate(R.layout.fragment_home, container, false)
        fusedLocationProviderClient =LocationServices.getFusedLocationProviderClient(requireContext())

       /* requestPermissionLauncher =
            this.registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    //showPermissionRequestExplanation("Location Permission Required","Need location permission to make app function properly"){requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)}
                    //requestLocationPermission()
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }*/

        // We need to use getLastLocation

        /*var button:Button = view.findViewById(R.id.button2)
        button.setOnClickListener(View.OnClickListener { view->
            requestLocationPermission()
        })*/
        /*view.button2.setOnClickListener {
            getlastlocation()
        }*/


        return view
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
                        Log.d("locationCheck", "Latitude"+location.latitude + " Longitude "
                                + location.longitude + "\n CityName " + cityName(location.latitude,location.longitude)+" StateName" +stateName(location.latitude,location.longitude))
                    }
                }
            }else
            {
                Log.d("locationCheck","Location is not enabled")
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
    /*private fun requestLocationPermission()
    {
        when {
            ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("locationCheck","Location is granted")
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION)->
            {
                showPermissionRequestExplanation("Location Permission Required","Need location permission to make app function properly"){requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)}

            }
            else ->{
                requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
            }
        }
    }*/
    private fun RequestPermission(){
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
            interval = 100
            fastestInterval  = 50
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            maxWaitTime = 100
        }
        fusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest,locationCallback, Looper.getMainLooper()
        )
    }
    private val locationCallback = object : LocationCallback()
    {
        override fun onLocationResult(p0: LocationResult) {
            var lastLocation = p0.lastLocation
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