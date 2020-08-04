package isdigital.veridium.flash.view

import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import isdigital.veridium.flash.R
import isdigital.veridium.flash.util.CustomTypefaceSpan
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
        settingDescriptionText()
        forceMinimize(requireActivity(), this)

        btnRedirectWebView.setOnClickListener {
            findNavController().navigate(PortabilitySuccessFragmentDirections.actionPortabilitySuccessFragmentToPortabilityPinFragment())
        }
    }

    private fun settingDescriptionText(){
        textView6.text = ""
        val descriptionText = resources.getString(R.string.sim_activated_fl_message_portability).split("|")
        val descriptionTextNb = descriptionText[0]
        val descriptionTextBold = descriptionText[1]
        val prefixText = SpannableString(descriptionTextBold)
        val prefixTextLen = prefixText.length

        prefixText.setSpan(
            CustomTypefaceSpan("", ResourcesCompat.getFont(context!!, R.font.gotham_bold)!!),
            0,
            prefixTextLen,
            0
        )

        prefixText.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    context!!,
                    R.color.dark_purple
                )
            ),0,prefixTextLen,0
        )
        textView6.append(descriptionTextNb)
        textView6.append(prefixText)
    }

}
