package com.geekbrains.notes

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListFragment : Fragment(R.layout.list_fragment) {
    private val model: MainViewModel by activityViewModels()
    private lateinit var adapter: ItemsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ItemsAdapter(model)

        model.modifiedNoteId.observe(viewLifecycleOwner, { id -> adapter.notifyItemChanged(id) })
        model.removedItem.observe(viewLifecycleOwner, { id -> handleRemove(id) })

        view.findViewById<Button>(R.id.add_note).setOnClickListener {
            model.items.add(Item())
            model.selectedNoteId.value = model.items.size - 1
        }

        val rv = view.findViewById<RecyclerView>(R.id.notes_list)
        rv.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        rv.adapter = adapter
    }

    private fun handleRemove(id: Int) {
        adapter.notifyItemRemoved(id)
        adapter.notifyItemRangeChanged(id, model.items.size)
    }
}
