package isdigital.veridium.flash.view


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import isdigital.veridium.flash.R
import isdigital.veridium.flash.util.RecyclerViewOnItemClickListener
import isdigital.veridium.flash.view.adapter.OrdersListAdapter
import isdigital.veridium.flash.viewmodel.OrderViewModel
import kotlinx.android.synthetic.main.orders_fragment.*
import isdigital.veridium.flash.model.pojo.ActivationPOJO

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

        //if (orderViewModel.userHasOrders) { ArrayList<ActivationPOJO>()
        // arrayListOf(this.orderViewModel.lstOrder)

        ordersListAdapter = OrdersListAdapter(
            arrayListOf()
            , object : RecyclerViewOnItemClickListener<ActivationPOJO> {
                override fun onItemClicked(posApplicant: ActivationPOJO) {
                    Toast.makeText(context!!, "${posApplicant.formId}", Toast.LENGTH_SHORT)
                        .show()
//                    findNavController().navigate(action)
//                    Navigation.createNavigateOnClickListener(R.id.action_contactsFragment_to_contactProfileFragment)
                }

                override fun onCallButtonClicked(posApplicant: ActivationPOJO) {
                    Toast.makeText(context!!, "${posApplicant.formId}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        )
        ordersList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ordersListAdapter
        }

        //} else {

        //}
//        Toast.makeText(context!!, "${safeArgs.dni}", Toast.LENGTH_SHORT).show()
//        Toast.makeText(context!!, "${safeArgs.orders!!.list}", Toast.LENGTH_SHORT).show()

    }

    fun bindOrders() {
        this.orderViewModel.loadError.observe(this, Observer { loadError ->
            loadError?.let {
                if (loadError) {
                    ordersListAdapter.updateordersList(orderViewModel.lstOrder.value!!)
                    //ordersError.visibility = View.VISIBLE
                    ordersList.visibility = View.GONE
                    //pbOrders.visibility = View.GONE
                }
            }
        })

        orderViewModel.loading.observe(this, Observer { loading ->
            loading?.let {
                if (!loading) {
                    ordersListAdapter.updateordersList(orderViewModel.lstOrder.value!!)
                    ordersList.visibility = View.VISIBLE
                    //ordersError.visibility = View.GONE
                    //pbOrders.visibility = View.GONE
                }
            }
        })
    }
}

/*
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
            )
 */
