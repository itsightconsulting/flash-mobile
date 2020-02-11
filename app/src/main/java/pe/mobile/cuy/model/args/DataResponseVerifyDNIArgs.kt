package pe.mobile.cuy.model.args

import android.os.Parcel
import android.os.Parcelable
import pe.mobile.cuy.model.parcelable.OrderInformationArgs
import java.util.*

data class DataResponseVerifyDNIArgs(val status: Boolean, val list: ArrayList<OrderInformationArgs>):Parcelable{

    constructor(parcel: Parcel) : this(
        parcel.readBoolean(),
        parcel.createTypedArrayList(OrderInformationArgs.CREATOR)!!
    )


    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeBoolean(status)
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