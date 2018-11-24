package timoptr.multiselectablelist.adapter

import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import timoptr.multiselectablelist.R
import timoptr.multiselectablelist.viewmodel.VehicleViewModel

/**
 * Created by timoptr on 03/12/2017.
 */
class VehicleAdapter(selectionMode: Selection) : SelectableAdapter<VehicleViewModel, SelectableAdapter.SelectableViewHolderBinding>(object :DiffUtil.ItemCallback<VehicleViewModel>() {
    override fun areItemsTheSame(oldItem: VehicleViewModel, newItem: VehicleViewModel): Boolean =
            oldItem == newItem


    override fun areContentsTheSame(oldItem: VehicleViewModel, newItem: VehicleViewModel): Boolean =
            oldItem.name == newItem.name && oldItem.type == newItem.type

}, selectionMode) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectableViewHolderBinding =
            SelectableViewHolderBinding(DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(parent.context), R.layout.item_vehicle, parent, false))

    class VehicleLayoutManager(context: Context) : LinearLayoutManager(context) {
        override fun canScrollVertically(): Boolean {
            return false
        }
    }
}