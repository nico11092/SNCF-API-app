package com.example.app_tp1

open class Station (var code:String, var libelle:String, var long:String, var lat:String){
    override fun toString(): String {
        return libelle
    }
}