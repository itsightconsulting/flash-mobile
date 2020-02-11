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
import java.util.*
import android.app.DatePickerDialog
import android.widget.DatePicker
import com.itsight.flash.util.invokerQuitDialog
import java.text.SimpleDateFormat

/**
 * A simple [Fragment] subclass.
 */
class FormFragment : Fragment() {
    private var cal = java.util.Calendar.getInstance()
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


        // create an OnDateSetListener
        /*val dateSetListener = object : DatePickerDialog.OnDateSetListener {

            override fun onDateSet(
                view: DatePicker, year: Int, monthOfYear: Int,
                dayOfMonth: Int
            ) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }

         */

        // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
        txtDateOfBirth.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                /* DatePickerDialog(
                    this@FormFragment,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
                */
            }
        })




        btn_continue.setOnClickListener {
            if (!this.validatorMatrix.checkValidity())
                this.view?.csSnackbar(
                    "Debe completar los campos requeridos",
                    Snackbar.LENGTH_LONG
                )
            else {
                val action = FormFragmentDirections.actionFormFragmentToFormPhoneFragment()
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

    private fun updateDateInView() {
        val myFormat = "MM/dd/yy" // mention the format you need
        /*val sdf = java.text.SimpleDateFormat(myFormat, java.util.Locale.US)
        etDateOfBirth!!.text = sdf?.format(cal.getTime())
         */
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

}
