package isdigital.veridium.flash.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import isdigital.veridium.flash.R
import isdigital.veridium.flash.model.parcelable.OrderInformationArgs
import isdigital.veridium.flash.util.RecyclerViewOnItemClickListener
import isdigital.veridium.flash.util.changeDateFormat
import kotlinx.android.synthetic.main.item_order.view.*
import java.util.*

class OrdersListAdapter(
    val ordersList: ArrayList<OrderInformationArgs>
    , val itemClickListener: RecyclerViewOnItemClickListener<OrderInformationArgs>
) : RecyclerView.Adapter<OrdersListAdapter.ContactViewHolder>() {

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
            holder.view.orderType.text = "Activación y portabilidad"
            holder.view.orderNumber.text = "Número telefónico: " + ordersList[position].phoneNumber
        } else holder.view.orderType.text = "Activación de nuevo número telefónico"

        val orderDate = changeDateFormat(
            ordersList[position].formCreationDate!!,
            "dd/MM/yyyy HH:mm",
            "yyyy-MM-dd HH:mm:ss"
        )
        val orderDateArr = orderDate.split(" ")
        holder.view.orderDate.text = orderDateArr[0] + " a las " + orderDateArr[1]
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