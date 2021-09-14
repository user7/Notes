package com.geekbrains.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemsAdapter(val model: MainViewModel) : RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textView: TextView = view.findViewById(R.id.notes_list_item_text)
        fun getTextView() = textView

        init {
            textView.setOnClickListener { model.editOldItem(adapterPosition) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.getTextView().text = model.getItem(position)!!.name
    }

    override fun getItemCount(): Int = model.getItemsCount()

    fun handleRemove(index: Int) {
        notifyItemRemoved(index)
    }

    fun handleInsert(index: Int) {
        notifyItemInserted(index)
    }
}