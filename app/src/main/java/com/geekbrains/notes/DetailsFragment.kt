package com.geekbrains.notes

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import java.text.DateFormat.getDateTimeInstance
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
    private val dateFormat = getDateTimeInstance()

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
        handleInterfaceStateChanged()
        model.interfaceState.observe(viewLifecycleOwner) { _ -> handleInterfaceStateChanged() }
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

    // произошел какой-то апдейт интерфейса, возможно надо перерисовать
    private fun handleInterfaceStateChanged() {
        val pos = model.getEditingPos()
        when {
            // нет операции, чистим и выключаем
            !pos.isValid() -> clearFieldsSetEnabled(false)

            // вставка, чистим и включаем, ставим текущую дату
            pos.insertNew -> {
                clearFieldsSetEnabled(true)
                setDate(Date())
            }

            // иначе это редактирование, индекс обязан быть валидным, поэтому !!
            else -> {
                val item: Item = model.getItem(pos.index)!!
                nameText.text = item.name
                descText.text = item.desc
                setDate(item.date)
                setEditingEnabled(true)
            }
        }
    }

    private fun clearFieldsSetEnabled(setEnabled: Boolean) {
        nameText.text = ""
        descText.text = ""
        dateText.text = ""
        setEditingEnabled(setEnabled)
    }

    private fun setDate(date: Date) {
        dateText.text = dateFormat.format(date)
        dateValue = date
    }

    private fun handleSave() {
        val pos = model.getEditingPos()
        val item = Item(nameText.text.toString(), descText.text.toString(), dateValue)
        if (pos.insertNew) {
            model.insertEditedItem(pos.index, item)
        } else {
            model.saveEditedItem(pos.index, item)
        }
        model.setInterfaceState(MainViewModel.InterfaceState.SHOW_LIST)
    }

    private fun handleDelete() {
        model.removeOrDiscardEditedItem()
    }

    private fun pickDate() {
        val c = Calendar.getInstance()
        c.time = dateValue
        val picker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth -> modifyDate(year, month, dayOfMonth) },
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
        setDate(c.time)
    }
}