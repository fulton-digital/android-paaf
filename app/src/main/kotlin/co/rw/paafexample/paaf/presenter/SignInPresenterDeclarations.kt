package co.rw.paafexample.paaf.presenter

import co.rw.paafexample.paaf.base.SignInAction
import co.rw.paafexample.paaf.base.SignInClickEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class SignInChannels(val signInActionChannel: ReceiveChannel<SignInAction>)

fun CoroutineScope.signInPresenter(clickEventChannel: ReceiveChannel<SignInClickEvent>): SignInChannels {
    val signInActionChannel = Channel<SignInAction>()

    // We don't want our presenter running on the main thread
    launch(Dispatchers.IO) {
        for (signInClickEvent in clickEventChannel) {
            when (signInClickEvent) {
                SignInClickEvent.SignInButton -> {
                    // Do some real sign in, for the example we will just delay
                    delay(timeMillis = 1000)
                    signInActionChannel.send(SignInAction.SignInSuccessful)
                }
            }
        }
    }

    return SignInChannels(signInActionChannel)
}
