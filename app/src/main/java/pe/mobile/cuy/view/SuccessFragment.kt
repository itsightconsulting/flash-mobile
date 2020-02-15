package pe.mobile.cuy.view


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import pe.mobile.cuy.R
import kotlinx.android.synthetic.main.success_fragment.*

/**
 * A simple [Fragment] subclass.
 */
class SuccessFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.success_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnFinalize.setOnClickListener {
            val action = SuccessFragmentDirections.actionSuccessFragmentToPreActivationFragment()
            findNavController().navigate(action)
        }
    }

}
