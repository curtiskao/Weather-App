package com.example.weatherapp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
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
    private var cityName = "";
    private var temp = "";
    private var windSpeed = "";
    private var weather = "";
    private var humidity = "";
    private var app_temp = "";
    private var dayTime = false
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

        getLocation()
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

    //wind_spd: Wind speed (Default m/s).
    //app_temp: Apparent/"Feels Like" temperature (default Celcius).
    //rh: Relative humidity (%).

    private fun fetchTemperatureAPI(){
        val dialog = ProgressDialog(context)
        dialog.setMessage("Fetching weather data...")
        dialog.show()
        val queue = Volley.newRequestQueue(context)
        val stringRequest = StringRequest(Request.Method.GET, weather_url, { response ->
            dialog.dismiss()
            Log.d("url","api success")
            val respObj = JSONObject(response)
            var jsonObject = respObj.getJSONArray("data").getJSONObject(0)
            Log.d("url jsonobject",jsonObject.toString())
            cityName = jsonObject.getString("city_name")
            temp = jsonObject.getString("temp")
            windSpeed = jsonObject.getString("wind_spd")
            weather = jsonObject.getJSONObject("weather").getString("description")
            humidity = jsonObject.getString("rh")
            app_temp = jsonObject.getString("app_temp")
            dayTime = jsonObject.getString("pod").equals("d")

            setScreen()


        }, {
            dialog.dismiss()
            Log.d("url","api fail")
        })

        queue.add(stringRequest)
    }

    private fun setScreen(){
        val tv_location = view?.findViewById<TextView>(R.id.tv_location)
        tv_location?.text = cityName
        val tv_temperature = view?.findViewById<TextView>(R.id.tv_temperature)
        tv_temperature?.text = temp +" °C"
        val tv_weather = view?.findViewById<TextView>(R.id.tv_weather_desc)
        tv_weather?.text = weather
        val tv_humidity = view?.findViewById<TextView>(R.id.tv_humidity)
        tv_humidity?.text = "Humidity: $humidity %"
        val tv_windspeed = view?.findViewById<TextView>(R.id.tv_wind_speed)
        tv_windspeed?.text = "Wind speed $windSpeed m/s"
        val tv_app_temp = view?.findViewById<TextView>(R.id.tv_app_temp)
        tv_app_temp?.text = "Feels like: $app_temp °C"

        val button_refresh = view?.findViewById<Button>(R.id.button_refresh)
        val weatherLayout = view?.findViewById<ConstraintLayout>(R.id.weather_layout)
        if(dayTime){
            button_refresh?.setBackgroundColor(resources.getColor(R.color.blue))
            weatherLayout?.setBackgroundColor(resources.getColor(R.color.lightBlue))
        }else{
            button_refresh?.setBackgroundColor(resources.getColor(R.color.darkerGrey))
            weatherLayout?.setBackgroundColor(resources.getColor(R.color.grey))
        }

    }


}