package com.itsight.flash.view

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.itsight.flash.FlashApplication
import com.itsight.flash.R
import com.itsight.flash.preferences.UserPrefs
import com.itsight.flash.viewmodel.ListViewModel
import kotlinx.android.synthetic.main.initial_fragment.*
import com.synnapps.carouselview.ImageListener

/**
 * A simple [Fragment] subclass.
 */
class InitialFragment : Fragment() {

    private lateinit var viewModel: ListViewModel
    private val sampleImages =
        intArrayOf(R.drawable.carrusel_1, R.drawable.carrusel_2, R.drawable.carrusel_3)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.initial_fragment, container, false)
    }

    var imageListener =
        ImageListener { position, imageView ->
            run {
                imageView.setImageResource(sampleImages[position])
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //        var estado: Boolean = UserPrefs.getHideCarousel(FlashApplication.appContext)
//        if (estado)

//        viewModel = ViewModelProviders.of(this).get(ListViewModel::class.java)
//        viewModel.refresh()

        refreshLayout.setOnRefreshListener {
            loadingView.visibility = View.VISIBLE
            txtTitleCarousel.visibility = View.VISIBLE
            txtDescriptionCarousel.visibility = View.VISIBLE
            refreshLayout.isRefreshing = false
        }

//        observeViewModel()
        carouselView.pageCount = sampleImages.size

        carouselView.setImageListener(imageListener)

        // Inflate the layout for this fragment
        btnGetStarted.setOnClickListener {
            carouselView.pauseCarousel()
            saveHideCarousel()
            val action = InitialFragmentDirections.actionInitialFragmentToPreActivationFragment()
            findNavController().navigate(action)
        }

        carouselView.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
//                Toast.makeText(context!!, "x", Toast.LENGTH_SHORT).show()
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
//                Toast.makeText(context!!, "x1", Toast.LENGTH_SHORT).show()
            }

            override fun onPageSelected(position: Int) {
                printCarousel(position);
                if (position == 2) {
                    btnGetStarted.visibility = View.VISIBLE
                    carouselView.pauseCarousel()
                }
//                Toast.makeText(context!!, "$position", Toast.LENGTH_SHORT).show()
            }
        })

        tabLayout.setupWithViewPager(carouselView.containerViewPager, true)
    }

    private fun printCarousel(pos: Int) {

        var title: String = "";
        var description: String = "";
        when (pos) {
            0 -> {
                title = resources.getString(R.string.title_carrusel_1)
                description = resources.getString(R.string.description_carrusel_1)
            }
            1 -> {
                title = resources.getString(R.string.title_carrusel_2)
                description = resources.getString(R.string.description_carrusel_2)
            }
            2 -> {
                title = resources.getString(R.string.title_carrusel_3)
                description = resources.getString(R.string.description_carrusel_3)
            }
        }
        txtTitleCarousel.text = title;
        txtDescriptionCarousel.text = description;
    }

    private fun saveHideCarousel() {
        UserPrefs.putHideCarousel(FlashApplication.appContext, true)
    }
}
