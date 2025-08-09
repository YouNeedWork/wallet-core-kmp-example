package org.helloworld.fianceapp.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

class HomeScreen :Screen {

    @Composable
    override fun Content() {
        Text("Hello world")
    }
}