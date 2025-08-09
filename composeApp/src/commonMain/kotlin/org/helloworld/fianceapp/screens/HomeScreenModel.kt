package org.helloworld.fianceapp.screens

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import kotlinx.coroutines.launch
import org.helloworld.fianceapp.Greeting

class HomeScreenModel(): StateScreenModel<HomeScreenModel.State>(State.Loading){
    sealed class State {
        object Loading : State()
        data class Result(val item: String) : State()
    }

    fun getItem(index: Int) {
        screenModelScope.launch {
            try {
                var msg = Greeting().greeting()
                mutableState.value = State.Result(msg)
            } catch (e: Exception) {
                mutableState.value = State.Result(e.message.toString())
            }
        }
    }

    override fun onDispose() {
        Logger.log(
            Severity.Info,
            "L",
            null,
            "OnDispost",
        )
    }
}