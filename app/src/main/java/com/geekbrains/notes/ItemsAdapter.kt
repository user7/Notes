package com.geekbrains.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemsAdapter(val model: MainViewModel, val listFragment: ListFragment) :
    RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {

    private var contextMenuItemIndex: Int = -1
    fun getContextMenuItemIndex() = contextMenuItemIndex

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textView: TextView = view.findViewById(R.id.notes_list_item_text)
        fun getTextView() = textView

        init {
            listFragment.registerForContextMenu(view)
            textView.setOnClickListener { model.editOldItem(adapterPosition) }
            textView.setOnLongClickListener {
                textView.showContextMenu()
                contextMenuItemIndex = adapterPosition
                true
            }
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

    fun handleAddAll(index: Int) {
        notifyItemRangeInserted(index, model.getItemsCount())
    }
}