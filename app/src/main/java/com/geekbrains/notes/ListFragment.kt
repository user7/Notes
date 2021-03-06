package com.geekbrains.notes

import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView

class ListFragment : Fragment(R.layout.fragment_list) {
    private val model: MainViewModel by activityViewModels()
    private lateinit var adapter: ItemsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ItemsAdapter(model, this)

        model.modifiedItemIndex.observe(viewLifecycleOwner) { i -> adapter.notifyItemChanged(i) }
        model.removedItemIndex.observe(viewLifecycleOwner) { i -> adapter.handleRemove(i) }
        model.insertedItemIndex.observe(viewLifecycleOwner) { i -> adapter.handleInsert(i) }
        model.insertedItemsStart.observe(viewLifecycleOwner) { i -> adapter.handleAddAll(i) }
        view.findViewById<Button>(R.id.add_note).setOnClickListener { model.editNewItem() }

        view.findViewById<RecyclerView>(R.id.notes_list).adapter = adapter
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        view: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, view, menuInfo)
        val menuInflater: MenuInflater = requireActivity().menuInflater
        menuInflater.inflate(R.menu.list_context, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.context_action_delete -> {
                adapter.model.removeItem(adapter.getContextMenuItemIndex())
                return true
            }
            R.id.context_action_edit -> {
                adapter.model.editOldItem(adapter.getContextMenuItemIndex())
                return true
            }
        }
        return super.onContextItemSelected(item)
    }
}