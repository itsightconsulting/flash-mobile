package isdigital.veridium.flash.view


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import isdigital.veridium.flash.FlashApplication
import isdigital.veridium.flash.R
import isdigital.veridium.flash.model.args.DataResponseVerifyDNIArgs
import isdigital.veridium.flash.model.parcelable.OrderInformationArgs
import isdigital.veridium.flash.model.pojo.ActivationPOJO
import isdigital.veridium.flash.preferences.UserPrefs
import isdigital.veridium.flash.util.RecyclerViewOnItemClickListener
import isdigital.veridium.flash.view.adapter.OrdersListAdapter
import isdigital.veridium.flash.viewmodel.OrderViewModel
import kotlinx.android.synthetic.main.orders_fragment.*

/**
 * A simple [Fragment] subclass.
 */
class OrdersFragment : Fragment() {

    private lateinit var ordersListAdapter: OrdersListAdapter
    private lateinit var orderViewModel: OrderViewModel
    //private val safeArgs: OrdersFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.orders_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //if (orderViewModel.userHasOrders) { ArrayList<ActivationPOJO>()
        // arrayListOf(this.orderViewModel.lstOrder)
        val myArgs = arguments!!.getParcelable<DataResponseVerifyDNIArgs>("orders")

        ordersListAdapter = OrdersListAdapter(
            myArgs!!.list
            , object : RecyclerViewOnItemClickListener<OrderInformationArgs> {
                override fun onItemClicked(oOrderInformation: OrderInformationArgs) {
                    /*
                    Toast.makeText(
                        context!!,
                        "onItemClicked ${oOrderInformation.id}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                     */
                    selectOrder(oOrderInformation)
                }

                override fun onCallButtonClicked(oOrderInformation: OrderInformationArgs) {
                    /*Toast.makeText(
                        context!!,
                        "onCallButtonClicked ${posApplicant.id}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                     */
                    selectOrder(oOrderInformation)
                }
            }
        )
        orderViewModel = ViewModelProviders.of(this).get(OrderViewModel::class.java)
        ordersList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ordersListAdapter
        }
        //} else {

        //}
//        Toast.makeText(context!!, "${safeArgs.dni}", Toast.LENGTH_SHORT).show()
//        Toast.makeText(context!!, "${safeArgs.orders!!.list}", Toast.LENGTH_SHORT).show()

    }

    fun selectOrder(oOrderInformation: OrderInformationArgs) {

        val dni: String? = UserPrefs.getUserDni(FlashApplication.appContext)

        if (oOrderInformation.wantPortability) {
            val oActivationPOJO = ActivationPOJO(
                oOrderInformation.id!!,
                dni!!,
                oOrderInformation.name!!,
                oOrderInformation.lastName!!,
                oOrderInformation.birthDate!!,
                oOrderInformation.email!!,
                oOrderInformation.wantPortability,
                oOrderInformation.sponsorTeamId,
                oOrderInformation.phoneNumber,
                oOrderInformation.currentCompany,
                oOrderInformation.planType,
                oOrderInformation.formCreationDate
            )

            UserPrefs.putActivation(FlashApplication.appContext, oActivationPOJO)

        } else {
            val oActivationPOJO = ActivationPOJO(
                oOrderInformation.id!!,
                dni!!,
                oOrderInformation.name!!,
                oOrderInformation.lastName!!,
                oOrderInformation.birthDate!!,
                oOrderInformation.email!!,
                oOrderInformation.wantPortability,
                oOrderInformation.sponsorTeamId,
                null,
                null,
                null,
                oOrderInformation.formCreationDate
            )

            UserPrefs.putActivation(FlashApplication.appContext, oActivationPOJO)

        }

        val action = OrdersFragmentDirections.actionOrdersFragmentToTermsFragment()
        findNavController().navigate(action)
    }
}
