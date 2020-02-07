package com.itsight.flash.view

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.itsight.flash.FlashApplication
import com.itsight.flash.R
import com.itsight.flash.preferences.UserPrefs
import kotlinx.android.synthetic.main.navigation_activity.*
import java.util.*


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

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, UUID.randomUUID().toString())
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

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
//        graph.setDe .setDefaultArguments(intent.extras)


        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.initialFragment)
        )

        setupActionBar(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, dest, _ ->

            toolbarTitleParams.setMargins(-(aproxToolbarTitleLeftMargin), 0, 0, 0)
            toolbar_title.layoutParams = toolbarTitleParams

            if (dest.id == R.id.preActivationFragment) {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            } else if (dest.id == R.id.ordersFragment) {

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

//        var estado: Boolean = UserPrefs.getHideCarousel(FlashApplication.appContext)
//        if (estado) graph.startDestination = R.id.preActivationFragment
//        else
        graph.startDestination = R.id.initialFragment
        host.navController.graph = graph

    }

    private fun setupActionBar(
        navController: NavController,
        appBarConfig: AppBarConfiguration
    ) {
        setupActionBarWithNavController(navController, appBarConfig)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val v = currentFocus

        if (v != null &&
            (ev!!.action === MotionEvent.ACTION_UP || ev!!.action === MotionEvent.ACTION_MOVE) &&
            v is EditText &&
            !v.javaClass.name.startsWith("android.webkit.")
        ) {
            val scrcoords = IntArray(2)
            v.getLocationOnScreen(scrcoords)
            val x = ev!!.rawX + v.left - scrcoords[0]
            val y = ev!!.rawY + v.top - scrcoords[1]

            if (x < v.left || x > v.right || y < v.top || y > v.bottom)
                hideKeyboard(this)
        }
        return super.dispatchTouchEvent(ev)
    }

    fun hideKeyboard(activity: Activity?) {
        if (activity != null && activity.window != null && activity.window.decorView != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
        }
    }
}
