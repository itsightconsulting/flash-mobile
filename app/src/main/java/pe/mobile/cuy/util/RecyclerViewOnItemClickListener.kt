package pe.mobile.cuy.util

interface RecyclerViewOnItemClickListener<T> {

    fun onItemClicked(obj: T)

    fun onCallButtonClicked(obj: T)
}