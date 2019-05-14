package com.example.aplicationpokedex.Models

import android.os.Parcel
import android.os.Parcelable

data class Coin (
        val _id:String?,
        val nombre:String?,
        val country:String?,
        var value:Int?,
        val value_us:Int?,
        var year:Int?,
        var review:String?,
        var available:Boolean?,
        var img:String?

) : Parcelable {
    constructor(parcel: Parcel) : this(
            _id = parcel.readString(),
            nombre = parcel.readString(),
            country = parcel.readString(),
            value = parcel.readInt(),
            value_us = parcel.readInt(),
            year = parcel.readInt(),
            review = parcel.readString(),
            available = parcel.readInt() == 1,
            img = parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(_id)
        parcel.writeString(nombre)
        parcel.writeString(country)
        parcel.writeValue(value)
        parcel.writeValue(value_us)
        parcel.writeValue(year)
        parcel.writeString(review)
        parcel.writeValue(available)
        parcel.writeString(img)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Coin> {
        override fun createFromParcel(parcel: Parcel): Coin {
            return Coin(parcel)
        }

        override fun newArray(size: Int): Array<Coin?> {
            return arrayOfNulls(size)
        }
    }
}