package com.itsight.flash.view


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.itsight.flash.R

/**
 * A simple [Fragment] subclass.
 */
class PreActivationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.pre_activation_fragment, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }


}
