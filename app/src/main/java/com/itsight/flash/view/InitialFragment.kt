package com.itsight.flash.view


import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.itsight.flash.R
import com.itsight.flash.viewmodel.ListViewModel
import kotlinx.android.synthetic.main.initial_fragment.*
import com.synnapps.carouselview.ImageListener

/**
 * A simple [Fragment] subclass.
 */
class InitialFragment : Fragment() {

    private lateinit var viewModel: ListViewModel
    private val sampleImages = intArrayOf(R.drawable.carrusel_1, R.drawable.carrusel_2, R.drawable.carrusel_3)

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

        viewModel = ViewModelProviders.of(this).get(ListViewModel::class.java)
        viewModel.refresh()

        refreshLayout.setOnRefreshListener {
            dogsList.visibility = View.GONE
            listError.visibility = View.GONE
            loadingView.visibility = View.VISIBLE
            viewModel.refreshBypassCache()
            refreshLayout.isRefreshing = false
        }

        observeViewModel()
        carouselView.pageCount = sampleImages.size

        carouselView.setImageListener(imageListener)

        txtDescriptionCarousel.setOnClickListener {
            carouselView.stopCarousel()
            carouselView.isDisableAutoPlayOnUserInteraction
            Toast.makeText(context!!, "x", Toast.LENGTH_SHORT).show()
        }

        // Inflate the layout for this fragment
        button.setOnClickListener {
            carouselView.pauseCarousel()
            val action = InitialFragmentDirections.actionInitialFragmentToPreActivationFragment()
            findNavController().navigate(action)
        }

        carouselView.addOnPageChangeListener( object : ViewPager.OnPageChangeListener{
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
                if (position== 3)
                    carouselView.stopCarousel()

                Toast.makeText(context!!, "$position", Toast.LENGTH_SHORT).show()
            }
        })

        tabLayout.setupWithViewPager(carouselView.containerViewPager, true)
    }

    fun observeViewModel() {

        viewModel.carouselLoadError.observe(this, Observer {isError ->
            isError?.let {
                listError.visibility = if(it) View.VISIBLE else View.GONE
            }
        })
        viewModel.loading.observe(this, Observer { isLoading ->
            isLoading?.let {
                loadingView.visibility = if(it) View.VISIBLE else View.GONE
                if(it) {
                    listError.visibility = View.GONE
                    dogsList.visibility = View.GONE
                }
            }
        })
    }
    /*override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }*/
}
