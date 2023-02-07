package com.example.app_tp1

import android.os.Parcel
import android.os.Parcelable

class Station(var code: String, var libelle:String, var long:String, var lat:String):Parcelable{

    constructor(parcel: Parcel) : this(
        parcel.readString() as String,
        parcel.readString() as String,
        parcel.readString() as String,
        parcel.readString() as String
    ) {
    }

    override fun toString(): String {
        return libelle
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(code)
        parcel.writeString(libelle)
        parcel.writeString(long)
        parcel.writeString(lat)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Station> {
        override fun createFromParcel(parcel: Parcel): Station {
            return Station(parcel)
        }

        override fun newArray(size: Int): Array<Station?> {
            return arrayOfNulls(size)
        }
    }
}