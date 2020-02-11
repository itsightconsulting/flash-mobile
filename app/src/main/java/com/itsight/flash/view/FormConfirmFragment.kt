package com.itsight.flash.view


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

import com.itsight.flash.R
import kotlinx.android.synthetic.main.form_confirm_fragment.*

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
        btn_confirm.setOnClickListener {
            val action = FormConfirmFragmentDirections.actionFormConfirmFragmentToTermsFragment()
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
}
