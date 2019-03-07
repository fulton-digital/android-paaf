package co.rw.paafexample.paaf.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import co.rw.paafexample.R
import co.rw.paafexample.paaf.base.SignInAction
import co.rw.paafexample.paaf.base.SignInClickEvent
import co.rw.paafexample.paaf.presenter.signInPresenter
import kotlinx.android.synthetic.main.activity_signin.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.jetbrains.anko.sdk23.coroutines.onClick
import kotlin.coroutines.CoroutineContext

/**
 * A login screen that offers login via email/password.
 */
class SignInActivity() : AppCompatActivity(), CoroutineScope {

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private val clickEventChannel = Channel<SignInClickEvent>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        job = SupervisorJob()

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
                            Toast.makeText(this@SignInActivity, "Sign In Success!", Toast.LENGTH_SHORT)
                                    .show()
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        coroutineContext.cancelChildren()
    }
}
