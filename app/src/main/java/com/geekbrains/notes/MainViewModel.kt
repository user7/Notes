package com.geekbrains.notes

import androidx.lifecycle.*

class MainViewModel : ViewModel() {
    private val items: Items = Items()

    private val mutableModifiedItemIndex: MutableLiveData<Int> = MutableLiveData()
    val modifiedItemIndex: LiveData<Int> = mutableModifiedItemIndex

    private val mutableSelectedItemIndex: MutableLiveData<Int> = MutableLiveData()
    val selectedItemIndex: LiveData<Int> = mutableSelectedItemIndex

    private val mutableRemovedItemIndex: MutableLiveData<Int> = MutableLiveData()
    val removedItemIndex: LiveData<Int> = mutableRemovedItemIndex

    enum class InterfaceState { SHOW_LIST, SHOW_DETAILS }

    private val mutableInterfaceState: MutableLiveData<InterfaceState> =
        MutableLiveData(InterfaceState.SHOW_LIST)
    val interfaceState: LiveData<InterfaceState> = mutableInterfaceState
    fun setInterfaceState(state: InterfaceState) = mutableInterfaceState.postValue(state)

    fun addNewItem() {
        items.add(Item())
        mutableSelectedItemIndex.postValue(items.size - 1)
        mutableModifiedItemIndex.postValue(items.size - 1)
    }

    fun getItem(index: Int): Item? = items.getOrNull(index)
    fun getItemsCount() = items.size

    fun saveEditedItem(index: Int, item: Item) {
        val old = items[index]
        if (old != item) { // data class Item
            items[index] = item
            mutableModifiedItemIndex.postValue(index)
        }
        setInterfaceState(InterfaceState.SHOW_LIST)
    }

    fun removeItem(index: Int) {
        items.removeAt(index)
        mutableRemovedItemIndex.postValue(index)
        selectItem(if (index == items.size) index - 1 else index)
        setInterfaceState(InterfaceState.SHOW_LIST)
    }

    fun selectItem(index: Int) {
        mutableSelectedItemIndex.postValue(if (index < 0 || index > items.size) -1 else index)
    }
}