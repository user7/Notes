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
    private lateinit var dateText: TextView
    private lateinit var descText: TextView
    private val model: MainViewModel by activityViewModels()
    private var currentItem: Int = 0
    private val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm:ss z")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nameText = view.findViewById(R.id.details_name)
        dateText = view.findViewById(R.id.details_date)
        descText = view.findViewById(R.id.details_desc)
        view.findViewById<Button>(R.id.details_button_save).setOnClickListener { handleSave() }
        view.findViewById<Button>(R.id.details_button_delete).setOnClickListener { handleDelete() }
        view.findViewById<ImageButton>(R.id.details_date_picker).setOnClickListener { pickDate() }
        model.selectedNoteId.observe(viewLifecycleOwner, { id -> handleSelectedNote(id) })
    }

    fun currentItemGood() = 0 <= currentItem && currentItem < model.items.size

    fun handleSelectedNote(id: Int) {
        currentItem = id
        if (currentItemGood()) {
            nameText.text = model.items[currentItem].name
            descText.text = model.items[currentItem].desc
            writeDate(model.items[currentItem].date)
        } else {
            nameText.text = ""
            dateText.text = ""
            descText.text = ""
        }
    }

    fun writeDate(date: Date) {
        dateText.text = dateFormat.format(date)
    }

    fun readDate(): Date {
        val str = dateText.text.toString()
        return if (str.equals("")) Date() else dateFormat.parse(str)!!
    }

    fun handleSave() {
        if (currentItemGood()) {
            model.items[currentItem] = Item(
                nameText.text.toString(),
                descText.text.toString(),
                readDate()
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

    fun pickDate() {
        val d = readDate()
        val c = Calendar.getInstance()
        c.time = d
        val dy = c.get(Calendar.YEAR)
        val dm = c.get(Calendar.MONTH)
        val dd = c.get(Calendar.DAY_OF_MONTH)
        val picker = DatePickerDialog(
            requireContext(),
            { view, year, month, dayOfMonth -> modifyDate(year, month, dayOfMonth) },
            dy, dm, dd
        )
        picker.show()
    }

    fun modifyDate(year: Int, month: Int, dayOfMonth: Int) {
        val d = readDate()
        val c = Calendar.getInstance()
        c.time = d
        c.set(Calendar.YEAR, year)
        c.set(Calendar.MONTH, month)
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        writeDate(c.time)
    }
}