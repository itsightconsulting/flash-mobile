package pe.mobile.cuy.view


import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.zxing.Result
import pe.mobile.cuy.R
import pe.mobile.cuy.util.invokerBarcodeSuccess
import kotlinx.android.synthetic.main.sim_card_fragment.*
import me.dm7.barcodescanner.zxing.ZXingScannerView

/**
 * A simple [Fragment] subclass.
 */
class SimCardFragment : Fragment(), ZXingScannerView.ResultHandler {

    private lateinit var mScannerView: ZXingScannerView
    private var dialog: Dialog? = null


    override fun handleResult(rawResult: Result?) {
//        Toast.makeText(context!!, "${rawResult?.text}", Toast.LENGTH_LONG).show()
//        invokerQuitDialog(context!!).show()
        mScannerView.stopCamera()

        val diagSucc = invokerBarcodeSuccess(context!!)
        diagSucc.show()

        diagSucc.findViewById<Button>(R.id.btnBarcodeSuccess).setOnClickListener {
            diagSucc.dismiss()
            dialog?.dismiss()
            val action = SimCardFragmentDirections.actionSimCardFragmentToBiometricFragment()
            findNavController().navigate(action)
        }
    }

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
            mScannerView = ZXingScannerView(context)
            dialog = Dialog(context!!, R.style.dialog_scanner)

            dialog?.setContentView(mScannerView)
            dialog?.show()
            mScannerView.setResultHandler(this)
            mScannerView.startCamera()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


}
