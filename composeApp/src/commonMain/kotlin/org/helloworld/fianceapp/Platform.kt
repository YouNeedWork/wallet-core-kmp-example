package org.helloworld.fianceapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform