package com.example.weatherapp

import android.annotation.SuppressLint
import android.app.Activity
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject


class FirstFragment : Fragment() {

    var weather_url = ""
    var api_id = "030314b750cc43e7b39e503dfe37150c"
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity as Activity)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_refresh).setOnClickListener {
            getLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(){

        fusedLocationClient.lastLocation.addOnFailureListener {
            Log.d("url fail", it.toString())

        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            // get the latitude and longitude
            // and create the http URL
            weather_url = "https://api.weatherbit.io/v2.0/current?" + "lat=" + location?.latitude + "&lon=" + location?.longitude + "&key=" + api_id
            Log.d("url", weather_url)
            fetchTemperatureAPI()
        }
    }

    private fun fetchTemperatureAPI(){
        val queue = Volley.newRequestQueue(context)
        val stringRequest = StringRequest(Request.Method.GET, weather_url, { response ->
            Log.d("url","api success")
            val respObj = JSONObject(response)
            Log.d("url",respObj.toString())

        }, {
            Log.d("url","api fail")
        })

        queue.add(stringRequest)
    }


}