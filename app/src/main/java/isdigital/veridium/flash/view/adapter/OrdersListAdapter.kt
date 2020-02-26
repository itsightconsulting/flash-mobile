package isdigital.veridium.flash.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import isdigital.veridium.flash.FlashApplication
import isdigital.veridium.flash.R
import isdigital.veridium.flash.model.parcelable.OrderInformationArgs
import isdigital.veridium.flash.util.RecyclerViewOnItemClickListener
import isdigital.veridium.flash.util.changeDateFormat
import kotlinx.android.synthetic.main.item_order.view.*
import java.util.*

class OrdersListAdapter(
    private val ordersList: ArrayList<OrderInformationArgs>
    , private val itemClickListener: RecyclerViewOnItemClickListener<OrderInformationArgs>
) : RecyclerView.Adapter<OrdersListAdapter.ContactViewHolder>() {

    private val hourFormat = "HH"
    private val hourMinuteFormat = "HH:mm"

    private val resources = FlashApplication.appContext.resources

    fun updateordersList(newordersList: List<OrderInformationArgs>) {
        ordersList.clear()
        ordersList.addAll(newordersList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_order, parent, false)
        return ContactViewHolder(view)
    }

    override fun getItemCount() = ordersList.size

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        if (ordersList[position].wantPortability) {
            holder.view.orderType.text = resources.getString(R.string.web_activation_order_type_ph)

            holder.view.orderNumber.text = resources.getString(R.string.web_activation_order_number_ph) + ordersList[position].phoneNumber
        } else holder.view.orderType.text = resources.getString(R.string.web_activation_order_new)

        val orderDate = changeDateFormat(
            ordersList[position].formCreationDate!!,
            resources.getString(R.string.datetime_format_one),
            resources.getString(R.string.datetime_format_two)
        )

        val orderDateArr = orderDate.split(" ")
        val horas = changeDateFormat(orderDateArr[1], hourFormat, hourMinuteFormat).toInt()
        var formatoAMPM = ""
        if (horas in 0..11)
            formatoAMPM = " AM"
        else if (horas in 12..24)
            formatoAMPM = " PM"

        holder.view.orderDate.text = "${orderDateArr[0]} a las ${orderDateArr[1]} $formatoAMPM"
        val ordersList = ordersList[position]
        holder.bind(ordersList, itemClickListener)
    }

    class ContactViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

        fun bind(
            posApplicant: OrderInformationArgs,
            clickListener: RecyclerViewOnItemClickListener<OrderInformationArgs>
        ) {
            itemView.setOnClickListener {
                clickListener.onItemClicked(posApplicant)
            }

            itemView.btnContactCall.setOnClickListener {
                clickListener.onCallButtonClicked(posApplicant)
            }
        }
    }
}