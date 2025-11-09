package com.br.listadecompras.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.br.listadecompras.Const
import com.br.listadecompras.R
import com.br.listadecompras.data.model.ListItemAggregator
import com.br.listadecompras.databinding.ListAggregatorItemBinding
import com.bumptech.glide.Glide

class ListAggregatorItemAdapter(items: List<ListItemAggregator>) :
    RecyclerView.Adapter<ListAggregatorItemAdapter.ListViewHolder>() {
    var items: List<ListItemAggregator> = items
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ListViewHolder(val binding: ListAggregatorItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ListAggregatorItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val item = items[position]
        Glide.with(holder.itemView.context)
            .load(item.imageUri ?: "")
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(holder.binding.itemImage)

        holder.binding.itemText.text = item.name

        holder.binding.root.setOnClickListener {
            holder.binding.root.findNavController().navigate(
                R.id.action_homeFragment_to_listAggregatorFragment,
                Bundle().apply { putString(Const.AGGREGATOR_ID_BUNDLE, item.id ?: "") })
        }
    }

    override fun getItemCount() = items.size


}
