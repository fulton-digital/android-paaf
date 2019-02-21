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

/**
 * A login screen that offers login via email/password.
 */
@ExperimentalCoroutinesApi
class SignInActivity() : AppCompatActivity(), CoroutineScope by MainScope() {
    private val clickEventChannel = Channel<SignInClickEvent>(capacity = Channel.CONFLATED)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        email_sign_in_button.onClick {
            clickEventChannel.send(SignInClickEvent.SignInButton)
        }
    }

    override fun onResume() {
        super.onResume()

        launch {
            signInPresenter(clickEventChannel).signInActionChannel.let {
                for (signInAction in it) {
                    when (signInAction) {
                        SignInAction.SignInSuccessful -> {
                            toast("Sign In Success!").show()
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        cancel()  // CoroutineScope.cancel
    }
}
