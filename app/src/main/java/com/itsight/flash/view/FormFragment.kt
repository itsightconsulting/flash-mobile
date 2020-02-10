package com.itsight.flash.view


import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.itsight.flash.R
import com.itsight.flash.util.csSnackbar
import com.itsight.flash.validator.MasterValidation
import kotlinx.android.synthetic.main.form_fragment.*

/**
 * A simple [Fragment] subclass.
 */
class FormFragment : Fragment() {
    private lateinit var validatorMatrix: MasterValidation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.form_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
/*
        this.validatorMatrix = MasterValidation()
            .valid(etName, true)
            .required()
            .and()
            .valid(etLastName)
            .required()
            .and()
            .valid(etDateOfBirth)
            .required()
            .and()
            .valid(etEmail)
            .required()
            .email()
            .active()
*/

        btn_continue.setOnClickListener {
            /* if (!this.validatorMatrix.checkValidity())
                this.view?.csSnackbar(
                    "Debe completar los campos requeridos",
                    Snackbar.LENGTH_LONG
                )
            else {
               */
            val action = FormFragmentDirections.actionFormFragmentToFormPhoneFragment()
            findNavController().navigate(action)
            //}

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}
