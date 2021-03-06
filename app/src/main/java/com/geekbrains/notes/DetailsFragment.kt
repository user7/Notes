package com.geekbrains.notes

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import java.text.DateFormat.getDateTimeInstance
import java.util.*


class DetailsFragment : Fragment(R.layout.fragment_details) {
    private lateinit var nameText: TextView
    private lateinit var descText: TextView
    private lateinit var dateText: TextView
    private var dateValue = Date()
    private var uuid = UUID.randomUUID()

    private lateinit var saveButton: Button
    private lateinit var deleteButton: Button

    private val model: MainViewModel by activityViewModels()
    private val dateFormat = getDateTimeInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nameText = view.findViewById(R.id.details_name)
        dateText = view.findViewById(R.id.details_date)
        dateText.setOnClickListener { pickDate() }
        descText = view.findViewById(R.id.details_desc)
        saveButton = view.findViewById(R.id.details_button_save)
        saveButton.setOnClickListener { handleSave() }
        deleteButton = view.findViewById(R.id.details_button_delete)
        deleteButton.setOnClickListener { handleDelete() }
        model.interfaceState.observe(viewLifecycleOwner) { handleInterfaceStateChanged() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        model.getEditingState().editingFields = getItemFromInputs()
    }

    private fun setEditingEnabled(b: Boolean) {
        nameText.isEnabled = b
        descText.isEnabled = b
        dateText.isEnabled = b
        saveButton.isEnabled = b
        deleteButton.isEnabled = b
    }

    // произошел какой-то апдейт интерфейса, надо восстановить значения полей
    private fun handleInterfaceStateChanged() {
        val es = model.getEditingState()
        setInputsFromItem(es.editingFields)
        setEditingEnabled(es.isValid())
    }

    private fun setDate(date: Date) {
        dateText.text = dateFormat.format(date)
        dateValue = date
    }

    private fun getItemFromInputs() =
        Item(nameText.text.toString(), descText.text.toString(), dateValue, uuid)

    private fun setInputsFromItem(item: Item) {
        nameText.text = item.name
        descText.text = item.desc
        setDate(item.date)
        uuid = item.uuid
    }

    private fun handleSave() {
        val pos = model.getEditingState()
        val item = getItemFromInputs()
        if (pos.insertNew) {
            model.insertEditedItem(pos.index, item)
        } else {
            model.saveEditedItem(pos.index, item)
        }
        model.setInterfaceState(MainViewModel.InterfaceState.SHOW_LIST)
    }

    private fun handleDelete() {
        if (model.getEditingState().insertNew) {
            // новый элемент просто выкидываем
            model.removeOrDiscardEditedItem()
        } else {
            // если элемент уже существовал, то сначала запрашиваем подтверждение
            setFragmentResultListener(DeleteWarningDialogFragment.KEY) { key, bundle ->
                if (bundle.getBoolean(DeleteWarningDialogFragment.ISYES)) {
                    model.removeOrDiscardEditedItem()
                }
            }
            DeleteWarningDialogFragment().show(parentFragmentManager, null)
        }
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