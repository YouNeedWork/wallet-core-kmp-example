package org.helloworld.fianceapp

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class Greeting {
    private val client = HttpClient()

    suspend fun greeting(): String {
        val response = client.get("https://www.baidu.com/")
        return response.bodyAsText()
    }
}