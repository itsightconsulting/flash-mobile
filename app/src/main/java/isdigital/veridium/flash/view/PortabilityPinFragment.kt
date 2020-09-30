package isdigital.veridium.flash.view

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.util.Log
import android.view.*
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import isdigital.veridium.flash.R
import isdigital.veridium.flash.preferences.UserPrefs
import isdigital.veridium.flash.util.PORTABILITY_WEB_VIEW

/**
 * A simple [Fragment] subclass.
 */
class PortabilityPinFragment : Fragment() {

    lateinit var mWebView: WebView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment

        val v: View = inflater.inflate(R.layout.portability_pin_fragment, container, false)
        mWebView = v.findViewById<View>(R.id.webView) as WebView
        UserPrefs.getIccid(context)?.let {
            //Log.d("PORTABILITY_WEB_VIEW", PORTABILITY_WEB_VIEW + it.substring(IntRange(0, 18)))
            if (it.length > 1) {
                mWebView.loadUrl(PORTABILITY_WEB_VIEW + it.substring(IntRange(0, 18)))
            }
        }

        // Enable Javascript
        val webSettings: WebSettings = mWebView.settings
        webSettings.javaScriptEnabled = true
        mWebView.webViewClient = WebViewClient()
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        btnBackToInit.setOnClickListener {
//            val action = PortabilityPinFragmentDirections.actionPortabilityPinFragmentToPreActivationFragment()
//            findNavController().navigate(action)
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

}
