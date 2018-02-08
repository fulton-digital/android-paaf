package co.rw.paafexample.paaf

import co.rw.paafexample.paaf.base.SignInAction
import co.rw.paafexample.paaf.base.SignInClickEvent
import co.rw.paafexample.paaf.presenter.signInPresenter
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class PaafExampleTest {
    private val clickEventChannel = Channel<SignInClickEvent>()
    private val actionChannel = signInPresenter(clickEventChannel = clickEventChannel).signInActionChannel

    @Test
    fun testShouldReceiveSignInActionOnSignInButtonClickEvent() {
        runBlocking {
            clickEventChannel.send(SignInClickEvent.SignInButton)

            val action = actionChannel.receive()

            assertEquals(SignInAction.SignInSuccessful, action)
        }
    }
}
