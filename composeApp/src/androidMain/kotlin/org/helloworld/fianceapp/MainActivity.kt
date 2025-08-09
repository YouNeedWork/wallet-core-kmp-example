package org.helloworld.fianceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import org.helloworld.fianceapp.screens.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        System.loadLibrary("TrustWalletCore")

        super.onCreate(savedInstanceState)

        setContent {
            Navigator(HomeScreen())
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    Navigator(HomeScreen())
}