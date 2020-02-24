package hu.ppke.itk.mindtechpokemon.basic

import androidx.recyclerview.widget.DiffUtil

interface ModelBase {
    val id: Any

    // force the use of data classes (or custom equals and hasCode implementations)
    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

    companion object {
        fun <T : ModelBase> comparator() = object : DiffUtil.ItemCallback<T>() {
            override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem
            override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem.id == newItem.id
        }
    }
}