package com.example.app_tp1

import android.os.Parcel
import android.os.Parcelable

class Stop(var hourArrival:String?, var minuteArrival:String?, var hourDeparture:String?, var minuteDeparture:String?, var station: Station?):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Station::class.java.classLoader)!!
    ) {
    }

    override fun toString(): String {
        return station.toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(hourArrival)
        parcel.writeString(minuteArrival)
        parcel.writeString(hourDeparture)
        parcel.writeString(minuteDeparture)
        parcel.writeParcelable(station, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Stop> {
        override fun createFromParcel(parcel: Parcel): Stop {
            return Stop(parcel)
        }

        override fun newArray(size: Int): Array<Stop?> {
            return arrayOfNulls(size)
        }
    }
}