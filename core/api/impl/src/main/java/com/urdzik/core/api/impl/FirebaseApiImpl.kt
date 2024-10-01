package com.urdzik.core.api.impl

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.urdzik.core.api.contract.FirebaseApi
import com.urdzik.core.api.contract.model.BookResponse
import kotlin.coroutines.suspendCoroutine

class FirebaseApiImpl : FirebaseApi {

    private val database = FirebaseDatabase.getInstance().reference

    override suspend fun getBookById(id: String): BookResponse {
        return suspendCoroutine { continuation ->
            database.child("audiobooks").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.children.mapNotNull { it.getValue(BookResponse::class.java) }
                        .firstOrNull { it.id == id }
                        ?.let {
                            continuation.resumeWith(Result.success(it))
                        } ?: continuation.resumeWith(Result.failure(Exception("No data found")))
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWith(Result.failure(error.toException()))
                }
            })

        }
    }
}