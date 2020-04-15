package isdigital.veridium.flash.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import isdigital.veridium.flash.R
import isdigital.veridium.flash.util.forceMinimize
import kotlinx.android.synthetic.main.portability_success_fragment.*

/**
 * A simple [Fragment] subclass.
 */
class PortabilitySuccessFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.portability_success_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        forceMinimize(requireActivity(), this)

        btnRedirectWebView.setOnClickListener {
            findNavController().navigate(PortabilitySuccessFragmentDirections.actionPortabilitySuccessFragmentToPortabilityPinFragment())
        }
    }

}
