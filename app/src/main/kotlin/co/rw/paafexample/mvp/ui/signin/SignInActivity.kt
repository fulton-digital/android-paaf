package co.rw.paafexample.mvp.ui.signin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import co.rw.paafexample.PaafApplication
import co.rw.paafexample.R
import kotlinx.android.synthetic.main.activity_signin.email_sign_in_button
import org.jetbrains.anko.sdk23.listeners.onClick
import javax.inject.Inject

class SignInActivity : AppCompatActivity(), SignInView {

    @Inject
    lateinit var signInPresenter: SignInPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        (application as PaafApplication).mvpAppComponent.inject(this)

        signInPresenter.onAttach(this)

        email_sign_in_button.onClick {

        }
    }

    override fun onResume() {
        super.onResume()
        signInPresenter.onAttach(this)
    }

    override fun onPause() {
        super.onPause()
        signInPresenter.onDetach()
    }

    override fun onSignInSuccess() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
