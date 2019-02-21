package co.rw.paafexample.paaf

import co.rw.paafexample.paaf.base.SignInAction
import co.rw.paafexample.paaf.base.SignInClickEvent
import co.rw.paafexample.paaf.presenter.signInPresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PaafExampleTest {
    private val clickEventChannel = Channel<SignInClickEvent>()
    private lateinit var actionChannel: ReceiveChannel<SignInAction>

    @Before
    fun setUpActionChannel() {
        GlobalScope.launch(Dispatchers.Default) {
            actionChannel = signInPresenter(clickEventChannel = clickEventChannel).signInActionChannel
        }
    }

    @Test
    fun testShouldReceiveSignInActionOnSignInButtonClickEvent() {
        runBlocking {
            launch(Dispatchers.Default) {
                clickEventChannel.send(SignInClickEvent.SignInButton)

                val action = actionChannel.receive()

                assertEquals(SignInAction.SignInSuccessful, action)
            }
        }
    }
}
