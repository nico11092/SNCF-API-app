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
import org.json.JSONTokener
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private var ListStation = ArrayList<Station>()
    private var ListTrain = ArrayList<Train>()
    private lateinit var RecherStation:Station
    var listItems = ArrayList<String>()
    var client = OkHttpClient()
    var adapter:MyAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listView:ListView = findViewById(R.id.result)
        adapter = MyAdapter(listItems, this)
        listView.setAdapter(adapter)

        //initialisation de la liste des stations
        this.initListStations()

        //creation de la recherche
        val saisie:AutoCompleteTextView = findViewById(R.id._saisie)
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ListStation)
        saisie.setAdapter(arrayAdapter)

        //event sur le saisie d'une ville dans la recherche
        saisie.setOnItemClickListener{parent, view, position, id ->
            //recuperation du nom de la ville
            val nom_ville = parent.getItemAtPosition(position).toString()

            //on recupere la sation rechercher
            this.recherche(nom_ville)

            //on chercher les données
            this.run()
        }

        listView.onItemClickListener = AdapterView.OnItemClickListener {  parent, _, position, _ ->
            val intent = Intent(this, MapsActivity::class.java)
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

    private fun recherche(nom:String){
        for(station in ListStation){
            if(station.libelle == nom){
                RecherStation = station
            }
        }
    }

    private fun run(){
        val request = Request.Builder().url("https://api.sncf.com/v1/coverage/sncf/stop_areas/stop_area:SNCF:${RecherStation.code}/departures?datetime=20230120T090721&key=e0cae2da-8d96-4738-8060-a0c8a0d4e4da").build()

        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {}

            override fun onResponse(call: Call, response: Response) {

                val reponse: String? = response.body?.string()

               update(reponse)
            }
        })
    }

    private fun update(response:String?){
        ListTrain.clear()
        val jsonObject = JSONTokener(response).nextValue() as JSONObject

        //On cherche les départ
        val jsonArray = jsonObject.getJSONArray("departures") as JSONArray

        for (i in 0 until jsonArray.length()){
            if(i < 8){
                val display_info = jsonArray.getJSONObject(i).getJSONObject("display_informations")
                val stop_date = jsonArray.getJSONObject(i).getJSONObject("stop_date_time")

                val numero = display_info.getString("trip_short_name")
                val direction = display_info.getString("direction")
                val date_heure = stop_date.getString("departure_date_time")


                val heure:String = date_heure.substring(9, 11)
                val minute = date_heure.substring(11, 13)

                val train = Train(numero, direction, heure, minute)
                ListTrain.add(train)
            }
        }

        this@MainActivity.runOnUiThread(java.lang.Runnable {
            listItems.clear()
            for(i in 0 until ListTrain.size){
                listItems.add(ListTrain[i].toString())
            }
            adapter?.notifyDataSetChanged()
        })
    }
}