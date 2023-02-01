package com.example.app_tp1

class Train(val numero:String, val destination:String, val heure:String, val minute:String) {
    var stops = ArrayList<Stop>()

    fun addStop(stop: Stop, departureStation:Boolean, arrivalStation:Boolean){
        stops.add(stop)

        //depart
        if(departureStation){
            //ajout from
        }

        //arrive
        if(arrivalStation){
            //ajout to
        }
    }

    override fun toString(): String {
        return heure + "h" + minute + " - " + destination + " : " + numero
    }
}