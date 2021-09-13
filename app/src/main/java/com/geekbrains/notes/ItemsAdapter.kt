package com.geekbrains.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemsAdapter(val model: MainViewModel) : RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.notes_list_item_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = model.getItem(position)!!.name
        holder.textView.setOnClickListener {
            model.editOldItem(position)
        }
    }

    override fun getItemCount(): Int = model.getItemsCount()

    fun handleRemove(index: Int) {
        notifyItemRemoved(index)
        notifyItemRangeChanged(index, model.getItemsCount())
    }

    fun handleInsert(index: Int) {
        notifyItemInserted(index)
        notifyItemRangeChanged(index + 1, model.getItemsCount())
    }
}