package pe.mobile.cuy.view


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import pe.mobile.cuy.R
import pe.mobile.cuy.model.args.DataResponseVerifyDNIArgs
import pe.mobile.cuy.validator.MasterValidation
import pe.mobile.cuy.viewmodel.OrderViewModel
import kotlinx.android.synthetic.main.pre_activation_fragment.*
import pe.mobile.cuy.FlashApplication
import pe.mobile.cuy.preferences.UserPrefs
import pe.mobile.cuy.util.*


/**
 * A simple [Fragment] subclass.
 */
class PreActivationFragment : Fragment() {

    private lateinit var validatorMatrix: MasterValidation
    private lateinit var orderViewModel: OrderViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(false)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.pre_activation_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        forceMinimize(requireActivity(), this)

        // Delete all preferences
        UserPrefs.clear(FlashApplication.appContext)

        this.orderViewModel = ViewModelProviders.of(this).get(OrderViewModel::class.java)

        val dniLenMessage = resources.getString(R.string.dni_length)
        this.validatorMatrix = MasterValidation()
            .valid(etDNI, true)
            .required()
            .minLength(8, dniLenMessage)
            .maxLength(8, dniLenMessage).active()

        btnValidateDocument.setOnClickListener {
            if (!this.validatorMatrix.checkValidity()) {
                this.view?.csSnackbar(
                    "Debe completar los campos requeridos",
                    Snackbar.LENGTH_LONG
                )
            } else {
                showSpinner(activity)
                orderViewModel.getAllByDni(etDNI.text.toString())
            }
        }

        chatExternalLink.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(resources.getString(R.string.chatExternalLink))
            )
            startActivity(browserIntent)
        }

        orderViewModel.loading.observe(this, Observer { loading ->
            loading?.let {
                if (loading) {
                    orderViewModel.loading.value = false

                    hideSpinner(activity)
                    this.view?.visibility = View.VISIBLE

                    UserPrefs.putUserDni(FlashApplication.appContext, etDNI.text.toString())

                    if (!orderViewModel.userHasOrders) {
                        val action =
                            PreActivationFragmentDirections.actionPreActivationFragmentToFormFragment()
                        findNavController().navigate(action)
                    } else {
                        val dni = etDNI.text.toString()
                        val action =
                            PreActivationFragmentDirections.actionPreActivationFragmentToOrdersFragment()
                        action.dni = dni
                        action.orders = DataResponseVerifyDNIArgs(
                            true,
                            orderInformationToArgs(orderViewModel.lstOrder.value!!, ArrayList())
                        )
                        findNavController().navigate(action)

                    }
                }
            }
        })
    }

}
