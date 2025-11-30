package com.br.listadecompras.data.repository

import com.br.listadecompras.data.model.ListItemAggregator
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ListItemAggregatorRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun create(userId: String, list: ListItemAggregator): Boolean {
        return try {
            val id = list.id ?: UUID.randomUUID().toString()
            list.id = id

            db.collection("users")
                .document(userId)
                .collection("lists")
                .document(id)
                .set(list)
                .await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun update(userId: String, list: ListItemAggregator): Boolean {
        return try {
            db.collection("users")
                .document(userId)
                .collection("lists")
                .document(list.id!!)
                .set(list)
                .await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun delete(userId: String, listId: String): Boolean {
        return try {
            val docRef = db.collection("users")
                .document(userId)
                .collection("lists")
                .document(listId)

            val itemsSnapshot = docRef.collection("items").get().await()
            for (doc in itemsSnapshot.documents) {
                doc.reference.delete().await()
            }

            docRef.delete().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    suspend fun getById(userId: String, id: String): ListItemAggregator? {
        return try {
            val snapshot = db.collection("users")
                .document(userId)
                .collection("lists")
                .document(id)
                .get()
                .await()
            snapshot.toObject(ListItemAggregator::class.java)?.apply { this.id = snapshot.id }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getAllByUser(userId: String): List<ListItemAggregator> {
        return try {
            val snapshot = db.collection("users")
                .document(userId)
                .collection("lists")
                .get()
                .await()
            snapshot.toObjects(ListItemAggregator::class.java)
                .sortedBy { it.name }

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
