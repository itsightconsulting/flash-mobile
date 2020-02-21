package isdigital.veridium.flash.view


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.error_fragment.*

import isdigital.veridium.flash.R
import isdigital.veridium.flash.util.forceMinimize

/**
 * A simple [Fragment] subclass.
 */
class ErrorFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.error_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        forceMinimize(requireActivity(), this)

        btnBackHome.setOnClickListener {
            val action = ErrorFragmentDirections.actionErrorFragmentToPreActivationFragment()
            findNavController().navigate(action)
        }

    }


}
