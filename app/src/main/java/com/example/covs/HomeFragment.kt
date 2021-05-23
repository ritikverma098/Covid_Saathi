package com.example.covs

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment


class HomeFragment : Fragment() {
    private fun showPermissionRequestExplanation(
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
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
      var view:View =  inflater.inflate(R.layout.fragment_home, container, false)
        requestPermissionLauncher =
            this.registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {

                    //requestLocationPermission()
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }
        //requestLocationPermission()
        var button:Button = view.findViewById(R.id.button2)
        button.setOnClickListener(View.OnClickListener { view->
            requestLocationPermission()
        })



        return view
    }
    private fun requestLocationPermission()
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
    }


}