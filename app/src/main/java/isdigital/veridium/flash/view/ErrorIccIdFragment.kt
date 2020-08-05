package isdigital.veridium.flash.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import isdigital.veridium.flash.R
import isdigital.veridium.flash.util.forceMinimize
import kotlinx.android.synthetic.main.error_iccid_fragment.*

/**
 * A simple [Fragment] subclass.
 * Use the [ErrorIccIdFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ErrorIccIdFragment : Fragment() {

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
            val action = ErrorIccIdFragmentDirections.actionErrorIccIdFragmentToPreActivationFragment()
            findNavController().navigate(action)
        }

    }
}