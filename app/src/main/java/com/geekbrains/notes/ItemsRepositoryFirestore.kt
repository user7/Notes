package com.geekbrains.notes

import androidx.core.util.Consumer
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

const val USERS = "users"
const val ITEMS = "items"
const val DATE = "date"
const val NAME = "name"
const val DESC = "desc"
const val LASTACCESS = "lastaccess"

class ItemsRepositoryFirestore : ItemsRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var userId: String = ""

    override fun setUserId(userId: String) {
        this.userId = userId
        db.collection(USERS).document(this.userId).update(LASTACCESS, Date())
            .report("update $USERS.$userId.$LASTACCESS")
    }

    override fun getItems(callback: Consumer<Items>) {
        db.collection(USERS).document(userId).collection(ITEMS).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val items: Items = Items()
                    for (doc in it.result) {
                        items.add(
                            Item(
                                doc.getString(NAME)!!,
                                doc.getString(DESC)!!,
                                doc.getDate(DATE)!!,
                                UUID.fromString(doc.id),
                            )
                        )
                    }
                    callback.accept(items)
                } else {
                    d("failed get $USERS.$userId.$ITEMS: ${it.exception.toString()}")
                }
            }
    }

    override fun removeItem(uuid: UUID) {
        db.collection(USERS).document(userId).collection(ITEMS)
            .document(uuid.toString())
            .delete()
            .report("delete $USERS.$userId.$ITEMS.${uuid.toString()}")
    }

    override fun addItem(item: Item) {
        val data: Map<String, Any> = hashMapOf(
            NAME to item.name,
            DESC to item.desc,
            DATE to item.date,
        );
        db.collection(USERS).document(userId).collection(ITEMS)
            .document(item.uuid.toString()).set(data)
            .report("add $USERS.$userId.$ITEMS.${item.uuid.toString()}")
    }
}

private fun <TResult> Task<TResult>.report(description: String): Task<TResult> {
    return this.addOnCompleteListener {
        if (it.isSuccessful) {
            d("ok $description")
        } else {
            d("failed $description: ${it.exception.toString()}")
        }
    }
}