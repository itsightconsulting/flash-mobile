package isdigital.veridium.flash.view


import android.content.res.Resources
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

import isdigital.veridium.flash.R
import kotlinx.android.synthetic.main.form_confirm_fragment.*
import isdigital.veridium.flash.FlashApplication
import isdigital.veridium.flash.model.pojo.ActivationPOJO
import isdigital.veridium.flash.preferences.UserPrefs

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

            val oActivation = UserPrefs.getActivation(FlashApplication.appContext)
            if (oActivation == null) throw  Resources.NotFoundException()

            UserPrefs.putActivation(FlashApplication.appContext, oActivation)

            val action =
                FormConfirmFragmentDirections.actionFormConfirmFragmentToTermsFragment()
            findNavController().navigate(action)
        }

        btn_cancel.setOnClickListener {
            val action = FormConfirmFragmentDirections.actionFormConfirmFragmentToFormFragment()
            findNavController().navigate(action)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun showInformation() {

        tvName.text = tvName.text.toString() + resources.getString(R.string.label_colon)
        tvLastName.text = tvLastName.text.toString() + resources.getString(R.string.label_colon)
        tvDateOfBirth.text =
            tvDateOfBirth.text.toString() + resources.getString(R.string.label_colon)
        tvSponsorTeamId.text =
            tvSponsorTeamId.text.toString() + resources.getString(R.string.label_colon)
        tvPlanType.text = tvPlanType.text.toString() + resources.getString(R.string.label_colon)


        val oActivation: ActivationPOJO = UserPrefs.getActivation(FlashApplication.appContext)

        tvDniResumen.text = oActivation.dni
        tvNameResumen.text = oActivation.name
        tvLastNameResumen.text = oActivation.lastName
        tvDateOfBirthResumen.text = oActivation.birthDate
        tvEmailResumen.text = oActivation.email

        if (oActivation.sponsorTeamId != "") {
            lySponsorTeamId.visibility = View.VISIBLE
            tvSponsorTeamId.visibility = View.VISIBLE
            tvSponsorTeamIdResumen.visibility = View.VISIBLE
            tvSponsorTeamIdResumen.text = oActivation.sponsorTeamId
        }

        if (oActivation.wantPortability) {
            tvPhoneNumber.visibility = View.VISIBLE
            tvPhoneNumberResumen.visibility = View.VISIBLE
            tvPhoneNumberResumen.text = formatoPhoneNumber(oActivation.phoneNumber ?: "")

            tvCurrentCompany.visibility = View.VISIBLE
            tvCurrentCompanyResumen.visibility = View.VISIBLE
            tvCurrentCompanyResumen.text = oActivation.currentCompany

            tvPlanType.visibility = View.VISIBLE
            tvPlanTypeResumen.visibility = View.VISIBLE
            tvPlanTypeResumen.text = oActivation.planType
        }
    }

    private fun formatoPhoneNumber(phoneNumber: String): String {
        var _phoneNumber = phoneNumber
        /*var index: Int = 0
        while (phoneNumber[index] != null) {
            _phoneNumber = phoneNumber.substring(index, 2)
            index++
        }

         */
        if (phoneNumber.length == 9) {
            _phoneNumber = phoneNumber.substring(0, 3) +
                    " " + phoneNumber.substring(3, 6) +
                    " " + phoneNumber.substring(6, 9)
        }
        return _phoneNumber
    }
}
