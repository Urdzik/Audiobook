package com.urdzik.core.api.contract

import com.urdzik.core.api.contract.model.BookResponce

interface FirebaseApi {
    suspend fun getBook(id: String = "TGl0dGxlIFByaW5jZQ=="): BookResponce
}