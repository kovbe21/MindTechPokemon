package hu.ppke.itk.mindtechpokemon.basic

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

sealed class ListItem<out D>(val viewType: Int) {
    class Data<out D : ModelBase>(val data: D) : ListItem<D>(
        VIEW_TYPE_DATA
    )
    class Loading<out D>(val position: Int) : ListItem<D>(
        VIEW_TYPE_LOADING
    )
    class Empty<out D> : ListItem<D>(
        VIEW_TYPE_EMPTY
    )
    class Error<out D>(val retry: () -> Unit) : ListItem<D>(
        VIEW_TYPE_ERROR
    )

    companion object {
        class DiffItemCallback<D : ModelBase> : DiffUtil.ItemCallback<ListItem<D>>() {
            override fun areItemsTheSame(oldItem: ListItem<D>, newItem: ListItem<D>): Boolean = when (oldItem) {
                is Data -> newItem is Data && oldItem.data.id == newItem.data.id
                is Loading -> newItem is Loading && oldItem.position == newItem.position // to prevent scrolling by the diff callback
                is Empty -> newItem is Empty
                is Error -> newItem is Error
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: ListItem<D>, newItem: ListItem<D>): Boolean = when (oldItem) {
                is Data -> newItem is Data && oldItem.data == newItem.data
                is Loading -> newItem is Loading && oldItem.position == newItem.position
                is Empty -> newItem is Empty
                is Error -> newItem is Error && oldItem.retry === newItem.retry
            }
        }

        const val VIEW_TYPE_DATA = 0
        const val VIEW_TYPE_LOADING = 1
        const val VIEW_TYPE_EMPTY = 2
        const val VIEW_TYPE_ERROR = 3
    }
}