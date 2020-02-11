package com.itsight.flash.view


import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar

import com.itsight.flash.R
import com.itsight.flash.util.csSnackbar
import com.itsight.flash.validator.MasterValidation
import kotlinx.android.synthetic.main.form_phone_fragment.*

/**
 * A simple [Fragment] subclass.
 */
class FormPhoneFragment : Fragment() {

    private lateinit var validatorMatrix: MasterValidation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.form_phone_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.validatorMatrix =
            MasterValidation().valid(etPhoneNumber, true, ::checkPortabilityArtificial).required()
                .and()
                .valid(etConfirmPhoneNumber, true, ::checkPortabilityArtificial)
                .equalsTo(etPhoneNumber).active()

        val adapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_menu_popup_item,
            arrayOf(1, 2, 3)
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        acPlanType.setAdapter(adapter)

        btn_continue.setOnClickListener {
            if (this.validatorMatrix.checkValidity()) {
                val action =
                    FormPhoneFragmentDirections.actionFormPhoneFragmentToFormConfirmFragment()
                findNavController().navigate(action)
            } else {
                this.view?.csSnackbar(
                    "Debe completar los campos requeridos",
                    Snackbar.LENGTH_LONG
                )
            }
        }
    }

    fun checkPortabilityArtificial(): Boolean {
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}
