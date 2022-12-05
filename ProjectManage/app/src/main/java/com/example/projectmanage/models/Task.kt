package com.example.projectmanage.models

import android.os.Parcel
import android.os.Parcelable

data class Task(
    var title:String = "",
    var createdBy:String = "",
    var cardList:ArrayList<Card> = ArrayList()
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Card.CREATOR)!! as ArrayList<Card>
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(createdBy)
        parcel.writeTypedList(cardList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}
