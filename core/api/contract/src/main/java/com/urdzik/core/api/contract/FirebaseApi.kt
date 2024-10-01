package com.urdzik.core.api.contract

import com.urdzik.core.api.contract.model.BookResponse

interface FirebaseApi {
    suspend fun getBookById(id: String = "TGl0dGxlIFByaW5jZQ=="): BookResponse
}