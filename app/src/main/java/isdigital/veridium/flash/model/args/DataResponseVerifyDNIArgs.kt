package isdigital.veridium.flash.model.args

import android.os.Parcel
import android.os.Parcelable
import isdigital.veridium.flash.model.parcelable.OrderInformationArgs
import isdigital.veridium.flash.util.readBooleanMe
import isdigital.veridium.flash.util.writeBooleanMe
import java.util.*

data class DataResponseVerifyDNIArgs(val status: Boolean, val list: ArrayList<OrderInformationArgs>):Parcelable{

    constructor(parcel: Parcel) : this(
        parcel.readBooleanMe(),
        parcel.createTypedArrayList(OrderInformationArgs.CREATOR)!!
    )


    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeBooleanMe(status)
        dest.writeTypedList(list)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DataResponseVerifyDNIArgs> {
        override fun createFromParcel(parcel: Parcel): DataResponseVerifyDNIArgs {
            return DataResponseVerifyDNIArgs(parcel)
        }

        override fun newArray(size: Int): Array<DataResponseVerifyDNIArgs?> {
            return arrayOfNulls(size)
        }
    }

}