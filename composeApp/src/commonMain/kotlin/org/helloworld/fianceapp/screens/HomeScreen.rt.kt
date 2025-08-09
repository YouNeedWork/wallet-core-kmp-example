package org.helloworld.fianceapp.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentCompositeKeyHash
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import com.trustwallet.core.CoinType
import com.trustwallet.core.EthereumMessageSigner
import com.trustwallet.core.HDWallet
import financetrchingapp.composeapp.generated.resources.Res
import financetrchingapp.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource

class HomeScreen :Screen {
    override val key: ScreenKey = uniqueScreenKey


    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { HomeScreenModel() }
        val state by screenModel.state.collectAsState()

        LaunchedEffect(currentCompositeKeyHash) {
            screenModel.getItem(0)
        }

        MaterialTheme {
            var showContent by remember { mutableStateOf(false) }
            var log = ""
            val wallet = HDWallet(256, "")
            log += "Created mnemonic: ${wallet.mnemonic}\n\n"
            val btcAddress = wallet.getAddressForCoin(CoinType.Bitcoin)
            log += "Bitcoin address: ${btcAddress}\n\n"
            val ethAddress = wallet.getAddressForCoin(CoinType.Ethereum)
            log += "Ethereum address: ${ethAddress}\n\n"

            var key =  wallet.getKeyForCoin(CoinType.Ethereum)

            var msg=  EthereumMessageSigner.signMessage(key,"123")
            var res = EthereumMessageSigner.verifyMessage(key.getPublicKey(CoinType.Ethereum),"123",msg)

            log+= "sign result verify          ${res}\n\n"

            Logger.log(
                Severity.Info,
                "L",
                null,
                log,
            )

            when (val result = state) {
                is HomeScreenModel.State.Loading -> Text("Loading")
                is HomeScreenModel.State.Result ->  Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .safeContentPadding()
                        .fillMaxSize(),

                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Button(onClick = { showContent = !showContent }) {
                        Text("Click me!")
                    }

                    AnimatedVisibility(showContent) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Image(painterResource(Res.drawable.compose_multiplatform), null)
                            Text(result.item)
                        }
                    }
                }
           }
        }


    }
}

