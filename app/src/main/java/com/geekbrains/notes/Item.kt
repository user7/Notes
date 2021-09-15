package com.geekbrains.notes

import java.util.*
import kotlin.collections.ArrayList

data class Item(
    val name: String = "",
    val desc: String = "",
    val date: Date = Date(),
    val uuid: UUID = UUID.randomUUID(),
)

typealias Items = ArrayList<Item>