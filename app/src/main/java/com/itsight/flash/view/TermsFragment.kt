package com.itsight.flash.view


import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.method.ScrollingMovementMethod
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.itsight.flash.R
import com.itsight.flash.util.CustomTypefaceSpan
import com.itsight.flash.util.csSnackbar
import kotlinx.android.synthetic.main.terms_fragment.*

/**
 * A simple [Fragment] subclass.
 */
class TermsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.terms_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        txtTerms.movementMethod = ScrollingMovementMethod()
        txtTerms.visibility = View.INVISIBLE
        frameLayout.post {
            val elementsHeight = frameLayout.height
            txtTerms.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                this.topMargin = elementsHeight + 32
                this.bottomMargin = elementsHeight + (96 * 3.2).toInt()
            }
            txtTerms.visibility = View.VISIBLE

        }

        settingFullTermAndCoditionsText()
        settingTermAcceptText()

        btnTerms.setOnClickListener {
            if (chkTermsLabel.isChecked) {

            } else {
                this.view?.csSnackbar(
                    "Debe aceptar los términos y condiciones",
                    Snackbar.LENGTH_LONG
                )
            }
        }
    }

    private fun settingFullTermAndCoditionsText() {
        txtTerms.text = ""
        val subT1 = SpannableString("I. Flash Mobile Peru ")
        subT1.setSpan(StyleSpan(Typeface.BOLD), 0, subT1.length, 0)
        txtTerms.append(subT1)
        txtTerms.append("Ctrero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non provident, similique sunt in culpa qui ofﬁcia deserunt mollitia animi, id est laborum et dolorum fuga.Ctrero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non provident, similique sunt in culpa qui ofﬁcia deserunt mollitia animi, id est laborum et dolorum fuga.\n\n")
        val subT2 = SpannableString("II. Et harum quidem rerum ")
        subT2.setSpan(StyleSpan(Typeface.BOLD), 0, subT2.length, 0)
        txtTerms.append(subT2)
        txtTerms.append("facilis est et expedita distinctio. Nam libero tempore, cum soluta nobis est elerigendi optio cumque nihil impedit quo minus id quod maxime placeat facere possimus, omnis voluptas assumenda est, omnis dolor repellendus. Temporibus autem quibusdam et aut ofﬁciis debitis aut rerum necessitatibus saepe eveniet ut et voluptates repudiandae sint et molestiae. Temporibus autem quibusdam et aut ofﬁciis debitis aut rerum necessitatibus saepe eveniet ut et voluptates repudiandae sint et quas molestias excepturi sint occaecati cupiditate nonsimilique ")
    }

    private fun settingTermAcceptText() {
        chkTermsLabel.text = ""

        val prefixText = SpannableString("I read and accept ")
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
            ), 0, prefixTextLen, 0
        )
        chkTermsLabel.append(prefixText)
        chkTermsLabel.append("the Terms and conditions of the service and validate my information is real.")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


}
