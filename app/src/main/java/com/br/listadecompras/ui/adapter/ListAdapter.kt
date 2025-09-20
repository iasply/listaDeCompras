package com.br.listadecompras.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.br.listadecompras.data.model.ListItemAggregator
import com.br.listadecompras.databinding.ListItemBinding

class ListAdapter(private val items: List<ListItemAggregator>) :
    RecyclerView.Adapter<ListAdapter.ListViewHolder>() {

    class ListViewHolder(val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val item = items[position]
        holder.binding.itemImage.setImageResource(item.imageResId)
        holder.binding.itemText.text = item.name
    }

    override fun getItemCount() = items.size
}
