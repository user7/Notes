package com.geekbrains.notes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels

class DetailsFragment : Fragment(R.layout.details_fragment) {
    private lateinit var nameText: TextView
    private lateinit var dateText: TextView
    private lateinit var descText: TextView
    private val model: MainViewModel by activityViewModels()
    private var currentItem: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nameText = view.findViewById(R.id.details_name)
        descText = view.findViewById(R.id.details_desc)
        view.findViewById<Button>(R.id.details_button_save).setOnClickListener { handleSave() }
        view.findViewById<Button>(R.id.details_button_delete).setOnClickListener { handleDelete() }
        model.selectedNoteId.observe(viewLifecycleOwner, { id -> handleSelectedNote(id) })
    }

    fun currentItemGood() = 0 <= currentItem && currentItem < model.items.size

    fun handleSelectedNote(id: Int) {
        currentItem = id
        if (currentItemGood()) {
            nameText.text = model.items[currentItem].name
            descText.text = model.items[currentItem].desc
        } else {
            nameText.text = ""
            descText.text = ""
        }
    }

    fun handleSave() {
        if (currentItemGood()) {
            model.items[currentItem] = Item(
                nameText.text.toString(),
                descText.text.toString()
            )
            model.modifiedNoteId.value = currentItem
        }
    }

    fun handleDelete() {
        if (currentItemGood()) {
            model.items.removeAt(currentItem)
            if (currentItem == model.items.size) {
                currentItem--
            }
            model.removedItem.value = currentItem
        }
    }
}