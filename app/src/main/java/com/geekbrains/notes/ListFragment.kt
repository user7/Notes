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

        model.modifiedItemIndex.observe(viewLifecycleOwner) { id -> adapter.notifyItemChanged(id) }
        model.removedItemIndex.observe(viewLifecycleOwner) { id -> adapter.handleRemove(id) }
        model.insertedItemIndex.observe(viewLifecycleOwner) { id -> adapter.handleInsert(id) }
        view.findViewById<Button>(R.id.add_note).setOnClickListener {
            model.editNewItem()
        }

        view.findViewById<RecyclerView>(R.id.notes_list).adapter = adapter
    }
}
