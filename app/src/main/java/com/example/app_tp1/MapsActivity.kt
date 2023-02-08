package com.example.app_tp1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.TextView

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.app_tp1.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.PolylineOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap



        val affichage: TextView = findViewById(R.id._affiche_train)


        val train = intent.extras?.get("train") as Train
        val dep:String = train.stops[0].station.toString()
        val arr:String = train.stops[train.stops.size-1].station.toString()

        affichage.setText(train.type + " n°"+train.num+"\n"+dep+" - "+arr)

        var lastInit:LatLng? = null

        for(i in 0 until train.stops.size) {
            val stop: Stop = train.stops.get(i)

            val lat = stop.station?.lat
            val long = stop.station?.long

            if(lat != null && long != null){
                val ville = LatLng(lat.toDouble(), long.toDouble())
                mMap.addMarker(MarkerOptions().position(ville).title("Marker in La Rochelle"))

                if(i != 0){
                    mMap.addPolyline(PolylineOptions().add(lastInit, ville))
                }

                lastInit = ville

            }
        }



            // Add a marker in Sydney and move the camera
        val sydney = LatLng(46.160329, -1.151139)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}