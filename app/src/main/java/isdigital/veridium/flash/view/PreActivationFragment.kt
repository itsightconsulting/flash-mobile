package isdigital.veridium.flash.view


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import isdigital.veridium.flash.FlashApplication
import isdigital.veridium.flash.R
import isdigital.veridium.flash.model.args.DataResponseVerifyDNIArgs
import isdigital.veridium.flash.preferences.UserPrefs
import isdigital.veridium.flash.util.*
import isdigital.veridium.flash.validator.MasterValidation
import isdigital.veridium.flash.viewmodel.ActivationViewModel
import isdigital.veridium.flash.viewmodel.OrderViewModel
import kotlinx.android.synthetic.main.pre_activation_fragment.*


/**
 * A simple [Fragment] subclass.
 */
class PreActivationFragment : Fragment() {

    private lateinit var validatorMatrix: MasterValidation
    private lateinit var orderViewModel: OrderViewModel
    private lateinit var activationViewModel: ActivationViewModel

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
        //etDNI.setText("45677654")

        this.orderViewModel = ViewModelProviders.of(this).get(OrderViewModel::class.java)
        this.activationViewModel = ViewModelProviders.of(this).get(ActivationViewModel::class.java)
//        this.activationViewModel.auth()

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
                this.activationViewModel.auth()
                showSpinner(activity)
            }
        }

        chatExternalLink.setOnClickListener {
                        val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(resources.getString(R.string.chatExternalLink))
            )
            startActivity(browserIntent)
        }
        eventListenersAuth()
        orderViewModel.loadError.observe(this, Observer { loadError ->
            loadError?.let {
                if (loadError) {
                    orderViewModel.loadError.value = false

                    if (orderViewModel.refreshToken.value!!) {
                        if (orderViewModel.cantRefreshToken == 1) {
                            this.activationViewModel.auth()
                            orderViewModel.cantRefreshToken += 1
                            orderViewModel.errorMessage = TOKEN_ERROR_MESSAGE
                        } else
                            orderViewModel.errorMessage = GENERIC_ERROR_MESSAGE
                    }

                    hideSpinner(activity)
                    this.view?.visibility = View.VISIBLE
                    this.view?.csSnackbar(
                        message = orderViewModel.errorMessage,
                        duration = Snackbar.LENGTH_LONG
                    )
                }
            }
        })
        orderViewModel.loading.observe(this, Observer { loading ->
            loading?.let {
                if (loading) {

                    orderViewModel.loading.value = false

                    hideSpinner(activity)
                    this.view?.visibility = View.VISIBLE

                    UserPrefs.putUserDni(FlashApplication.appContext, etDNI.text.toString())


                    if (!orderViewModel.userHasOrders) {
                        orderViewModel.cantRefreshToken = 0
                        orderViewModel.loading.value = false

                        val action =
                            PreActivationFragmentDirections.actionPreActivationFragmentToFormFragment()
                        findNavController().navigate(action)
                    } else {
                        orderViewModel.cantRefreshToken = 0
                        orderViewModel.loading.value = false

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

    fun eventListenersAuth() {
        activationViewModel.loading.observe(this, Observer { loading ->
            loading?.let {
                if (it) {
                    activationViewModel.loading.value = false
                    if (activationViewModel.loadError.value!!) {

                        if (!verifyAvailableNetwork(activity!!))
                            activationViewModel.errorMessage = "Sin conexión"

                        this.view?.csSnackbar(
                            message = activationViewModel.errorMessage,
                            duration = Snackbar.LENGTH_LONG
                        )
                    } else {
                        UserPrefs.setApiToken(
                            FlashApplication.appContext,
                            activationViewModel.api_token
                        )
                        orderViewModel.getAllByDni(etDNI.text.toString())
                    }
                }
            }

        })
    }
}
