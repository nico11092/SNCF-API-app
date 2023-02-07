package com.example.app_tp1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ListView
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private var ListTrain = ArrayList<Train>()
    private var ListStation = ArrayList<Station>()
    private var ListLink = ArrayList<String>()
    private lateinit var arrayAdapterTrain:ArrayAdapter<Train>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //initialisation de la liste des stations
        this.initListStations()

        //on ajoute la liste de la station à la recherche
        val saisie:AutoCompleteTextView = findViewById(R.id._saisie)
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ListStation)
        saisie.setAdapter(arrayAdapter)

        //on creer l'adapter sur la listview
        val listView:ListView = findViewById(R.id.result)
        arrayAdapterTrain = ArrayAdapter<Train>(this, android.R.layout.simple_list_item_1, ListTrain)
        listView.adapter = arrayAdapterTrain

        //evenement sur la recherche (selection d'une ville)
        saisie.setOnItemClickListener{parent, _, position, _ ->
            //on recuperer la station
            var station:Station = parent.getItemAtPosition(position) as Station

            //on recherche sur l'api
            getTrainApiSNCF(station)
        }

        listView.onItemClickListener = AdapterView.OnItemClickListener {  parent, _, position, _ ->
            val train_rechercher:Train = parent.getItemAtPosition(position) as Train
            println(train_rechercher.stops)

            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("train", train_rechercher)
            startActivity(intent)
        }
    }

    private fun initListStations(){
        val inputStream = resources.openRawResource(R.raw.gares)

        inputStream.bufferedReader().useLines { lines ->
            lines.forEach {
                val list = it.split(";")
                val station = Station(list[0].substring(2,list[0].length),list[1],list[2],list[3])
                ListStation.add(station)
            }
        }
    }

    private fun getTrainApiSNCF(station:Station) :Unit{
        val url = "https://api.sncf.com/v1/coverage/sncf/stop_areas/stop_area:SNCF:${station.code}/departures/?count=8&key=e0cae2da-8d96-4738-8060-a0c8a0d4e4da"
        println(url)
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("\n\n\n\n ERROR \n\n\n\n")
            }
            override fun onResponse(call: Call, response: Response) {
                showTrains(JSONObject(response.body?.string()))
            }
        })
    }

    private fun showTrains(response:JSONObject){
        //on vide la liste de train actuelle et la liste de lien
        ListTrain.clear()
        ListLink.clear()

        //On cherche les départ
        val listDepartures = response.getJSONArray("departures") as JSONArray

        for(i in 0 until 1) {
            val display_info = listDepartures.getJSONObject(i).getJSONObject("display_informations")
            val stop_date = listDepartures.getJSONObject(i).getJSONObject("stop_date_time")

            //information du train
            val num = display_info.getString("trip_short_name")
            val type = display_info.getString("commercial_mode")
            val localHour = stop_date.getString("departure_date_time").toString().substring(9, 11)
            val localMinute =
                stop_date.getString("departure_date_time").toString().substring(11, 13)
            val train = Train(num, type, localHour, localMinute)

            //info stop
            val route = listDepartures.getJSONObject(i).getJSONObject("route")
            val nom = route.getJSONObject("direction").getJSONObject("stop_area").getJSONArray("codes")
                    .getJSONObject(1).getString("value")
            val index = ListStation.indices.find { ListStation[it].code == nom }

            if (index != null) {
                val arret: Stop = Stop(null, null, null, null, ListStation.get(index))
                train.addStop(arret, false, true)
            }

            //lien vehicule_journey
            val lien = listDepartures.getJSONObject(i).getJSONArray("links").getJSONObject(1).getString("id")
            ListLink.add(lien)

            //ajout train list de train
            ListTrain.add(train)
        }

        this@MainActivity.runOnUiThread(java.lang.Runnable {
            arrayAdapterTrain.notifyDataSetChanged()
            getInformationForMap()
        })
    }

    private fun getInformationForMap(){
        for(i in 0 until ListLink.size) {
            val url = "https://api.sncf.com/v1/coverage/sncf/vehicle_journeys/${ListLink.get(i)}/?key=e0cae2da-8d96-4738-8060-a0c8a0d4e4da"
            println(url)
            val request = Request.Builder().url(url).build()
            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("\n\n\n\n ERROR \n\n\n\n")
                }

                override fun onResponse(call: Call, response: Response) {
                    showTrainsInformationMap(JSONObject(response.body?.string()), i)
                }
            })
        }
    }

    private fun showTrainsInformationMap(response:JSONObject, train:Int){
        ListTrain.get(train).stops.clear()

        //On cherche les vehicule_journeys
        val vehicule_journeys = response.getJSONArray("vehicle_journeys") as JSONArray

        val stop = vehicule_journeys.getJSONObject(0).getJSONArray("stop_times")
        for(i in 0 until stop.length()){
            val id = stop.getJSONObject(i).getJSONObject("stop_point").getString("id").substring(16,24)
            val departure_time = stop.getJSONObject(i).getString("departure_time")
            val arrival_time = stop.getJSONObject(i).getString("arrival_time")

            val localHour_dep = departure_time.substring(0, 2)
            val localMinute_dep = departure_time.substring(2, 4)
            val localHour_arr = arrival_time.substring(0, 2)
            val localMinute_arr = arrival_time.substring(2, 4)


            val index = ListStation.indices.find { ListStation[it].code == id }
            if(index != null) {
                if (i == 0) {
                    val depart:Stop = Stop(null, null, localHour_dep, localMinute_dep, ListStation.get(index))
                    ListTrain.get(train).addStop(depart, true, false)
                } else if (i == stop.length() - 1) {
                    val arrive:Stop = Stop(localHour_arr, localMinute_arr, null, null, ListStation.get(index))
                    ListTrain.get(train).addStop(arrive, false, true)
                } else {
                    val arret:Stop = Stop(localHour_arr, localMinute_arr, localHour_dep, localMinute_dep, ListStation.get(index))
                    ListTrain.get(train).addStop(arret, false, false)
                }
            }
        }
    }
}