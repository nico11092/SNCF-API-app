package com.example.app_tp1

class Stop(var hourArrival:String?, var minuteArrival:String?, var hourDeparture:String?, var minuteDeparture:String?, var station:Station ) {
    override fun toString(): String {
        return "Stop(hourArrival=$hourArrival, minuteArrival=$minuteArrival, hourDeparture=$hourDeparture, minuteDeparture=$minuteDeparture, station=$station)"
    }
}