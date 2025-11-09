package com.br.listadecompras.data.repository

import com.br.listadecompras.data.model.ListItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ListItemRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun create(userId: String, listId: String, item: ListItem): Boolean {
        return try {
            val id = item.id ?: UUID.randomUUID().toString()
            item.id = id

            db.collection("users")
                .document(userId)
                .collection("lists")
                .document(listId)
                .collection("items")
                .document(id)
                .set(item)
                .await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun update(userId: String, listId: String, item: ListItem): Boolean {
        return try {
            requireNotNull(item.id) { "Item ID não pode ser nulo para atualização." }

            db.collection("users")
                .document(userId)
                .collection("lists")
                .document(listId)
                .collection("items")
                .document(item.id!!)
                .set(item)
                .await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun delete(userId: String, listId: String, itemId: String): Boolean {
        return try {
            db.collection("users")
                .document(userId)
                .collection("lists")
                .document(listId)
                .collection("items")
                .document(itemId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getAllByListAggregator(userId: String, listId: String): List<ListItem> {
        return try {
            val snapshot = db.collection("users")
                .document(userId)
                .collection("lists")
                .document(listId)
                .collection("items")
                .get()
                .await()
            snapshot.toObjects(ListItem::class.java)
                .sortedWith(compareBy({ it.category }, { it.name }))

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun filterByUserAndQuery(
        userId: String,
        listAggregatorId: String,
        query: String
    ): List<ListItem> {
        return try {
            val normalizedQuery = query.trim().lowercase()

            val snapshot = db.collection("users")
                .document(userId)
                .collection("lists")
                .document(listAggregatorId)
                .collection("items")
                .orderBy("name")
                .startAt(normalizedQuery)
                .endAt(normalizedQuery + "\uf8ff")
                .get()
                .await()

            snapshot.documents.mapNotNull { document ->
                document.toObject(ListItem::class.java)?.apply { id = document.id }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getById(userId: String, listId: String, itemId: String): ListItem? {
        return try {
            val snapshot = db.collection("users")
                .document(userId)
                .collection("lists")
                .document(listId)
                .collection("items")
                .document(itemId)
                .get()
                .await()

            snapshot.toObject(ListItem::class.java)?.apply {
                this.id = snapshot.id
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


}
