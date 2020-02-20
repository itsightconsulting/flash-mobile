package pe.mobile.cuy.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pe.mobile.cuy.R
import pe.mobile.cuy.model.pojo.OrderPOJO
import pe.mobile.cuy.util.RecyclerViewOnItemClickListener
import kotlinx.android.synthetic.main.item_order.view.*
import pe.mobile.cuy.model.parcelable.OrderInformationArgs
import pe.mobile.cuy.model.pojo.ActivationPOJO
import pe.mobile.cuy.util.changeDateFormat
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
        if (ordersList[position].wantPortability){
            holder.view.orderType.text = "Activación y portabilidad"
            holder.view.orderNumber.text = "Número telefónico: " + ordersList[position].phoneNumber
        }
        else holder.view.orderType.text = "Activación de nuevo número telefónico"

        holder.view.orderDate.text = changeDateFormat(ordersList[position].formCreationDate!!,"dd/MM/yyyy HH:mm","yyyy-MM-dd HH:mm:ss") //  + " a las " + ordersList[position].formCreationDate
        val ordersList = ordersList[position]
        holder.bind(ordersList, itemClickListener)
    }

    class ContactViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        private val type: TextView = view.orderType

        fun bind(
            posApplicant: OrderInformationArgs,
            clickListener: RecyclerViewOnItemClickListener<OrderInformationArgs>
        ) {
            if (posApplicant.wantPortability)
                type.text = "Activación y portabilidad"
            type.text = "Activación de nuevo número telefónico"

            //type.text = posApplicant.type

            itemView.setOnClickListener {
                clickListener.onItemClicked(posApplicant)
            }

            itemView.btnContactCall.setOnClickListener {
                clickListener.onCallButtonClicked(posApplicant)
            }
        }
    }
}