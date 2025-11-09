package com.br.listadecompras.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.br.listadecompras.data.model.ListItem
import com.br.listadecompras.databinding.ListItemBinding

class ListItemAdapter(
    items: List<ListItem>,
    private val onCheckedChange: (ListItem, Boolean) -> Unit,
    private val onItemClick: (ListItem) -> Unit
) : RecyclerView.Adapter<ListItemAdapter.ListViewHolder>() {
    var items: List<ListItem> = items
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ListViewHolder(val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val item = items[position]

        holder.binding.textView.text = "${item.name} - ${item.qtde} ${item.unit.label}"

        val backgroundColor = item.category.colorHex.toColorInt()
        holder.binding.root.setBackgroundColor(backgroundColor)

        holder.binding.checkBox.isChecked = item.checked
        holder.binding.checkBox.setOnCheckedChangeListener(null)
        holder.binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            onCheckedChange(item, isChecked)
        }

        holder.binding.root.setOnClickListener {
            onItemClick(item)
        }
    }


    override fun getItemCount() = items.size
}
