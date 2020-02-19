package isdigital.veridium.flash.util

interface RecyclerViewOnItemClickListener<T> {

    fun onItemClicked(obj: T)

    fun onCallButtonClicked(obj: T)
}