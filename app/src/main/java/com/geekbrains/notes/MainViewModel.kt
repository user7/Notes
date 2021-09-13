package com.geekbrains.notes

import androidx.lifecycle.*

class MainViewModel : ViewModel() {
    private val items: Items = Items()

    private var selectedIndex: Int = -1
    fun getSelectedIndex() = selectedIndex

    private val mutableModifiedItemIndex: MutableLiveData<Int> = MutableLiveData()
    val modifiedItemIndex: LiveData<Int> = mutableModifiedItemIndex

    private val mutableRemovedItemIndex: MutableLiveData<Int> = MutableLiveData()
    val removedItemIndex: LiveData<Int> = mutableRemovedItemIndex

    private val mutableInsertedItemIndex: MutableLiveData<Int> = MutableLiveData()
    val insertedItemIndex: LiveData<Int> = mutableInsertedItemIndex

    // Режимы списка и деталей. Для портретной ориентации всегда показывается только один из двух
    // фрагментов. В альбомной ориентации лайот не зависит от режима, но при переключении
    // на SHOW_DETAILS происходит загрузка редактируемого элемента в DetailsFragment, обзервер
    // переменной interfaceState используется для отправки сигнала "обновись" из ListFragment
    // в DetailsFragment.
    enum class InterfaceState { SHOW_LIST, SHOW_DETAILS }

    private val mutableInterfaceState: MutableLiveData<InterfaceState> =
        MutableLiveData(InterfaceState.SHOW_LIST)

    fun setInterfaceState(state: InterfaceState) = mutableInterfaceState.postValue(state)
    val interfaceState: LiveData<InterfaceState> = mutableInterfaceState

    // -1,* = невалидная позиция; i,true = вставить в позицию i; i,false = редактировать элемент i
    data class EditingPos(val index: Int = -1, val insertNew: Boolean = false) {
        fun isValid() = index != -1
    }

    private var editingPos: EditingPos = EditingPos()
    fun getEditingPos() = editingPos

    fun editNewItem() {
        val index: Int
        if (0 <= selectedIndex && selectedIndex < items.size)
            index = selectedIndex + 1
        else
            index = items.size
        setEditingPos(index, true)
        setInterfaceState(InterfaceState.SHOW_DETAILS)
    }

    fun editOldItem(index: Int) {
        setEditingPos(index, false)
        setInterfaceState(InterfaceState.SHOW_DETAILS)
    }

    fun getItem(index: Int): Item? = items.getOrNull(index)
    fun getItemsCount() = items.size

    fun saveEditedItem(index: Int, item: Item) {
        val old = items[index]
        if (old != item) { // data class Item
            items[index] = item
            mutableModifiedItemIndex.postValue(index)
        }
        setEditingPos(index, false)
        setInterfaceState(InterfaceState.SHOW_LIST)
    }

    fun insertEditedItem(index: Int, item: Item) {
        items.add(index, item)
        setEditingPos(index, false)
        mutableInsertedItemIndex.postValue(index)
        setInterfaceState(InterfaceState.SHOW_LIST)
    }

    fun cancelEditing() {
        setEditingPos(-1)
        setInterfaceState(InterfaceState.SHOW_LIST)
    }

    fun removeOrDiscardEditedItem() {
        if (editingPos.insertNew) {
            setEditingPos(editingPos.index, false)
        } else {
            items.removeAt(editingPos.index)
            mutableRemovedItemIndex.postValue(editingPos.index)
            val i = if (editingPos.index == items.size) editingPos.index - 1 else editingPos.index
            setEditingPos(i)
        }
        setInterfaceState(InterfaceState.SHOW_LIST)
    }

    private fun setEditingPos(index: Int, insertNew: Boolean = false) {
        var good = when {
            index < 0 -> false
            index == items.size && insertNew -> true
            index >= items.size -> false
            else -> true
        }
        if (good) {
            editingPos = EditingPos(index, insertNew)
            selectedIndex = index
        } else {
            editingPos = EditingPos()
            selectedIndex = -1
        }
    }
}