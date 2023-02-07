package com.example.app_tp1

import android.os.Parcel
import android.os.Parcelable

class Train(val num:String, val type:String, val localHour:String, val localMinute:String): Parcelable{
    var from: Stop? = null
    var to:Stop? = null
    var stops = ArrayList<Stop>()

    constructor(parcel: Parcel) : this(
        parcel.readString() as String,
        parcel.readString() as String,
        parcel.readString() as String,
        parcel.readString() as String
    ) {
        from = parcel.readParcelable(Stop::class.java.classLoader)
        to = parcel.readParcelable(Stop::class.java.classLoader)
        parcel.readList(stops, Stop::class.java.classLoader)
    }

    fun addStop(stop: Stop, departureStation:Boolean, arrivalStation:Boolean){
        stops.add(stop)

        //depart
        if(departureStation){
            from = stop
        }

        //arrive
        if(arrivalStation){
            to = stop
        }
    }

    override fun toString(): String {
        return localHour+"h"+localMinute+" - "+ to.toString() +"\n"+type+" "+num
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(num)
        parcel.writeString(type)
        parcel.writeString(localHour)
        parcel.writeString(localMinute)
        parcel.writeParcelable(from, flags)
        parcel.writeParcelable(to, flags)
        parcel.writeList(stops)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Train> {
        override fun createFromParcel(parcel: Parcel): Train {
            return Train(parcel)
        }

        override fun newArray(size: Int): Array<Train?> {
            return arrayOfNulls(size)
        }
    }
}