package com.urdzik.core.api.impl

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// Setup firebase datastore
class FirestoreApi {
    val tag = "FirestoreApi"

    val firestore = Firebase.firestore
    val collection = firestore.collection("books")

    init {
        setup()
    }

    fun setup() {
        collection.get().addOnSuccessListener { result ->
            for (document in result) {
                Log.d(tag, "${document.id} => ${document.data}")
            }
        }.addOnFailureListener { exception ->
            Log.d(tag,"Error getting documents: $exception")
        }
    }

}


