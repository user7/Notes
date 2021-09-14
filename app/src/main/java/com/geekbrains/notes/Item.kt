package com.geekbrains.notes

import java.util.Date

data class Item(val name: String = "", val desc: String = "", val date: Date = Date())

typealias Items = ArrayList<Item>