package hu.ppke.itk.mindtechpokemon.basic

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.view.postDelayed
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hu.ppke.itk.mindtechpokemon.Constants
import hu.ppke.itk.mindtechpokemon.R
import hu.ppke.itk.mindtechpokemon.basic.ListItem.Companion.VIEW_TYPE_DATA
import hu.ppke.itk.mindtechpokemon.basic.ListItem.Companion.VIEW_TYPE_EMPTY
import hu.ppke.itk.mindtechpokemon.basic.ListItem.Companion.VIEW_TYPE_ERROR
import hu.ppke.itk.mindtechpokemon.basic.ListItem.Companion.VIEW_TYPE_LOADING
import kotlinx.android.synthetic.main.item_empty.view.*
import kotlinx.android.synthetic.main.item_error.view.*
import kotlinx.android.synthetic.main.item_loading.view.*

open class CommonPagedAdapter<D : ModelBase>(
    @LayoutRes private val itemLayoutId: Int,
    private val bind: (View, D?) -> Unit,
    private val retry: () -> Unit,
    private val boundCallback: (position: Int, type: Int) -> Unit,
    private val config: AdapterConfig = AdapterConfig()
) : ListAdapter<ListItem<D>, CommonPagedAdapter<D>.CommonViewHolder2>(
    ListItem.Companion.DiffItemCallback<D>()
) {
    data class AdapterConfig(
        @StringRes val loadingMessage: Int = R.string.loading,
        @StringRes val emptyMessage: Int = R.string.no_items,
        @StringRes val errorMessage: Int = R.string.loading_error
    )

    private val TAG = "CommonPagedAdapter"

    override fun getItemViewType(position: Int): Int = getItem(position).viewType

    private fun ViewGroup.inflate(layoutRes: Int): View = LayoutInflater.from(context).inflate(layoutRes, this, false)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder2 = when (viewType) {
        VIEW_TYPE_DATA -> CommonViewHolder2(parent.inflate(itemLayoutId))
        VIEW_TYPE_LOADING -> CommonViewHolder2(parent.inflate(R.layout.item_loading))
        VIEW_TYPE_EMPTY -> CommonViewHolder2(parent.inflate(R.layout.item_empty))
        VIEW_TYPE_ERROR -> CommonViewHolder2(parent.inflate(R.layout.item_error))
        else -> throw IllegalArgumentException()
    }

    override fun onBindViewHolder(holder: CommonViewHolder2, position: Int) {
        holder.bind(getItem(position))
        // trigger loading new items
        boundCallback(position, getItemViewType(position))
    }

    protected fun View.delayedRetry(position: Int) {
        postDelayed(Constants.LOAD_RETRY_DELAY) {
            if (itemCount > position && getItemViewType(position) == VIEW_TYPE_LOADING && isAttachedToWindow) {
                Log.d(TAG, "Delayed loading retry at $position")
                retry()
                delayedRetry(position)
            }
        }
    }

    open inner class CommonViewHolder2(view: View) : RecyclerView.ViewHolder(view) {
        open fun bind(item: ListItem<D>) = when (item) {
            is ListItem.Data -> bind(itemView, item.data)
            is ListItem.Loading -> {
                Log.v(TAG , "loading new staffs")
                itemView.item_loading_message.setText(config.loadingMessage)
                itemView.item_loading_message.delayedRetry(item.position)
            }
            is ListItem.Empty -> {
                itemView.item_empty_message.setText(config.emptyMessage)

            }
            is ListItem.Error -> {
                itemView.item_error_message.setText(config.errorMessage)
                itemView.item_error_retry.setOnClickListener { retry() }

            }
        }
    }


}