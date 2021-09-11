package com.geekbrains.notes

import java.util.Date

data class Item(var name: String = "", var desc: String = "", var date: Date = Date())

typealias Items = ArrayList<Item>