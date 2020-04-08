package isdigital.veridium.flash.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.analytics.FirebaseAnalytics
import isdigital.veridium.flash.FlashApplication
import isdigital.veridium.flash.R
import isdigital.veridium.flash.preferences.UserPrefs
import isdigital.veridium.flash.util.invokerQuitDialog
import kotlinx.android.synthetic.main.navigation_activity.*


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val toolbarTitleParams: LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    private val aproxToolbarTitleLeftMargin = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)

        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        setContentView(R.layout.navigation_activity)

        //Load nav host fragment
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment? ?: return

        // Set up Action Bar
        // Prepare redirect
        val navController = host.navController
        val inflater = navController.navInflater
        val graph = inflater.inflate(R.navigation.mobile_navigation)

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.initialFragment)
        )

        setupActionBar(navController, appBarConfiguration)


        navController.addOnDestinationChangedListener { _, dest, _ ->

            toolbarTitleParams.setMargins(-(aproxToolbarTitleLeftMargin), 0, 0, 0)
            toolbar_title.layoutParams = toolbarTitleParams

            if (dest.id == R.id.preActivationFragment) {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            } else if (dest.id == R.id.ordersFragment ||
                dest.id == R.id.formFragment ||
                dest.id == R.id.formPhoneFragment ||
                dest.id == R.id.formConfirmFragment ||
                dest.id == R.id.simCardFragment ||
                dest.id == R.id.termsFragment ||
                dest.id == R.id.biometricFragment ||
                dest.id == R.id.formIccidNumberFragment
            ) {

                toolbar.navigationIcon?.let {
                    toolbarTitleParams.setMargins(
                        -(it.intrinsicWidth + aproxToolbarTitleLeftMargin),
                        0,
                        0,
                        0
                    )
                    toolbar_title.layoutParams = toolbarTitleParams
                }

                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            } else {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }
            toolbar_title.text = toolbar.title
        }

        val estado: Boolean = UserPrefs.getHideCarousel(FlashApplication.appContext)
        if (estado)
            graph.startDestination = R.id.preActivationFragment
        else
            graph.startDestination = R.id.initialFragment
        host.navController.graph = graph
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val navHostFragment = this.supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val childFragments = navHostFragment?.childFragmentManager?.fragments
        childFragments?.forEach {
            it.onActivityResult(requestCode, resultCode, data)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun setupActionBar(
        navController: NavController,
        appBarConfig: AppBarConfiguration
    ) {
        setupActionBarWithNavController(navController, appBarConfig)
    }

    override fun onSupportNavigateUp(): Boolean {
        if (transitionerLayout.isVisible) {
            return false
        }
        return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.preActivationFragment) {
            if (transitionerLayout.isVisible) {
                return true
            }

            val quitDialog = invokerQuitDialog(this)
            quitDialog.show()

            val buttonDialog = quitDialog.findViewById(R.id.btnBack) as Button
            buttonDialog.setOnClickListener {
                quitDialog.dismiss()
                val options = navOptions {
                    anim {
                        enter = R.anim.slide_in_left
                        exit = R.anim.slide_out_right
                        popEnter = R.anim.slide_in_right
                        popExit = R.anim.slide_out_left
                    }
                }
                // Delete all preferences
                // UserPrefs.clear(FlashApplication.appContext)

                findNavController(R.id.nav_host_fragment).navigate(
                    R.id.preActivationFragment,
                    null,
                    options
                )
            }
            val buttonCancel = quitDialog.findViewById(R.id.btnCancel) as Button
            buttonCancel.setOnClickListener {
                quitDialog.dismiss()
            }

            true
        } else {
            (item.onNavDestinationSelected(findNavController(R.id.nav_host_fragment))
                    || super.onOptionsItemSelected(item))
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val v = currentFocus

        if (v != null &&
            (ev!!.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) &&
            v is EditText &&
            !v.javaClass.name.startsWith("android.webkit.")
        ) {
            val scrcoords = IntArray(2)
            v.getLocationOnScreen(scrcoords)
            val x = ev.rawX + v.left - scrcoords[0]
            val y = ev.rawY + v.top - scrcoords[1]

            if (x < v.left || x > v.right || y < v.top || y > v.bottom)
                hideKeyboard(this)
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun hideKeyboard(activity: Activity?) {
        if (activity != null && activity.window != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
        }
    }

}
