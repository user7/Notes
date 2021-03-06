package com.geekbrains.notes

import androidx.lifecycle.*

class MainViewModel : ViewModel() {
    private val items: Items = Items()
    private val itemsRepository: ItemsRepository = ItemsRepositoryFirestore()

    private var selectedIndex: Int = -1
    fun getSelectedIndex() = selectedIndex

    private val mutableModifiedItemIndex: MutableLiveData<Int> = MutableLiveData()
    val modifiedItemIndex: LiveData<Int> = mutableModifiedItemIndex

    private val mutableRemovedItemIndex: MutableLiveData<Int> = MutableLiveData()
    val removedItemIndex: LiveData<Int> = mutableRemovedItemIndex

    private val mutableInsertedItemIndex: MutableLiveData<Int> = MutableLiveData()
    val insertedItemIndex: LiveData<Int> = mutableInsertedItemIndex

    private val mutableInsertedItemsStart: MutableLiveData<Int> = MutableLiveData()
    val insertedItemsStart: LiveData<Int> = mutableInsertedItemsStart

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

    fun load() {
        itemsRepository.getItems() {
            items.addAll(it)
            mutableInsertedItemsStart.postValue(this.items.size)
        }
    }

    private var userId: String? = null
    fun setUserId(userId: String) {
        this.userId = userId
        itemsRepository.setUserId(userId)
    }
    fun isSignedIn() = userId != null

    // index = -1 - невалидная позиция, остальные поля игнорируются
    // index >= 0 - индекс где редактировать или вставить новый, от 0 до size - 1
    // insertNew = false - редактировать элемент index
    // insertNew = true - вставить новый элемент после элемента index
    // editingFields - набор начальных значений полей, также используется при реконфигурации
    data class EditingState(
        val index: Int = -1,
        val insertNew: Boolean = false,
        var editingFields: Item = Item(),
    ) {
        fun isValid() = index != -1
    }

    private var editingState: EditingState = EditingState()
    fun getEditingState() = editingState

    fun editNewItem() {
        val index: Int
        if (0 <= selectedIndex && selectedIndex < items.size) {
            index = selectedIndex + 1
        } else {
            index = items.size
        }
        setupEditNew(index)
        setInterfaceState(InterfaceState.SHOW_DETAILS)
    }

    fun editOldItem(index: Int) {
        setupEditOld(index)
        setInterfaceState(InterfaceState.SHOW_DETAILS)
    }

    fun getItem(index: Int): Item? = items.getOrNull(index)
    fun getItemsCount() = items.size

    fun saveEditedItem(index: Int, item: Item) {
        val old = items[index]
        if (old != item) { // data class Item
            itemsRepository.setItem(item)
            items[index] = item
            mutableModifiedItemIndex.postValue(index)
        }
        setupEditOld(index)
        setInterfaceState(InterfaceState.SHOW_LIST)
    }

    fun insertEditedItem(index: Int, item: Item) {
        items.add(index, item)
        itemsRepository.setItem(item)
        setupEditOld(index)
        mutableInsertedItemIndex.postValue(index)
        setInterfaceState(InterfaceState.SHOW_LIST)
    }

    fun cancelEditing() {
        setupEditingDisabled()
        setInterfaceState(InterfaceState.SHOW_LIST)
    }

    fun removeOrDiscardEditedItem() {
        if (editingState.insertNew) {
            setupEditOld(editingState.index)
        } else {
            removeItem(editingState.index)
            val i =
                if (editingState.index == items.size) editingState.index - 1 else editingState.index
            setupEditOld(i)
        }
        setInterfaceState(InterfaceState.SHOW_LIST)
    }

    fun removeItem(index: Int) {
        itemsRepository.removeItem(items[index].uuid)
        items.removeAt(index)
        mutableRemovedItemIndex.postValue(index)
    }

    private fun setupEditNew(index: Int) {
        if (index < 0 || index > items.size) { // при вставке индекс равный size валиден
            setupEditingDisabled()
        } else {
            editingState = EditingState(index, true, Item())
        }
    }

    private fun setupEditOld(index: Int) {
        if (index < 0 || index >= items.size) { // при редактировании максимальный индекс size - 1
            setupEditingDisabled()
        } else {
            editingState = EditingState(index, false, items[index])
        }
    }

    private fun setupEditingDisabled() {
        editingState = EditingState()
    }
}