package com.itsight.flash.view


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.itsight.flash.R
import com.itsight.flash.util.csSnackbar
import com.itsight.flash.validator.MasterValidation
import kotlinx.android.synthetic.main.form_fragment.*
import java.util.*
import android.app.DatePickerDialog
import kotlinx.android.synthetic.main.alert_error.view.*

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

        this.validatorMatrix = MasterValidation()
            .valid(etName, true)
            .required()
            .and()
            .valid(etLastName, true)
            .required()
            .and()
            .valid(etDateOfBirth, true)
            .required()
            .and()
            .valid(etEmail, true)
            .required()
            .email()
            .active()


        // Calendar
        val cal = java.util.Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
        ivCalendar.setOnClickListener {
            BuildDatePickerDialog(year, month, day)
        }

        btn_continue.setOnClickListener {
            var estado: Boolean? = null;
            if (rbDoNotWantToPort.isChecked) estado = false
            if (rbWantToPort.isChecked) estado = true
            if (estado == null) {
                rbWantToPort.error = "Debes seleccionar una opción"
            }

            if (!this.validatorMatrix.checkValidity() || estado == null)
                this.view?.csSnackbar(
                    "Debe completar los campos requeridos",
                    Snackbar.LENGTH_LONG
                )
            else {
                var action = FormFragmentDirections.actionFormFragmentToFormConfirmFragment()
                if (estado) action = FormFragmentDirections.actionFormFragmentToFormPhoneFragment()
                findNavController().navigate(action)
            }

        }

/*this.view?.let {
    invokerQuitDialog(context!!).show()
}*/
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    fun BuildDatePickerDialog(year: Int, month: Int, day: Int) {
        val dpd = DatePickerDialog(
            context!!,
            DatePickerDialog.OnDateSetListener { view, nYear, nMonth, nDayOfMonth ->
                var nDayOfMonthStr = nDayOfMonth.toString()
                var nMonthStr = nMonth.toString()
                if (nDayOfMonth < 10)
                    nDayOfMonthStr = "0" + nDayOfMonth

                if (nMonth < 10)
                    nMonthStr = "0" + nMonth
                etDateOfBirth.setText(nDayOfMonthStr + "/" + nMonthStr + "/" + nYear)
            }, year, month, day
        )
        dpd.datePicker.maxDate = Date().time
        dpd.show()
    }
}
