package timoptr.multiselectablelist.adapter

import android.databinding.ViewDataBinding
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.recyclerview.extensions.DiffCallback
import android.support.v7.widget.RecyclerView
import android.view.View
import timoptr.multiselectablelist.util.OnItemSelectedListener
import timoptr.multiselectablelist.util.SelectableItem
import timoptr.multiselectablelist.BR

/**
 * Created by timothynibeaudeau on 01/12/2017.
 * From https://medium.com/@maydin/multi-and-single-selection-in-recyclerview-d29587a7dee2
 */

/**
 * This Adapter is use to help us to create a list of selectable item it use :
 * - databinding you will have a boolean in your item, so you will have to specify the selected comportment in your XML
 * - [ListAdapter] to be able to use Livedata as data source and to be able to properly add data at different times
 *
 * To use this adapter you have to override/implement :
 * - override [onBindViewHolder] and call super.onBindViewHolder() after you set the item of the viewHolder
 *
 *
 * What you can do :
 * - customize the ViewHolder by extending SelectableAdapter.ViewHolder
 * - use itemSelectedListener to listen on itemSelection
 * - selectionMode (MULTIPLE or SINGLE)
 * - get the list of the selectedItems
 *
 *
 * @param diffCallback use to specify how to detect when an item need to be update, already exist or need to be add default oldItem == newItem
 * @param selectionMode by default [Selection.MULTIPLE]
 *
 *
 * @sample val adapter = object : SelectableAdapter<T, SelectableAdapter.SelectableViewHolderBinding>(object : DiffCallback<T>() {
override fun areItemsTheSame(oldItem: T, newItem: T): Boolean =
oldItem == newItem

override fun areContentsTheSame(oldItem: T, newItem: T): Boolean =
oldItem == newItem
}, selectionMode) {
override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectableViewHolderBinding = SelectableViewHolderBinding(DataBindingUtil.bind(LayoutInflater.from(parent.context).inflate(itemLayout, parent, false))!!)
}
 *
 */
abstract class SelectableAdapter<V : SelectableItem, VH : SelectableAdapter.SelectableViewHolder>(diffCallback: DiffCallback<V> = object : DiffCallback<V>() {
    override fun areItemsTheSame(oldItem: V, newItem: V): Boolean =
            oldItem == newItem

    override fun areContentsTheSame(oldItem: V, newItem: V): Boolean =
            oldItem == newItem
}, val selectionMode: Selection = Selection.MULTIPLE) : ListAdapter<V, VH>(diffCallback) {

    /**
     * Listener to listen on item selection, it's null by default replace by yours
     * if you want to be notify of selection, if you only want the selected items you can use [getSelectedItems]
     */
    var itemSelectedListener: OnItemSelectedListener? = null

    /**
     * Enum to expose the different mode available of Selection
     */
    enum class Selection {
        MULTIPLE,
        SINGLE
    }

    private val internalItemSelectionListener = object : OnItemSelectedListener {
        override fun onItemSelected(item: SelectableItem) {
            //if single browse devices and set all items.isSelected = false
            if (selectionMode == Selection.SINGLE) {
                (0 until itemCount)
                        .map { getItem(it) }
                        .filter { item != it }
                        .forEach { it.isSelected.set(false) }
            }
            itemSelectedListener?.onItemSelected(item)//Send outside of the adapter if need
        }
    }

    /**
     * Give a list of selected items
     *
     * @return list of selected item, empty if no items selected
     */
    fun getSelectedItems(): List<V> = (0 until itemCount)
            .map { getItem(it) }.filter { it.isSelected.get() }


    /**
     * Use to set internal listener
     *
     * WARNING : call this method in your implementation or the selection mechanism won't work
     */
    override fun onBindViewHolder(holder: VH, position: Int) {
        if (position > -1) {
            val item = getItem(position)
            holder.bind(item, internalItemSelectionListener)
        }
    }

    /**
     * Custom ViewHolder to be use in [SelectableAdapter]
     *
     */
    open class SelectableViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        lateinit var item: SelectableItem
        private lateinit var selectableListener: OnItemSelectedListener

        init {
            view.setOnClickListener {
                item.isSelected.set(!item.isSelected.get())
                selectableListener.onItemSelected(item)
            }
        }

        /**
         * Need to be call inside [onBindViewHolder] to enable selection
         */
        internal open fun bind(item: SelectableItem, internalItemSelectionListener: OnItemSelectedListener) {
            this.item = item
            selectableListener = internalItemSelectionListener
        }
    }


    /**
     * Custom ViewHolder to be use in [SelectableAdapter] with Binding
     *
     */
    open class SelectableViewHolderBinding(val binding: ViewDataBinding) : SelectableViewHolder(binding.root) {

        override fun bind(item: SelectableItem, internalItemSelectionListener: OnItemSelectedListener) {
            super.bind(item, internalItemSelectionListener)
            binding.setVariable(BR.viewModel, item)
        }
    }

}
