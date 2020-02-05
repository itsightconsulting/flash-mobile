package com.itsight.flash.view


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.itsight.flash.R
import com.itsight.flash.viewmodel.TempViewModel
import kotlinx.android.synthetic.main.pre_activation_fragment.*


/**
 * A simple [Fragment] subclass.
 */
class PreActivationFragment : Fragment() {

    private lateinit var tempView: TempViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(false)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.pre_activation_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnValidateDocument.setOnClickListener {
            Glide.with(this).load(R.drawable.cargador).into(
                activity!!.findViewById(R.id.mainSpinner)
            )
            activity!!.findViewById<LinearLayout>(R.id.transitionerLayout).visibility = View.VISIBLE
            mainTemp.visibility = View.GONE
            tempView.test()
        }

        tempView = ViewModelProviders.of(this).get(TempViewModel::class.java)

        tempView.loading.observe(this, Observer {
            loading->
                loading?.let {
                    if(loading){
                        tempView.loading.value = false
                        activity!!.findViewById<LinearLayout>(R.id.transitionerLayout).visibility = View.GONE
                        mainTemp.visibility = View.VISIBLE
                        val action = PreActivationFragmentDirections.actionPreActivationFragmentToOrdersFragment()
                        findNavController().navigate(action)
                    }
                }
        })
    }

    /*override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }*/


}
