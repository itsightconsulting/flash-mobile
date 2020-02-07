package com.itsight.flash.model.parcelable

import android.os.Parcel
import android.os.Parcelable

data class OrderInformationArgs(val id: String?,
                                val planType: String?,
                                val sponsorTeamId: String?,
                                val name: String?,
                                val lastName: String?,
                                val birthDate: String?,
                                val email: String?,
                                val wantPortability: Boolean,
                                val phoneNumber: String,
                                val currentCompany: String?,
                                val status: String?,
                                val creationDate: String?): Parcelable{

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readBoolean(),
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readString())


    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(planType)
        dest.writeString(sponsorTeamId)
        dest.writeString(name)
        dest.writeString(lastName)
        dest.writeString(birthDate)
        dest.writeString(email)
        dest.writeBoolean(wantPortability)
        dest.writeString(phoneNumber)
        dest.writeString(currentCompany)
        dest.writeString(status)
        dest.writeString(creationDate)
    }

    override fun describeContents(): Int {
        return 0
    }


    companion object CREATOR : Parcelable.Creator<OrderInformationArgs> {
        override fun createFromParcel(parcel: Parcel): OrderInformationArgs {
            return OrderInformationArgs(parcel)
        }

        override fun newArray(size: Int): Array<OrderInformationArgs?> {
            return arrayOfNulls(size)
        }
    }
}