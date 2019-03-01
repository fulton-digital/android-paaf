package co.rw.paafexample.paaf.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import co.rw.paafexample.R
import co.rw.paafexample.paaf.base.SignInAction
import co.rw.paafexample.paaf.base.SignInClickEvent
import co.rw.paafexample.paaf.presenter.signInPresenter
import kotlinx.android.synthetic.main.activity_signin.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.jetbrains.anko.sdk23.coroutines.onClick
import org.jetbrains.anko.toast
import kotlin.coroutines.CoroutineContext

/**
 * A login screen that offers login via email/password.
 */
class SignInActivity() : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.Main

    private val clickEventChannel = Channel<SignInClickEvent>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        email_sign_in_button.onClick(coroutineContext) {
            clickEventChannel.send(SignInClickEvent.SignInButton)
        }
    }

    override fun onResume() {
        super.onResume()

        launch {
            signInPresenter(clickEventChannel).signInActionChannel.let {
                var counter =1
                for (signInAction in it) {
                    when (signInAction) {
                        SignInAction.SignInSuccessful -> {
                            toast("Sign In Success! counter $counter").show()
                        }
                    }

                    counter++
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        coroutineContext.cancel()  // CoroutineScope.cancel
    }
}
