package com.geekbrains.notes

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import java.text.SimpleDateFormat
import java.util.*


class DetailsFragment : Fragment(R.layout.details_fragment) {
    private lateinit var nameText: TextView
    private lateinit var descText: TextView
    private lateinit var dateText: TextView
    private var dateValue = Date()

    private lateinit var saveButton: Button
    private lateinit var deleteButton: Button
    private lateinit var datePickerButton: ImageButton
    private var editingEnabled: Boolean = false

    private val model: MainViewModel by activityViewModels()
    private var currentItem = -1
    private val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm:ss z")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nameText = view.findViewById(R.id.details_name)
        dateText = view.findViewById(R.id.details_date)
        descText = view.findViewById(R.id.details_desc)
        saveButton = view.findViewById(R.id.details_button_save)
        saveButton.setOnClickListener { handleSave() }
        deleteButton = view.findViewById(R.id.details_button_delete)
        deleteButton.setOnClickListener { handleDelete() }
        datePickerButton = view.findViewById(R.id.details_date_picker)
        datePickerButton.setOnClickListener { pickDate() }
        handleSelectedItem(-1)
        model.selectedItemIndex.observe(viewLifecycleOwner) { id -> handleSelectedItem(id) }
    }

    private fun setEditingEnabled(b: Boolean) {
        nameText.isEnabled = b
        descText.isEnabled = b
        dateText.isEnabled = b
        saveButton.isEnabled = b
        datePickerButton.isEnabled = b
        deleteButton.isEnabled = b
        editingEnabled = b
    }

    private fun handleSelectedItem(index: Int) {
        val item: Item? = model.getItem(index)
        currentItem = index
        if (item != null) {
            nameText.text = item.name
            descText.text = item.desc
            setDate(item.date)
            setEditingEnabled(true)
        } else {
            nameText.text = ""
            descText.text = ""
            dateText.text = ""
            setEditingEnabled(false)
        }
    }

    private fun setDate(date: Date) {
        dateText.text = dateFormat.format(date)
        dateValue = date
    }

    private fun handleSave() {
        model.saveEditedItem(
            currentItem, Item(
                nameText.text.toString(),
                descText.text.toString(),
                dateValue
            )
        )
    }

    private fun handleDelete() {
        model.removeItem(currentItem)
    }

    private fun pickDate() {
        val c = Calendar.getInstance()
        c.time = dateValue
        val picker = DatePickerDialog(
            requireContext(),
            { view, year, month, dayOfMonth -> modifyDate(year, month, dayOfMonth) },
            c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
        )
        picker.show()
    }

    private fun modifyDate(year: Int, month: Int, dayOfMonth: Int) {
        val c = Calendar.getInstance()
        c.time = dateValue
        c.set(Calendar.YEAR, year)
        c.set(Calendar.MONTH, month)
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        dateValue = c.time
    }
}