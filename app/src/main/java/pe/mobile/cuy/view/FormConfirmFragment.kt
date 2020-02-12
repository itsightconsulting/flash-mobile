package pe.mobile.cuy.view


import android.content.res.Resources
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

import pe.mobile.cuy.R
import kotlinx.android.synthetic.main.form_confirm_fragment.*
import pe.mobile.cuy.FlashApplication
import pe.mobile.cuy.preferences.UserPrefs

/**
 * A simple [Fragment] subclass.
 */
class FormConfirmFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.form_confirm_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showInformation()

        btn_confirm.setOnClickListener {
            val action =
                FormConfirmFragmentDirections.actionFormConfirmFragmentToTermsFragment()
            findNavController().navigate(action)
        }

        btn_cancel.setOnClickListener({
            val action = FormConfirmFragmentDirections.actionFormConfirmFragmentToFormFragment()
            findNavController().navigate(action)
        })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    fun showInformation() {

        var oActivation = UserPrefs.getActivation(FlashApplication.appContext)
        if (oActivation == null) throw  Resources.NotFoundException()

        tvDniResumen.text = oActivation.dni
        tvNameResumen.text = oActivation.name
        tvLastNameResumen.text = oActivation.lastName
        tvDateOfBirthResumen.text = oActivation.birthDate
        tvEmailResumen.text = oActivation.email

        if (oActivation.sponsorTeamId != "") {
            tvSponsorTeamId.visibility = View.VISIBLE
            tvSponsorTeamIdResumen.visibility = View.VISIBLE
            tvSponsorTeamIdResumen.text = oActivation.sponsorTeamId
        }

        if (oActivation.wantToPortability) {
            tvPhoneNumber.visibility = View.VISIBLE
            tvPhoneNumberResumen.visibility = View.VISIBLE
            tvPhoneNumberResumen.text = oActivation.phoneNumber

            tvCurrentCompany.visibility = View.VISIBLE
            tvCurrentCompanyResumen.visibility = View.VISIBLE
            tvCurrentCompanyResumen.text = oActivation.currentCompany

            tvPlanType.visibility = View.VISIBLE
            tvPlanTypeResumen.visibility = View.VISIBLE
            tvPlanTypeResumen.text = oActivation.planType
        }
    }
}
