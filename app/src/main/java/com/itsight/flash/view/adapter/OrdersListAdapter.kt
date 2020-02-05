package com.itsight.flash.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.itsight.flash.R
import com.itsight.flash.model.pojo.OrderPOJO
import com.itsight.flash.util.RecyclerViewOnItemClickListener
import kotlinx.android.synthetic.main.item_order.view.*
import java.util.*

class OrdersListAdapter(
    val ordersList: ArrayList<OrderPOJO>
    , val itemClickListener: RecyclerViewOnItemClickListener<OrderPOJO>
) : RecyclerView.Adapter<OrdersListAdapter.ContactViewHolder>() {

    fun updateordersList(newordersList: List<OrderPOJO>) {
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
        holder.view.orderType.text = ordersList[position].type
        holder.view.orderDate.text = ordersList[position].timestamp
        holder.view.orderNumber.text = ordersList[position].number
        val ordersList = ordersList[position]
        holder.bind(ordersList, itemClickListener)
    }

    class ContactViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        private val type: TextView = view.orderType

        fun bind(
            posApplicant: OrderPOJO,
            clickListener: RecyclerViewOnItemClickListener<OrderPOJO>
        ) {
            type.text = posApplicant.type

            itemView.setOnClickListener {
                clickListener.onItemClicked(posApplicant)
            }

            itemView.btnContactCall.setOnClickListener {
                clickListener.onCallButtonClicked(posApplicant)
            }
        }
    }
}