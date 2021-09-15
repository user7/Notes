package com.geekbrains.notes

import androidx.core.util.Consumer
import java.util.*

interface ItemsRepository {
    fun getItems(callback: Consumer<Items>)
    fun removeItem(uuid: UUID)
    fun addItem(item: Item)
    fun setUserId(userId: String)
}