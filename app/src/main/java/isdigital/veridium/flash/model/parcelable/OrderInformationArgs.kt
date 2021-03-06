package isdigital.veridium.flash.model.parcelable

import android.os.Parcel
import android.os.Parcelable
import isdigital.veridium.flash.util.readBooleanMe
import isdigital.veridium.flash.util.writeBooleanMe

data class OrderInformationArgs(
    val id: String?,
    val planType: String?,
    val sponsorTeamId: String?,
    val name: String?,
    val paternal_lastName: String?,
    val maternal_lastName: String?,
    val birthDate: String?,
    val email: String?,
    val wantPortability: Boolean,
    val phoneNumber: String?,
    val currentCompany: String?,
    val status: String?,
    val formCreationDate: String?,
    var populatedCenter: String?,
    var coveragePopulatedCenter: String?,
    var acceptTermsCoveragePopulatedCenter: String?
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readBooleanMe(),
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )


    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(planType)
        dest.writeString(sponsorTeamId)
        dest.writeString(name)
        dest.writeString(paternal_lastName)
        dest.writeString(maternal_lastName)
        dest.writeString(birthDate)
        dest.writeString(email)
        dest.writeBooleanMe(wantPortability)
        dest.writeString(phoneNumber)
        dest.writeString(currentCompany)
        dest.writeString(status)
        dest.writeString(formCreationDate)
        dest.writeString(populatedCenter)
        dest.writeString(coveragePopulatedCenter)
        dest.writeString(acceptTermsCoveragePopulatedCenter)
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