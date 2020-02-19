package isdigital.veridium.flash.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import isdigital.veridium.flash.R
import isdigital.veridium.flash.preferences.UserPrefs
import com.synnapps.carouselview.ImageListener
import kotlinx.android.synthetic.main.initial_fragment.*
import isdigital.veridium.flash.FlashApplication

/**
 * A simple [Fragment] subclass.
 */
class InitialFragment : Fragment() {

    private val sampleImages =
        intArrayOf(R.drawable.carrusel_1, R.drawable.carrusel_2, R.drawable.carrusel_3)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.initial_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnGetStarted.visibility = View.INVISIBLE

        var imageListener =
            ImageListener { position, imageView ->
                run {
                    imageView.setImageResource(sampleImages[position])
                }
            }

        carouselView.setImageListener(imageListener)

        carouselView.pageCount = sampleImages.size


        // Inflate the layout for this fragment
        btnGetStarted.setOnClickListener {
            carouselView.pauseCarousel()
            saveHideCarousel()
            val action = InitialFragmentDirections.actionInitialFragmentToPreActivationFragment()
            findNavController().navigate(action)
        }

        carouselView.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                printCarousel(position)
                if (position == 2) {
                    btnGetStarted.visibility = View.VISIBLE
                    carouselView.pauseCarousel()
                }
            }
        })

        tabLayout.setupWithViewPager(carouselView.containerViewPager, true)
    }

    private fun printCarousel(pos: Int) {

        var title = ""
        var description = ""
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
        txtTitleCarousel.text = title
        txtDescriptionCarousel.text = description
    }

    private fun saveHideCarousel() {
        UserPrefs.putHideCarousel(FlashApplication.appContext, true)
    }
}