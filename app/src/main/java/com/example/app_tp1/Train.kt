package com.example.app_tp1

class Train(val numero:String, val destination:String, val heure:String, val minute:String) {
    override fun toString(): String {
        return heure + "h" + minute + " - " + destination + " : " + numero
    }
}