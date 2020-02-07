package com.itsight.flash.view


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.itsight.flash.R
import com.itsight.flash.model.pojo.OrderPOJO
import com.itsight.flash.util.RecyclerViewOnItemClickListener
import com.itsight.flash.util.showSpinner
import com.itsight.flash.view.adapter.OrdersListAdapter
import com.itsight.flash.viewmodel.OrderViewModel
import kotlinx.android.synthetic.main.orders_fragment.*

/**
 * A simple [Fragment] subclass.
 */
class OrdersFragment : Fragment() {

    private lateinit var ordersListAdapter: OrdersListAdapter
    private lateinit var orderViewModel: OrderViewModel
    private val safeArgs: OrdersFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.orders_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.orderViewModel = ViewModelProviders.of(this).get(OrderViewModel::class.java)

        ordersListAdapter = OrdersListAdapter(
            arrayListOf(
                OrderPOJO(
                    "Activation & portability",
                    "15/01/2020 at 09:45 am",
                    "Phone number: 94483-8302\n"
                ),
                OrderPOJO("New phone number activation ", "18/01/2020 at 02:10 pm", ""),
                OrderPOJO(
                    "Activation & portability",
                    "18/01/2020 at 05:34 pm",
                    "Phone number: 92726-3521"
                ),
                OrderPOJO("New phone number activation ", "18/01/2020 at 02:10 pm", ""),
                OrderPOJO(
                    "Activation & portability",
                    "18/01/2020 at 05:34 pm",
                    "Phone number: 92726-3521"
                ),
                OrderPOJO("New phone number activation ", "18/01/2020 at 02:10 pm", "")
            ),
            object : RecyclerViewOnItemClickListener<OrderPOJO> {
                override fun onItemClicked(posApplicant: OrderPOJO) {
                    Toast.makeText(context!!, "${posApplicant.timestamp}", Toast.LENGTH_SHORT)
                        .show()
//                    findNavController().navigate(action)
//                    Navigation.createNavigateOnClickListener(R.id.action_contactsFragment_to_contactProfileFragment)
                }

                override fun onCallButtonClicked(posApplicant: OrderPOJO) {
                    Toast.makeText(context!!, "${posApplicant.timestamp}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        )
        ordersList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ordersListAdapter
        }
        Toast.makeText(context!!, "${safeArgs.dni}", Toast.LENGTH_SHORT).show()
        Toast.makeText(context!!, "${safeArgs.orders!!.list}", Toast.LENGTH_SHORT).show()

    }
}
