package com.geekbrains.notes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    val items: Items = Items()
    val modifiedNoteId: MutableLiveData<Int> = MutableLiveData()
    val selectedNoteId: MutableLiveData<Int> = MutableLiveData()
    val removedItem: MutableLiveData<Int> = MutableLiveData()
}