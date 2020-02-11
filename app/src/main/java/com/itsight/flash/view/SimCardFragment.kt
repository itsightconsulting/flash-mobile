package com.itsight.flash.view


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.zxing.integration.android.IntentIntegrator
import com.itsight.flash.R
import kotlinx.android.synthetic.main.sim_card_fragment.*

/**
 * A simple [Fragment] subclass.
 */
class SimCardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.sim_card_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageButton.setOnClickListener {
            IntentIntegrator(activity).initiateScan()
            /*val action = SimCardFragmentDirections.actionSimCardFragmentToSuccessFragment()
            findNavController().navigate(action)*/
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Log.d("SimCardFragment", "Cancelled scan")
                Toast.makeText(context!!, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                Log.d("SimCardFragment", "Scanned")
                Toast.makeText(context!!, "${result.contents}", Toast.LENGTH_LONG).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


}
